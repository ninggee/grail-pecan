package edu.hkust.clap.random;

import edu.hkust.clap.random.Pattern.MEMLOC;
import edu.hkust.clap.random.Pattern.TYPE;

public class AtomicSet {
	int acsnum;
	Pattern six,seven,eight,nine,ten,eleven,twelve,thirteen,fourteen;
	AtomicSet()
	{
		six = new Pattern(Pattern.PID.SIX);
		seven = new Pattern(Pattern.PID.SEVEN);
		eight = new Pattern(Pattern.PID.EIGHT);
		nine = new Pattern(Pattern.PID.NINE);
		ten = new Pattern(Pattern.PID.TEN);
		eleven = new Pattern(Pattern.PID.ELEVEN);
		twelve = new Pattern(Pattern.PID.TWELVE);
		thirteen = new Pattern(Pattern.PID.THIRTEEN);
		fourteen = new Pattern(Pattern.PID.FOURTEEN);
	}
	
	public void proceed(int id, int tp, long threadId) {
		acsnum++;
		if(acsnum==1000000)
		{
			six.reset();
			seven.reset();
			eight.reset();
			nine.reset();
			ten.reset();
			eleven.reset();
			twelve.reset();
			thirteen.reset();
			fourteen.reset();
			
			acsnum=0;
		}
		//14 TYPES OF PROBLEMATIC ACCESS PATTERN
		//UNIMOD IS A TOOL FOR FSM
		//COME ON, ANY TWO THREADS CAN CONTRIBUTE A PATTERN
		//SO MANY!!!
		//we first consider the NINE patterns involve two variables
		MEMLOC memloc;
		TYPE type;
		if(id==0)
			memloc = MEMLOC.L1;
		else
			memloc = MEMLOC.L2;
		if(tp==0)
			type = TYPE.READ;
		else
			type = TYPE.WRITE;
		
		System.err.println(threadId+" "+type+" "+memloc);
		six.updateStatus(memloc,type,threadId);
		seven.updateStatus(memloc,type,threadId);
		eight.updateStatus(memloc,type,threadId);
		nine.updateStatus(memloc,type,threadId);
		ten.updateStatus(memloc,type,threadId);
		eleven.updateStatus(memloc,type,threadId);
		twelve.updateStatus(memloc,type,threadId);
		thirteen.updateStatus(memloc,type,threadId);
		fourteen.updateStatus(memloc,type,threadId);
		
	}
	public int getAcsNum() {
		return acsnum;
	}
	public boolean patternMatched()
	{
		if(six.getStatus()==Pattern.Status.CONFLICT
				||seven.getStatus()==Pattern.Status.CONFLICT
				||eight.getStatus()==Pattern.Status.CONFLICT
				||nine.getStatus()==Pattern.Status.CONFLICT
				||ten.getStatus()==Pattern.Status.CONFLICT
				||eleven.getStatus()==Pattern.Status.CONFLICT
				||twelve.getStatus()==Pattern.Status.CONFLICT
				||thirteen.getStatus()==Pattern.Status.CONFLICT
				||fourteen.getStatus()==Pattern.Status.CONFLICT)
			return true;
		else
			return false;
	}

	public Pattern getMatchedPattern() 
	{
		if(six.getStatus()==Pattern.Status.CONFLICT)
		{
			return six;
		}else if(seven.getStatus()==Pattern.Status.CONFLICT)
		{
			return seven;
		}else if(eight.getStatus()==Pattern.Status.CONFLICT)
		{
			return eight;
		}else if(nine.getStatus()==Pattern.Status.CONFLICT)
		{
			return nine;
		}else if(ten.getStatus()==Pattern.Status.CONFLICT)
		{
			return ten;
		}else if(eleven.getStatus()==Pattern.Status.CONFLICT)
		{
			return eleven;
		}else if(twelve.getStatus()==Pattern.Status.CONFLICT)
		{
			return twelve;
		}else if(thirteen.getStatus()==Pattern.Status.CONFLICT)
		{
			return thirteen;
		}else if(fourteen.getStatus()==Pattern.Status.CONFLICT)
		{
			return fourteen;
		}
		else
			return null;
	}
}
