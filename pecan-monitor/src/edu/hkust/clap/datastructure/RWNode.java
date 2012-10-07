package edu.hkust.clap.datastructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import edu.hkust.clap.engine.TraceEngine;
import edu.hkust.clap.lpxz.context.ContextValueManager;
import edu.hkust.clap.lpxz.context.MethodItsCallSiteLineTuple;
import edu.hkust.clap.organize.SootAgent4Pecan;

public class RWNode extends AbstractNode 
{
	private int line; // which line does the access happen?
	private String mem_str;  // which memory location is accessed?
	private int newmem; // identical as the above field.
	public String msig = null; // which method does the access happen in?
	public String jcode = null; // which jimple IR (soot IR) represents the access?
	 
	private int t_index;  // leave this field alone
	private int atom_index =-1; // leave this field alone
	private Set<Integer> lockset; // leave this field alone
	private LinkedList<Integer> context; // leave this field alone	
	
	private List<MethodItsCallSiteLineTuple> MCPairList= null;// calling stack, for java programs, I maintain it at the trace collection time.
	// HK:for your llvm implementation, you do not need to maintain this field
	// all you need to maintain is the (callsite+methodsignature) pair of a METHOD node (both the entry and exit).
	// Our code will preprocess the trace internally to set the contexts for each RWnodes.

	// main 0          innermost at the last.
//	public long context_num_encoding ; // leave this field alone
//	public void setContext_numEncoding(long contextarg)
//	{
//		this.context_num_encoding = contextarg;
//	}
//	public long getContext_numEncoding()
//	{
//		return context_num_encoding;
//	}
//	
//	public Vector getContext_VectorEncoding()
//	{
//		long tmp= context_num_encoding;
//		Vector toret= ContextValueManager.long2Vector(tmp);
//		Collections.reverse(toret);
//		return  toret;
//	}
	
	
	Vector intContext;
	
	public void setContext_VectorEncoding(Vector contextarg)
	{
		this.intContext = contextarg;
	}

	
	public Vector getContext_VectorEncoding()
	{
		return intContext;// no need to reverse, the vector addes elements in a special order
	}
	

	
	public String getMsig() {
		return msig;
	}
	public void setMsig(String msig) {
		this.msig = msig;
	}
	public String getJcode() {
		return jcode;
	}
	public void setJcode(String jcode) {		
		this.jcode = jcode;
	}
	public RWNode(int line, int mem, long tid, TYPE type)
	{
		super(mem,tid,type);
		
		this.line = line;
		this.MCPairList= null;
	}
	public RWNode(int mem, long tid, TYPE type)
	{
		super(mem,tid,type);
		this.MCPairList= null;
		
	}
	public void setContext(LinkedList<Integer> context)
	{
		this.context = context;
	}
	public LinkedList<Integer> getContext()
	{
		return context;
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
	public void setLockSet(Set<Integer> lockset)
	{
		 this.lockset = lockset;
	}
	public Set<Integer> getLockSet()
	{
		return lockset;
	}
	public void setTIndex(int t_index)
	{
		this.t_index = t_index;
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

	public String toString()
	{
		if(mem_str==null)
			return ID+" "+mem+" "+line+" "+tid+" "+type;
		else
			return ID+" "+mem_str+" "+line+" "+tid+" "+type;
	}
	public String printToString(List steVector)
	{
//		String ctxt = "";
//		
//		for(Integer id : getContext())
//		{
//			ctxt= ctxt + id.intValue() + " ";
//		}
//		
		String ctxt_lp = "";
		
         for (int i= steVector.size()-1; i >=0 ; i--)
         {
//        	 if(stes.get(i) ==null) 
//        	 {
//        		 ctxt_lp = ctxt_lp + "" + "null" + "\n";
//        	 }
//        	 else 
        	 {
        		 String newCtxt = steVector.get(i).toString();
        		 
        		 if(TraceEngine.invokes.size()==0)
        			  TraceEngine.loadMethodsNeedAtomIntention();
        		 boolean tag = false;
        		 for(String atomIntention : TraceEngine.invokes)
        		 {
        			 //spec.jbb.JBBmain.run()(JBBmain.java:127)
        			 int lastleftbrace = atomIntention.lastIndexOf('(');
        			  atomIntention=atomIntention.substring(0, lastleftbrace);
        			  int lastBlank = atomIntention.lastIndexOf(' ');
        			  atomIntention=atomIntention.substring(lastBlank+1);
        	          if(newCtxt.contains(atomIntention))
        	          {
        	        	  tag=true;
        	          }
        		 }
        		 if(tag)
        		   ctxt_lp = ctxt_lp + "** " +newCtxt + "\n";
        		 else
        			 ctxt_lp = ctxt_lp +newCtxt + "\n"; 
			}
        	
         }
         
          
	      
			return ID+" "+line+" "+tid+" "+type + " msig:" + msig + " jcode:" + jcode + " ctxt:\n" + ctxt_lp  ; //ctxt_lp
	}
	
	public String printToString( )
	{
//		String ctxt = "";
//		
//		for(Integer id : getContext())
//		{
//			ctxt= ctxt + id.intValue() + " ";
//		}
//		
		String ctxt_lp = "";
		
         for (int i= MCPairList.size()-1; i >=0 ; i--)
         {
//        	 if(stes.get(i) ==null) 
//        	 {
//        		 ctxt_lp = ctxt_lp + "" + "null" + "\n";
//        	 }
//        	 else 
        	 {
        		 ctxt_lp = ctxt_lp + "" + MCPairList.get(i).toString() + "\n";
			}
        	
         }
         
          
	      
			return ID+" "+line+" "+tid+" "+type + " msig:" + msig + " jcode:" + jcode + " ctxt:\n" + ctxt_lp  ; //ctxt_lp
	}
	
	public List<MethodItsCallSiteLineTuple> getMCPairList() {
		return MCPairList;
	}
	public void setMCPairList_deepClone(List<MethodItsCallSiteLineTuple> mCPairList) {
	//	if(mCPairList==null) throw new  RuntimeException("at least one method should be in the list, so the list is not null!");
		
	    // HK may takes the main method as having no context..
		MCPairList = new ArrayList<MethodItsCallSiteLineTuple>();
		if(mCPairList !=null ) 
		{
			MCPairList.addAll(mCPairList);
		}
	
	}
	
	
}