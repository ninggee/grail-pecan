
/* ExampleDAG.java */

import graph.*;

import java.io.*;
import java.util.*;

/**
 * This class has only one method main.
 * It is an application that transform well-formed
 * algebraic expressions by applying rewrite rules.  
 * Method main has two parameters: input and output file names.
 * The input file is an ASCII file comprising a well-formed
 * algebraic expression that may contain white spaces.
 * The ouptut is the input expession followed by 
 * transformed expression obtained by iterative 
 * application of rewrite rules.
 * 
@author Alexander Sakharov
 */

public class ExampleDAG {

public static void print(String s, DAG e, PrintWriter out) {
	out.print(s + "   ");
	// Print transformed ExpressionDAG
	DAGIterator t;
	ExpressionDAG c;
	t = e.iterator();
	while ( (c=(ExpressionDAG)(t.allorder()))!=null ) {
		if ( c.isLeaf() ) 
			out.print( c.tag + " ");
		if ( !c.isLeaf() && t.visit()==1 )
			out.print("( ");
		if ( !c.isLeaf() && t.visit()>1 && !t.isPost() )
			out.print(c.tag + " ");
		if ( !c.isLeaf() && t.isPost() )
			out.print(") ");
	} 
	out.println(""); 
}

/**
 * Reads an expression, applies rewrite rules and prints a result.
*/
public static void main( String args[] ) 
{ 
	ExpressionDAG expr;
	int index;
	ExpressionDAG temp;
	boolean flag;
	int i;
	int m; 
	double number;
	String identifier;
	DAGIterator trace;
	ExpressionDAG current;

// Rewriting rules
	ExpressionDAG x = new ExpressionDAG();
	ExpressionDAG lhs[] = new ExpressionDAG[4];
	ExpressionDAG rhs[] = new ExpressionDAG[4];

	lhs[0] = new ExpressionDAG(new ExpressionDAG(x),"+",new ExpressionDAG("0.0"));
	rhs[0] = new ExpressionDAG(x);

	lhs[1] = new ExpressionDAG(new ExpressionDAG(x),"-",new ExpressionDAG("0.0"));
	rhs[1] = new ExpressionDAG(x);

	lhs[2] = new ExpressionDAG(new ExpressionDAG(x),"-",new ExpressionDAG(x));
	rhs[2] = new ExpressionDAG("0.0");

	lhs[3] = new ExpressionDAG(new ExpressionDAG("0.0"),"+",new ExpressionDAG(x));
	rhs[3] = new ExpressionDAG(x);

if (args.length!=2){
  	System.out.println("Should be two files as parameters");
  	return; 
}

try {

	FileInputStream inStream = new FileInputStream(args[0]);
	Reader reader = new BufferedReader(new InputStreamReader(inStream));
	StreamTokenizer input = new StreamTokenizer(reader);

	FileOutputStream outStream = new FileOutputStream(args[1]);
	PrintWriter outFile = new PrintWriter(outStream,true);

	expr = new ExpressionDAG();
	trace = expr.iterator();

	m=input.nextToken();
	current=(ExpressionDAG)(trace.allorder());
	current.tag="";

	for ( ; m!=StreamTokenizer.TT_EOF && m!='/'; m=input.nextToken() ) {
		if ( m==StreamTokenizer.TT_EOL ) 
			continue;
		if ( current.isLeaf() )
			index=1;
		else
			index=current.lastIndex()+1;
		if ( m==StreamTokenizer.TT_NUMBER ) {
			number=(int)input.nval;
			temp=new ExpressionDAG();
			temp.tag=String.valueOf(number);
			current.embed(temp,index);
			current=(ExpressionDAG)(trace.allorder());
			current=(ExpressionDAG)(trace.allorder());
		} else if ( m==StreamTokenizer.TT_WORD ) {
			identifier=input.sval;
			temp=new ExpressionDAG();
			temp.tag=identifier;
			current.embed(temp,index);
			current=(ExpressionDAG)(trace.allorder());
			current=(ExpressionDAG)(trace.allorder());
		} else {
			if ( m=='(' ) {
				temp=new ExpressionDAG();
				temp.tag="";
				current.embed(temp,index);
				current=(ExpressionDAG)(trace.allorder());
			} else if ( m==')' ) {
				current=(ExpressionDAG)trace.allorder();
			} else {
				current.tag=String.valueOf((char)m);
			}
		}
	}

	print("original",expr,outFile);

	// Merge similar expressions
	Vector nodes = new Vector();
	trace = expr.iterator();
	while ( (current=(ExpressionDAG)(trace.postorder()))!=null ) {
		nodes.addElement(current);
		for(i = 0;i < nodes.size()-1; i++) {
			if ( current.congruent((DAG)(nodes.elementAt(i))) ) {
				trace.skip();
				current.substitute((DAG)(nodes.elementAt(i)));
				nodes.removeElementAt(nodes.size()-1);
				break;
			}		
		}
	}

	// Check merges
	nodes = new Vector();
	trace = expr.iterator();
	while ( (current=(ExpressionDAG)(trace.postorder()))!=null ) {
		if ( nodes.contains(current) ) {
			print("merged item",current,outFile);
		} else {
			nodes.addElement(current);		
		}
	}

	// Rewriting loop
	do {
		trace=expr.iterator();
		flag=false;
		while ( (current=(ExpressionDAG)(trace.singlePostorder()))!=null ) {
			for(i=0;i<lhs.length;i++) { 
				if ( lhs[i].weakMatch(current) ) {
					if ( current==expr ) {
						expr=(ExpressionDAG)rhs[i].instantiate();											
					} else { //trace.skip();
						current.substituteCopy(rhs[i].instantiate()); 
						}
					flag=true;
					break;
				}
				lhs[i].clean();
				rhs[i].clean();
			}
		}
	} while ( flag );

	print("final",expr,outFile);

	inStream.close();
	outStream.close();

} catch(IOException iofailure) {
	System.out.println("I/O Error"); 
}

}

}