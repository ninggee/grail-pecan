
/* ExpressionDAG.java */

import graph.*;
import java.lang.String;

/**
 * This class defines well-formed binary algebraic expressions as
 * objects of a sub-class of DAG. The only data member of this 
 * class is a string representing either an algebraic operation or
 * a number. Variables are are represented as slots with empty
 * strings as tags.
 * 
@author Alexander Sakharov
 */

public class ExpressionDAG extends DAG implements Cloneable {
	
	public String tag;

/**
 * Constructs a default terminal node.
*/
public ExpressionDAG() 
{
	tag="";
}

/**
 * Constructs a terminal node with the given tag.
*/
public ExpressionDAG(String s) 
{
	tag=s;
}

/**
 * Constructs a node representing an ExpressionDAG with 
 * the two given sub-ExpressionDAGs and value.
*/
public ExpressionDAG(ExpressionDAG left, String s, ExpressionDAG right) 
{
	tag=s;
	embed(left,1);
	embed(right,2);
}

/**
 * Constructs a slot.
*/
public ExpressionDAG(ExpressionDAG e) 
{
	tag="";
	slot(e);
}

/**
 * Compares two nodes.
*/
public boolean nodeEqual(DAG q)
{ 
	if ( !tag.equals(((ExpressionDAG)q).tag) )
		return false;
	return true;
}

/**
 * Copies this node.
*/
public DAG nodeCopy() 
{ 
	ExpressionDAG e;
	e = new ExpressionDAG();
	e.tag=tag;
	return e;
}

}
