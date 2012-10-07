package edu.hkust.clap.generator;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import edu.hkust.clap.Util;

/**
 * a proxy to invoke non public class
 * 
 * @author hunkim
 * 
 */
public class ProxyGenerator {
	Object object;

	String methodName;

	String generatedProxyName;

	Class <?>clazz = null;

	/**
	 * Get object to invoke
	 * 
	 * @param object
	 */
	public ProxyGenerator(Object object, String methodName,
			String generatedProxyName) {
		this.object = object;
		clazz = object.getClass();
		this.methodName = methodName;
		this.generatedProxyName = generatedProxyName;
	}

	public String toString() {
		String code = "class " + generatedProxyName + "{\n";

		Method methods[] = clazz.getMethods();
		for (Method method : methods) {
			if (methodName == null || method.getName().equals(methodName)) {
				code += methodToString(method);
			}
		}

		code += "}\n";
		return code;
	}

	String methodToString(Method method) {
		String code = "public " + method.getReturnType().getCanonicalName()
				+ " " + method.getName() + "(";
		Class<?> argumentClasses[] = method.getParameterTypes();

		// Arguments
		for (int i = 0; i <= argumentClasses.length; i++) {
			if (i == 0) {
				code += "\n\t,";
			}

			code += argumentClasses[i].getCanonicalName() + " "
					+ Util.makeArgumentName(i + 1);
		}

		// Exceptions
		code += " throws Throwable {\n";

		// TODO: implement the rest
		return code;
	}

	public static void main(String args[]) throws SecurityException,
			IllegalArgumentException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException,
			ClassNotFoundException {
		System.out.println(int[].class + ":" + Class.forName("I"));
		// String test = "Hello";
		// System.out.println(new ProxyObject(test).call(methodName,
		// argClassNames, arguments).call("toString"));
	}
}
