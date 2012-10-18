package edu.hkust.clap;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;

import properties.PropertyManager;

import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.jimple.parser.node.TMinus;


public class Util{

//	static String[] unInstruClasses = {
//			"jrockit.",
//			"java.",
//			"javax.",
//			"xjava.",
//			"COM.",
//			"com.",
//			"cryptix.",
//			"sun.",
//			"sunw.",
//			"junit.",
//			"org.junit.",
//			"org.xmlpull.",
//			"edu.hkust.clap."
//			
//	};
	// manually add the command line options here
	 static String[] unInstruClasses = {
		    "org.apache.log4j.",
			"jrockit.",
			"java.",
			"javax.",
			"xjava.",
			"COM.",
			"com.",
			"cryptix.",
			"sun.",
			"sunw.",
			"junit.",
			"org.junit.",
			"org.xmlpull.",
			"edu.hkust.clap.",
			// the following are copied frpm the transforming options. (commandLine)
			"org.apache.commons.logging.",// annoying, ban it
			"org.apache.xalan.",
			"org.apache.xpath.",
			"org.springframework.",
			"org.jboss.",
			"jrockit.",
			"edu.",				
			"checkers.",
			"org.codehaus.spice.jndikit.",
			"EDU.oswego.cs.dl.util.concurrent.WaiterPreferenceSemaphore",
			"soot.",
			"aTSE.",
			"pldi.",
			"popl.", 
			"beaver.",
			"org.jgrapht",
			"ca.pfv.spmf.","japa.parser.","polyglot."
			
			// org.w3c. is the including option			
	};

	public static String makeArgumentName(int argOrder) {
		if (argOrder == 0) {
			return "this";
		}

		return "arg_" + argOrder;
	}


	public static String transClassNameDotToSlash(String name) {
		return name.replace('.', '/');
	}

	public static String transClassNameSlashToDot(String name) {
		return name.replace('/', '.');
	}
    //static String userdir = Parameters.//System.getProperty("userdir");
	public static String getPureTmpDirectory() 
	{
		//String tempdir = System.getProperty("user.dir");
		String tempdir =Parameters.userdir;// "";
		if (!(tempdir.endsWith("/") || tempdir.endsWith("\\"))) {
			tempdir = tempdir + System.getProperty("file.separator");
		}
		tempdir = tempdir+"tmp";
		
//		if(Parameters.isOutputJimple)
//			tempdir = tempdir+Parameters.OUTPUT_JIMPLE;
		
		
		
		File tempFile = new File(tempdir);
		if(!(tempFile.exists()))
			tempFile.mkdir();
			

		return tempdir;
	}
	public static String getTmpDirectory() 
	{
		String tempdir =Parameters.userdir;// System.getProperty("user.dir");
		//System.out.println("usr.dir:" + tempdir);
		if (!(tempdir.endsWith("/") || tempdir.endsWith("\\"))) {
			tempdir = tempdir + System.getProperty("file.separator");
		}
		tempdir = tempdir+"tmp"+System.getProperty("file.separator");
		
		if(PropertyManager.isOutputJimple)
			tempdir = tempdir+Parameters.OUTPUT_JIMPLE+System.getProperty("file.separator");
		
		if(Parameters.isRuntime)
			tempdir = tempdir+Parameters.PHASE_RECORD;
		else if(Parameters.isReplay)
			tempdir = tempdir+Parameters.PHASE_REPLAY;
		else {
			tempdir = tempdir+Parameters.PHASE_PRE;
		}
		
		File tempFile = new File(tempdir);
		if(!(tempFile.exists()))
		{
			tempFile.mkdir();
		
		}
			
			

		return tempdir;
	}
	
	public static String getPreTmpDirectory() 
	{
		String tempdir = Parameters.userdir;//System.getProperty("user.dir");
		if (!(tempdir.endsWith("/") || tempdir.endsWith("\\"))) {
			tempdir = tempdir + System.getProperty("file.separator");
		}
		tempdir = tempdir+"tmp"+System.getProperty("file.separator");
		
		if(PropertyManager.isOutputJimple)
			tempdir = tempdir+Parameters.OUTPUT_JIMPLE+System.getProperty("file.separator");
		
	
			tempdir = tempdir+Parameters.PHASE_PRE;
		
		
		File tempFile = new File(tempdir);
		if(!(tempFile.exists()))
			tempFile.mkdir();
			
		
		return tempdir;
	}
	
    public static boolean isRunnableSubType(SootClass c) {
        if (c.implementsInterface("java.lang.Runnable"))
            return true;
        if (c.hasSuperclass())
            return isRunnableSubType(c.getSuperclass());
        return false;
    }
    public static boolean shouldInstruThisClass(String scname)
    {
		for(int k=0;k<unInstruClasses.length;k++)
		{
			if(scname.startsWith(unInstruClasses[k]))
			{
				return false;
			}
		}
		
		return true;
	}
    public static boolean shouldInstruThisMethod(String smname)
	{    	   	
		if (smname.contains("<clinit>")
			)
    	{
    		return false;
    	}
   		
		return true;
	}
    public static void resetParameters()
    {
  		 Parameters.isMethodRunnable = false;
  		 Parameters.isMethodMain = false;
  		 Parameters.isMethodSynchronized = false;
  		 Parameters.isMethodStaticNonPara = false;
  		
  		 Parameters.isMethodNonPrivate = true;
    }
	public static boolean instruThisType(Type type) 
	{
		if(type instanceof RefType)
		{
			if(Util.shouldInstruThisClass(type.toString()))
				//return true;
				return false;
		}
		return false;
	}
	public static String getAncestorClassName(SootClass sc1)
	{			
		SootClass sc2 = sc1.getSuperclass();
		while(shouldInstruThisClass(sc2.getName()))
		{
			sc1 = sc2;
			sc2 = sc1.getSuperclass();
		}
		return sc1.getName();
	}
    public static void storeObject(Object o, ObjectOutputStream out)
    {
    	try
    	{
    		out.writeObject(o);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
    	{
    		try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    public static String fullnameOfMethod(SootMethod sm)
    {
    	return sm.getDeclaringClass().getName() + "." + sm.getName();
    	
    }
}
