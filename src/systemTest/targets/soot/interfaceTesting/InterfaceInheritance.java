package soot.defaultInterfaceMethods;

public class InterfaceInheritance implements InterfaceTestB{
	public void main() {
		InterfaceInheritance testClass = new InterfaceInheritance();
		testClass.print();
	}

	@Override
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