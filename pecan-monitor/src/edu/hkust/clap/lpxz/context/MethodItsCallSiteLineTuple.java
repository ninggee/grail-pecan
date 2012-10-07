package edu.hkust.clap.lpxz.context;

import java.io.Serializable;

import soot.JastAddJ.ThisAccess;
import soot.jimple.Stmt;

public class MethodItsCallSiteLineTuple implements Serializable{

    public int lineOfCallSite = -1;
	public int getLineOfCallSite() {
		return lineOfCallSite;
	}

	public void setLineOfCallSite(int lineOfCallSite) {
		this.lineOfCallSite = lineOfCallSite;
	}

	public String curMsig = null;
	public String getCurMsig() {
		return curMsig;
	}

	public void setCurMsig(String curMsig) {
		this.curMsig = curMsig;
	}

	public String getTheCallsite() {
		return theCallsite;
	}

	public void setTheCallsite(String theCallsite) {
		this.theCallsite = theCallsite;
	}

	public MethodItsCallSiteLineTuple(String msigarg, String theCallsite, int linepara) {// stmt prevents from efficient dumping
		
		this.curMsig = msigarg;
		this.theCallsite = theCallsite;
		this.lineOfCallSite = linepara;
		
	}

	public String theCallsite = null; 
	
	public boolean equals(MethodItsCallSiteLineTuple arg)
	{
		if(curMsig.equals(arg.curMsig) && theCallsite.equals(arg.theCallsite) && lineOfCallSite== arg.lineOfCallSite)
			return true;
		else {
			return false;
		}
		
	}
	
	public String toString()
	{
		return curMsig + " called at " + theCallsite + " :" + lineOfCallSite;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
