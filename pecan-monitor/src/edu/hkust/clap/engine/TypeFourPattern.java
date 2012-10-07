package edu.hkust.clap.engine;

import edu.hkust.clap.datastructure.RWNode;

public class TypeFourPattern extends PatternII{

	TypeFourPattern(RWNode nodeI,RWNode nodeK, RWNode nodeR, RWNode nodeJ)
	{
		super(nodeI,nodeK,nodeR,nodeJ);
	}
	public String toString()
	{
		return "*** PATTERN FOUR --- "+nodeI+" * "+nodeK+" * "+nodeR+" * "+nodeJ+" ***";
	}
}
