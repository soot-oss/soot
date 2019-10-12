package soot.interfaceTesting;

public class TestClassInterfaceSameSignature implements helloWorld {
	
	public void print() {
		System.out.println("Welcome to Java 8");
	}
	public void main() {	
		TestClassInterfaceSameSignature main = new TestClassInterfaceSameSignature();
		main.print();		
	}	
}

interface helloWorld{
	default void print() {
		System.out.println("Hello World !!");
	}
}

