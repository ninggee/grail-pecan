package edu.hkust.clap.engine;

import edu.hkust.clap.datastructure.RWNode;

public class TypeThreePattern extends PatternII
{
	TypeThreePattern(RWNode nodeI,RWNode nodeK, RWNode nodeR, RWNode nodeJ)
	{
		super(nodeI,nodeK,nodeR,nodeJ);
	}
	public String toString()
	{
		return "*** AV-III --- "+nodeI+" * "+nodeK+" * "+nodeR+" * "+nodeJ+" ***";
	}
}
