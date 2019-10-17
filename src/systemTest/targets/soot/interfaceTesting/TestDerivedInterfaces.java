package soot.interfaceTesting;

public class TestDerivedInterfaces implements InterfaceTestOne, InterfaceTestTwo{
	public void main() {
		TestDerivedInterfaces testClass = new TestDerivedInterfaces();
		testClass.print();
	}	
}

interface InterfaceTestOne{
	default void print() {
		System.out.println("This is interface one");
	}
}

interface InterfaceTestTwo extends InterfaceTestOne{
	default void print() {
		System.out.println("This is interface two");
	}
}