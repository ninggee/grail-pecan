package edu.hkust.clap.random;

import edu.hkust.clap.monitor.Monitor;

public class Verifier 
{
	static AtomicSet[] atomicsets = new AtomicSet[5];
	static
	{
		for(int i=0;i<5;i++)
		{
			atomicsets[i] = new AtomicSet();
		}
	}
	/**
	 *  LET'S ASSUME 10 SHARED MEMORY LOATIONS
	 *  CONSECTIVE ONES ARE IN THE SAME ATOMIC SET
	 */
	public static synchronized void check(int index, int type, long threadId) {

		//HOW TO DEFINE UNITS OF WOKR??
		//WE MIGHT FIRST ASSUME THE ENTIRE EXECUTION SEQUENCE AS 
		//A SINGLE UNIT OF WORK FOR EACH THREAD
		updateState(index,type,threadId);
		//IF TRUE, JUST STOP AND SIMULATE CRASH
		if(verify(index))
		{
			Pattern pattern = getMatchedPattern(index);
			assert(pattern!=null);
			
			System.out.print("\n");
			System.out.println("****************************************************\n"
			+"*** Crash Point: AtomicSet "+index/2+" - "+atomicsets[index/2].getAcsNum()+" | Pattern "+pattern.getId()+" ***\n"
			+"***                   "+pattern.getAccess(0)+"                ***\n"
			+"***                   "+pattern.getAccess(1)+"                ***\n"
			+"***                   "+pattern.getAccess(2)+"                ***\n"
			+"***                   "+pattern.getAccess(3)+"                ***\n"
			+"****************************************************");
			Monitor.crashed(null);
		}
	}
	private static Pattern getMatchedPattern(int index) {
		return atomicsets[index/2].getMatchedPattern();
	}
	private static boolean verify(int index) {
		if(atomicsets[index/2].patternMatched())
			return true;
		else
			return false;
	}
	private static void updateState(int index, int type, long threadId) 
	{
		int idx = index/2;
		int id = index%2;
		System.err.print("AtomicSet "+ idx+" - ");
		atomicsets[idx].proceed(id,type,threadId);
	}

}
