package edu.hkust.clap.engine;

import edu.hkust.clap.datastructure.RWNode;

public class TypeFivePattern extends PatternII{

	TypeFivePattern(RWNode nodeI,RWNode nodeK, RWNode nodeR, RWNode nodeJ)
	{
		super(nodeI,nodeK,nodeR,nodeJ);
	}
	public String toString()
	{
		return "*** PATTERN FIVE --- "+nodeI+" * "+nodeK+" * "+nodeR+" * "+nodeJ+" ***";
	}
}
