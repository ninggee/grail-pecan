package edu.hkust.clap.engine;

import java.io.Serializable;


import edu.hkust.clap.datastructure.RWNode;

public class PatternI implements Pattern, Serializable{
	protected RWNode nodeI;
	protected RWNode nodeJ;
	protected RWNode nodeK;
	protected String mem;
	

	PatternI(RWNode nodeI, RWNode nodeK, RWNode nodeJ)
	{
		this.nodeI = nodeI;
		this.nodeJ = nodeJ;
		this.nodeK = nodeK;
		this.mem = nodeI.getMemString();
	}
	public RWNode getNodeI()
	{
		return nodeI;
	}
	public RWNode getNodeJ()
	{
		return nodeJ;
	}
	public RWNode getNodeK()
	{
		return nodeK;
	}
	public boolean equals(Object o)
	{
		if(o instanceof PatternI)
		{
			PatternI pattern = (PatternI)o;
			RWNode I = pattern.getNodeI();
			RWNode J = pattern.getNodeJ();
			RWNode K = pattern.getNodeK();
			
			if(nodeI.getLine()==I.getLine()&&nodeI.getMemString()==I.getMemString()
					&& nodeJ.getLine()==J.getLine()&&nodeJ.getMemString()==J.getMemString()
					&& nodeK.getLine()==K.getLine()&&nodeK.getMemString()==K.getMemString())
				return true;
			else
				return false;
		}
		else
			return super.equals(o);
	}
	public int hashCode()
	{
		return nodeI.getLine()<<8+nodeJ.getLine()<<8+nodeK.getLine();
	}
	@Override
	public String printToString() {
		// TODO Auto-generated method stub
		return "";
	}
	@Override
	public String getAnormalMem() {
		return mem;
	}
	
}
