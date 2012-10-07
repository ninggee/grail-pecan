package edu.hkust.clap.lpxz.jvmStack;


import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.org.apache.bcel.internal.generic.NEW;




class MainThread {
	int a = 0, b = 1, c = 2;

	public MainThread()
	{
		
	}


	public static void main(String[] args) {
		int e = 4;
		int g = 5;
		MainThread mt = new MainThread();
		//simulate exception scenario
		for(int i=0; i<2; i++)
		{
			try {
				if(i == 0)
				{
					//user defined exception
					test(i, e);
						  
				}
				if(i == 1)
				{
					//null pointer exception
					//throw new NullPointerException();
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}	
		Thread thread1 = new Thread(new CounterThread("thread1"));
		Thread thread2 = new Thread(new CounterThread("thread2"));
		thread1.start();
		thread2.start();
	}


	private static void test(int i, int e) {
		StackTraceElement[] eles =Thread.currentThread().getStackTrace();
		List<String> stes =CatchStackLPXZ.getFullStackTrace_lpxz();
		try{
			  // Create file 
		

			  FileWriter fstream = new FileWriter("out"+ Thread.currentThread().getName()+".txt");
			  BufferedWriter out = new BufferedWriter(fstream);
			  for(String str :stes)
				{
					out.write(str + "\n");
					
				}
			  
//			  for(StackTraceElement ele:eles)
//			  {
//				  out.write(""+ ele+"\n");
//			  }
			    
			  //Close the output stream
			  out.close();
			  }catch (Exception e2){//Catch exception if any
			  System.err.println("Error: " + e2.getMessage());
			  }
			  
		
	}



	
	
	
}