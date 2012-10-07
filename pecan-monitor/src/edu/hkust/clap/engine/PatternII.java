package edu.hkust.clap.engine;

import edu.hkust.clap.datastructure.RWNode;

public class PatternII implements Pattern{
	protected RWNode nodeI;
	protected RWNode nodeJ;
	protected RWNode nodeK;
	protected RWNode nodeR;
	protected String mem;
	
	public PatternII(RWNode nodeI,RWNode nodeK, RWNode nodeR, RWNode nodeJ)
	{
		this.nodeI = nodeI;
		this.nodeK = nodeK;
		this.nodeR = nodeR;
		this.nodeJ = nodeJ;
		this.mem = nodeI.getMemString()+" & "+nodeJ.getMemString();
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
	public RWNode getNodeR()
	{
		return nodeR;
	}
	public boolean equals(Object o)
	{
		if(o instanceof PatternII)
		{
			PatternII pattern = (PatternII)o;
			RWNode I = pattern.getNodeI();
			RWNode J = pattern.getNodeJ();
			RWNode K = pattern.getNodeK();
			RWNode R = pattern.getNodeR();
			
			if(nodeI.getLine()==I.getLine()&&nodeI.getMemString()==I.getMemString()
					&& nodeJ.getLine()==J.getLine()&&nodeJ.getMemString()==J.getMemString()
					&& nodeK.getLine()==K.getLine()&&nodeK.getMemString()==K.getMemString()
					&& nodeR.getLine()==R.getLine()&&nodeR.getMemString()==R.getMemString())
				return true;
			else
				return false;
		}
		else
			return super.equals(o);
	}
	public int hashCode()
	{
		return nodeI.getLine()<<8+nodeJ.getLine()<<8+nodeK.getLine()<<8+nodeR.getLine();
	}
	public String toString()
	{
		return "*** ASV --- "+nodeI+" * "+nodeK+" * "+nodeR+" * "+nodeJ+" ***";
	}
	public String printToString()
	{
		return "*** ASV --- "+mem+"--- "+nodeI.printToString()+" * "+nodeK.printToString()+" * "+nodeR.printToString()+" * "+nodeJ.printToString()+" ***";
	}
	@Override
	public String getAnormalMem() {
		return mem;
	}
}
