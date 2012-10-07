package edu.hkust.clap.random;

public class Pattern 
{
	private Status currentStatus;
	private PID ID;//pattern id
	MEMLOC firstMemloc;
	MEMLOC secondMemloc;
	long firstTid;
	long secondTid;
	TYPE[] types;
	long[] tids;
	MEMLOC[] mems;
	Pattern(PID pid)
	{
		ID = pid;
		currentStatus = Status.ENTRY;
		types = new TYPE[4];
		tids = new long[4];
		mems = new MEMLOC[4];
		for(int i=0;i<4;i++)
		{
			types[i] = TYPE.NULL;
			tids[i] = 0;
			mems[i] = MEMLOC.NULL;
		}
	}
	public String getAccess(int i)
	{
		return tids[i]+" "+types[i]+" "+mems[i];
	}
	public PID getId()
	{
		return ID;
	}
	public void reset()
	{
		currentStatus = Status.ENTRY;
	}
	public Status getStatus() {
		return currentStatus;
	}
	enum TYPE
	{
		READ,WRITE,NULL
	}
	enum MEMLOC
	{
		L1,L2,NULL
	}
	enum Status
	{
		ENTRY,
		A,
		B,
		C,
		CONFLICT
	}
	enum PID
	{
		ONE,TWO,THREE,FOUR,FIVE,SIX,SEVEN,EIGHT,NINE,TEN,ELEVEN,TWELVE,THIRTEEN,FOURTEEN
	}
	
	/**
	 * lid be either 0 or 1: l1 or l2
	 * type be either 0 or 1: READ OR WRITE
	 * this treatment is too strict and not necessarily reflect the fact of real pattern
	 * It may cause the access pattern extremely hard to manifest in certain cases
	 */
	public void updateStatus(MEMLOC memloc, TYPE type, long threadId) 
	{
		switch(ID)
		{
		case SIX:
			switch(currentStatus)
			{
				case ENTRY:
					if(type==TYPE.WRITE)
					{
						currentStatus=Status.A;
						firstTid=threadId;
						firstMemloc = memloc;
						types[0]= TYPE.WRITE;
						tids[0] = threadId;
						mems[0] = memloc;
					}
					break;
				case A:
					if(type==TYPE.WRITE&&memloc==firstMemloc&&threadId!=firstTid)
					{
						currentStatus=Status.B;
						secondTid=threadId;
						types[1]= TYPE.WRITE;
						tids[1] = threadId;
						mems[1] = memloc;
					}
					break;
				case B:
					if(type==TYPE.WRITE&&memloc!=firstMemloc&&threadId==secondTid)
					{
						currentStatus=Status.C;
						secondMemloc = memloc;
						types[2]= TYPE.WRITE;
						tids[2] = threadId;
						mems[2] = memloc;
					}
					break;
				case C:
					if(type==TYPE.WRITE&&memloc==secondMemloc&&threadId==firstTid)
					{
						currentStatus=Status.CONFLICT;
						types[3]= TYPE.WRITE;
						tids[3] = threadId;
						mems[3] = memloc;
					}
					break;
				case CONFLICT:
					break;
				default:
					break;
			}
			break;
		case SEVEN:
			switch(currentStatus)
			{
			case ENTRY:
				if(type==TYPE.WRITE)
				{
					currentStatus=Status.A;
					firstTid=threadId;
					firstMemloc = memloc;
					types[0]= TYPE.WRITE;
					tids[0] = threadId;
					mems[0] = memloc;
				}
				break;
			case A:
				if(type==TYPE.WRITE&&memloc!=firstMemloc&&threadId!=firstTid)
				{
					currentStatus=Status.B;
					secondTid=threadId;
					secondMemloc = memloc;
					types[1]= TYPE.WRITE;
					tids[1] = threadId;
					mems[1] = memloc;
				}
				break;
			case B:
				if(type==TYPE.WRITE&&memloc==firstMemloc&&threadId==secondTid)
				{
					currentStatus=Status.C;
					types[2]= TYPE.WRITE;
					tids[2] = threadId;
					mems[2] = memloc;
				}
				break;
			case C:
				if(type==TYPE.WRITE&&memloc==secondMemloc&&threadId==firstTid)
				{
					currentStatus=Status.CONFLICT;
					types[3]= TYPE.WRITE;
					tids[3] = threadId;
					mems[3] = memloc;
				}
				break;
			case CONFLICT:
				break;
			default:
				break;
			}
			break;
		case EIGHT:
			switch(currentStatus)
			{
			case ENTRY:
				if(type==TYPE.WRITE)
				{
					currentStatus=Status.A;
					firstTid=threadId;
					firstMemloc = memloc;
					types[0]= TYPE.WRITE;
					tids[0] = threadId;
					mems[0] = memloc;
				}
				break;
			case A:
				if(type==TYPE.WRITE&&memloc!=firstMemloc&&threadId!=firstTid)
				{
					currentStatus=Status.B;
					secondTid=threadId;
					secondMemloc = memloc;
					types[1]= TYPE.WRITE;
					tids[1] = threadId;
					mems[1] = memloc;
				}
				break;
			case B:
				if(type==TYPE.WRITE&&memloc==secondMemloc&&threadId==firstTid)
				{
					currentStatus=Status.C;
					types[2]= TYPE.WRITE;
					tids[2] = threadId;
					mems[2] = memloc;
				}
				break;
			case C:
				if(type==TYPE.WRITE&&memloc==firstMemloc&&threadId==secondTid)
				{
					currentStatus=Status.CONFLICT;
					types[3]= TYPE.WRITE;
					tids[3] = threadId;
					mems[3] = memloc;
				}
				break;
			case CONFLICT:
				break;
			default:
				break;
			}
			break;
		case NINE:
			switch(currentStatus)
			{
			case ENTRY:
				if(type==TYPE.WRITE)
				{
					currentStatus=Status.A;
					firstTid=threadId;
					firstMemloc = memloc;
					types[0]= TYPE.WRITE;
					tids[0] = threadId;
					mems[0] = memloc;
				}
				break;
			case A:
				if(type==TYPE.READ&&memloc==firstMemloc&&threadId!=firstTid)
				{
					currentStatus=Status.B;
					secondTid=threadId;
					types[1]= TYPE.READ;
					tids[1] = threadId;
					mems[1] = memloc;
				}
				break;
			case B:
				if(type==TYPE.READ&&memloc!=firstMemloc&&threadId==secondTid)
				{
					currentStatus=Status.C;
					secondMemloc = memloc;
					types[2]= TYPE.READ;
					tids[2] = threadId;
					mems[2] = memloc;
				}
				break;
			case C:
				if(type==TYPE.WRITE&&memloc==secondMemloc&&threadId==firstTid)
				{
					currentStatus=Status.CONFLICT;
					types[3]= TYPE.WRITE;
					tids[3] = threadId;
					mems[3] = memloc;
				}
				break;
			case CONFLICT:
				break;
			default:
				break;
			}
			break;
		case TEN:
			switch(currentStatus)
			{
			case ENTRY:
				if(type==TYPE.WRITE)
				{
					currentStatus=Status.A;
					firstTid=threadId;
					firstMemloc = memloc;
					types[0]= TYPE.WRITE;
					tids[0] = threadId;
					mems[0] = memloc;
				}
				break;
			case A:
				if(type==TYPE.READ&&memloc!=firstMemloc&&threadId!=firstTid)
				{
					currentStatus=Status.B;
					secondTid=threadId;
					secondMemloc = memloc;
					types[1]= TYPE.READ;
					tids[1] = threadId;
					mems[1] = memloc;
				}
				break;
			case B:
				if(type==TYPE.READ&&memloc==firstMemloc&&threadId==secondTid)
				{
					currentStatus=Status.C;
					types[2]= TYPE.READ;
					tids[2] = threadId;
					mems[2] = memloc;
				}
				break;
			case C:
				if(type==TYPE.WRITE&&memloc==secondMemloc&&threadId==firstTid)
				{
					currentStatus=Status.CONFLICT;
					types[3]= TYPE.WRITE;
					tids[3] = threadId;
					mems[3] = memloc;
				}
				break;
			case CONFLICT:
				break;
			default:
				break;
			}
			break;
		case ELEVEN:
			switch(currentStatus)
			{
			case ENTRY:
				if(type==TYPE.READ)
				{
					currentStatus=Status.A;
					firstTid=threadId;
					firstMemloc = memloc;
					types[0]= TYPE.READ;
					tids[0] = threadId;
					mems[0] = memloc;
				}
				break;
			case A:
				if(type==TYPE.WRITE&&memloc==firstMemloc&&threadId!=firstTid)
				{
					currentStatus=Status.B;
					secondTid=threadId;
					types[1]= TYPE.WRITE;
					tids[1] = threadId;
					mems[1] = memloc;
				}
				break;
			case B:
				if(type==TYPE.WRITE&&memloc!=firstMemloc&&threadId==secondTid)
				{
					currentStatus=Status.C;
					secondMemloc=memloc;
					types[2]= TYPE.WRITE;
					tids[2] = threadId;
					mems[2] = memloc;
				}
				break;
			case C:
				if(type==TYPE.READ&&memloc==secondMemloc&&threadId==firstTid)
				{
					currentStatus=Status.CONFLICT;
					types[3]= TYPE.READ;
					tids[3] = threadId;
					mems[3] = memloc;
				}
				break;
			case CONFLICT:
				break;
			default:
				break;
			}
			break;
		case TWELVE:
			switch(currentStatus)
			{
			case ENTRY:
				if(type==TYPE.READ)
				{
					currentStatus=Status.A;
					firstTid=threadId;
					firstMemloc = memloc;
					types[0]= TYPE.READ;
					tids[0] = threadId;
					mems[0] = memloc;
				}
				break;
			case A:
				if(type==TYPE.WRITE&&memloc!=firstMemloc&&threadId!=firstTid)
				{
					currentStatus=Status.B;
					secondTid=threadId;
					secondMemloc = memloc;
					types[1]= TYPE.WRITE;
					tids[1] = threadId;
					mems[1] = memloc;
				}
				break;
			case B:
				if(type==TYPE.WRITE&&memloc==firstMemloc&&threadId==secondTid)
				{
					currentStatus=Status.C;
					types[2]= TYPE.WRITE;
					tids[2] = threadId;
					mems[2] = memloc;
				}
				break;
			case C:
				if(type==TYPE.READ&&memloc==secondMemloc&&threadId==firstTid)
				{
					currentStatus=Status.CONFLICT;
					types[3]= TYPE.READ;
					tids[3] = threadId;
					mems[3] = memloc;
				}
				break;
			case CONFLICT:
				break;
			default:
				break;
			}
			break;
		case THIRTEEN:
			switch(currentStatus)
			{
			case ENTRY:
				if(type==TYPE.READ)
				{
					currentStatus=Status.A;
					firstTid=threadId;
					firstMemloc = memloc;
					types[0]= TYPE.READ;
					tids[0] = threadId;
					mems[0] = memloc;
				}
				break;
			case A:
				if(type==TYPE.WRITE&&memloc!=firstMemloc&&threadId!=firstTid)
				{
					currentStatus=Status.B;
					secondTid=threadId;
					secondMemloc = memloc;
					types[1]= TYPE.WRITE;
					tids[1] = threadId;
					mems[1] = memloc;
				}
				break;
			case B:
				if(type==TYPE.READ&&memloc==secondMemloc&&threadId==firstTid)
				{
					currentStatus=Status.C;
					types[2]= TYPE.READ;
					tids[2] = threadId;
					mems[2] = memloc;
				}
				break;
			case C:
				if(type==TYPE.WRITE&&memloc==firstMemloc&&threadId==secondTid)
				{
					currentStatus=Status.CONFLICT;
					types[3]= TYPE.WRITE;
					tids[3] = threadId;
					mems[3] = memloc;
				}
				break;
			case CONFLICT:
				break;
			default:
				break;
			}
			break;
		case FOURTEEN:
			switch(currentStatus)
			{
			case ENTRY:
				if(type==TYPE.WRITE)
				{
					currentStatus=Status.A;
					firstTid=threadId;
					firstMemloc = memloc;
					types[0]= TYPE.WRITE;
					tids[0] = threadId;
					mems[0] = memloc;
				}
				break;
			case A:
				if(type==TYPE.READ&&memloc!=firstMemloc&&threadId!=firstTid)
				{
					currentStatus=Status.B;
					secondTid=threadId;
					secondMemloc = memloc;
					types[1]= TYPE.READ;
					tids[1] = threadId;
					mems[1] = memloc;
				}
				break;
			case B:
				if(type==TYPE.WRITE&&memloc==secondMemloc&&threadId==firstTid)
				{
					currentStatus=Status.C;
					types[2]= TYPE.WRITE;
					tids[2] = threadId;
					mems[2] = memloc;
				}
				break;
			case C:
				if(type==TYPE.READ&&memloc==firstMemloc&&threadId==secondTid)
				{
					currentStatus=Status.CONFLICT;
					types[3]= TYPE.READ;
					tids[3] = threadId;
					mems[3] = memloc;
				}
				break;
			case CONFLICT:
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
	}
}
