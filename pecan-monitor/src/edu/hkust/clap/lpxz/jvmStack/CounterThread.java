package edu.hkust.clap.lpxz.jvmStack;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

public class CounterThread implements Runnable {

	private String name = null;

	private static int index = 0;
	
	public CounterThread(String name) {
		this.name = name;
	}

	public void run() {
		try {
			for(int i=0;i<1;i++)
			{
				this.updateIndex();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void updateIndex()
	{
		
		
		synchronized(CounterThread.class)
		{
			++index;
		}
	}

}
