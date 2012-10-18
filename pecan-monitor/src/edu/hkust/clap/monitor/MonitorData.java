package edu.hkust.clap.monitor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;

import edu.hkust.clap.datastructure.AbstractNode;
import edu.hkust.clap.datastructure.RWNode;
import edu.hkust.clap.lpxz.context.ContextValueManager;

public class MonitorData implements Serializable
{
	 /**
	 * 
	 */
	 private static final long serialVersionUID = 1L;
	 private Vector<AbstractNode> mainvec;
	// private HashMap<Integer,HashMap<Integer,Vector<RWNode>>> speHashMap;
	 private HashMap<Integer, Vector<RWNode>> rtId2Vector ;
	 private String classname;
	 
	 public List<String> ctxts = null;// contain the list of all the contexts.
	 
	 public MonitorData()
	 {		
		 mainvec = new Vector<AbstractNode>();
		 rtId2Vector = new HashMap<Integer, Vector<RWNode>>();
		 ctxts =ContextValueManager.ctxts;// for dumping
		// speHashMap = new HashMap<Integer,HashMap<Integer,Vector<RWNode>>>(); 
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

	 
	 public void addRTid2Vector(int rtID , RWNode node)
	 {
		Vector<RWNode > vector = rtId2Vector.get(rtID);
		if(vector ==null)
		{
			vector = new Vector<RWNode>();
			rtId2Vector.put(rtID, vector);
		}
		
		vector.add(node);
		
	 }
	 
	 public HashMap<Integer, Vector<RWNode>> getRTid2Vector()
	 {
		 return rtId2Vector;
	 }
//	 public void addToAccessVec(int index, AbstractNode node)
//	 {
//		 mainvec.add(node);
//	 }
//	 public void addToSPEHashMap(int defaultHashCode, int spe, RWNode node)
//	 { 
//			HashMap<Integer,Vector<RWNode>> map = speHashMap.get(spe);
//			if(map==null)
//			{
//				map = new HashMap<Integer,Vector<RWNode>>();
//				speHashMap.put(spe, map);
//				Vector<RWNode> vec = new Vector<RWNode>();
//				map.put(defaultHashCode,vec);
//				vec.add(node);
//			}
//			else
//			{
//				Vector<RWNode> vec = map.get(defaultHashCode);
//				if(vec==null)
//				{
//					vec = new Vector<RWNode>();
//					map.put(defaultHashCode, vec);
//					vec.add(node);
//				}
//				else
//				{
//					vec.add(node);
//				}
//			}
//	 }
//	 public HashMap<Integer,HashMap<Integer,Vector<RWNode>>> getSPEHashMap()
//	 {
//		 return speHashMap;
//	 }
}
