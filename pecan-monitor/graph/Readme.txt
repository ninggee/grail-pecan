/*
*************************************************************************                           

 Copyright (c) 1998 Alexander Sakharov   All rights reserved.                

The files from this package are not public-domain software nor shareware. 
One may not redistribute files from this package without written 
permission from Alexander Sakharov. One may not sell amy product derived 
from sources of the package without a written permission from Alexander 
Sakharov.
 
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

			graph.zip

This package conatins code that supports handling recursive types in Java. 
Recursive types are classes containing members of the same type. Data of 
recursive types are usually viewed as directed graphs. Each class object 
represents a node in a graph as well as a subgraph rooted at this node. 
Most of cyclic graphs representing recursive data types contain cycles 
because of backward references. It means that most of cyclic graphs are
essentially acyclic graphs or even trees and are treated as such in programs. 

There is an abstract class for representing trees (Tree) and an abstract class 
for representing rooted acyclic graphs (DAG). (Note that class DAG was named 
Graph in the SIGPLAN Notices letter.) Classes Tree and DAG provide methods to 
store and update references to children. These classes provide random access 
to immediate children of each node. Every object of class Tree holds 
the parent of the node. Objects of class DAG have a reference to the root 
instead of the parent. Two methods in each class are abstract and should be 
defined in a derived class. 

Unlike collection implementation, iterators for trees and rooted acyclic graphs 
are classes, not interfaces. All iterator functionality is pre-implemented. 
Users do not have to write their own code supporting graph traversals. Several 
traversal orders are implemented for iteration over graph nodes. 
Fail-fast iteration is not currently supported, but can be easily added. 
See my letter in the December 1998 issue of SIGPLAN Notices for a more 
detailed description of this package.

Contents:
	graph/DAG.java			- class DAG
	graph/DAGIterator.java		- DAG iterator class
	graph/Tree.java			- class Tree
	graph/TreeIterator.java		- Tree iterator class
	ExpressionDAG.java		- sample class derived from DAG
	ExampleDAG.java			- sample application working with DAGs
	ExpressionTree.java		- sample class derived from Tree
	ExampleTree.java		- sample application working with Trees
	example.txt			- sample source data
	readme.txt			- this file


To use these code, JDK should be installed. One has to create package 
named grph and place DAG, Tree and their iterator files in the package, 
i.e. in a directory named graph. Compile these Java files with javac.

To run examples:
	javac ExampleDAG.java
	java ExampleDAG example.txt result

	javac ExampleTree.java
	java ExampleTree example.txt result
