package edu.hkust.clap.transformer;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java_cup.production;

import pldi.locking.SynchronizedRegionFinder;
import pldi.locking.SynchronizedRegionFlowPair;
import properties.PropertyManager;

import Drivers.Utils;
import Drivers.DirectedGraphToDotGraph.DotNamer;
import soot.Body;
import soot.BodyTransformer;
import soot.IntType;
import soot.Local;
import soot.Modifier;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Unit;
import soot.VoidType;
import soot.jimple.AssignStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NopStmt;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.ThrowStmt;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JReturnVoidStmt;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.graph.pdg.EnhancedUnitGraph;
import soot.toolkits.graph.pdg.HashMutablePDG;
import soot.toolkits.graph.pdg.PDGNode;
import soot.toolkits.scalar.FlowSet;
import soot.util.Chain;


public class AddIndicatorTransformer extends BodyTransformer{
	public static  Stmt getFirstNonIdentityStmt(Body bb)
    {
        Iterator it = bb.getUnits().iterator();
        Object o = null;
        while (it.hasNext())
            if (!((o = it.next()) instanceof IdentityStmt))
                break;
        if (o == null)
            throw new RuntimeException("no non-id statements!");
        return (Stmt)o;
    }
	
	
	    HashSet<String> invokes= null;
		private  void loadMethodsNeedIndicator() {    
		 
		    // save to the project's name! in the folder InvokedMethods/
		
			String filename = "/home/lpxz/eclipse/workspace/APIDesigner/InvokedMethods/" + PropertyManager.projectName + "_indicator";
			
			
			ObjectInputStream in = null;
	    	try
	    	{
				
				FileInputStream fis = new FileInputStream(filename);
				in = new ObjectInputStream(fis);
				invokes = (HashSet<String>)in.readObject();
				
//				for(String invoke : invokes)
//				{
//					System.err.println(invoke);
//				}
	    	}catch(Exception Exception){
	    		System.out.println("dump error!");
	    	}  
	    	
		}
	
		@Override
		protected void internalTransform(Body b, String phaseName,
				Map options) {
			if(!PropertyManager.addIndicator) return;
			SootMethod  sm =b.getMethod();
			if(sm.getName().equals(SootMethod.constructorName) ||sm.getName().equals(SootMethod.staticInitializerName))
				return;// do not handle it.
			if(properties.PropertyManager.fullydynamic)
			{
				if(!related2Sync(sm))
				{
					return;
				}
				else
				{
					//properties.PropertyManager.write2File(properties.PropertyManager.indicator_whitelist, sm.getSignature());
					List<String> whitelists =properties.PropertyManager.readFromFile(properties.PropertyManager.indicator_whitelist);
					
					boolean pass = false;
					for(String white: whitelists)
					{
						if(sm.getSignature().equals(white))
						{
							//System.err.print(sm.getSignature() + " is filtered !");
							pass = true;
						}
					}
					if(!pass) return;
//					System.err.print("continuing indicator....");
				}
			}
			else
			{
				if(invokes==null)
			    	 loadMethodsNeedIndicator();
				 
				
			     boolean goon = false;
			     for(String str : invokes)
			     {
			    	 if(sm.getSignature().equals(str))
			    		 goon = true;
			     }
			     if(!goon) return;// no need to process it
			}
			
		     
            System.err.println(sm.getSignature() + " need the indicator!...");
            SootClass motherClass = b.getMethod().getDeclaringClass();   		
    		String nameString = "LPXZ_" + motherClass.getName().replace("$", "").replace(".", "_");
    		
    		SootField sootField = null;
    		if(!motherClass.declaresFieldByName(nameString)) //insert the field only once
    		 sootField= addField2Class(nameString,motherClass );
    		else
    			sootField=motherClass.getFieldByName(nameString);    		
    		
    		try{
    		   addFieldAssign2Method(sootField, b);	
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    		b.validate();
    		
	    }

		private boolean related2Sync(SootMethod sm) {
			if(sm.isSynchronized())
			{
				
				return true;
			}
			else
			{  
				if(sm.hasActiveBody())
				{
					Body b =sm.getActiveBody();
					ExceptionalUnitGraph eug = new ExceptionalUnitGraph(b);		    	
					SynchronizedRegionFinder ta = new SynchronizedRegionFinder(eug, b,  true);
					Chain units = b.getUnits();
					Unit lastUnit = (Unit) units.getLast();
					FlowSet fs = (FlowSet) ta.getFlowBefore(lastUnit);
					if(fs!=null)
					{    					
					    for (Iterator iterator = fs.iterator(); iterator
								.hasNext();) {
					    	SynchronizedRegionFlowPair srfp = (SynchronizedRegionFlowPair) iterator.next();
					    	return true;
					    }
					}
				}
				
				
				return false;
			}
		}

		private void addFieldAssign2Method(SootField sootField, Body b) {
			Stmt firstStmt = getFirstNonIdentityStmt(b);
			
	   		PatchingChain<Unit> units = b.getUnits();

	    	
    		Local localVar = Jimple.v().newLocal(sootField.getName()+ b.getMethod().getName(),
    				IntType.v());
    		;

    		if(!b.getLocals().contains(localVar))
    		{
    			b.getLocals().add(localVar);
    		}else
    		{
    			return ; // already add local and insert.
    		}
    		
    		

    			Stmt newStmt = Jimple.v().newAssignStmt(localVar, IntConstant.v(5));
    			
        		units.insertBefore(newStmt,firstStmt );
    		
    	

    		Stmt newStmt2 = Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(b.getThisLocal(),sootField.makeRef()),
    				localVar);
    		units.insertBefore(newStmt2,firstStmt);			
    		
			
			
		}

		private SootField addField2Class(String nameString, SootClass motherClass) {
			SootField globalLockObj = null;
	   		try {
    			globalLockObj = motherClass.getFieldByName(nameString
    					);
    			// field already exists
    		} catch (RuntimeException re) {
    			// field does not yet exist (or, as a pre-existing error, there is
    			// more than one field by this name)
    			globalLockObj = new SootField(nameString, IntType.v(),  Modifier.PUBLIC);// make it an instance field!!
    			motherClass.addField(globalLockObj);
    		}
    		
//    		SootMethod enforceM = null;
//    		Body clinitBody =null;
//    		Unit firstStmt =null;
//    		boolean addingNewClinit = !motherClass.declaresMethod("void <clinit>()");
//    		if (addingNewClinit) {
//    			enforceM = new SootMethod("<clinit>", new ArrayList(), VoidType
//    					.v(), Modifier.PUBLIC | Modifier.STATIC);
//    			clinitBody = Jimple.v().newBody(enforceM);
//    			enforceM.setActiveBody(clinitBody);
//    			motherClass.addMethod(enforceM);
//    			clinitBody.getUnits().add(Jimple.v().newReturnVoidStmt());
//    			firstStmt = clinitBody.getFirstNonIdentityStmt();// no non-id stmt
//    			 if(firstStmt instanceof JReturnStmt || firstStmt instanceof JReturnVoidStmt)
//    			 {// the empty body incurs problems during the lock injection.
//    				 Stmt nop=Jimple.v().newNopStmt();
//    				 clinitBody.getUnits().insertBefore(nop, firstStmt);									 
//    			 }	
//    		} else {
//    			enforceM = motherClass.getMethod("void <clinit>()");
//    			if(!enforceM.hasActiveBody())
//    				enforceM.retrieveActiveBody();
//    			clinitBody = (JimpleBody) enforceM.getActiveBody();
//    			firstStmt = clinitBody.getFirstNonIdentityStmt();
//    		}
//    		PatchingChain<Unit> clinitUnits = clinitBody.getUnits();
//
//    	
//    		Local ctxtListLocal = Jimple.v().newLocal("clinit_local",
//    				IntType.v());
//    		;
//
//    		if(!clinitBody.getLocals().contains(ctxtListLocal))
//    		{
//    			clinitBody.getLocals().add(ctxtListLocal);
//    		}   		
//    		
//    		// assign new object to lock obj
//    		Stmt newStmt = Jimple.v().newAssignStmt(ctxtListLocal, IntConstant.v(0));
//    		clinitUnits.insertBefore(newStmt,firstStmt );
//
//
//    		Stmt newStmt2 = Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(globalLockObj.makeRef()),
//    				ctxtListLocal);
//    		clinitUnits.insertBefore(newStmt2,firstStmt);	
    		
    		return globalLockObj;
		}
		

		
	}
	

