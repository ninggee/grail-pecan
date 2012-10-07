
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

/* FSM.java */

package fsm;

import java.util.*;
import java.io.*;
import java.beans.*;

/**
 * This class serves as a base for FSM components.
 * Any FSM component is specified as a class derived from this class.
 * It declares static variables for storing a start state name,
 * an optional stop state name, and transitions as strings.
 * Derivatives of this class are FSM specification classes.
 * They define values of the aforementioned variables.
 * Transition actions are given by the names of methods implementing them
 * in the same FSM specification classes. Argument lists optionally follow
 * the method names. Guards are given by valid Java expressions substantiated
 * in the same FSM specification classes.
 *
 * Method main of this class is a code generation facility.
 * When a specification class is executed, i. e. its main method is run,
 * a FSM component is generated as a class derived from the specification class.
 *
@author Alexander Sakharov
@see fsm.FSMController
 */
public abstract class FSM extends Object {

	// Component specification

	// specification class name
        protected static String specification;
	// generated component class name
        protected static String fsm;

	protected static String stop;
	protected static String start;
	protected static String[][] transition;
			// of { "state1", "event", "state2" "action", "guard" }

        // Generated
        protected static Object[] state;		// of String

        // Internal data
        protected EventObject event;
        // protected int eventIndex;
        protected int stateIndex = -1;
        // protected int guard = -1;
        protected boolean transit = false;

        // Property get/set methods

/**
 * Yields the specified stop state.
*/
public String getStop() {
	return stop;
}

/**
 * Yields the specified start state.
*/
public String getStart() {
	return start;
}

/**
 * Yields transitions.
*/
public String[][] getTransition() {
	return transition;
}

/**
 * Yields the name of specification class
*/
public String getSpecification() {
	return specification;
}

/**
 * Yields the name of generated specification class
*/
public String getFsm() {
	return fsm;
}

/**
 * Yields the currntly processed event.
*/
public EventObject getEvent() {
	return event;
}

/**
 * Yields the state index
*/
public int getStateIndex() {
	return stateIndex;
}

/**
 * Yields boolean indicating whether the latest event triggered a transition
 * Presumably, this function is called once after an event methos invocation.
 * If called two times in a row, the second call will always return false.
*/
public boolean transited() {
        boolean t = transit;
        transit = false;
	return t;
}

/**
 * Yields array of FSM states
*/
public Object[] getState() {
	return state;
}

/**
 * Activates this FSM.
*/
public void activate() {
	stateIndex = 0;
}

/**
 * Deactivates this FSM.
*/
public void deactivate() {
	stateIndex = -1;
}

/**
 * Checks if this FSM is active.
*/
public boolean isActive() {
	return stateIndex >= 0;
}

        // Code generation helper methods

/**
 * Yields the index of the specified string in the specified array of strings.
 * If the array does not contain the specified string, -1 is returned and
 * a warning is printed.
@param s string to match against array elements
@param s array of strings
*/
private static int indexInArray(String s, String[] a) {
	int i;
	for(i=0;i<a.length;i++) {
		if ( a[i].length() <= s.length() ) {
			if ( a[i].equalsIgnoreCase(s) )
				return i;
		}
	}
	System.out.println("Warning: " + s + " not listed");
	return -1;
}

/**
 * Checks if transition with the specified index is conventional, i. e. does not
 * contain regular expressions of events.
@param j transition index
*/
private static boolean isConventional(int j) {
          if ( transition[j][1].indexOf('(')>=0
          || transition[j][1].indexOf(')')>=0
          || transition[j][1].indexOf('|')>=0
          || transition[j][1].indexOf('*')>=0 ) {
            return false;
          } else {
            return true;
          }
}

/**
 * Generates a FSM component from a specification class.
 * -v|-verbose is the only permissible parameter indicating
 * whether to print code generation details.
@param arg command line arguments
*/
public static void main(String arg[])
{
	int i, j, k, m, n;

    String[] state;
    String name;
    String base;
    String[] transEvent;
    String[] action;
    String[] guard;
    Vector stateV;
    Vector[] transitionV;
    Vector eventVector;
    int[] state1Index;
    int[] eventIndex;
    int[] state2Index;
    int[] state1IndexV;
    int[] eventIndexV;
    int[] state2IndexV;
    String[] actionV;
    String[] guardV;
    Vector stateTransV;

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

    // open files
    FileOutputStream outStream;
    PrintWriter code;
    try {
      outStream = new FileOutputStream(fsm + ".java");
      if ( verbose ) {
      System.out.println(fsm + ".java");
      }
      code = new PrintWriter(outStream,true);
    } catch (IOException e) {
      System.out.println("Cannot open output - aborting");
      e.printStackTrace();
      return;
    }

    // Process transitions with event expressions
    stateV = new Vector();
    stateTransV = new Vector();

    eventVector = new Vector();

    transitionV = new Vector[6];
    transitionV[0] = new Vector();
    transitionV[1] = new Vector();
    transitionV[2] = new Vector();
    transitionV[3] = new Vector();
    transitionV[4] = new Vector();
    transitionV[5] = new Vector();
    for(j=0;j<transition.length;j++) {
          // Regex expr;   // ().|*
          Vector internalState;   /* of HashSet of Regex */
          Vector transitionSource;   /* of HashSet of Regex */
          Vector transitionEvent;   /* of String */
          Vector transitionTarget;   /* of HashSet of Regex */



          if ( verbose ) {
          System.out.println("--- transitionV ! --- " + j);
	  for(k=0;k<transitionV[1].size();k++) {
                System.out.print("(");
		System.out.print((String)(transitionV[0].elementAt(k)) + ", ");
                System.out.print((String)(transitionV[1].elementAt(k)) + ") -> ");
                System.out.print((String)(transitionV[2].elementAt(k)) + " ");
                System.out.println("");
          }
          }

    } // end loop over transitions

        // Collect states
        stateV = new Vector();
        stateTransV = new Vector();
        stateV.addElement(start);
        stateTransV.addElement(new Integer(-1));
        for(j=0;j<transition.length;j++) {
          if ( !stateV.contains(transition[j][0]) ) {
            stateV.addElement(transition[j][0]);
            stateTransV.addElement(new Integer(-1));
          }
          if ( !stateV.contains(transition[j][2]) ) {
            stateV.addElement(transition[j][2]);
            stateTransV.addElement(new Integer(-1));
          }
        }

        //
        for(j=0;j<transitionV[0].size();j++) {
          if ( !stateV.contains(transitionV[0].elementAt(j)) ) {
            stateV.addElement(transitionV[0].elementAt(j));
            stateTransV.addElement(transitionV[5].elementAt(j));
          }
          if ( !stateV.contains(transitionV[2].elementAt(j)) ) {
            stateV.addElement(transitionV[2].elementAt(j));
            stateTransV.addElement(transitionV[5].elementAt(j));
          }
        }

        state = new String[stateV.size()];
        for(j=0;j<stateV.size();j++) {
          state[j] = (String)(stateV.elementAt(j));
        }

        // Collect transition events
        eventVector = new Vector();
        for(j=0;j<transition.length;j++) {
          if ( isConventional(j) ) {
            if ( !eventVector.contains(transition[j][1]) ){
              eventVector.addElement(transition[j][1]);
            }
          }
        }

        for(j=0;j<transitionV[1].size();j++) {
            if ( !eventVector.contains(transitionV[1].elementAt(j)) ){
              eventVector.addElement(transitionV[1].elementAt(j));
            }
        }

        transEvent = new String[eventVector.size()];
        for(k=0;k<eventVector.size();k++) {
          transEvent[k] = (String)(eventVector.elementAt(k));
        }

        // Calculate indices
	state1Index = new int[transition.length];
        eventIndex = new int[transition.length];
	state2Index = new int[transition.length];
        action = new String[transition.length];
        guard = new String[transition.length];
	state1IndexV = new int[transitionV[0].size()];
        eventIndexV = new int[transitionV[0].size()];
	state2IndexV = new int[transitionV[0].size()];
        actionV = new String[transitionV[0].size()];
        guardV = new String[transitionV[0].size()];

	for(j=0;j<transition.length;j++) {
          if ( isConventional(j ) ) {
		state1Index[j] =  indexInArray(transition[j][0],state);
		eventIndex[j] =  indexInArray(transition[j][1],transEvent);
		state2Index[j] =  indexInArray(transition[j][2],state);
                if ( transition[j].length>3 && transition[j][3] != null ) {
                  action[j] = transition[j][3];
                }
                if ( transition[j].length>4 && transition[j][4] != null ) {
                  guard[j] = transition[j][4];
                }
          }
        }

        if ( verbose ) {
        System.out.println("--- stateTransV ---");
	for(j=0;j<stateTransV.size();j++) {
                System.out.print(stateTransV.elementAt(j) + " " );
        }
        System.out.println("");
        }

        if ( verbose ) {
        System.out.println("--- indicesV ---");
        }
	for(j=0;j<transitionV[0].size();j++) {
		state1IndexV[j] =  indexInArray((String)(transitionV[0].elementAt(j)),state);
		eventIndexV[j] =  indexInArray((String)(transitionV[1].elementAt(j)),transEvent);
		state2IndexV[j] =  indexInArray((String)(transitionV[2].elementAt(j)),state);
                actionV[j] = (String)(transitionV[3].elementAt(j));
                guardV[j] = (String)(transitionV[4].elementAt(j));
                if ( verbose ) {
                System.out.println("+" + state1IndexV[j] + "+" + eventIndexV[j] + "+" + state2IndexV[j] );
                }
        }
	//

        // print specifications (if verbose)
        if ( verbose ) {
	System.out.println("--- state ---");
	for(j=0;j<state.length;j++) {
		System.out.println(state[j]);
	}

	System.out.println("--- start ---");
	System.out.println(start);

	System.out.println("--- stop ---");
	System.out.println(stop);

	System.out.println("--- event ---");
	for(j=0;j<transEvent.length;j++) {
		System.out.println(transEvent[j]);
	}

	System.out.println("--- transition ---");
	for(j=0;j<transition.length;j++) {
          if ( isConventional(j) ) {
                System.out.print("(");
		System.out.print(state1Index[j]);
                System.out.print(",");
		System.out.print(eventIndex[j]);
                System.out.print(") -> ");
		System.out.print(state2Index[j]);
                System.out.print(" ");
		System.out.print(action[j]);
                if ( guard[j] != null ) {
                  System.out.print(" ?");
		  System.out.print(guard[j]);
                }
                System.out.println("");
          }
	}
        } // for verbose


	// Code generation starts
	code.println("/* " + fsm + ".java */");
	code.println("import java.util.*;");
        code.println("import fsm.*;");
	code.println("public class " + fsm + " extends " + specification + " implements EventListener ");
	code.println("{");

        // state[]
        code.println("static { state = new Object[] { ");
        for(j=0;j<state.length;j++) {
          code.println("\"" + state[j] + "\", ");
        }
        code.println("}; }");

        // Generate event vectors
        for(k=0;k<transition.length;k++) {
          if ( !isConventional(k) ) {
            code.println("protected Vector eventVector" + k + " = new Vector(); ");
          }
        }

	// Generation of event procedures
        boolean indent;
	for(j=0;j<transEvent.length;j++) {
	  code.print("public synchronized void ");
	  code.println(transEvent[j] + "(EventObject eventValue) {");
          code.println("\tevent = eventValue;");
          //code.println("\ttransit = false;");
	  code.println("\tswitch(stateIndex) {");

          for (m=0; m < state.length; m++ ) {
            if ( state[m].indexOf(':')<0 ) {
                boolean flag = false;
		for(k=0;k<transition.length;k++) {
                  if ( isConventional(k)
                  && eventIndex[k] == j && state1Index[k] == m ) {
                    flag = true;
                    break;
                  }
                }
		for(k=0;k<transitionV[0].size();k++) {
                      if ( eventIndexV[k] == j && state1IndexV[k] == m ) {
                        flag = true;
                        break;
                      }
                }
                if ( flag ) {
                    code.print("\t\tcase ");
		    code.print(m);
		    code.println(": ");
                    for(k=0;k<transition.length;k++) {
                        boolean transitionFound = false;
			if ( isConventional(k)
                        && eventIndex[k] == j && state1Index[k] == m ) {
                                code.print("\t\t\t");
                                if ( guard[k]!=null && guard[k].length()!=0 ) {
                                    code.println("if ( " + guard[k] + " ) { ");
                                    //code.println("if ( guard==" + k + " ) { ");
                                    code.print("\t\t\t\t");
				}
                                if ( action[k]!=null && action[k].length()!=0 ) {
				  code.print(action[k]);
                                  if ( action[k].indexOf('(') < 0 ) {
				    code.print("(); ");
                                  } else {
                                    code.print("; ");
                                  }
                                }
				if ( state[state2Index[k]].equals(stop) ) {
					code.print("deactivate(); ");
                                        code.print("transit = true; ");
				} else {
					code.print("stateIndex = ");
					code.print(state2Index[k]);
					code.print("; ");
                                        code.print("transit = true; ");
				}
                                if ( guard[k]!=null && guard[k].length()!=0 ) {
                                  code.println("break;");
                                  code.println("\t\t\t}");
				} else {
                                  code.println("");
                                }
			}
                    }

                    // generate transitions entering internal machines
                    for(k=0;k<transitionV[0].size();k++) {
			if ( eventIndexV[k] == j && state1IndexV[k] == m ) {
                                indent = false;
                                if ( guardV[k]!=null && guardV[k].length()!=0 ) {
                                    code.println("\t\t\tif ( " + guardV[k] + " ) { ");
                                    //code.println("\t\t\tif ( guard==" + ((Integer)transitionV[5].elementAt(k)).intValue() + " ) { ");
				    indent = true;
                                }
                                if ( indent ) code.print("\t");
                                code.println("\t\t\teventVector"
                                + ((String)transitionV[2].elementAt(k)).substring(((String)transitionV[2].elementAt(k)).indexOf(":") + 1,
                                  ((String)transitionV[2].elementAt(k)).indexOf("/"))
                                + ".addElement(eventValue); ");
                                if ( actionV[k]!=null && actionV[k].length()!=0 ) {
				  if ( indent ) code.print("\t");
                                  int ind1 = actionV[k].indexOf(transEvent[j] + ":");
                                  int ind2 = actionV[k].indexOf("~default:");
                                  String s = null;
                                  int last;
                                  if ( ind1>=0 ) {
                                    last = actionV[k].indexOf(";", ind1 + transEvent[j].length() + 1);
                                    if ( last<0 ) last = actionV[k].length();
                                    s = actionV[k].substring(ind1 + transEvent[j].length() + 1, last);
                                    code.print("\t\t\t" + s);
                                  } else if ( ind2>=0 ) {
                                    last = actionV[k].indexOf(";", ind2 + 9);
                                    if ( last<0 ) last = actionV[k].length();
                                    s = actionV[k].substring(ind2 + 9, last);
                                    code.print("\t\t\t" + s);
                                  }
                                  if ( s!=null && s.indexOf('(') < 0 ) {
				    code.print("(); ");
                                  } else {
                                    code.print("; ");
                                  }
                                }
				if ( state[state2IndexV[k]].equals(stop) ) {
					code.print("deactivate(); ");
                                        code.print("transit = true; ");
				} else {
					code.print("stateIndex = ");
					code.print(state2IndexV[k]);
					code.print("; ");
                                        code.print("transit = true; ");
				}
                                if ( guardV[k]!=null && guardV[k].length()!=0 ) {
                                  code.println("break;");
                                  code.println("\t\t\t}");
				} else {
                                  code.println("");
                                }
			}
                    }
                    code.println("\t\tbreak;");
                }

            } else { // internal state pair
                code.print("\t\tcase ");
		code.print(m);
	        code.println(": ");
                boolean flag = false;
		for(k=0;k<transitionV[0].size();k++) {
                  if ( eventIndexV[k] == j && state1IndexV[k] == m ) {
                    flag = true;
                    break;
                  }
                }
                if ( flag ) {
                    for(k=0;k<transitionV[0].size();k++) {
			if ( eventIndexV[k] == j && state1IndexV[k] == m ) {
                                code.println("\t\t\teventVector"
                                + state[m].substring(state[m].indexOf(":") + 1, state[m].indexOf("/"))
                                + ".addElement(eventValue); ");
                                if ( actionV[k]!=null && actionV[k].length()!=0 ) {
                                  int ind1 = actionV[k].indexOf(transEvent[j] + ":");
                                  int ind2 = actionV[k].indexOf("~default:");
                                  String s = null;
                                  int last;
                                  if ( ind1>=0 ) {
                                    last = actionV[k].indexOf(";", ind1 + transEvent[j].length() + 1);
                                    if ( last<0 ) last = actionV[k].length();
                                    s = actionV[k].substring(ind1 + transEvent[j].length() + 1, last);
                                    code.print("\t\t\t" + s);
                                  } else if ( ind2>=0 ) {
                                    last = actionV[k].indexOf(";", ind2 + 9);
                                    if ( last<0 ) last = actionV[k].length();
                                    s = actionV[k].substring(ind2 + 9, last);
                                    code.print("\t\t\t" + s);
                                  }
                                  if ( s!=null && s.indexOf('(') < 0 ) {
				    code.print("(); ");
                                  } else {
                                    code.print("; ");
                                  }
                                }
				if ( state[state2IndexV[k]].equals(stop) ) {
					code.print("deactivate(); ");
                                        code.print("transit = true;");
				} else {
					code.print("stateIndex = ");
					code.print(state2IndexV[k]);
					code.print("; ");
                                        code.print("transit = true;");
				}
                                code.println("");
			}
                    }
                } else { // no internal transitions with this event and hidden state
                    code.println("\t\t\teventVector"
                    + state[m].substring(state[m].indexOf(":") + 1, state[m].indexOf("/"))
                    + ".removeAllElements(); ");

                    int tind = -1;
                    if ( stateTransV.elementAt(m)!=null ) {
                      tind = ((Integer)stateTransV.elementAt(m)).intValue();
                    }
                    if ( tind>=0 ) {
                                  int ind1 = transition[tind][3].indexOf("~normal:");
                                  int ind2 = transition[tind][3].indexOf("~premature:");
                                  String s = null;
                                  int last;
                                  if ( ind1>=0 && state[m].indexOf('#')>=0 ) {
                                    last = transition[tind][3].indexOf(";", ind1 + 8);
                                    if ( last<0 ) last = transition[tind][3].length();
                                    s = transition[tind][3].substring(ind1 + 8, last);
                                    code.print("\t\t\t" + s);
                                  } else if ( ind2>=0 && state[m].indexOf('#')<0 ) {
                                    last = transition[tind][3].indexOf(";", ind2 + 11);
                                    if ( last<0 ) last = transition[tind][3].length();
                                    s = transition[tind][3].substring(ind2 + 11, last);
                                    code.print("\t\t\t" + s);
                                  }
                                  if ( s!=null && s.indexOf('(') < 0 ) {
				    code.print("(); ");
                                  }
                                  code.println("");
                    }

		    code.print("\t\t\tstateIndex = ");
                    String initialState;
                    if ( state[m].indexOf("#")<0 ) {
                      initialState = state[m].substring(0, state[m].indexOf(":"));
                    } else {
                      initialState = state[m].substring(state[m].indexOf("#") + 1, state[m].length());
                    }
		    code.print(indexInArray(initialState,state));
		    code.print("; ");
                    code.println("transit = true; ");
                    code.println("\t\t\t" + transEvent[j] + "(eventValue); ");
                }
                code.println("\t\tbreak;");
            }
          }
          code.println("\t}");
          // code.println("\treturn stateIndex;");
          code.println("}");
	}

	// eventless
        code.println("public void eventless() {");
        //code.println("\ttransit = false;");
	code.println("\tswitch(stateIndex) {");

        for (m=0; m < state.length; m++ ) {
          if ( state[m].indexOf(':')<0 ) {
                boolean flag = false;
		for(k=0;k<transition.length;k++) {
                  if ( isConventional(k)
                  && transition[k][1].length() == 0 && state1Index[k] == m ) {
                    flag = true;
                    break;
                  }
                }
                if ( flag ) {
                    code.print("\t\tcase ");
		    code.print(m);
		    code.println(": ");
                    for(k=0;k<transition.length;k++) {
			if ( isConventional(k)
                        && transition[k][1].length() == 0 && state1Index[k] == m ) {
                                code.print("\t\t\t");
                                if ( guard[k]!=null && guard[k].length()!=0 ) {
                                  code.println("if ( " + guard[k] + " ) { ");
                                  //code.println("if ( guard==" + k + " ) { ");
                                  code.print("\t\t\t\t");
				}
                                if ( action[k]!=null && action[k].length()!=0 ) {
				  code.print(action[k]);
                                  if ( action[k].indexOf('(') < 0 ) {
				    code.print("(); ");
                                  } else {
                                    code.print("; ");
                                  }
                                }
				if ( state[state2Index[k]].equals(stop) ) {
					code.print("deactivate(); ");
                                        code.print("transit = true; ");
				} else {
					code.print("stateIndex = ");
					code.print(state2Index[k]);
					code.print("; ");
                                        code.print("transit = true; ");
				}
                                code.println("break;");
                                if ( guard[k]!=null && guard[k].length()!=0 ) {
                                  code.println("");
                                  code.println("\t\t\t}");
				}
			}
                    }
                    code.println("");
		}
          }
        }
        code.println("\t}");
        // code.println("\treturn stateIndex;");
        code.println("}");

        code.println("}");
	code.close();


  if ( verbose ) {
  System.out.println("Done");
  }
}

}
