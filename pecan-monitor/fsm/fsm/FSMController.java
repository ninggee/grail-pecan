
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

/* FSMController.java */

package fsm;

import graph.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;

/**
 * This class serves as a base for FSM controllers.
 * Any FSM controller is specified as a class derived from this class.
 * This declares static variables for storing specifications of the following:
 * containment; data exchange between containers and sub-components;
 * event sources; an event-processing mode.
 * Values of these variables are defined in derived specification classes.
 * Class FSMController also dispatches invocation of data exchange methods.
 * Events may be delivered synchronously or asynchronously.
 * In the latter case, they are queued.
 * The default order of processing events in the queue is FIFO,
 * which can be overridden.
 *
 * Method main of this class is a code generation facility.
 * When a specification class is executed, i. e. its main method is run,
 * a FSM controller is generated as a class derived from the specification class.
 *
@author Alexander Sakharov
@see fsm.FSM
 */
public class FSMController extends Object {

        // protected int stateIndex = -1;

        // Internal data
        private static Method componentInMethod[][];
        private static Method componentOutMethod[][];

        private StateConfiguration active;
        private Vector recepient; // of type StateConfiguration

        // These values are specified in a subClass

	// specification class name
        protected static String specification;
	// generated controller class name
        protected static String controller;

	protected static String[][] containment;
        // protected static String[][] concurrency;
        protected static String[][] exchange;
        protected static String[][] source;
        protected static char delivery;    // 's' (synchronous), 'q' (asynchronous with queuing)

        // Values of these members are generated
        protected static FSM component[];
        protected static boolean subComponent[][][]; // <--

/**
 * ctor
*/
public FSMController() {
	activate();
}

/**
 * Creates two arrays of data exchange methods from two arrays with their names.
 * The first array is for methods passing data from containers to sub-components.
 * The second one is for passing data back.
 *
@param componentInName array of the names of methods passing data from containers to sub-components
@param componentOutName array of the names of methods passing data back from sub-components to containers
*/
public void setComponentDataExchange(String[][] componentInName, String[][] componentOutName) {
    int k, m, n;
    Class cl;
    Method[] func;

    componentInMethod = new Method[componentInName.length][];
    cl = this.getClass();
    func = cl.getMethods();
    componentInMethod = new Method[componentInName.length][];
    for(n=0;n<componentInName.length;n++) {
      componentInMethod[n] = new Method[componentInName.length];
      for(m=0;m<componentInName[n].length;m++) {
        if ( componentInName[n][m]!=null && componentInName[n][m].length()>0 ) {
          for(k=0;k<func.length;k++) {
            if ( func[k].getName().equals(componentInName[n][m]) ) {
	      componentInMethod[n][m] = func[k];
            }
          }
        }
      }
    }

    componentOutMethod = new Method[componentOutName.length][];
    for(n=0;n<componentOutName.length;n++) {
      componentOutMethod[n] = new Method[componentOutName.length];
      for(m=0;m<componentOutName[n].length;m++) {
        if ( componentOutName[n][m]!=null && componentOutName[n][m].length()>0 ) {
          for(k=0;k<func.length;k++) {
            if ( func[k].getName().equals(componentOutName[n][m]) ) {
	      componentOutMethod[n][m] = func[k];
            }
          }
        }
      }
    }
}

      // Property access methods

/**
 * Yields the name of specification class
*/
public String getSpecification() {
	return specification;
}

/**
 * Yields the name of generated specification class
*/
public String getController() {
	return controller;
}

/**
 * Yields the value of member 'containment'
*/
public String[][] getContainment() {
	return containment;
}

/**
 * Yields the value of member 'concurrency'
*/
/*
public String[][] getConcurrency() {
	return concurrency;
}
*/

/**
 * Yields the value of member 'exchange'
*/
public String[][] getExchange() {
	return exchange;
}

/**
 * Yields the value of member 'source'
*/
public String[][] getSource() {
	return source;
}

/**
 * Yields the value of member 'delivery'
*/
public char getDelivery() {
	return delivery;
}

/**
 * Yields array of all controller's components
*/
public FSM[] getComponent() {
	return component;
}

/**
 * Yields booleans determining containment relationship
*/
public boolean[][][] isSubComponent() {
	return subComponent;
}

/**
 * Activates this FSMController.
*/
public void activate() {
	active = new StateConfiguration();
	active.setComponent(0);
	active.setState(0);
        recepient = null;
	component[0].activate();
}

/**
 * Deactivates this FSMController.
*/
public void deactivate() {
	active = null;
}

/**
 * Checks if this FSMController is active.
*/
public boolean isActive() {
	return (active!=null);
}

/**
 * Yields the index of event to take from the event queue.
 * This default implementation always returns zero, that is, the first element.
 * Hence, it implements the FIFO dequeuing.
*/
public int dequeue() {
	return 0;
}

/**
 * Adjusts state configuration before transition.
 *
@param sc state configuration sub-tree whose tip node is transition source
*/
public void preTransition(StateConfiguration sc) {
      int i, j;
      int comp;
      int st;
      TreeIterator iter;
      StateConfiguration nd;

      // pre
      iter = sc.iterator();
      while ( (nd=(StateConfiguration)iter.postorder())!=sc ) {
        // TBD: propagate data for those requiring to do so even if it is not in stop state
        comp = nd.getComponent();
        component[comp].transit = false;
        st = nd.getState();
        // Pass data out for 'e' (all exits)
        StateConfiguration parent = (StateConfiguration)nd.getParent();
        int p = parent.getComponent();
        if ( componentOutMethod[p][comp] != null && st > 0 ) {
          //System.out.println("preTransition, comp=" + comp);
          Object[] params = new Object[2];
          params[0] = component[p];
          params[1] = component[comp];
          try {
            componentOutMethod[p][comp].invoke(this, params);
          } catch (Exception e) {
            System.out.println("FSMController Error"); e.printStackTrace();
            return;
          }
        }
      }
}

/**
 * Adjusts state configuration after transition.
 *
@param sc state configuration sub-tree whose tip node is transition source
@param state new state resulting from transition execution
*/
public void postTransition(StateConfiguration sc, int state) {
      int i, j;
      int comp;
      int st;
      TreeIterator iter;
      StateConfiguration nd;

      // pre
      iter = sc.iterator();
      while ( (nd=(StateConfiguration)iter.postorder())!=sc ) {
        comp = nd.getComponent();
        component[comp].deactivate();
      }

      sc.cutoff();

    // post
    StateConfiguration parent = (StateConfiguration)sc.getParent();
    if ( state >= 0 ) {
      sc.setState(state);
    } else {
      // check if all concurrently run peer components finished
      if ( parent == null ) {
        deactivate();
        return;
      }

      // Pass data out when a stop state is reached
      int p = parent.getComponent();
      int c = sc.getComponent();
      if ( componentOutMethod[p][c] != null ) {
        // System.out.println("postTransition, comp=" + c);
        Object[] params = new Object[2];
        params[0] = component[p];
        params[1] = component[c];
        try {
          componentOutMethod[p][c].invoke(this, params);
        } catch (Exception e) {
          System.out.println("FSMController Error");
          e.printStackTrace();
          return;
        }
      }

    }
    recepient.addElement(sc);
}

/**
 * Adjusts state configuration when an event is completely processed.
 *
*/
public void adjustStateConfiguration() {
      int i, j;
      int comp;
      int st;
      TreeIterator iter;
      StateConfiguration nd;

      if ( active==null ) return;

    // cut off all nodes in stop state (they are all leaves)
      iter = active.iterator();
      while ( (nd=(StateConfiguration)iter.postorder())!=null ) {
        if ( nd.getState() < 0 ) {
          nd.substitute(null);
        }
      }


    boolean flag;
    int index;

    do {
      flag = false;
      iter = active.iterator();
      while ( (nd=(StateConfiguration)iter.allorder())!=null ) {
        if ( nd.isLeaf() ) {
          index = 1;
          comp = nd.getComponent();
          st = nd.getState();
          for(j=0;j<component.length;j++) {
            if ( subComponent[comp][st][j] ) {
              flag = true;
	      StateConfiguration temp= new StateConfiguration();
              temp.setComponent(j);
              temp.setState(0);
	      nd.embed(temp,index++);
              // set start state
              component[j].activate();
              // Pass data in
              Object[] params = new Object[2];
              params[0] = component[comp];
              params[1] = component[j];
              if ( componentInMethod[comp][j] != null ) {
                try {
                  componentInMethod[comp][j].invoke(this, params);
                } catch (Exception e)
                { System.out.println("FSMController Error"); e.printStackTrace(); return; }
              }
            }
          }
        }
      }
    } while ( flag );
}

/**
 * Checks if the FSM given by the tip node of the specified state configuration
 * is run concurrently with
 * the FSMs that already received the currently handled event.
 *
@param point state configuration whose tip FSM is checked
*/
public boolean concurs(StateConfiguration point) {
  int i;
  for (i = 0; i < recepient.size(); i++) {
    if ( point.contains((Tree)recepient.elementAt(i)) )
      return false;
  }
  return true;
}

/**
 * Creates and returns a state configuration iterator.
*/
public TreeIterator stateConfigurationIterator() {
    recepient = new Vector();
    return active.iterator();
}

      // Code generation
/**
 * Generates a FSM controller from a specification class.
 * -v|-verbose is the only permissible parameter indicating
 * whether to print code generation details.
@param arg command line arguments
*/
public static void main(String arg[]) {

	int i, j, k, m, n;
        Vector allComponent;
        Vector allSource;
        Vector allCommunication;
        Vector allEvent;
        Vector eventOwner;
        FSM fsm;
        boolean flag;

    // get parameters
    boolean verbose = false;
    if (arg.length>1) {
        System.out.println("Too many parameters. Usage: <class> [ -v | -verbose ]");
        return;
    }
    if (arg.length==1) {
      if ( arg[0].equalsIgnoreCase("-v") || arg[0].equalsIgnoreCase("-verbose") ) {
        verbose = true;
      } else {
        System.out.println("Too few parameters. Usage: <class> [ -v | -verbose ]");
        return;
      }
    }

    if ( verbose ) {
	System.out.println("--- containment ---");
	for(j=0;j<containment.length;j++) {
		System.out.print(containment[j][0] + " ");
                System.out.print(containment[j][1] + " ");
                System.out.println(containment[j][2] + " ");
	}
        System.out.println("");

	System.out.println("--- exchange ---");
	for(j=0;j<exchange.length;j++) {
		System.out.print(exchange[j][0] + " ");
                System.out.print(exchange[j][1] + " ");
                System.out.print(exchange[j][2] + " ");
                System.out.print(exchange[j][3] + " ");
	}
        System.out.println("");

 	System.out.println("--- source ---");
	for(j=0;j<source.length;j++) {
          System.out.print(source[j][0] + ": ");
          for(k=1;k<source[j].length;k++) {
		System.out.print(source[j][k] + " ");
          }
          System.out.println("");
	}

        System.out.println("--- delivery ---" + delivery);
    }

// // // // // -------------------------------------------


    FileOutputStream adapterStream;
    PrintWriter adapter;
    try {
        adapterStream = new FileOutputStream(controller + ".java");
        adapter = new PrintWriter(adapterStream,true);
    } catch (IOException e) {
        System.out.println("Cannot open output - aborting");
        e.printStackTrace();
        return;
    }

  // Adapter code generation
  adapter.println("/* " + controller + ".java */");
  adapter.println("import java.util.*;");
  adapter.println("import java.io.*;");
  adapter.println("import fsm.*;");
  adapter.println("import graph.*;");
  adapter.print("public class " + controller + " extends " + specification + " implements ");
  if ( delivery=='q' ) {
  adapter.print(" Runnable, ");
  }
  adapter.println("EventListener, ");

  allComponent = new Vector();
  allComponent.addElement(containment[0][0]);
  for(j=0;j<containment.length;j++) {
      if ( allComponent.contains(containment[j][0])
      && containment[j][2].length()>0
      && !allComponent.contains(containment[j][2]) ) {
          allComponent.addElement(containment[j][2]);
      }
  }

  if ( verbose ) {
  System.out.println("--- components ---");
  for(j=0;j<allComponent.size();j++) {
	System.out.print(allComponent.elementAt(j) + " ");
  }
  System.out.println("");
  }

  allSource = new Vector();
  //event = new String[allComponent.size()][];

    // get allSource
    for (j = 0; j < source.length; j++) {
        if ( !allSource.contains(source[j][1]) ) {
            allSource.addElement(source[j][1]);
        }
    }

    if ( verbose ) {
    System.out.println("--- sources ---");
    for(j=0;j<allSource.size();j++) {
	System.out.print(allSource.elementAt(j) + " ");
    }
    System.out.println("");
    }

    allCommunication = new Vector();
    Class sourceClass;
    Field listenerField;
    for(i=0;i<allSource.size();i++) {
      try {
          sourceClass = Class.forName((String)(allSource.elementAt(i)));
      } catch (ClassNotFoundException e) {
          System.out.println("Undefined class: " + (String)(allSource.elementAt(i)) + " - aborting");
          return;
      }
      try {
		listenerField = sourceClass.getField("listener");
      } catch (NoSuchFieldException e) {
        System.out.println("Undefined variable 'listener' - aborting");
        e.printStackTrace();
        return;
      }
      allCommunication.addElement(listenerField.getType().getName());
    }

    if ( verbose ) {
    System.out.println("--- allCommunication ---");
    for(j=0;j<allCommunication.size();j++) {
	System.out.print(allCommunication.elementAt(j) + " ");
    }
    System.out.println("");
    }

    allEvent = new Vector();
    eventOwner = new Vector();
    for(j=0;j<allCommunication.size();j++) {
	Class inter;
	try {
	  inter = Class.forName((String)(allCommunication.elementAt(j)));
	} catch (ClassNotFoundException e) {
	  System.out.println("Undefined class: " + (String)(allCommunication.elementAt(j)) + " - aborting");
          e.printStackTrace();
          return;
        }
	Method[] inter_func = inter.getMethods();
	for(k=0;k<inter_func.length;k++) {
	  String func = inter_func[k].getName();
          if ( !allEvent.contains(func) ) {
            allEvent.addElement(func);
            eventOwner.addElement(allSource.elementAt(j));
          }
	}
    }

    if ( verbose ) {
    System.out.println("--- events ---");
    for(j=0;j<allEvent.size();j++) {
	System.out.print(allEvent.elementAt(j) + " ");
    }
    System.out.println("");
    }

  for ( i = 0; i < allCommunication.size(); i++ ) {
    adapter.print((String)(allCommunication.elementAt(i)));
    if ( i < allCommunication.size()-1 ) {
      adapter.print(",");
    }
    adapter.print(" ");
  }
  adapter.println("{");

  // ctor
  adapter.println("public " + controller + "() {");
  //adapter.println("\tsuper();");
  /*
  adapter.println("\tactive = new StateConfiguration();");
  adapter.println("\tactive.setComponent(0);");
  adapter.println("\tactive.setState(0);");
  adapter.println("\tcomponent[0].activate();");
  */
  // data exchange among components
  adapter.println("setComponentDataExchange(new String[][] {");
  for ( i = 0; i < allComponent.size(); i++ ) {
    adapter.print("{");
    for(j=0;j<allComponent.size();j++) {
        flag = false;
	for (k = 0; k < exchange.length; k++ ) {
		if ( exchange[k][0].equals(allComponent.elementAt(i))
		&& exchange[k][1].equals(allComponent.elementAt(j)) ) {
        	    adapter.print("\"" + exchange[k][2] + "\", ");
                    flag = true;
                    break;
		}
	}
        if ( !flag ) {
          adapter.print("\"\", ");
        }
    }
    adapter.print("}, ");
  }


  adapter.println("}, new String[][] { ");
  for ( i = 0; i < allComponent.size(); i++ ) {
    adapter.print("{ ");
    for(j=0;j<allComponent.size();j++) {
        flag = false;
	for (k = 0; k < exchange.length; k++ ) {
		if ( exchange[k][0].equals(allComponent.elementAt(i))
		&& exchange[k][1].equals(allComponent.elementAt(j)) ) {
        	    adapter.print("\"" + exchange[k][3] + "\", ");
                    flag = true;
                    break;
		}
	}
        if ( !flag ) {
          adapter.print("\"\", ");
        }
    }
    adapter.print("}, ");
  }
  adapter.println("} ); ");

  adapter.println("}"); // end of ctor

  adapter.println("protected Vector eventQueue = new Vector();");
  adapter.println("protected Vector eventIndexQueue = new Vector();");

  /*
  adapter.print("private static EventSource source[] = { ");
  for ( i = 0; i < allSource.size(); i++ ) {
    adapter.print("new ");
    adapter.print((String)(allSource.elementAt(i)));
    adapter.print("()");
    if ( i < allSource.size()-1 ) {
      adapter.print(",");
    }
    adapter.print(" ");
  }
  adapter.println("};");
  */


  // adapter.println("private int[] compQueue;");
  // adapter.println("int i;");

  adapter.println("static {");
  // component
  adapter.println("component = new FSM[] {");
  for ( i = 0; i < allComponent.size(); i++ ) {
    adapter.print(" new " + className((String)allComponent.elementAt(i)) + "(");
    adapter.println("), ");
  }
  adapter.println("};");

  String state[][];
  state = new String[allComponent.size()][];
  // get component states
  for ( i = 0; i < allComponent.size(); i++ ) {
    Class generatedFSM;
    try {
	  generatedFSM = Class.forName(className((String)(allComponent.elementAt(i))));
    } catch (ClassNotFoundException e) {
          System.out.println("Undefined class: " + className((String)(allComponent.elementAt(i))) + " - aborting");
          e.printStackTrace();
          return;
    }


        // Introspect generated components
        Method stateMethod;

	try {
		stateMethod = generatedFSM.getMethod("getState", null);
	} catch (Exception e) {
	  System.out.println("Undefined variable: state");
          e.printStackTrace();
          return;
        }

        // FSM properties
	try {
          fsm = (FSM)generatedFSM.newInstance();
	} catch (Exception e) {
	  System.out.println("Could not create a component instance - aborting");
          e.printStackTrace();
          return;
        }

	try {
		Object[] tmp = (Object[])(stateMethod.invoke(fsm, null));
                state[i] = new String[tmp.length];
                for ( j = 0; j < state[i].length; j++ ) {
                  state[i][j] = tmp[j].toString();
                }
	} catch (Exception e) {
	  System.out.println("Could not introspect states - aborting");
          e.printStackTrace();
          return;
        }
  }

  adapter.println("subComponent = new boolean[][][] { ");
  for ( i = 0; i < allComponent.size(); i++ ) {
    adapter.print("{ ");
    for ( j = 0; j < state[i].length; j++ ) {
      adapter.print("{ ");
      for ( m = 0; m < allComponent.size(); m++ ) {
        for ( k = 0; k < containment.length; k++ ) {
          if ( containment[k][0].equals(allComponent.elementAt(i))
          && ( containment[k][1].equals(state[i][j])
              || ( state[i][j].indexOf(containment[k][1]) >= 0 && state[i][j].indexOf("#") < 0 )
              || state[i][j].indexOf("#" + containment[k][1]) >= 0
              )
          && containment[k][2].equals(allComponent.elementAt(m)) ) {
            adapter.print("true, ");
            break;
          }
        }
        if ( k >= containment.length ) {
          adapter.print("false, ");
        }
      }
      adapter.println(" }, ");
    }
    adapter.println(" }, ");
  }
  adapter.println("};");

  adapter.println("} // end of static");

  // adapter.println("private Thread sourceThread[];");

  adapter.println("public int dequeue() {");
  adapter.println("\treturn 0;");
  adapter.println("}");

  for ( i = 0; i < allEvent.size(); i++ ) {
   adapter.println("public synchronized void " + (String)(allEvent.elementAt(i)) + "(EventObject event) {");
   adapter.println("\tif ( isActive() ) {");
   if ( delivery=='q' ) {
    adapter.println("\t\t\teventQueue.addElement(event);");
    adapter.println("\t\t\teventIndexQueue.addElement(new Integer(" + i + "));");
   } else if ( delivery=='s' ) {
    adapter.println("\tint stateIndex;");
    adapter.println("\tTreeIterator iter;");
    adapter.println("\tStateConfiguration point;");
    //adapter.println("\trecepient = new Vector();");
    //adapter.println("\t\t\titer = active.iterator();");
    adapter.println("\titer = stateConfigurationIterator();");
    adapter.println("\t\t\tcompLabel" + i + ": while ( (point = (StateConfiguration)iter.postorder())!=null ) {");
    adapter.println("\t\t\tif ( concurs(point) ) {");
    adapter.println("\t\t\tswitch(point.getComponent()) {");

    for ( k = 0; k < allComponent.size(); k++ ) {
      adapter.println("\t\t\t\tcase " + k + ": \t\t// " + (String)allComponent.elementAt(k));
      String comp_method = componentMethod((String)allComponent.elementAt(k), (String)eventOwner.elementAt(i), (String)allEvent.elementAt(i));
      if ( comp_method.length() > 0 ) {
        adapter.println("\t\t\t\t\tpreTransition(point);");
        //adapter.println("\t\t\t\t\tif ( ((" + className((String)allComponent.elementAt(k)) + ")component[" + k + "])." + comp_method + "Guard(event) ) {");
        adapter.println("\t\t\t\t\tsynchronized(component[" + k + "]) {");
        adapter.println("\t\t\t\t\t((" + className((String)allComponent.elementAt(k)) + ")component[" + k + "])." + comp_method + "(event);");
        adapter.println("\t\t\t\t\tif ( ((" + className((String)allComponent.elementAt(k)) + ")component[" + k + "]).transited() ) {");
        adapter.println("\t\t\t\t\t\tstateIndex = ((" + className((String)allComponent.elementAt(k)) + ")component[" + k + "]).getStateIndex();");
        adapter.println("\t\t\t\t\t\tpostTransition(point, stateIndex);");
        //adapter.println("\t\t\t\t\t\trecepient.addElement(point);");
        adapter.println("\t\t\t\t\t}");
        adapter.println("\t\t\t\t\t}");
      }
      adapter.println("\t\t\t\tbreak;");
    }
    adapter.println("\t\t\t} }");
    adapter.println("\t\t\t}");
    adapter.println("\t\t\tadjustStateConfiguration();");
   } else {
    System.out.println("Incorrect delivery value - aborting");
    return;
   }
   adapter.println("\t}"); // if ( !isActive() ) ((EventSource)event.getSource()).removeEventListener(this);");
   adapter.println("}");
  }

if ( delivery=='q' ) {
  adapter.println("public void run() {");
  adapter.println("\tint i;");
  adapter.println("\tint stateIndex;");
  adapter.println("\tTreeIterator iter;");
  adapter.println("\tStateConfiguration point;");

  adapter.println("\tboolean flag = true;");
  adapter.println("\tfor (;;) {");
  adapter.println("\t\tint eventIndex = -1;");
  adapter.println("\t\tEventObject eventValue = null;");

  //adapter.println("\t\t\ttry { sleep(10); }");
  //adapter.println("\t\t\tcatch (InterruptedException e) { ; }");
  adapter.println("\t\tsynchronized (this) {");
  adapter.println("\t\t\tif ( eventIndexQueue.isEmpty() ) {");
  adapter.println("\t\t\t\tif ( flag ) continue;");
  adapter.println("\t\t\t\tflag = true;");
  adapter.println("\t\t\t} else {");
  // pick event from queue
  adapter.println("\t\t\t\tint n = dequeue();");
  adapter.println("\t\t\t\teventIndex = ((Integer)(eventIndexQueue.elementAt(n))).intValue();");
  adapter.println("\t\t\t\teventIndexQueue.removeElementAt(n);");
  adapter.println("\t\t\t\teventValue = (EventObject)(eventQueue.elementAt(n));");
  adapter.println("\t\t\t\teventQueue.removeElementAt(n);");
  adapter.println("\t\t\t\tflag = false;");
  adapter.println("\t\t\t} ");
  adapter.println("\t\t} ");
    // eventless transitions
    adapter.println("\t\tif ( flag ) {");
    adapter.println("\t\t\titer = stateConfigurationIterator();");
    adapter.println("\t\t\tcompLabel_: while ( (point = (StateConfiguration)iter.postorder())!=null ) {");
    adapter.println("\t\t\tif ( concurs(point) ) {");
    adapter.println("\t\t\tswitch(point.getComponent()) {");

    for ( k = 0; k < allComponent.size(); k++ ) {
      adapter.println("\t\t\t\tcase " + k + ": \t\t// " + (String)allComponent.elementAt(k));
      //adapter.println("System.out.println(\"Eventless   \");");
      adapter.println("\t\t\t\t\tpreTransition(point);");
      //adapter.println("\t\t\t\t\tif ( ((" + className((String)allComponent.elementAt(k)) + ")component[" + k + "]).eventlessGuard() ) {");
      adapter.println("\t\t\t\t\tsynchronized(component[" + k + "]) {");
      adapter.println("\t\t\t\t\t((" + className((String)allComponent.elementAt(k)) + ")component[" + k + "]).eventless();");
      adapter.println("\t\t\t\t\tif ( ((" + className((String)allComponent.elementAt(k)) + ")component[" + k + "]).transited() ) {");
      adapter.println("\t\t\t\t\t\tstateIndex = ((" + className((String)allComponent.elementAt(k)) + ")component[" + k + "]).getStateIndex();");
      adapter.println("\t\t\t\t\t\tpostTransition(point, stateIndex);");
      adapter.println("\t\t\t\t\t}");
      adapter.println("\t\t\t\t\t}");
      adapter.println("\t\t\t\tbreak;");
    }
    adapter.println("\t\t\t}");
    adapter.println("\t\t\t}");
    adapter.println("\t\t\t}");
    adapter.println("\t\t\tadjustStateConfiguration();");
    adapter.println("\t\t\tcontinue;");
    adapter.println("\t\t}");

  // process event
  adapter.println("\t\titer = stateConfigurationIterator();");
  adapter.println("\t\tswitch( eventIndex ) {");
  for ( i = 0; i < allEvent.size(); i++ ) {
    adapter.println("\t\t\tcase " + i + ": \t\t// " + allEvent.elementAt(i));
    adapter.println("\t\t\tcompLabel" + i + ": while ( (point = (StateConfiguration)iter.postorder())!=null ) {");
    adapter.println("\t\t\tif ( concurs(point) ) {");
    adapter.println("\t\t\tswitch(point.getComponent()) {");

    for ( k = 0; k < allComponent.size(); k++ ) {
      adapter.println("\t\t\t\tcase " + k + ": \t\t// " + (String)allComponent.elementAt(k));
      String comp_method = componentMethod((String)allComponent.elementAt(k), (String)eventOwner.elementAt(i), (String)allEvent.elementAt(i));
      if ( comp_method.length() > 0 ) {
        //adapter.println("System.out.println(\"Event: " + allEvent.elementAt(i) + "\");");
        adapter.println("\t\t\t\t\tpreTransition(point);");
        //adapter.println("\t\t\t\t\tif ( ((" + className((String)allComponent.elementAt(k)) + ")component[" + k + "])." + comp_method + "Guard(eventValue) ) {");
        adapter.println("\t\t\t\t\tsynchronized(component[" + k + "]) {");
        adapter.println("\t\t\t\t\t((" + className((String)allComponent.elementAt(k)) + ")component[" + k + "])." + comp_method + "(eventValue);");
        adapter.println("\t\t\t\t\tif ( ((" + className((String)allComponent.elementAt(k)) + ")component[" + k + "]).transited() ) {");
        adapter.println("\t\t\t\t\t\tstateIndex = ((" + className((String)allComponent.elementAt(k)) + ")component[" + k + "]).getStateIndex();");
        adapter.println("\t\t\t\t\t\tpostTransition(point, stateIndex);");
        adapter.println("\t\t\t\t\t}");
        adapter.println("\t\t\t\t\t}");
      }
      adapter.println("\t\t\t\tbreak;");
    }
    adapter.println("\t\t\t}");
    adapter.println("\t\t\t}");
    adapter.println("\t\t\t}");
    adapter.println("\t\t\tadjustStateConfiguration();");

    adapter.println("\t\t\t\tbreak;");
  }

  adapter.println("\t\t}");
  adapter.println("\t}");
  adapter.println("}");
} // end of delivery=='q'

  adapter.println("}");
  if ( verbose ) {
  System.out.println("Done");
  }

}

/**
 * Retrieves a component transition event method name for the specified
 * component, event source, and listener interface method.
@param comp FSM component
@param src event source
@param m listener interface method
*/
private static String componentMethod(String comp, String src, String m) {
  int i, j;
  for ( i = 0; i < source.length; i++ ) {
    if ( source[i][0].equals(comp)
    && source[i][1].equals(src) ) {
      for ( j = 2; j < source[i].length; j+=2 ) {
        if ( source[i][j+1].equals(m) ) {
          return source[i][j];
        }
      }
      break;
    }
  }
  return "";
}

/**
 * Extracts the beginning of the specified string till character ':'.
@param comp string
*/
private static String className(String comp) {
  if ( comp.indexOf(":")<0 )
    return comp;
  else
    return comp.substring(0,comp.indexOf(":"));
}

}