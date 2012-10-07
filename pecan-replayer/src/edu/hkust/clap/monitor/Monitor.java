package edu.hkust.clap.monitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.GZIPOutputStream;

import edu.hkust.clap.Parameters;
import edu.hkust.clap.MonitorThread;
import edu.hkust.clap.Util;
import edu.hkust.clap.datastructure.AbstractNode;
import edu.hkust.clap.replayer.ReplayControl;
import edu.hkust.clap.tracer.TraceReader;

public class Monitor 
{

	public static void crashed(Throwable crashedException) {
		System.exit(0);
	}
    public static void startRunThreadBefore(Thread t, long threadId)
    {	

	}
	public static void threadStartRun(long threadId)
	{
	}
	public static void threadExitRun(long threadId)
	{
	}
    public static void joinRunThreadAfter(Thread t,long threadId)
    {
    }
    
    public static void waitAfter(int iid, long threadId)
    {	
    }
    public static void notifyBefore(int iid, long threadId)
    {	
    }
    public static void waitAfter(Object o,int iid, long id)
    {	
    }
    public static void notifyBefore(Object o,int iid, long id)
    {	
    }
    public static void notifyAllBefore(Object o,int iid, long id)
    {	
    }
    public static void enterMonitorAfter(Object o, int iid,long id) {
    }
    public static void exitMonitorBefore(Object o,int iid,long id) {
    }
    public static void enterMonitorAfter(int iid,long id) 
    {
  
    }
    public static void exitMonitorBefore(int iid,long id) 
    {

    }
    public static void enterSyncMethodBefore(int iid,long threadId) 
    {
    }
    public static void exitSyncMethodAfter(int iid,long threadId) 
    {
    	
    }
    
    public static void notifyAllBefore(int iid, long threadId)
    {	

    }
	
	public static boolean youMayCrash(String methodName, Object objects[],
			String types[],long threadId) {
		return true;
	}
	public static void youAreOK() 
	{
    }
//    public static void enterNonPrivateMethodAfter(long threadId) 
//    {
//    }
//    public static void exitNonPrivateMethodBefore(long threadId) 
//    {
//    }
    public static void enterNonPrivateMethodAfter(int methoId,long threadId, String msig) 
    {
    }
    public static void exitNonPrivateMethodBefore(int methoId, long threadId, String msig) 
    {
    }
    public static void enterPrivateMethodAfter(int methoId, long threadId,String msig) 
    {
    }
    public static void exitPrivateMethodBefore(int methoId, long threadId, String msig) 
    {
    }
    public static void waitBefore(int iid, long id)
    {
    }
    
    
    public static void readBefore(int iid,long id) {		
    }
    public static void writeBefore(int iid,long id) {
    }
    
	public static void mainThreadStartRun(long threadId, String methodName, String[] args)
	{	
		Runtime.getRuntime().addShutdownHook(new MonitorThread());
	}

//
//    public static void readBeforeStatic(int iid,long tid) {
//    	accessSPE(iid,tid);
//    }
//    public static void writeBeforeStatic(int iid,long tid) {
//    	accessSPE(iid,tid);
//    }
//    public static void readBeforeInstance(Object o, int iid, long threadId)
//    {    	
//    	accessSPE(iid,threadId);
//    }
//    public static void writeBeforeInstance(Object o, int iid,long threadId) 
//    {
//    	accessSPE(iid,threadId);
//    }
    public static void readBeforeInstance(Object o, int iid,long tid, int line, String msig, String jcode) {	
    	accessSPE(iid,tid,line);
    }
    public static void writeBeforeInstance(Object o, int iid,long tid, int line, String msig, String jcode) {
    	accessSPE(iid,tid,line);
    }
    public static void readBeforeStatic(int iid,long tid, int line,String msig, String jcode) {	
    	accessSPE(iid,tid,line);
    }
    public static void writeBeforeStatic(int iid,long tid, int line,String msig, String jcode) {
    	accessSPE(iid,tid,line);
    }
	public static void accessSPE(int iid, long tid)
	{
		accessSPE(iid,tid,0);

	}
	public static void accessSPE(int iid, long tid, int line)
	{

		ReplayControl.check(line);

	}
	public synchronized static void caredByClap(String name, Object o)
	{
		MonitorThread.saveObject(name,o);
	}
}
