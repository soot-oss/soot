package soot.interfaceTesting;

public class TestMain implements Default {
	public void main()
	{
		TestMain main = new TestMain();
		main.target();
		main.printMessage();
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

