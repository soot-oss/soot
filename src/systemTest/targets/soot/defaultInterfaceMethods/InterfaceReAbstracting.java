package soot.defaultInterfaceMethods;

public class InterfaceReAbstracting implements InterfaceA, InterfaceB{
	public void main() {
		InterfaceReAbstracting testClass = new InterfaceReAbstracting();
		testClass.print();
	}
	
	public void print() {
		System.out.println("This is print method of main class");
	}
	
}

interface InterfaceA{
	default void print() {
		System.out.println("This is interface A");
	}
}

interface InterfaceB extends InterfaceA{
	void print();
}