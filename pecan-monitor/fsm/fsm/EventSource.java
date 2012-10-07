
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

/* EventSource.java */

package fsm;

import java.util.*;

/**
 * This interface declares methods to add and remove evemt listeners.
 * Presumably, classes that are event sources implement this interface.
 *
 *
 *
@author Alexander Sakharov
 */
public interface EventSource {

/**
 * Adds the specified event listener to the listeners maintained by an event source.
@param parm event listener to add
*/
	public abstract void addEventListener(EventListener parm);

/**
 * Removes the specified event listener from the set of listeners maintained by an event source.
@param parm event listener to remove
*/
	public abstract void removeEventListener(EventListener parm);

}

