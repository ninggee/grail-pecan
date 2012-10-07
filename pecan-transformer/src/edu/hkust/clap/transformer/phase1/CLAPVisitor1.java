package edu.hkust.clap.transformer.phase1;

import edu.hkust.clap.Parameters;
import edu.hkust.clap.Util;
import edu.hkust.clap.transformer.Visitor;
import edu.hkust.clap.transformer.contexts.InvokeContext;
import edu.hkust.clap.transformer.contexts.RHSContextImpl;
import edu.hkust.clap.transformer.contexts.RefContext;
import soot.*;
import soot.jimple.*;
import soot.util.*;

/**
 * Copyright (c) 2007-2008
 * Pallavi Joshi	<pallavi@cs.berkeley.edu>
 * Koushik Sen <ksen@cs.berkeley.edu>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * <p/>
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * <p/>
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p/>
 * 3. The names of the contributors may not be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class CLAPVisitor1 extends Visitor {

    public CLAPVisitor1(Visitor visitor) {
        super(visitor);
    }
    
    public void visitStmtAssign(SootMethod sm, Chain units, AssignStmt assignStmt) {
        nextVisitor.visitStmtAssign(sm, units, assignStmt);
    }

    public void visitStmtEnterMonitor(SootMethod sm, Chain units, EnterMonitorStmt enterMonitorStmt) {
    
    	nextVisitor.visitStmtEnterMonitor(sm, units, enterMonitorStmt);
    }

    public void visitStmtExitMonitor(SootMethod sm, Chain units, ExitMonitorStmt exitMonitorStmt) {
    	
        nextVisitor.visitStmtExitMonitor(sm, units, exitMonitorStmt);
    }
    
    /** Although synchronized instance method invocation and static method invocation
     *  target at different locks,
     * we still use the same SPE for them
     */
    public void visitInstanceInvokeExpr(SootMethod sm, Chain units, Stmt s, InstanceInvokeExpr invokeExpr, InvokeContext context) {
    	
        nextVisitor.visitInstanceInvokeExpr(sm, units, s, invokeExpr, context);

    }
    
    /**
     * The name of the memory location is class name + a special string "STATIC"
     */
    public void visitStaticInvokeExpr(SootMethod sm, Chain units, Stmt s, StaticInvokeExpr invokeExpr, InvokeContext context) {
	
    	nextVisitor.visitStaticInvokeExpr(sm, units, s, invokeExpr, context);   
    }


    public void visitArrayRef(SootMethod sm, Chain units, Stmt s, ArrayRef arrayRef, RefContext context) {
    	nextVisitor.visitArrayRef(sm, units, s, arrayRef, context);
    }

    public void visitInstanceFieldRef(SootMethod sm, Chain units, Stmt s, InstanceFieldRef instanceFieldRef, RefContext context) {
    
        String sig = instanceFieldRef.getField().getDeclaringClass().getName()+"."+instanceFieldRef.getField().getName();
    	//write field
        if (context != RHSContextImpl.getInstance() || instanceFieldRef.getField().getType() instanceof ArrayType) 
        {
            if(!Visitor.tlo.isObjectThreadLocal(instanceFieldRef, sm))
        	{//|| sig.contains("TableDescriptor.referencedColumnMap")
            	if(!sig.contains("SQLChar"))
            	sharedVariableWriteAccessSet.add(sig);         	
        	}
        }
        nextVisitor.visitInstanceFieldRef(sm, units, s, instanceFieldRef, context);
    }
    
    public void visitStaticFieldRef(SootMethod sm, Chain units, Stmt s, StaticFieldRef staticFieldRef, RefContext context) {
    	
        String sig = staticFieldRef.getField().getDeclaringClass().getName()+"."+staticFieldRef.getField().getName();
    	//write field
        if (context != RHSContextImpl.getInstance() || staticFieldRef.getField().getType() instanceof ArrayType) 
        {
            if(!Visitor.tlo.isObjectThreadLocal(staticFieldRef, sm))
        	{
            	sharedVariableWriteAccessSet.add(sig);
            	
        	}
        }
        nextVisitor.visitStaticFieldRef(sm, units, s, staticFieldRef, context);
    }

}
