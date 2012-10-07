package edu.hkust.clap;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import edu.hkust.clap.tracer.TraceReader;

public class MonitorThread extends Thread{
	private static long start_time;
	private static PrintStream myout;
	private static HashMap<String,Object> objectmap;
	
	static PrintStream defaultOutstream;
	public static PrintStream defaultErrstream;
	public MonitorThread()
	{
		super("MonitorThread");
		try{
			defaultOutstream = System.out;
			defaultErrstream = System.err;
			myout = new PrintStream(new BufferedOutputStream( new FileOutputStream(
					Util.getTmpOutputDirectory()+TraceReader.filename)));
			objectmap = new HashMap<String,Object>();
			
			//System.setOut(myout);
		    //System.setErr(myout);
		}catch(Exception e)
		{
			
		}
		start_time = System.currentTimeMillis();
		
	}
	public static void saveObject(String name, Object o)
	{
		objectmap.put(name, o);
	}
	public void run()
	{
		long end_time = System.currentTimeMillis();
		long exe_time = end_time - start_time;
		
		Iterator<String> objectsIt = objectmap.keySet().iterator();
		while(objectsIt.hasNext())
		{
			String name = objectsIt.next();
			Object o = objectmap.get(name);
			
			if(isArray(o))
			{
				String content = getArrayContent(o);
				myout.println(name+": "+content);
			}
			else				
				myout.println(name+": "+o.toString());
		}
		
		
		if(myout!=null)
			myout.close();
		
		System.setOut(defaultOutstream);
		System.out.println("Monitoring time: "+exe_time+" ms");

	}   
	 private String getArrayContent(Object o) {
			//Class componentClass = o.getClass().getComponentType();
			//Arrays.toString((long[])o);
		 String typename = o.getClass().getComponentType().getName();
		 if(typename.equals("int"))
		 {
			 return Arrays.toString((int[])o); 
		 }
		 else if(typename.equals("long"))
		 {
			 return Arrays.toString((long[])o); 
		 }
		 else if(typename.equals("char"))
		 {
			 return Arrays.toString((char[])o); 
		 }
		 else if(typename.equals("boolean"))
		 {
			 return Arrays.toString((boolean[])o); 
		 }
		 else if(typename.equals("byte"))
		 {
			 return Arrays.toString((byte[])o); 
		 }
		 else if(typename.equals("float"))
		 {
			 return Arrays.toString((float[])o); 
		 }
		 else if(typename.equals("double"))
		 {
			 return Arrays.toString((double[])o); 
		 }
		 else if(typename.equals("short"))
		 {
			 return Arrays.toString((short[])o); 
		 }
		else
		{
			return Arrays.toString((Object[])o);
		}
	}
	/**
	   * Check if the given object is an array (primitve or native).
	   *
	   * @param obj  Object to test.
	   * @return     True of the object is an array.
	   */
	  public static boolean isArray(final Object obj) {
	     if (obj != null)
	        return obj.getClass().isArray();
	     return false;
	  }
}	
