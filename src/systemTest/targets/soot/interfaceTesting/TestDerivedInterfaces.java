package soot.interfaceTesting;

public class TestDerivedInterfaces implements interfaceTestOne, interfaceTestTwo{
	public void main() {
		TestDerivedInterfaces testClass = new TestDerivedInterfaces();
		testClass.print();
	}	
}

interface interfaceTestOne{
	default void print() {
		System.out.println("This is interface one");
	}
}

interface interfaceTestTwo extends interfaceOne{
	default void print() {
		System.out.println("This is interface two");
	}
}