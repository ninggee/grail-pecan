package edu.hkust.clap.transformer;

import edu.hkust.clap.Parameters;
import edu.hkust.clap.Util;
import edu.hkust.clap.transformer.phase0.PreTransformer;
import edu.hkust.clap.transformer.phase1.WholeProgramTransformer;
import edu.hkust.clap.transformer.phase1.TransformerForInstrumentation;
import edu.hkust.clap.transformer.phase2.JTPTransformer;



import soot.*;
import soot.jimple.spark.SparkTransformer;

import soot.options.Options;


import java.io.*;

import java.util.*;
import java.util.zip.GZIPOutputStream;

import properties.PropertyManager;


public class TransformClass {
    
private static void cp_Dir1Star_Dir2(File sourceLocation, File targetLocation) throws Exception {
//	File  sourceLocation = new File(source);
//	File targetLocation = new File(target);
    //System.out.println("copying " + sourceLocation.getAbsolutePath() + "/* to " + targetLocation.getAbsolutePath());
	        if (sourceLocation.isDirectory()) {
	            if (!targetLocation.exists()) {
	                targetLocation.mkdir();
	            }
	            
	            String[] children = sourceLocation.list();
	            for (int i=0; i<children.length; i++) {
	            	cp_Dir1Star_Dir2(new File(sourceLocation, children[i]),
	                        new File(targetLocation, children[i]));
	            }
	        } else {
	            
	            InputStream in = new FileInputStream(sourceLocation);
	            OutputStream out = new FileOutputStream(targetLocation);
	            
	            // Copy the bits from instream to outstream
	            byte[] buf = new byte[1024];
	            int len;
	            while ((len = in.read(buf)) > 0) {
	                out.write(buf, 0, len);
	            }
	            out.flush();
	            in.close();
	            out.close();
	        }
	    
//	    System.out.println("copying " + string + "/* to " + string2);
//		String cmd = "ls " + string;
//		Runtime run = Runtime.getRuntime();
//		Process pr = null;
//		try {
//		pr = run.exec(cmd);
//		} catch (IOException e) {
//		e.printStackTrace();
//		}
//		try {
//		pr.waitFor();
//		} catch (InterruptedException e) {
//		e.printStackTrace();
//		}
//		BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
//		String line = "";
//		try {
//		while ((line=buf.readLine())!=null) {
//		
//			String cmd2 = "cp -r "+string+ "/"+ line + " "+ string2;
//			
//			Runtime run2 = Runtime.getRuntime();
//			Process pr2 = null;
//			try {
//			pr2 = run2.exec(cmd2);
//			} catch (IOException e2) {
//			e2.printStackTrace();
//			}
//			try {
//			pr2.waitFor();
//			} catch (InterruptedException e3) {
//			e3.printStackTrace();
//			}
//		}
//		} catch (IOException e) {
//		e.printStackTrace();
//		}

		
	}

// no preprocessing, it brings too much differences in the jimple encoding. , and a lot of troubles.
// maybe some classes are only produced in the preprocesssing, which lead to the wrong jimple encoding (double processing leads to it).
// We have an alternative approach to generate the classes in a postprocessing, which lead to no wrong encoding (We can remember and prune the procecssed classes). 
// now, it preserves all the jimple variables name correctly, CHECKED


public void processAllAtOnce(String[] args, Visitor visitor) throws Exception {
	
	TransformerForInstrumentation.v().setVisitor(visitor);

    // arg is not mainclass any mroe
	// it is the project name

	String mainclass = PropertyManager.mainClass;

    //String analyzedFolder = PropertyManager.origAnalyzedFolder;//"/home/lpxz/eclipse/workspace/openjms/bin";

	
//	String runtimeDir = Util.getPureTmpDirectory() + "/" + Parameters.PHASE_RECORD + "/" +PropertyManager.projectName ;
//	String replayDir =  Util.getPureTmpDirectory() + "/" + Parameters.PHASE_REPLAY;    	
	
	if(PropertyManager.prePhase)
	{
		transformPreVersion(mainclass); 	
		transformRuntimeVersion_pre(mainclass); 
		saveTransformResults(mainclass);
	}
	else {
		System.out.print("do not call the pre phase");
		transformRuntimeVersion(mainclass); 
		saveTransformResults(mainclass);
	}

	

}
//    public void processAllAtOnce(String[] args, Visitor visitor) throws Exception {
//    	
//    	TransformerForInstrumentation.v().setVisitor(visitor);
//    	//Parameters.isOutputJimple = true;
//    	String mainclass = args[0];  	
//    	
//    	//
//    	//cp openjms/bin-> openjms/bin_bak
//    	System.err.println("0 make sure the disk is writeable, 1 remember to set the analyzedFolder!@! in Parameters.java, used in processAllAtonece");
//    	//System.err.println("2 remember to generate the bin_bak folder in the remote project too! otherwise, the copyto will generate a file in stead of a folder");
//    	String analyzedFolder = Parameters.analyzedFolder;//"/home/lpxz/eclipse/workspace/openjms/bin";
//    	///home/lpxz/eclipse/workspace/simple
//    	//String analyzedFolder = "/home/lpxz/eclipse/workspace/simple/bin";
//    	String analyzedBak = analyzedFolder + "_bak";    
//    	System.err.println("copying from " + analyzedFolder + " to " + analyzedBak);
//    	cp_Dir1Star_Dir2(new File(analyzedFolder), new File(analyzedBak));// auto-create if not present
//    
//    	transformPreVersion(mainclass, analyzedFolder);// pre
//    	
//    	
//    	
//    	// tmp/pre--copy--> openjms/bin, tmp/runtime, tmp/replay
//    	String outputDir = Util.getPreTmpDirectory();
//    	String runtimeDir = Util.getPureTmpDirectory() + "/" + Parameters.PHASE_RECORD;
//    	String replayDir =  Util.getPureTmpDirectory() + "/" + Parameters.PHASE_REPLAY;
//    	System.err.println("copying from " + outputDir + " to " + analyzedFolder);
//    	System.err.println("copying from " + outputDir + " to " + runtimeDir);
//    	System.err.println("copying from " + outputDir + " to " + replayDir);
//    	cp_Dir1Star_Dir2(new File(outputDir), new File(analyzedFolder));
//    	cp_Dir1Star_Dir2(new File(outputDir), new File(runtimeDir));
//    	cp_Dir1Star_Dir2(new File(outputDir), new File(replayDir));
//         	
//    	
//    	transformRuntimeVersion(mainclass);    	
//    	transformReplayVersion(mainclass);
//    	
//    	
//    	// rm -rf openjms/bin
//    	rmDir(analyzedFolder);
//     	System.err.println("copying from " + analyzedBak + " to " + analyzedFolder);
//    	cp_Dir1Star_Dir2(new File(analyzedBak), new File(analyzedFolder));// auto-create
//    	
//    	saveTransformResults(mainclass);
//    }

	private void rmDir(String analyzedFolder) {
		 System.out.println("deleting... " + analyzedFolder);
		// System.out.println("1 to delete, 0 to skip the operation");

	      //  open up standard input
	      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	      // jsut do it
//	      String userName = null;
//
//	      //  read the username from the command-line; need to use try/catch with the
//	      //  readLine() method
//	      try {
//	         userName = br.readLine();
//	      } catch (IOException ioe) {
//               ioe.printStackTrace();
//	      }
//          
//          if(userName==null || userName.contains("0"))
//          {
//        	  return;
//          }
		
		
		
		
			String cmd = "rm -rf " + analyzedFolder;
			Runtime run = Runtime.getRuntime();
			Process pr = null;
			try {
			pr = run.exec(cmd);
			} catch (IOException e) {
			e.printStackTrace();
			}
			try {
			pr.waitFor();
			} catch (InterruptedException e) {
			e.printStackTrace();
			}		
	}

	private void mkDir(String analyzedFolder) {


		File f = new File(analyzedFolder);
		try{
		if(f.mkdir())
		System.out.println("Directory " + analyzedFolder +" Created");
		else
		System.out.println("Directory is not created");
		}catch(Exception e){
		e.printStackTrace();
		} 
//		 System.out.println("mkdir " + analyzedFolder);
//			String cmd = "mkdir " + analyzedFolder;
//			Runtime run = Runtime.getRuntime();
//			Process pr = null;
//			try {
//			pr = run.exec(cmd);
//			} catch (IOException e) {
//			e.printStackTrace();
//			}
//			try {
//			pr.waitFor();
//			} catch (InterruptedException e) {
//			e.printStackTrace();
//			}
//		
		
	
		
	}

	private void saveTransformResults(String mainclass) 
	{

    	System.err.println("*** *** *** *** *** *** *** *** *** *** ");
    	System.err.println("*** Total access num: "+ Visitor.totalaccessnum);
    	System.err.println("*** Instrumented SPE access num: "+ Visitor.instrusharedaccessnum);
    	
    	HashMap<Integer,String> indexSPEMap = new HashMap<Integer,String>();
    	
		Iterator<Value> speSetIt = Visitor.speIndexMap.keySet().iterator();
		System.err.println(" ");
    	System.err.println("*** SPE size: "+ Visitor.speIndexMap.size()); 
		System.err.println("*** *** *** *** *** *** *** *** *** *** ");
		System.err.println("*** SPE name: ");
		while(speSetIt.hasNext())
		{
			Value spe = speSetIt.next();
			String spe_str = spe.toString();
			Integer index = Visitor.speIndexMap.get(spe);
			indexSPEMap.put(index, spe_str);
			System.err.println("*** "+spe_str);
		}
		
		
		try 
		{
			File file_IndexSPEMap = new File("tmp"+System.getProperty("file.separator")+"spe."+mainclass+".gz");
			ObjectOutputStream fout_IndexSPEMap = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file_IndexSPEMap)));
	    	Util.storeObject(indexSPEMap,fout_IndexSPEMap);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
    	
		Iterator<String> methodSetIt = Visitor.methodIndexMap.keySet().iterator();
		System.err.println(" ");
		System.err.println("*** Method size: "+ Visitor.methodIndexMap.size());
		System.err.println("*** *** *** *** *** *** *** *** *** *** ");
		System.err.println("*** Method name: ");
		while(methodSetIt.hasNext())
		{
			String methodname = methodSetIt.next();
			Integer index = Visitor.methodIndexMap.get(methodname);
			System.err.println("*** "+methodname);
		}
		
		try 
		{
			File file_MethodIndexMap = new File("tmp"+System.getProperty("file.separator")+"method."+mainclass+".gz");
			ObjectOutputStream fout_MethodIndexMap = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file_MethodIndexMap)));
	    	Util.storeObject(Visitor.methodIndexMap,fout_MethodIndexMap);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	
	private void transformPreVersion(String mainclass) 
	{	
		Parameters.isRuntime = false;
		Parameters.isReplay = false;
		setPreOptions(mainclass);

//        // redirect the output to the Pre
		String path = Util.getPureTmpDirectory() + "/" + Parameters.PHASE_PRE + "/" +PropertyManager.projectName ;//Util.getTmpDirectory();//.replace("\\", "\\\\")
		System.err.println("transform to folder:" + path);
		String argString = PropertyManager.preTransArg + " -d "+path;// process-dir, no missing of classes!
		//excludeArgString + includeArgString;

		if(Parameters.isOutputJimple)
		{
			soot.Main.main(("-f jimple "+argString).split(" "));//"-f","jimple",c"-x","javato.","-x","edu." \\sootOutput "-process-dir", processDir
		}
		else
		{
			soot.Main.main(( argString).split(" "));//"-f","jimple",c"-x","javato.","-x","edu." \\sootOutput "-process-dir", processDir
		}
		
		soot.G.reset();
		System.err.println("***** Pre version generated *****");
	}
	
	//as a reference
	//"-x","jrockit.","-x","edu.","-x","com.","-x","checkers.","-x","org.xmlpull.","-x","org.apache.xml.","-x","org.apache.xpath.";
	//"-i","org.apache.log4j",
	//"-x","org.apache.xalan.",
	//"-x","org.apache.xpath.",
	//"-i","org.apache.derby.",
	//"-i","org.w3c.",
	//"-x","org.springframework.","-x","org.jboss.","-x","jrockit.","-x","edu.","-x","com.","-x","javax.","-x","checkers.","-x","org.xmlpull."};
    
	private void transformRuntimeVersion_pre(String mainclass) 
	{	
		Parameters.isRuntime = true;
		Parameters.isReplay = false;
		setRecordOptions_pre(mainclass);
		
		
		 String excludeArgString = "";   
		 String includeArgString = "";    
		 
		 if(PropertyManager.excludeString!=null && PropertyManager.excludeString.length()!=0)
		 {
			 String[] excludes =PropertyManager.excludeString.split(" ");
			 for(String excludeItem : excludes)
	        {
				excludeArgString +=" -x " + excludeItem ;
	        } 	
		 }
		 if(PropertyManager.includeString!=null && PropertyManager.includeString.length()!=0)
		 {
				String[] includes =PropertyManager.includeString.split(" ");	     
				for(String includeItem : includes)
		        {
					includeArgString +=" -i " + includeItem ;
		        }
		 }		
        // redirect the input to the Pre.
		 String newCP = Util.getPureTmpDirectory() + "/" + Parameters.PHASE_PRE + "/" +PropertyManager.projectName ;
		String path = Util.getPureTmpDirectory() + "/" + Parameters.PHASE_RECORD + "/" +PropertyManager.projectName ;//Util.getTmpDirectory();//.replace("\\", "\\\\")
		System.err.println("transform to folder:" + path);
		String argString = "-cp .:"+ newCP + ":" +Parameters.ClassPath +" -pp -validate " + mainclass+" -d "+path+
		excludeArgString + includeArgString;
		
		if(Parameters.isOutputJimple)
		{
			soot.Main.main(("-f jimple "+argString).split(" "));//"-f","jimple",c"-x","javato.","-x","edu." \\sootOutput "-process-dir", processDir
		}
		else
		{
			soot.Main.main(argString.split(" "));//"-f","jimple",c"-x","javato.","-x","edu." \\sootOutput "-process-dir", processDir
		}
		
		soot.G.reset();
		System.err.println("***** Runtime version generated *****");
	}
	
	private void transformRuntimeVersion(String mainclass) 
	{	
		Parameters.isRuntime = true;
		Parameters.isReplay = false;
		setRecordOptions(mainclass);
		
		
		 String excludeArgString = "";   
		 String includeArgString = "";    
		 
		 if(PropertyManager.excludeString!=null && PropertyManager.excludeString.length()!=0)
		 {
			 String[] excludes =PropertyManager.excludeString.split(" ");
			 for(String excludeItem : excludes)
	        {
				excludeArgString +=" -x " + excludeItem ;
	        } 	
		 }
		 if(PropertyManager.includeString!=null && PropertyManager.includeString.length()!=0)
		 {
				String[] includes =PropertyManager.includeString.split(" ");	     
				for(String includeItem : includes)
		        {
					includeArgString +=" -i " + includeItem ;
		        }
		 }		
        // redirect the input to the Pre.
		String path = Util.getPureTmpDirectory() + "/" + Parameters.PHASE_RECORD + "/" +PropertyManager.projectName ;//Util.getTmpDirectory();//.replace("\\", "\\\\")
		System.err.println("transform to folder:" + path);
		String argString = "-cp .:"+ Parameters.ClassPath +" -pp -validate " + mainclass+" -d "+path+
		excludeArgString + includeArgString;
		
		if(Parameters.isOutputJimple)
		{
			soot.Main.main(("-f jimple "+argString).split(" "));//"-f","jimple",c"-x","javato.","-x","edu." \\sootOutput "-process-dir", processDir
		}
		else
		{
			soot.Main.main(argString.split(" "));//"-f","jimple",c"-x","javato.","-x","edu." \\sootOutput "-process-dir", processDir
		}
		
		soot.G.reset();
		System.err.println("***** Runtime version generated *****");
	}
	
	private void transformReplayVersion(String mainclass) {
		
		
		Parameters.isRuntime = false;
		Parameters.isReplay = true;
		setReplayOptions(mainclass);
		Visitor.resetParameter();
		
		String path = Util.getTmpDirectory();//.replace("\\", "\\\\")
		String[] args1 = {"-cp","."+":"+Parameters.ClassPath,"-pp","-validate",mainclass,"-d",path,
				"-f","jimple",
				"-x","jrockit.","-x","edu.","-x","com.","-x","checkers.","-x","org.xmlpull.","-x","org.apache.xml.","-x","org.apache.xpath."};
		String[] args2 = {"-cp","."+":"+Parameters.ClassPath,"-pp","-validate",mainclass,"-d",path,
				"-x","org.apache.xalan.",
				"-x","org.apache.xpath.",
				"-i","org.apache.derby.",
				"-i","org.w3c.",
				"-x","jrockit.","-x","edu.","-x","com.","-x","javax.","-x","checkers.","-x","org.xmlpull."};
		String[] args3 = {"-cp",Parameters.ClassPath,"-pp","-validate",mainclass,"-d",path,
				"-i","org.apache.log4j",
				"-x","org.apache.xalan.",
				"-x","org.apache.xpath.",
				"-i","org.apache.derby.",
				"-i","org.w3c.",
				"-x","org.springframework.","-x","org.jboss.","-x","jrockit.","-x","edu.","-x","com.","-x","javax.","-x","checkers.","-x","org.xmlpull."};
	
		if(Parameters.isOutputJimple)
		{
			soot.Main.main(args1);//"-f","jimple",c"-x","javato.","-x","edu." \\sootOutput "-process-dir", processDir
		}
		else
		{
			soot.Main.main(args3);//"-f","jimple",c"-x","javato.","-x","edu." \\sootOutput "-process-dir", processDir
		}
		
		soot.G.reset();
		System.err.println("--- Replay version generated ---");
	}
	
	private void setRecordOptions(String mainclass)
	{
       PhaseOptions.v().setPhaseOption("jb", "enabled:true");
       Options.v().set_keep_line_number(true);
    	PhaseOptions.v().setPhaseOption("jb", "use-original-names:false");
    	

       Options.v().set_whole_program(true);
       Options.v().set_app(true);
       
       
      //Enable Spark
       HashMap<String,String> opt = new HashMap<String,String>();
       //opt.put("verbose","true");
       opt.put("propagator","worklist");
       opt.put("simple-edges-bidirectional","false");
       opt.put("on-fly-cg","true");
       opt.put("set-impl","double");
       opt.put("double-set-old","hybrid");
       opt.put("double-set-new","hybrid");
       opt.put("pre_jimplify", "true");
       SparkTransformer.v().transform("",opt);
       PhaseOptions.v().setPhaseOption("cg.spark", "enabled:true");
				
       // redirect the input classpath to pre.
       String newCP = Parameters.ClassPath;
       Scene.v().setSootClassPath(System.getProperty("sun.boot.class.path")
               + File.pathSeparator + System.getProperty("java.class.path") +File.pathSeparator+
               newCP);
       
       
       PackManager.v().getPack("wjtp").add(new Transform("wjtp.transformer1", new WholeProgramTransformer()));
       PackManager.v().getPack("jtp").add(new Transform("jtp.transformer2", new JTPTransformer() ));
       
       SootClass appclass = Scene.v().loadClassAndSupport(mainclass);
       Scene.v().setMainClass(appclass);
       
       
       
       Scene.v().loadClassAndSupport(Visitor.observerClass);
       Scene.v().setSootClassPath(null);// let the soot.Main.main() reloads the classpath!
	}
	
	private void setRecordOptions_pre(String mainclass)
	{
       PhaseOptions.v().setPhaseOption("jb", "enabled:true");
       Options.v().set_keep_line_number(true);
    	PhaseOptions.v().setPhaseOption("jb", "use-original-names:false");
    	

       Options.v().set_whole_program(true);
       Options.v().set_app(true);
       
       
      //Enable Spark
       HashMap<String,String> opt = new HashMap<String,String>();
       //opt.put("verbose","true");
       opt.put("propagator","worklist");
       opt.put("simple-edges-bidirectional","false");
       opt.put("on-fly-cg","true");
       opt.put("set-impl","double");
       opt.put("double-set-old","hybrid");
       opt.put("double-set-new","hybrid");
       opt.put("pre_jimplify", "true");
       SparkTransformer.v().transform("",opt);
       PhaseOptions.v().setPhaseOption("cg.spark", "enabled:true");
				
       // redirect the input classpath to pre.
       String newCP =  Util.getPureTmpDirectory() + "/" + Parameters.PHASE_PRE + "/" +PropertyManager.projectName ;
       Scene.v().setSootClassPath(System.getProperty("sun.boot.class.path")
               + File.pathSeparator + System.getProperty("java.class.path") +File.pathSeparator+
               newCP);
       
       
       PackManager.v().getPack("wjtp").add(new Transform("wjtp.transformer1", new WholeProgramTransformer()));
       PackManager.v().getPack("jtp").add(new Transform("jtp.transformer2", new JTPTransformer() ));
       
       SootClass appclass = Scene.v().loadClassAndSupport(mainclass);
       Scene.v().setMainClass(appclass);
       
       
       
       Scene.v().loadClassAndSupport(Visitor.observerClass);
       Scene.v().setSootClassPath(null);// let the soot.Main.main() reloads the classpath!
	}
	
	private void setReplayOptions(String mainclass)
	{
       Options.v().set_app(true);
       Options.v().set_keep_line_number(true);
    	PhaseOptions.v().setPhaseOption("jb", "use-original-names:false");
//     	PhaseOptions.v().setPhaseOption("jb.ne", "enabled:false");
//     	PhaseOptions.v().setPhaseOption("jj.ne", "enabled:false");
//     	PhaseOptions.v().setPhaseOption("jj.cp-ule", "enabled:false");
     	
       Scene.v().setSootClassPath(System.getProperty("sun.boot.class.path")
               + File.pathSeparator + System.getProperty("java.class.path")+File.pathSeparator+
               Parameters.ClassPath);
              
       PackManager.v().getPack("jtp").add(new Transform("jtp.transformer", new JTPTransformer()));
   
       SootClass appclass = Scene.v().loadClassAndSupport(mainclass);
       Scene.v().setMainClass(appclass);
       
       Scene.v().loadClassAndSupport(Visitor.observerClass);
	}

	

	private void setPreOptions(String mainclass)
	{

		List excludesList= new ArrayList();// same as in WholeAnalysis
		excludesList.add("jrockit.");
		excludesList.add("com.bea.jrockit");
		excludesList.add("sun.");
		Options.v().set_exclude(excludesList);
		 Options.v().set_keep_line_number(true);
		
		
		 
	      Scene.v().setSootClassPath(System.getProperty("sun.boot.class.path")
	               + File.pathSeparator + System.getProperty("java.class.path"));
	      SootClass throwableClass = Scene.v().loadClassAndSupport("java.lang.Throwable");
	      Scene.v().setSootClassPath(null);// let the soot.Main.main() reloads the classpath.

	      PackManager.v().getPack("jtp").add(new Transform("jtp.addIndicator", new AddIndicatorTransformer() ));
	       
	       PackManager.v().getPack("jtp").add(new Transform("jtp.addAtomIntention", new AddAtomIntentionTransformer()));
	       

	}
	
}
