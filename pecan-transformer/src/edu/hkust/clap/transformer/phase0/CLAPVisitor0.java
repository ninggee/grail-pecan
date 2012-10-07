package edu.hkust.clap.transformer.phase0;

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

public class CLAPVisitor0 extends Visitor {
// yes, do nothing.
    public CLAPVisitor0(Visitor visitor) {
        super(visitor);
    }
    
//    public void visitStmtAssign(SootMethod sm, Chain units, AssignStmt assignStmt) {
//        nextVisitor.visitStmtAssign(sm, units, assignStmt);
//    }
//    
//    public void visitStmtEnterMonitor(SootMethod sm, Chain units, EnterMonitorStmt enterMonitorStmt) 
//    {	
//        nextVisitor.visitStmtEnterMonitor(sm, units, enterMonitorStmt);
//    }
//
//    public void visitStmtExitMonitor(SootMethod sm, Chain units, ExitMonitorStmt exitMonitorStmt) {
//    	
//        nextVisitor.visitStmtExitMonitor(sm, units, exitMonitorStmt);
//    }
//
//    public void visitInstanceInvokeExpr(SootMethod sm, Chain units, Stmt s, InstanceInvokeExpr invokeExpr, InvokeContext context) {
//        
//        
//        nextVisitor.visitInstanceInvokeExpr(sm, units, s, invokeExpr, context);
//    }
//
//    public void visitStaticInvokeExpr(SootMethod sm, Chain units, Stmt s, StaticInvokeExpr invokeExpr, InvokeContext context) {
//        
//    	
//    	nextVisitor.visitStaticInvokeExpr(sm, units, s, invokeExpr, context);
//    }
//
//    public void visitArrayRef(SootMethod sm, Chain units, Stmt s, ArrayRef arrayRef, RefContext context) {
//
//        nextVisitor.visitArrayRef(sm, units, s, arrayRef, context);
//    }
//
//    public void visitInstanceFieldRef(SootMethod sm, Chain units, Stmt s, InstanceFieldRef instanceFieldRef, RefContext context) 
//    {
//		Visitor.instrusharedaccessnum++;        	
//
//        nextVisitor.visitInstanceFieldRef(sm, units, s, instanceFieldRef, context);
//    }
//
//    public void visitStaticFieldRef(SootMethod sm, Chain units, Stmt s, StaticFieldRef staticFieldRef, RefContext context) 
//    { 
//        nextVisitor.visitStaticFieldRef(sm, units, s, staticFieldRef, context);
//    }
}
