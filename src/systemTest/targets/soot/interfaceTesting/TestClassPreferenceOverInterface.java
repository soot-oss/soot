package soot.interfaceTesting;

public class TestClassPreferenceOverInterface implements helloWorld {
	
	public void print() {
		System.out.println("Welcome to Java 8");
	}
	public void main() {	
		TestClassPreferenceOverInterface testClass = new TestClassPreferenceOverInterface();
		testClass.print();		
	}	
}

interface helloWorld{
	default void print() {
		System.out.println("Hello World !!");
	}
}

