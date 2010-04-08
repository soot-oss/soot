package soot.rtlib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;


public interface IUnexpectedReflectiveCallHandler {
	
	public void classNewInstance(Class<?> c);
	public void classForName(String typeName);
	public void constructorNewInstance(Constructor<?> c);
	public void methodInvoke(Object receiver, Method m);

}