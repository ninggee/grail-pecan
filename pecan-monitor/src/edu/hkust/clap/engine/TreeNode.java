package edu.hkust.clap.engine;

import java.util.Set;

public class TreeNode {
	private Integer key;
	private Set<Integer> childs; 
	TreeNode(Integer key)
	{
		this.key = key;
	}
	public void addChild(Integer child)
	{
		childs.add(child);
	}
}
