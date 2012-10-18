package edu.hkust.clap.lpxz.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import properties.PropertyManager;



import soot.Body;
import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.Stmt;

import edu.hkust.clap.datastructure.RWNode;

import edu.hkust.clap.lpxz.asmStack.PreciseStackGetter;
import edu.hkust.clap.lpxz.jvmStack.CatchStackLPXZ;
import edu.hkust.clap.lpxz.jvmStack.FullSTEManipulater;
import edu.hkust.clap.organize.SootAgent4Pecan;

public class ContextValueManager {

	public static List<String> ctxts = new  ArrayList<String>();
	
	static  HashMap<Thread, Vector> cacheLine  = new  HashMap<Thread, Vector>();// last ctxt vector 4 each thread
	
	private static int getValue(String  tmp) {
//		String tmp = ste.toString();
		
		if(ctxts.contains(tmp))
		{
			return ctxts.indexOf(tmp);
		}else {
			ctxts.add(tmp);
			return  ctxts.indexOf(tmp);
		}	
		
	}

	public static Vector translate2ClassNameV(Vector intv)
	{
		Vector tmpVector = new  Vector();
		for(int i=0; i<intv.size(); i++)
		{
			Integer integer = (Integer)intv.get(i); 
			String ctxtString = ctxts.get(integer.intValue()); 	
			tmpVector.add(FullSTEManipulater.getClass(ctxtString));
			
		}
		return tmpVector;
	}
	
	public static Vector translate2StringV(Vector intv)
	{
		Vector tmpVector = new  Vector();
		for(int i=0; i<intv.size(); i++)
		{
			Integer integer = (Integer)intv.get(i); 
			String ctxtString = ctxts.get(integer.intValue()); 
			tmpVector.add(ctxtString);
		}
		return tmpVector;
	}


//	public static void  invalidifyContextValueCache(Thread currentThread)
//	{
//		cacheLine.put(currentThread, null);// the entry of a new method will invalidify it. 
//	}
	
	public static long base = 10000;// at most 10000 methods for an application.
	public static long vector2long(Vector vv)
	{ // 325
		long tmp =0;
		for(int i=0; i<vv.size(); i++)
		{
			int value  = ((Integer)vv.get(i)).intValue();
			if(tmp *base + value> Long.MAX_VALUE)
			{
				return tmp;// cut from the middle!!! at least we preserve the bottom entries of the vector.
			}
			tmp=tmp *base;
			tmp+=value;		
			
		}
		return tmp;		
	}
	
	public static Vector long2Vector(long value)
	{
		Vector<Integer> vv = new Vector<Integer>();
		while(value!=0)
		{
			int yushu =(int)(value % base);
			vv.add(0, yushu);//addFirst
			value= value/base;			
		}
		return vv;
	}
	
	public static void main(String[] args)
	{
	Vector<Integer> vv=	long2Vector(325);
	for(int i=0; i<vv.size(); i++)
	{
		System.out.println(vv.get(i));
	}
//		Vector<Integer> vv = new Vector<Integer>();
//		vv.add(3);
//		vv.add(2);
//		vv.add(5);
//		System.out.println(vector2long(vv));
	}
	//static Vector tmp = new Vector();
	public static Vector assignContextValue(Thread currentThread, String msig) {
		// msig is used as an oracle for testing the correctnessS
		
		//==========optimization code: reuse the same context vectors for those events in the same method!
//		Vector lastVector = cacheLine.get(currentThread);		
//		if(lastVector!= null )
//		{			
//			
//			return lastVector;//the shared accesses in the same method will reuse it
//		}
//		else 
		{
			
			Vector tmp = new Vector();
			//StackTraceElement[] stes = currentThread.getStackTrace();
			List<String> fullstes =PreciseStackGetter.getFullStackTrace_lpxz(msig);
			for(int i=0;i<fullstes.size(); i++)
			{
				String fullste = fullstes.get(i);				
				tmp.add(getValue(fullste));//add to the last		
			}	
			return tmp;
			
//			   [java] spec.jbb.infra.Util.TransactionLogBuffer.putChar(char,int,int)(TransactionLogBuffer.java)
//			     [java] spec.jbb.NewOrderTransaction.setupInitLog()(NewOrderTransaction.java:147)
//			     [java] spec.jbb.NewOrderTransaction.<init>(spec.jbb.Company,short)(NewOrderTransaction.java:104)
//			     [java] spec.jbb.Company.loadInitialOrders()(Company.java:657)
//			     [java] spec.jbb.Company.primeWithDummyData(short,int)(Company.java:185)
//			     [java] spec.jbb.JBBmain.increaseNumWarehouses(int,int,int)(JBBmain.java:295)
//			     [java] spec.jbb.JBBmain.runWarehouse(int,int,float)(JBBmain.java:306)
//			     [java] spec.jbb.JBBmain.doIt()(JBBmain.java:260)
//			     [java] spec.jbb.JBBmain.main(java.lang.String[])(JBBmain.java:713)
			     
//			cacheLine.put(currentThread, tmp);
			
			
			
		}
	}



	private static boolean identical(Vector tmp, Vector lastVector) {
	    if(tmp.size() != lastVector.size()) return false;
	    for(int i=0; i< tmp.size() ; i++)
	    {
	    	if(tmp.get(i)!=lastVector.get(i))
	    	{
	    		return false;
	    	}
	    }
		// still not return-false? you are true
		return true;
	}



	
	public static boolean considerIt(String ste) {
	    String classname  = FullSTEManipulater.getClass(ste);
	    
	    if(PropertyManager.projectname.equals("openjms"))
	    {
	    	if(classname.startsWith("org.apache"))
	    		return false;
	    }
	    
	    if(SootAgent4Pecan.shouldInstruThis_ClassFilter(classname))
	    {
	    	
	    	String methodName = FullSTEManipulater.getMethod(ste);
	    	if(!methodName.contains("<clinit>"))
	    	{
	    		return true;
	    	}
	    }
	    
	    
		
		return false;
	}
	public static Local jvmInstance = Jimple.v().newLocal("placeholder", RefType.v("java.lang.Object"));
	static Vector tmpVector = new  Vector();
	
	// [c1 c2 c3] contains less information than ste1 ste2 ste 3 (we do not know which methods are really invoked by each c, virtual func)
	// [M1 M2 M3] contains less information than ste1, ste2 ste3 (we do not know which call sites are used. )
	//Instead, [c0,M0, c1, M1, c2, M2, ... cn, Mn] is the wanted
	// M is the string msig, which can be used to load the method easily. c is the stmt, 
	
	// internal: get [Mn, cn, ... M0,c0], then reverse.
	//rename!
	public static Vector<MethodItsCallSiteLineTuple> translate2MCpairV(Vector intv) {
		//===========get string vector
		tmpVector.clear();
		for(int i=0; i<intv.size(); i++)
		{
			Integer integer = (Integer)intv.get(i); 
			String ctxtString = ctxts.get(integer.intValue()); 
			tmpVector.add(ctxtString);
		}
		// =========== get stmt vector
		Vector<MethodItsCallSiteLineTuple> toret = new Vector<MethodItsCallSiteLineTuple>();	
			for(int i=0; i<= tmpVector.size()-2; i++)// 0 is the last called |tmpVector.size()-1 is the run(), it is possble size==1
			{
				String curSTE = (String) tmpVector.get(i);
				String secSTE = (String) tmpVector.get(i+1);
			    SootMethod curMethod = SootAgent4Pecan.getMethod(FullSTEManipulater.getClass(curSTE), FullSTEManipulater.getMethodSig(curSTE));
			    SootMethod secMethod = SootAgent4Pecan.getMethod(FullSTEManipulater.getClass(secSTE), FullSTEManipulater.getMethodSig(secSTE));
			    if(secMethod ==null) 
			    {
			    	System.out.println("xxx");
			    	SootAgent4Pecan.getMethod(FullSTEManipulater.getClass(secSTE), FullSTEManipulater.getMethodSig(secSTE));
			    }
			    Unit unit = SootAgent4Pecan.getInvokeUnit(secMethod, FullSTEManipulater.getInvokeLine(secSTE), curMethod);
			    MethodItsCallSiteLineTuple pair  = new MethodItsCallSiteLineTuple(curMethod.getSignature(), unit.toString(), FullSTEManipulater.getInvokeLine(secSTE));
			    toret.add(pair);
		    	
			}	
			// for handling the size-1 index, including the case size ==1, which is missed by the abvoe for loop.
			String curSTE =(String) tmpVector.get(tmpVector.size() -1);// the last one
			SootMethod curMethod = SootAgent4Pecan.getMethod(FullSTEManipulater.getClass(curSTE), FullSTEManipulater.getMethodSig(curSTE));			
    		Stmt tmpStmt =method2UniqueFakeStmt.get(curMethod);
    		if(tmpStmt ==null)// only when no one is present, uniqueness
    		{
    			InvokeExpr ie  =null;
				if(curMethod.isStatic())
				{
					 ie  =Jimple.v().newStaticInvokeExpr( curMethod.makeRef());// otherwise, wrong staticness
				}
				else {
					 ie  =Jimple.v().newSpecialInvokeExpr(jvmInstance, curMethod.makeRef());
				}
				tmpStmt = Jimple.v().newInvokeStmt(ie);
				method2UniqueFakeStmt.put(curMethod, tmpStmt);						
    		}					
    		MethodItsCallSiteLineTuple pair = new MethodItsCallSiteLineTuple(curMethod.getSignature(), tmpStmt.toString(), -1 );// called by jvm
			toret.add(pair);	
		return toret;
	}
	
	public static void getSTEVector(RWNode pNode, List pvList) {
		Vector intv =pNode.getContext_VectorEncoding();
		//===========get string vector
		
		if(intv ==null) return;
		for(int i=0; i<intv.size(); i++)
		{
			
			Integer integer = (Integer)intv.get(i); 
			
			String ctxtString = ctxts.get(integer.intValue()); 
			pvList.add(ctxtString);
		}
	
	  
	}
	
	static HashMap<SootMethod, Stmt> method2UniqueFakeStmt = new  HashMap<SootMethod, Stmt>();

	
	public static void getMCPairV(RWNode pnode, List pv_list) {
		if(PropertyManager.useasmStack)
		{
			//0 is the last called
			Vector pv =pnode.getContext_VectorEncoding();
			
			
			Vector pv_mcpair = ContextValueManager.translate2MCpairV(pv);	
			
			// to stmt context directly, then, everything will be simple
			pv_list.clear();
		
			
			pv_list.addAll(pv_mcpair);
			
			// in jvmStack, the order is: 0 methodA 1 run()
			// the returned list is, 0 run 1 methodA. so, reverse!!
		   Collections.reverse(pv_list);
		   
		   // the following is for human readability, remember that we will print some readable info for each RWnode at last.
		   pnode.setMCPairList_deepClone(pv_list);
		}
		else if(PropertyManager.usePostStack){
			pv_list.addAll(pnode.getMCPairList()); // 
		}else {
			throw new RuntimeException("seems you are not providing any contexts");
		}
		
		
	  
	}





}
