package edu.hkust.clap.engine;

import java.io.ObjectOutputStream;

import properties.PropertyManager;
import edu.hkust.clap.Parameters;
import edu.hkust.clap.monitor.MonitorData;
/**
 * 
 * @author Jeff
 *	only synchronized regions are atomic
 */
public class EngineMain
{  
	// remember to set the PropertyManager.useJvmStack properly
	public static MonitorData getMonitorData()
	{
		String path = CommonUtil.getTempFilePath("trace" +PropertyManager.projectname);
		System.err.println("load from trace from: " + path);
		Object obj = CommonUtil.getDeserializedObject(path);
		if(obj instanceof MonitorData)
			return (MonitorData) obj;
		else
			return null;
	}
    public static void main(String[] args)
    {
    	long start_time = System.currentTimeMillis();
    	
    	MonitorData mondata = getMonitorData();
    	TraceEngine engine = new TraceEngine(mondata);
    	
    	//engine.setCheckRace();
    	engine.setCheckAtomicity();
    	engine.setCheckAtomSet();
    	
    	//engine.setAtomRegionAll();
    	
    	//engine.setRemoveRedundance();
    	
    	long process_start_time = System.currentTimeMillis();
    	engine.preProcess();
    	long process_end_time = System.currentTimeMillis();

    	long detect_start_time = System.currentTimeMillis();
    	engine.findAllPatterns();
    	long detect_end_time = System.currentTimeMillis();
    	
    	engine.showAllPatterns();
    	engine.saveAllPatterns();   	

    	long tranform_start_time = System.currentTimeMillis();  	
    	if(Parameters.noDump)
    	{
    		
    	}
    	else {
    		engine.transform();
		}
    
    	long tranform_end_time = System.currentTimeMillis();

    	engine.computePostStatistics();
    	
    	long end_time = System.currentTimeMillis();
    	long total_time = (end_time - start_time);
    	
    	long process_total_time = (process_end_time - process_start_time);
    	long detect_total_time = (detect_end_time - detect_start_time);
    	long tranform_total_time = (tranform_end_time - tranform_start_time);
    	
    	if(PropertyManager.reportTime)
    	{
    		CommonUtil.print("total time (msec): "+ total_time+"\n");
    	}
    	engine.reportStatistics();
    	
//    	CommonUtil.print("Total Data Processing Time: "+process_total_time+" ms");
//    	CommonUtil.print("Total Pattern Search Time: "+detect_total_time+" ms");
//    	CommonUtil.print("Total Schedule Generation Time: "+tranform_total_time+" ms");
//    	CommonUtil.print("Total Processing Time: "+total_time+" ms");
    	
    	CommonUtil.closeFileWriter();

    }
}	
