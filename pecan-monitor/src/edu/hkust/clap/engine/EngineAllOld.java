package edu.hkust.clap.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import edu.hkust.clap.Parameters;
import edu.hkust.clap.Util;
import edu.hkust.clap.datastructure.AbstractNode;
import edu.hkust.clap.datastructure.AbstractNode.TYPE;
import edu.hkust.clap.monitor.Monitor;
import edu.hkust.clap.monitor.MonitorData;
/**
 * 
 * @author Jeff
 *	All methods are atomic
 */
public class EngineAllOld extends Thread
{
	private static boolean checkRace = true;
	private static boolean checkAtomicity = true;
	private static boolean checkAtomSet = true;	
	private static boolean transform_ = true;
	
	private static FileWriter fw;
	
	private static String tempDir;
	private static String tidDir;
	private static String lineDir;
	
	private static Vector<AbstractNode>[] accessvec;
	private static int transPatternNumber;
	private static int realPatternNumber;
	private static int totalPatternNumber;
	
	private static int numberOfSharedVariables;
	private static int numberOfThreads;
	private static int numberOfMessageNodes;
	private static int numberOfLockNodes;
	private static int numberOfReadWriteNodes;
	private static int numberOfTotalNodes;
	private static int numberOfTotalNonMethodEntryExitNodes;
	private static int numberOfPatternI;
	private static int numberOfPatternII;
	
	private static int numberOfRacePair;
	private static int numberOfTypeOnePattern;
	private static int numberOfTypeTwoPattern;
	private static int numberOfTypeThreePattern;
	private static int numberOfTypeFourPattern;
	private static int numberOfTypeFivePattern;
	
	private static ObjectOutputStream out;

	private static LinkedList<HashMap<Long,ThreadNodes>> acclist;
	private static HashSet<Pattern> patterns = new HashSet<Pattern>();
	private static HashMap<AbstractNode,HashSet<AbstractNode>> racemap = new HashMap<AbstractNode,HashSet<AbstractNode>>();
 	private static HashSet<PatternRace> racepatterns = new HashSet<PatternRace>();
 	
	private static HashMap<Pattern,AbstractNode> patternsToNodes = new HashMap<Pattern,AbstractNode>();
	private static HashSet<TranformNodePair> transformedpairs = new HashSet<TranformNodePair>();
	private static HashMap<Long,Vector<AbstractNode>> threadNodes = new HashMap<Long,Vector<AbstractNode>>();
	private static HashMap<Long,HashMap<Integer,LinkedList<AbstractNode>>> threadAtomAccessList = new HashMap<Long,HashMap<Integer,LinkedList<AbstractNode>>>();
	private static LinkedList<Long> threadIDList =  new LinkedList<Long>();
	
	private static HashMap<Long,Set<Integer>> threadLockSet = new HashMap<Long,Set<Integer>>();
	private static HashMap<Long,Integer> threadAtomIndex = new HashMap<Long,Integer>();
	private static HashMap<Long,Vector<AbstractNode>> threadAtomNodes = new HashMap<Long,Vector<AbstractNode>>();

	private static HashMap<Long,LockHistory> threadLockHistory = new HashMap<Long,LockHistory>();
	
	/**
	 * The next message passing node of the current node from the same thread
	 */
	private static HashMap<Integer,Integer> determinNodeMap = new HashMap<Integer,Integer>();
	/**
	 * The last message passing node of the current node from the same thread
	 */
	private static HashMap<Integer,Integer> determindNodeMap = new HashMap<Integer,Integer>();
	
	private static PartialOrderRelation por;
	private static Vector<AbstractNode> maintrace;

	EngineAllOld()
	{
		super("MonitorThread");
	}
	public void run()
	{
		saveMonitorData();
		//Monitor.generateTestDriver(Monitor.saveMonitorData());
	}
    private static ObjectOutputStream getOutputStream(String filename)
    {
    	ObjectOutputStream out = null;
    	try
    	{	
			String path = filename;
			File f = new File(path);
			FileOutputStream fos = new FileOutputStream(f);
			out = new ObjectOutputStream(fos);
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		return null;
    	}
    	return out;
    }
    private static String getTempDir()
    {
    	if(tempDir == null)
    	{
			String userdir = System.getProperty("user.dir");//user.dir
			if (!(userdir.endsWith("/") || userdir.endsWith("\\"))) {
				userdir = userdir + System.getProperty("file.separator");
			}
			tempDir = userdir+"tmp"+System.getProperty("file.separator");
			
			File tempFile = new File(tempDir);
			
			if(!tempFile.exists())
			{
				tempFile.mkdir();
			}
			else
			{			
				deleteFile(tempFile);
			}
    	}
		return tempDir;
    }
    private static String getScheduleTidDir()
    {
    	if(tidDir == null)
    	{

			tidDir = getTempDir()+"schedule_tid";
			
			File tempFile = new File(tidDir);
			
			if(!tempFile.exists())
			{
				tempFile.mkdir();
			}
			else
			{			
				deleteFile(tempFile);
			}
    	}
		return tidDir;
    }
    private static String getScheduleLineDir()
    {
    	if(lineDir == null)
    	{

    		lineDir = getTempDir()+"schedule_line";
			
			File tempFile = new File(lineDir);
			
			if(!tempFile.exists())
			{
				tempFile.mkdir();
			}
			else
			{			
				deleteFile(tempFile);
			}
    	}
		return lineDir;
    }
	private static void deleteFile(File f)
	{
		// Get all files in directory
		File[] files = f.listFiles();
		for (File file : files)
		{
		   // Delete each file

		   if (!file.delete())
		   {
		       // Failed to delete file
		       //System.out.println("Failed to delete "+file);
		   }
		}
	}
    private void saveMonitorData()
    {
    	ObjectOutputStream out = null;
		try
		{
			out = getOutputStream("trace");
    		out.writeObject(Monitor.mondata);
		}catch(Exception e)
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
    
    public static void saveProcessMonitorData(MonitorData mondata)
    {
    	ObjectOutputStream out = null;
    	
    	try
    	{				
    		String path = System.getProperty("user.dir");
    		path = path+System.getProperty("file.separator")+"tmp"+System.getProperty("file.separator")+"trace";
    		File f = new File(path);
    		f.delete();
    		
			FileOutputStream fos = new FileOutputStream(f);
			out = new ObjectOutputStream(fos);
			out.writeObject(mondata);
    	}catch(Exception e)
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
    
    public static Object getMonitorData()
    {
    	Object obj = null;
    	try {
			String path = System.getProperty("user.dir");
			path = path+System.getProperty("file.separator")+"tmp"+System.getProperty("file.separator")+"trace";
			File traceFile = new File(path);
			FileInputStream fis = new FileInputStream(traceFile);
			ObjectInputStream in = new ObjectInputStream(fis);
		
			obj = in.readObject();

		} catch (IOException e) {
			e.printStackTrace();
		}finally
		{
			return obj;
		}
    }

    private static void intializeLockHistory() {

    	Iterator<Long> threadIt = threadNodes.keySet().iterator();
    	while(threadIt.hasNext())
    	{
    		Long threadId = threadIt.next();   		
    		threadLockHistory.put(threadId, new LockHistory(threadNodes.get(threadId)));
    	}
	}

    /*
	  * processMonitorData()
	  * 1. compute pre-statics
	  * 2. get instance-accessvec
	  * 3. compute acclist
	  * 4. assign line number
	  */
	 private static void processMonitorData(MonitorData mondata)
	 {
		 maintrace = mondata.getTrace();
		 preProcessMonitordata();
			 
		 accessvec = getInstanceAccessVec(mondata.getSPEHashMap());
		 
		 removeImmutableAccess();
		 removeSingleThreadAccess();
		 
		 acclist = getAccList(accessvec);
		 threadAtomAccessList = getThreadAtomAccessList(acclist);
		 assignLinNum(mondata.getClassName());
		 
		 if(checkAtomSet)
			 intializeLockHistory();
	 }
	 private static HashMap<Long, HashMap<Integer, LinkedList<AbstractNode>>> getThreadAtomAccessList(
			LinkedList<HashMap<Long, ThreadNodes>> acclist) 
	{
			HashMap<Long,HashMap<Integer,LinkedList<AbstractNode>>> threadAtomAccessList = new HashMap<Long,HashMap<Integer,LinkedList<AbstractNode>>>();
			HashMap<Long,HashMap<Integer,Integer>> threadAtomIndexMap = new HashMap<Long,HashMap<Integer,Integer>>();
			for(int mem=0; mem<acclist.size();mem++)
	    	{
	    		HashMap<Long,ThreadNodes> map = acclist.get(mem);
	    		Set<Long> mapkeyset = map.keySet();
	    		//if(mapkeyset.size()>1)
	    		{
	    			Iterator<Long> mapIt = mapkeyset.iterator();
	    			while(mapIt.hasNext())
	    			{
	    				Long tid = mapIt.next();
	    				HashMap<Integer,LinkedList<AbstractNode>> atomAccessMap = threadAtomAccessList.get(tid);
	    				if(atomAccessMap==null)
	    				{
	    					atomAccessMap= new HashMap<Integer,LinkedList<AbstractNode>>();
	    					threadAtomAccessList.put(tid,atomAccessMap);
	    				}
	    				LinkedList<AbstractNode> listnodes = map.get(tid).getAllWRNodes();
	    				atomAccessMap.put(mem,listnodes);
	    			}
	    		}
	    	}
		return threadAtomAccessList;
	}
	private static LinkedList<HashMap<Long,ThreadNodes>> getAccList(Vector<AbstractNode>[] accessvec)
	 {
		 LinkedList<HashMap<Long,ThreadNodes>> acclist = new LinkedList<HashMap<Long,ThreadNodes>>();
		 
		for(int i=0;i<accessvec.length;i++)
		{
			HashMap<Long,ThreadNodes> map = new HashMap<Long,ThreadNodes>();
			int size = accessvec[i].size();
			if(size>0)
			{
				acclist.add(map);
				for(int j=0;j<size;j++)
				{
					AbstractNode node = accessvec[i].get(j);
					long tid = node.getTId();
					if(map.get(tid)==null)
					{
						map.put(tid, new ThreadNodes());
					}
					
					map.get(tid).addrwnode(node);
					if(node.getType()==TYPE.READ)
					{	
						map.get(tid).addreadnode(node);
					}
					else if(node.getType()==TYPE.WRITE)
					{					
						map.get(tid).addwritenode(node);
					}
				}
			}
		}
		return acclist;
	 }
	 private static Vector<AbstractNode>[] getInstanceAccessVec(HashMap<Integer, HashMap<Integer, Vector<AbstractNode>>> speHashMap)
	 {
		 int instancespesize = 0;
		 
		 Iterator<Integer> speIt = speHashMap.keySet().iterator();
		 while(speIt.hasNext())
		 {
			int spe = speIt.next();
			instancespesize+=speHashMap.get(spe).values().size();
		 }
		 
		 Vector<AbstractNode>[] instanceaccessvec = new Vector[instancespesize];
		 int index =0;
		 
		 Iterator<Integer> speIt2 = speHashMap.keySet().iterator();
		 while(speIt2.hasNext())
		 {
			int spe = speIt2.next();
			Iterator<Vector<AbstractNode>> valueIt = speHashMap.get(spe).values().iterator();
			while(valueIt.hasNext())
			{				
				instanceaccessvec[index] = valueIt.next();
				int size = instanceaccessvec[index].size();
				numberOfReadWriteNodes+=size; 
				for(int k=0;k<size;k++)
					instanceaccessvec[index].get(k).setNewMem(index);
				
				index++;
			}
		 }
		 return instanceaccessvec;
	 }

    private static void assignLinNum(String classname)
    {
    	ObjectInputStream in = null;
	    String filename = Util.getTmpTransDirectory()+"spe."+classname+".gz";
		try {
			File file = new File(filename);
			
			if (filename.endsWith(".gz")) {
				in = new ObjectInputStream(new GZIPInputStream(
						new FileInputStream(file)));
			} else {
				in = new ObjectInputStream(new FileInputStream(file));
			}
			
			HashMap<Integer,String> indexSPEMap = (HashMap<Integer,String>) Util.loadObject(in);
			
			for(int i=0;i<accessvec.length;i++)
			{
				for(int j=0;j<accessvec[i].size();j++)
				{
					AbstractNode node = accessvec[i].get(j);
					node.setMemString(indexSPEMap.get(node.getMem()));
				}
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			
		}
    }
    private static void removeImmutableAccess()
    {
    	HashSet set = new HashSet();
    	for(int i=0;i<accessvec.length;i++)
		{
			for(int j=0;j<accessvec[i].size();j++)
			{
				AbstractNode node = accessvec[i].get(j);
				if(node.getType()==TYPE.WRITE)
				{					
					set.add(i);
					break;
				}
			}
		}
    	for(int i=0;i<accessvec.length;i++)
		{
    		if(!set.contains(i))
    			accessvec[i].clear();
		}
    	
    }
    private static void removeSingleThreadAccess()
    {
    	HashSet set = new HashSet();
    	for(int i=0;i<accessvec.length;i++)
		{
    		HashSet tset = new HashSet();
			for(int j=0;j<accessvec[i].size();j++)
			{
				AbstractNode node = accessvec[i].get(j);
				tset.add(node.getTId());
				if(tset.size()>1)
				{
					numberOfSharedVariables++;
					set.add(i);
					break;	
				}
			}
		}
    	for(int i=0;i<accessvec.length;i++)
		{
    		if(!set.contains(i))
    			accessvec[i].clear();
		}
    	
    }
    
    public static void main(String[] args)
    {
    	long start_time = System.currentTimeMillis();
    	MonitorData mondata = (MonitorData)getMonitorData();
    	
    	processMonitorData(mondata);
    	
    	try
    	{
    		fw = new FileWriter(new File(getTempDir()+mondata.getClassName()+".txt"));
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	long detect_start_time = System.currentTimeMillis();
    	findPatterns();
    	long detect_end_time = System.currentTimeMillis();
    	    
    	showRacePattern();
    	
    	showTransformedPattern();
    	
    	long tranform_start_time = System.currentTimeMillis();
    	
    	if(transform_)transform();
    	
    	transPatternNumber = patternsToNodes.size()+numberOfRacePair;
    	
    	long tranform_end_time = System.currentTimeMillis();

    	computePostStatistics();
    	
    	long end_time = System.currentTimeMillis();
    	long total_time = (end_time - start_time);
    	
    	long detect_total_time = (detect_end_time - detect_start_time);
    	long tranform_total_time = (tranform_end_time - tranform_start_time);
    	
    	totalPatternNumber = realPatternNumber+transPatternNumber;
    	
    	print("\n-------------------------------------\n");
    	print("Number of Threads: "+numberOfThreads);
    	print("Number of Shared Variables: "+numberOfSharedVariables);
    	print("Number of Lock Nodes: "+numberOfLockNodes);
    	print("Number of Message Nodes: "+numberOfMessageNodes);
    	print("Number of Non-Method Entry/Exit Nodes: "+numberOfTotalNonMethodEntryExitNodes);
    	print("Number of Read/Write Nodes: "+numberOfReadWriteNodes);
    	print("Number of Total Nodes: "+numberOfTotalNodes+"\n");
    	
    	print("Number of Races: "+numberOfRacePair);
//    	print("Number of Type One Patterns: "+numberOfTypeOnePattern);
//    	print("Number of Type Two Patterns: "+numberOfTypeTwoPattern);
//    	print("Number of Type Three Patterns: "+numberOfTypeThreePattern);
//    	print("Number of Type Four Patterns: "+numberOfTypeFourPattern);
//    	print("Number of Type Five Patterns: "+numberOfTypeFivePattern);
    	print("Number of Atomicity Violations: "+numberOfPatternI);
    	print("Number of ASVs: "+numberOfPatternII);
    	print("Number of Real Violations: "+realPatternNumber);
    	print("Number of Predicted Violations: "+transPatternNumber);
    	print("Number of Total Violations: "+totalPatternNumber+"\n");
    	
    	print("Total Pattern Search Time: "+detect_total_time+" ms");
    	print("Total Schedule Generation Time: "+tranform_total_time+" ms");
    	print("Total Processing Time: "+total_time+" ms");
    	
    	try
    	{
    		fw.close();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    private static void showRacePattern() {

    	numberOfRacePair = racepatterns.size();
    	Iterator<PatternRace> patternsIt = racepatterns.iterator();
    	while(patternsIt.hasNext())
    	{
    		Pattern p = patternsIt.next();
			print("\n"+p.toString());
    	}		
	}
	private static void preProcessMonitordata()
    {
    	numberOfTotalNodes = maintrace.size();
    	int nonMethodEntryExitNodes_index =0;
    	por = new PartialOrderRelation();
    	
    	HashMap<Long,Integer> threadIdIndex = new HashMap<Long,Integer>();

    	
    	int k=0;
    	while(maintrace.size()>k)
    	{
    		AbstractNode node = maintrace.get(k);
    		long threadId = node.getTId();
    		Integer atomIndex = threadAtomIndex.get(threadId);
    		if(atomIndex==null)
    		{
    			atomIndex=0;
    			threadAtomIndex.put(threadId, 0);
    			threadIDList.add(threadId);
    		}
    		if(node.getType() == TYPE.ENTRY || node.getType() == TYPE.EXIT)
    		{
    			maintrace.remove(k);
    			
    			k--;
    			/*
    			Vector<AbstractNode> vecatom = threadAtomNodes.get(threadId);
        		if(vecatom==null)
        		{
        			vecatom = new Vector<AbstractNode>();
        			threadAtomNodes.put(threadId,vecatom);
        		}
        		if(node.getType()==TYPE.ENTRY)
        		{
        			vecatom.add(node);
        		}
        		else
        		{
        			if(vecatom.size()>0)
        				vecatom.remove(vecatom.size()-1);
        		}
        		if(vecatom.isEmpty())
        			threadAtomIndex.put(threadId, ++atomIndex);
        		*/
    		}
    		else
    		{
        		node.setID(nonMethodEntryExitNodes_index++);
        		
    			Vector<AbstractNode> vecatom = threadAtomNodes.get(threadId);
        		if(vecatom==null)
        		{
        			vecatom = new Vector<AbstractNode>();
        			threadAtomNodes.put(threadId,vecatom);
        		}

        		if(node.getType() == TYPE.LOCK)
        		{
            		vecatom.add(node);
        		}
        		else if (node.getType() == TYPE.UNLOCK)
        		{
        			if(vecatom.size()>0)
        			vecatom.remove(vecatom.size()-1);
        			
            		if(vecatom.isEmpty())
            		{
            			node.setAtomIndex(atomIndex);
            			threadAtomIndex.put(threadId, ++atomIndex);
            		}
        		}
        		/*
        		if (node.getType() == TYPE.SEND || node.getType() == TYPE.RECEIVE || node.getType() == TYPE.NA)
        		{
        			threadAtomIndex.put(threadId, ++atomIndex);
        		}
        		
        		
        		node.setAtomIndex(atomIndex);
        		*/
        		
        		if (node.getType() == TYPE.SEND || node.getType() == TYPE.RECEIVE)
        		{
        			threadAtomIndex.put(threadId, ++atomIndex);
        		}
        		
        		if(!vecatom.isEmpty())
        			node.setAtomIndex(atomIndex);
        		
    		}
    		
    		k++;
    	}	
    	
    	numberOfTotalNonMethodEntryExitNodes = nonMethodEntryExitNodes_index;
    	Integer t_index = 0;
    	determindNodeMap.put(0, 0);
    	
    	for(k=0;k<numberOfTotalNonMethodEntryExitNodes;k++)
    	{	
    		AbstractNode node = maintrace.get(k);
    		long threadId = node.getTId();
    		Vector<AbstractNode> vec = threadNodes.get(threadId);
    		if(vec==null)
    		{
    			vec = new Vector<AbstractNode>();
    			threadNodes.put(threadId,vec);
    		}
    		Set<Integer> lockset = threadLockSet.get(threadId);
    		if(lockset==null)
    		{
    			lockset = new HashSet<Integer>();
    			threadLockSet.put(threadId, lockset);
    		}
    		
    		vec.add(node);
    		
    		if(node.getType() == TYPE.LOCK || node.getType() == TYPE.UNLOCK)
    		{
    			numberOfLockNodes++;
    			if(node.getType() == TYPE.LOCK)
    			{
    				lockset.add(node.getMem());
    			}
    			else
    			{
    				lockset.remove(node.getMem());
    			}
    		}
    		else if(node.getType() == TYPE.SEND || node.getType() == TYPE.RECEIVE || node.getType() == TYPE.NA)
    		{
    			numberOfMessageNodes++;    			
    			
    			Set<AbstractNode> outNodes = node.getOutNodes();
    			if(outNodes!=null&&!outNodes.isEmpty())
    			{
    				Iterator<AbstractNode> nodesIt = outNodes.iterator();
	    			while(nodesIt.hasNext())
	    			{
	    				AbstractNode nextNode = nodesIt.next();
	    				por.addMultiThreadOrder(k,nextNode.getID());
	    			}
	    			t_index++;
	    			determindNodeMap.put(t_index, k);
    			}
    			
    			Set<AbstractNode> inNodes = node.getInNodes();
    			if(inNodes!=null&&!inNodes.isEmpty())
    			{
    				Iterator<AbstractNode> nodesIt = inNodes.iterator();
	    			while(nodesIt.hasNext())
	    			{
	    				AbstractNode nextNode = nodesIt.next();
	    				por.addMultiThreadOrder(nextNode.getID(),k);
	    			}
	    			t_index++;
	    			determindNodeMap.put(t_index, k);
    			}   			
    			
    			Integer lastIndex = threadIdIndex.get(threadId);
    			if(lastIndex!=null)
    			{
    				por.addSingleThreadOrder(lastIndex,k);
        			determinNodeMap.put(lastIndex, k);
    			}
    			threadIdIndex.put(threadId,k);
    		}
    		node.setLockSet(new HashSet<Integer>(lockset));
    		node.setTIndex(t_index);

    	}
    	
    	por.computePartialOrder();
    	
    	numberOfThreads = threadNodes.keySet().size();    	
    }
    
	private static void computePostStatistics() {
		
		Iterator<Pattern> patternsIt = patterns.iterator();
		while(patternsIt.hasNext())
		{
			Pattern p = patternsIt.next();
			if(p instanceof PatternI)
			{
				numberOfPatternI++;
				if(p instanceof TypeOnePattern)
				{
					numberOfTypeOnePattern++;
				}
				else
					numberOfTypeTwoPattern++;
			}
			else 
			{
				numberOfPatternII++;
				if(p instanceof TypeThreePattern)
				{
					numberOfTypeThreePattern++;	
				}
				else if(p instanceof TypeFourPattern)
				{
					numberOfTypeFourPattern++;
				}
				else
					numberOfTypeFivePattern++;
				
			}	
		}
	}
	private static void print(String str)
    {
    	System.out.println(str);
    	try 
		{
			fw.write(str+"\n");
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
    }
	private static void printerr(String str)
    {
    	System.err.println(str);
    	System.out.println("--------------------------------------");
    	try 
		{
			fw.write(str+"\n");
			fw.write("--------------------------------------"+"\n");
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
    }
	/*
	 * Find five types of patterns
	 * First use partial order constraints
	 * Then use locking constraints
	 */
    private static void findPatterns() {
    	
    	printerr("\n*** Violation Patterns [ID - Memory Location - Line Number - Thread - Access Type] ***\n");
    	    	    	
    	for(int i=0; i<acclist.size();i++)
    	{
    		HashMap<Long,ThreadNodes> map = acclist.get(i);
    		Set<Long> mapkeyset = map.keySet();
    		if(mapkeyset.size()>1)
    		{
    			Iterator<Long> mapIt = mapkeyset.iterator();
    			while(mapIt.hasNext())
    			{
    				Long tid = mapIt.next();
    				
    				if(checkRace)
    				{
    					findRace(map,tid);
    				}
    				
    				if(checkAtomicity)
    				{
	    				findAtomicityPatternOne(map,tid);
	    				findAtomicityPatternTwo(map,tid);
    				}
    			}
    		}
    	}
    	
		if(checkAtomSet)
		{
        	findAtomSetPatterns();
		}
    	
	}
	/**
	 * Two reads by one thread on sides and two writes in the middle by another thread
	 * @param acclist
	 * @param i
	 * @param map
	 */
	private static void findAtomSetPatternFive(LinkedList<HashMap<Long, ThreadNodes>> acclist, int i,
			HashMap<Long, ThreadNodes> map) 
	{		
	}
	/**
	 * Two writes by one thread on sides and two reads in the middle by another thread
	 * @param acclist
	 * @param i
	 * @param map
	 */
	private static void findAtomSetPatternFour(LinkedList<HashMap<Long, ThreadNodes>> acclist, int i,
			HashMap<Long, ThreadNodes> map) 
	{
		
	}
	/**
	 * Four writes on two different shared variables by two threads
	 * @param acclist
	 * @param i
	 * @param map
	 */
	private static void findAtomSetPatterns() 
	{
		for(int i=0;i<threadIDList.size()-1;i++)
		{
			Long tid1= threadIDList.get(i);
			HashMap<Integer,LinkedList<AbstractNode>> accessmap1 = threadAtomAccessList.get(tid1);

			if(accessmap1.keySet().size()>1)
			{
				HashSet<Integer> memSet = new HashSet<Integer>(accessmap1.keySet());
				
				for(int j=i+1;j<threadIDList.size();j++)
				{
					Long tid2= threadIDList.get(j);
					HashMap<Integer,LinkedList<AbstractNode>> accessmap2 = threadAtomAccessList.get(tid2);
					
					memSet.retainAll(accessmap2.keySet());
					
					if(memSet.size()>1)
					{
						Integer[] memarray = new Integer[memSet.size()];
						Iterator<Integer> memSetIt= memSet.iterator();
						int memPos=0;
						while(memSetIt.hasNext())
						{
							memarray[memPos++]=memSetIt.next();
						}
						
						for(int m1index = 0; m1index<memarray.length-1;m1index++)
							for(int m2index=m1index+1;m2index<memarray.length;m2index++)
							{
								Integer mem1 = memarray[m1index];
								Integer mem2 = memarray[m2index];
								
								searchAtomSetPatterns(accessmap1.get(mem1),accessmap1.get(mem2),accessmap2.get(mem1),accessmap2.get(mem2));
							}
					}
				

				}
			}
		}
	}
			
	private static void searchAtomSetPatterns(
			LinkedList<AbstractNode> linkedListI,
			LinkedList<AbstractNode> linkedListJ,
			LinkedList<AbstractNode> linkedListK,
			LinkedList<AbstractNode> linkedListR) 
	{
		for(int i=0;i<linkedListI.size();i++)
		{
			for(int j=0;j<linkedListJ.size();j++)
			{		
				for(int r=0;r<linkedListR.size();r++)
				{
					for(int k=0;k<linkedListK.size();k++)
					{
						
						AbstractNode nodeI = linkedListI.get(i);
						AbstractNode nodeJ = linkedListJ.get(j);
						AbstractNode nodeK = linkedListK.get(k);
						AbstractNode nodeR = linkedListR.get(r);
						
				        if(nodeI.getType()==TYPE.WRITE&&nodeJ.getType()==TYPE.WRITE&&nodeK.getType()==TYPE.WRITE&&nodeR.getType()==TYPE.WRITE
				        		|| nodeI.getType()==TYPE.WRITE&&nodeJ.getType()==TYPE.WRITE&&nodeK.getType()==TYPE.READ&&nodeR.getType()==TYPE.READ
				        		||nodeI.getType()==TYPE.READ&&nodeJ.getType()==TYPE.READ&&nodeK.getType()==TYPE.WRITE&&nodeR.getType()==TYPE.WRITE)
				        {
						
						int I = nodeI.getID();
						int J = nodeJ.getID();
						int K = nodeK.getID();
						int R = nodeR.getID();
						
						if(I>J)
						{
							int tempIndex = I;
							I = J;
							J = tempIndex;
							
							AbstractNode tempNode = nodeI;
							nodeI = nodeJ;
							nodeJ = tempNode;
						}
						
						if(K>R)
						{
							int tempIndex = K;
							K = R;
							R = tempIndex;
							
							AbstractNode tempNode = nodeK;
							nodeK = nodeR;
							nodeR = tempNode;
						}
						
						if(K<I)
						{
							int tempIndexI = I;
							int tempIndexJ = J;
							I = K;
							J = R;
							K = tempIndexI;
							R = tempIndexJ;
							
							AbstractNode tempNodeI = nodeI;
							AbstractNode tempNodeJ = nodeJ;
							nodeI = nodeK;
							nodeJ = nodeR;
							nodeK = tempNodeI;
							nodeR = tempNodeJ;
						}
						if(canNotReach(nodeJ,nodeK))
						{
	
						Pattern p = new PatternII(nodeI,nodeK,nodeR,nodeJ);
						
						if(!isRedundantPattern(p))
						{							    	    				
		    				
							if(I<K&&R<J)
		    				{
		    	    			showRealPattern(p);
		    				}
		    				else 
		    				{		
		    					if(nodeI.getLockSet()!=null&&nodeJ.getLockSet()!=null&&nodeK.getLockSet()!=null&&nodeR.getLockSet()!=null)
	    						{
		    					//CHECK LOCKING CONSTRAINTS	
		    					
		    					Set<Integer> lockhistory  = threadLockHistory.get(nodeK.getTId()).getLockHistory(nodeK, nodeR);
		    					
		    					Set<Integer> locks = new HashSet<Integer>(lockhistory);
	    						locks.retainAll(nodeI.getLockSet());
	    						if(locks.size()==0)
		    					{
	    							//PATTERN SATISFIED
    		    					savePattern(p,nodeI);
		    					}
	    						else
	    						{
    		    					Vector<AbstractNode> ijnodes = threadNodes.get(nodeI.getTId());

    		    					locks = new HashSet<Integer>(lockhistory);
		    						locks.retainAll(nodeJ.getLockSet());
		    						if(locks.size()==0)
    		    					{
		    							//PATTERN SATISFIED
        		    					savePattern(p,ijnodes.get(ijnodes.indexOf(nodeJ)-1));
    		    					}
		    						else
		    						{	    		    						
		    							//CHECK THREE CASES
		    							
	    		    					int iindex = ijnodes.indexOf(nodeI)+2;
	    		    					int jindex = ijnodes.indexOf(nodeJ)-2;
	    		    					if(iindex<=jindex)
	    		    					{
	    		    					
		    		    					int index = (iindex+jindex)/2;
		    		    					//for(int index = iindex;index<=jindex;index++)
		    		    					{
		    		    						AbstractNode nodex = ijnodes.get(index);					    		    												    		    						
		    		    						
			        		    				locks = new HashSet<Integer>(lockhistory);        		    					
			        		    				locks.retainAll(nodex.getLockSet());
			        		    				if(locks.size()==0)
			        		    				{
			        	    		    			if(canNotReach(nodex,nodeR))
			        	    		    			{
			                		    				savePattern(p,nodex);
			        	    		    			}
		    		    						}
			        		    				else
			        		    				{
			        		    					index = iindex;
			        		    					nodex = ijnodes.get(index);					    		    												    		    						
			    		    						
				        		    				locks = new HashSet<Integer>(lockhistory);        		    					
				        		    				locks.retainAll(nodex.getLockSet());
				        		    				if(locks.size()==0)
				        		    				{
				        		    					if(canNotReach(nodex,nodeR))
				        	    		    			{
				                		    					savePattern(p,nodex);
				        	    		    			}
				        		    				}
				        		    				else
				        		    				{
				        		    					index = jindex;
				        		    					nodex = ijnodes.get(index);					    		    												    		    						
				    		    						
					        		    				locks = new HashSet<Integer>(lockhistory);        		    					
					        		    				locks.retainAll(nodex.getLockSet());
					        		    				if(locks.size()==0)
					        		    				{
					        		    					if(canNotReach(nodex,nodeR))
					        	    		    			{
					                		    				savePattern(p,nodex);
					        	    		    			}
					        		    				}
				        		    				}
			        		    				}
			        		    					
		    		    					}
	    		    					}
		    						}
	    						}
	    						}
		    				}
						}
						}
				        }
					}
				}
			}
		}
				        	
	}

	private static void findAtomicityPatternTwo(HashMap<Long, ThreadNodes> map, Long tid) 
	{
			LinkedList<AbstractNode> nodes = map.get(tid).getAllRNodes();
			for (int j=0; j<nodes.size();j++)
			{
				AbstractNode rnode = nodes.get(j);
				Iterator<Long> mapIt2 = map.keySet().iterator();
				while(mapIt2.hasNext())
				{	
					Long tid2 =  mapIt2.next();
					if(tid!=tid2)
					{
		    				LinkedList<AbstractNode> wnodes = map.get(tid2).getAllWNodes();
		    				if(wnodes.size()>1)
		    				{
		    					//PRE CHECK PARTIAL ORDER CONSTRAINTS
		    					if(canNotReach(rnode,wnodes.getFirst()) && canNotReach(wnodes.getLast(),rnode))
		    					{	
		
			    					for(int k=0;k<wnodes.size();k++)
			    					{
			    						AbstractNode firstwnode = wnodes.get(k);
			    						if(!canNotReach(rnode,firstwnode))
			    							break;
			    						
			    						for(int m=wnodes.size()-1;m>k;m--)
			    						{
				    	    				AbstractNode lastwnode = wnodes.get(m);
			    		    				if(!canNotReach(lastwnode,rnode))
			    		    					break;
				    	    				
			    		    				Pattern p = new TypeTwoPattern(firstwnode,rnode,lastwnode);
			    		    				
			    		    				if(!isRedundantPattern(p))
			    		    				{    
							    				int K = rnode.getID();
				    		    				int I = firstwnode.getID();
				    		    				int J = lastwnode.getID();
				    		    				
				    		    				if(K>I&&K<J)
				    		    				{
				    		    	    			showRealPattern(p);
				    		    				}
				    		    				else 
				    		    				{
				    		    					if(rnode.getLockSet()!=null&&firstwnode.getLockSet()!=null&&lastwnode.getLockSet()!=null)
						    						{	
			    		    						Set<Integer> locks = new HashSet<Integer>(rnode.getLockSet());
			    		    						locks.retainAll(firstwnode.getLockSet());
			    		    						if(locks.size()==0)
			        		    					{
			    		    							//PATTERN SATISFIED
			            		    					savePattern(p,firstwnode);
			        		    					}
			    		    						else
			    		    						{
		    		    								Vector<AbstractNode> flnodes = threadNodes.get(firstwnode.getTId());

			    		    							locks = new HashSet<Integer>(rnode.getLockSet());
			    		    							locks.retainAll(lastwnode.getLockSet());
			    		    							if(locks.size()==0)
				        		    					{
				    		    							//PATTERN SATISFIED
			                		    					savePattern(p,flnodes.get(flnodes.indexOf(lastwnode)-1));
				        		    					}
			    		    							else
			    		    							{
			    	    		    	        			int firstIndex = flnodes.indexOf(firstwnode)+2;
			    	    		    	        			int lastIndex = flnodes.indexOf(lastwnode)-2;
			    		    		    					//CHECK LOCKING CONSTRAINTS
			    	    		    	        			if(firstIndex<=lastIndex)
			    	    		    	        			{
			    	    		    	        				AbstractNode nodex = flnodes.get((firstIndex+lastIndex)/2);
			    	    		    	        				
			    	    		    	        				locks = new HashSet<Integer>(rnode.getLockSet());        		    					
		    			        		    					locks.retainAll(nodex.getLockSet());
		    			        		    					if(locks.size()==0)
		    			        		    					{
		    			            		    						//PATTERN SATISFIED
		    			                		    					savePattern(p,nodex);
		    			        		    					}
		    			        		    					else 
		    			        		    					{
		    			        		    						nodex = flnodes.get(firstIndex);
		    			        		    						locks = new HashSet<Integer>(rnode.getLockSet());        		    					
			    			        		    					locks.retainAll(nodex.getLockSet());
			    			        		    					if(locks.size()==0)
			    			        		    					{
			    			            		    						//PATTERN SATISFIED
			    			                		    					savePattern(p,nodex);
			    			        		    					}
			    			        		    					else 
			    			        		    					{
			    			        		    						nodex = flnodes.get(lastIndex);
			    			        		    						locks = new HashSet<Integer>(rnode.getLockSet());        		    					
				    			        		    					locks.retainAll(nodex.getLockSet());
				    			        		    					if(locks.size()==0)
				    			        		    					{
				    			            		    						//PATTERN SATISFIED
				    			                		    					savePattern(p,nodex);
				    			        		    					}
			    			        		    					}

		    			        		    					}
			    	    		    	        			}
			    		    		    					
			    	    		    	        			/*//for(int i=firstIndex;i<=lastIndex;i++)//TODO: THIS STEP IS TOO INEFFICIENT!!!
			    		    		    					//{
			    		    		    						AbstractNode nodex = flnodes.get((firstIndex+lastIndex)/2);
			    	        	    		    				if((K<I&&canNotReach(rnode,nodex))||(K>J&&canNotReach(nodex,rnode)))
			    	        	    		    				{
			    			        		    					locks = new HashSet<Integer>(rnode.getLockSet());        		    					
			    			        		    					locks.retainAll(nodex.getLockSet());
			    			        		    					if(locks.size()==0)
			    			        		    					{
			    			            		    						//PATTERN SATISFIED
			    			                		    					savePattern(p,nodex);
			    			            		    						//break;
			    			        		    					}
			    	        	    		    				}
			    		    		    					//}*/
			    		    							}
			    		    						}
			    	
				    		    				}
		    		    					}
		    		    				}
		    						}
		    					}
		    				}
						}
	    				
					}
				}
			}
	}
	private static void findAtomicityPatternOne(HashMap<Long, ThreadNodes> map, Long tid) 
	{
			LinkedList<AbstractNode> writenodes = map.get(tid).getAllWNodes();
			for (int j=0; j<writenodes.size();j++)
			{
				AbstractNode wnode = writenodes.get(j);
				
				Iterator<Long> mapIt2 = map.keySet().iterator();
				while(mapIt2.hasNext())
				{	
					Long tid2 =  mapIt2.next();
					if(tid!=tid2)
					{
		    				LinkedList<AbstractNode> nodes = map.get(tid2).getAllWRNodes();
		    				if(nodes.size()>1)
		    				{    	
		    					//PRE CHECK PARTIAL ORDER CONSTRAINTS
		    					if(canNotReach(wnode,nodes.getFirst()) && canNotReach(nodes.getLast(),wnode))
		    					{	
			    					for(int k=0;k<nodes.size();k++)
			    					{
			    						AbstractNode firstnode = nodes.get(k);
			    						if(!canNotReach(wnode,firstnode))
			    							break;
			    						
			    						for(int m=nodes.size()-1;m>k;m--)
			    						{   		    				
			    		    				AbstractNode lastnode = nodes.get(m);
			    		    				if(!canNotReach(lastnode,wnode))
			    		    					break;
			    		    				
			    		    				Pattern p = new TypeOnePattern(firstnode,wnode,lastnode);
			    		    				
			    		    				if(!isRedundantPattern(p))
			    		    				{    	
				    		    				int K = wnode.getID();
				    		    				int I = firstnode.getID();
				    		    				int J = lastnode.getID();
				    		    				
				    		    				if(K>I&&K<J)
				    		    				{
				    		    	    			showRealPattern(p);
				    		    				}
				    		    				else 
				    		    				{
				    		    					if(wnode.getLockSet()!=null&&firstnode.getLockSet()!=null&&lastnode.getLockSet()!=null)
						    						{
			    		    						Set<Integer> locks = new HashSet<Integer>(wnode.getLockSet());
			    		    						locks.retainAll(firstnode.getLockSet());
			    		    						if(locks.size()==0)
			        		    					{
			    		    							//PATTERN SATISFIED
			            		    					savePattern(p,firstnode);
			        		    					}
			    		    						else
			    		    						{
		    	    		    	        			Vector<AbstractNode> flnodes = threadNodes.get(firstnode.getTId());

			    		    							locks = new HashSet<Integer>(wnode.getLockSet());
			    		    							locks.retainAll(lastnode.getLockSet());
			    		    							if(locks.size()==0)
				        		    					{
				    		    							//PATTERN SATISFIED
			                		    					savePattern(p,flnodes.get(flnodes.indexOf(lastnode)-1));
				        		    					}
			    		    							else
			    		    							{
			    		    								//IF WE CAN HERE, PRINT MESSAGE 
			    		    								//System.err.println("\n*** INEFFICIENT LOCKING CONSTRAINT ***\n");
			    		    								
			    		    								
			    		    								//JUST USE A SIMPLE AND EFFICIENT WAY
			    		    								//HOPE FOR THE BEST AT THIS MOMENT
			    	    		    	        			int firstIndex = flnodes.indexOf(firstnode)+2;
			    	    		    	        			int lastIndex = flnodes.indexOf(lastnode)-2;
			    		    		    					//CHECK LOCKING CONSTRAINTS
			    	    		    	        			if(firstIndex<=lastIndex)
			    	    		    	        			{
			    	    		    	        				AbstractNode nodex = flnodes.get((firstIndex+lastIndex)/2);
			    	    		    	        				
			    	    		    	        				locks = new HashSet<Integer>(wnode.getLockSet());        		    					
		    			        		    					locks.retainAll(nodex.getLockSet());
		    			        		    					if(locks.size()==0)
		    			        		    					{
		    			            		    						//PATTERN SATISFIED
		    			                		    					savePattern(p,nodex);
		    			        		    					}
		    			        		    					else 
		    			        		    					{
		    			        		    						nodex = flnodes.get(firstIndex);
		    			        		    						locks = new HashSet<Integer>(wnode.getLockSet());        		    					
			    			        		    					locks.retainAll(nodex.getLockSet());
			    			        		    					if(locks.size()==0)
			    			        		    					{
			    			            		    						//PATTERN SATISFIED
			    			                		    					savePattern(p,nodex);
			    			        		    					}
			    			        		    					else 
			    			        		    					{
			    			        		    						nodex = flnodes.get(lastIndex);
			    			        		    						locks = new HashSet<Integer>(wnode.getLockSet());        		    					
				    			        		    					locks.retainAll(nodex.getLockSet());
				    			        		    					if(locks.size()==0)
				    			        		    					{
				    			            		    						//PATTERN SATISFIED
				    			                		    					savePattern(p,nodex);
				    			        		    					}
			    			        		    					}

		    			        		    					}
			    	    		    	        			}
/*			    		    		    					//for(int i=firstIndex;i<=lastIndex;i++)//TODO: THIS STEP IS TOO INEFFICIENT!!!
			    		    		    					//{
			    		    		    						AbstractNode nodex = flnodes.get((firstIndex+lastIndex)/2);
			    	        	    		    				if((K<I&&canNotReach(wnode,nodex))||(K>J&&canNotReach(nodex,wnode)))
			    	        	    		    				{
			    			        		    					locks = new HashSet<Integer>(wnode.getLockSet());        		    					
			    			        		    					locks.retainAll(nodex.getLockSet());
			    			        		    					if(locks.size()==0)
			    			        		    					{
			    			            		    						//PATTERN SATISFIED
			    			                		    					savePattern(p,nodex);
			    			            		    						//break;
			    			        		    					}
			    	        	    		    				}
			    		    		    					//}*/
			    		    							}
			    		    						}
				    		    				}
			    		    				}
			    						}
			    					}
		    					}
		    				}
						}
					}
				}
			}
		
	}
	
	private static void findRace(HashMap<Long, ThreadNodes> map, Long tid) 
	{
			LinkedList<AbstractNode> nodes = map.get(tid).getAllWRNodes();
			for (int j=0; j<nodes.size();j++)
			{
				AbstractNode node = nodes.get(j);
				
				Iterator<Long> mapIt2 = map.keySet().iterator();
				while(mapIt2.hasNext())
				{	
					Long tid2 =  mapIt2.next();
					if(tid!=tid2)
					{
		    				LinkedList<AbstractNode> nodes2 = map.get(tid2).getAllWRNodes();
		    				if(nodes2.size()>0)
		    				{    			    							    					
		    					//PRE CHECK PARTIAL ORDER CONSTRAINTS
		    					if(canNotReach(node,nodes2.getFirst()) && canNotReach(nodes2.getLast(),node))
		    					{	
			    					for(int k=0;k<nodes2.size();k++)
			    					{
			    						AbstractNode node2 = nodes2.get(k);
			    						
			    						if(node.getType()==TYPE.WRITE||node2.getType()==TYPE.WRITE)
			    						{
				    						if(!canNotReach(node,node2)||!canNotReach(node2,node))
				    							break;
				    						
				    						if(node.getLockSet()!=null&&node2.getLockSet()!=null)
				    						{
				    						Set<Integer> locks = new HashSet<Integer>(node.getLockSet());
				    						locks.retainAll(node2.getLockSet());
				    						if(locks.size()==0)
				    						{
				    							saveRacePattern(node,node2);
//				    							if(node.getMemString().contains("_available"))
//				    								System.out.print(true);
				    								
				    						}
				    						}
			    						}
			    					}
		    					}
		    				}
						}
					}
				}
			}
	
	private static void saveRacePattern(AbstractNode node, AbstractNode node2) 
	{		
		PatternRace pattern = new PatternRace(node,node2);
		
		if(!racepatterns.contains(pattern))
		{
			racepatterns.add(pattern);
/*		
			HashSet<AbstractNode> raceset = racemap.get(node);
			if(raceset==null)
			{
				raceset = new HashSet<AbstractNode>();
				racemap.put(node, raceset);
			}
			raceset.add(node2);
*/			
		}
	
	}
	private static boolean isRedundantPattern(Pattern pattern)
	{
		if(patterns.contains(pattern))
			return true;
		return false;
	}
	
	/**
	 * 
	 * @param pattern: real detected pattern
	 * @param nodex: i'
	 */
	private static void savePattern(Pattern pattern, AbstractNode nodex) 
	{
			patterns.add(pattern);
			patternsToNodes.put(pattern,nodex);
	}
	
	private static void testCanReach(Vector<AbstractNode>[] accessvec)
    {
    	System.err.println("\n*** Testing Reachability ***\n");
    	
		for(int i=0;i<accessvec.length;i++)
		{
			for(int j=0;j<accessvec[i].size()-1;j++)
			{
				for(int k=j+1;k<accessvec[i].size();k++)
				{
					if(canNotReach(accessvec[i].get(j),accessvec[i].get(k)))
					{
						print(accessvec[i].get(j)+" ---> "+accessvec[i].get(k)+"\n");
					}
				}
			}
			
		}
    }
    private static void showTransformedPattern()
    {    			
    	Iterator<Pattern> patternsIt = patternsToNodes.keySet().iterator();
    	while(patternsIt.hasNext())
    	{
    		Pattern p = patternsIt.next();
			//System.out.print("\n*** Transformed Pattern ***"+p.toString());

			print(p.toString());
    	}
    }
    private static void transform()
    {
    	//save the original trace first
    	Vector<AbstractNode> originaltrace = getAFreshTrace(maintrace.size()-1);
		removeNonReadWriteNodes(originaltrace);
		saveTransformedSchedule(originaltrace,0);
    	
    	
    	transformRace();
    	
    	Iterator<Pattern> patternsIt = patternsToNodes.keySet().iterator();
    	while(patternsIt.hasNext())
    	{
    		Pattern p = patternsIt.next();
			AbstractNode nodeX = patternsToNodes.get(p);
    		if(p instanceof PatternI )
    		{
    			transformAtomicity((PatternI)p,nodeX);
    			
    		}
    		else if(p instanceof PatternII)
    		{

    			transformAtomset((PatternII)p,nodeX);
    		}
    	}
    }
    
    private static void transformRace() {

    	Iterator<PatternRace> patternsIt = racepatterns.iterator();
    	while(patternsIt.hasNext())
    	{
    		PatternRace p = patternsIt.next();
    		transPatternNumber++;
			print("\n*** Apply Transformation >>> "+transPatternNumber+": "+p.toString());
			
			AbstractNode nodeA = p.getNodeI();
			AbstractNode nodeB = p.getNodeII();
					
			int aIndex = nodeA.getID();
			int bIndex = nodeB.getID();
			
			if(aIndex>bIndex)
			{
				int tempIndex = aIndex;
				aIndex = bIndex;
				bIndex = tempIndex;
				
				AbstractNode tempNode = nodeA;
				nodeA = nodeB;
				nodeB = tempNode;
			}
			
			if(aIndex>0&&bIndex>0)
			{
				Vector<AbstractNode> trace = getAFreshTrace(bIndex);
				
				Vector<AbstractNode> depNodes = getDependentNodes(aIndex,bIndex);
//				for(int k=0;k<depNodes.size();k++)
//				{
//					AbstractNode node = depNodes.get(k);
//					int pos = node.getID();
//					trace.remove(pos);
//					//trace.add(node);
//				}
				
				trace.removeAll(depNodes);
				
				trace.remove(nodeA);
				if(nodeA.getType()==TYPE.WRITE)
					trace.insertElementAt(nodeA, trace.indexOf(nodeB));
				else
					trace.add(nodeA);
				
				removeNonReadWriteNodes(trace);
				saveTransformedSchedule(trace,transPatternNumber);
			}
			
    	}
		
	}
    private static void removeNonReadWriteNodes(Vector<AbstractNode> trace) {

    	for(int k=0;k<trace.size();k++)
    	{
    		AbstractNode node = trace.get(k);
    		if(node.getType()!=TYPE.READ&&node.getType()!=TYPE.WRITE)
    		{
    			trace.remove(k);
    			k--;
    		}
    	}
		
	}
	private static Vector<AbstractNode> getDependentNodes(int aIndex, int bIndex) {
		//TODO: Rewrite this function
		//USE threadIdset and then memoryset
		
    	Vector<AbstractNode> depNodes = new Vector<AbstractNode>();
    	Set<AbstractNode> recvNodes = new HashSet<AbstractNode>();
    	Set<Long> depTidSet = new HashSet<Long>();
    	//Set<Integer> depMemSet = new HashSet<Integer>();
    	
    	AbstractNode startNode = maintrace.get(aIndex);
    	depTidSet.add(startNode.getTId());
//    	if(startNode.getType() ==TYPE.WRITE)
//    		depMemSet.add(startNode.getMem());
    	
    	for(int pos =aIndex+1;pos<bIndex;pos++)
    	{
    		AbstractNode node = maintrace.get(pos);
    		//boolean isDepNode =false;
    		long tid = node.getTId();
    		int mem = node.getMem();
    		if(depTidSet.contains(tid))//||
    		{
    			depNodes.add(node);
//    			if(node.getType() == TYPE.WRITE)
//    				depMemSet.add(mem);
//    			else
    				if(node.getType() == TYPE.SEND)
    				recvNodes.addAll(node.getOutNodes());
    		}
//    		else if(depMemSet.contains(mem))
//    		{
//    			depNodes.add(node);
//    			depTidSet.add(tid);
//    		}
    		else if(recvNodes.contains(node))
    		{
    			depNodes.add(node);
    			depTidSet.add(tid);
    		}
    			
    	}
    	   	
//    	Vector<AbstractNode> nodes = threadNodes.get(startNode.getTId());
//    	int startpos = nodes.indexOf(startNode);
//    	for(int k=startpos+1;k<nodes.size();k++)
//    	{
//    		AbstractNode node = nodes.get(k);
//    		int nodeId = node.getID();
//    		if(nodeId<bIndex)
//    		{
//    			depNodes.add(node);
//    			if(node.getType()==TYPE.SEND)
//    			{
//    				Set<AbstractNode> outNodes = node.getOutNodes();
//    				Iterator<AbstractNode> outNodesIt = outNodes.iterator();
//    				while(outNodesIt.hasNext())
//    				{
//    					AbstractNode aanode = outNodesIt.next();
//    					int aaIndex = aanode.getID();
//    					if(aaIndex<bIndex)
//    					{
//    						depNodes.add(aanode);    					
//    						depNodes.addAll(getDependentNodes(aaIndex,bIndex));
//    					}
//    				}
//    			}
//    		}
//    		else
//    			break;
//    	}
		return depNodes;
	}
	private static Vector<AbstractNode> getAFreshTrace(int bIndex) {
		
    	Vector<AbstractNode> trace = new Vector<AbstractNode>();
    	for(int i=0;i<=bIndex;i++)
    	{
    		trace.add(maintrace.get(i));
    	}
		return trace;
	}
	private static void transformAtomicity(PatternI p, AbstractNode nodeX)
    {
		transPatternNumber++;
		print("\n*** Apply Transformation >>> "+transPatternNumber+": "+p.toString());
				
    	AbstractNode nodeI = p.getNodeI();
		AbstractNode nodeJ = p.getNodeJ();
		AbstractNode nodeK = p.getNodeK();
		
		int IIndex = nodeI.getID();
		int JIndex = nodeJ.getID();
		int KIndex = nodeK.getID();
		
		int XIndex = nodeX.getID();
		
		if(IIndex>0&&JIndex>0&&KIndex>0&&XIndex>0)
		{		

			if(KIndex>JIndex)
			{		
				Vector<AbstractNode> trace = getAFreshTrace(KIndex);
					
				Vector<AbstractNode> depNodes_move = getDependentNodes(XIndex,JIndex+1);
				for(int k=0;k<depNodes_move.size();k++)
				{
					AbstractNode node = depNodes_move.get(k);
					trace.remove(node);
					trace.add(node);
				}
				
				Vector<AbstractNode> depNodes = getDependentNodes(JIndex,KIndex);
				trace.removeAll(depNodes);
				
				removeNonReadWriteNodes(trace);
				saveTransformedSchedule(trace,transPatternNumber);
			}
			else if(KIndex<IIndex)
			{	
				
				Vector<AbstractNode> trace = getAFreshTrace(JIndex);

				Vector<AbstractNode> depNodes_move = getDependentNodes(KIndex,XIndex);
				
				depNodes_move.add(0,nodeK);
				
				int pos;
				AbstractNode lastnode = nodeX;
				for(int k=0;k<depNodes_move.size();k++)
				{
					AbstractNode node = depNodes_move.get(k);
					trace.remove(node);
					pos = trace.indexOf(lastnode);
					trace.insertElementAt(node,pos+1);
					lastnode = node;
				}
				
				removeNonReadWriteNodes(trace);
				saveTransformedSchedule(trace,transPatternNumber);
				
			}
		}
    }
    private static void transformAtomset(PatternII p, AbstractNode nodeX)
    {
    	
    	transPatternNumber++;
		print("\n*** Apply Transformation >>> "+transPatternNumber+": "+p.toString());
		
		
    	AbstractNode nodeI = p.getNodeI();
		AbstractNode nodeJ = p.getNodeJ();
		AbstractNode nodeK = p.getNodeK();
		AbstractNode nodeR = p.getNodeR();

		int IIndex = nodeI.getID();
		int JIndex = nodeJ.getID();
		int KIndex = nodeK.getID();
		int RIndex = nodeR.getID();
		
		int XIndex = nodeX.getID();
		
		if(IIndex>0&&JIndex>0&&KIndex>0&&RIndex>0&&XIndex>0)
		{		
			//MAKE SURE K<R
			if(KIndex>RIndex)
			{
				int tempIndex = KIndex;
				KIndex = RIndex;
				RIndex = tempIndex;
				
				AbstractNode tempNode = nodeK;
				nodeK = nodeR;
				nodeR = tempNode;
			}
			
			if(KIndex>IIndex)
			{	

				Vector<AbstractNode> trace = getAFreshTrace(RIndex);

				Vector<AbstractNode> depNodes_move = getDependentNodes(XIndex,JIndex+1);
				for(int k=0;k<depNodes_move.size();k++)
				{
					AbstractNode node = depNodes_move.get(k);
					
					trace.remove(node);
					trace.add(node);
				}
				
				Vector<AbstractNode> depNodes = getDependentNodes(JIndex,RIndex);
				trace.removeAll(depNodes);
				
				removeNonReadWriteNodes(trace);
				saveTransformedSchedule(trace,transPatternNumber);										
			}
			else if(RIndex<JIndex)
			{
				Vector<AbstractNode> trace = getAFreshTrace(JIndex);

				Vector<AbstractNode> depNodes_move = getDependentNodes(KIndex,XIndex);
				
				depNodes_move.add(0,nodeK);
				
				int pos;
				AbstractNode lastnode = nodeX;
				for(int k=0;k<depNodes_move.size();k++)
				{
					AbstractNode node = depNodes_move.get(k);
					trace.remove(node);
					pos = trace.indexOf(lastnode);
					trace.insertElementAt(node,pos+1);
					lastnode = node;
				}
				
				Vector<AbstractNode> depNodes = getDependentNodes(RIndex,JIndex);
				trace.removeAll(depNodes);
				
				removeNonReadWriteNodes(trace);
				saveTransformedSchedule(trace,transPatternNumber);	
			} 
			else if(KIndex<IIndex&&RIndex>JIndex)
			{
				Vector<AbstractNode> trace = getAFreshTrace(RIndex);

				Vector<AbstractNode> depNodes_move1 = getDependentNodes(XIndex,JIndex+1);
				for(int k=0;k<depNodes_move1.size();k++)
				{
					AbstractNode node = depNodes_move1.get(k);
					trace.remove(node);
					trace.add(node);
				}
				
				Vector<AbstractNode> depNodes_move2 = getDependentNodes(KIndex,XIndex);
				
				depNodes_move2.add(0,nodeK);
				
				int pos;
				AbstractNode lastnode = nodeX;
				for(int k=0;k<depNodes_move2.size();k++)
				{
					AbstractNode node = depNodes_move2.get(k);
					trace.remove(node);
					pos = trace.indexOf(lastnode);
					trace.insertElementAt(node,pos+1);
					lastnode = node;
				}
				
				Vector<AbstractNode> depNodes = getDependentNodes(JIndex,RIndex);
				trace.removeAll(depNodes);
				
				removeNonReadWriteNodes(trace);
				saveTransformedSchedule(trace,transPatternNumber);		
			}
			
		}
		    					
    }
	private static void insertStopEvent(Vector<AbstractNode> trace, int i) 
    {
    	AbstractNode node = new AbstractNode(-1, -1, -1, null, null, null, null);
    	trace.insertElementAt(node,i);	
	}
	private static int getPos(Vector<AbstractNode> trace, AbstractNode node0) 
    {
		int index = node0.getID();
		int size = trace.size();
		int pos = size-1;
		if(trace.get(pos).getID()!=index)
		{
			int left =0;
			int right = size;
	    	pos = size/2;
	    	
	    	int id = trace.get(pos).getID();
			while( id != index)
			{
				if(id>index)
				{
					right = pos;
	
				}
				else
				{
					left = pos;
				}
				pos = (left+right)/2;
				id = trace.get(pos).getID();
			}
		}
		return pos;
	}
	/**
     * 0 MEANS CONTINUE
     */
    private static void removeAfterThreadEvent(Vector<AbstractNode> trace,
			Vector<AbstractNode> nodes, AbstractNode node0) {
		
		int index = node0.getID();
		int size = nodes.size();
		int id=size-1;
		for(;nodes.get(id).getID()>index;id--)
		{			
			nodes.get(id).setTid(0);
			if(id==0)
				break;
		}
	}
    
    /**
     * -1 MEANS VIOLATION CREATED
     */
	private static void removeAfterEvent(Vector<AbstractNode> trace,
			AbstractNode node0) 
    {
		int pos = getPos(trace,node0);
		if(pos!=trace.size()-1)
		{
			AbstractNode stopNode = trace.get(pos+1);
			
			//-1 MEANS VIOLATION CREATED
			stopNode.setTid(-1);
		}
	}
	private static void showRealPattern(Pattern p) 
    {
		if(p instanceof TypeOnePattern)
		{
			AbstractNode nodeI = ((TypeOnePattern)p).getNodeI();
//			if(nodeI.getMemString()=="org.exolab.jms.net.multiplexer.MultiplexInputStream._available")
//				System.out.print(true);
		}
		
		patterns.add(p);
    	realPatternNumber++;
		print("\n*** Real Pattern ***"+p.toString());
	}

	private static MonitorData makeFreshHBG() {
    	MonitorData hbg = (MonitorData)getMonitorData();
		return hbg;
	}
	private static void applyTransfromBefore(Vector<AbstractNode>[] accessvec, AbstractNode nodeL, AbstractNode nodeR) {
    	
    	//TranformNodePair pair = new TranformNodePair(nodeL,nodeR);
    	
		//if(!transformedpairs.contains(pair))
		//{
		//	transformedpairs.add(pair);
			
		int mem = nodeL.getMem();
		for(int k=0;k<accessvec[mem].size();k++)	
		{
			if(nodeR.getID()==accessvec[mem].get(k).getID())
			{
				nodeR = accessvec[mem].get(k);
				break;
			}	
		}
		int index = -1;
		for(int k=0;k<accessvec[mem].size();k++)	
		{
			if(nodeL.getID()==accessvec[mem].get(k).getID())
			{
				index = k;
				break;
			}	
		}

		accessvec[mem].remove(nodeR);
		accessvec[mem].insertElementAt(nodeR, index);
		//}
	}
	private static void applyTransfromAfter(Vector<AbstractNode>[] accessvec, AbstractNode nodeL, AbstractNode nodeR) {
    	

			int mem = nodeL.getMem();
			for(int k=0;k<accessvec[mem].size();k++)	
			{
				if(nodeR.getID()==accessvec[mem].get(k).getID())
				{
					nodeR = accessvec[mem].get(k);
					break;
				}	
			}
			int index = -1;
			for(int k=0;k<accessvec[mem].size();k++)	
			{
				if(nodeL.getID()==accessvec[mem].get(k).getID())
				{
					index = k;
					break;
				}	
			}
			accessvec[mem].remove(nodeR);
			if(nodeL.getID()<nodeR.getID())
				accessvec[mem].insertElementAt(nodeR, index+1);
			else
				accessvec[mem].insertElementAt(nodeR, index);

	}
	private static void saveTransformedSchedule(
			Vector<AbstractNode> trace, int transPatternNumber) 
	{
		ObjectOutputStream out = null;
		Vector<Long> schedule_tid_ = new Vector<Long>();
		Vector<Integer> schedule_line_ = new Vector<Integer>();
		for(int k=0;k<trace.size();k++)
		{
			AbstractNode node = trace.get(k);
			schedule_tid_.add(node.getTId());
			schedule_line_.add(node.getLine());
		}
		
		try
		{	
			out = getOutputStream(getScheduleTidDir()+System.getProperty("file.separator")+transPatternNumber);
    		out.writeObject(schedule_tid_);
		}catch(Exception e)
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
    	
		try
		{	
			out = getOutputStream(getScheduleLineDir()+System.getProperty("file.separator")+transPatternNumber);
    		out.writeObject(schedule_line_);
		}catch(Exception e)
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
	private static AbstractNode getDetNode(AbstractNode node)
	{
		Integer id = determinNodeMap.get(determindNodeMap.get(node.getTIndex()));
		if(id==null)
			return null;
		return	maintrace.get(id);
	}
	private static AbstractNode getDetDNode(AbstractNode node)
	{
		return	maintrace.get(determindNodeMap.get(node.getTIndex()));
	}
	/*
	 * Two nodes by different threads
	 */
	private static boolean canNotReach(AbstractNode node1, AbstractNode node2)
    {
		//COMPUTE THE PARTIAL ORDER CONSTAINT ON DEMAND			
		if(getDetNode(node1)==null)
			return true;
		
    	int id1 = getDetNode(node1).getID();
    	int id2 = getDetDNode(node2).getID();
    	
    	return por.canNotReach(id1,id2);
    }    

    private static void printThreadViewedNodes(HashMap<Long, Vector<AbstractNode>> threadNodes)
    {
    	System.err.println("*** Thread Viewed Nodes ***");
    	Iterator<Vector<AbstractNode>> threadNodesIt = threadNodes.values().iterator();
    	while(threadNodesIt.hasNext())
    	{
    		Vector<AbstractNode> vec = threadNodesIt.next();
    		for(int i=0;i<vec.size();i++)
    		{
    			System.out.println(vec.get(i));
    		}
    	}
    }
    private static void printSPEViewedNodes(Vector<AbstractNode>[] accessvec)
    {
    	System.err.println("\n*** SPE Viewed Nodes ***");
		for(int i=0;i<accessvec.length;i++)
		{
			for(int j=0;j<accessvec[i].size();j++)
			System.out.println(accessvec[i].get(j));
		}
    }
}	
