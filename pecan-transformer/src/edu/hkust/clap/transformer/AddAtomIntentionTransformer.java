package edu.hkust.clap.transformer;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.hkust.clap.Util;

import pldi.locking.MethodLocker;
import properties.PropertyManager;

import Drivers.Utils;
import Drivers.DirectedGraphToDotGraph.DotNamer;
import soot.Body;
import soot.BodyTransformer;
import soot.Modifier;
import soot.PatchingChain;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.Jimple;
import soot.jimple.NopStmt;
import soot.jimple.Stmt;
import soot.jimple.ThrowStmt;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JReturnVoidStmt;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.graph.pdg.EnhancedUnitGraph;
import soot.toolkits.graph.pdg.HashMutablePDG;
import soot.toolkits.graph.pdg.PDGNode;


public class AddAtomIntentionTransformer extends BodyTransformer{
	
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
	private  void loadMethodsNeedAtomIntention() {    
	 
	    // save to the project's name! in the folder InvokedMethods/
	
		String filename = "/home/lpxz/eclipse/workspace/APIDesigner/InvokedMethods/" + PropertyManager.projectName + "_atomintention";
		
		
		ObjectInputStream in = null;
    	try
    	{
			
			FileInputStream fis = new FileInputStream(filename);
			in = new ObjectInputStream(fis);
			invokes = (HashSet<String>)in.readObject();
			
			System.err.println("methods as the mother atomicity intention!");
			for(String invoke : invokes)
			{
				System.err.println(invoke);
			}
    	}catch(Exception Exception){
    		System.out.println("dump error!");
    	}  
    	
	}
	
		@Override
		protected void internalTransform(Body b, String phaseName,
				Map options) {
			if(!PropertyManager.addAtomIntent) return;
			SootMethod  sm =b.getMethod();
			if(sm.getName().equals(SootMethod.constructorName) ||sm.getName().equals(SootMethod.staticInitializerName))
				return;// do not handle it.
		    
			if(properties.PropertyManager.fullydynamic)
			{
				if(!(sm.toString().contains("void run()")&&Util.isRunnableSubType(sm.getDeclaringClass())))
				{
					return;
				}
				else{
					//properties.PropertyManager.write2File(properties.PropertyManager.atomIntention_whitelist, sm.getSignature());
					List<String> whitelists =properties.PropertyManager.readFromFile(properties.PropertyManager.atomIntention_whitelist);
					boolean pass = false;
					for(String white: whitelists)
					{
						if(sm.getSignature().equals(white))
						{
							pass = true;
						}
					}
					if(!pass) return;
//					System.err.print("whole thread is atomic.............." + sm.getDeclaringClass().getName());
					
				}
				
			}
			else
			{
				if(invokes==null)
			    	 loadMethodsNeedAtomIntention();

			     
			     boolean goon = false;
			     

			     for(String str : invokes)
			     {
			    	
			    	 if(sm.getSignature().equals(str))
			    		 goon = true;
			     }

			     if(!goon) return;// no need to process it
			}
			
			
		    
		   
			Body bb =sm.getActiveBody();
		
			if(!sm.isSynchronized() && sm.hasActiveBody()) 
			{
				
				 Body body = sm.getActiveBody();
				 PatchingChain<Unit> units = body.getUnits();
				 Stmt firstNon =getFirstNonIdentityStmt(body);
				 if(firstNon instanceof JReturnStmt || firstNon instanceof JReturnVoidStmt)
				 {// the empty body incurs problems during the lock injection.
					 Stmt nop=Jimple.v().newNopStmt();
					 units.insertBefore(nop, firstNon);									 
				 }					
				boolean random = true;
				
				 MethodLocker.addlock(sm, random);
				
			}	
			//bb.validate();
			
		}
	
	}
	

