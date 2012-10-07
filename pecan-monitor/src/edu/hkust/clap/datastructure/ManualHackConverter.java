package edu.hkust.clap.datastructure;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.zip.GZIPOutputStream;

import edu.hkust.clap.MonitorThread;

import edu.hkust.clap.monitor.Monitor;
import edu.hkust.clap.monitor.MonitorData;
//import edu.hkust.clap.*;
//import edu.hkust.clap.datastructure.*;
//import edu.hkust.clap.generator.*;

public class ManualHackConverter {
    private static boolean debug = true;

    private static int objectIndexCounter = 500;

    public static MonitorData mondata = new MonitorData();

    private static HashMap<Integer,LockNode> lockDepMap = new HashMap<Integer,LockNode>();
    private static HashMap<Long,Integer> objectMemMap = new HashMap<Long,Integer>();
    private static HashMap<Long,Long> callsiteInfoMap = new HashMap<Long,Long>();

    public static void main(String[] args) {
        DataInputStream in;
        try {
            in = new DataInputStream(new
                      BufferedInputStream(new FileInputStream("trace.out")));
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found");
            return;
        }
     
        {
             MessageNode startNode = new MessageNode(11,1,AbstractNode.TYPE.SEND);// thread 1 starts thread 11
             MessageNode newNode1 = new MessageNode(11,11,AbstractNode.TYPE.RECEIVE); // thread 11 begins running
             LockNode newNode2 = new LockNode(502,11,AbstractNode.TYPE.LOCK); // thread 11 locks the lock 502
             RWNode newNode3 = new RWNode(19,0,11,AbstractNode.TYPE.WRITE); // thread 11 writes to mem 0
             RWNode newNode4 = new RWNode(23,0,11,AbstractNode.TYPE.WRITE); //thread 11 writes to mem 0
             LockNode newNode5 = new LockNode(502,11,AbstractNode.TYPE.UNLOCK);  // thread 11 unlocks the lock 502
             MessageNode exitNode = new MessageNode(11,11,AbstractNode.TYPE.SEND); // thread 11 stops running
             RWNode newNode6 = new RWNode(37,0,1,AbstractNode.TYPE.WRITE); // thread 1 writes to mem 0
             MessageNode newNode7 = new MessageNode(11,1,AbstractNode.TYPE.RECEIVE); // thread 1's joining operation of thread 11.
            
             mondata.addToTrace(startNode);
             mondata.addToTrace(newNode1);             
             mondata.addToTrace(newNode2);
             mondata.addToTrace(newNode3);
             mondata.addToTrace(newNode4);
             mondata.addToTrace(newNode5);             
             mondata.addToTrace(exitNode);
             mondata.addToTrace(newNode6);
             mondata.addToTrace(newNode7);
             
             
             //*** AV-I --- 4 0 19 11 WRITE * 8 0 37 1 WRITE * 5 0 23 11 WRITE ***
        	

        	
        	
        	
        } 
        saveMonitorData();
    }

    public static void saveMonitorData() {
    	
    	ObjectOutputStream	out  = null;
		try
		{
			out = MonitorThread.getOutputStream();
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
    
    	
//        ObjectOutputStream fout_monitordata;
//
//        try {
//          fout_monitordata = new ObjectOutputStream(new GZIPOutputStream(
//                            new FileOutputStream("monitor.trace.gz")));
//         
//          // TODO: match output file format with Monitor.saveMonitorData()
//          //Util.storeObject(mondata, fout_monitordata);
//          fout_monitordata.writeObject(mondata);
//          fout_monitordata.flush();
//          fout_monitordata.close();
//        } catch (FileNotFoundException e) {
//            System.out.println("FileNotFoundException");
//        } catch (IOException e) {
//            System.out.println("IOException");
//        }
    }

    /*-------------------------------------------------------------------------------------
    1. thread creation
      type  := 49
      tid   := thread id
    2. thread join
      type  := 50
      tid   := thread id
    3. thread start
      type  := 51
      tid   := thread id
    4. thread end
      type  := 52
      tid   := thread id
    5. method entry
      type  := 53
      id    := method id
      tid   := thread id
    6. method exit
      type  := 54
      id    := method id
      tid   := thread id
    7. lock acquisition
      type  := 55
      addr  := lock address
      tid   := thread id
    8. lock release
      type  := 56
      addr  := lock address
      tid   := thread id
    9. shared variable read
      type  := 57
      addr  := variable address
      tid   := thread id
      id    := (method id << 32) | (basic block id << 16) | (line id)
    10. shared variable write
      type  := 58
      addr  := variable address
      tid   := thread id
      id    := (method id << 32) | (basic block id << 16) | (line id)
    11. method call
      type  := 59
      addr  := callee method id
      tid   := thread id
      id    := (method id << 32) | (basic block id << 16) | (line id)
    --------------------------------------------------------------------------------------*/
    public static void appendMonitorData(byte type, long tid, long id, long addr) {
        AbstractNode node = null;
        LockNode newNode, lockDepNode;
        int iid, line;
        Long callsite;
        switch(type) {
            case 49:        // create
                // is this right way to generate iid?
                iid = getObjectMem(tid);
                node = new MessageNode(iid, tid, AbstractNode.TYPE.SEND);
                mondata.addToTrace(node);
                if(debug) System.out.println("CREATE: " + iid + "\t" + tid);
                break;
            case 50:        // join
                iid = getObjectMem(tid);
                node = new MessageNode(iid, tid, AbstractNode.TYPE.RECEIVE);
                mondata.addToTrace(node);
                if(debug) System.out.println("JOIN: " + iid + "\t" + tid);
                break;
            case 51:        // start
                iid = getObjectMem(tid);
                node = new MessageNode(iid, tid, AbstractNode.TYPE.RECEIVE);
                mondata.addToTrace(node);
                if(debug) System.out.println("START: " + iid + "\t" + tid);
                break;
            case 52:        // end
                iid = getObjectMem(tid);
                node = new MessageNode(iid, tid, AbstractNode.TYPE.SEND);
                mondata.addToTrace(node);
                if(debug) System.out.println("END: " + iid + "\t" + tid);
                break;
            case 53:        // entry
                iid = getObjectMem(id);
                callsite = callsiteInfoMap.get(tid);
                if(callsite != null) {
                    node = new MethodNode(iid, tid, AbstractNode.TYPE.ENTRY, false, callsite.toString());
                    mondata.addToTrace(node);
                    if(debug) System.out.println("ENTRY: " + iid + "\t" + tid + "\t" + callsite);
                }
                else {
                    node = new MethodNode(iid, tid, AbstractNode.TYPE.ENTRY, false, Long.toString(-1));
                    mondata.addToTrace(node);
                    if(debug) System.out.println("ENTRY: " + iid + "\t" + tid + "\t" + callsite);
                }
                break;
            case 54:        // exit
                iid = getObjectMem(id);
                node = new MethodNode(iid, tid, AbstractNode.TYPE.EXIT, false, null);
                mondata.addToTrace(node);
                if(debug) System.out.println("EXIT: " + iid + "\t" + tid);
                break;
            case 55:        // lock
                iid = getObjectMem(addr);
                newNode = new LockNode(iid, tid, AbstractNode.TYPE.LOCK);
                
                lockDepNode = lockDepMap.get(iid);
                newNode.setDepNode(lockDepNode);
                node = newNode;
                mondata.addToTrace(node);
                if(debug) System.out.println("LOCK: " + iid + "\t" + tid);
                break;
            case 56:        // unlock
                iid = getObjectMem(addr);
                newNode = new LockNode(iid, tid, AbstractNode.TYPE.UNLOCK);
                lockDepMap.put(iid, newNode);
                node = newNode;
                mondata.addToTrace(node);
                if(debug) System.out.println("UNLOCK: " + iid + "\t" + tid);
                break;
            case 57:        // shared read
                line = getObjectMem(id);
                iid = getObjectMem(addr);
                node = new RWNode(line, iid, tid, AbstractNode.TYPE.READ);
                mondata.addToTrace(node);
                if(debug) System.out.println("READ: " + line + "\t" + iid + "\t" + tid);
                break;
            case 58:        // shared write
                line = getObjectMem(id);
                iid = getObjectMem(addr);
                node = new RWNode(line, iid, tid, AbstractNode.TYPE.WRITE);
                mondata.addToTrace(node);
                if(debug) System.out.println("WRITE: " + line + "\t" + iid + "\t" + tid);
                break;
            case 59:        // method call
                callsiteInfoMap.put(tid, id);
                if(debug) System.out.println("CALL: " + tid + "\t" + id);
                break;
            default:
                if(debug) System.out.println("unrecognized type: " + type);
                break;
        }
        
    }

    private static int getObjectMem(long o) {
        if(objectMemMap.get(o) == null) {
            objectMemMap.put(o, objectIndexCounter++);
        }
        return objectMemMap.get(o);
    }
}


























