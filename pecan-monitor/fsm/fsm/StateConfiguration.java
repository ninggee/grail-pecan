
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

/* StateConfiguration.java */

package fsm;

import graph.*;

/**
 * This class serves to represent state configurations (UML term).
 * State configurations are trees. Each node holds
 * a component index and index of components state.
 *
 *
 *
@author Alexander Sakharov
 */
public class StateConfiguration extends Tree implements Cloneable {

	private int comp;
        private int st;

public StateConfiguration() {
  comp = -1;
  st = -1;
}

/**
 * Yields component index.
 *
*/
public int getComponent() {
        return comp;
}

/**
 * Yields component state index.
 *
*/
public int getState() {
        return st;
}

/**
 * Stores the specified values as the component index.
@param c component index
*/
public void setComponent(int c) {
        comp = c;
}

/**
 * Stores the specified value as the component state index.
@param s component state index
*/
public void setState(int s) {
        st = s;
}

/**
 * Compares two nodes.
*/
public boolean nodeEqual(Tree q)
{
	return (comp == ((StateConfiguration)q).comp && st == ((StateConfiguration)q).st);
}

/**
 * Copies this node.
*/
public Tree nodeCopy()
{
	StateConfiguration e;
	e = new StateConfiguration();
	e.comp = comp;
        e.st = st;
	return e;
}

}
