package edu.hkust.clap.engine;

import java.util.HashMap;
import java.util.LinkedList;

import edu.hkust.clap.datastructure.RWNode;
import edu.hkust.clap.datastructure.AbstractNode.TYPE;

public class ThreadNodes 
{
	LinkedList<LinkedList<RWNode>> rwnodes = new LinkedList<LinkedList<RWNode>>();
	LinkedList<LinkedList<RWNode>> writenodes = new LinkedList<LinkedList<RWNode>>();
	LinkedList<LinkedList<RWNode>> readnodes = new LinkedList<LinkedList<RWNode>>();
	
	LinkedList<RWNode> allnodes = new LinkedList<RWNode>();
	LinkedList<RWNode> allwnodes = new LinkedList<RWNode>();
	LinkedList<RWNode> allrnodes = new LinkedList<RWNode>();
	
	HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
	
	private int currentAtomIndex;
	private int atomIndex;
	
	public ThreadNodes(LinkedList<RWNode> nodes)
	{
		for(int k=0;k<nodes.size();k++)
		{
			RWNode node = nodes.get(k);
			
			addrwnode(node);
			if(node.getType()==TYPE.READ)
			{	
				addreadnode(node);
			}
			else if(node.getType()==TYPE.WRITE)
			{					
				addwritenode(node);
			}
		}
	}
	
	/**
	 * To overcome the trace inaccuracy, we need to do a bit trick here
	 */
	public void addrwnode(RWNode node)
	{
		allnodes.add(node);
		if(node.getAtomIndex()>=0)
		{
			Integer index = map.get(node.getAtomIndex());
			if(index==null)
			{
				rwnodes.add(new LinkedList<RWNode>());
				writenodes.add(new LinkedList<RWNode>());
				readnodes.add(new LinkedList<RWNode>());	
							
				index=currentAtomIndex;
				map.put(node.getAtomIndex(), index);
				currentAtomIndex++;
			}
			atomIndex=index;
					
			rwnodes.get(atomIndex).add(node);
		}		
	}
	public void addwritenode(RWNode node)
	{		
		allwnodes.add(node);
		
		if(node.getAtomIndex()>=0)
		{
			writenodes.get(atomIndex).add(node);
		}
	}
	public void addreadnode(RWNode node)
	{
		allrnodes.add(node);
		if(node.getAtomIndex()>=0)
		{
		readnodes.get(atomIndex).add(node);
		}
	}
	public LinkedList<RWNode> getrwnodes(int index)
	{
		return rwnodes.get(index);
	}
	public LinkedList<RWNode> getwritenodes(int index)
	{
		return writenodes.get(index);
	}
	public LinkedList<RWNode> getreadnodes(int index)
	{
		return readnodes.get(index);
	}
	public int getSize()
	{
		return rwnodes.size();
	}
	public LinkedList<LinkedList<RWNode>> getAllNodes()
	{
		return rwnodes;
	}
	public LinkedList<RWNode> getAllWRNodes()
	{
		return allnodes;
	}
	public LinkedList<RWNode> getAllWNodes()
	{
		return allwnodes;
	}
	public LinkedList<RWNode> getAllRNodes()
	{
		return allrnodes;
	}
}
