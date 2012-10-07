package edu.hkust.clap.datastructure;

import java.io.Serializable;
import java.util.Set;

public class AbstractNode implements Serializable{
	/**
	 * There are three kinds of mems: SPE, thread object id, ordinary object id
	 */
	private static final long serialVersionUID = 1L;
	int line;
	private int ID;
	int mem;
	String mem_str;
	int newmem;
	long tid;
	int t_index;
	int atom_index;
	TYPE type;
	Set<Integer> lockset;
	Set<AbstractNode> outNodes;
	Set<AbstractNode> inNodes;
	
	public AbstractNode(long tid, TYPE type)
	{
		this.tid = tid;
		this.type = type;
	}
	
	public AbstractNode(int mem, long tid, TYPE type)
	{
		this.mem = mem;
		this.tid = tid;
		this.type = type;
	}
	public AbstractNode(int mem, long tid, TYPE type, Set<AbstractNode> outNodes, Set<AbstractNode> inNodes)
	{
		this.mem = mem;
		this.tid = tid;
		this.type = type;
		this.outNodes = outNodes;
		this.inNodes = inNodes;
	}
	public AbstractNode(int mem, long tid, TYPE type, Set<Integer> lockset, Set<AbstractNode> outNodes, Set<AbstractNode> inNodes)
	{
		this.mem = mem;
		this.tid = tid;
		this.type = type;
		this.lockset = lockset;
		this.outNodes = outNodes;
		this.inNodes = inNodes;
	}
	public AbstractNode(int line, int mem, long tid, TYPE type, Set<Integer> lockset, Set<AbstractNode> outNodes, Set<AbstractNode> inNodes)
	{
		this.line = line;
		this.mem = mem;
		this.tid = tid;
		this.type = type;
		this.lockset = lockset;
		this.outNodes = outNodes;
		this.inNodes = inNodes;
	}
	public AbstractNode(int line, int mem, long tid, TYPE type)
	{
		this.line = line;
		this.mem = mem;
		this.tid = tid;
		this.type = type;
	}
	public void addOutNode(AbstractNode node)
	{
		this.outNodes.add(node);
	}
	public void addInNode(AbstractNode node)
	{
		this.inNodes.add(node);
	}
	public int getMem()
	{
		return mem;
	}
	public void setNewMem(int mem)
	{
		this.newmem = mem;
	}
	public int getNewMem()
	{
		return newmem;
	}
	public int getLine()
	{
		return line;
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
	public void setLockSet(Set<Integer> lockset)
	{
		 this.lockset = lockset;
	}
	public Set<Integer> getLockSet()
	{
		return lockset;
	}
	public Set<AbstractNode> getOutNodes()
	{
		return outNodes;
	}
	public Set<AbstractNode> getInNodes()
	{
		return inNodes;
	}
	public void setTIndex(int t_index)
	{
		this.t_index = t_index;
	}
	public void setTid(int tid)
	{
		this.tid = tid;
	}
	public void setAtomIndex(int atom_index)
	{
		this.atom_index = atom_index;
	}
	public int getAtomIndex()
	{
		return atom_index;
	}
	public int getTIndex()
	{
		return t_index;
	}

	public void setMemString(String mem_str)
	{
		this.mem_str = mem_str;
	}
	public String getMemString()
	{
		if(mem_str!=null)
			return mem_str;
		else
			return "";
	}
	public enum TYPE
	{
		READ,WRITE,LOCK,UNLOCK,SEND,RECEIVE,ENTRY,EXIT,NA
	}
	public String toString()
	{
		if(mem_str==null)
			return ID+" "+mem+" "+line+" "+tid+" "+type;
		else
			return ID+" "+mem_str+" "+line+" "+tid+" "+type;
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
	
}
