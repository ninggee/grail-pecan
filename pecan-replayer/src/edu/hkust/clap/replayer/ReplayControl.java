package edu.hkust.clap.replayer;

import java.util.Vector;

import edu.hkust.clap.MonitorThread;
import edu.hkust.clap.datastructure.AbstractNode;
import edu.hkust.clap.tracer.TraceReader;

public class ReplayControl 
{
	private static String lastThreadName;
	
	public static void enforceSerialExecution()
	{
		String threadName = Thread.currentThread().getName();
	
		if(lastThreadName==null)
		{
			lastThreadName=threadName;
			return;
		}
		else if(lastThreadName.equals(threadName))
			return;
		else
		{
			synchronized (ActiveChecker.lock)
			{
				(new PhetChecker(0)).check();
			}
			
			ActiveChecker.blockIfRequired();
			lastThreadName=null;
		}
	}
	public static void check(int linenumber) {
		
		if(TraceReader.schedule_tid!=null)
		{
			Vector<Long> schedule_tid = TraceReader.schedule_tid;
			Vector<Integer> schedule_line = TraceReader.schedule_line;
			String threadName = Thread.currentThread().getName();

			
			Long threadId = TraceReader.threadNameToIdMap.get(threadName);
			
			if(threadId==null||!schedule_tid.contains(threadId))//||threadId==1
				return;
				
			if(schedule_tid.isEmpty())
			{
				return;
				//HOPING FOR EXECEPTION!
				//System.exit(0);
			}
			
			long tid = schedule_tid.get(0);
//			while(tid==1)
//			{
//				schedule_tid.remove(0);
//				tid = schedule_tid.get(0);
//			}
			
//			if(tid == 0)
//			{
//				//SHOULD DISABLE EXECUTION OF THE THREAD?
//				schedule_tid.remove(0);
//				schedule_line.remove(0);
//				return;
//			}

			synchronized (ActiveChecker.lock)
			{
				if(threadId!=tid)
				{
					(new PhetChecker(0)).check();
				}
			}
			tid = schedule_tid.get(0);
			while(threadId!=tid)
			{
				ActiveChecker.blockIfRequired();
				try{
					tid = schedule_tid.get(0);
				}catch(Exception e)
				{
					if(e instanceof ArrayIndexOutOfBoundsException)
					{
						return;
					}
				}
			}
			
			int line = schedule_line.get(0);
			
			if(linenumber>0)
			{
				if(line!=linenumber)
				{
					//System.err.println("Replay failed! Line number does not match!");
					//System.exit(0);
					oneStepForward(tid,line);
				}
				else
				{
					oneStepForward(tid,line);
				}
			}
			schedule_tid.remove(0);
			schedule_line.remove(0);
			if(schedule_tid.isEmpty())
			{
				if(TraceReader.file_index==0)
				{
					MonitorThread.defaultErrstream.println("Replay Successfully!");
				}
				else
					
				MonitorThread.defaultErrstream.println("Violation Successfully Created!");
				//HOPING FOR EXECEPTION!
				//System.exit(0);
			}
//			try {
//				Thread.currentThread().sleep(10);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		else
		{
			//System.err.println("--- unexpected objectMap null ---");
		}
			
	}
	private static void oneStepForward(long tid, int line)
	{
		Scheduler.clearCounter();
		
		//System.out.println("tid = "+tid+" | line = "+line);
		
	}
}
