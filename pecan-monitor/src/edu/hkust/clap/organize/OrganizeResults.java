package edu.hkust.clap.organize;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;




import soot.Body;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.Stmt;

import edu.hkust.clap.datastructure.RWNode;

import edu.hkust.clap.lpxz.context.ContextValueManager;
import edu.hkust.clap.lpxz.context.MethodItsCallSiteLineTuple;

public class OrganizeResults {

//	*** AV-I --- "org.exolab.jms.net.connector.ManagedConnectionHandle._connectionCount" --- 66673 273 21 READ msig:<org.exolab.jms.net.connector.ManagedConnectionHandle: void incActiveConnections()> jcode:$i0 = r0.<org.exolab.jms.net.connector.ManagedConnectionHandle: int _connectionCount> ctxt:
//	org.exolab.jms.net.connector.ManagedConnectionHandle.incActiveConnections(ManagedConnectionHandle.java:272)
//	org.exolab.jms.net.connector.ManagedConnectionHandle.access$0(ManagedConnectionHandle.java:307)
//	org.exolab.jms.net.connector.ManagedConnectionHandle$ConnectionHandle.<init>(ManagedConnectionHandle.java:148)
//	org.exolab.jms.net.connector.ManagedConnectionHandle.getConnection(AbstractConnectionManager.java:142)
//	org.exolab.jms.net.connector.AbstractConnectionManager.allocateConnection(AbstractConnectionFactory.java:167)
//	org.exolab.jms.net.connector.AbstractConnectionFactory.getConnection(AbstractConnectionManager.java:208)
//	org.exolab.jms.net.connector.AbstractConnectionManager.getConnection(AbstractConnectionManager.java:187)
//	org.exolab.jms.net.connector.AbstractConnectionManager.getConnection(ConnectionContext.java:122)
//	org.exolab.jms.net.connector.ConnectionContext.getConnection(UnicastDelegate.java:216)
//	org.exolab.jms.net.orb.UnicastDelegate.readObject(Native Method)
//	org.exolab.jms.net.util.SerializationHelper.read(Response.java:174)
//	org.exolab.jms.net.connector.Response.read(Channel.java:158)
//	org.exolab.jms.net.multiplexer.Channel.invoke(MultiplexedManagedConnection.java:352)
//	org.exolab.jms.net.multiplexer.MultiplexedManagedConnection.invoke(MultiplexedConnection.java:86)
//	org.exolab.jms.net.multiplexer.MultiplexedConnection.invoke(ManagedConnectionHandle.java:322)
//	org.exolab.jms.net.connector.ManagedConnectionHandle$ConnectionHandle.invoke(UnicastDelegate.java:153)
//	org.exolab.jms.net.orb.UnicastDelegate.invoke(Proxy.java:102)
//	org.exolab.jms.net.proxy.Proxy.invoke(RegistryImpl__Proxy.java:39)
//	org.exolab.jms.client.net.JmsServerStubImpl.getServerConnectionFactory(JmsServerStubImpl.java:169)
//	org.exolab.jms.client.net.JmsServerStubImpl.createConnection(JmsConnection.java:174)
//	org.exolab.jms.client.JmsConnection.<init>(JmsConnectionFactory.java:284)
//	org.exolab.jms.client.JmsConnectionFactory.createConnection(JmsConnectionFactory.java:266)
//	org.exolab.jms.client.JmsConnectionFactory.createConnection(Receiver.java:115)
//	driver.Receiver.run(-1)
//	 * 61309 280 19 WRITE msig:<org.exolab.jms.net.connector.ManagedConnectionHandle: void decActiveConnections()> jcode:r0.<org.exolab.jms.net.connector.ManagedConnectionHandle: int _connectionCount> = $i1 ctxt:
//	org.exolab.jms.net.connector.ManagedConnectionHandle.decActiveConnections(ManagedConnectionHandle.java:279)
//	org.exolab.jms.net.connector.ManagedConnectionHandle.access$2(ManagedConnectionHandle.java:359)
//	org.exolab.jms.net.connector.ManagedConnectionHandle$ConnectionHandle.close(UnicastDelegate.java:169)
//	org.exolab.jms.net.orb.UnicastDelegate.dispose(Proxy.java:79)
//	org.exolab.jms.net.proxy.Proxy.disposeProxy(JmsServerStubImpl.java:266)
//	org.exolab.jms.client.net.JmsServerStubImpl.getServerConnectionFactory(JmsServerStubImpl.java:169)
//	org.exolab.jms.client.net.JmsServerStubImpl.createConnection(JmsConnection.java:174)
//	org.exolab.jms.client.JmsConnection.<init>(JmsConnectionFactory.java:284)
//	org.exolab.jms.client.JmsConnectionFactory.createConnection(JmsConnectionFactory.java:266)
//	org.exolab.jms.client.JmsConnectionFactory.createConnection(Sender.java:119)
//	driver.Sender.run(-1)
//	 * 67012 280 21 WRITE msig:<org.exolab.jms.net.connector.ManagedConnectionHandle: void decActiveConnections()> jcode:r0.<org.exolab.jms.net.connector.ManagedConnectionHandle: int _connectionCount> = $i1 ctxt:
//	org.exolab.jms.net.connector.ManagedConnectionHandle.decActiveConnections(ManagedConnectionHandle.java:279)
//	org.exolab.jms.net.connector.ManagedConnectionHandle.access$2(ManagedConnectionHandle.java:359)
//	org.exolab.jms.net.connector.ManagedConnectionHandle$ConnectionHandle.close(UnicastDelegate.java:169)
//	org.exolab.jms.net.orb.UnicastDelegate.dispose(Proxy.java:79)
//	org.exolab.jms.net.proxy.Proxy.disposeProxy(JmsServerStubImpl.java:266)
//	org.exolab.jms.client.net.JmsServerStubImpl.getServerConnectionFactory(JmsServerStubImpl.java:169)
//	org.exolab.jms.client.net.JmsServerStubImpl.createConnection(JmsConnection.java:174)
//	org.exolab.jms.client.JmsConnection.<init>(JmsConnectionFactory.java:284)
//	org.exolab.jms.client.JmsConnectionFactory.createConnection(JmsConnectionFactory.java:266)
//	org.exolab.jms.client.JmsConnectionFactory.createConnection(Receiver.java:115)
//	driver.Receiver.run(-1)
//	 ***
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	
	static List<MethodItsCallSiteLineTuple> pv_list =  new  ArrayList<MethodItsCallSiteLineTuple>();
	static List<MethodItsCallSiteLineTuple> cv_list =  new  ArrayList<MethodItsCallSiteLineTuple>();
	static List<MethodItsCallSiteLineTuple> rv_list =  new  ArrayList<MethodItsCallSiteLineTuple>();
	
	public static CSMethod pc2csMethod(RWNode pnode, RWNode cnode) {
		pv_list.clear();
		cv_list.clear();
		// help setting the pnode's MCPairList, for human readable
		ContextValueManager.getMCPairV(pnode, pv_list);
		ContextValueManager.getMCPairV(cnode, cv_list);
		
		System.out.println("p context");
		for(MethodItsCallSiteLineTuple mPair : pv_list)
		{
			System.out.println(mPair.curMsig + "called at " + mPair.theCallsite);
		}
		
		System.out.println("c context");
		for(MethodItsCallSiteLineTuple mPair : cv_list)
		{
			System.out.println(mPair.curMsig + "called at " + mPair.theCallsite);
		}
		
		
	   int i=0;
	   while(i!=pv_list.size() && i!= cv_list.size())
	   {
		   MethodItsCallSiteLineTuple pvEle = pv_list.get(i);
		   MethodItsCallSiteLineTuple cvEle = cv_list.get(i);
		   if(pvEle.equals(cvEle))
		   {
			   i++;
		   }else {
			   // at i, different
			   // maybe c different, maybe m different, the handling is the same
			   CSMethod ret = new CSMethod();
		        ret.setMethod_type(CSMethod.PCMethod);
			   for(int k=0; k<= i-1; k++)
				{
					ret.MCpairList.add((MethodItsCallSiteLineTuple)pv_list.get(k));
				}
			   
				ret.setMsig(((MethodItsCallSiteLineTuple)pv_list.get(i-1)).curMsig);
				ret.setpAnc(((MethodItsCallSiteLineTuple)pv_list.get(i)).theCallsite.toString());
				ret.setpAncLine(((MethodItsCallSiteLineTuple)pv_list.get(i)).lineOfCallSite);
				ret.setcAnc(((MethodItsCallSiteLineTuple)cv_list.get(i)).theCallsite.toString());// the Ci is given birth by Mi-1, so, Ci and Ci' belong to the same Mi-1
				ret.setcAncLine(((MethodItsCallSiteLineTuple)cv_list.get(i)).lineOfCallSite);
				
				
				System.out.println("pcMsig:" + ret.getMsig());
				System.out.println("p:" + ret.getpAnc());
				System.out.println("c:" + ret.getcAnc());
				
				return ret;
			   
		   }		   
	   }		
		
		// 1 determine the ctxt

		// 2 determine the msig		
		
		// 3 determine the anscestor!
		if(i==0)
		{
			throw new RuntimeException("at least common context: main or run");
		}
		
		CSMethod ret = new CSMethod();
        ret.setMethod_type(CSMethod.PCMethod);	
        int lastCommon = i-1; // i is the first uncommon index.
		for(int k=0; k<= lastCommon; k++)// i==pv_list.size of cv_list.size
		{
			ret.MCpairList.add((MethodItsCallSiteLineTuple)pv_list.get(k));
		}
		ret.setMsig(((MethodItsCallSiteLineTuple)pv_list.get(lastCommon)).curMsig);
		
		if(i==pv_list.size() && i !=cv_list.size())
		{			
			ret.setpAnc(pnode.getJcode());
			ret.setpAncLine(pnode.getLine());
			ret.setcAnc(((MethodItsCallSiteLineTuple)cv_list.get(i)).theCallsite.toString());
			ret.setcAncLine(((MethodItsCallSiteLineTuple)cv_list.get(i)).lineOfCallSite);
		}
		if(i!=pv_list.size() && i ==cv_list.size())
		{
			ret.setpAnc(((MethodItsCallSiteLineTuple)pv_list.get(i)).theCallsite.toString());
			ret.setpAncLine(((MethodItsCallSiteLineTuple)pv_list.get(i)).lineOfCallSite);
			ret.setcAnc(cnode.getJcode());
			ret.setcAncLine(cnode.getLine());
		}
		if(i==pv_list.size() && i ==cv_list.size())
		{
			ret.setpAnc(pnode.getJcode());
			ret.setpAncLine(pnode.getLine());
			ret.setcAnc(cnode.getJcode());
			ret.setcAncLine(cnode.getLine());
		}
		
		
		System.out.println("pcMsig:" + ret.getMsig());
		System.out.println("p:" + ret.getpAnc());
		System.out.println("c:" + ret.getcAnc());
		

		
		
		return ret;

	}
	

	public static CSMethod r2csMethod(RWNode rnode) {
        CSMethod ret = new CSMethod();
        ret.setMethod_type(CSMethod.RMethod);
        
		rv_list.clear();
		ContextValueManager.getMCPairV(rnode, rv_list);
		
		ret.MCpairList.addAll(rv_list);
		
		ret.setMsig((rv_list.get(rv_list.size()-1)).curMsig);
        ret.setrAnc(rnode.getJcode());
        ret.setrAncLine(rnode.getLine());
        
        //correctness checking:
        SootMethod sm = Scene.v().getMethod(ret.getMsig());
        sm.retrieveActiveBody();
       Body bb = sm.getActiveBody();
       if(!bb.toString().contains(rnode.getJcode()))// coorec
       {
    	   System.err.println(rnode.getMsig());
    	   System.out.println(ret.getMsig());
    	   throw new RuntimeException("fatal");
       }

		return ret;
		
	}

}
