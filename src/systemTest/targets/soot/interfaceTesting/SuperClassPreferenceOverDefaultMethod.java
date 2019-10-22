package soot.defaultInterfaceMethods;

public class SuperClassPreferenceOverDefaultMethod extends SuperClass implements InterfaceOne, InterfaceTwo{
	public void main() {
		TestSuperClassPreferenceOverInterface testClass = new TestSuperClassPreferenceOverInterface();
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

