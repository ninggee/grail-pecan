package edu.hkust.clap.tracer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.GZIPInputStream;


import edu.hkust.clap.*;
import edu.hkust.clap.datastructure.AbstractNode;
import edu.hkust.clap.datastructure.MyAccessVector;
import edu.hkust.clap.monitor.MonitorData;


public class TraceReader {

	public static Vector<AbstractNode> trace;
	public static Vector<Long> schedule_tid;
	public static Vector<Integer> schedule_line;
	
	public static MonitorData mondata;
	public static int file_index = 0;//0: same schedule as record
	public static String filename;
	public static HashMap<String,Long> threadNameToIdMap = new HashMap<String,Long>();
	static Date traceFileDate;

	public static void setFileIndex(String[] args)
	{
		if(args.length>0)
		file_index = Integer.valueOf(args[0]);
	}

	/**
	 * Read traceItem from serialized information in file
	 * 
	 */
	public synchronized static void readTrace(int type, String traceFileName) throws Exception{

		ObjectInputStream in = null;

		try {
			File traceFile = new File(traceFileName);
			

			if (traceFileName.endsWith(".gz")) {
				in = new ObjectInputStream(new GZIPInputStream(
						new FileInputStream(traceFile)));
			} else {
				in = new ObjectInputStream(new FileInputStream(traceFileName));
			}
		
			switch (type)
			{
				case 1:
					
						
						String[] tid_files = getSchedulePath("schedule_tid");
						String[] line_files = getSchedulePath("schedule_line");
						
						int MAX_INDEX = tid_files.length-1;
						
						if(file_index>MAX_INDEX)
						{
							file_index = file_index%MAX_INDEX;
							if(file_index==0)
							{
								file_index =MAX_INDEX;
							}
						}
						
						filename = tid_files[file_index];
						
						if(file_index==0)
						{
							//System.err.println("Serial Execution");
							String directoryName = Util.getTmpOutputDirectory();
							Util.deleteOldOutputs(directoryName);
						}
						else
							System.err.println(filename);
						
//						if(tid_files[index].contains("CVS"))
//							index++;
						
						schedule_tid = (Vector<Long>)getTrace("schedule_tid"+System.getProperty("file.separator")+filename);
						schedule_line = (Vector<Integer>)getTrace("schedule_line"+System.getProperty("file.separator")+filename);

					//printAccessVector();
					break;
				case 2:
					threadNameToIdMap = (HashMap<String,Long>) Util.loadObject(in);
					break;
				default:
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//return object;
		
	}
	public static String[] getSchedulePath(String path)
	{
		File dir = new File(Util.getTmpRecordDirectory()+path);

		String[] children = dir.list();
		if (children == null) {
		    // Either dir does not exist or is not a directory
		} else {
//		    for (int i=0; i<children.length; i++) {
//		        // Get filename of file or directory
//		        String filename = children[i];
//		    }
		}
		return children;
	}
	public static Object getTrace(String path)
	{
		Object obj = null;
		try {
			File traceFile = new File(Util.getTmpRecordDirectory()+path);
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
//	private static void printAccessVector() {
//		for(int i=0; i<accessVector.length;i++)
//		{
//			int size = accessVector[i].getSize();
//			System.out.print(i+": ");
//			for(int j=0;j<size;j++)
//				System.out.print(accessVector[i].getElement(j)+" ");
//			System.out.println(" ");
//			System.out.print(i+": ");
//			for(int j=0;j<size;j++)
//				System.out.print(accessVector[i].getCounter(j)+" ");
//			System.out.println(" ");
//		}
//	}
	
	public static Date getTraceFileDate() {
		return traceFileDate;
	}
}
