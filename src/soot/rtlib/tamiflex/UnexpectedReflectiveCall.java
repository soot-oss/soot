package soot.rtlib.tamiflex;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class UnexpectedReflectiveCall {
	
	private final static IUnexpectedReflectiveCallHandler handler;
	
	static {
		String listenerClassName = System.getProperty("BOOSTER_LISTENER", "soot.rtlib.tamiflex.DefaultHandler");
		try {
			handler = (IUnexpectedReflectiveCallHandler) Class.forName(listenerClassName).newInstance();
		} catch (Exception e) {
			throw new Error("Error instantiating listener for Booster.",e);
		}
	}
	
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

	public static void fieldSet(Object receiver, Field f) {
		handler.fieldSet(receiver, f);
	}

	public static void fieldGet(Object receiver, Field f) {
		handler.fieldGet(receiver, f);
	}
}