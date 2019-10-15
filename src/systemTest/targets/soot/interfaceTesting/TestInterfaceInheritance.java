package soot.interfaceTesting;

public class TestInterfaceInheritance implements interfaceTestB{
	public void main() {
		TestInterfaceInheritance testClass = new TestInterfaceInheritance();
		testClass.print();
	}
	
	public void printMessage() {
		System.out.println("This is print method of main class");
	}	
}

interface interfaceTestA{
	default void print() {
		System.out.println("This is interface A");
	}
}

interface interfaceTestB extends interfaceTestA{
	void printMessage();
}