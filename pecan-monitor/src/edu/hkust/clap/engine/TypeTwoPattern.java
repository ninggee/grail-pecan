package edu.hkust.clap.engine;

import java.io.Serializable;

import edu.hkust.clap.datastructure.RWNode;

public class TypeTwoPattern extends PatternI implements Serializable{

	
	public TypeTwoPattern(RWNode nodeI, RWNode nodeK, RWNode nodeJ)
	{
		super(nodeI,nodeK,nodeJ);
	}
	public String toString()
	{
		return "*** AV-II --- "+nodeI+" * "+nodeK+" * "+nodeJ+" ***";
	}
	public String printToString()
	{
		return "*** AV-II --- "+nodeI.getMemString()+" --- "+nodeI.printToString()+" * "+nodeK.printToString()+" * "+nodeJ.printToString()+" ***";
	}
}
