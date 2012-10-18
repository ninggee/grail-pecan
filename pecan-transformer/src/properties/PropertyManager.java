package properties;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import edu.hkust.clap.transformer.TransformClass;

public class PropertyManager {

	public static boolean fullydynamic= true;
	
	public static boolean noCtxtForbug= false;
	
	public static Properties props ;
	static{
		props = loadProperties("/home/lpxz/eclipse/workspace/Dcon/properties");
	}
	
	public static String projectName = System.getProperty("projectname");	
	public static String preTransArg = props.getProperty(projectName+"_pretrans_arg"); //"/h
	public static String ClassPath = props.getProperty(projectName+"_trans_classpath"); //"/home/lpxz/eclipse/workspace/app/bin";////"/home/lpxz/eclipse/workspace/openjms/bin";
	public static String mainClass= props.getProperty(projectName+"_trans_mainClass"); //"benchmarks.dstest.MTLinkedListInfiniteLoop";//System.getProperty("mainclass");//"driver.OpenJMSDriver";
    public static String excludeString = props.getProperty(projectName+"_trans_excludelist");
    public static String includeString = props.getProperty(projectName+"_trans_includelist");
    public static String outputform =    props.getProperty(projectName+"_trans_outputform");
    public static boolean isOutputJimple=props.getProperty(projectName+"_trans_outputform").equals("J")||props.getProperty(projectName+"_trans_outputform").equals("jimple");
   
    //for Dcon
//	public static String origAnalyzedFolder = props.getProperty("classpath"); //"/home/lpxz/eclipse/workspace/app/bin";////"/home/lpxz/eclipse/workspace/openjms/bin";
//	public static String mainClass= props.getProperty("mainClass"); //"benchmarks.dstest.MTLinkedListInfiniteLoop";//System.getProperty("mainclass");//"driver.OpenJMSDriver";
//    public static String projectName = props.getProperty("projectname");
//    public static String outputform = props.getProperty("trans_outputform");
    
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
//		Properties props = loadProperties("/home/lpxz/eclipse/workspace/Dcon/properties");
//		System.out.println(props.getProperty("mainclass"));
		HashMap map  =  new HashMap();
		map.put(null, 2);
		System.out.println(map.get(null));
		

	}

	public static boolean addAtomIntent = true;
	public static boolean addIndicator = true;

	public static boolean useasmStack = true;
	public static String atomIntention_whitelist = "/home/lpxz/eclipse/workspace/pecan/pecan-transformer/tmp/atomintention_whitelist_" + projectName;
    
	public static String indicator_whitelist = "/home/lpxz/eclipse/workspace/pecan/pecan-transformer/tmp/indicator_whitelist_" + projectName;

	public static boolean prePhase = false;// apidesigner: true dcon: false
    public static void write2File(String filename, String content)
    {
    	try{
    		  // Create file 
    		  
    		  FileWriter fstream = new FileWriter(filename, true);
    		  BufferedWriter out = new BufferedWriter(fstream);
    		  out.write(content+"\n");
    		  //Close the output stream
    		  out.close();
    		  }catch (Exception e){//Catch exception if any
    		  System.err.println("Error: " + e.getMessage());
    		  }
     }
    
    public static List<String> readFromFile(String filename)
    {
          List<String> toret = new ArrayList<String>();
    	  try{
    	  // Open the file that is the first 
    	  // command line parameter
    	  FileInputStream fstream = new FileInputStream(filename);
    	  // Get the object of DataInputStream
    	  DataInputStream in = new DataInputStream(fstream);
    	  BufferedReader br = new BufferedReader(new InputStreamReader(in));
    	  String strLine;
    	  //Read File Line By Line
    	  while ((strLine = br.readLine()) != null)   {
    	  // Print the content on the console
    		  toret.add(strLine);
    	  }
    	  //Close the input stream
    	  in.close();
    	    }catch (Exception e){//Catch exception if any
    	  System.err.println("Error: " + e.getMessage());
    	  }
    	    return toret;
    }

	

	


}
