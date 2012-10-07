/*
*************************************************************************

 Copyright (c) 2000 Alexander Sakharov   All rights reserved.

The files from this package are not public-domain software nor shareware.
One may not redistribute files from this package without written
permission from Alexander Sakharov. One may not sell amy product derived
from sources of the package without a written permission from Alexander
Sakharov. If code from this package is used elsewhere, this copyright
notice shall accompany every code fragment from the package, and the
following web page reference shall be included:
http://members.aol.com/asakharov/fsm.html

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

			fsm.zip

This package conatins code that supports specification and generation of
finite state machines (FSM) and their controllers in Java. 

Any FSM component is specified as a class derived from an abstract class called
FSM. Class FSM declares static variables for storing a start state name, an
optional stop state name, and transitions as strings. FSM specification classes
define values of the aforementioned variables. Transition actions are given by
the names of methods implementing them in the same FSM specification classes.
Argument lists optionally follow the method names. Guards are given by valid
Java expressions substantiated in the same FSM specification classes.

Assembly of components is specified as a class derived from an abstract class
called FSMController. Class FSMController declares static variables for storing
specifications of the following: containment; data exchange between containers
and sub-components; event sources; an event-processing mode. Values of these
variables are defined in derived classes. Class FSMController also dispatches
invocation of data exchange methods. Events may be delivered synchronously or
asynchronously. In the latter case, they may be queued or not. If event queuing
is allowed, then the default order of processing events in the queue is FIFO,
which can be overridden. In case of asynchronous event delivery, the controller
is Runnable.

Also, both class FSM and class FSMController declare static variables for storing
the name of the specification classes and the name of generated FSM component
and controller classes. The values of these variables are given in the
respective specification classes.

Containment is specified by an array mapping container FSM states to
sub-components. Each element of this array consists of a container identifier, 
a container state, and a sub-component identifier. The identifiers are class 
names optionally followed by : and a sequence of letters or digits. If more than 
one sub-component is given for one container state, then these sub-components are 
run concurrently.

Each element of an array specifying data exchange between containers and
sub-components consists of two component identifiers and two method names. 
The first method passes data from the container to the sub-component,
whereas the other passes data back. All data exchange methods should be
implemented in the same assembly specification classes. Note that components
connected through containment may share event sources. 

Event sources are specified by an array whose elements comprise component and 
event source classes along with mappings of transition events to listener 
interface methods of source listeners. It is assumed that the event sources 
implement methods that add and remove event listeners.

Classes FSM and FSMController incorporate code generation facilities. Method main 
of class FSM builds components from their specifications whereas method main of
class FSMController is responsible for building FSM controllers from assembly
specifications. The classes generated from FSM specifications contain
implementations of methods named as events in the transitions specified in their
super classes. Additionally, a couple of methods are generated for executing 
eventless transitions (for asynchronous event delivery only). Each generated FSM 
component class has an array with states. For the sake of efficiency, 
transition events and states are represented by integers in generated code.

The controllers generated from assembly specifications implement control flow
between containers and their sub-components. They also play the role of event 
adapters for all relevant FSM components in case of asynchronous event delivery. 
The generated controllers implement all listener interface methods for specified 
event sources. Each generated controller has a static Boolean array employed for 
fast determination of containment and another static array with references to data 
exchange methods. The controllers queue events when so specified. The
controllers call counterparts of listener interface methods and 'eventless'
methods of the classes generated from FSM specifications. In implementation of
control flow among components, we maintain active state configurations and
follow the UML semantics of composite states including concurrent ones.

For more information on this subject, see my papers 'A Hybrid State Machine
Notation for Component Specification' in the April 2000 issue of SIGPLAN Notices
and 'State Machine Specification Directly in Java and C++' in OOPSLA 2000
Companion. Note that this variant of the FSM package does not have support for
synchronization points and hybrid transitions with regular expressions of 
events.

Contents:
	fsm/FSM.java			- class FSM
	fsm/FSMController.java		- class FSMController
	fsm/StateConfiguration.java	- class representing active state configurations
	fsm/EventSource.java		- interface for event sources to implement
	Main.java			- sample application
	Maint.java			- sample application
	TVRemote.java			- sample event source
	TVCommunication.java		- sample event listener interface
	TVApp.java			- sample assembly specification class
	TVHandler.txt			- sample FSM component (container)
	TVSet.txt			- sample FSM component (sub-component)
	TVDevice.txt			- sample FSM component (sub-component)
	Readme.txt			- this file


To use these code, JDK 1.2 and my package 'graph' should be installed. It is
necessary to create package named fsm and place FSM.java, FSMController.java,
StateConfiguration.java, and EventSource.java files in the package, i.e. in a
directory named fsm. Compile these Java files with javac.

To run examples:
	javac TV*.java
	java TVHandler
	java TVSet
	java TVDevice
	javac TVHandlerSub.java TVSetSub.java TVDeviceSub.java
	java TVApp
	javac TVAppAdapter.java 
	javac Main.java (or Maint.java)
	java Main (or Maint)
