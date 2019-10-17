package soot.interfaceTesting;

public class TestSimpleDefault implements Default {
	public void main()
	{
		TestSimpleDefault testClass = new TestSimpleDefault();
		testClass.target();
		testClass.printMessage();
	}
	public void printMessage()
	{
		System.out.println("Hello World!");
	}
}


interface Default{
	default void target() {
		System.out.println("Hello!");
	}
	void printMessage();
}

