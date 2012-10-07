package edu.hkust.clap.lpxz.jvmStack;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Type;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
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
import com.sun.jdi.request.ThreadDeathRequest;
import com.sun.jdi.request.ThreadStartRequest;

public class EventThread extends Thread {

	private final VirtualMachine vm; // Running VM

	private final String[] excludes = {"java.*", "javax.*", "sun.*", "com.sun.*", "com.ibm.*"}; // 
	private static String[] allowedHashTable = {"edu.hkust.clap.lpxz.jvmStack.CatchStackLPXZException"};
	private boolean isAllowedException(String name) {
		 for(String str : allowedHashTable)
		 {
			 if(str.equals(name))
			 return true;
		 }		
		return  false;
	}
	
	
	private final PrintWriter writer; // Where output goes

	private boolean connected = true; // Connected to VM

	private boolean vmDied = true; // VMDeath occurred


	

	// Maps ThreadReference to ThreadTrace instances
	//private Map<ThreadReference, ThreadTrace> traceMap = new LinkedHashMap<ThreadReference, ThreadTrace>();

	
	EventThread(VirtualMachine vm, PrintWriter writer) {
		super("event-handler");
		this.vm = vm;		
		this.writer = writer;
	}

	/**
	 * Run the event handling thread. As long as we are connected, get event
	 * sets off the queue and dispatch the events within them.
	 */
	public void run() {
		EventQueue queue = vm.eventQueue();
		while (connected) {
			try {
				EventSet eventSet = queue.remove();
				EventIterator it = eventSet.eventIterator();
				while (it.hasNext()) {
					handleEvent(it.nextEvent());
				}
				eventSet.resume();
			} catch (InterruptedException exc) {
				// Ignore
			} catch (VMDisconnectedException discExc) {
				handleDisconnectedException();
				break;
			}
		}
	}

	/**
	 * Register events we want
	 * Create the desired event requests, and enable them so that we will get
	 * events.
	 * 
	 * @param excludes
	 *            Class patterns for which we don't want events
	 * @param watchFields
	 *            Do we want to watch assignments to fields
	 */
	void setEventRequests() {
		EventRequestManager mgr = vm.eventRequestManager();
		// want all exceptions
		ExceptionRequest excReq = mgr.createExceptionRequest(null, true, true);
		// suspend so we can step
		for (int i = 0; i < excludes.length; ++i) {
			excReq.addClassExclusionFilter(excludes[i]);
	    }
		excReq.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);// may suspend all if necessary
		excReq.enable();

		// do not worry, we use two exceptions to make sure the first exception wont pass away too soon.
		// remember that, the point stops only when it occurs, after being popped, the system does not block at all
		
		
		// make the exception event not pass its method quickly: work as a stop point. Otherwise, the exception's method may pass quickly or not. (uncertain output)
//		MethodEntryRequest menr = mgr.createMethodEntryRequest();// when happen, blocking the thread. when disposed, no blocking
//		for (int i = 0; i < excludes.length; ++i) {
//			menr.addClassExclusionFilter(excludes[i]);			
//		}
//		menr.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
//		menr.enable();

//		MethodExitRequest mexr = mgr.createMethodExitRequest();
//		for (int i = 0; i < excludes.length; ++i) {
//			mexr.addClassExclusionFilter(excludes[i]);
//		}
//		mexr.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
//		mexr.enable();

		ThreadStartRequest tsr = mgr.createThreadStartRequest();
		// Make sure we sync on thread death
		tsr.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
		tsr.enable();

		ThreadDeathRequest tdr = mgr.createThreadDeathRequest();
		// Make sure we sync on thread death
		tdr.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
		tdr.enable();
	}

	
	/**
	 * Dispatch incoming events
	 */
	private void handleEvent(Event event) {
		if (event instanceof ExceptionEvent) {
		 //System.out.println(event);
			if (isAllowedException(((ExceptionEvent) event)
					.exception().referenceType().name())) {
				//System.out.println(event);
				exceptionEvent((ExceptionEvent) event);
			}
		} else if (event instanceof MethodEntryEvent) {
//			if (isMainThreadOrCreatedFromMain(((MethodEntryEvent) event)
//					.thread())) {
//				methodEntryEvent((MethodEntryEvent) event);
//			}
		} else if (event instanceof MethodExitEvent) {
//			if (isMainThreadOrCreatedFromMain(((MethodExitEvent) event)
//					.thread())) {
//				methodExitEvent((MethodExitEvent) event);
//			}
		} else if (event instanceof StepEvent) {
		//	stepEvent((StepEvent) event);
		} else if (event instanceof ThreadStartEvent) {
		//	threadStartEvent((ThreadStartEvent) event);
		} else if (event instanceof ThreadDeathEvent) {
		//	threadDeathEvent((ThreadDeathEvent) event);
		} else if (event instanceof VMStartEvent) {
			vmStartEvent((VMStartEvent) event);
		} else if (event instanceof VMDeathEvent) {
			vmDeathEvent((VMDeathEvent) event);
		} else if (event instanceof VMDisconnectEvent) {
			vmDisconnectEvent((VMDisconnectEvent) event);
		} else {
			throw new Error("Unexpected event type");
		}
	}



	/***************************************************************************
	 * A VMDisconnectedException has happened while dealing with another event.
	 * We need to flush the event queue, dealing only with exit events (VMDeath,
	 * VMDisconnect) so that we terminate correctly.
	 */
	synchronized void handleDisconnectedException() {
		EventQueue queue = vm.eventQueue();
		while (connected) {
			try {
				EventSet eventSet = queue.remove();
				EventIterator iter = eventSet.eventIterator();
				while (iter.hasNext()) {
					Event event = iter.nextEvent();
					if (event instanceof VMDeathEvent) {
						vmDeathEvent((VMDeathEvent) event);
					} else if (event instanceof VMDisconnectEvent) {
						vmDisconnectEvent((VMDisconnectEvent) event);
					}
				}
				eventSet.resume(); // Resume the VM
			} catch (InterruptedException exc) {
				// ignore
			}
		}
	}

	private void vmStartEvent(VMStartEvent event) {
		//writer.println("-- VM Started --");
	}

	// Forward event for thread specific processing
//	private void methodEntryEvent(MethodEntryEvent event) {
//		//threadTrace(event.thread()).methodEntryEvent(event);
//	}

	// Forward event for thread specific processing
//	private void methodExitEvent(MethodExitEvent event) {
//		//threadTrace(event.thread()).methodExitEvent(event);
//	}

	// Forward event for thread specific processing
//	private void stepEvent(StepEvent event) {
//		//threadTrace(event.thread()).stepEvent(event);
//	}

//	void threadDeathEvent(ThreadDeathEvent event) {
//		ThreadTrace trace = (ThreadTrace) traceMap.get(event.thread());
//		if (trace != null) { // only want threads we care about
//			trace.threadDeathEvent(event); // Forward event
//		}
//	}
//
//	void threadStartEvent(ThreadStartEvent event) {
//		threadTrace(event.thread()).threadStartEvent(event);
//	}

	private void exceptionEvent(ExceptionEvent event) {		
		event.thread().suspend();
		try {
			exploreStackSnapShot(event.thread());
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			event.thread().resume();
		}
		
		
		
		// Step to the catch
		// Only one step request allowed per thread
//		EventRequestManager mgr = vm.eventRequestManager();
//		StepRequest req = mgr.createStepRequest(thread,
//				StepRequest.STEP_MIN, StepRequest.STEP_INTO);
//		req.addCountFilter(1); // next step only
//	
//		req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
//		req.enable();
		
	}
	
	

	public void vmDeathEvent(VMDeathEvent event) {
		vmDied = true;
		//printAllThreadInfos();
		//writer.println("-- The application exited --");
	}

	public void vmDisconnectEvent(VMDisconnectEvent event) {
//		try {
//			for(FileInputStream fis:CatchStackLPXZ.openedFIS.values())
//			{
//				
//					fis.close();				
//			}
//			
//			for(BufferedInputStream bis:CatchStackLPXZ.openedBIS.values())
//			{
//				
//					bis.close();				
//			}
//			
//			for(DataInputStream dis:CatchStackLPXZ.openedDIS.values())
//			{
//				
//					dis.close();				
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
		
		
		connected = false;
	
		
//		if (!vmDied) {
//			writer.println("-- The application has been disconnected --");
//		}
	}
	
	public boolean isMainThreadOrCreatedFromMain(ThreadReference tr) {
		if (tr == null || "system".equalsIgnoreCase(tr.name())) {
			return false;
		}
		if ("main".equalsIgnoreCase(tr.name())
				|| "main".equalsIgnoreCase(tr.threadGroup().name())) {
			return true;
		}
		return false;
	}

	
	private void exploreStackSnapShot(ThreadReference thisThread) {
		if (isMainThreadOrCreatedFromMain(thisThread)) 
		{
			try {

				
				List<StackFrame> frames =thisThread.frames();				
				Map last =null;
				String  fullSte = "";
				for (StackFrame frame : frames) {
//					Method mm =frame.location().method();
//					System.out.println(mm.signature());
				Location loc =	frame.location();
				int callsitelineNO= loc.lineNumber();
				String sourcename  = loc.sourceName();
				Method theMethod  =loc.method();
				ReferenceType refType =theMethod.declaringType();
				String methodName =theMethod.name();
				//String sig = theMethod.signature();// dislike it
				List<Type> argTypes = theMethod.argumentTypes();
				String arglist = "(" ;
				for(Type argType: argTypes)
				{
					arglist += argType.name() + ",";
				}
				if(arglist.endsWith(","))
				{
					arglist = arglist.substring(0, arglist.length()-1);
				}
				arglist+=")";
				
				
				 fullSte += "" + refType.name() + "." + methodName + arglist + "(" + sourcename+ ":" + callsitelineNO+ ")" + "\n";
				
				 //				fullStes.add(fullSte);				
				}
				
				//System.out.println(fullSte);
				writeStaackTraceOfThread(thisThread, fullSte);
			} catch (Exception e) {
				// ignore the exception
			} 
		}
	}
	/**
	 * This class keeps context on events in one thread. In this implementation,
	 * context is the indentation prefix.
	 */
//	class ThreadTrace {
////		final ThreadReference thread;
////
////		String baseIndent = "   ";
////
////		static final String threadDelta = "\t";
////
////		StringBuffer logRecord;
////		
////		Stack<Method> methodStack = new Stack<Method>();
////
////		ThreadTrace(ThreadReference thread) {
////			this.thread = thread;
////			logRecord = new StringBuffer(256);
////			println("====== " + thread.name() + " ======");
////		}
////
////		private void println(String str) {
////			logRecord.append(this.baseIndent+str);
////			logRecord.append(System.getProperty("line.separator"));
////		}
////
////		String getLogRecord() {
////			return logRecord.toString();
////		}
//
////		void methodEntryEvent(MethodEntryEvent event) {
////			methodStack.push(event.method());
////			increaseIndent();
////			println("Enter Method:" + event.method().name());
////			this.printVisiableVariables();
////			
////		}
////
////		void methodExitEvent(MethodExitEvent event) {
////			println("Exit Method:" + event.method().name());
////			decreaseIndent();
////			methodStack.pop();
////		}
//
//		
//
//		// Step to exception catch
////		void stepEvent(StepEvent event) {
////			//when exception happens, adjust the indent
////			while(methodStack.capacity()>0 && methodStack.peek()!= event.location().method())
////			{
////				this.decreaseIndent();
////				methodStack.pop();
////			}
////			EventRequestManager mgr = vm.eventRequestManager();
////			mgr.deleteEventRequest(event.request());
////		}
////
////		void threadDeathEvent(ThreadDeathEvent event) {
////			println("====== " + thread.name() + " end ======");
////			println("");
////		}
////
////		void threadStartEvent(ThreadStartEvent event) {
////			println("Thread " + event.thread().name() + " Start");
////		}
//
////		private void printVisiableVariables()
////		{
////			try
////			{
////				this.thread.suspend();
////				if(this.thread.frameCount()>0)
////				{
////					//retrieve current method frame
////					StackFrame frame = this.thread.frame(0);
////					List<Field> fields = frame.thisObject().referenceType().allFields();
////					increaseIndent();
////					for (Field field : fields) {
////						println(field.name() + "\t"
////								+ field.typeName()
////								+ "\t" + frame.thisObject().getValue(field));
////					}
////					decreaseIndent();
////				}
////			}
////			catch(Exception e)
////			{
////				//ignore
////			}
////			finally
////			{
////				this.thread.resume();
////			}
////		}
////		
////		
////		
////		
////		private void printStackSnapShot() {
////			if (isMainThreadOrCreatedFromMain(this.thread)) {
////				try {
////					this.thread.suspend();
////					println("Thread Status:" + this.thread.status());
////					println("FrameCount in thread:" + this.thread.frameCount());
////					List<StackFrame> frames = this.thread.frames();
////					for (StackFrame frame : frames) {
////						println("Frame(" + frame.location()
////								+ ")");
////						if (frame.thisObject() != null) {
////							increaseIndent();
////							println("");
////							List<Field> fields = frame.thisObject()
////									.referenceType().allFields();
////							for (Field field : fields) {
////								println(field.name() + "\t"
////										+ field.typeName()
////										+ "\t" 
////										+ frame.thisObject().getValue(field));
////							}
////							decreaseIndent();
////						}
////						List<LocalVariable> lvs = frame.visibleVariables();
////						increaseIndent();
////						println("");
////						for (LocalVariable lv : lvs) {
////							println(lv.name() + "\t" 
////									+ lv.typeName() + "\t" 
////									+ frame.getValue(lv));
////						}
////						decreaseIndent();
////					}
////				} catch (Exception e) {
////					// ignore the exception
////				} finally {
////					this.thread.resume();
////				}
////			}
////		}
////		
////		public void increaseIndent()
////		{
////			baseIndent += threadDelta;
////		}
////		
////		public void decreaseIndent()
////		{
////			baseIndent = baseIndent.substring(0,baseIndent.length()-threadDelta.length());
////		}
//	}

	private void writeStaackTraceOfThread(ThreadReference thisThread,
			String fullSte) {
	
		 FileWriter fstream;
		try {
			// id is immutable! it is a safe identifier
			fstream = new FileWriter(CatchStackLPXZ.tracefilename + thisThread.name()+".txt");
			  BufferedWriter out = new BufferedWriter(fstream);			 
				 out.write(fullSte);
				 out.flush();
				  out.close();
		} catch (IOException e) {
			
		throw new RuntimeException("fix the problem!");
		}

		
	}
	
}

	/**
	 * Returns the ThreadTrace instance for the specified thread, creating one
	 * if needed.
	 */
//	ThreadTrace threadTrace(ThreadReference thread) {
//		ThreadTrace trace = (ThreadTrace) traceMap.get(thread);
//		if (trace == null) {
//			trace = new ThreadTrace(thread);
//			traceMap.put(thread, trace);
//		}
//		return trace;
//	}

	
//	public void printAllThreadInfos() {
//		Set<ThreadReference> threadSet = traceMap.keySet();
//		for (ThreadReference thread : threadSet) {
//			writer.println(traceMap.get(thread).getLogRecord());
//			writer
//					.println("*********************************************************");
//		}
//	}


