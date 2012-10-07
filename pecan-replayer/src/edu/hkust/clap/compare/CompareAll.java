package edu.hkust.clap.compare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import edu.hkust.clap.Util;
import org.apache.commons.io.FileUtils;

public class CompareAll {

	public static void main(String[] args) 
	{
			
		
		int fail_num = 0;
		int harmful_num = 0;
		int benign_num = 0;
		
		File dir = new File(Util.getTmpOutputDirectory());
		String[] files = dir.list();	
		
		int total = files.length-1;
			
		for(int id = 1;id< files.length;id++)
		{
			String filename = files[id];
			
			String file_id_path = Util.getTmpOutputDirectory()+filename;
			try{
				String file_contect = readFileAsString(file_id_path);
				
				
				if(file_contect.contains("Failed"))
				{
					System.err.println(filename+": Fail");
					fail_num++;
				}
				else
				{
					 if(compare(filename,"0"))
					 {
						System.err.println(filename+": Benign");
						benign_num++;
					 }
					else
					{
						System.err.println(filename+": Harmful");
						harmful_num++;
					}
				}
			}catch(Exception e)
			{
				
			}

		}
		
		int real_num = total -fail_num;	
		System.out.println("\n----- summary -----\n");
		
		System.out.println("Real: "+real_num);
		System.out.println("Harmful: "+harmful_num);
		System.out.println("Benign: "+benign_num);
		
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
