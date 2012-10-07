package edu.hkust.clap;

public class Parameters {
	public static int LOOP_STMT_COUNT = 5;
	public static int RAND_EXCEPTION_COUNT = 1000;
	
	public static long RAND_SEED=0;
	public static int ACS_TYPE_RATE = 1;
	
	public static final String UTIL_CLASSNAME = "edu.hkust.clap.Util";
	public static final String CRASH_ANNOTATION="Crashed_with";
	public static final String CATCH_EXCEPTION_SIG = "<edu.hkust.clap.monitor.Monitor: void crashed(java.lang.Throwable)>";
	
	public static int CLONE_DEPTH = 3;
	public static int INST_MIN_COUNT = 10;
	public static String PHASE_RECORD ="runtime";
	public static String PHASE_REPLAY ="replay";
	public static String TEMP_DIR ="tmp";
	public static boolean shouldInstru = false;
	public static boolean instruStaticData = true;
	public static boolean instruStaticMethod = true;
	
	public static boolean isMethodPublic=false;
	public static boolean isMethodStatic = false;
	public static boolean isMethodRunnable = false;
	public static boolean isMethodSynchronized = false;
	public static boolean isMethodMain = false;
	
	public static boolean isRuntime=true;
	public static boolean isReplay=false;
	
	public static boolean isInnerClass = false;
	public static boolean isAnonymous = false;
	public static boolean isStmtInLoop = false;
	
	public static boolean injectRandomException = false;
	public static boolean isCrashed = false;
	public static int lockCount=0;
}
