package edu.hkust.clap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import edu.hkust.clap.Util;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<String>  sootArgs = new LinkedList(Arrays.asList(args));
		int len = sootArgs.size();
		if(len<2)
			System.err.println("please specify the run type and the main class ... ");
		else if(args[0].contains("-replay")){
			run(args);
		}
		else
			System.err.println("sorry, unknown parameters ... ");
	}
			
	private static void run(String[] args){
	
		try {
			Class<?> c = Class.forName(args[1]);
		    Class[] argTypes = new Class[] { String[].class };
		    Method main = c.getDeclaredMethod("main", argTypes);
		    String[] mainArgs;
		    String[] dumbArgs = {};
		    if(args.length>1)
		    	mainArgs = Arrays.copyOfRange(args, 2, args.length);
		    else
		    	mainArgs=dumbArgs;
		    //System.out.format("invoking %s.main()%n", c.getName());
		    main.invoke(null, (Object)mainArgs);
			// production code should handle these exceptions more gracefully
			} catch (ClassNotFoundException x) {
			    x.printStackTrace();
			} catch (NoSuchMethodException x) {
			    x.printStackTrace();
			} catch (IllegalAccessException x) {
			    x.printStackTrace();
			} catch (InvocationTargetException x) {
			    x.printStackTrace();
			}
		
	}

}
