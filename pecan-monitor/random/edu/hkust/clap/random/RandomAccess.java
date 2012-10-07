package edu.hkust.clap.random;

import java.util.HashMap;
import java.util.Random;

import edu.hkust.clap.Parameters;

public class RandomAccess {
	static HashMap<String,Random> actmap = new HashMap<String,Random>();
	static HashMap<String,Random> indexmap = new HashMap<String,Random>();
	static long seed = Parameters.RAND_SEED;
	static int rate = Parameters.ACS_TYPE_RATE;
	public static int getRandomAccessType(String threadName)
	{
		
		Random generator = actmap.get(threadName);
		if(generator==null)
		{
			
			generator = new Random(getLong(threadName.getBytes()));
			actmap.put(threadName, generator);
		}
		
		int num = generator.nextInt()%rate;
		if(num==0)
			return 1;//WRITE
		else
			return 0;//READ
	}
	public static int getRandomSPEIndex(String threadName)
	{
		Random generator = indexmap.get(threadName);
		if(generator==null)
		{
			
			generator = new Random(seed+getLong(threadName.getBytes()));
			indexmap.put(threadName, generator);
		}
		
		int num = generator.nextInt()%10;
		if(num<0)
			num=-num;
		
		return num;
	}
	private static long getLong(byte[] bytes) 
	{
		long l =0;
		for(int i =0; i <bytes.length; i++){	    	
			   l <<= 8;
			   l ^= (long)bytes[i] & 0xFF;	    	
			}
		return l;
	}
}
