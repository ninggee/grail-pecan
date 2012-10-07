package edu.hkust.clap.datastructure;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class AbstractNode implements Serializable{
	/**
	 * There are three kinds of mems: SPE, thread object id, ordinary object id
	 */
	
	protected static final long serialVersionUID = 1L;

	protected int mem; // for locknode, it is the runtime id of the lock
	// for messagenode, it is the runtime id of the mutex. for example in o.wait(), mem is o's runtime id.
	// for RWnode, it is the runtime id of the accessed location.
	// for methodnode, it is not important, you can set it as the method id.
	
	protected int ID; // leave it alone, set internally by pecan
	protected long tid;  /// thread id
	protected TYPE type; //	enum TYPE READ,WRITE,LOCK,UNLOCK,SEND,RECEIVE,ENTRY,EXIT

	
	private AbstractNode depnode;
	
	public void setDepNode(AbstractNode node)
	{
		this.depnode = node;
	}
	public AbstractNode getDepNode()
	{
		return depnode;
	}
	
	public AbstractNode(int mem, long tid, TYPE type)
	{
		this.mem = mem;
		this.tid = tid;
		this.type = type;
		
		if(Math.random()>0.5)
			Thread.yield();
	}

	public int getMem()
	{
		return mem;
	}
	
	public void setID(int id)
	{
		this.ID = id;		
	}
	public int getID()
	{
		return ID;
	}
	public long getTId()
	{
		return tid;
	}

	public void setTid(int tid)
	{
		this.tid = tid;
	}
	public boolean equals(AbstractNode node)
	{
		if(this.ID == node.getID())
		{
			return true;
		}
		else
			return false;
	}
	public TYPE getType()
	{
		return type;
	}
	public enum TYPE
	{
		READ,WRITE,LOCK,UNLOCK,SEND,RECEIVE,ENTRY,EXIT
	}
	public String toString()
	{
		return ID+" "+mem+" "+tid+" "+type;
	}
}
