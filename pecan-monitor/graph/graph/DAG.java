
/*
*************************************************************************                           

 Copyright (c) 1998 Alexander Sakharov   All rights reserved.                

The files from this package are not public-domain software nor shareware.
One may not redistribute files from this package without written
permission from Alexander Sakharov. One may not sell amy product derived
from sources of the package without a written permission from Alexander
Sakharov. If code from this package is used elsewhere, this copyright
notice shall accompany every code fragment from the package, and the
following web page reference shall be included:
http://members.aol.com/asakharov/graph.html
 
There is no any warranty for the files of this distribution. Alexander 
Sakharov makes no representation with respect to the adequacy of this 
distribution package for any particular purpose or with respect to its 
adequacy to produce any particular result. Alexander Sakharov shall not
be liable for loss or damage arising out of the use of the package 
regardless of how sustained. In no event shall Alexander Sakharov be 
liable for special, direct, indirect or consequential damage, loss, 
costs or fees or expenses of any nature or kind. 

*************************************************************************
*/

/* DAG.java */

package graph;

import java.util.*;

/**
 * This class supports processing of so-called recursive data types.
 * Recursive types are classes containing members of the same type.
 * Objects of recursive types are usually viewed as directed graphs. 
 * Recursive references represent directed edges.
 * Each object of a recursive class represents 
 * a node in a graph as well as 
 * a subgraph rooted at this node.  
 
 * Rooted directed acyclic graphs (DAG) represent a subset of graphs.
 * They have no loops, i.e. descending down edges never leads to 
 * the same node.
 * Each object of class DAG also can be viewed as
 * a representation of the subDAG whose top node is this object.
 * Every node holds references to children and the root. 
 * Access to immediate children of each node is indexed.
 *
 * DAG is an abstract class. Methods 
 * nodeEqual and nodeCopy are abstract and should be defined 
 * in a derived class.
 * Class DAG contains methods that implement various DAG modifications.
 * It also has methods implementing DAG comparison and pattern matching.
 * Terminal nodes called slots are utilized as place holders matched
 * against DAGs.
 * This class is useful for processing symbolic expressions.
 * 
@version 1.1.1
@author Alexander Sakharov
@see graph.Tree
 */

public abstract class DAG extends Object implements Cloneable {

	DAG m_root;				// DAG root node 
	Vector m_children = new Vector();	// node children
	static private Object m_dummy = new Object();	// 

public DAG() {
	m_root = this;
}

/**
 * Yield the child with the specified index.
 * 
 * @param n child index
*/
public DAG getReference(int n) {
	if ( isSlot() )
		return null;
	if ( m_children.size()<n )
		return null; 
	return (DAG)(m_children.elementAt(n-1));
}

/**
 * Replaces the child with the specified index with the specified DAG.
@param n index of the child to replace
@param g DAG that replaces a child
*/
protected void setReference(int n,DAG g) {
	if ( isSlot() )
		throw new RuntimeException("DAG");
	if ( m_children.size()<n )
		m_children.setSize(n);
	m_children.setElementAt(g,n-1);
}

/**
Yields the maximum possible child index.
*/
protected int maxIndex() {
	if ( isSlot() )
		return 0;
	return m_children.size();
}

/**
 * Returns the boolen value indicating if this node is equal to the specified node.
 * Children are not taken in consideration here. Even null vs non-null
 * values are not compared.
@param g node to compare with this
*/
protected abstract boolean nodeEqual(DAG g);

/**
Clones a node without cloning children
*/
protected abstract DAG nodeCopy();

/**
 * Turns this, which is presumably a leaf node, into a slot and  
 * associates the specified DAG with the slot.
 * The associated DAG is a place holder for match method.
 * The DAG associated with this should be root and leaf.
 * The first child of the associated DAG will be used 
 * for storing DAGs by methods match/weakMatch. 
 * This retains its leaf node status.
@param t associated DAG
 *
*/
public void slot(DAG t) {
	if ( m_children.size()!=0 || t.m_children.size()!=0 
	|| t.m_root!=t || t==null )
		throw new RuntimeException("DAG");
	m_children.setSize(2);
	m_children.setElementAt(m_dummy,0);
	m_children.setElementAt(t,1);
}

/**
 * Returns true if this is a slot. Otherwise, false is returned.
*/
public boolean isSlot() {
	if ( m_children.size()!=2 )
		return false;
	return m_children.elementAt(0)==m_dummy;
}

/**
 * Yields the value stored as the first child of the DAG associated with this slot.
 * 
*/
public DAG getSlotValue() {
	if ( !isSlot() )
		throw new RuntimeException("DAG");
	if ( ((DAG)m_children.elementAt(1)).m_children.size()==0 )
		return null;
	else
		return (DAG)(((DAG)m_children.elementAt(1)).m_children.elementAt(0));
}

/**
 * Stores the specified DAG as the first child of the DAG associated with this slot.
@param g DAG that is assigned to this slot
*/
public void setSlotValue(DAG g) {
	if ( !isSlot() ) 
		throw new RuntimeException("DAG");
	if ( g!=null )
		if ( g.m_root!=g )
			throw new RuntimeException("DAG");
	((DAG)m_children.elementAt(1)).m_children.setSize(1);
	((DAG)m_children.elementAt(1)).m_children.setElementAt(g,0);
}

/**
 * Returns the index of the next child.
 * Non-null children are skipped.
 * If there is no next child, then -1 is returned.
@param n current index
*/
public int nextIndex(int n)
{ 
	int k;
// ---
	if ( n<0 )
		return -1;
	for(k=n;;k++) {
		if ( k>maxIndex() )
			return -1;
		if ( getReference(k)!=null )
			return k;
	}
}

/**
 * Returns the index of the first non-null child.
 * Null children are skipped. 
 * If there is no next child, then -1 is returned.
*/
public int firstIndex()
{ 
	return nextIndex(1);
}

/**
 * Returns the index of the last non-null child or -1 
 * if there are no children.
*/
public int lastIndex()
{ 
	int k;
	int last;
// ---
	for(k=maxIndex();k>0;k--) {
		if ( getReference(k)!=null ) 
			return k;
	}
	return -1;
}

/**
 * Cuts off all children from this.
*/
public void cutoff()
{
	m_children.setSize(0);
}

/**
 * Returns the root.
*/
public DAG getRoot()
{ 
// ---
	return m_root;
}

/**
 * Creates an instance of DAGIterator for use in iteration over the nodes of 
 * the DAG rooted at this.
*/
public DAGIterator iterator()
{ 
	DAGIterator s;
// ---
	s = new DAGIterator(this);
	return s;
}

/**
 * Checks if this is a DAG root.
*/
public boolean isRoot()
{ 
// ---
	return m_root==this;
}

/**
 * Check if this has no children nodes.
*/
public boolean isLeaf()
{
	int k;
// ---
	k=nextIndex(1);
	return k<1;
}

/**
 * Checks if this and the specified DAG have the same root.
@param d the DAG to check if it is an alias of this
*/
public boolean aliasOf(DAG d)
{ 
// ---
	return m_root==d.m_root;
}

/**
Checks if the specified DAG is a subDAG of this.
@param d the DAG to check if it is a subDAG of this
*/
public boolean contains(DAG d)
{ 
// ---
	DAG nd;
	DAGIterator s; 
// ---
	s= new DAGIterator(this); 
	while ( (nd=s.preorder())!=null )
		if ( nd==d )
			return true;
	return false;
}

/**
 * Clones this DAG. It is used internally by other methods of the class DAG
 * to make copies of subDAGs that will be eventually substituted into other DAGs 
 * or contitute new DAGs.
@param d the new top node. If it is null, then the newly generated copy of
	this node becomes the root. 
*/
synchronized DAG copy(DAG d) 
{ 
	DAG nd;
	DAG r;
	DAG t;
	DAGIterator s;
	DAGIterator ns; 
	Vector g;
	Vector ng;
	int n; 
	int m;
// ---
	if ( this==null )
		return null;

	s = new DAGIterator(this);
	g = new Vector(); 
	ng = new Vector();

	// loop over all DAG nodes
	while ( (nd=s.preorder())!=null ) { 
		n=g.indexOf(nd);
		if ( n>=0 ) {		// already visited node
			if ( s.nodes.size()>1 ) {
				t=(DAG)s.nodes.pop();
				m=g.indexOf(s.nodes.peek());
				((DAG)ng.elementAt(m)).setReference(((Integer)s.counts.peek()).intValue(),(DAG)ng.elementAt(n));
				s.nodes.push(t);	
			}
		} else {			// node visited for the first time
			g.addElement(nd);
			r= nd.nodeCopy();
			if ( nd.isSlot() ) {
				r.m_children.setSize(2);
				r.m_children.setElementAt(m_dummy,0);
				r.m_children.setElementAt(nd.m_children.elementAt(1),1);
			}
			// make copy of this node the root of the new DAG
			if ( d==null )	
				d=r;
			r.m_root=d;
			ng.addElement(r);
			if ( s.nodes.size()>1 ) {
				t=(DAG)s.nodes.pop();
				m=g.indexOf(s.nodes.peek());
				((DAG)ng.elementAt(m)).setReference(((Integer)s.counts.peek()).intValue(),r);
				s.nodes.push(t);
			}
		} 
	}
	return (DAG)ng.firstElement();
}

/**
 * Embeds the specified DAG as a child of this DAG.
 * The specified DAG replaces the specified child of this.
 * The specified DAG should not be a subDAG unless
 * the two have the same root.
@param p DAG to embed
@param n index of the child to be replaced 
@exception RuntimeException index is out of bounds
or an attempt is made to embed a subDAG which is not a part
of this DAG or is a slot
*/
public synchronized void embed(DAG p, int n)
{
// ---
	DAG nd;
	DAGIterator s; 
// ---
	if ( n<0 )
		throw new RuntimeException("DAG");
	if ( isSlot() )
		throw new RuntimeException("DAG");
	// Only whole DAG or a subDAG of this can be moved
	if ( p!=null )
		if ( p.m_root!=p && p.m_root!=m_root )
			throw new RuntimeException("DAG");
// ---
	if ( this.getReference(n)!=null )
		throw new RuntimeException("DAG");
	this.setReference(n,p);
	s= new DAGIterator(p); 
	while ( (nd=s.preorder())!=null )
		nd.m_root=m_root;
}

/**
 * Clones the specified DAG and embeds it as a child of this tree.
 * The copy of the specified DAG replaces the specified child of this.
@param p DAG to clone and embed
@param n index of the child to be replaced 
@exception RuntimeException index is out of bounds or this is a slot
*/
public synchronized void embedCopy(DAG p, int n)
{
// ---
	if ( n<0 )
		throw new RuntimeException("DAG");
	if ( isSlot() )
		throw new RuntimeException("DAG");
// ---
	if ( this.getReference(n)!=null )
		throw new RuntimeException("DAG");
	this.setReference(n,p.copy(m_root));
}

/**
 * Duplicates the tip node of the specified DAG and inserts it between this and its parents.
 * This becomes the n-th child of the duplicated node.
 * All other references of the duplicated node are set to null. 
 * This method can be modeled by other methods of class 
 * DAG but it is simpler and more efficient to use insertNodeCopy when it is 
 * necessary to insert new nodes into an existing tree.
@param d DAG whose tip inserted atop of this
@param n index of the child this is converted into
@exception RuntimeException null DAG parameter
*/
public synchronized void insertNodeCopy(DAG d, int n)
{ 
	DAG r;
	DAG nd;
	int k;
	int i;
	DAGIterator s;
// --- 
	if ( d==null )
		throw new RuntimeException("DAG");
// ---
	r=d.nodeCopy();
	r.m_root=m_root;
	// set references in d's copy
	k=r.nextIndex(1);
	while ( k>=0 ) {
		if ( k==n )
			r.setReference(k,this);
		else
			r.setReference(k,null); 
		k=r.nextIndex(k);
	}
	// update references to this
	s = new DAGIterator(m_root); 
	while ( (nd=s.preorder())!=null )
		for (i=1;i<=nd.maxIndex();i++) { 
			if ( nd.getReference(i)==null )
				continue;
			if ( nd==this )
				continue;
			if ( nd.getReference(i)==this )
				nd.setReference(i,r);
		}
}

/**
 * Substitutes the specified DAG for this.
 * The specified DAG should not be a subDAG unless
 * the two have the same root.
 * Note that references to nodes below this from outside
 * of this remain analtered.
@param p DAG to substitute
@exception RuntimeException this is a root
or an attempt is made to substitute a subDAG that is not a part of this DAG
*/
public synchronized void substitute(DAG p)
{ 
	DAGIterator s; 
	DAG nd;
	int i;
// ---
	// cannot substitute for the root
	if ( this == m_root )
		throw new RuntimeException("DAG");
	// Only whole DAG or a subDAG of this can be moved
	if ( p!=null )
		if ( p.m_root!=p && p.m_root!=m_root )
			throw new RuntimeException("DAG");
// ---
	s = new DAGIterator(m_root); 
	if ( p==null )
		if ( this==null )
			return;
	// update references to this
	while ( (nd=s.preorder())!=null ) {
		if ( nd.isSlot() )
			continue;
		for (i=1;i<=nd.maxIndex();i++) { 
			if ( nd.getReference(i)==null )
				continue;
			if ( nd.getReference(i)==this )
				nd.setReference(i,p);
		}
	}
	if ( p!=null )
		if ( p.m_root==p ) {	// update m_root of cloned nodes
			s = new DAGIterator(p); 
			while ( (nd=s.preorder())!=null )
				nd.m_root=m_root;
		}
}

/**
 * Checks if no reference from outside of this leads to a node below this. 
*/
public boolean coupled()
{ 
	DAGIterator s; 
	DAG nd;
	int i;
// ---
	s = new DAGIterator(m_root);
	while ( (nd=s.preorder())!=null ) {
		if ( nd == this ) {
			s.skip();
			continue;
		}
		for (i=1;i<=nd.maxIndex();i++)  
			if ( nd.getReference(i)!=this && contains(nd.getReference(i)) ) 
				return true;
	}
	return false;
}

/**
 * Clones and substitutes the specified DAG for this.
 * Note that references to nodes below this from outside
 * of this remain analtered.
@param p DAG to clone and substitute
@exception RuntimeException this is a root
*/
public synchronized void substituteCopy(DAG p)
{ 
	DAGIterator s; 
	DAG nd;
	DAG u; 
	int i;
// ---
	// cannot substitute for the root
	if ( this == m_root )
		throw new RuntimeException("DAG");
// ---
	s= new DAGIterator(m_root); 
	if ( p==null ) {
		if ( this==null )
			return;
		u=null;
	} else {
		u=p.copy(m_root); 
	}
	// update references to this
	while ( (nd=s.preorder())!=null ) {
		if ( nd.isSlot() )
			continue;
		for (i=1;i<=nd.maxIndex();i++) { 
			if ( nd.getReference(i)==null )
				continue;
			if ( nd.getReference(i)==this )
				nd.setReference(i,u);
		}
	}
}

/**
 * Compares the specified DAG with this.
 * Returns a boolean indicating whether the two DAGs are equal, i.e. 
 * there is such one-to-one mapping 
 * between both the nodes and edges of the two DAGs that 
 * the contensts of the respective nodes are equal (which is checked
 * by nodeEqual) and that respective edges connect corresponding start 
 * and end nodes.
@return true if the DAGs are equal
@param r DAG to compare with this
@exception RuntimeException this or the specified DAG is a slot
*/
public boolean congruent(DAG r)
{ 
	DAGIterator sp;
	DAGIterator sr; 
	Vector gp;
	Vector gr;
	DAG ndp;
	DAG ndr; 
	int i;
// ---
	if ( isSlot() )
		throw new RuntimeException("DAG");
	if ( r.isSlot() )
		throw new RuntimeException("DAG");
// ---
	if (this==r)
		return true;
	if (this==null || r==null)
		return false;

	sp = new DAGIterator(this);
	sr = new DAGIterator(r);
	gp = new Vector(); 
	gr = new Vector(); 

	ndp=sp.preorder();
	ndr=sr.preorder();
	// simultaneous traversal of both DAGs
	for(;ndp!=null && ndr!=null;) {
		if ( !(ndp.nodeEqual(ndr)) ) 
			return false;
		if ( ndp.maxIndex()!=ndr.maxIndex() ) 
			return false;
		// Checking if this is 1-to-1 mapping
		if ( gp.indexOf(ndp)!=gr.indexOf(ndr) )
			return false;
		gp.addElement(ndp);
		gr.addElement(ndr);
		// checking for null vs non-null children
		for (i=1;i<=ndp.maxIndex(); i++) {
			if ( ndp.getReference(i)==null && ndr.getReference(i)!=null )
				return false;
			if ( ndp.getReference(i)!=null && ndr.getReference(i)==null )
				return false;		
		}
		ndp=sp.preorder();
		ndr=sr.preorder();
	}
	// checking if both traversals are completed
	if ( ndp==ndr )
		return true;
	else
		return false;
}

/**
 * Compares the specified DAG with this.
 * Returns a boolean indicating whether there is such mapping 
 * between the nodes of two DAGs that all edges are mapped one-to-one 
 * and the contensts of the respective nodes are equal (which is checked
 * by nodeEqual). Several nodes of one DAG may map into one node of 
 * the other DAG.
@return true if there is the mapping
@param r DAG to compare with this
@exception RuntimeException this or the specified DAG is a slot
*/
public boolean similar(DAG r)
{ 
	DAGIterator sp;
	DAGIterator sr; 
	DAG ndp;
	DAG ndr; 
	int i;
// ---
	if ( isSlot() )
		throw new RuntimeException("DAG");
	if ( r.isSlot() )
		throw new RuntimeException("DAG");
// ---
	if (this==r)
		return true;
	if (r==null)
		return false;

	sp = new DAGIterator(this);
	sr = new DAGIterator(r);

	ndp=sp.preorder();
	ndr=sr.preorder();
	// simultaneous traversal of both DAGs
	for(;ndp!=null && ndr!=null;) {
		if ( !(ndp.nodeEqual(ndr)) ) 
			return false;
		if ( ndp.maxIndex()!=ndr.maxIndex() ) 
			return false;
		// checking for null vs non-null children
		for (i=1;i<=ndp.maxIndex(); i++) { 
			if ( ndp.getReference(i)==null && ndr.getReference(i)!=null )
				return false;
			if ( ndp.getReference(i)!=null && ndr.getReference(i)==null )
				return false;		
		}
		ndp=sp.preorder();
		ndr=sr.preorder();
	}
	// checking if both traversals are completed
	if ( ndp==ndr )
		return true;
	else
		return false;
}

/**
 * Clones this DAG.
 * It may create a standalone DAG out of a subDAG.
*/
public Object clone()
{ 
// ---
	return copy(null);
}

/**
 * Checks if this and the specified DAG match. 
 * Returns a boolean indicating whether they match. This may contain slots
 * which play the role of variables matching any DAG. Thus, this DAG serves as a pattern.
 * If the two DAGs match, then the counterparts of all slots become the first children
 * of the DAGs associated with slots. Even if match yields false, still DAGs 
 * could be assigned to
 * some DAGs associated with slots. The user can apply the method clean to cut the
 * first children of the associated DAGs off.
 * All slots associated with the same DAG should match congruent DAGs 
 * in order for match to succeed.
 * If this method yields true, it means that there is such one-to-one mapping 
 * between the non-slot nodes of the two DAGs that all edges are also mapped one-to-one
 * and the contensts of the respective nodes are equal, which is checked
 * by nodeEqual.
@return true if this and the specified DAG match
@param r DAG to match against this
@see congruent
@see clean
*/
public boolean match(DAG r)
{ 
	DAGIterator sp;
	DAGIterator sr; 
	Vector gp;
	Vector gr;
	DAG ndp;
	DAG ndr; 
	int i;
// ---
	if (this==r)
		return true;
	if (this==null || r==null)
		return false;

	sp = new DAGIterator(this);
	sr = new DAGIterator(r);
	gp = new Vector(); 
	gr = new Vector(); 

	ndp=sp.preorder();
	ndr=sr.preorder();
	// simultaneous traversal of both DAGs
	for(;ndp!=null && ndr!=null;) {
		if ( ndp.isSlot() ) {
			if ( ndp.getSlotValue()==null ) {
				ndp.setSlotValue((DAG)ndr.clone());
			} else {
				if ( !ndp.getSlotValue().congruent(ndr) )
					return false;
			}
			sp.skip();
			sr.skip();
			ndp=sp.preorder();
			ndr=sr.preorder();
			continue;
		}
		if ( !(ndp.nodeEqual(ndr)) ) 
			return false;
		if ( ndp.maxIndex()!=ndr.maxIndex() ) 
			return false;
		// Checking if this is 1-to-1 mapping
		if ( gp.indexOf(ndp)!=gr.indexOf(ndr) )
			return false;
		gp.addElement(ndp);
		gr.addElement(ndr);
		// checking for null vs non-null children
		for (i=1;i<=ndp.maxIndex(); i++) {
			if ( ndp.getReference(i)==null && ndr.getReference(i)!=null )
				return false;
			if ( ndp.getReference(i)!=null && ndr.getReference(i)==null )
				return false;		
		}
		ndp=sp.preorder();
		ndr=sr.preorder();
	}
	// checking if both traversals are completed
	if ( ndp==ndr )
		return true;
	else
		return false;
}

/**
 * Checks if this and the specified DAG match with respect to the similarity test.
 * Returns a boolean indicating whether they match. This may contain slots
 * which play the role of variables matching any DAG. Thus, this DAG serves as a pattern.
 * If the two DAGs match, then the counterparts of all slots become the first children
 * of the DAGs associated with slots. Even if match yields false, still DAGs 
 * could be assigned to
 * some DAGs associated with slots. The user can apply the method clean to cut the
 * first children of the associated DAGs off.
 * All slots associated with the same DAG should match similar DAGs 
 * in order for match to succeed.
 * If this method yields true, it means that there is such mapping 
 * between the non-slot nodes of the two DAGs that all edges are mapped one-to-one
 * and the contensts of the respective nodes are equal, which is checked
 * by nodeEqual. Several nodes of one DAG may map into one node of 
 * the other DAG.
@return true if this and the specified DAG match
@param r DAG to match against this
@see congruent
@see clean
*/
public boolean weakMatch(DAG r)
{ 
	DAGIterator sp;
	DAGIterator sr; 
	DAG ndp;
	DAG ndr; 
	int i;
// ---
	if (this==r)
		return true;
	if (r==null)
		return false;

	sp = new DAGIterator(this);
	sr = new DAGIterator(r);

	ndp=sp.preorder();
	ndr=sr.preorder();
	// simultaneous traversal of both DAGs
	for(;ndp!=null && ndr!=null;) {
		if ( ndp.isSlot() ) {
			if ( ndp.getSlotValue()==null ) { 
				ndp.setSlotValue((DAG)ndr.clone());
			} else {
				if ( !ndp.getSlotValue().similar(ndr) )
					return false;
			}
			sp.skip();
			sr.skip();
			ndp=sp.preorder();
			ndr=sr.preorder();
			continue;
		}
		if ( !(ndp.nodeEqual(ndr)) ) 
			return false;
		if ( ndp.maxIndex()!=ndr.maxIndex() ) 
			return false;
		// checking for null vs non-null children
		for (i=1;i<=ndp.maxIndex(); i++) { 
			if ( ndp.getReference(i)==null && ndr.getReference(i)!=null )
				return false;
			if ( ndp.getReference(i)!=null && ndr.getReference(i)==null )
				return false;		
		}
		ndp=sp.preorder();
		ndr=sr.preorder();
	}
	// checking if both traversals are completed
	if ( ndp==ndr )
		return true;
	else
		return false;
}

/**
 * Cuts off the first children of all DAGs associated with slots in this DAG. 
 * This method cleans a pattern DAG 
 * to be matched against another one again.
*/
public void clean()
{ 
// ---
	DAG nd;
	DAGIterator s; 
// ---
	s= new DAGIterator(this); 
	while ( (nd=s.preorder())!=null ) {
		if ( nd.isSlot() ) {
			s.skip();
			nd.setSlotValue(null);
		}
	}
}

/**
 * Instantiates this DAG that is presumably a pattern. This is cloned and 
 * all slots in it are replaced by the DAGs that are the first children of the 
 * DAGs associated with slots from this DAG.
*/
public DAG instantiate()
{ 
// ---
	DAG r;
	DAG nd;
	DAGIterator s; 
// ---
	r=(DAG)clone();
	if ( r.isSlot() )
		return r.getSlotValue();
	s= new DAGIterator(r); 
	while ( (nd=s.preorder())!=null ) {
		if ( nd.isSlot() ) {
			if ( nd.getSlotValue()!=null ) {
				s.skip();
				nd.substitute(nd.getSlotValue());
			}
		}
	}
	return r;
}

}

