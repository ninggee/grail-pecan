package properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Properties;



public class PropertyManager {
     public static boolean proceed2Grail = false;
	public static boolean noCtxtForbug= true;
	
	public static Properties props ;
	static{
		props = loadProperties("/home/lpxz/eclipse/workspace/APIDesigner/properties");
	}
	
	//must set the project name, always
	public static String projectname = System.getProperty("projectname");
	
	
	
//	public static String origAnalyzedFolder = props.getProperty("origAnalyzedFolder"); //"/home/lpxz/eclipse/workspace/app/bin";////"/home/lpxz/eclipse/workspace/openjms/bin";
	public static String mainClassArg= props.getProperty(projectname+"_monitor_mainClassArg"); //"benchmarks.dstest.MTLinkedListInfiniteLoop";//System.getProperty("mainclass");//"driver.OpenJMSDriver";
    
	
    public static Properties loadProperties(String filename)
    {
    	Properties properties = new Properties();
    	try {
    	    properties.load(new FileInputStream(filename));
    	} catch (IOException e) {
    	}
    	return properties;
    }


	public static void main(String[] args) {		

	}


	public static boolean useasmStack = true;
    // ant cannot recognize the current project properly when being invoked in a different project.
	public static String SystemUserDir = "/home/lpxz/eclipse/workspace/pecan/pecan-monitor";
	
	public static boolean useContext4InterestingMethod= false;// try it
	public static boolean allAreInteresting = true;
	public static boolean saveAllPatterns = true;
	public static boolean reportTime = true;
	public static boolean lpxzonly = false;
	
	
	
	static HashSet<String> invokes= null;
	private static void loadInterestingInvokedMethods() {    
	 
	    // save to the project's name! in the folder InvokedMethods/
	
		String filename = "/home/lpxz/eclipse/workspace/APIDesigner/InvokedMethods/" + projectname +  "_indicator";
		ObjectInputStream in = null;
    	try
    	{
			
			FileInputStream fis = new FileInputStream(filename);
			in = new ObjectInputStream(fis);
			invokes = (HashSet<String>)in.readObject();
			
//			for(String invoke : invokes)
//			{
//				System.err.println(invoke);
//			}
    	}catch(Exception Exception){
    		System.out.println("dump error!");
    	}  
    	
	}




	public static boolean isInteresting(String sootmethodsig) {

         if(allAreInteresting)
         {
        	 return true;
         }else
         {
     		if(invokes==null)
     		{
     			loadInterestingInvokedMethods();
//     			for(String str: invokes)
//     				System.out.println(str);
     		}
     		
     		
     		
     	
     		return invokes.contains(sootmethodsig);// exactly the same format, no need to transform
         }
		
		
		

	}

}
