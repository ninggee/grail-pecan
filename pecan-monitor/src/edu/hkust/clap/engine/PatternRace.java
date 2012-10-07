package edu.hkust.clap.engine;

import edu.hkust.clap.datastructure.RWNode;

public class PatternRace implements Pattern{
	protected RWNode nodeI;
	protected RWNode nodeII;
	protected String mem;
	public PatternRace(RWNode nodeI, RWNode nodeII)
	{
		this.nodeI = nodeI;
		this.nodeII = nodeII;
		this.mem = nodeI.getMemString();
	}
	public String getAnormalMem()
	{
		return mem;
	}
	public RWNode getNodeI()
	{
		return nodeI;
	}
	public RWNode getNodeII()
	{
		return nodeII;
	}
	public void setNodeI(RWNode nodeI)
	{
		this.nodeI = nodeI;
	}
	public void setNodeII(RWNode nodeII)
	{
		this.nodeII = nodeII;
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof PatternRace)
		{
			PatternRace pattern = (PatternRace)o;
			RWNode I = pattern.getNodeI();
			RWNode II = pattern.getNodeII();
			
			if(nodeI.getLine()==I.getLine()&&nodeI.getMemString()==I.getMemString()
					&& nodeII.getLine()==II.getLine()&&nodeII.getMemString()==II.getMemString())
				return true;
			else
				return false;
		}
		else
			return super.equals(o);
	}
	public int hashCode()
	{
		return nodeI.getLine()<<8+nodeII.getLine();
	}
	public String toString()
	{
		return "*** RACE *** "+nodeI+" * "+nodeII+" ***";
	}
	@Override
	public String printToString() {
		return "*** RACE --- "+mem+" --- "+nodeI.printToString()+" * "+nodeII.printToString()+" ***";
	}
}
