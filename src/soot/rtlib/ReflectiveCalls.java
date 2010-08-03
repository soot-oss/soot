package soot.rtlib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class ReflectiveCalls {
	
	private final static Set<String> classForName = new HashSet<String>();
	private final static Set<String> classNewInstance = new HashSet<String>();
	private final static Set<String> constructorNewInstance = new HashSet<String>();
	private final static Set<String> methodInvoke = new HashSet<String>();
	
	static {
		//soot will add initialization code here
	}
	
	public static void knownClassForName(int contextId, String className) {
		if(!classForName.contains(contextId+className)) {
			UnexpectedReflectiveCall.classForName(className);
		}
	}
	
	public static void knownClassNewInstance(int contextId, Class<?> c) {
		if(!classNewInstance.contains(contextId+c.getName())) {
			UnexpectedReflectiveCall.classNewInstance(c);
		}
	}
	
	public static void knownConstructorNewInstance(int contextId, Constructor<?> c) {
		if(!constructorNewInstance.contains(contextId+SootSig.sootSignature(c))) {
			UnexpectedReflectiveCall.constructorNewInstance(c);
		}
	}
	
	public static void knownMethodInvoke(int contextId, Object o, Method m) {
		if(!methodInvoke.contains(contextId+SootSig.sootSignature(o,m))) {
			UnexpectedReflectiveCall.methodInvoke(o, m);
		}
	}
}
