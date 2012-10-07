package edu.hkust.clap.datastructure;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class LockNode extends AbstractNode
{
	public LockNode(int mem, long tid, TYPE type)
	{
		super(mem,tid,type);
	}
}
