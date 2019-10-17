package soot.interfaceTesting;

public class TestSuperClassPreferenceOverInterface extends TestSuperClass implements InterfaceOne, InterfaceTwo{
	public void main() {
		TestClassPreferenceOverInterface testClass = new TestClassPreferenceOverInterface();
		testClass.print();
	}
}

interface InterfaceOne{
	default void print() {
		System.out.println("This is the default method of interface one");
	}

}

interface InterfaceTwo{
	default void print() {
		System.out.println("This is the default method of interface two");
	}
}

