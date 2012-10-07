
/* ExampleTree.java */

import graph.*;

import java.io.*;

public class ExampleTree {

public static void main( String args[] ) 
{ 
	ExpressionTree expr;
	int index;
	ExpressionTree temp;
	boolean flag;
	int i;
	int m; 
	double number;
	String identifier;
	TreeIterator trace;
	ExpressionTree current;

// Rewriting rules
	ExpressionTree x = new ExpressionTree();
	ExpressionTree lhs[] = new ExpressionTree[4];
	ExpressionTree rhs[] = new ExpressionTree[4];

	lhs[0] = new ExpressionTree(new ExpressionTree(x),"+",new ExpressionTree("0.0"));
	rhs[0] = new ExpressionTree(x);

	lhs[1] = new ExpressionTree(new ExpressionTree(x),"-",new ExpressionTree("0.0"));
	rhs[1] = new ExpressionTree(x);

	lhs[2] = new ExpressionTree(new ExpressionTree(x),"-",new ExpressionTree(x));
	rhs[2] = new ExpressionTree("0.0");

	lhs[3] = new ExpressionTree(new ExpressionTree("0.0"),"+",new ExpressionTree(x));
	rhs[3] = new ExpressionTree(x);

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

	expr = new ExpressionTree();
	trace = expr.iterator();

	m=input.nextToken();
	current=(ExpressionTree)(trace.allorder());
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
			temp=new ExpressionTree();
			temp.tag=String.valueOf(number);
			current.embed(temp,index);
			current=(ExpressionTree)(trace.allorder());
			current=(ExpressionTree)(trace.allorder());
		} else if ( m==StreamTokenizer.TT_WORD ) {
			identifier=input.sval;
			temp=new ExpressionTree();
			temp.tag=identifier;
			current.embed(temp,index);
			current=(ExpressionTree)(trace.allorder());
			current=(ExpressionTree)(trace.allorder());
		} else {
			if ( m=='(' ) {
				temp=new ExpressionTree();
				temp.tag="";
				current.embed(temp,index);
				current=(ExpressionTree)(trace.allorder());
			} else if ( m==')' ) {
				current=(ExpressionTree)trace.allorder();
			} else {
				current.tag=String.valueOf((char)m);
			}
		}
	}

	// Print original expression
	trace = expr.iterator();
	while ( (current=(ExpressionTree)(trace.allorder()))!=null ) {
		if ( current.isLeaf() ) 
			outFile.print( current.tag + " ");
		if ( !current.isLeaf() && trace.visit()==1 )
			outFile.print("( ");
		if ( !current.isLeaf() && trace.visit()>1 && !trace.isPost() )
			outFile.print(current.tag + " ");
		if ( !current.isLeaf() && trace.isPost() )
			outFile.print(") ");
	} 
	outFile.println("");
/*
	TreeIterator t;
	ExpressionTree c;
	t = ((Tree)expr.clone()).iterator();
	while ( (c=(ExpressionTree)(t.allorder()))!=null ) {
		if ( c.isLeaf() ) 
			outFile.print( c.tag + " ");
		if ( !c.isLeaf() && t.visit()==1 )
			outFile.print("( ");
		if ( !c.isLeaf() && t.visit()>1 && !t.isPost() )
			outFile.print(c.tag + " ");
		if ( !c.isLeaf() && t.isPost() )
			outFile.print(") ");
	} 
	outFile.println("");
*/
	// Rewriting loop
	do {
		trace=expr.iterator();
		flag=false;
		while ( (current=(ExpressionTree)(trace.postorder()))!=null ) {
			for(i=0;i<lhs.length;i++) { 
				if ( lhs[i].match(current) ){
					if ( current==expr ) 
						expr=(ExpressionTree)rhs[i].instantiate();					
					else
						current.substituteCopy(rhs[i].instantiate());
					flag=true;
					break;
				}
				lhs[i].clean();
				rhs[i].clean();
			}
		}
	} while ( flag );

	// Print transformed expression
	trace = expr.iterator();
	while ( (current=(ExpressionTree)(trace.allorder()))!=null ) {
		if ( current.isLeaf() ) 
			outFile.print( current.tag + " ");
		if ( !current.isLeaf() && trace.visit()==1 )
			outFile.print("( ");
		if ( !current.isLeaf() && trace.visit()>1 && !trace.isPost() )
			outFile.print(current.tag + " ");
		if ( !current.isLeaf() && trace.isPost() )
			outFile.print(") ");
	} 
	outFile.println(""); 

	inStream.close();
	outStream.close();

} catch(IOException iofailure) {
	System.out.println("I/O Error"); 
}

}

}