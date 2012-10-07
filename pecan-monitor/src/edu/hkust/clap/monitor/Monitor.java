package edu.hkust.clap.monitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import properties.PropertyManager;


//import com.google.common.collect.ArrayListMultimap;

import edu.hkust.clap.*;
import edu.hkust.clap.datastructure.*;
import edu.hkust.clap.engine.CommonUtil;
import edu.hkust.clap.generator.*;
import edu.hkust.clap.lpxz.context.ContextValueManager;

public class Monitor 
{
   static	boolean  debug = false;
	public static int VECARRAYSIZE = 500;
	private static int objectIndexCounter = 500;
	
	public static Throwable crashedException=null;
	public static HashMap<String,Long> threadNameToIdMap = new HashMap<String,Long>();
	private static HashMap<Integer,Integer> lockCountsMap = new HashMap<Integer,Integer>();
	private static HashMap<Integer,LockNode> lockDepMap = new HashMap<Integer,LockNode>();
	
	private static HashMap<Object,Integer> objectMemMap = new HashMap<Object,Integer>();

	public static AbstractNode entryNode;
	public static MonitorData mondata;
	
	public static HashMap<Thread,MessageNode> threadToStartNode = new HashMap<Thread,MessageNode>();
	public static HashMap<Thread,MessageNode> threadToExitNode = new HashMap<Thread,MessageNode>();
	
	public static HashMap<Integer,MessageNode> indexToDeterminNode = new HashMap<Integer,MessageNode>();

	//public static ThreadLocal<HashSet<Integer>> locksets = new ThreadLocal<HashSet<Integer>>();
	//public static ThreadLocal<AbstractNode> currentNodes = new ThreadLocal<AbstractNode>();
	
	//public static ArrayListMultimap<Integer,long[]> multimap = ArrayListMultimap.create();

	public static String methodname;
    public static String[] mainargs;
    
	public static void setVecArraySize(Integer size) {
		VECARRAYSIZE = size;
		objectIndexCounter = size;
	}
	private static int getObjectMem(Object o)
	{
		if(objectMemMap.get(o)==null)
		{
			objectMemMap.put(o, objectIndexCounter++);
		}
		return objectMemMap.get(o);
	}
    //synchronized
    public  static String saveMonitorData()// why need to synchronized? can not understand
    {
    	String traceFile_=null;
		File traceFile_monitordata = null;
		File traceFile_threadNameToIdMap= null;
		ObjectOutputStream fout_monitordata;
		ObjectOutputStream fout_threadNameToIdMap;
		
		//SAVE Runtime Information
		try 
		{
			System.out.println("come here!!!");
			// for replay!
			traceFile_monitordata = File.createTempFile("Clap", "_monitordata.trace.gz", new File(
					Util.getReplayTmpDirectory(methodname)));
			
			String traceFileName = traceFile_monitordata.getAbsolutePath();
			int index  =traceFileName.indexOf("_monitordata");
			traceFile_ = traceFileName.substring(0, index);
			traceFile_threadNameToIdMap = new File(traceFile_+"_threadNameToIdMap.trace.gz");
			
			assert (traceFile_monitordata != null && traceFile_threadNameToIdMap != null);

			fout_monitordata = new ObjectOutputStream(new GZIPOutputStream(
					new FileOutputStream(traceFile_monitordata)));
			fout_threadNameToIdMap = new ObjectOutputStream(new GZIPOutputStream(
					new FileOutputStream(traceFile_threadNameToIdMap)));			
					
			Util.storeObject(mondata, fout_monitordata);
			Util.storeObject(threadNameToIdMap, fout_threadNameToIdMap);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return traceFile_;
    }
    public static void generateTestDriver(String traceFile_)
    {
		//GENERATE Test Driver		
		try {
			CrashTestCaseGenerator.main(new String[] { traceFile_,
					Util.getTmpReplayDirectory() });
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	public synchronized static void crashed(Throwable crashedException) 
	{
		
		Parameters.isCrashed = true;
		System.exit(-1);
		
		System.err.println("--- program crashed! ---");
		System.err.println("--- preparing for reproducing the crash ... ");
		String traceFile_ = saveMonitorData();
		System.err.println("--- generating the test driver program ... ");
		generateTestDriver(traceFile_);
		
		System.exit(0);
	}

    public synchronized static void startRunThreadBefore(Thread t, long threadId)
    {	
    	int iid = System.identityHashCode(t);
    	int id = getObjectMem(iid);
    	
//		AbstractNode startNode = new AbstractNode(
//				id,threadId,AbstractNode.TYPE.SEND,
//				new HashSet<Integer>(getCurrentThreadLockSet()),new HashSet<AbstractNode>(),new HashSet<AbstractNode>());
    	MessageNode startNode = new MessageNode(
				id,threadId,AbstractNode.TYPE.SEND);
    	if(debug)
          System.out.println("MessageNode startNode = new MessageNode("+ id+ ","
        		+ threadId+ ",AbstractNode.TYPE.SEND);");
    	
    	mondata.addToTrace(startNode);
		threadToStartNode.put(t,startNode);
	}
    
	public synchronized static void threadStartRun(long threadId)
	{    	
		Thread currentThread = Thread.currentThread();
		String threadName = currentThread.getName();
		threadNameToIdMap.put(threadName,threadId);
		
		//locksets.set(new HashSet<Integer>());
	//	if(threadId ==9)
//		{
//		    System.out.println(threadId);
//		    Thread.currentThread().dumpStack();
//		}
	
		int iid = System.identityHashCode(currentThread);
    	int id = getObjectMem(iid);
    	
//		AbstractNode newNode = new AbstractNode(
//				id,threadId,AbstractNode.TYPE.RECEIVE,
//				new HashSet<Integer>(getCurrentThreadLockSet()),new HashSet<AbstractNode>(),new HashSet<AbstractNode>());
    	
    	MessageNode newNode = new MessageNode(
				id,threadId,AbstractNode.TYPE.RECEIVE); 
    	if(debug)
    	{
    		System.out.println("MessageNode newNode = new MessageNode("+id+","+threadId+",AbstractNode.TYPE.RECEIVE);");
    	}
    	  
    	  
		mondata.addToTrace(newNode);
		
		MessageNode node = threadToStartNode.get(currentThread);
		if(node!=null)
		{
			newNode.setDepNode(node);
		}
	}
	public synchronized static void threadExitRun(long threadId)
	{
		Thread currentThread = Thread.currentThread();
		
		int iid = System.identityHashCode(currentThread);
    	int id = getObjectMem(iid);
    	
//		AbstractNode exitNode = new AbstractNode(
//				id,threadId,AbstractNode.TYPE.SEND,
//				new HashSet<Integer>(getCurrentThreadLockSet()),new HashSet<AbstractNode>(),new HashSet<AbstractNode>());

    	MessageNode exitNode = new MessageNode(
				id,threadId,AbstractNode.TYPE.SEND);
    	
    	if(debug)
    	{
    		System.out.println("MessageNode exitNode = new MessageNode("+id+"," +threadId+",AbstractNode.TYPE.SEND);");
    	}
    	 
		mondata.addToTrace(exitNode);
		
		threadToExitNode.put(currentThread,exitNode);
	}
    public synchronized static void joinRunThreadAfter(Thread t,long threadId)
    {
    	int iid = System.identityHashCode(t);
    	int id = getObjectMem(iid);
    	
//		AbstractNode newNode = new AbstractNode(
//				id,threadId,AbstractNode.TYPE.RECEIVE,
//				new HashSet<Integer>(getCurrentThreadLockSet()),new HashSet<AbstractNode>(),new HashSet<AbstractNode>());

    	MessageNode newNode = new MessageNode(
				id,threadId,AbstractNode.TYPE.RECEIVE);
    	if(debug)
    	{
    		System.out.println("MessageNode newNode = new MessageNode("+id+"," + threadId+",AbstractNode.TYPE.RECEIVE);");
    	}
    	 
		
		mondata.addToTrace(newNode);
		
		MessageNode node = threadToExitNode.get(t);
		if(node!=null)
		{
			newNode.setDepNode(node);
		}
    }
    
    public synchronized static void waitAfter(Object o, int iid, long threadId)
    {	    
   	 	iid = System.identityHashCode(o);
    	int id = getObjectMem(iid);
    	
//		AbstractNode newNode = new AbstractNode(
//				id,threadId,AbstractNode.TYPE.RECEIVE,
//				new HashSet<Integer>(getCurrentThreadLockSet()),new HashSet<AbstractNode>(),new HashSet<AbstractNode>());

    	MessageNode newNode = new MessageNode(
				id,threadId,AbstractNode.TYPE.RECEIVE);
        
		if(debug)
		{
			System.out.print("is this used at all?");
		}
		mondata.addToTrace(newNode);

		MessageNode node = indexToDeterminNode.get(id);
		if(node!=null)
		{
			newNode.setDepNode(node);
		}
    }
    public synchronized static void notifyBefore(Object o, int iid, long threadId)
    {	    	
   	 	iid = System.identityHashCode(o);
    	int id = getObjectMem(iid);
    	
//		AbstractNode newNode = new AbstractNode(
//				id,threadId,AbstractNode.TYPE.SEND,
//				new HashSet<Integer>(getCurrentThreadLockSet()),new HashSet<AbstractNode>(),new HashSet<AbstractNode>());

    	MessageNode newNode = new MessageNode(
				id,threadId,AbstractNode.TYPE.SEND);
        
		if(debug)
		{
			System.out.print("is this used at all?");
		}
		mondata.addToTrace(newNode);
		
		indexToDeterminNode.put(id,newNode);
    }
    public synchronized static void notifyAllBefore(Object o, int iid, long threadId)
    {	 
   	 	iid = System.identityHashCode(o);
    	int id = getObjectMem(iid);
    	
//		AbstractNode newNode = new AbstractNode(
//				id,threadId,AbstractNode.TYPE.SEND,
//				new HashSet<Integer>(getCurrentThreadLockSet()),new HashSet<AbstractNode>(),new HashSet<AbstractNode>());
	
    	MessageNode newNode = new MessageNode(
				id,threadId,AbstractNode.TYPE.SEND);
        
		if(debug)
		{
			System.out.print("is this used at all?");
		}
		mondata.addToTrace(newNode);
		
		indexToDeterminNode.put(id,newNode);
    }
    /*private static HashSet<Integer> getCurrentThreadLockSet()
    {
    	HashSet<Integer> ls = locksets.get();
    	if(ls==null)
    	{
    		ls = new HashSet<Integer>();
    		locksets.set(ls);
    	}
    	return ls;
    }*/
    
    private static boolean isNotReentrant(int id)
    {
    	Integer num = lockCountsMap.get(id);
    	if(num==null)
    	{
    		lockCountsMap.put(id,1);
    		return true;
    	}
    	else
    	{
    		lockCountsMap.put(id,++num);
    		return false;
    	}
    }
    private static boolean isLastRelease(int id)
    {
    	Integer num = lockCountsMap.get(id);
    	assert(num!=null);
    	
    	if(num==1)
    	{
    		lockCountsMap.put(id,null);
    		return true;
    	}
    	else
    	{
    		lockCountsMap.put(id,--num);
    		return false;
    	}
    }
    
    public synchronized static void enterMonitorAfter(Object o,int iid,long threadId) 
    {
   	 	iid = System.identityHashCode(o);
   	 	int id = getObjectMem(iid);
   	 	
   	 	if(isNotReentrant(id))
   	 	{
//   	 		getCurrentThreadLockSet().add(id);
//    	
//			AbstractNode newNode = new AbstractNode(
//					id,threadId,AbstractNode.TYPE.LOCK,
//					new HashSet<Integer>(getCurrentThreadLockSet()),new HashSet<AbstractNode>(),new HashSet<AbstractNode>());
   	 	LockNode newNode = new LockNode(
					id,threadId,AbstractNode.TYPE.LOCK);
        
		if(debug)
		{
			System.out.println("LockNode newNode = new LockNode("+id+","+threadId+",AbstractNode.TYPE.LOCK);");
		}
		LockNode lockDepNode = lockDepMap.get(id);
		newNode.setDepNode(lockDepNode);
			mondata.addToTrace(newNode);
   	 	}
    }
    
    /**
     * Static invocation of a synchronized method 
     * @param iid -- the index of static monitor object
     * @param threadId
     */
    public synchronized static void enterMonitorAfter(int iid, long threadId) 
    {
    	int id = getObjectMem(iid);
    	
    	if(isNotReentrant(id))
    	{
//    		getCurrentThreadLockSet().add(id);
//    	
//			AbstractNode newNode = new AbstractNode(
//					id,threadId,AbstractNode.TYPE.LOCK,
//					new HashSet<Integer>(getCurrentThreadLockSet()),new HashSet<AbstractNode>(),new HashSet<AbstractNode>());
        	
			LockNode newNode = new LockNode(id,threadId,AbstractNode.TYPE.LOCK);
			
			if(debug)
			{
				System.out.println("LockNode newNode = new LockNode("+id+","+threadId+",AbstractNode.TYPE.LOCK);");
			}
			
			LockNode lockDepNode = lockDepMap.get(id);
			newNode.setDepNode(lockDepNode);
			
			mondata.addToTrace(newNode);
    	}
    }
    
    public synchronized static void exitMonitorBefore(Object o,int iid,long threadId) 
    {
   	 	iid = System.identityHashCode(o);
   	 	int id = getObjectMem(iid);
   	 	
   	 	if(isLastRelease(id))
   	 	{
//   	 		getCurrentThreadLockSet().remove(id);
//    	
//			AbstractNode newNode = new AbstractNode(
//					id,threadId,AbstractNode.TYPE.UNLOCK,
//					new HashSet<Integer>(getCurrentThreadLockSet()),new HashSet<AbstractNode>(),new HashSet<AbstractNode>());

   	 	LockNode newNode = new LockNode(
					id,threadId,AbstractNode.TYPE.UNLOCK);
		if(debug)
		{
			System.out.println("LockNode newNode = new LockNode("+id+","+threadId+",AbstractNode.TYPE.UNLOCK);");
		}
		lockDepMap.put(id,newNode);

			mondata.addToTrace(newNode);
   	 	}
    }
    
    /**
     * Exit of a static synchronized method
     * @param iid
     * @param threadId
     */
    public synchronized static void exitMonitorBefore(int iid,long threadId) 
    {
    	int id = getObjectMem(iid);
    	
    	if(isLastRelease(id))
    	{
//    		getCurrentThreadLockSet().remove(id);
//    	
//			AbstractNode newNode = new AbstractNode(
//					id,threadId,AbstractNode.TYPE.UNLOCK,
//					new HashSet<Integer>(getCurrentThreadLockSet()),new HashSet<AbstractNode>(),new HashSet<AbstractNode>());
		
    		LockNode newNode = new LockNode(id,threadId,AbstractNode.TYPE.UNLOCK);
    		if(debug)
			{
				System.out.print("LockNode newNode = new LockNode("+id+", " +threadId+",AbstractNode.TYPE.UNLOCK);");
			}
			lockDepMap.put(id,newNode);
			
			mondata.addToTrace(newNode);
    	}
    }
    
    
  
    
	/**
	 * ENTER AND EXIT OF A NON-PRIVATE METHOD
	 * @param threadId
	 */
    // for the topmost method, it is sometimes hard to telle the signature as line no is not there, BUT, we have it attached as an argument here:
    public synchronized static void enterNonPrivateMethodAfter(int methoId, long threadId, String msubsig) 
    {
    	if(debug)
 		{
 			System.out.println("enter: " + msubsig);
 		}
    //  ContextValueManager.invalidifyContextValueCache(Thread.currentThread());

      //System.out.println(msubsig);
      //
  //   	MethodNode newNode = new MethodNode(methoId,threadId,AbstractNode.TYPE.ENTRY,false, msubsig);
//    //	System.out.println("enter:" + newNode.getAppSTE().getmsig());
////    	if(threadId>35)
////		{
////    		System.out.println(threadId + " entering" + newNode.appSTE);
////		}
//		mondata.addToTrace(newNode);
    }
    
	/**
	 * ENTER AND EXIT OF A PRIVATE METHOD
	 * @param threadId
	 */
    public synchronized static void enterPrivateMethodAfter(int methoId, long threadId, String msubsig) 
    {
    	
    	if(debug)
 		{
 			System.out.println("enter: " + msubsig);
 		}
  //  	 ContextValueManager.invalidifyContextValueCache(Thread.currentThread());

//    	MethodNode newNode = new MethodNode(methoId,threadId,AbstractNode.TYPE.ENTRY,true, msubsig);
//  //  	System.out.println("enter:" + newNode.getAppSTE().getmsig());
////    	if(threadId>35)
////		{
////    		System.out.println(threadId + " entering" + newNode.appSTE);
////		}
//		mondata.addToTrace(newNode);
    }
    public synchronized static void exitNonPrivateMethodBefore(int methoId, long threadId, String msubsig) 
    {    			
    	if(debug)
 		{
 			System.out.println("exit: " + msubsig);
 		}
    //	ContextValueManager.invalidifyContextValueCache(Thread.currentThread()); // change context scope
    	

    	
    	
    	
//    	MethodNode newNode = new MethodNode(methoId,threadId,AbstractNode.TYPE.EXIT,false, msubsig);
//    //	System.out.println("exit:" + newNode.getAppSTE().getmsig());
////    	if(threadId>35)
////		{
////    		System.out.println(threadId + " exiting" + newNode.appSTE);
////		}
//		mondata.addToTrace(newNode);
    }
    

    public synchronized static void exitPrivateMethodBefore(int methoId, long threadId,String msubsig) 
    {
    	if(debug)
 		{
 			System.out.println("exit: " + msubsig);
 		}
    //	ContextValueManager.invalidifyContextValueCache(Thread.currentThread()); // change context scope

    			
//    	MethodNode newNode = new MethodNode(methoId,threadId,AbstractNode.TYPE.EXIT,true,msubsig);
//  //  	System.out.println("exit:" + newNode.getAppSTE().getmsig());
////    	if(threadId>35)
////		{
////    		System.out.println(threadId + " exiting" + newNode.appSTE);
////		}
//		mondata.addToTrace(newNode);
    }
    
    
    
//	if(msubsig.equals(methodStack.get(methodStack.size()-1)))
//	{
//		methodStack.remove(methodStack.size()-1);
//	}
//	else {
//		String peek =null;
//		while(!methodStack.isEmpty())
//    	{
//			peek = methodStack.get(methodStack.size()-1);
//			if(!msubsig.equals(peek))
//			{
//				methodStack.remove(methodStack.size()-1);
//			}
//			else {
//				break;
//			}
//			        		
//    	}
//		
//		peek = methodStack.get(methodStack.size()-1);
//		if(msubsig.equals(peek))
//		{
//			methodStack.remove(methodStack.size()-1);
//		}
//		else {
//			throw new RuntimeException("what is up"); // may ban it later..
//		}
//	}

    /**
     * This method is not used in the current implementation
     * But might be used in a later revision
     * @param o
     * @param iid
     * @param threadId
     */
    public synchronized static void enterSyncMethodBefore(Object o,int iid,long threadId) 
    {
    	 iid = System.identityHashCode(o);
    	 int id = getObjectMem(iid);
    	 
    	 if(isNotReentrant(id))
    	 {
//    		 getCurrentThreadLockSet().add(id);
//     	
//	 		 AbstractNode newNode = new AbstractNode(
//	 				id,threadId,AbstractNode.TYPE.LOCK,
//	 				new HashSet<Integer>(getCurrentThreadLockSet()),new HashSet<AbstractNode>(),new HashSet<AbstractNode>());
    	     	
    		 LockNode newNode = new LockNode(id,threadId,AbstractNode.TYPE.LOCK);
	 		if(debug)
	 		{
	 			System.out.println("LockNode newNode = new LockNode("+id+","+threadId+",AbstractNode.TYPE.LOCK);");
	 		}
    		 
 			LockNode lockDepNode = lockDepMap.get(id);
			newNode.setDepNode(lockDepNode);
			
	 		mondata.addToTrace(newNode);
    	 }
    }
    public synchronized static void exitSyncMethodAfter(Object o,int iid,long threadId) 
    {
    	iid = System.identityHashCode(o);
    	int id = getObjectMem(iid);
    	
    	if(isLastRelease(id))
    	{
//    		getCurrentThreadLockSet().remove(id);
//    	
//			AbstractNode newNode = new AbstractNode(
//					id,threadId,AbstractNode.TYPE.UNLOCK,
//	 				new HashSet<Integer>(getCurrentThreadLockSet()),new HashSet<AbstractNode>(),new HashSet<AbstractNode>());
//			
    		LockNode newNode = new LockNode(id,threadId,AbstractNode.TYPE.UNLOCK);
    		if(debug)
    		{
    			System.out.println("LockNode newNode = new LockNode("+id+","+threadId+",AbstractNode.TYPE.UNLOCK);");
    		}
			
			lockDepMap.put(id,newNode);

    		mondata.addToTrace(newNode);
    	}
    }
    /* Enter synchronized method before
     * This method is not used in the current implementation
     */
    public synchronized static void enterSyncMethodBefore(int iid,long threadId) 
    {
    	int id = getObjectMem(iid);
    	if(isNotReentrant(id))
    	{
//    		getCurrentThreadLockSet().add(id);
//    	
//			AbstractNode newNode = new AbstractNode(
//					id,threadId,AbstractNode.TYPE.LOCK,
//	 				new HashSet<Integer>(getCurrentThreadLockSet()),new HashSet<AbstractNode>(),new HashSet<AbstractNode>());
//			
    		LockNode newNode = new LockNode(id,threadId,AbstractNode.TYPE.LOCK);
    		if(debug)
    		{
               System.out.println("LockNode newNode = new LockNode("+id+","+threadId+",AbstractNode.TYPE.LOCK);");
    		}

		    	
			LockNode lockDepNode = lockDepMap.get(id);
			newNode.setDepNode(lockDepNode);
			
			mondata.addToTrace(newNode);
    	}
    }
    public synchronized static void exitSyncMethodAfter(int iid,long threadId) 
    {
    	int id = getObjectMem(iid);
    	
    	if(isLastRelease(id))
    	{
//    		getCurrentThreadLockSet().remove(id);
//    	
//			AbstractNode newNode = new AbstractNode(
//					id,threadId,AbstractNode.TYPE.UNLOCK,
//	 				new HashSet<Integer>(getCurrentThreadLockSet()),new HashSet<AbstractNode>(),new HashSet<AbstractNode>());

    		LockNode newNode = new LockNode(id,threadId,AbstractNode.TYPE.UNLOCK);
    		if(debug)
    		{
               System.out.println("LockNode newNode = new LockNode("+id+","+threadId+",AbstractNode.TYPE.UNLOCK);");
    		}
			lockDepMap.put(id,newNode);

			
    		mondata.addToTrace(newNode);
    	}
    }
	public synchronized static void mainThreadStartRun(long threadId,String methodName, String[] args)
	{
		
	}
	public synchronized static void mainThreadStartRun0(String classname, String[] args)
	{
		Thread mainThread = Thread.currentThread();
		long threadId = mainThread.getId();
		
		threadNameToIdMap.put(Parameters.MAIN_THREAD_NAME,threadId);//Thread.currentThread().getName()
		mainargs = args;
		methodname = classname+".main";
		
		int mem =getObjectMem(System.identityHashCode(mainThread));
		
		mondata = new MonitorData();
		mondata.setClassName(classname);
		
	}

	/**
	 * Read before an instance variable, but without line number information
	 * @param o
	 * @param iid
	 * @param threadId
	 */
//	public synchronized static void readBeforeInstance(Object o, int iid, long threadId)
//	{
//		RWNode newNode = new RWNode(
//				iid,threadId,AbstractNode.TYPE.READ);
//		
//		mondata.addToTrace(newNode); 
//		
//		//TODO: need branch node info
//		//indexToDeterminNode.get(iid).addNextNode(newNode);
//		
//		int defaultHashCode = System.identityHashCode(o);
//		mondata.addToSPEHashMap(defaultHashCode, iid, newNode);
//	}
//	public synchronized static void writeBeforeInstance(Object o, int iid, long threadId)
//	{
//		System.err.println("here");
//		
//		RWNode newNode = new RWNode(
//				iid,threadId,AbstractNode.TYPE.WRITE);
//		
//		mondata.addToTrace(newNode);
//		
//		//TODO: need branch node info
//		//indexToDeterminNode.get(iid).addNextNode(newNode);		
//		
//		int defaultHashCode = System.identityHashCode(o);
//		mondata.addToSPEHashMap(defaultHashCode, iid, newNode);
//	}

	// static fields have different different IDs from instances. 
	//different instances have different IDs!
	static int counter =0;
	//static HashMap<Object, Integer> o2rtID = new HashMap<Object, Integer>();
	static HashMap<Integer, HashMap<Object, Integer>> spe2obj2rtID = new HashMap<Integer, HashMap<Object, Integer>>();
	
	// if two things have different spes, they must have different IDs, 
	// for example, min_House, and min_Customer,
	// otherwise, in case their values are the same, they will wrongly form a violation.
	
	//at org.exolab.castor.xml.util.XMLFieldDescriptors.hashCode(XMLFieldDescriptors.java:194) 
	// the hashmap invokes the hashcode function, which again calls rtID... stackoverflow
	private static int rtID4each(Object o, int speid) { // null is a special object, merely..
		HashMap<Object, Integer> obj2rtID =spe2obj2rtID.get(speid);
		if(obj2rtID==null)
		{
			obj2rtID = new HashMap<Object, Integer>();
			spe2obj2rtID.put(speid,obj2rtID );
		}
		int  systemIdentityCode = System.identityHashCode(o);// definitly not instrumented.. not like hashcode..
		Integer tmp = (Integer)obj2rtID.get(systemIdentityCode); // o
		if(tmp ==null)
		{
			int tmpcounter = counter++;
			obj2rtID.put(systemIdentityCode, tmpcounter); // o can be null, but the spe key already takes effect to pick the right obj2rtID map.
			tmp = (Integer)obj2rtID.get(systemIdentityCode);
		}
		return tmp.intValue();
		
//		if(o ==null)
//		{
//			// static field: null + spe (spe important)
//			Integer tmp  = (Integer)spe2rtID.get(speid);
//			if(tmp ==null)
//			{
//				int tmpcounter = counter++;
//				spe2rtID.put(speid, tmpcounter);
//				tmp =  (Integer)spe2rtID.get(speid);
//			}
//			return tmp.intValue();
//		}
//		else {
//			// instance: obj + spe (obj important)
//			Integer tmp = (Integer)o2rtID.get(o);
//			if(tmp ==null)
//			{
//				int tmpcounter = counter++;
//				o2rtID.put(o, tmpcounter);
//				tmp = (Integer)o2rtID.get(o);
//			}
//			return tmp.intValue();			
//		}		
	}
	
	
	  static HashMap<Integer, String> indexSPEMap =null; 
	   private static String getName4RWNode(int spe)
	   {
		   if(indexSPEMap ==null)
		   {
			   ObjectInputStream in = null;
			    String filename = CommonUtil.getTmpTransDirectory()+"spe."+mondata.getClassName()+".gz";
			
				
			    try 
				{
					File file = new File(filename);
					
					if (filename.endsWith(".gz")) {
						in = new ObjectInputStream(new GZIPInputStream(
								new FileInputStream(file)));
					} else {
						in = new ObjectInputStream(new FileInputStream(file));
					}
					
					indexSPEMap = (HashMap<Integer, String>) CommonUtil.loadObject(in);
					
				}catch(Exception e)
				{
					e.printStackTrace();
				}finally
				{
					
				}
		   }
		   return indexSPEMap.get(spe);
		   
	   }
	   
	   
	
	public synchronized static void readBeforeInstance(Object o, int iid, long threadId, int line, String msig, String jcode)
	{
		//||jcode.contains("LPXZ_")
//		if(!PropertyManager.isInteresting(msig))
//			 return;

		int rtID4each = rtID4each(o, iid);//iid is spe, not precise, was replaced later in engineMain, we replace it now directly
	  
		RWNode newNode = new RWNode(line,rtID4each,threadId,AbstractNode.TYPE.READ);
		newNode.setNewMem(rtID4each);// set for the jeff's violation, I remove the invocation there.
		newNode.setMemString(getName4RWNode(iid));		
		newNode.setMsig(msig);
		newNode.setJcode(jcode+ rtID4each);
		
		if(debug)
		{
         //  System.out.println("RWNode newNode = new RWNode("+line+","+rtID4each+","+threadId+",AbstractNode.TYPE.READ);");
		}
		
		//<spec.jbb.ResFilter: boolean accept(java.io.File,java.lang.String)>
		 if(PropertyManager.useContext4InterestingMethod )
		 {
			 if(PropertyManager.isInteresting(msig))
				{
				// System.out.println("xxx");
				 // still too many!
				
					Vector contextVal=ContextValueManager.assignContextValue(Thread.currentThread(), msig);
					newNode.setContext_VectorEncoding(contextVal);
				}
		 }
		
		
		
		mondata.addToTrace(newNode);
		mondata.addRTid2Vector(rtID4each, newNode);
		
		//TODO: need branch node info
		//indexToDeterminNode.get(iid).addNextNode(newNode);
		
		
		//mondata.addToSPEHashMap(defaultHashCode, iid, newNode);
	}

	public synchronized static void writeBeforeInstance(Object o, int iid, long threadId, int line, String msig, String jcode)
	{

//		if(!PropertyManager.isInteresting(msig))
//			 return;
		int rtID4each = rtID4each(o, iid);
		RWNode newNode = new RWNode(line,rtID4each,threadId,AbstractNode.TYPE.WRITE);//MAKE THIS NULL TEMPORARILY
		newNode.setNewMem(rtID4each);// set for the jeff's violation, I remove the invocation there.
		newNode.setMemString(getName4RWNode(iid));
		newNode.setMsig(msig);
		newNode.setJcode(jcode+ rtID4each);
		
		
		if(debug)
		{
        //   System.out.println("RWNode newNode = new RWNode("+line+","+rtID4each+","+threadId+",AbstractNode.TYPE.WRITE)");
		}
		
		 if(PropertyManager.useContext4InterestingMethod )
		 {
			 if(PropertyManager.isInteresting(msig))
				{
				 Vector contextVal=ContextValueManager.assignContextValue(Thread.currentThread(), msig);
					newNode.setContext_VectorEncoding(contextVal);
				}
		 }
		
		
		mondata.addToTrace(newNode);
		mondata.addRTid2Vector(rtID4each, newNode);
		
		//TODO: need branch node info
		//indexToDeterminNode.get(iid).addNextNode(newNode);
		
//		int defaultHashCode = System.identityHashCode(o);
		//mondata.addToSPEHashMap(defaultHashCode, iid, newNode);
	}

    public synchronized static void readBeforeStatic(int iid,long threadId, int line, String msig, String jcode) {
//    	if(!PropertyManager.isInteresting(msig))
//			 return;

    	int rtID4each = rtID4each(null, iid);
    	RWNode newNode = new RWNode(line,rtID4each,threadId,AbstractNode.TYPE.READ);
    	newNode.setNewMem(rtID4each);// set for the jeff's violation, I remove the invocation there.
    	newNode.setMemString(getName4RWNode(iid));
    	newNode.setMsig(msig);
    	newNode.setJcode(jcode+ rtID4each);
		
		
		if(debug)
		{
        //   System.out.println("RWNode newNode = new RWNode("+line+","+rtID4each+","+threadId+",AbstractNode.TYPE.READ);");
		}
		
		 if(PropertyManager.useContext4InterestingMethod )
		 {
			 if(PropertyManager.isInteresting(msig))
				{
				 Vector contextVal=ContextValueManager.assignContextValue(Thread.currentThread(), msig);
					newNode.setContext_VectorEncoding(contextVal);
				}
		 }
		
		
		mondata.addToTrace(newNode);
		mondata.addRTid2Vector(rtID4each, newNode);
		//TODO: need branch node info
		//indexToDeterminNode.get(iid).addNextNode(newNode);
		
	//	mondata.addToSPEHashMap(0, iid, newNode);
    }
    public synchronized static void writeBeforeStatic(int iid,long threadId, int line, String msig, String jcode) 
    {
//    	 if(!PropertyManager.isInteresting(msig))
//			 return;

    	
    	int rtID4each = rtID4each(null, iid);
    	RWNode newNode = new RWNode(line,rtID4each,threadId,AbstractNode.TYPE.WRITE);//MAKE THIS NULL TEMPORARILY
    	newNode.setNewMem(rtID4each);// set for the jeff's violation, I remove the invocation there.
    	newNode.setMemString(getName4RWNode(iid));
    	newNode.setMsig(msig);
    	newNode.setJcode(jcode+ rtID4each);
		if(debug)
		{
         //  System.out.println("RWNode newNode = new RWNode("+line+","+rtID4each+","+threadId+",AbstractNode.TYPE.WRITE);");
		}
		
		if(PropertyManager.useContext4InterestingMethod )
		 {
			 if(PropertyManager.isInteresting(msig))
				{
				   Vector contextVal=ContextValueManager.assignContextValue(Thread.currentThread(), msig);
					newNode.setContext_VectorEncoding(contextVal);
				}
		 }
		
		
		mondata.addToTrace(newNode);	
		mondata.addRTid2Vector(rtID4each, newNode);
		//TODO: need branch node info
		//indexToDeterminNode.get(iid).addNextNode(newNode);

		//mondata.addToSPEHashMap(0, iid, newNode);

    }
	public synchronized static void accessSPE(int index,long threadId) {
	}
}
