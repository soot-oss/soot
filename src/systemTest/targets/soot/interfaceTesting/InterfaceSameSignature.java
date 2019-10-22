package soot.defaultInterfaceMethods;

public class InterfaceSameSignature implements Read, Write {
	
	public void print() {
		Write.super.print();
		Read.super.print();
	}
	public void main() {	
		InterfaceSameSignature testClass = new InterfaceSameSignature();
		testClass.read();
		testClass.write();
		testClass.print();
	}	
}

interface Read{
	default void read() {
		System.out.println("Reading the console input..");
	}
	default void print() {
		System.out.println("This is a read method");
	}
}

interface Write{
	default void write() {
		System.out.println("Writing to console output..");
	}
	default void print()
	{
		System.out.println("This is a write method");
	}
}
