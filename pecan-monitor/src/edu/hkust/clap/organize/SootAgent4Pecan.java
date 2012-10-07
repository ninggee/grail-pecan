package edu.hkust.clap.organize;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import properties.PropertyManager;
import soot.Body;
import soot.G;
import soot.PhaseOptions;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;
import soot.jimple.spark.SparkTransformer;
import soot.options.Options;
import soot.tagkit.Host;
import soot.tagkit.LineNumberTag;
import soot.tagkit.SourceLineNumberTag;
import soot.tagkit.SourceLnPosTag;

import edu.hkust.clap.datastructure.RWNode;

import edu.hkust.clap.engine.Pattern;
import edu.hkust.clap.engine.PatternI;
import edu.hkust.clap.engine.TypeOnePattern;
import edu.hkust.clap.engine.TypeTwoPattern;
import edu.hkust.clap.lpxz.context.ContextValueManager;
import edu.hkust.clap.lpxz.context.MethodItsCallSiteLineTuple;

import edu.hkust.clap.lpxz.jvmStack.ConvertSootMsig2FullSTEMsig;
import edu.hkust.clap.lpxz.jvmStack.FullSTEManipulater;

// with the help of Msig stealed from the jvm internal, everything turns to be simple
//  we do not need the snapshot funciton of the string stack. we just reference to one stack if necessary.
// btw, we use the int stack
public class SootAgent4Pecan {
//private static final int err_threshold = 3;// sometimes, soot gives an approximated lineNO, or the jvm, but the error distance is always <=3.
	// if it does not work, goto the pecan-instrumentor.jar for soot version 2
	
public static String[] unInstruClasses = {
    "org.apache.log4j.",
	"jrockit.",
	"java.",
	"javax.",
	"xjava.",
	"COM.",
	"com.",
	"cryptix.",
	"sun.",
	"sunw.",
	"junit.",
	"org.junit.",
	"org.xmlpull.",
	"edu.hkust.clap.",
	// the following are copied frpm the transforming options. (commandLine)
	"org.apache.commons.logging.",// annoying, ban it
	"org.apache.xalan.",
	"org.apache.xpath.",
	"org.springframework.",
	"org.jboss.",
	"jrockit.",
	"edu.",				
	"checkers.",
	"org.codehaus.spice.jndikit.",
	"EDU.oswego.cs.dl.util.concurrent.WaiterPreferenceSemaphore",
	"soot.",
	"aTSE.",
	"pldi.",
	"popl.", 
	"beaver.",
	"org.jgrapht",
	"ca.pfv.spmf.","japa.parser.", "polyglot."
	
	// org.w3c. is the including option			
};
public static boolean shouldInstruThis_ClassFilter(String scname)
{
	for(int k=0;k<unInstruClasses.length;k++)
	{
		if(scname.startsWith(unInstruClasses[k]))
		{
			return false;
		}
	}
	
	return true;
}
//	public static String getClassNameFromMSig(String msig)
//	{
//		//<org.exolab.jms.net.multiplexer.Multiplexer: int getNextChannelId()> 
//		int lbrace =msig.indexOf('<');
//		int maohao = msig.indexOf(':');
//		return msig.substring(lbrace+1, maohao);		
//	}
	
//	public static String getMsigFromStackElem(
//			List<StackTraceElement_lpxz> pctxts, int lastCommonIndex) {
//		// lastCommonIndex is actually the i-1
//		StackTraceElement_lpxz ele = pctxts.get(lastCommonIndex);
//		String sig = ele.getmsig();		
//		return sig;
//	}

	
	

	public static int getLineNum(Host h) {
        if (h.hasTag("LineNumberTag")) {
            return ((LineNumberTag) h.getTag("LineNumberTag")).getLineNumber();
        }
        if (h.hasTag("SourceLineNumberTag")) {
            return ((SourceLineNumberTag) h.getTag("SourceLineNumberTag")).getLineNumber();
        }
        if (h.hasTag("SourceLnPosTag")) {
            return ((SourceLnPosTag) h.getTag("SourceLnPosTag")).startLn();
        }
        return -1;
    }
	
	// TO remove!
	// this function is very important, make it correct!
//	public static Stmt search4CallSiteStmt(String secondClass,
//			String secondMethod, int eleLineNO, String eleLineNoInvoke) {
//       // mName is not important, we care about the eleLineNoInvoke only.
//		// mName is not necessarily the same as the callsite invocation.
//		// YOU need to trust eleLineNoInvoke!absolutely correct.
//		// It is taken from the jdk stacktraceelement.
//		// the lineNO may not be quite accurate, for example, 137 maybe taken as 138.
//		
////		boolean check = couplingCheck(secondClass, filename);
////		if(!check)
////		{
////			System.err.println("seems not matching between the caller and the callee!!");
////			
////		}
////		if(check)// the second one really matches the caller, instead of the far-away relative
//		{
//			//
//			    String argsOfToyW = "-app -p jb use-original-names:false -f J -pp -cp /home/lpxz/java_standard/jre/lib/jsse.jar:"+
//			    PropertyManager.origAnalyzedFolder+" " + secondClass; // java.lang.Math
//				String interString = argsOfToyW;
//				String[] finalArgs = interString.split(" ");
//		        soot.Main.v().processCmdLine(finalArgs);
//		        Options.v().set_keep_line_number(true);			
//				
//				List excludesList= new ArrayList();
//				excludesList.add("jrockit.");
//				excludesList.add("com.bea.jrockit");
//				excludesList.add("sun.");
//				Options.v().set_exclude(excludesList);
//			
//				
//			    SootClass secondclass = Scene.v().loadClassAndSupport(secondClass);
//			   
//			    secondclass.setApplicationClass();
//			    
//			    Scene.v().loadNecessaryClasses();
//			    
//			    
////			    Scene.v().loadNecessaryClasses();
//			    List<SootMethod> sms = secondclass.getMethods();
//			    List<SootMethod> hit = new ArrayList<SootMethod>();
//			    for(SootMethod sm : sms)
//			    {
//			    	if(sm.getName().equals(secondMethod))
//			    	{
//			    		hit.add(sm);
//			    	}
//			    }
//			    boolean theParent = false;
//			    int recentMethodEntry = Integer.MAX_VALUE;
//			    SootMethod recentMethod  = null;
//			    for(SootMethod sm:hit)
//			    {
//			    	sm.retrieveActiveBody(); 		    	
//			    	
//			    	if(sm.hasActiveBody())
//			    	{
//			    		Body bb = sm.getActiveBody();
//			    	    Stmt stmt  =(Stmt) bb.getUnits().getLast();
//			    	    int lineNO = getLineNum(stmt);
//			    	    if(eleLineNO <=lineNO)
//			    	    {
//			    	    	// lineNO is valid for candidates
//			    	    	if(lineNO<=recentMethodEntry)// this one is more recent.
//			    	    	{
//			    	    		recentMethodEntry = lineNO;
//			    	    		recentMethod = sm;
//			    	    	}
//			    	    }
//			    	    
//			    	}
//			    }
//			    if(recentMethod==null) 
//			    	{
//			    	G.reset();
//			    	return null;
//			    	}
//			    Body bb =recentMethod.getActiveBody();// recentMethod!=NULL
//
//			    HashSet<Unit> possibleUnits  = new HashSet<Unit>();
//	    		Iterator<Unit> it =bb.getUnits().iterator();
//	    	    while (it.hasNext()) {
//					Stmt unit = (Stmt) it.next();
//					if(unit.containsInvokeExpr())
//					{
//						SootMethod invokeM = unit.getInvokeExpr().getMethod();
//						String invokeMStr=invokeM.getName();// not necessarily the same, 
//						// one is Impl.loopup, one is soot-based: interface.lookup!							
//
//						if(eleLineNoInvoke.contains(invokeMStr))// just use contains for simplicity
//						{
//							if(getLineNum(unit)==eleLineNO)// exactly the same??
//							{
//								// hit 
//								G.reset();
//								return unit;
//							}
//							else {
//								possibleUnits.add(unit);
//							}							
//						}
//						else if(eleLineNoInvoke.contains("<clinit>"))
//						{
//							// special case, there is no statement there containing the clinint,
//							//just find the calling statements of the static methods of the class!,
//							//if many, put them into the possibleUnits for further line-based pruning.
//							int sep =eleLineNoInvoke.lastIndexOf('.');
//							String elelineNoinvoke_class = eleLineNoInvoke.substring(0, sep);
//							if(invokeM.getDeclaringClass().getName().equals(elelineNoinvoke_class))
//							{
//								// hit
//								if(getLineNum(unit)==eleLineNO)// exactly the same??
//								{
//									// hit 
//									G.reset();
//									return unit;
//								}
//								else {
//									possibleUnits.add(unit);
//								}	
//							}
//							
//								
//						}
//						
//						
//						
//					}
//				}
//	    	    if(possibleUnits.size() !=0)
//	    	    {
//	    	    	// still not return the signature yet, error_tolerance of the line NO!
//	    	    	G.reset();
//	    	    	Unit best =null;
//	    	    	int bestDis= Integer.MAX_VALUE;
//	    	    	for(Unit possible:possibleUnits)
//	    	    	{
//	    	    		int itsLine = getLineNum(possible);
//	    	    		if(Math.abs(itsLine-eleLineNO) < bestDis)
//	    	    		{
//	    	    			best = possible;
//	    	    			bestDis= Math.abs(itsLine-eleLineNO);
//	    	    		}	    	    		
//	    	    	}
//	    	    	if(bestDis <= err_threshold)
//	    	    	{
//	    	    		return (Stmt)best;
//	    	    	}
//	    	    	else {
//						throw new RuntimeException("double checking " + bestDis);
//					}
////	    	    	System.out.println("I want " + eleLineNO);
////	    	    	for(Unit possible:possibleUnits)
////	    	    	{
////	    	    		System.out.println("but one possible is :" + getLineNum(possible));
////	    	    		
////	    	    	}
////	    	    	System.out.println();
//	    	    	
////	    	    	throw new RuntimeException("check please!line no is not exactly the same?");
//	    	    	
//	    	    }
//			    
//
//		}
//		
//		G.reset();
//		return null;
//	}
	
	
	static Set<Stmt> likelyinvokes = new  HashSet<Stmt>();
	
	public static Unit getInvokeUnit(SootMethod secMethod, int invokeLine, SootMethod curMethod) {
		// curMethod is used to find the closest jimple invoke stmt. just an improvement for human understanding
		secMethod.retrieveActiveBody(); 	
		Body bb =secMethod.getActiveBody();// recentMethod!=NULL
		
		    HashSet<Unit> possibleUnits  = new HashSet<Unit>();
 		Iterator<Unit> it =bb.getUnits().iterator();
 		likelyinvokes.clear();
 	       while (it.hasNext()) {
				Stmt unit = (Stmt) it.next();
				if(getLineNum(unit)==invokeLine)// not necessarily the right unit, it provides only an anchor with which, we know how to fix the bugs.
				{
					likelyinvokes.add(unit);					
				}
			}
 	       
 	       for(Stmt  stmt : likelyinvokes)
 	       {
 	    	   if(stmt.containsInvokeExpr())
 	    	   {
 	    		  if( stmt.getInvokeExpr().getMethod().getSignature().equals(curMethod.getSignature()))
 	    		  {
 	    			  return stmt;
 	    		   }
 	    	   }
 	       }
 	       
 	       for(Stmt  stmt : likelyinvokes) // no exact invoke stmts due to intermediate reflection calls.
 	       {
 	    	   if(stmt.containsInvokeExpr())
 	    	   {
 	    		  return stmt;
 	    	   }
 	       }
 	       
 	       for(Stmt  stmt : likelyinvokes) // no exact invoke stmts due to intermediate reflection calls.
 	       {
 	    	   throw new  RuntimeException("it should be an invoke stmt!");
 	    	   //return stmt;
 	       }
 	       
 	    return null;
 	    
	}
		
	


    // to remove:	
//	public static MethodItsCallSitePair computeCurMsigItsCS(String secondClass,
//			String secondMethod, int eleLineNO, String curClass , String curMethod) {
//       // mName is not important, we care about the eleLineNoInvoke only.
//		// mName is not necessarily the same as the callsite invocation.
//		// YOU need to trust eleLineNoInvoke!absolutely correct.
//		// It is taken from the jdk stacktraceelement.
//		// the lineNO may not be quite accurate, for example, 137 maybe taken as 138.
//		
////		boolean check = couplingCheck(secondClass, filename);
////		if(!check)
////		{
////			System.err.println("seems not matching between the caller and the callee!!");
////			
////		}
////		if(check)// the second one really matches the caller, instead of the far-away relative
//		{
//			//
////			    String argsOfToyW = "-app -p jb use-original-names:false -f J -pp -cp /home/lpxz/java_standard/jre/lib/jsse.jar:"+
////			    PropertyManager.origAnalyzedFolder+" " + secondClass; // java.lang.Math
////				String interString = argsOfToyW;
////				String[] finalArgs = interString.split(" ");
////		        soot.Main.v().processCmdLine(finalArgs);
////		        Options.v().set_keep_line_number(true);			
////				
////				List excludesList= new ArrayList();
////				excludesList.add("jrockit.");
////				excludesList.add("com.bea.jrockit");
////				excludesList.add("sun.");
////				Options.v().set_exclude(excludesList);
//			
//				
//			    SootClass secondclass = Scene.v().loadClassAndSupport(secondClass);
//			   
//			    secondclass.setApplicationClass();
//			    
//			    Scene.v().loadNecessaryClasses();
//			    
//			    
////			    Scene.v().loadNecessaryClasses();
//			    List<SootMethod> sms = secondclass.getMethods();
//			    List<SootMethod> hit = new ArrayList<SootMethod>();
//			    for(SootMethod sm : sms)
//			    {
//			    	if(sm.getName().equals(secondMethod))
//			    	{
//			    		hit.add(sm);
//			    	}
//			    }
//			    boolean theParent = false;
//			    int recentMethodEntry = Integer.MAX_VALUE;
//			    SootMethod recentMethod  = null;
//			    for(SootMethod sm:hit)
//			    {
//			    	sm.retrieveActiveBody(); 		    	
//			    	
//			    	if(sm.hasActiveBody())
//			    	{
//			    		Body bb = sm.getActiveBody();
//			    	    Stmt stmt  =(Stmt) bb.getUnits().getLast();
//			    	    int lineNO = getLineNum(stmt);
//			    	    if(eleLineNO <=lineNO)
//			    	    {
//			    	    	// lineNO is valid for candidates
//			    	    	if(lineNO<=recentMethodEntry)// this one is more recent.
//			    	    	{
//			    	    		recentMethodEntry = lineNO;
//			    	    		recentMethod = sm;
//			    	    	}
//			    	    }
//			    	    
//			    	}
//			    }
//			    if(recentMethod==null) 
//			    	{
//			   // 	G.reset();
//			    	return null;
//			    	}
//			    Body bb =recentMethod.getActiveBody();// recentMethod!=NULL
//
//			    HashSet<Unit> possibleUnits  = new HashSet<Unit>();
//	    		Iterator<Unit> it =bb.getUnits().iterator();
//	    	    while (it.hasNext()) {
//					Stmt unit = (Stmt) it.next();
//					if(unit.containsInvokeExpr())
//					{
//						SootMethod invokeM = unit.getInvokeExpr().getMethod();
//						String invokeMStr=invokeM.getName();// not necessarily the same, 
//						// one is Impl.loopup, one is soot-based: interface.lookup!							
//
//						if(curMethod.equals(invokeMStr))// just use contains for simplicity
//						{
//							if(getLineNum(unit)==eleLineNO)// exactly the same??
//							{
//								// hit 
//								//G.reset();
//								return new MethodItsCallSitePair(invokeM.getSignature(), unit.toString());								
//							}
//							else {
//								
//								possibleUnits.add(unit);
//							}							
//						}
//						else if(curMethod.contains("<clinit>"))
//						{
//							
//							throw new  RuntimeException("I ban it on the context recording process!");
//							// special case, there is no statement there containing the clinint,
//							//just find the calling statements of the static methods of the class!,
//							//if many, put them into the possibleUnits for further line-based pruning.
////							int sep =eleLineNoInvoke.lastIndexOf('.');
////							String elelineNoinvoke_class = eleLineNoInvoke.substring(0, sep);
////							if(invokeM.getDeclaringClass().getName().equals(elelineNoinvoke_class))
////							{
////								// hit
////								if(getLineNum(unit)==eleLineNO)// exactly the same??
////								{
////									// hit 
////									G.reset();
////									return unit;
////								}
////								else {
////									possibleUnits.add(unit);
////								}	
////							}						
//								
//						}
//						
//						
//						
//					}
//				}
//	    	    if(possibleUnits.size() !=0)
//	    	    {
//	    	    	// still not return the signature yet, error_tolerance of the line NO!
//	    	    //	G.reset();
//	    	    	Unit best =null;
//	    	    	int bestDis= Integer.MAX_VALUE;
//	    	    	for(Unit possible:possibleUnits)
//	    	    	{
//	    	    		int itsLine = getLineNum(possible);
//	    	    		if(Math.abs(itsLine-eleLineNO) < bestDis)
//	    	    		{
//	    	    			best = possible;
//	    	    			bestDis= Math.abs(itsLine-eleLineNO);
//	    	    		}	    	    		
//	    	    	}
//	    	    	if(bestDis <= err_threshold)
//	    	    	{
//	    	    		Stmt beststmt = (Stmt) best;
//	    	    		SootMethod invokeM = beststmt.getInvokeExpr().getMethod();						
//	    	    		return new MethodItsCallSitePair(invokeM.getSignature(), beststmt.toString());	    	    		
//	    	    	}
//	    	    	else {
//						throw new RuntimeException("double checking " + bestDis);
//					}
////	    	    	System.out.println("I want " + eleLineNO);
////	    	    	for(Unit possible:possibleUnits)
////	    	    	{
////	    	    		System.out.println("but one possible is :" + getLineNum(possible));
////	    	    		
////	    	    	}
////	    	    	System.out.println();
//	    	    	
////	    	    	throw new RuntimeException("check please!line no is not exactly the same?");
//	    	    	
//	    	    }
//			    
//
//		}
//		
//	//	G.reset();
//		return null;
//	}

	
//	// reuse the body stored in the secondGCS, if you search with the soot naively,
//	// you will get a unit which can not direct you to the wanted Statement.
//	// remind that soot generates new jimple unit for the same code in a different run.
////	public static Stmt search4CallSiteStmt_reuseGCS(ContextGraphMethod secondGCS, String className, String mName,
////			String filename, int eleLineNO, String eleLineNoInvoke) {
////       // mName is not important, we care about the eleLineNoInvoke only.
////		// mName is not necessarily the same as the callsite invocation.
////		// YOU need to trust eleLineNoInvoke!absolutely correct.
////		// It is taken from the jdk stacktraceelement.
////		// the lineNO may not be quite accurate, for example, 137 maybe taken as 138.
////		String secondClass = secondGCS.getClassname();
////		 
//////		boolean check = couplingCheck(secondClass, filename);
//////		if(!check)
//////		{
//////			System.err.println("seems not matching between the caller and the callee!!");
//////			
//////		}
//////		if(check)// the second one really matches the caller, instead of the far-away relative
////		{
////			    Body bb =secondGCS.getBb();// recentMethod!=NULL
////
////			    HashSet<Unit> possibleUnits  = new HashSet<Unit>();
////	    		Iterator<Unit> it =bb.getUnits().iterator();
////	    	    while (it.hasNext()) {
////					Stmt unit = (Stmt) it.next();
////					if(unit.containsInvokeExpr())
////					{
////						SootMethodRef invokeM = unit.getInvokeExpr().getMethodRef();// do not use the getMethod(), as only the methodref is present, the method is recycled together with the gc
////						String invokeMStr=invokeM.name();// not necessarily the same, 
////						// one is Impl.loopup, one is soot-based: interface.lookup!							
////
////						if(eleLineNoInvoke.contains(invokeMStr))// 
////						{
////							if(getLineNum_inGCS(unit, secondGCS)==eleLineNO)// exactly the same??
////							{
////								// hit 
////								G.reset();
////								return unit;
////							}
////							else {
////								possibleUnits.add(unit);
////							}							
////						}
////						else if(eleLineNoInvoke.contains("<clinit>"))
////						{
////							// special case, there is no statement there containing the clinint,
////							//just find the calling statements of the static methods of the class!,
////							//if many, put them into the possibleUnits for further line-based pruning.
////							int sep =eleLineNoInvoke.lastIndexOf('.');
////							String elelineNoinvoke_class = eleLineNoInvoke.substring(0, sep);
////							if(invokeM.declaringClass().getName().equals(elelineNoinvoke_class))
////							{
////								// hit
////								if(getLineNum_inGCS(unit, secondGCS)==eleLineNO)// exactly the same??
////								{
////									// hit 
////									G.reset();
////									return unit;
////								}
////								else {
////									possibleUnits.add(unit);
////								}	
////							}
////							
////								
////						}
////						
////						
////						
////					}
////				}
////	    	    if(possibleUnits.size() !=0)
////	    	    {
////	    	    	// still not return the signature yet, error_tolerance of the line NO!
////	    	    	G.reset();
////	    	    	Unit best =null;
////	    	    	int bestDis= Integer.MAX_VALUE;
////	    	    	for(Unit possible:possibleUnits)
////	    	    	{
////	    	    		int itsLine = getLineNum_inGCS((Stmt)possible, secondGCS);
////	    	    		if(Math.abs(itsLine-eleLineNO) < bestDis)
////	    	    		{
////	    	    			best = possible;
////	    	    			bestDis= Math.abs(itsLine-eleLineNO);
////	    	    		}	    	    		
////	    	    	}
////	    	    	if(bestDis <= err_threshold)
////	    	    	{
////	    	    		return (Stmt)best;
////	    	    	}
////	    	    	else {
////						throw new RuntimeException("double checking " + bestDis);
////					}
//////	    	    	System.out.println("I want " + eleLineNO);
//////	    	    	for(Unit possible:possibleUnits)
//////	    	    	{
//////	    	    		System.out.println("but one possible is :" + getLineNum(possible));
//////	    	    		
//////	    	    	}
//////	    	    	System.out.println();
////	    	    	
//////	    	    	throw new RuntimeException("check please!line no is not exactly the same?");
////	    	    	
////	    	    }
////			    
////
////		}
////		
////		G.reset();
////		return null;
////	}
//
//	
//	public static int getLineNum_inGCS(Stmt unit, ContextGraphMethod secondGCS) {
//		HashMap<Unit, Integer> u2LineNo= secondGCS.getU2LineNO();
//		if(u2LineNo==null) 
//		{
//			throw new RuntimeException("the GCS should have been created, otherwise it would not be used here");
//		}
//		Integer ret =u2LineNo.get(unit);
//		if(ret ==null) throw new RuntimeException("every one is put into the map via the dfs!");
//		return ret.intValue();
//	}


//	public static boolean couplingCheck(String secondClass, String filename) {
//		// may not be successfully if the caller class (inner class) is in a file wtih a different name 
//		if(filename.contains(".java"))
//		{
//			int index =filename.indexOf(".java");
//			if(index !=-1)
//			{
//				String pureName =filename.substring(0, index);
//				if(secondClass.contains(pureName))
//				{
//					return true; //pass
//				}
//			}
//			
//		}
//		return false;
//	}





//	public static String computeAncJcodeFromFirstDifferentStackElem(
//			List<StackTraceElement_lpxz> pctxts, int firstUniqueIndex) {
//		StackTraceElement_lpxz ele = pctxts.get(firstUniqueIndex);
//		String className = ele.getClassName();
//		String mName = ele.getMethodName();
//		String filename = ele.getFileName();
//		int eleLineNO =  ele.getLineNumber();
//		String eleLineNo_invoke = ele.getLineNumber_invoke();
//
//		
//		StackTraceElement_lpxz commonOne = pctxts.get(firstUniqueIndex -1);
//		String commonClass = commonOne.getClassName();
//		String commonMethod = commonOne.getMethodName();
//		
//
//		
//		if(eleLineNO >0)
//		{
//			Stmt hitCS = search4CallSiteStmt(commonClass, commonMethod, eleLineNO, eleLineNo_invoke);	
//			if(hitCS!=null)
//			{
//				int sootEleLineNo= SootAgent4Pecan.getLineNum(hitCS);
//				if(sootEleLineNo!=eleLineNO)
//					System.err.println("e:" + sootEleLineNo );
//				
//				
//				
//				return hitCS.toString() ;			
//			}
//			
//		}
//		return null;
//	}


	
	public static SootMethod getMethod(String className, String methodSig) {
		Scene.v().loadNecessaryClasses();
	    SootClass sc = Scene.v().loadClassAndSupport(className);
	    sc.setApplicationClass();
	    Scene.v().loadNecessaryClasses();
	    
	    List<SootMethod> methods =sc.getMethods();
	    for(SootMethod sMethod : methods)
	    {
	    	//String sootsig =sMethod.getSignature();
	    	String fullSTEofSootsig  = ConvertSootMsig2FullSTEMsig.convertSootMethod(sMethod);
	    		    	
	    	if(fullSTEofSootsig.equals(methodSig))
	    	{
	    		return sMethod;
	    	}
	    	
	    }
		
		return null;
	}
	
	
	

	//================toremove:
// shortcut, account for most cases, efficient, hot path
//	public static Body search4MethodBodyInClass(String className, String method) {
//		// no need any more, consider the sootLoadNecessary, super NB
//       		
//		 
//		Scene.v().loadNecessaryClasses();
//	    SootClass sc = Scene.v().loadClassAndSupport(className);
//	    sc.setApplicationClass();
//	    Scene.v().loadNecessaryClasses();
//	   SootMethod sm = sc.getMethodByName(method);
//	    if(!sm.hasActiveBody())
//              sm.retrieveActiveBody();
//	    Body bb  =sm.getActiveBody();
//	   
//	    return bb;	   
//	}
	
	// stmt is the calling stmt inside the body, use it to distinguish methods with same name.

//	public static Body search4MethodBodyInClass(String className, String method, int oneStmtLine) {
//		// no need any more, consider the sootLoadNecessary, super NB
////        String argsOfToyW = "-f J -p jb use-original-names:false -pp -cp .:/home/lpxz/jrmc-1.6.0/jre/lib/jsse.jar " + className; // java.lang.Math
////		String interString = argsOfToyW;
////		String[] finalArgs = interString.split(" ");
////        soot.Main.v().processCmdLine(finalArgs);
////        
////		List excludesList= new ArrayList();
////		excludesList.add("jrockit.");
////		excludesList.add("com.bea.jrockit");
////		excludesList.add("sun.");
////		Options.v().set_exclude(excludesList);
////		 Options.v().set_keep_line_number(true);			
//		 
////		Scene.v().loadNecessaryClasses();
//		
//		
//	    SootClass sc = Scene.v().loadClassAndSupport(className);
//	    sc.setApplicationClass();
//	    Scene.v().loadNecessaryClasses();
//	    Iterator<SootMethod> smIt =sc.getMethods().iterator();
//	   
//	   
//	    while (smIt.hasNext()) {
//			SootMethod sm = (SootMethod) smIt.next();
//			if(sm.getName().equals(method))
//			{
//			    if(!sm.hasActiveBody())
//		              sm.retrieveActiveBody();
//			    Body bb  =sm.getActiveBody();		    
//			    boolean existGreater = false;
//			    boolean existLess =false;
//			    for(Unit unit  : bb.getUnits())
//			    {
//			    	int  line = getLineNum(unit);
//			    	if(line>=oneStmtLine)
//			    		existGreater = true;
//			    	if(line <= oneStmtLine)
//			    		existLess = true;	    	
//			    	
//			    }
//			    if(existGreater && existLess)
//			    	return bb;
//			   
//			}
//			
//		}
//	    return null;
//
//	  
//	   
//	}


	



	
//	public static String getFullMethodName(StackTraceElement_lpxz ste)
//	{ 
//		String classname = ste.getClassName();
//		String methodname = ste.getMethodName();
//		return classname + "." + methodname;
//		
//	}
	
//	public static String getFullMethodName(StackTraceElement ste)
//	{ 
//		String classname = ste.getClassName();
//		String methodname = ste.getMethodName();
//		return classname + "." + methodname;
//		
//	}
	
//	public static List<StackTraceElement_lpxz> snapshot(List<StackTraceElement_lpxz> ctxtStack )
//	{
//		List<StackTraceElement_lpxz> ret = new ArrayList<StackTraceElement_lpxz>();
//		ret.addAll(ctxtStack);
//		return ret;
//		
//	}

	public static void main(String[] args) throws IOException {}
//	public static void sootLoadNecessary(String classname) {
//		
//		//-w is necessary..System.class is needed..
//	    String argsOfToyW = "-w -app -p jb use-original-names:false -f J -pp -cp /home/lpxz/java_standard/jre/lib/jsse.jar:"+ origAnalyzedFolder+" " + classname; // java.lang.Math
//		String interString = argsOfToyW;
//		String[] finalArgs = interString.split(" ");
//        soot.Main.v().processCmdLine(finalArgs);
//        Options.v().set_keep_line_number(true);			
//		
//		List excludesList= new ArrayList();
//		excludesList.add("jrockit.");
//		excludesList.add("com.bea.jrockit");
//		excludesList.add("sun.");
//		Options.v().set_exclude(excludesList);
//	
//		
//	
//	    SootClass curclass = Scene.v().loadClassAndSupport(classname);	   
//	    curclass.setApplicationClass();	    
//	    Scene.v().loadNecessaryClasses();		
//	}
	
//	public static void sootLoadNecessary(List list) {		
//		//" -main-class " + classname +
//		HashSet<String> rootClassNames = new HashSet<String>();
//		HashSet<String> applicationClassNames = new HashSet<String>();
//		String trueMainClassStr= null;
//		for(Object elem : list)
//		{
//			CSMethodPair pair = (CSMethodPair)elem;
//			CSMethod o1 = pair.getO1();// must be pc!
//			CSMethod o2 = pair.getO2();	
//			
//			
//			String m1sig =o1.getMsig();
//			String m2sig = o2.getMsig();
//			String classname = SootAgent4Engine2.getClassNameFromMSig(m1sig);
//			String classname2 = SootAgent4Engine2.getClassNameFromMSig(m2sig);
//			applicationClassNames.add(classname);
//			applicationClassNames.add(classname2);	
//			
//			
//			String cgRootClass1 =o1.getStes().get(0).getClassName();
//			String cgRootClass2 =o2.getStes().get(0).getClassName();
//			rootClassNames.add(cgRootClass1);
//			rootClassNames.add(cgRootClass2);
//			
//			String cgRootMStr1 = o1.getStes().get(0).getMethodName();
//			String cgRootMStr2 = o2.getStes().get(0).getMethodName();
//			if(cgRootMStr1.equals("main"))
//				trueMainClassStr = cgRootClass1;
//			else if(cgRootMStr2.equals("main")) // one main is enough
//				trueMainClassStr = cgRootClass2;			
//		}
//        if(trueMainClassStr==null)
//        {
//        	trueMainClassStr =(String) rootClassNames.toArray()[0];
//        }
//	    String argsOfToyW = "-w -app -p jb use-original-names:false -f c" +
//	    		" -pp -cp /home/lpxz/java_standard/jre/lib/jsse.jar:"+ origAnalyzedFolder
//	    		+ " " + trueMainClassStr; // java.lang.Math
//		    
//
//	    
//	    
//	    String interString = argsOfToyW;
//		String[] finalArgs = interString.split(" ");
//			
//        soot.Main.v().processCmdLine(finalArgs);
//        Options.v().set_keep_line_number(true);			
//		
//		List excludesList= new ArrayList();
//		for(String exclude : MethodNode.unInstruClasses)
//		{
//			excludesList.add(exclude);
//		}		
//		Options.v().set_exclude(excludesList);
//	
//		
//	    for(String classname:applicationClassNames)
//	    {
//		    SootClass curclass = Scene.v().loadClassAndSupport(classname);	   
//		    curclass.setApplicationClass();	 
//	    }
//  
//	    Set<SootMethod> methods = new HashSet<SootMethod>();
//        for(String cgRootClass1: rootClassNames)
//        {
//        	methods.addAll(entrysOfRoot(cgRootClass1));
//        }
//        List<SootMethod> ret = new ArrayList<SootMethod>();	 
//	    ret.addAll(methods);//	        
//   	    Scene.v().setEntryPoints(ret);// if no entry points constructed, no cg is constructed..
//   	    
//   	    
//   	    Scene.v().loadNecessaryClasses();// at last
//
//	}


//	private static HashSet<SootMethod> entrysOfRoot(String cgRootClass1) {
//		HashSet<SootMethod> ret = new HashSet<SootMethod>();
//		SootClass mainclass =Scene.v().loadClassAndSupport(cgRootClass1);	      
//        SootClass cls = mainclass;//;Scene.v().getMainClass(); 
//        if(cls.declaresMethodByName("main"))
//        {
//        	  ret.add( cls.getMethodByName("main"));     
//        }
//        else if(cls.declaresMethodByName("run")){
//        	 ret.add( cls.getMethodByName("run"));     
//		}	        
//        else {
//			// do nothing
//        	throw new RuntimeException("you are the root class?");
//		}
//        for (SootMethod clinit : EntryPoints.v().clinitsOf(mainclass)) {
//             ret.add(clinit);
//        }
//        ret.addAll( EntryPoints.v().implicit() );
//        return ret;
//	}

//	public static void sootDestroyNecessary() {
//		
//		G.v().reset();
//		return ;
//		
//	}



	
	
	public static void sootLoadNecessary(List list) {		
		//" -main-class " + classname +
		HashSet<String> classes  = new  HashSet<String>();
		Iterator<Pattern> patternsIt = list.iterator();
		int pid  = 0;
    		while(patternsIt.hasNext())
    		{
	    		Pattern p = patternsIt.next();
                pid++;
	    		if(p instanceof TypeOnePattern || p instanceof TypeTwoPattern)
	    		{
	    			PatternI patternI = (PatternI) p;	 
	    			{
		    			RWNode pnode  = patternI.getNodeI();
		    			RWNode rnode = patternI.getNodeK();
		    			RWNode cnode = patternI.getNodeJ();
		    			Vector pv = pnode.getContext_VectorEncoding();
		    			Vector cv = cnode.getContext_VectorEncoding();
		    			Vector rv = rnode.getContext_VectorEncoding();
		    			
		    			classes.addAll(ContextValueManager.translate2ClassNameV(pv));
		    			classes.addAll(ContextValueManager.translate2ClassNameV(cv));
		    			classes.addAll(ContextValueManager.translate2ClassNameV(rv));	    			
	    			}   			
	    		}    		
    		}
    	
    		String classarg  = "";
         for(String classname  : classes) 
         {
        	 classarg += " " + classname ; 
         }
        
		 String argsOfToyW = "-f J -p jb use-original-names:false -pp -cp .:/home/lpxz/jrmc-1.6.0/jre/lib/jsse.jar" + classarg ;


	    String interString = argsOfToyW;
		String[] finalArgs = interString.split(" ");
			
        soot.Main.v().processCmdLine(finalArgs);
        
        
		List excludesList= new ArrayList();
		excludesList.add("jrockit.");
		excludesList.add("com.bea.jrockit");
		excludesList.add("sun.");
		Options.v().set_exclude(excludesList);
		 Options.v().set_keep_line_number(true);			
		 
		Scene.v().loadNecessaryClasses();
	}
	
	public static void sootDestroyNecessary() {
		
		G.v().reset();
		return ;
		
	}

	


	
}
