/* Main.java */

import fsm.*;

public class Main extends Object {

public static void main(String argv[])
{
    int i;

    try {

	TVAppAdapter adapter = new TVAppAdapter();

        EventSource source[] = { new TVRemote() };
	for(i=0;i<source.length;i++) {
		source[i].addEventListener(adapter);
	}
	Thread[] sourceThread = new Thread[source.length];
	for(i=source.length-1;i>=0;i--) {
		sourceThread[i]= new Thread((Runnable)source[i]);
		sourceThread[i].start();
	}

        for(i=0;i<source.length;i++) {
		sourceThread[i].join();
	}
	for(i=0;i<source.length;i++) {
		source[i].removeEventListener(adapter);
	}

    } catch (Exception e) {
	System.out.println("Main: Error");
        e.printStackTrace();
        return;
    }
}

}