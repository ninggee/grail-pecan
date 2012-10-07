
/* ExpressionTree.java */

import graph.*;
import java.lang.String;

/**
 * This class defines well-formed binary algebraic expressions as
 * objects of a sub-class of Tree. The only data member of this 
 * class is a string representing either an algebraic operation or
 * a number. Variables are are represented as slots with empty
 * strings as tags.
 * 
@author Alexander Sakharov
 */

public class ExpressionTree extends Tree implements Cloneable {
	
	public String tag;

/**
 * Constructs a default terminal node.
*/
public ExpressionTree() 
{
	tag="";
}

/**
 * Constructs a terminal node with the given tag.
*/
public ExpressionTree(String s) 
{
	tag=s;
}

/**
 * Constructs a node representing an ExpressionTree with 
 * the two given sub-ExpressionTrees and value.
*/
public ExpressionTree(ExpressionTree left, String s, ExpressionTree right) 
{
	tag=s;
	embed(left,1);
	embed(right,2);
}

/**
 * Constructs a slot.
*/
public ExpressionTree(ExpressionTree e) 
{
	tag="";
	slot(e);
}

/**
 * Compares two nodes.
*/
public boolean nodeEqual(Tree q)
{ 
	if ( !tag.equals(((ExpressionTree)q).tag) )
		return false;
	return true;
}

/**
 * Copies this node.
*/
public Tree nodeCopy() 
{ 
	ExpressionTree e;
	e = new ExpressionTree();
	e.tag=tag;
	return e;
}

}
