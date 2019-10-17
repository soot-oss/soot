package soot.interfaceTesting;

public class TestSuperClassPreferenceOverInterface extends TestSuperClass implements interfaceOne, interfaceTwo{
	public void main() {
		TestClassPreferenceOverInterface testClass = new TestClassPreferenceOverInterface();
		testClass.print();
	}
}

interface interfaceOne{
	default void print() {
		System.out.println("This is the default method of interface one");
	}

}

interface interfaceTwo{
	default void print() {
		System.out.println("This is the default method of interface two");
	}
}

