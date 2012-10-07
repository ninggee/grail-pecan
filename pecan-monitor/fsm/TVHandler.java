/* TVHandler.java */

import fsm.*;
import java.util.*;

public class TVHandler extends FSM {

protected int volumeLevel = 0;
protected double factor = 1;
Vector channel = new Vector();
Vector incdec = new Vector();
int currentChannel = 1;
double clock = -1;
public boolean error = false;

static {

        specification = "TVHandler";
        fsm = "TVHandlerSub";

        start = "Off";
	stop = "";

	transition = new String [][] {
	 { "Off", "power", "On", "turnOn" },
	 { "On", "up", "On", "volumePlus" },
          { "Mute", "up", "On", "volumePlus", "!isError()" },
          { "Mute", "down", "On", "", "!isError()" },
          { "Mute", "up", "On", "displayError", "isError()" },
          { "Mute", "down", "Mute", "displayError", "isError()" },
	 { "On", "down", "On", "volumeMinus" },
	 { "On", "muting", "Mute", "volumeOff" },
          { "Mute", "muting", "On", "volumeOn" },
	 { "On", "power", "Off", "turnOff" },
          { "Mute", "power", "Off", "turnOff" },

          { "On", "program", "Programming", "displayMenu", "isClockSet()" },
          { "On", "program", "Select", "displaySelect", "!isClockSet()" },
          { "Select", "waitClock", "On", "waitClock" },
          { "Select", "stopClock", "On", "stopClock" },

          // { "On", "(enter (up|down) (up|down)*)",  "On", "enter:channelStart;up:channelUpDown(1);down:channelUpDown(-1);~premature:quitUD", "!isError()" },
          // { "On", "( (one|two|three|four|five|six|seven|eight|nine|zero) (one|two|three|four|five|six|seven|eight|nine|zero)* enter )",  "On", "~default:def;~normal:end;~premature:quit;enter:updateChannel" },
        };

}

	static final int one = 1;
	static final int two = 2;
	static final int three = 3;
	static final int four = 4;
	static final int five = 5;
	static final int six = 6;
	static final int seven = 7;
	static final int eight = 8;
	static final int nine = 9;
	static final int zero = 0;
	static final int enter = 10;

// Internal methods

// Property access methods

public double getFactor() {
	return factor;
}

public void setFactor(double d) {
	factor = d;
}

// Guards
public boolean isError() {
        System.out.println("TVHandler: isError?");
	return error;
}

public boolean isClockSet() {
        System.out.println("TVHandler: isClockSet?");
	return clock >= 0;
}

// Transition functions

public void volumeOn()
{
System.out.println("TVHandler: volumeOn");
}

public void volumeOff()
{
System.out.println("TVHandler: volumeOff");
}

public void volumePlus()
{
System.out.println("TVHandler: volumePlus");
}

public void volumeMinus()
{
System.out.println("TVHandler: volumeMinus");
}

public void turnOn()
{
System.out.println("TVHandler: turnOn");
}

public void turnOff()
{
System.out.println("TVHandler: turnOff");
}

public void displayError()
{
System.out.println("TVHandler: displayError");
}

public void displayMenu()
{
System.out.println("TVHandler: displayMenu");
}

public void displaySelect()
{
System.out.println("TVHandler: displaySelect");
}

public void waitClock()
{
System.out.println("TVHandler: waitClock");
}

public void stopClock()
{
System.out.println("TVHandler: stopClock");
}

public void channelUpDown(int n)
{
System.out.println("TVHandler: channelUpDown(" + n + ")");
}

public void channelStart()
{
System.out.println("TVHandler: channelStart");
}

public void def()
{
System.out.println("TVHandler: def");
}

public void end()
{
System.out.println("TVHandler: end");
}

public void quit()
{
System.out.println("TVHandler: quit");
}

public void endUD()
{
System.out.println("TVHandler: endUD");
}

public void quitUD()
{
System.out.println("TVHandler: quitUD");
}

public void updateChannel()
{
System.out.println("TVHandler: updateChannel");
}

}