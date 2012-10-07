package edu.hkust.clap.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PartialOrderRelation 
{	
	private HashMap<Integer,HashSet<Integer>> multiThreadsOrderMap = new HashMap<Integer,HashSet<Integer>>();
	private HashMap<Integer,HashSet<Integer>> partialOrderMap = new HashMap<Integer,HashSet<Integer>>();
	private HashSet<Integer> processedNodes = new HashSet<Integer>();
	
	private HashMap<Integer,Integer> idIndexMap = new HashMap<Integer,Integer>();
	private int indexCounter=0;
	
	private boolean[][] porelation;
	public void addSingleThreadOrder(int first, int second) {
		addMultiThreadOrder(first, second);
	}
	public void addMultiThreadOrder(int key, int child) {
		
		if(!idIndexMap.containsKey(key))
		{
			
			
			idIndexMap.put(key, indexCounter++);
		}
		
		if(!idIndexMap.containsKey(child))
		{
			
			idIndexMap.put(child, indexCounter++);
		}
		
		HashSet<Integer> set = multiThreadsOrderMap.get(key);
		if(set==null)
		{
			set = new HashSet<Integer>();
			multiThreadsOrderMap.put(key, set);
		}
		set.add(child);
	}
	
	public void computePORelation_new()
	{
		int size = indexCounter;
		porelation = new boolean[size][size];
		
		Iterator<Integer> poIt0 = idIndexMap.keySet().iterator();
		while(poIt0.hasNext())
		{
			int key = poIt0.next();
			int index1 = idIndexMap.get(key);
			porelation[index1] = new boolean[size];
			porelation[index1][index1] = true;
			
			HashSet<Integer> set = multiThreadsOrderMap.get(key);
			if(set!=null)
			{
				Iterator<Integer> setIt = set.iterator();
				while(setIt.hasNext())
				{
					int child = setIt.next();
					int index2 = idIndexMap.get(child);
					porelation[index1][index2] = true;
				}
			}
		}
		
		propogate_new();
	}
	
	/**
	 * Use matrix multiplicity to compute the reachability matrix
	 */
	private void propogate_new()
	{
		int n = porelation.length;
		
	    boolean changed = true;
		while(changed)
		{
			changed = false;
		    for(int i=0;i<n;i++)
		    {
		    	for(int j=0;j<n;j++)
		    	{
		    		if(!porelation[i][j])
			    		for(int k=0;k<n;k++)
			    		{
			    			if(porelation[i][k]&porelation[k][j])
			    			{
				    			porelation[i][j]=true;
				    			
				    			changed = true;
				    			
				    			break;
			    			}
			    		}
		    	}
		    }		    
		}
	}
	
	private void computePORelation()
	{
		Set<Integer> set = partialOrderMap.keySet();
		int size = set.size();
		porelation = new boolean[size][size];
		
		Iterator<Integer> poIt0 = set.iterator();
		for(int i=0;i<size;i++)
		{
			idIndexMap.put(poIt0.next(),i);
			porelation[i] = new boolean[size];
			for(int j=0;j<size;j++)
			{
				porelation[i][j] = false;
			}
		}
		
		Iterator<Integer> poIt = partialOrderMap.keySet().iterator();
		while(poIt.hasNext())
		{
			Integer id1 = poIt.next();
			int i = idIndexMap.get(id1);
			Iterator<Integer> poIt2 = partialOrderMap.get(id1).iterator();
			while(poIt2.hasNext())
			{
				Integer id2 = poIt2.next();
				int j = idIndexMap.get(id2);
				porelation[i][j] = true;
			}
		}
	}
	public void computePartialOrder()
	{
		Iterator<Integer> orderIt = multiThreadsOrderMap.keySet().iterator();
		while(orderIt.hasNext())
		{
			propagate(orderIt.next());
		}
		
		computePORelation();
	}
	
	private HashSet<Integer> propagate(int id1)
	{
		processedNodes.add(id1);
		
		HashSet<Integer> set = multiThreadsOrderMap.get(id1);
		HashSet<Integer> newset = new HashSet<Integer>(id1);

		if(set!=null)			
		{		
			Iterator<Integer> setIt = set.iterator();
			while(setIt.hasNext())
			{
					Integer id2 = setIt.next();
					if(!processedNodes.contains(id2))
						newset.addAll(propagate(id2));
					else
					{
						newset.addAll(partialOrderMap.get(id2));
					}
			}
			newset.addAll(set);
		}
		
		partialOrderMap.put(id1, newset);

		return newset;
	}
	/**
	 * use DFS or BFS is easy to cause StackOverflowError
	 * so let's compute it beforehand
	 * @param id1
	 * @param id2
	 * @return
	 */
	public boolean canNotReach(int id1, int id2) 
	{		
		
		int i = idIndexMap.get(id1);
		
		int j = idIndexMap.get(id2);
		
		
		if(porelation[i][j])
			return false;
		else
			return true;
	}

	public boolean canNotReachOnDemand(int id1, int id2) 
	{
		
		HashSet<Integer> next_dts = multiThreadsOrderMap.get(id1);
		
		if(next_dts!=null)
		{
			if(next_dts.contains(id2))
				return false;
			else
			{
				Iterator<Integer> next_dtsIt= next_dts.iterator();
				while(next_dtsIt.hasNext())
				{
					Integer id = next_dtsIt.next();
					if(!canNotReach(id,id2))
						return false;
				}
			}
		}
		
		return true;
	}
}
