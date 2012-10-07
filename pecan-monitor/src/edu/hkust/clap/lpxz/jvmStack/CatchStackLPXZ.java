package edu.hkust.clap.lpxz.jvmStack;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.Field;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Type;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.ExceptionRequest;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.jdi.request.StepRequest;
import com.sun.jdi.request.ThreadDeathRequest;
import com.sun.jdi.request.ThreadStartRequest;

public class CatchStackLPXZ {
	

	
	//===================inter-jvm communication, use the file to access the stored stack in the host jvm
	// the code can be optimized by openning the file once and close once.
	// but make sure the file is flushed and cleaned properly.
	//do not use id, use name!
	static HashMap<String, List<String>> thread2stack = new HashMap<String, List<String>>();
	// with it, we do not need to synchronize threads on accessing the thread-specific list.
	// I do not want to create too many lists
	
	//example:
//	CatchStackLPXZ.getFullStackTrace_lpxz()(CatchStackLPXZ.java:140)
//	CounterThread.updateIndex(int)(CounterThread.java:31)
//	CounterThread.run()(CounterThread.java:19)
//	java.lang.Thread.run()(Thread.java:619)
	
	

	public static List<String> getFullStackTrace_lpxz() {		
		String tname =Thread.currentThread().getName();
		List<String> stack =thread2stack.get(tname);
		if(stack==null)
		{
			stack = new ArrayList<String>();
			thread2stack.put(tname, stack);
		}
		stack.clear();
		
		
		// DONOT MOVE IT BEHIND THE FIRST THROW STATEMENT, IT WILL DEGENERATE THE TRACE COLLECTION
		// the following is the optimization code, once open, ever use

		    
		   

		
			      

			
		//==============do not move the code up or down: otherwise, the stack may contain some classload methods======	
	      // dis.available() returns 0 if the file does not have more lines.
		try {
	
		
			File file = new File(CatchStackLPXZ.tracefilename + tname+ ".txt");
			if(!file.exists()) file.createNewFile();// CAN NOT BE REMOVED!! IF REMOVED, THE DATAINPUTSTREAM WILL THROW EXCEPTION, MAKING CATCHSTACKEXCEPTION NOT HAPPEN ANY MORE.
		    FileInputStream fis = null;
		    BufferedInputStream bis = null;
		    DataInputStream dis =null;
		    

			
			
		fis = new FileInputStream(file);
		// Here BufferedInputStream is added for fast reading.
	      bis = new BufferedInputStream(fis);
	      dis = new DataInputStream(bis);
	      
			try {
				throw new CatchStackLPXZException("User Defined Exception");
			} catch (Exception e) {
				
			}	
	      
		     

			
			
	      while (dis.available() != 0) {

	      // this statement reads the line from the file and print it to
	        // the console.
	    	  String readline = dis.readLine();
	    	  if(!readline.isEmpty()) // avoid  the blank line
	    	  {
	    		  stack.add(readline);
	    	  }
	       }
			
	      
	      // this one is for blocking the world.
	      try {
				throw new CatchStackLPXZException("User Defined Exception");
			} catch (Exception e) {
				
			}
			
			fis.close();
			bis.close();
			dis.close();
			
			
	} catch (Exception e) {
      e.printStackTrace();
    }
	   	


	    
		return  stack;
	}
	//====================
	// Running remote VM
	private final VirtualMachine vm;
	public static String tracefilename = "stacktrace";


	

	// Mode for tracing the Trace program (default= 0 off)
	private int debugTraceMode = 0;

	/**
	 * main
	 */
	public static void main(String[] args) {
	//	System.out.println("hello");
		new CatchStackLPXZ(args);
	}

	/**
	 * Parse the command line arguments. Launch target VM. Generate the trace.
	 */
	CatchStackLPXZ(String[] args) {
		PrintWriter writer = new PrintWriter(System.out);
		int inx;
		for (inx = 0; inx < args.length; ++inx) {
			String arg = args[inx];
			if (arg.charAt(0) != '-') {
				break;
			}
			if (arg.equals("-output")) {
				try {
					writer = new PrintWriter(new FileWriter(args[++inx]));
				} catch (IOException exc) {
					System.err.println("Cannot open output file: " + args[inx]
							+ " - " + exc);
					System.exit(1);
				}
			}
		}
		if (inx >= args.length) {
			System.err.println("<class> missing");
			usage();
			System.exit(1);
		}
		StringBuffer sb = new StringBuffer();
		sb.append(args[inx]);
		for (++inx; inx < args.length; ++inx) {
			sb.append(' ');
			sb.append(args[inx]);
		}
		vm = launchTarget(sb.toString());
		generateTrace(writer);
	}

	/**
	 * Generate the trace. Enable events, start thread to display events, start
	 * threads to forward remote error and output streams, resume the remote VM,
	 * wait for the final event, and shutdown.
	 */
	void generateTrace(PrintWriter writer) {
		vm.setDebugTraceMode(debugTraceMode);
		EventThread eventThread = new EventThread(vm, writer);
		eventThread.setEventRequests();
		eventThread.start();
		vm.resume();

		// Shutdown begins when event thread terminates
		try {
			eventThread.join();
		} catch (InterruptedException exc) {
			// we don't interrupt
		}
		writer.close();
	}

	/**
	 * Launch target VM. Forward target's output and error.
	 */
	VirtualMachine launchTarget(String mainArgs) {
		mainArgs = mainArgs.trim();
		LaunchingConnector connector = findLaunchingConnector();
		Map arguments = connectorArguments(connector, mainArgs);
		try {
			return connector.launch(arguments);
		} catch (IOException exc) {
			throw new Error("Unable to launch target VM: " + exc);
		} catch (IllegalConnectorArgumentsException exc) {
			throw new Error("Internal error: " + exc);
		} catch (VMStartException exc) {
			throw new Error("Target VM failed to initialize: "
					+ exc.getMessage());
		}
	}

	/**
	 * Find a com.sun.jdi.CommandLineLaunch connector
	 */
	LaunchingConnector findLaunchingConnector() {
		List connectors = Bootstrap.virtualMachineManager().allConnectors();
		Iterator iter = connectors.iterator();
		while (iter.hasNext()) {
			Connector connector = (Connector) iter.next();
			if ("com.sun.jdi.CommandLineLaunch".equals(connector.name())) {
				return (LaunchingConnector) connector;
			}
		}
		throw new Error("No launching connector");
	}

	/**
	 * Return the launching connector's arguments.
	 */
	Map connectorArguments(LaunchingConnector connector, String mainArgs) {
		Map arguments = connector.defaultArguments();
		Connector.Argument mainArg = (Connector.Argument) arguments.get("main");
		if (mainArg == null) {
			throw new Error("Bad launching connector");
		}
		mainArg.setValue(mainArgs);
		
		 Connector.Argument optsArg = (Connector.Argument) arguments
			.get("options");
	if (optsArg == null) {
		throw new Error("Bad launching connector");
	}
	String cp = "-Djava.class.path=" + System.getProperty("java.class.path") + ":" +
	System.getProperty("sun.boot.class.path") ; // "C:\\test\\";//
	//+ ":/home/lpxz/eclipse/workspace/simple/bin"
	System.out.println(cp);
	
	
  
	optsArg.setValue(cp);
	
		return arguments;
	}

	/**
	 * Print command line usage help
	 */
	void usage() {
		System.err.println("Usage: java Trace <options> <class> <args>");
		System.err.println("<options> are:");
		System.err.println("  -output <filename>   Output trace to <filename>");	
	}
	
	
}



