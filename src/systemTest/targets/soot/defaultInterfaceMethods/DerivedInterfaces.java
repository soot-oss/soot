package soot.defaultInterfaceMethods;

public class DerivedInterfaces implements InterfaceTestOne, InterfaceTestTwo{
	public void main() {
		DerivedInterfaces testClass = new DerivedInterfaces();
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