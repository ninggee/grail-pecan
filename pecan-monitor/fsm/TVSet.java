/* TVSet.java */

import fsm.*;
import java.util.*;

public class TVSet extends FSM {

  private boolean bright = true;
  private boolean flag = false;

static {
        specification = "TVSet";
        fsm = "TVSetSub";

        start = "Start";
	stop = "Stop";

	transition = new String[][] {
	  { "Start", "toggle", "Brightness", "swap", "isBrightness()" },
          { "Start", "toggle", "Contrast", "swap", "!isBrightness()" },
          { "Brightness", "up", "Brightness", "incBrightness", "" },
          { "Brightness", "down", "Brightness", "incBrightness", "" },
          { "Contrast", "up", "Contrast", "incContrast", "" },
          { "Contrast", "down", "Contrast", "decContrast", "" },
	  { "Brightness", "toggle", "Contrast", "swap", "!already()" },
          { "Brightness", "toggle", "Stop", "swap", "already()" },
	  { "Contrast", "toggle", "Brightness", "swap", "!already()" },
          { "Contrast", "toggle", "Stop", "swap", "already()" },
	};

}

// Transition functions

protected void incBrightness()
{
	System.out.println("TVSet: incBrightness");
}

protected void incContrast()
{
	System.out.println("TVSet: incContrast");
}

protected void decBrightness()
{
	System.out.println("TVSet: decBrightness");
}

protected void decContrast()
{
	System.out.println("TVSet: decContrast");
}

protected void swap()
{
        flag = true;
        if ( bright )
          bright = false;
        else
          bright = true;
	System.out.println("TVSet: swap");
}

// Guards

public boolean isBrightness() {
        System.out.println("TVSet: isBrightness?");
	return bright;
}

public boolean already() {
        System.out.println("TVSet: already?");
	return flag;
}

}