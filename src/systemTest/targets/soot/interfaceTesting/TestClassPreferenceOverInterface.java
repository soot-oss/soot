package soot.interfaceTesting;

public class TestClassPreferenceOverInterface implements HelloWorld {
	
	public void print() {
		System.out.println("Welcome to Java 8");
	}
	public void main() {	
		TestClassPreferenceOverInterface testClass = new TestClassPreferenceOverInterface();
		testClass.print();		
	}	
}

interface HelloWorld{
	default void print() {
		System.out.println("Hello World !!");
	}
}

