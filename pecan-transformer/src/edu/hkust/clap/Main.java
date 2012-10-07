package edu.hkust.clap;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import edu.hkust.clap.Parameters;
import edu.hkust.clap.Util;

public class Main {

	

	public static void main(String[] args) {
		run(args);
//		if(args.length<1)
//			System.err.println("please specify the main class ... ");
//		else {
//		
//		}
	}

	
	private static void run(String[] args){	
		try 
		{
			String mainclass = "edu.hkust.clap.transformer.CLAPTransform";;
			Class<?> c = Class.forName(mainclass);
		    Class[] argTypes = new Class[] { String[].class };
		    Method main = c.getDeclaredMethod("main", argTypes);
		    String[] mainArgs = Arrays.copyOfRange(args, 0, 1);

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
