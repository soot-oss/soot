package soot.defaultInterfaceMethods;

public class SimpleDefaultInterface implements Default {
	public void main()
	{
		SimpleDefaultInterface testClass = new SimpleDefaultInterface();
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

