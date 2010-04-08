package soot.rtlib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;


public class UnexpectedReflectiveCall {
	
	private static IUnexpectedReflectiveCallHandler handler = new DefaultHandler();
	
	//delegate methods
	
	public static void classNewInstance(Class<?> c) {
		handler.classNewInstance(c);
	}

	public static void classForName(String typeName) {
		handler.classForName(typeName);
	}

	public static void constructorNewInstance(Constructor<?> c) {
		handler.constructorNewInstance(c);
	}

	public static void methodInvoke(Object receiver, Method m) {
		handler.methodInvoke(receiver, m);
	}
}