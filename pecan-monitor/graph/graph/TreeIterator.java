
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

/* TreeIterator.java */

package graph;

import java.util.*;

/**
 * This class represents iterators for class Tree.
 * Iteration over objects of class Tree is essentially tree traversal.
 * Traversal of trees is implemented with using a stack of nodes.
 * Traversals in pre-order, post-order, and traversals with
 * multiple visits of non-terminal nodes are supported.
 * Note that changes in trees in the process of traversal are permissible
 * if they do not affect nodes on the traversal stack.
 * If the currently visited node is updated, adjustement to the iterator
 * is required when traversing in preorder or allorder.
 * Changes to other nodes on the stack break the iterator.
 *
@version 1.1.1
@author Alexander Sakharov
@see graph.Tree
*/

public class TreeIterator extends Object {

	private Tree m_top;
	private int m_visit;
	Stack nodes;
	Stack counts;

/**
 * Constructs a TreeIterator to walk over the specified Tree.
@param p tree to traverse
*/
public TreeIterator(Tree p)
{
	nodes = new Stack();
	counts = new Stack();
	m_top=p;
}

/**
 * Returns a number specifying the current visit.
 * It returns the ordinal number of the current visit for all-order traversals.
 * If this is the last visit, lastIndex()+1 is returned for non-terminal nodes.
 * This method always returns 1 for pre-order.
 * It returns lastIndex()+1 when walking non-terminal nodes in post-order.
 * This method is useful when doing all-order traversals visit
 * non-terminal tree nodes several times.
*/
public int visit()
{
	return m_visit;
}

/**
 * Returns true if the currently visited node is not a leaf node and it is
 * the first visit of this node.
 * Returns false otherwise.
*/
public boolean isPre()
{
	if ( !((Tree)nodes.peek()).isLeaf() && m_visit==1 )
		return true;
	else
		return false;
}

/**
 * Returns true if the currently visited node is not a leaf node and it is
 * the last visit of this node.
 * Returns false otherwise.
*/
public boolean isPost()
{
	if ( !((Tree)nodes.peek()).isLeaf() && m_visit>=((Tree)nodes.peek()).lastIndex()+1 )
		return true;
	else
		return false;
}

/**
 * Forces to skip visiting of yet unvisited children of the currently visited node.
 * This method sets the ordinal number of the current visit to Integer.MAX_VALUE.
 * Due to that, the currently visited node can be modified before
 * the following iterator invocation.
*/
public void skip()
{
	m_visit=Integer.MAX_VALUE;
}

/**
 * Adjusts the iterator after changes to the currently visited node.
 * It is applied when traversing in preorder or allorder.
 * The currently visited node should not be null'ed.
@exception RuntimeException the currently visited node is null'ed
*/
public void adjust()
{       int n;
	Tree d;
	if ( nodes.size()>1 ) {
		nodes.pop();
		d=((Tree)nodes.peek()).getReference(((Integer)counts.peek()).intValue());
	        if ( d==null )
		  throw new RuntimeException("TreeIterator");
  		m_visit = 1;
                nodes.push(d);
        }
}

/**
 * Iterates to the next tree node in pre-order.
*/
public synchronized Tree preorder()
{
	int n;
	Tree d;
// ---
	if ( nodes.size()==0 ) {
		// traversal start
		if ( m_top==null )
			return null;
		else
			nodes.push(m_top);
	} else {
		// traversal continuation
		n=((Tree)nodes.peek()).nextIndex(1);
		if ( n>=0 && m_visit==1 ) {
			// go to the first child
			counts.push(new Integer(n));
			nodes.push(((Tree)nodes.peek()).getReference(n));
		} else {
			// no children
			while ( nodes.size()>1 ) {
				// return to the nearest predecessor that has an unvisited child
				d=(Tree)nodes.pop();
				n=((Tree)nodes.peek()).nextIndex(((Integer)counts.peek()).intValue()+1);
				counts.pop(); counts.push(new Integer(n));
				if ( n>=0 ) {
					break;
				}
				counts.pop();
			 }
			if ( counts.size()==0 ) {
				// traversal is complete
				nodes.removeAllElements();
				counts.removeAllElements();
				m_top=null;
				return (null);
			} else
				// push the unvisited child onto the stack
				nodes.push(((Tree)nodes.peek()).getReference(((Integer)counts.peek()).intValue()));
		}
	}
	m_visit=1;
	return (Tree)nodes.peek();
}

/**
 * Iterates to the next tree node in post-order.
*/
public synchronized Tree postorder()
{
	int n;
	Tree d;
// ---
	if ( nodes.size()==0 ) {
		// traversal start
		if ( m_top==null )
			return null;
		else {
			nodes.push(m_top);
			while ( (n=((Tree)nodes.peek()).nextIndex(1))>=0 ) {
				// down untill a terminal node is reached
				counts.push(new Integer(n));
				nodes.push(((Tree)nodes.peek()).getReference(n));
			}
		}
	// traversal continuation
	} else if ( nodes.size()==1 ) {
		// all nodes have been visited
		nodes.removeAllElements();
		counts.removeAllElements();
		m_top=null;
		return (null);
	} else if ( nodes.size()>1 ) {
		// some are left - go to the next sibling
		nodes.pop();
		n=((Tree)nodes.peek()).nextIndex(((Integer)counts.peek()).intValue()+1);
		counts.pop();
		counts.push(new Integer(n));
		if ( n<0 ) {
			// one level up if there are no more siblings
			counts.pop();
		} else {
			nodes.push(((Tree)nodes.peek()).getReference(((Integer)counts.peek()).intValue()));
			while ( (n=((Tree)nodes.peek()).nextIndex(1))>=0 ) {
				// down until a terminal node is reached
				counts.push(new Integer(n));
				nodes.push(((Tree)nodes.peek()).getReference(n));
			}
		}
	}
	if ( ((Tree)nodes.peek()).isLeaf() )
		m_visit=1;
	else
		m_visit=((Tree)nodes.peek()).lastIndex()+1;
	return (Tree)nodes.peek();
}

/**
 * Iterates to the next tree node by going through all edges first forward and then backward.
 * Therefore, each node is visited before visiting the first child
 * and then after visiting each child. Terminal nodes are visited once.
*/
public synchronized Tree allorder()
{
	int n;
	Tree d;
// ---
	if ( nodes.size()==0 ) {
		// traversal start
		if ( m_top==null ) {
			return null;
		} else {
			m_visit=1;
			nodes.push(m_top);
		}
	} else {
		// traversal continuation
		if ( ((Tree)nodes.peek()).isLeaf() || isPost() ) {
			// no children: go one level up
			if ( nodes.size()>1 ) {
				// there is a predecessor
				d=(Tree)nodes.pop();
				m_visit=((Tree)nodes.peek()).nextIndex(((Integer)counts.peek()).intValue()+1);
				if ( m_visit==-1 )
					m_visit=((Tree)nodes.peek()).lastIndex()+1;
				counts.pop();
			} else {
				// no predecessors: traversal is complete
				nodes.removeAllElements();
				counts.removeAllElements();
				m_top=null;
				return (null);
			}
		} else {
			// m_visit>=1
			// go to the next child
			n=((Tree)nodes.peek()).nextIndex(m_visit);
			counts.push(new Integer(n));
			nodes.push(((Tree)nodes.peek()).getReference(n));
			m_visit=1;
		}
	}
	return (Tree)nodes.peek();
}

/**
 * Returns the currently visited node of the traversed Tree.
*/
public Tree current()
{
	return (Tree)nodes.peek();
}

/**
 * Forces to walk to the parent of the currently visited node.
*/
public synchronized void pop()
{
	nodes.pop();
	counts.pop();
}

/**
 * Forces to walk to the child with the specified index from the currently visited node.
@param n index of the child to push onto the traversal stack
*/
public synchronized void push(int n)
{
	nodes.push(current().getReference(n));
	counts.push(new Integer(n));
}

}