package soot.interfaceTesting;

public class TestInterfaceReAbstracting implements InterfaceA, InterfaceB{
	public void main() {
		TestInterfaceReAbstracting testClass = new TestInterfaceReAbstracting();
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