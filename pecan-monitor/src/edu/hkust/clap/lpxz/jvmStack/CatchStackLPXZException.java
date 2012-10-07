package edu.hkust.clap.lpxz.jvmStack;

public class CatchStackLPXZException extends Exception {

	private String desc;
	
	public CatchStackLPXZException(String desc)
	{
		this.desc = desc;
	}
	
	public String getDesc()
	{
		return this.desc;
	}
}
