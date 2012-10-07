package edu.hkust.clap.engine;

import java.io.Serializable;

import edu.hkust.clap.datastructure.RWNode;

public class TypeOnePattern extends PatternI implements Serializable{

	public TypeOnePattern(RWNode nodeI, RWNode nodeK, RWNode nodeJ)
	{
		super(nodeI,nodeK,nodeJ);
	}
	public String toString()
	{
		return "*** AV-I --- "+nodeI+" * "+nodeK+" * "+nodeJ+" ***";
	}

	public String printToString()
	{
		return "*** AV-I --- "+nodeI.getMemString()+" --- "+nodeI.printToString()+" * "+nodeK.printToString()+" * "+nodeJ.printToString()+" ***";
	}
}
