package edu.hkust.clap.organize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;

import properties.PropertyManager;

import com.thoughtworks.xstream.XStream;

import edu.hkust.clap.engine.CommonUtil;

public class SaveLoad {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Object object = SaveLoad.load(SaveLoad.default_filename);
		List list  =(List)object;
		for(Object elem : list)
		{
			CSMethodPair pair = (CSMethodPair)elem;
			CSMethod o1 = pair.getO1();
			CSMethod o2 = pair.getO2();
			System.out.println(o1.hashCode() + o2.hashCode());
			
		}
		
		

	}
	
	public static String default_AllPatterns = "/home/lpxz/eclipse/workspace/pecan/pecan-monitor/tmp/AllPatterns";
	
	public static String default_filename = "/home/lpxz/eclipse/workspace/pecan/pecan-monitor/tmp/CSMethodPairList" + PropertyManager.projectname;
	
	public static String objectmap_filename = "/home/lpxz/eclipse/workspace/pecan/pecan-monitor/tmp/objectmap" + PropertyManager.projectname;

	public static void save(Object toDump, String filename )
	{
		ObjectOutputStream out = null;
    	try
    	{	
			String path = filename;
			File f = new File(path);
			FileOutputStream fos = new FileOutputStream(f);
			out = new ObjectOutputStream(fos);
			System.err.println("write the CSPairs to :" + path);
			
			out.writeObject(toDump);
    	}
		catch(Exception e)
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
	
	public static Object load(String filename)
	{
		Object obj = null;
		ObjectInputStream in =null;
		try
    	{
			
			File file = new File(filename);
			in = new ObjectInputStream(
					new FileInputStream(file));
			obj = in.readObject();
			System.err.println("load CSPairs from " + filename);
			
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
    	{
    		try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return obj;
    	}
		
	}

	public static void saveX(Object toDump, String filename )
	{
		XStream xStream = new XStream();
		
	
    	try
    	{	
			String path = filename;
			File f = new File(path);
			FileOutputStream fos = new FileOutputStream(f);
			xStream.toXML(toDump, fos);
			
    	}
		catch(Exception e)
    	{
    		e.printStackTrace();
    	}
	}
	
	public static Object loadX(String filename)
	{
		XStream xStream = new XStream();
		Object obj = null;
		ObjectInputStream in =null;
		try
    	{
			
			File file = new File(filename);
			FileInputStream fis = new FileInputStream(file);
			
			obj = xStream.fromXML(fis);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
    	{

			return obj;
    	}
		
	}

	
}
