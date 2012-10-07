package edu.hkust.clap.lpxz.jvmStack;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;



public class FullSTEManipulater {
     public static String tryRemoveLineSpec(String steString)// remove the line for the last one, it is useless, others' lines
     {
    	 if(steString.indexOf(':')==-1)
    		 return steString;// does not contain line NO at all
    	 else {
			int maohao = steString.indexOf(':');
			
			return steString.substring(0, maohao) + ")";// not append the line no
		}
     }
     
     public static int getInvokeLine(String steString)
     {
    	 if(steString.indexOf(':')==-1)
    		 throw  new RuntimeException("no line no available");
    	 else {
			int maohao = steString.indexOf(':');
			int rightBrace = steString.lastIndexOf(')');
			String lineString  = steString.substring(maohao + 1, rightBrace);			
			return Integer.parseInt(lineString);
		}
     }
     
     public static String getPartInLastBrace(String steString)
     {
    	 int  lastleftBrace = steString.lastIndexOf('(');
    	 int lastrightBrace = steString.lastIndexOf(')');
    	 return steString.substring(lastleftBrace+1, lastrightBrace);
     }     
     public static String getClass(String ste)
     {
    	
    	 int leftBrace = ste.indexOf('(');
    	 String cm = ste.substring(0, leftBrace);
    	 int  lastDotInCm= cm.lastIndexOf('.');
    	 return  cm.substring(0, lastDotInCm);  	 
    	 
     }
     public static String getMethod(String ste)
    {
    	 
    	 int leftBrace = ste.indexOf('(');
    	 String cm = ste.substring(0, leftBrace);
    	 int  lastDotInCm= cm.lastIndexOf('.');
    	 return  cm.substring(lastDotInCm +1);
   	 }
     
     public static String getMethodSig(String ste )
     {
    	int leftbrace = ste.indexOf('(');
    	int  rightbrace  = ste.indexOf(')');
    	String arg = ste.substring(leftbrace+1, rightbrace);
    	
    	return getMethod(ste) + "(" +  arg + ")";
    	
    	
    	 
     }
     
     public static String getMethodFromSig(String sig)
     {
    	 int leftBrace = sig.indexOf('(');
    	 return sig.substring(0, leftBrace);
     }
     
//     CatchStackLPXZ.getFullStackTrace_lpxz()(CatchStackLPXZ.java:117)
//     MainThread.test(int,int)(MainThread.java:60)
//     MainThread.main(java.lang.String[])(MainThread.java:39)

     public static void main(String[] args)
     {
    	 String sootsig  = "<spec.jbb.JBButil: void setLog(java.util.logging.Logger)>";
    	 
    	 System.out.println(getMethName_sootsig(sootsig));
    	 System.out.println(getArgList_sootsig(sootsig));
//    		try {
//    			
//    			
//    			
//    			File file = new File("outmain.txt");
//    			if(!file.exists()) file.createNewFile();// CAN NOT BE REMOVED!! IF REMOVED, THE DATAINPUTSTREAM WILL THROW EXCEPTION, MAKING CATCHSTACKEXCEPTION NOT HAPPEN ANY MORE.
//    		    FileInputStream fis = null;
//    		    BufferedInputStream bis = null;
//    		    DataInputStream dis =null;
//    		    
//
//    			
//    			
//    		fis = new FileInputStream(file);
//    		// Here BufferedInputStream is added for fast reading.
//    	      bis = new BufferedInputStream(fis);
//    	      dis = new DataInputStream(bis);
//    	      
//    			try {
//    				throw new CatchStackLPXZException("User Defined Exception");
//    			} catch (Exception e) {
//    				
//    			}	
//    	      
//    		     
//
//    			
//    			
//    	      while (dis.available() != 0) {
//
//    	      // this statement reads the line from the file and print it to
//    	        // the console.
//    	    	  String readline = dis.readLine();
//    	    	  if(!readline.isEmpty()) // avoid  the blank line
//    	    	  {
//    	    		 // analyze readline
//    	    		  System.out.println(getInvokeLine(readline));
//    	    		  System.out.println(getClass(readline));
//    	    		  System.out.println(getMethod(readline));
//    	    		  System.out.println(getMethodSig(readline));
//    	    		  System.out.println("==========");
//    	    	  }
//    	       }
//    		}catch (Exception e) {
//			   e.printStackTrace();
//			}
    			
    	      
    	 
     }
     
     //============================
    // <spec.jbb.JBButil: void setLog(java.util.logging.Logger)>
     
     public static String getClassName_sootsig(String sootsig)
     {
    	 
    	 int lbrace =sootsig.indexOf('<');
    	 int maohao = sootsig.indexOf(':');
    	 return sootsig.substring(lbrace +1, maohao);
     }
     
     public static String getMethName_sootsig(String sootsig)
     {
    	 ;
          int  lastleftBrace = sootsig.lastIndexOf('(');
          String beforeArg = sootsig.substring(0, lastleftBrace);
          int  lastBlank = beforeArg.lastIndexOf(' ');
          return beforeArg.substring(lastBlank+1);
     }
     
     public static String getArgList_sootsig(String sootsig)
     {
    	 int lastleftBrace =sootsig.lastIndexOf('(');
    	 int lastrightBrace = sootsig.lastIndexOf(')');
    	 return sootsig.substring(lastleftBrace +1, lastrightBrace);
     }
     
     public static String getFullMethodArg_sootsig(String sootsig)
     {
    	 return getClassName_sootsig(sootsig) + "." + getMethName_sootsig(sootsig) + "(" + getArgList_sootsig(sootsig)+ ")";
     }
     
     

}
