package soot.interfaceTesting;

public class TestInterfaceReAbstracting implements interfaceA, interfaceB{
	public void main() {
		TestInterfaceReAbstracting testClass = new TestInterfaceReAbstracting();
		testClass.print();
	}
	
	public void print() {
		System.out.println("This is print method of main class");
	}
	
}

interface interfaceA{
	default void print() {
		System.out.println("This is interface A");
	}
}

interface interfaceB extends interfaceA{
	void print();
}