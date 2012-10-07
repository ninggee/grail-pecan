package edu.hkust.clap.organize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import soot.Body;
import soot.Unit;
import soot.jimple.Stmt;
import soot.toolkits.graph.UnitGraph;

import edu.hkust.clap.lpxz.context.MethodItsCallSiteLineTuple;

public class CSMethod implements Serializable{
	 public static String PCMethod = "PCMethod";
	 public static String RMethod = "RMethod";
	
    public String method_type = "";
	
    public boolean isPC()
    {
    	return this.method_type.equals(PCMethod);    	
    }
    
    public boolean isR()
    {
    	return this.method_type.equals(RMethod);
    }
	public String getMethod_type() {
		return method_type;
	}

   

	public void setMethod_type(String method_type) {
		this.method_type = method_type;
	}

	// this is the newest context encoding, and the coolest
	List<MethodItsCallSiteLineTuple> MCpairList = new  ArrayList<MethodItsCallSiteLineTuple>();
	
public List<MethodItsCallSiteLineTuple> getMCpairList() {
		return MCpairList;
	}

	public void setMCpairList(List<MethodItsCallSiteLineTuple> cmSeq) {
		this.MCpairList = cmSeq;
	}

	
	
	

//	// to remove!
//	List<Stmt> ctxts = new ArrayList<Stmt>();
//	
//	public List<Stmt> getCtxts() {
//		return ctxts;
//	}
//	
//
//	public void setCtxts(List<Stmt> ctxts) {
//		this.ctxts = ctxts;
//	}


	






	public String getMsig() {
		return Msig;
	}



	public void setMsig(String msig) {
		this.Msig = msig;
	}



	public String getpAnc() {
		return pAnc;
	}



	public void setpAnc(String pAnc) {
		if(pAnc==null) throw new RuntimeException();
//		if(pAnc.toString().equals("$i1 = r0.<org.exolab.jms.net.connector.ManagedConnectionHandle: int _connectionCount>"))
//		{
//			System.err.println("xxx");
//			System.exit(1);
//		}// jcode is already that one, go to see the jcode
		this.pAnc = pAnc;
	}

//	Warning: Phase wjtp.tnlp is not a standard Soot phase listed in XML files.
//	$i1 = r0.<org.exolab.jms.net.connector.ManagedConnectionHandle: int _connectionCount>
//	    private synchronized void incActiveConnections()
//	    {
//	        org.exolab.jms.net.connector.ManagedConnectionHandle r0;
//	        int $i0, $i1;
//
//	        r0 := @this: org.exolab.jms.net.connector.ManagedConnectionHandle;
//	        $i0 = r0.<org.exolab.jms.net.connector.ManagedConnectionHandle: int _connectionCount>;
//	        $i1 = $i0 + 1;
//	        r0.<org.exolab.jms.net.connector.ManagedConnectionHandle: int _connectionCount> = $i1;
//	        return;
//	    }


	public String getcAnc() {
		
		return cAnc;
	}



	public void setcAnc(String cAnc) {
		if(cAnc==null) throw new RuntimeException();
		this.cAnc = cAnc;
	}

	public String getrAnc() {
		return rAnc;
	}



	public void setrAnc(String rAnc) {
		this.rAnc = rAnc;
	}


	String Msig = null;
	String pAnc = null;
	String cAnc = null;
	String rAnc = null;
	
	int pAncLine = -1;
	int cAncLine =-1;
	int rAncLine =-1;
		
	public int getpAncLine() {
		return pAncLine;
	}

	public void setpAncLine(int pAncLine) {
		this.pAncLine = pAncLine;
	}

	public int getcAncLine() {
		return cAncLine;
	}

	public void setcAncLine(int cAncLine) {
		this.cAncLine = cAncLine;
	}

	public int getrAncLine() {
		return rAncLine;
	}

	public void setrAncLine(int rAncLine) {
		this.rAncLine = rAncLine;
	}

	public void printIt()
    {
    	
    	if(method_type.equals(PCMethod))
    	{
    		if(pAnc!=null)
    		System.out.println("!!!panc: " + pAnc);
    		if(cAnc!=null)
    		System.out.println("!!!canc: " + cAnc);
    	}
    	else {
			System.out.println("!!!ranc: " + rAnc);
		}
    	System.out.println("msig:" + Msig);
    	System.out.println("ctxts:" );
    	for(int i=MCpairList.size()-1; i>=0 ; i--)
    	{
    		System.out.println(MCpairList.get(i));
    	}
    }
		
	
	//========================================
	Unit punit =null;

	Unit cunit = null;
	Unit runit = null;
	Set xedges = null;
	
	Body bb = null;

	UnitGraph ug= null;
	public Body getBb() {
		return bb;
	}



	public void setBb(Body bb) {
		this.bb = bb;
	}



	public UnitGraph getUg() {
		return ug;
	}



	public void setUg(UnitGraph ug) {
		this.ug = ug;
	}


	public Unit getPunit() {
		return punit;
	}



	public void setPunit(Unit punit) {
		this.punit = punit;
	}



	public Unit getCunit() {
		return cunit;
	}



	public void setCunit(Unit cunit) {
		this.cunit = cunit;
	}



	public Unit getRunit() {
		return runit;
	}



	public void setRunit(Unit runit) {
		this.runit = runit;
	}



	public Set getXedges() {
		return xedges;
	}



	public void setXedges(Set xedges) {
		this.xedges = xedges;
	}


	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
