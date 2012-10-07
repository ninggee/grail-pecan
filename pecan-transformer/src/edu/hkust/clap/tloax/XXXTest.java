package edu.hkust.clap.tloax;

import java.io.File;
import java.io.FileOutputStream;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarOutputStream;

import soot.Body;
import soot.BodyTransformer;
import soot.Context;
import soot.Local;
import soot.Pack;
import soot.PackManager;
import soot.PhaseOptions;
import soot.PointsToAnalysis;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Transform;
import soot.Value;
import soot.jimple.BreakpointStmt;
import soot.jimple.ConcreteRef;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.MonitorStmt;
import soot.jimple.NewExpr;
import soot.jimple.NopStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.AssignStmt;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThrowStmt;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.spark.ondemand.DemandCSPointsTo;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.GlobalVarNode;
import soot.jimple.spark.pag.LocalVarNode;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.pag.VarNode;
import soot.jimple.spark.sets.DoublePointsToSet;
import soot.jimple.spark.sets.EmptyPointsToSet;
import soot.jimple.spark.sets.PointsToSetInternal;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.infoflow.InfoFlowAnalysis;
import soot.jimple.toolkits.infoflow.SmartMethodInfoFlowAnalysis;
import soot.jimple.toolkits.thread.ThreadLocalObjectsAnalysis;
import soot.jimple.toolkits.thread.mhp.UnsynchronizedMhpAnalysis;
import soot.jimple.toolkits.thread.mhp.pegcallgraph.PegCallGraph;
import soot.options.Options;
import soot.util.ArrayNumberer;
import soot.util.Chain;
import soot.util.HashMultiMap;
import soot.util.MultiMap;

public class XXXTest 
{
	private static UnsynchronizedMhpAnalysis mhp;
	private static  ThreadLocalObjectsAnalysis tlo;
	private static PAG pag;
	private static SootMethod smethod;
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		//soot.lesson6.message.Main
		//account.Bank
		//bufwriter.BufWriter
		//rstack.BufferPool
		String mainclass = "message.Main";//
        if(args.length>0)
        	mainclass = args[0];
        
		Options.v().set_whole_program(true);
        Options.v().set_app(true);
		
		setOptions(mainclass);
		 
	      Pack jtp = PackManager.v().getPack("wjtp");
	      jtp.add(new Transform("wjtp.instrumenter",
	                            new SceneTransformer() 
	                 		   {
	                    			@Override
	                    			protected void internalTransform(String phaseName, Map options)  
	                    			{
	                    				//testIFS();
	                    				//testMHP();
	                    				testTLO();
	                    			}
	                 		   }));
	      
		String[] args2 = {"-f","jimple","-cp",".","-pp","-validate",mainclass};
		
		soot.Main.main(args2);//"-f","jimple",c"-x","javato.","-x","edu." \\sootOutput "-process-dir", processDir			
	
		long end = System.currentTimeMillis();
		long total=(end-start);
		System.out.println(total);
	}
	private static void setOptions(String mainclass)
	{

        enableJB();
        enalbeSpark();		
       Scene.v().setSootClassPath(System.getProperty("sun.boot.class.path")
               + File.pathSeparator + System.getProperty("java.class.path"));
       
       SootClass appclass = Scene.v().loadClassAndSupport(mainclass);
       Scene.v().setMainClass(appclass);
	}
	private static void enalbeSpark() {
		// TODO Auto-generated method stub
	      //Enable Spark
        HashMap<String,String> opt = new HashMap<String,String>();
        //opt.put("verbose","true");
        opt.put("propagator","worklist");
        opt.put("simple-edges-bidirectional","false");
        opt.put("on-fly-cg","true");
        opt.put("set-impl","double");
        opt.put("double-set-old","hybrid");
        opt.put("double-set-new","hybrid");
        opt.put("pre_jimplify", "true");
        SparkTransformer.v().transform("",opt);
        PhaseOptions.v().setPhaseOption("cg.spark", "enabled:true");
	}
	private static void enableJB() {
		// TODO Auto-generated method stub
        PhaseOptions.v().setPhaseOption("jb", "enabled:true");
        Options.v().set_keep_line_number(true);
        Options.v().setPhaseOption("jb", "use-original-names:true");
	}
	public static void testIFS()
	{
		InfoFlowAnalysis ifa = new InfoFlowAnalysis(true,true);
		
		
		Iterator classIt = Scene.v().getApplicationClasses().iterator();
		while (classIt.hasNext()) 
		{
			SootClass sc = (SootClass) classIt.next();

			System.err.println("class name: "+sc.getName());
			Iterator methodIt = sc.getMethods().iterator();
	     	  
			while (methodIt.hasNext()) 
			{
				SootMethod sm = (SootMethod) methodIt.next();	
				System.err.println("         method name: "+sm.getName());
				SmartMethodInfoFlowAnalysis smifa = ifa.getMethodInfoFlowAnalysis(sm);
				//InfoFlowAnalysis.printInfoFlowSummary(smifa.getMethodAbbreviatedInfoFlowGraph());
				InfoFlowAnalysis.printInfoFlowSummary(smifa.getMethodInfoFlowSummary());
			}
		}
	
	}
	private static void testMHP()
	{
		UnsynchronizedMhpAnalysis mhp = new UnsynchronizedMhpAnalysis();
	}
	private static void testTLO()
	{
//		mhp = new UnsynchronizedMhpAnalysis();
//		tlo = new ThreadLocalObjectsAnalysis(mhp);
//		pag = XG.v().getPAG();
		XFieldThreadEscapeAnalysis xtlo = new XFieldThreadEscapeAnalysis();
		//xtlo.printAllInfo();
		//xtlo.printPAGAllocInfo();
		//XObjectThreadEscapeAnalysis xtea = new XObjectThreadEscapeAnalysis(xtlo);
		
		if(true)
			return;
//		
		Iterator classIt = Scene.v().getApplicationClasses().iterator();
		while (classIt.hasNext()) 
		{
			SootClass sc = (SootClass) classIt.next();

			System.err.println("class name: "+sc.getName());
		         		        	  		       		        	  
			Iterator methodIt = sc.getMethods().iterator();
     	  
			while (methodIt.hasNext()) 
			{
				SootMethod sm = (SootMethod) methodIt.next();
				smethod = sm;
				System.err.println("         method name: "+sm.getName());
				
				if(sm.isAbstract() || sm.isNative())
					continue;

				Body body = null;
				try
				{
					body = sm.retrieveActiveBody();
				}catch(Exception e)
				{
					continue;
				}
     		 
				Chain units = body.getUnits();
		        Iterator stmtIt = units.snapshotIterator();
		        while (stmtIt.hasNext()) 
		        {
		            Stmt s = (Stmt) stmtIt.next();
			    	if (s instanceof AssignStmt) 
			    	{
			    		//System.err.println("AssignStmt: "+s);
			    		visitStmtAssign((AssignStmt)s);
			    	}
			    	else if (s instanceof InvokeStmt) 
			        {
			        	//System.err.println("InvokeStmt: "+s);
			        	visitInvokeExpr(s.getInvokeExpr());    
			        } else if (s instanceof IdentityStmt) {
			        	//System.err.println("IdentityStmt: "+s);
			        } else if (s instanceof GotoStmt) {
			        	//System.err.println("GotoStmt: "+s);
			        } else if (s instanceof IfStmt) {
			        	//System.err.println("IfStmt: "+s);
			        } else if (s instanceof TableSwitchStmt) {
			        	//System.err.println("TableSwitchStmt: "+s);
			        } else if (s instanceof LookupSwitchStmt) {
			        	//System.err.println("LookupSwitchStmt: "+s);
			        } else if (s instanceof MonitorStmt) {
			        	//System.err.println("MonitorStmt: "+s);
			        } else if (s instanceof ReturnStmt) {
			        	//System.err.println("ReturnStmt: "+s);
			        } else if (s instanceof ReturnVoidStmt) {
			        	//System.err.println("ReturnVoidStmt: "+s);
			        } else if (s instanceof ThrowStmt) {
			        	//System.err.println("ThrowStmt: "+s);
			        } else if (s instanceof BreakpointStmt) {
			        	//System.err.println("BreakpointStmt: "+s);
			        } else if (s instanceof NopStmt) {
			        	//System.err.println("NopStmt: "+s);
			        } else {
			            //throw new RuntimeException("UnknownASTNodeException");
			        }
			    }
			}
		}
	}
    private static void visitStmtAssign(AssignStmt assignStmt) 
    {
        Value left = assignStmt.getLeftOp();
        Value right = assignStmt.getRightOp();
        if (left instanceof ConcreteRef) {
           visitConcreteRef((ConcreteRef) left);
        } else if (left instanceof Local) {
           visitRHS(assignStmt,right);
        } else {
        	 //throw new RuntimeException("UnknownASTNodeException");
        }
    }
    private static void visitConcreteRef(ConcreteRef concreteRef) {
        if (concreteRef instanceof InstanceFieldRef)
            visitInstanceFieldRef((InstanceFieldRef) concreteRef);
        else if (concreteRef instanceof StaticFieldRef)
            visitStaticFieldRef((StaticFieldRef) concreteRef);
//        else
//        	 throw new RuntimeException("UnknownASTNodeException");
    }
   
    private static void visitStaticFieldRef(StaticFieldRef staticFieldRef) 
    {
    	SootField field = staticFieldRef.getField();
    	boolean isLocal = tlo.isObjectThreadLocal(staticFieldRef, smethod);
    	
    	boolean isShared = true;
    	if(isLocal)
    		isShared = false;
    	//System.err.println(field+" --- "+isShared);
	}
	private static void visitInstanceFieldRef(InstanceFieldRef instanceFieldRef) 
	{
    	SootField field = instanceFieldRef.getField();
    	boolean isLocal = tlo.isObjectThreadLocal(instanceFieldRef, smethod);
    	
    	boolean isShared = true;
    	if(isLocal)
    		isShared = false;
    	//System.err.println(field+" --- "+isShared);
	}
	private static void visitRHS(AssignStmt assignStmt,Value right) {
        if (right instanceof ConcreteRef)
            visitConcreteRef((ConcreteRef) right);
        else if (right instanceof InvokeExpr)
        	visitInvokeExpr((InvokeExpr) right);
		else if (right instanceof NewExpr) {
			visitNewExpr(assignStmt,(NewExpr)right);
		}
    }
    private static void visitNewExpr(AssignStmt assignStmt,NewExpr newExpr) {
		
    	boolean isLocal = tlo.isObjectThreadLocal(assignStmt.getLeftOp(),smethod);
    	boolean isShared = true;
    	if(isLocal)
    		isShared = false;
    	System.err.println(assignStmt+" --- "+isShared);
	}
    private static void visitInvokeExpr(InvokeExpr invokeExpr) {
        if (invokeExpr instanceof InstanceInvokeExpr) {
           visitInstanceInvokeExpr((InstanceInvokeExpr) invokeExpr);
        } else if (invokeExpr instanceof StaticInvokeExpr) {
           visitStaticInvokeExpr((StaticInvokeExpr) invokeExpr);
        } else {
        	 //throw new RuntimeException("UnknownASTNodeException");
        }
    }
    private static void visitInstanceInvokeExpr( InstanceInvokeExpr instanceInvokeExpr)
    {
    }
    private static void visitStaticInvokeExpr(StaticInvokeExpr staticInvokeExpr)
    {    	
    }
} 
