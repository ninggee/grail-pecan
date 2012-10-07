
/* TVEventListener.java */

import java.util.*;

public interface TVEventListener extends EventListener {

        // for TVHandler
	public void power ( EventObject evt );
        // mutual
	public void up ( EventObject evt );
        // mutual
	public void down ( EventObject evt );
        // for TVHandler
	public void muting ( EventObject evt );
	public void one ( EventObject evt );
	public void two ( EventObject evt );
	public void three ( EventObject evt );
	public void four ( EventObject evt );
	public void five ( EventObject evt );
	public void six ( EventObject evt );
	public void seven ( EventObject evt );
	public void eight ( EventObject evt );
	public void nine ( EventObject evt );
	public void zero ( EventObject evt );
	public void enter ( EventObject evt );
        public void program ( EventObject evt );
        public void wait ( EventObject evt );
        public void stop ( EventObject evt );
        // for TVSet
        public void toggle ( EventObject evt );
        // for TVDevice
        public void device ( EventObject evt );
        public void start ( EventObject evt );
}
