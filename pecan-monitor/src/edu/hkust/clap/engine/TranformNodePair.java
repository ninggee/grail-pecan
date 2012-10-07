package edu.hkust.clap.engine;

import edu.hkust.clap.datastructure.AbstractNode;

public class TranformNodePair 
{
	AbstractNode nodeL;
	AbstractNode nodeR;
	TranformNodePair(AbstractNode nodeL, AbstractNode nodeR)
	{
		this.nodeL = nodeL;
		this.nodeR = nodeR;
	}
	public AbstractNode getNodeL()
	{
		return nodeL;
	}
	public AbstractNode getNodeR()
	{
		return nodeR;
	}
	public boolean equals(TranformNodePair pair)
	{
		if(nodeL == pair.getNodeL()&& nodeR == pair.getNodeR())
			return true;
		else
			return false;
	}
}
