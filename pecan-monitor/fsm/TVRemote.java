
/* TVRemote.java */

import java.io.*;
import java.util.*;
import fsm.*;

public class TVRemote extends Object implements EventSource, Runnable {

	public TVEventListener listener=null;
	private EventObject evt = new EventObject(new Object());

public void addEventListener(EventListener parm)
{
	listener=(TVEventListener)parm;
}

public void removeEventListener(EventListener parm)
{
	listener=null;
}

public void run()
{
	int i, j;
	for (i=0;i<300;i++)
		i+=0;
	System.out.println("TVRemote: will power<");
	if ( listener!=null )
		listener.power(evt);
	System.out.println(">power");
	for (i=0;i<20000;i++)
		i+=0;
	System.out.println("TVRemote: will down<");
	if ( listener!=null )
		listener.down(evt);
	System.out.println(">down");
	for (i=0;i<30000;i++)
		i+=0;
	System.out.println("TVRemote: will muting<");
	if ( listener!=null )
		listener.muting(evt);
	System.out.println(">muting (off)");
	for (i=0;i<20000;i++)
          for (j=0;j<200;j++)
		i+=0;
	System.out.println("TVRemote: will down<");
	if ( listener!=null )
		listener.down(evt);
	System.out.println(">down");
	for (i=0;i<20000;i++)
          for (j=0;j<100;j++)
		i+=0;
	System.out.println("TVRemote: will up<");
	if ( listener!=null )
		listener.up(evt);
	System.out.println(">up");
	for (i=0;i<20000;i++)
          for (j=0;j<300;j++)
		i+=0;
	System.out.println("TVRemote: will one<");
	if ( listener!=null )
		listener.one(evt);
	System.out.println(">one");

	System.out.println("TVRemote: will two<");
	if ( listener!=null )
		listener.two(evt);
	System.out.println(">two");
	for (i=0;i<10000;i++)
		i+=0;
	System.out.println("TVRemote: will enter<");
	if ( listener!=null )
		listener.enter(evt);
	System.out.println(">enter");
	for (i=0;i<20000;i++)
		i+=0;
	System.out.println("TVRemote: will down<");
	if ( listener!=null )
		listener.down(evt);
	System.out.println(">down");
	for (i=0;i<20000;i++)
          for (j=0;j<500;j++)
		i+=0;
	System.out.println("TVRemote: will enter<");
	if ( listener!=null )
		listener.enter(evt);
	System.out.println(">enter");
	for (i=0;i<10000;i++)
		i+=0;
	System.out.println("TVRemote: will down<");
	if ( listener!=null )
		listener.down(evt);
	System.out.println(">down");
	for (i=0;i<10000;i++)
		i+=0;
	System.out.println("TVRemote: will down<");
	if ( listener!=null )
		listener.down(evt);
	System.out.println(">down");
	for (i=0;i<10000;i++)
          for (j=0;j<100;j++)
		i+=0;
	System.out.println("TVRemote: will two<");
	if ( listener!=null )
		listener.two(evt);
	System.out.println(">two");
	for (i=0;i<20000;i++)
		i+=0;
	System.out.println("TVRemote: will three<");
	if ( listener!=null )
		listener.three(evt);
	System.out.println(">three");
	for (i=0;i<10000;i++)
		i+=0;
	System.out.println("TVRemote: will enter<");
	if ( listener!=null )
		listener.enter(evt);
	System.out.println(">enter");

 	for (i=0;i<10000;i++)
          for (j=0;j<100;j++)
		i+=0;
	System.out.println("TVRemote: will toggle<");
	if ( listener!=null )
		listener.toggle(evt);
	System.out.println(">toggle (set to brightness)");
	for (i=0;i<30000;i++)
		i+=0;
	System.out.println("TVRemote: will up<");
	if ( listener!=null )
		listener.up(evt);
	System.out.println(">up (brightness)");

	for (i=0;i<10000;i++)
		i+=0;
	System.out.println("TVRemote: will device<");
	if ( listener!=null )
		listener.device(evt);
	System.out.println(">device");
	for (i=0;i<10000;i++)
		i+=0;
	System.out.println("TVRemote: will device<");
	if ( listener!=null )
		listener.device(evt);
	System.out.println(">device (move to second device)");
	for (i=0;i<30000;i++)
		i+=0;

        // this is for both Set and Device
  	System.out.println("TVRemote: will up (Set and Device)<");
	if ( listener!=null )
		listener.up(evt);
	System.out.println(">up ");
 	System.out.println("TVRemote: will up (Set and Device)<");
	if ( listener!=null )
		listener.up(evt);
	System.out.println(">up ");

	System.out.println("TVRemote: will start<");
	if ( listener!=null )
		listener.start(evt);
	System.out.println(">start (run device 2)");


	for (i=0;i<20000;i++)
		i+=0;
	System.out.println("TVRemote: will up<");
	if ( listener!=null )
		listener.up(evt);
	System.out.println(">up ??? (brightness up)");
 	for (i=0;i<20000;i++)
          for (j=0;j<300;j++)
		i+=0;
	System.out.println("TVRemote: will toggle<");
	if ( listener!=null )
		listener.toggle(evt);
	System.out.println(">toggle (finish set)");

	for (i=0;i<20000;i++)
          for (j=0;j<500;j++)
		i+=0;
	System.out.println("TVRemote: will muting<");
	if ( listener!=null )
		listener.muting(evt);
	System.out.println(">muting (on)");
	for (i=0;i<2000;i++)
		i+=0;
	System.out.println("TVRemote: will up<");
	if ( listener!=null )
		listener.up(evt);
	System.out.println(">up (sound on)");
	for (i=0;i<10000;i++)
		i+=0;
	System.out.println("TVRemote: will up<");
	if ( listener!=null )
		listener.up(evt);
	System.out.println(">up");

	for (i=0;i<20000;i++)
          for (j=0;j<100;j++)
		i+=0;
	System.out.println("TVRemote: will program<");
	if ( listener!=null )
		listener.program(evt);
	System.out.println(">program");
	for (i=0;i<10000;i++)
		i+=0;
	System.out.println("TVRemote: will wait<");
	if ( listener!=null )
		listener.wait(evt);
	System.out.println(">wait");
	for (i=0;i<10000;i++)
		i+=0;
	System.out.println("TVRemote: will down<");
	if ( listener!=null )
		listener.down(evt);
	System.out.println(">down");

	for (i=0;i<20000;i++)
          for (j=0;j<100;j++)
		i+=0;
	System.out.println("TVRemote: will power<");
	if ( listener!=null )
		listener.power(evt);
	System.out.println(">power (off)");
	for (i=0;i<100;i++)
		i+=0;
}

}
