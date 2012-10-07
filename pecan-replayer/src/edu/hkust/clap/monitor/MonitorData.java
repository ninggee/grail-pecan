package edu.hkust.clap.monitor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.Map.Entry;

import edu.hkust.clap.datastructure.AbstractNode;

public class MonitorData implements Serializable
{
	 /**
	 * 
	 */
	 private static final long serialVersionUID = 1L;
	 private Vector<AbstractNode> mainvec;
	 private HashMap<Integer,HashMap<Integer,Vector<AbstractNode>>> speHashMap;
	 private String classname;
	 
	 MonitorData()
	 {		
		 mainvec = new Vector<AbstractNode>();
		 speHashMap = new HashMap<Integer,HashMap<Integer,Vector<AbstractNode>>>(); 
	 }
	 
	 public void setClassName(String classname)
	 {
		 this.classname = classname;
	 }
	 public String getClassName()
	 {
		 return classname;
	 }
	 public Vector<AbstractNode> getTrace()
	 {
		 return mainvec;
	 }
	 public void addToTrace(AbstractNode node)
	 {
		 mainvec.add(node);
	 }

//	 public void addToAccessVec(int index, AbstractNode node)
//	 {
//		 mainvec.add(node);
//	 }
	 public void addToSPEHashMap(int defaultHashCode, int spe, AbstractNode node)
	 { 
			HashMap<Integer,Vector<AbstractNode>> map = speHashMap.get(spe);
			if(map==null)
			{
				map = new HashMap<Integer,Vector<AbstractNode>>();
				speHashMap.put(spe, map);
				Vector<AbstractNode> vec = new Vector<AbstractNode>();
				map.put(defaultHashCode,vec);
				vec.add(node);
			}
			else
			{
				Vector<AbstractNode> vec = map.get(defaultHashCode);
				if(vec==null)
				{
					vec = new Vector<AbstractNode>();
					map.put(defaultHashCode, vec);
					vec.add(node);
				}
				else
				{
					vec.add(node);
				}
			}
	 }
	 public HashMap<Integer,HashMap<Integer,Vector<AbstractNode>>> getSPEHashMap()
	 {
		 return speHashMap;
	 }
}
