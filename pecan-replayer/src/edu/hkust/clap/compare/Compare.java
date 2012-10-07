package edu.hkust.clap.compare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import edu.hkust.clap.Util;
import org.apache.commons.io.FileUtils;

public class Compare {

	public static void main(String[] args) 
	{
		if(args.length==0)
		{
			System.err.println("Please specify the file to be compared...");
			System.exit(0);
		}
		else
		{
			int id = Integer.valueOf(args[0]);
			
			File dir = new File(Util.getTmpOutputDirectory());
			String[] files = dir.list();	
			
			int MAX_INDEX = files.length-1;
			
			if(id>MAX_INDEX)
				id = id%MAX_INDEX;
			if(id==0)
				id=MAX_INDEX;
			
			String filename = files[id];
			
			
			String file_id_path = Util.getTmpOutputDirectory()+filename;
			try{
				String file_contect = readFileAsString(file_id_path);
				
				
				if(file_contect.contains("Failed"))
				{
					System.err.println(filename+": Fail");
					return;
				}
			}catch(Exception e)
			{
				
			}
			 if(compare(filename,"0"))
				System.err.println(filename+": Benign");
			else
				System.err.println(filename+": Real");
			

		}
		
	}
	public static boolean compare(String filea, String fileb)
	{
		
		
		File file1 = new File(Util.getTmpOutputDirectory()+filea);
		File file2 = new File(Util.getTmpOutputDirectory()+fileb);
		// Compare two files.
		try{
			return FileUtils.contentEquals(file1, file2);
		}catch(Exception e)
		{
			
		}
		return false;
	}
    /** @param filePath the name of the file to open. Not sure if it can accept URLs or just filenames. Path handling could be better, and buffer sizes are hardcoded
	    */ 
	    private static String readFileAsString(String filePath)
	    throws java.io.IOException{
	        StringBuffer fileData = new StringBuffer(1000);
	        BufferedReader reader = new BufferedReader(
	                new FileReader(filePath));
	        char[] buf = new char[1024];
	        int numRead=0;
	        while((numRead=reader.read(buf)) != -1){
	            String readData = String.valueOf(buf, 0, numRead);
	            fileData.append(readData);
	            buf = new char[1024];
	        }
	        reader.close();
	        return fileData.toString();
	    }

	// i
}
