package edu.hkust.clap.datastructure;
public class MyAccessVector {
	int CAPACITY = 100;
	long[] elementData;
	int[] counter;
	int pos;
	int ID;
	int finalRuntimePos;
	boolean isFirstAccessOnReplay = false;
	
	public long getElement(int i)
	{
		return elementData[i];
	}
	public int getCounter(int i)
	{
		return counter[i];
	}
	public synchronized long get()
	{
		if(!isFirstAccessOnReplay)
		{
			isFirstAccessOnReplay = true;
			finalRuntimePos = pos;
			pos = 1;
		}
		return elementData[pos];
	}
	
//	public synchronized void remove()
//	{
//		if(counter[pos]>0)
//		{
//			counter[pos]--;
//		}
//		else
//		{
//			pos++;
//		}
//	}
	public synchronized void remove()
	{
		pos++;
	}

}
