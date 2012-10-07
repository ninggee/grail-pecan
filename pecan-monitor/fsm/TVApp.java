/* TVApp.java */

import fsm.*;
import java.util.*;

public class TVApp extends FSMController {

static {
        /*
	concurrency = new String[1][];
        concurrency[0] = new String[4];
        concurrency[0][0] = "TVHandler";
	concurrency[0][1] = "TVClock";
        concurrency[0][2] = "";
        concurrency[0][3] = "";
        */

        specification = "TVApp";
        controller = "TVAppAdapter";

	containment = new String[2][];
        containment[0] = new String[3];
        containment[0][0] = "TVHandlerSub";
	containment[0][1] = "On";
        containment[0][2] = "TVSetSub";

        containment[1] = new String[3];
        containment[1][0] = "TVHandlerSub";
	containment[1][1] = "On";
        containment[1][2] = "TVDeviceSub";

	exchange = new String[2][];
        exchange[0] = new String[] { "TVHandlerSub", "TVSetSub", "passSetIn", "passSetOut" };
        exchange[1] = new String[] { "TVHandlerSub", "TVDeviceSub", "passDeviceIn", "passDeviceOut" };

        source = new String[3][];
        source[0] = new String[] {
        "TVHandlerSub",
	"TVRemote",
        "power",
        "power",
        "up",
        "up",
        "down",
        "down",
        "muting",
        "muting",
/*
        "enter",
        "enter",
        "zero",
        "zero",
        "one",
        "one",
        "two",
        "two",
        "three",
        "three",
        "four",
        "four",
        "five",
        "five",
        "six",
        "six",
        "seven",
        "seven",
        "eight",
        "eight",
        "nine",
        "nine",
*/
        "program",
        "program",
        "waitClock",
        "wait",
        "stopClock",
        "stop",
        };

        source[1] = new String[8];
        source[1][0] = "TVSetSub";
	source[1][1] = "TVRemote";
        source[1][2] = "toggle";
	source[1][3] = "toggle";
        source[1][4] = "up";
	source[1][5] = "up";
        source[1][6] = "down";
	source[1][7] = "down";

        source[2] = new String[] { "TVDeviceSub", "TVRemote",
        "device", "device", "run", "start", "up", "up", };

        delivery = 'q';
}

// Internal methods

public int dequeue() {
    System.out.println(" - dequeue - ");
    return 0;
}

public static void passSetIn(TVHandler tvh, TVSet tvs) {
  System.out.println("passSetIn");
}

public static void passSetOut(TVHandler tvh, TVSet tvs) {
  System.out.println("passSetOut");
}

public static void passDeviceIn(TVHandler tvh, TVDevice tvs) {
  System.out.println("passDeviceIn");
}

public static void passDeviceOut(TVHandler tvh, TVDevice tvs) {
  System.out.println("passDeviceOut");
}

}