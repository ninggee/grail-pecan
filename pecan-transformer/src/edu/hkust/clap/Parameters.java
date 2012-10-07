package edu.hkust.clap;

import properties.PropertyManager;

public class Parameters {

	public static final String UTIL_CLASSNAME = "edu.hkust.clap.Util";
	public static final String CRASH_ANNOTATION="Crashed_with";
	public static final String CARE_ANNOTATION="Cared_by_CLAP_";
	
	public static final String CATCH_EXCEPTION_SIG = "<edu.hkust.clap.monitor.Monitor: void crashed(java.lang.Throwable)>";
	public static final String CARED_BY_CLAP_SIG = "<edu.hkust.clap.monitor.Monitor: void caredByClap(java.lang.String,java.lang.Object)>";

	//   l3 = l0.<org.exolab.jms.net.connector.ManagedConnectionHandle: int _connectionCount>;
	public static String OUTPUT_JIMPLE ="jimple";
	public static String PHASE_PRE="pre";
	public static String PHASE_RECORD ="runtime";
	public static String PHASE_REPLAY ="replay";
		
	public static boolean isMethodRunnable = false;
	public static boolean isMethodMain = false;
	public static boolean isMethodSynchronized = false;
	public static boolean isInsideConstructor = false;

	public static String ClassPath =PropertyManager.ClassPath;//"/home/lpxz/eclipse/workspace/app/bin";
	
	
	public static boolean isRuntime=true;
	public static boolean isReplay=false;
	public static boolean isOutputJimple=PropertyManager.outputform.equals("J")||PropertyManager.outputform.equals("jimple");
	public static boolean isMethodNonPrivate = true;
	public static boolean isMethodStaticNonPara = false;
	public static String userdir ="/home/lpxz/eclipse/workspace/pecan/pecan-transformer";
}
