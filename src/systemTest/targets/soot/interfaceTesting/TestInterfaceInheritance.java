package soot.interfaceTesting;

public class TestInterfaceInheritance implements InterfaceTestB{
	public void main() {
		TestInterfaceInheritance testClass = new TestInterfaceInheritance();
		testClass.print();
	}
	
	public void printMessage() {
		System.out.println("This is print method of main class");
	}	
}

interface InterfaceTestA{
	default void print() {
		System.out.println("This is interface A");
	}
}

interface InterfaceTestB extends InterfaceTestA{
	void printMessage();
}