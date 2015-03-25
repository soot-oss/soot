package soot.rtlib.tamiflex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


public interface IUnexpectedReflectiveCallHandler {
	
	public void classNewInstance(Class<?> c);
	public void classForName(String typeName);
	public void constructorNewInstance(Constructor<?> c);
	public void methodInvoke(Object receiver, Method m);
	public void fieldSet(Object receiver, Field f);
	public void fieldGet(Object receiver, Field f);

}