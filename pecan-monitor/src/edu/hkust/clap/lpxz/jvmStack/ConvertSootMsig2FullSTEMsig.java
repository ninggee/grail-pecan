package edu.hkust.clap.lpxz.jvmStack;

import soot.Scene;
import soot.SootMethod;

public class ConvertSootMsig2FullSTEMsig {


	// make everything explicit. 
	//soot msig format:   <Simple$1: int identity(int)>
	// FullSTE msig format: identity(int) 
	// as seen, soot format contains the return type and class info, but it is not necessary for recognizing the sootMethod (we already have the class info).
	
	public static String convertSootMethod(SootMethod sm)
	{
		    String sootsig = sm.getSignature();
		    
			int lastLeftBrace  = sootsig.lastIndexOf('(');
			int lastrightBrace = sootsig.lastIndexOf(')');
			String sootarglist  = sootsig.substring(lastLeftBrace+1, lastrightBrace);
			
			String methodname = sm.getName();
			String className = sm.getDeclaringClass().getName();
			
			return  methodname + "(" + sootarglist + ")"; //className + "."+				
	}
	
	public static String convertSootMsig(String msig)
	{
		// when invoking, make sure the scene.v() already loads necessary classes
		 SootMethod sootMethod = Scene.v().getMethod(msig);
		 return convertSootMethod(sootMethod);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
