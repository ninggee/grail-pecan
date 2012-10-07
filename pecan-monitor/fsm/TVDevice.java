/* TVDevice.java */

import fsm.*;
import java.util.*;

public class TVDevice extends FSM {

// selection of devices to control
// TVDevice is to be run concurrently with TVSet

static {

        specification = "TVDevice";
        fsm = "TVDeviceSub";

        start = "Start";
	stop = "Stop";

	transition = new String[][] {
	  { "Start", "device", "Device1", "selectDevice1", "" },
          { "Device1", "device", "Device2", "selectDevice2", "" },
          { "Device2", "device", "Device3", "selectDevice3", "" },
          { "Device3", "device", "Device1", "selectDevice1", "" },
          { "Device1", "up", "Device1", "advanceDevice(1)", "" },
          { "Device2", "up", "Device2", "advanceDevice(2)", "" },
          { "Device3", "up", "Device3", "advanceDevice(3)", "" },
	  { "Device1", "run", "Stop", "runDevice1", "" },
          { "Device2", "run", "Stop", "runDevice2", "" },
          { "Device3", "run", "Stop", "runDevice3", "" },
	};

}



// Transition functions

protected void selectDevice1()
{
	System.out.println("TVDevice: selectDevice1");
}

protected void selectDevice2()
{
	System.out.println("TVDevice: selectDevice2");
}

protected void selectDevice3()
{
	System.out.println("TVDevice: selectDevice1");
}

protected void runDevice1()
{
	System.out.println("TVDevice: runDevice1");
}

protected void runDevice2()
{
	System.out.println("TVDevice: runDevice2");
}

protected void runDevice3()
{
	System.out.println("TVDevice: runDevice3");
}

protected void advanceDevice(int n)
{
	System.out.println("TVDevice: advanceDevice " + n);
}

}