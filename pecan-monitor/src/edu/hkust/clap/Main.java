package edu.hkust.clap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import properties.PropertyManager;

import edu.hkust.clap.monitor.Monitor;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		//System.out.println(System.getProperty("java.class.path") + ":" + System.getProperty("sun.boot.class.path"));
		
//		List<String>  arg = new LinkedList(Arrays.asList(args));
//		int len = arg.size();
//		if(len==0)
//			System.err.println("please specify the main class and parameters ... ");
//		else {
//			process(arg);
//		}
		process(Arrays.asList(PropertyManager.mainClassArg.split(" ")));
	}
	
	private static void process(List<String> args)
	{
		int index=-1;
		if(args.contains("-s"))
		{
			index = args.indexOf("-s");
			Monitor.setVecArraySize(Integer.valueOf(args.get(++index)));
		}
//		if(args.contains("-v"))
//		{
//			index = args.indexOf("-v");
//			MyAccessVector.setCapa(Integer.valueOf(args.get(++index)));
//		}
		run(args.subList(++index, args.size()));
	}
	private static void run(List<String> args)
	{	
		try 
		{
			String appname = args.get(0);
		    String[] mainArgs = {};
		    if(args.size() > 1)
		    {
		    	mainArgs = new String[args.size()-1];
		    	for(int k=0;k<args.size()-1;k++)
		    		mainArgs[k] = args.get(k+1);
		    }
		    
		    Monitor.mainThreadStartRun0(appname,mainArgs);
		    
			Class<?> c = Class.forName(appname);
		    Class[] argTypes = new Class[] { String[].class };
		    Method main = c.getDeclaredMethod("main", argTypes);
		    main.setAccessible(true);
		    
			Runtime.getRuntime().addShutdownHook(new MonitorThread());

		    main.invoke(null, (Object)mainArgs);
			// production code should handle these exceptions more gracefully
		   
			} catch (ClassNotFoundException x) {
			    x.printStackTrace();
			} catch (NoSuchMethodException x) {
			    x.printStackTrace();
			} catch (IllegalAccessException x) {
			    x.printStackTrace();
			} catch (InvocationTargetException x) {
			    x.printStackTrace();
			}
		
	}

}
