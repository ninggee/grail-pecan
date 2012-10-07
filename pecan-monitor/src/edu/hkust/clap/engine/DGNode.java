package edu.hkust.clap.engine;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import edu.hkust.clap.datastructure.AbstractNode;
import edu.hkust.clap.datastructure.MessageNode;

public class DGNode {
	
	LinkedList<AbstractNode> nodes;
	DGNode nextNode;
	HashSet<DGNode> remoteIns;
	DGNode prev_node;
	
	public void setPrevLocalNode(DGNode node)
	{
		this.prev_node= node;
	}
	public DGNode getPrevLocalNode()
	{
		return prev_node;
	}
	private int index;
		
	public void addRemoteInNode(DGNode node)
	{
		if(remoteIns==null)
		{
			remoteIns= new HashSet<DGNode>();
		}
		remoteIns.add(node);
	}
	public void addRemoteInNodes(HashSet<DGNode> nodes)
	{
		if(remoteIns==null)
		{
			remoteIns= new HashSet<DGNode>();
		}
		remoteIns.addAll(nodes);
	}
	public HashSet<DGNode> getRemoteInNodes()
	{
		return remoteIns;
	}
	public void setIndex(int index)
	{
		this.index = index;
	}
	public int getIndex()
	{
		return index;
	}
	DGNode(AbstractNode node)
	{
		nodes= new LinkedList<AbstractNode>();
		nodes.add(node);
		
	}
	public void setNextNode(DGNode node){
		this.nextNode = node;
		
	}
	public DGNode getNextNode()
	{
		return nextNode;
	}

	public LinkedList<AbstractNode> getNodes() {
		// TODO Auto-generated method stub
		return nodes;
	}
	public void addNode(AbstractNode node)
	{
		nodes.add(node);
	}
}
