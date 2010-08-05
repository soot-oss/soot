package soot.rtlib.tamiflex;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import soot.rtlib.IUnexpectedReflectiveCallHandler;

public class SilentHandler implements IUnexpectedReflectiveCallHandler {
	public void methodInvoke(Object receiver, Method m) {
	}

	public void constructorNewInstance(Constructor<?> c) {
	}

	public void classNewInstance(Class<?> c) {
	}

	public void classForName(String typeName) {
	}
}