package edu.hkust.clap.datastructure;


import org.antlr.grammar.v3.ANTLRParser.throwsSpec_return;

import properties.PropertyManager;
import edu.hkust.clap.organize.SootAgent4Pecan;




public class MethodNode extends AbstractNode{
	
	private boolean isPrivate; // not important
	private String mem_str;// not important	

	private String callsite ; // HK: this is important. Remeber that a calling context is a (callsite, methodsignature) pair
	private String methodSig ; // the methodsignature 
	// these information is not important in violation detection, but is important for the later Petri net construction.
	// make sure that, the context information you stored allow you to construct the Petri net without any ambiguity
	

	
	public String getMsig() {
		  return methodSig;
		}



		public String getCallsite() {
			
			return callsite;
		}
		
		
	public MethodNode(int mem, long tid, TYPE type, boolean isPrivate, String callsiteByHK)
	{
		
		super(mem,tid,type);
		this.isPrivate  =  isPrivate;
		
		callsite = callsiteByHK; 
		methodSig = ""+mem;// still used by HK for maintaining the context.
		
        
//		if(PropertyManager.noCtxtForbug)
//		{
//			this.appSTE =null;
//		}
//		else {
//			this.appSTE =process2(Thread.currentThread().getStackTrace(), msubsig);
//		}
		
			// process2(Thread.currentThread().getStackTrace(), msubsig);	
//		if(appSTE.getClassName().equals("org.exolab.jms.net.orb.RegistryImpl__Proxy") && 
//				appSTE.getMethodName().equals("lookup"))
//		{
//			
//			throw new RuntimeException();// no such method!
//		}
		
//		if(appSTE.getFileName() ==null)
//		{
//			throw new RuntimeException();
//		}
		//this.appSTE = new StackTraceElement(tmp.getClassName(), tmp.getMethodName(), tmp.getFileName(), tmp.getLineNumber());
        
	}



//	private StackTraceElement_lpxz process2(StackTraceElement[] stackTrace, String msubsig) {	   
//	    int lateIndex = 0;
//	    int hisParentIndex =0;
//
//	    for(int i=0; i<stackTrace.length; i++)// late is placed at the starting region...
//	    {
//	    	StackTraceElement ele = stackTrace[i];
//	    	if(ele.toString().startsWith("edu.hkust.clap.")||ele.toString().startsWith("java.lang.Thread.getStackTrace"))
//	    	{
//	    		continue;
//	    	}
//	    	else {
//	    		lateIndex = i;
//	    		hisParentIndex = i +1;
//				break;
//			}    	
//	    }
//	    StackTraceElement tmp =stackTrace[lateIndex];
//	    
//	    String parentFileName = "-1";
//	    int parentLineNo = -1;
//	    String parentLineInvoke = "-1";
//
//
//	    try {
//	    	// then, the child and its parent can pass the coupling testing.
//	    	// StackTraceElement parent = findFirstInstrumentedParent(stackTrace, hisParentIndex);//stackTrace[hisParentIndex];
//	 		StackTraceElement parent = null; 
//			for(int i = hisParentIndex; i < stackTrace.length ; i++)
//			{
//				parent = stackTrace[i];
//				String className = parent.getClassName();
//				if(SootAgent4Pecan.shouldInstruThis_ClassFilter(className))
//				{
//					String methodName = parent.getMethodName();
//					if(!methodName.contains("<clinit>")) //shouldInstruThisMethod(methodName)
//					{
//						parentFileName = parent.getFileName();
//				    	 parentLineNo = parent.getLineNumber();
//				    	 if(parentFileName==null) parentFileName ="-1";		
//				    	StackTraceElement beforeParent = stackTrace[i-1];
//				    	parentLineInvoke = SootAgent4Pecan.getFullMethodName(beforeParent);
//				    	
////				    	if(parentLineInvoke.equals("java.io.ObjectOutputStream.<init>")
////				    			&& parent.getClassName().equals("org.exolab.jms.net.multiplexer.Channel")
////				    			&& parent.getMethodName().equals("invoke") &&
////				    			tmp.getClassName().equals("org.exolab.jms.net.multiplexer.MultiplexOutputStream")
////				    			&& tmp.getMethodName().equals("write"))
////				    	{
////				    		parentLineNo ++;
////				    		parentLineNo--;
////				    	}
//						break;
//					}				
//				}				
//			}			    	
//		} catch (Exception e) {
//			e.printStackTrace();
//			
//		}    
//		
//	   return new StackTraceElement_lpxz(tmp.getClassName(), tmp.getMethodName(),parentFileName, parentLineNo, parentLineInvoke, msubsig);
//	   // the line corresponds to its children's callsite!
//	
//	}



//	private List<StackTraceElement> reform(StackTraceElement[] stackTrace) {
//	    List<StackTraceElement> stacks = new  ArrayList<StackTraceElement>();
//	    int lateIndex = 0;
//
//	    for(int i=0; i<stackTrace.length; i++)// late is placed at the starting region...
//	    {
//	    	StackTraceElement ele = stackTrace[i];
//	    	if(ele.toString().startsWith("edu.hkust.clap.")||ele.toString().startsWith("java.lang.Thread.getStackTrace"))
//	    	{
//	    		continue;
//	    	}
//	    	else {
//	    		lateIndex = i;
//				break;
//			}    	
//	    }
//	    int earlyIndex = stackTrace.length-1;
//	    for(int j = earlyIndex ; j>=lateIndex; j--)
//	    {
//	    	StackTraceElement ele = stackTrace[j];
//	    	if(ele.toString().startsWith("edu.hkust.clap.") || ele.toString().startsWith("java.lang.reflect.") || ele.toString().startsWith("sun.reflect."))
//		    {
//	    		continue;
//	    	}
//	    	else {
//	    		earlyIndex = j;
//				break;
//			}
//	    }
//	//    System.out.println("" );
//	    for(int k =earlyIndex; k>=lateIndex; k--)// from ending Index to starting index
//	    {		
//	    	if(stackTrace[k].toString().contains("<clinit>"))
//	    	{
//	    		// do not add them as they are not important (instrumented)
//	    		// and there are no exiting methods working for them.
//	    	}
//	    	else {
//	    		stacks.add(stackTrace[k]);
//			}
//	    	
//	    }
//	//    System.out.println("" );
//	   
//		return stacks;
//	}

	private StackTraceElement findFirstInstrumentedParent(
			StackTraceElement[] stackTrace, int hisParentIndex) {
		StackTraceElement tmp = null; 
		for(int i = hisParentIndex; i < stackTrace.length ; i++)
		{
			tmp = stackTrace[i];
			String className = tmp.getClassName();
			if(SootAgent4Pecan.shouldInstruThis_ClassFilter(className))		
			{
				String methodName = tmp.getMethodName();
				if(!methodName.contains("<clinit>")) //shouldInstruThisMethod(methodName)
				{
					return tmp;// the first satisfying the criteria is returned
				}				
			}
			
		}
		
		return tmp;
	}
	 



	public void setMemString(String mem_str)
	{
		this.mem_str = mem_str;
	}
	public String getMemString()
	{
		if(mem_str!=null)
			return mem_str;
		else
			return "";
	}

	public String toString()
	{
		if(mem_str==null)
			return ID+" "+mem+" "+" "+tid+" "+type;
		else
			return ID+" "+mem_str+" "+" "+tid+" "+type;
	}






}