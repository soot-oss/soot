package soot.rtlib.tamiflex;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class DefaultHandler implements IUnexpectedReflectiveCallHandler {
	public void methodInvoke(Object receiver, Method m) {
		System.err.println("Unexpected reflective call to method "+m);
	}

	public void constructorNewInstance(Constructor<?> c) {
		System.err.println("Unexpected reflective instantiation via constructor "+c);
	}

	public void classNewInstance(Class<?> c) {
		System.err.println("Unexpected reflective instantiation via Class.newInstance on class "+c);
	}

	public void classForName(String typeName) {
		System.err.println("Unexpected reflective loading of class "+typeName);
	}
}