package edu.hkust.clap.engine;

import java.util.HashMap;
import java.util.LinkedList;

import edu.hkust.clap.datastructure.RWNode;

public class ContextTree 
{
	private LinkedList<RWNode> simplified_nodes = new LinkedList<RWNode>();
	private ContextNode ctx_root = new ContextNode(0);
	private int depth;
	
	public ContextTree(LinkedList<RWNode> nodes, int depth)
	{
		this.depth  = depth;
		
		for(int j=0;j<nodes.size();j++)
		{
			RWNode node = nodes.get(j);
			int line = node.getLine();
			LinkedList<Integer> context = node.getContext();
			if(!isRedundant(context,line))
			{					
				simplified_nodes.add(node);
			}
		}
	}
	
	public LinkedList<RWNode> getSimplifiedNodes()
	{
		return simplified_nodes;
	}
	private boolean isRedundant(LinkedList<Integer> context,int line)
	{
		ContextNode cur_ctx_node = ctx_root;

		if(depth!=0)			
		{
			int size = context.size();
			int start_pos = size-1;
			int end_pos = size - depth;
			
			if(depth<0 || size<depth)
				end_pos = 0;
			
			int pos = start_pos;
			while(pos>=end_pos)
			{
				ContextNode ctx_node = new ContextNode(context.get(pos));
				ContextNode new_ctx_node = cur_ctx_node.getChildNode(ctx_node);
				if(new_ctx_node==null)
				{
					cur_ctx_node.addChild(ctx_node);
					new_ctx_node = ctx_node;
				}
				cur_ctx_node = new_ctx_node;
				pos--;
			}
		
		}
		
		if(cur_ctx_node.getKeyNumber(line)>1)
		{
			return true;
		}
		else
		{
			cur_ctx_node.incrementKeyNumber(line);
			return false;
		}
		
	}
	private class ContextNode
	{
		private LinkedList<ContextNode> children = new LinkedList<ContextNode>() ;
		private int id;
		private HashMap<Integer,Integer> key_map;
		private ContextNode(int id)
		{
			this.id = id;
		}
		public int getKeyNumber(int line)
		{
			if(key_map==null)
			{
				key_map=new HashMap<Integer,Integer>();
				key_map.put(line, 0);
				return 0;
			}
			else
			{
				Integer num = key_map.get(line);
				if(num==null)
				{
					num =0;
					key_map.put(line, 0);
				}
				return num;
			}
			
		}
		public void incrementKeyNumber(int line)
		{
			Integer num = key_map.get(line);
			num++;
			key_map.put(line, num);
		}
		@Override
		public boolean equals(Object o)
		{
			if(o instanceof ContextNode && ((ContextNode)o).id==this.id)
				return true;
			else
				return false;
		}
		public void addChild(ContextNode node)
		{
			children.add(node);
		}
		public ContextNode getChildNode(ContextNode node)
		{
			int index = children.indexOf(node);
			if(index>=0)
				return children.get(index);
			else
				return null;
		}
	}
}
