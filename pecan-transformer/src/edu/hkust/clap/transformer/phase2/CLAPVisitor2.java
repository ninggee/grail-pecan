package edu.hkust.clap.transformer.phase2;

import edu.hkust.clap.Parameters;
import edu.hkust.clap.Util;
import edu.hkust.clap.transformer.contexts.*;
import edu.hkust.clap.transformer.Visitor;
import soot.ArrayType;
import soot.Body;
import soot.Modifier;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.jimple.*;
import soot.util.Chain;

public class CLAPVisitor2 extends Visitor {

    public CLAPVisitor2(Visitor visitor) {
        super(visitor);
    }
    
    public void visitStmtAssign(SootMethod sm, Chain units, AssignStmt assignStmt) {
        nextVisitor.visitStmtAssign(sm, units, assignStmt);
    }
    
    public void visitStmtEnterMonitor(SootMethod sm, Chain units, EnterMonitorStmt enterMonitorStmt) 
    {
    	Visitor.totalaccessnum++; 	
    	Visitor.instrusharedaccessnum++;
    	    	
    	Value op = enterMonitorStmt.getOp();
    	Type type = op.getType();
    	String sig = type.toString();
    	Value memory = StringConstant.v(sig);
    	
		Visitor.addCallAccessSyncObjInstance(sm, units, enterMonitorStmt, "enterMonitorAfter", op,memory, false);
		if(Parameters.isReplay)
			units.remove(enterMonitorStmt);
		
        nextVisitor.visitStmtEnterMonitor(sm, units, enterMonitorStmt);
    }

    public void visitStmtExitMonitor(SootMethod sm, Chain units, ExitMonitorStmt exitMonitorStmt) {
    	Visitor.totalaccessnum++; 	
    	Visitor.instrusharedaccessnum++;
    	
    	Value op = exitMonitorStmt.getOp();
    	Type type = op.getType();
    	String sig = type.toString();
    	Value memory = StringConstant.v(sig);
    	
		Visitor.addCallAccessSyncObjInstance(sm, units, exitMonitorStmt, "exitMonitorBefore", op,memory, true);
		
		if(Parameters.isReplay)
			units.remove(exitMonitorStmt);
		
        nextVisitor.visitStmtExitMonitor(sm, units, exitMonitorStmt);
    }

    public void visitInstanceInvokeExpr(SootMethod sm, Chain units, Stmt s, InstanceInvokeExpr invokeExpr, InvokeContext context) {
        String sigclass = invokeExpr.getMethod().getDeclaringClass().getName();//+"."+invokeExpr.getMethod().getName();
        Value memory = StringConstant.v(sigclass);
        
        Value base = invokeExpr.getBase();
        String sig = invokeExpr.getMethod().getSubSignature();
        if (sig.equals("void wait()")||sig.equals("void wait(long)") || sig.equals("void wait(long,int)")) 
        {        	

    		Visitor.addCallAccessSyncObjInstance(sm, units, s, "waitAfter", base,memory, false);
        	Visitor.instrusharedaccessnum++;
        	Visitor.totalaccessnum++;
        	
        	if(Parameters.isReplay)
    			units.remove(s);

        //} else if (sig.equals("void wait(long)") || sig.equals("void wait(long,int)")) {

        } else if (sig.equals("void notify()")) 
        {
    		Visitor.addCallAccessSyncObjInstance(sm, units, s, "notifyBefore", base,memory, true);
        	Visitor.instrusharedaccessnum++;
        	Visitor.totalaccessnum++;
        	
        	if(Parameters.isReplay)
    			units.remove(s);
        } else if (sig.equals("void notifyAll()")) 
        {
    		Visitor.addCallAccessSyncObjInstance(sm, units, s, "notifyAllBefore", base,memory, true);
        	Visitor.instrusharedaccessnum++;
        	Visitor.totalaccessnum++;
        	
        	if(Parameters.isReplay)
    			units.remove(s);
        }
        else if (sig.equals("void start()") && isThreadSubType(invokeExpr.getMethod().getDeclaringClass())) 
        {
    		Visitor.addCallstartRunThreadBefore(sm, units, s, "startRunThreadBefore", invokeExpr.getBase());
        }
        else if ((sig.equals("void join()") || sig.equals("void join(long)") || sig.equals("void join(long,int)"))
                && isThreadSubType(invokeExpr.getMethod().getDeclaringClass()))
        {
    		Visitor.addCallJoinRunThreadAfter(sm, units, s, "joinRunThreadAfter", invokeExpr.getBase());
        }
        else if(invokeExpr.getMethod().isSynchronized())
        {
        	if(true)
        		return;
        	
            Visitor.addCallAccessSyncObj(sm,units, s, "enterSyncMethodBefore", memory, true);
        	Visitor.instrusharedaccessnum++;
        	Visitor.totalaccessnum++;
        	
            Visitor.addCallAccessSyncObj(sm,units, s, "exitSyncMethodAfter", memory, false);
        	Visitor.instrusharedaccessnum++;
        	Visitor.totalaccessnum++;
        }
        
        nextVisitor.visitInstanceInvokeExpr(sm, units, s, invokeExpr, context);
    }

    public void visitStaticInvokeExpr(SootMethod sm, Chain units, Stmt s, StaticInvokeExpr invokeExpr, InvokeContext context) {
        
    	if(true)
    		return;
    	
    	SootClass appClass = invokeExpr.getMethod().getDeclaringClass();
    	//SootMethod appMethod = invokeExpr.getMethod();
    	//appMethod.setModifiers(appMethod.getModifiers()&~Modifier.SYNCHRONIZED);
    	
    	//appClass.isApplicationClass()
        String sig = appClass.getName()+".STATIC.";//+"."+invokeExpr.getMethod().getName();

        if(invokeExpr.getMethod().isSynchronized())
        {
            Value memory = StringConstant.v(sig);
            
            Visitor.addCallAccessSPE(sm,units, s, "enterSyncMethodBefore", memory, true);
        	Visitor.instrusharedaccessnum++;
        	Visitor.totalaccessnum++;
        	
            Visitor.addCallAccessSPE(sm,units, s, "exitSyncMethodAfter", memory, false);
        	Visitor.instrusharedaccessnum++;
        	Visitor.totalaccessnum++;
        }  
        
    	nextVisitor.visitStaticInvokeExpr(sm, units, s, invokeExpr, context);
    }

    public void visitArrayRef(SootMethod sm, Chain units, Stmt s, ArrayRef arrayRef, RefContext context) {

        nextVisitor.visitArrayRef(sm, units, s, arrayRef, context);
    }

    public void visitInstanceFieldRef(SootMethod sm, Chain units, Stmt s, InstanceFieldRef instanceFieldRef, RefContext context) 
    {
    	Visitor.totalaccessnum++;
		
    	Value base = instanceFieldRef.getBase();
    	
		String sig = instanceFieldRef.getField().getDeclaringClass().getName()+"."+instanceFieldRef.getField().getName();
		Value memory = StringConstant.v(sig);
		
		if(!instanceFieldRef.getField().isFinal()&&!Parameters.isInsideConstructor)
		{
//			if(sig.contains("HashIterator")&&sig.contains("modCount"))
//				System.out.print(true);
			
		if(Visitor.sharedVariableWriteAccessSet.contains(sig))
		{	
			
			String methodname = "readBeforeInstance"; 
			
			if (context != RHSContextImpl.getInstance()) 
	        {
				methodname = "writeBeforeInstance";
	        }
			else if(instanceFieldRef.getField().getType() instanceof ArrayType)
			{
				
				Stmt nextStmt =  (Stmt)units.getSuccOf(s);
				if(s instanceof AssignStmt && nextStmt instanceof AssignStmt)
				{
					AssignStmt assgnStmt = (AssignStmt) s;
					AssignStmt assgnNextStmt = (AssignStmt) nextStmt;
					if(assgnNextStmt.getLeftOp().toString().contains(assgnStmt.getLeftOp().toString()))
			        {
						methodname = "writeBeforeInstance";
			        }
				}  
			}
				
			
		    Visitor.addCallAccessSPEInstance(sm,units, s, methodname, base, memory, true);
		    Visitor.instrusharedaccessnum++;        	
		}
		}
        nextVisitor.visitInstanceFieldRef(sm, units, s, instanceFieldRef, context);
    }

    public void visitStaticFieldRef(SootMethod sm, Chain units, Stmt s, StaticFieldRef staticFieldRef, RefContext context) 
    {        
    	Visitor.totalaccessnum++;
        String sig = staticFieldRef.getField().getDeclaringClass().getName()+"."+staticFieldRef.getField().getName();
		Value memory = StringConstant.v(sig);

		if(!staticFieldRef.getField().isFinal()&&!Parameters.isInsideConstructor)
        if(Visitor.sharedVariableWriteAccessSet.contains(sig))
		{	
			String methodname = "readBeforeStatic"; 
			
			if (context != RHSContextImpl.getInstance()) 
	        {
				methodname = "writeBeforeStatic";
	        }
			else if(staticFieldRef.getField().getType() instanceof ArrayType)
			{
				Stmt nextStmt =  (Stmt)units.getSuccOf(s);
				if(s instanceof AssignStmt && nextStmt instanceof AssignStmt)
				{
					AssignStmt assgnStmt = (AssignStmt) s;
					AssignStmt assgnNextStmt = (AssignStmt) nextStmt;
					if(assgnNextStmt.getLeftOp().toString().contains(assgnStmt.getLeftOp().toString()))
			        {
						methodname = "writeBeforeStatic";
			        }
				}  
			}
				
		    Visitor.addCallAccessSPEStatic(sm,units, s, methodname, memory, true);
		    Visitor.instrusharedaccessnum++;        	
		}
        nextVisitor.visitStaticFieldRef(sm, units, s, staticFieldRef, context);
    }
}
