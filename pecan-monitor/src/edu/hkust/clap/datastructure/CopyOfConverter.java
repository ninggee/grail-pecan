package edu.hkust.clap.datastructure;

//package edu.hkust.clap.converter;

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

public class CopyOfConverter {
    private static boolean debug = false;

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
        try {
            while (true) {
                byte type;
                long tid, id, addr, size;
                type = in.readByte();
                in.skipBytes(7);
                tid = in.readLong();
                id = in.readLong();
                addr = in.readLong();
                size = in.readByte();
                in.skipBytes(7);
                appendMonitorData(type, tid, id, addr, size);
            }
        } catch (EOFException e) {
            if(debug) System.out.println("EOF");
        } catch (IOException e) {
            System.out.println("IOExeption");
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
    public static void appendMonitorData(byte type, long tid, long id, long addr, long size) {
        AbstractNode node = null;
        LockNode newNode, lockDepNode;
        int iid, line;
        long callsite;
        switch(type) {
            case 49:        // create
                // is this right way to generate iid?
                iid = getObjectMem(tid);
                node = new MessageNode(iid, tid, AbstractNode.TYPE.SEND);
                if(debug) System.out.println("CREATE: " + iid + "\t" + tid);
                mondata.addToTrace(node);
                break;
            case 50:        // join
                iid = getObjectMem(tid);
                node = new MessageNode(iid, tid, AbstractNode.TYPE.RECEIVE);
                if(debug) System.out.println("JOIN: " + iid + "\t" + tid);
                mondata.addToTrace(node);
                break;
            case 51:        // start
                iid = getObjectMem(tid);
                node = new MessageNode(iid, tid, AbstractNode.TYPE.RECEIVE);
                if(debug) System.out.println("START: " + iid + "\t" + tid);
                break;
            case 52:        // end
                iid = getObjectMem(tid);
                node = new MessageNode(iid, tid, AbstractNode.TYPE.SEND);
                if(debug) System.out.println("END: " + iid + "\t" + tid);
                mondata.addToTrace(node);
                break;
            case 53:        // entry
                iid = getObjectMem(id);
                callsite = callsiteInfoMap.get(tid);
                node = new MethodNode(iid, tid, AbstractNode.TYPE.ENTRY, false, Long.toString(callsite));
                if(debug) System.out.println("ENTRY: " + iid + "\t" + tid + "\t" + callsite);
                mondata.addToTrace(node);
                break;
            case 54:        // exit
                iid = getObjectMem(id);
                node = new MethodNode(iid, tid, AbstractNode.TYPE.EXIT, false, null);
                if(debug) System.out.println("EXIT: " + iid + "\t" + tid);
                mondata.addToTrace(node);
                break;
            case 55:        // lock
                iid = getObjectMem(addr);
                newNode = new LockNode(iid, tid, AbstractNode.TYPE.LOCK);
                lockDepNode = lockDepMap.get(iid);
                newNode.setDepNode(lockDepNode);
                node = newNode;
                if(debug) System.out.println("LOCK: " + iid + "\t" + tid);
                mondata.addToTrace(node);
                break;
            case 56:        // unlock
                iid = getObjectMem(addr);
                newNode = new LockNode(iid, tid, AbstractNode.TYPE.UNLOCK);
                lockDepMap.put(iid, newNode);
                node = newNode;
                if(debug) System.out.println("UNLOCK: " + iid + "\t" + tid);
                mondata.addToTrace(node);
                break;
            case 57:        // shared read
                line = getObjectMem(id);
                iid = getObjectMem(addr);
                node = new RWNode(line, iid, tid, AbstractNode.TYPE.READ);
                if(debug) System.out.println("READ: " + line + "\t" + iid + "\t" + tid);
                mondata.addToTrace(node);
                break;
            case 58:        // shared write
                line = getObjectMem(id);
                iid = getObjectMem(addr);
                node = new RWNode(line, iid, tid, AbstractNode.TYPE.WRITE);
                if(debug) System.out.println("WRITE: " + line + "\t" + iid + "\t" + tid);
                mondata.addToTrace(node);
                break;
            case 59:        // method call
                callsiteInfoMap.put(tid, id);
                if(debug) System.out.println("CALL: " + tid + "\t" + id);
                break;
            default:
                if(debug) System.out.println("unrecognized type: " + type);
                break;
        }
//        mondata.addToTrace(node);
    }

    private static int getObjectMem(long o) {
        if(objectMemMap.get(o) == null) {
            objectMemMap.put(o, objectIndexCounter++);
        }
        return objectMemMap.get(o);
    }
}


























