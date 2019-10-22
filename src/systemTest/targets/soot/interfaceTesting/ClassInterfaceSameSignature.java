package soot.defaultInterfaceMethods;

public class ClassInterfaceSameSignature implements HelloWorld {
	
	public void print() {
		System.out.println("Welcome to Java 8");
	}
	public void main() {	
		ClassInterfaceSameSignature testClass = new ClassInterfaceSameSignature();
		testClass.print();		
	}	
}

interface HelloWorld{
	default void print() {
		System.out.println("Hello World !!");
	}
}

