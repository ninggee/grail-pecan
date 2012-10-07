package edu.hkust.clap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import properties.PropertyManager;
import edu.hkust.clap.monitor.Monitor;


public class MonitorThread extends Thread{
	private ObjectOutputStream out;
	private static String path;
	private static long start_time;
	MonitorThread()
	{
		super("MonitorThread");
		start_time = System.currentTimeMillis();
	}
	public void run()
	{
		long end_time = System.currentTimeMillis();
		long exe_time = end_time - start_time;
		System.out.println("come here!!!");
		System.out.println("Monitoring time: "+exe_time+" ms");
        
		saveMonitorData();
	//	Monitor.generateTestDriver(Monitor.saveMonitorData());// for replay..
		System.out.println("Save monitordata to "+path);
	}
    public static ObjectOutputStream getOutputStream()
    {
    	ObjectOutputStream out = null;
    	try
    	{
			String userdir = PropertyManager.SystemUserDir;//user.dir
			if (!(userdir.endsWith("/") || userdir.endsWith("\\"))) {
				userdir = userdir + System.getProperty("file.separator");
			}
			String tempdir = userdir+"tmp";
			File tempFile = new File(tempdir);
			if(!(tempFile.exists()))
				tempFile.mkdir();
			
			path = tempdir+System.getProperty("file.separator")+ "trace" +PropertyManager.projectname;
			System.out.println("save trace to path: " + path);
			File f = new File(path);
			FileOutputStream fos = new FileOutputStream(f);
			out = new ObjectOutputStream(fos);
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		return null;
    	}
    	return out;
    }
    private void saveMonitorData()
    {
		try
		{
			out = getOutputStream();
    		out.writeObject(Monitor.mondata);
		}catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
    	{
    		try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    public static Object getMonitorData()
    {
    	Object obj = null;
    	try {
			String path = PropertyManager.SystemUserDir;
			path = path+System.getProperty("file.separator")+"tmp"+System.getProperty("file.separator")+"trace";
			File traceFile = new File(path);
			FileInputStream fis = new FileInputStream(traceFile);
			ObjectInputStream in = new ObjectInputStream(fis);
		
			obj = in.readObject();

		} catch (IOException e) {
			e.printStackTrace();
		}finally
		{
			return obj;
		}
    }
}	
