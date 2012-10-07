
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

/* Tree.java */

package graph;

import java.util.*;

/**
 * This class supports processing of recursive types 
 * that can be classified as trees.  
 * Recursive types are classes containing members of the same type.
 * Data of recursive types are usually viewed as directed graphs.
 * Recursive references represent directed edges.
 * Each object of a recursive class represents 
 * a node in a graph as well as 
 * a subgraph rooted at this node. 
 
 * Trees constitute a subset of graphs.
 * In a tree, a unique edge leads to each node.
 * Each object of class Tree also can be viewed as
 * a representation of the subtree whose top node is this object.
 * Every node holds references to children. This class provides access
 * to the parent of each node. Access to immediate children of 
 * each node is indexed. 

 * Tree is an abstract class. Methods 
 * nodeEqual and nodeCopy are abstract and should be defined 
 * in a derived class.
 * Class Tree contains methods that implement various tree modifications.
 * It also has methods implementing tree comparison and pattern matching.
 * Terminal nodes called slots are utilized as place holders matched
 * against trees.
 * This class is useful for processing symbolic expressions.
 *
 *
 * 
@version 1.1.1
@author Alexander Sakharov
@see graph.DAG
 */

public abstract class Tree extends Object implements Cloneable {

	Tree m_parent;				//  
	private Vector m_children = new Vector();	// 
	static private Object m_dummy = new Object();	// 

/**
 * Turns this, which is presumably a leaf node, into a slot and associates 
 * the specified Tree with the slot.
 * The associated Tree is a place holder for match method.
 * The tree associated with this should be root and leaf.
 * Its first child of the associated Tree will be used 
 * for storing trees by method match. 
 * This retains its leaf node status.
@param t associated tree
 *
*/
public synchronized void slot(Tree t) {
	if ( m_children.size()!=0 || t.m_children.size()!=0 
	|| t.m_parent!=null || t==null )
		throw new RuntimeException("Tree");
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
 * Yields the child with the specified index.
 * 
@param n child index
*/
public Tree getReference(int n) {
	if ( isSlot() )
		return null;
	if ( m_children.size()<n )
		return null;  
	return (Tree)(m_children.elementAt(n-1));
}

/**
 * Replaces the child with the specified index with the specified Tree.
 * @param n index of the child to replace
 * @param g tree that replaces a child
*/
protected synchronized void setReference(int n,Tree g) {
	if ( isSlot() )
		throw new RuntimeException("Tree");
	if ( m_children.size()<n )
		m_children.setSize(n);
	m_children.setElementAt(g,n-1);
}

/**
 * Yields the value stored as the first child of the tree associated with this slot.
 * 
*/
public Tree getSlotValue() {
	if ( !isSlot() )
		throw new RuntimeException("Tree");
	if ( ((Tree)m_children.elementAt(1)).m_children.size()==0 )
		return null;
	else
		return (Tree)(((Tree)m_children.elementAt(1)).m_children.elementAt(0));
}

/**
 * Stores the specified tree as the first child of the tree associated with this slot.
@param g tree that is assigned to this slot
*/
public synchronized void setSlotValue(Tree g) {
	if ( !isSlot() ) 
		throw new RuntimeException("Tree");
	if ( g!=null )
		if ( g.m_parent!=null )
			throw new RuntimeException("Tree");
	((Tree)m_children.elementAt(1)).m_children.setSize(1);
	((Tree)m_children.elementAt(1)).m_children.setElementAt(g,0);
}

/**
 * Yields the maximum possible child index.
*/
protected int maxIndex() {
	if ( isSlot() )
		return 0;
	return m_children.size();
}

/**
 * Returns a boolean value indicating if this node is equal to the specified node.
 * Children are not taken in consideration here. Even null vs non-null
 * values are not compared.
@param g node to compare with this
*/
protected abstract boolean nodeEqual(Tree g);

/**
 * Clones a node without children 
*/
protected abstract Tree nodeCopy();

/**
 * Returns the index of the next non-null child.
 * Null children are skipped.
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
 * Returns the index of the first non-null child or -1
 * if there are no children.
*/
public int firstIndex()
{ 
	return nextIndex(1);
}

/**
 * Returns the index of the last non-null child) or -1 
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
 * Returns the parent of this node.
*/
public Tree getParent()
{ 
	return m_parent;
}

/**
 * Returns the root.
*/
public Tree getRoot()
{ 
	Tree p;
// ---
	if ( m_parent==null )
		return null;
	else {
		p=this;
		while ( p.m_parent!=null )
			p=p.m_parent;
		return p.m_parent;
	}
}

/**
 * Returns the index of this as a child of its parent.
*/
public int parentIndex()
{ 
	int i;
// ---
	for (i=1;i<=m_parent.maxIndex();i++) { 
		if ( m_parent.getReference(i)==null )
			continue;
		if ( m_parent.getReference(i)==this ) {
			return i;
		}
	}
	return -1;
}

/**
 * Returns the number of edges between this node and the root.
*/
public int depth()
{ 
// ---
	Tree p;
	int i=0;
// ---
	p=this;
	while ( p.m_parent!=null ) {
		p=p.m_parent;
		i++;
	}
	return i;
}

/**
 * Creates an instance of TreeIterator for use in iteration over the nodes of 
 * the tree rooted at this.
*/
public TreeIterator iterator()
{ 
	TreeIterator s;
// ---
	s = new TreeIterator(this);
	return s;
}

/**
 * Checks if this node is a tree root.
*/
public boolean isRoot()
{ 
	return m_parent==null;
}

/**
 * Checks if this node has no children.
*/
public boolean isLeaf()
{
	int k;
// ---
	k=nextIndex(1); 
	return k<1;
}

/**
 * Checks if this and the the specified Tree have the same root.
@param d tree whose root is compared with the root of this
*/
public boolean relates(Tree d)
{ 
	return getRoot()==d.getRoot();
}

/**
 * Checks if the specified Tree is a subtree of this.
@param d tree to check for being a subtree of this
*/
public boolean contains(Tree d)
{ 
	Tree nd;
	TreeIterator s; 
// ---
	s = iterator(); 
	while ( (nd=s.preorder())!=null )
		if ( nd==d )
			return true;
	return false;
}

/**
 * Clones this tree.
 * It may create a stand-alone tree out of a subtree.
*/
public Object clone()
{ 
	Tree r;
	Tree c; 
	Tree d;
	int i;
// ---
	if ( this==null )
		return null;

	r= nodeCopy();
	if ( isSlot() ) {
		r.m_children.setSize(2);
		r.m_children.setElementAt(m_dummy,0);
		r.m_children.setElementAt(m_children.elementAt(1),1);
		return r;
	}
	for (i=1;i<=maxIndex();i++) {
		c=getReference(i); 
		if ( c==null )
			continue;
		else {
			d=(Tree)c.clone();
			r.setReference(i,d);
			d.m_parent=r;
		}
	}
	r.m_parent=null;
	return r;
}

/**
 * Embeds the specified Tree as a child of this tree. 
 * The specified Tree replaces the specified child of this.
 * The embedded tree should not be a subtree unless
 * it is a subtree of this. 
@param p tree to embed
@param n index of the child to be replaced 
@exception RuntimeException index is out of bounds
or an attempt is made to embed a subtree which is not a part of this tree
or this is a slot
*/
public synchronized void embed(Tree p, int n)
{
	int i;
// ---
	if ( n<0 )
		throw new RuntimeException("Tree");
	if ( isSlot() )
		throw new RuntimeException("Tree");
	// Only whole tree or a subtree of this can be moved
	if ( p!=null )
		if ( p.getRoot()!=null && !contains(p) )
			throw new RuntimeException("Tree");
// ---
	if ( this.getReference(n)!=null )
		throw new RuntimeException("Tree");
	this.setReference(n,p);
	p.m_parent=this;
}

/**
 * Clones the specified Tree and embeds it as a child of this tree.
 * The copy of the specified Tree replaces the specified child of this.
@param p tree to clone and embed
@param n index of the child to be replaced 
@exception RuntimeException index is out of bounds or this is a slot
*/
public synchronized void embedCopy(Tree p, int n)
{
	if ( n<0 )
		throw new RuntimeException("Tree");
	if ( isSlot() )
		throw new RuntimeException("Tree");
// ---	
	if ( this.getReference(n)!=null )
		throw new RuntimeException("Tree");
	Tree t = (Tree) p.clone();
	this.setReference(n,t);
	t.m_parent=this;
}

/**
 * Duplicates the tip node of the specified Tree and inserts it between this and its parent.
 * This becomes the n-th child of the duplicated node.
 * All other references of the duplicated node are set to null. 
 * This method can be modeled by other methods of class 
 * Tree but it is simpler and more efficient to use insertNodeCopy when it is 
 * necessary to insert new nodes into an existing tree.
@param d tree whose tip inserted on atop of this
@param n index of the reference to this
@exception RuntimeException null Tree parameter 
*/
public synchronized void insertNodeCopy(Tree d, int n)
{ 
	Tree r;
	Tree parent;
	int k;
	int i;
// --- 
	if ( d==null )
		throw new RuntimeException("Tree");
// ---
	r=d.nodeCopy();
	parent=m_parent;
	r.m_parent=parent;
	// set references in d's copy
	k=r.nextIndex(1);
	while ( k>=0 ) {
		if ( k==n )
			r.setReference(k,this);
		else
			r.setReference(k,null); 
		k=r.nextIndex(k);
	}
	m_parent=r;
	// update reference to this from its parent
	for (i=1;i<=parent.maxIndex();i++) { 
		if ( parent.getReference(i)==null )
			continue;
		if ( parent.getReference(i)==this ) {
			parent.setReference(i,r);
			break;
		}
	}
}

/**
 * Substitutes the specified Tree for this.
 * The specified Tree should not be a subtree unless
 * it is a subtree of this.
@param p tree to substitute 
@exception RuntimeException this is a root
or an attempt is made to substitute a subtree which is not a part of this tree
*/

public synchronized void substitute(Tree p)
{  
	int i;
// ---
	// cannot substitute for the root
	if ( m_parent==null )
		throw new RuntimeException("Tree");
	// Only whole tree or a subtree of this can be moved
	if ( p!=null )
		if ( p.m_parent!=null && !contains(p) )
			throw new RuntimeException("Tree");
// ---
	if ( p==null )
		if ( this==null )
			return;
	// update reference to this
	for (i=1;i<=m_parent.maxIndex();i++) { 
		if ( m_parent.getReference(i)==null )
			continue;
		if ( m_parent.getReference(i)==this ) {
			p.m_parent=m_parent;
			m_parent.setReference(i,p);
			break;
		}
	}
}

/**
 * Clones and substitutes the specified Tree for this.
@param p tree to clone and substitute
@exception RuntimeException this is a root
*/
public synchronized void substituteCopy(Tree p)
{ 
	Tree u; 
	int i;
// ---
	// Cannot substitute for the root
	if ( m_parent==null )
		throw new RuntimeException("Tree");
// ---
	if ( p==null ) {
		if ( this==null )
			return;
		u=null;
	} else {
		u=(Tree)p.clone(); 
	}
	// update reference to this
	for (i=1;i<=m_parent.maxIndex();i++) { 
		if ( m_parent.getReference(i)==null )
			continue;
		if ( m_parent.getReference(i)==this ) {
			p.m_parent=m_parent;
			m_parent.setReference(i,u);
			break;
		}
	}
}

/**
 * Compares the specified Tree with this.
 * Returns a boolean indicating whether the two trees are equal, i.e. 
 * there is such one-to-one mapping 
 * between both the nodes and edges of the two trees that 
 * the contensts of the respective nodes are equal, which is checked
 * by nodeEqual.
@return true if the trees are equal
@param r tree to compare with this
@exception RuntimeException this or the specified Tree is a slot
*/
public boolean congruent(Tree r)
{ 
	TreeIterator sp;
	TreeIterator sr; 
	Tree ndp;
	Tree ndr; 
	int i;
// ---
	if ( isSlot() )
		throw new RuntimeException("Tree");
	if ( r.isSlot() )
		throw new RuntimeException("Tree");
// ---
	if (this==r)
		return true;
	if (r==null)
		return false;

	sp = iterator();
	sr = r.iterator();

	ndp=sp.preorder();
	ndr=sr.preorder();
	// simultaneous traversal of both Trees
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
 * Checks if this and the specified Tree match. 
 * Returns a boolean indicating whether they match. This may contain slots
 * which play the role of variables matching any tree. Thus, this tree serves as a pattern.
 * If the two trees match, then the counterparts of all slots become the first children
 * of the trees associated with slots. Even if match yields false, still trees 
 * could be assigned to
 * some trees associated with slots. The user can apply the method clean to cut the
 * first children of the associated trees off.
 * All slots rassociated with the same Tree should match congruent trees 
 * in order for match to succeed.
 * If this method yields true, it means that there is such one-to-one mapping 
 * between the non-slot nodes of the two trees that all edges are also mapped one-to-one
 * and the contensts of the respective nodes are equal, which is checked
 * by nodeEqual.
@return true if this and the specified Tree match
@param r tree to match against this
@see congruent
@see clean
*/
public synchronized boolean match(Tree r)
{ 
	TreeIterator sp;
	TreeIterator sr; 
	Tree ndp;
	Tree ndr;
	Tree t;
	int i;
// ---
	if (this==r)
		return true;
	if (this==null || r==null)
		return false;

	sp = iterator();
	sr = r.iterator(); 

	ndp=sp.preorder();
	ndr=sr.preorder();
	// simultaneous traversal of both Trees
	for(;ndp!=null && ndr!=null;) {
		if ( ndp.isSlot() ) {
			if ( ndp.getSlotValue()==null ) {
				ndp.setSlotValue((Tree)ndr.clone()); 
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
	return ndp==ndr;
}

/**
 * Cuts off the first children of all trees associated with slots in this Tree. 
 * This method cleans a pattern tree 
 * to be matched against another one again.
*/
public synchronized void clean()
{ 
	Tree nd;
	TreeIterator s; 
// ---
	s = iterator(); 
	while ( (nd=s.preorder())!=null ) {
		if ( nd.isSlot() ) {
			s.skip(); 
			nd.setSlotValue(null);
		}
	}
}

/**
 * Instantiates this Tree that is presumably a pattern. This is cloned and 
 * all slots in it are replaced by the trees are the first children of the 
 * trees associated with slots from this Tree.
*/
public synchronized Tree instantiate()
{ 
	Tree r;
	Tree nd;
	TreeIterator s; 
// ---
	r=(Tree)clone();
	if ( r.isSlot() ) {
		return r.getSlotValue();
		}
	s=r.iterator(); 
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

