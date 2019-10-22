package soot.defaultInterfaceMethods;

public class SuperClassImplementsInterface implements DefaultPrint{
	
	public void main() {
		SuperClassImplementsInterface main = new SuperClassImplementsInterface();
		main.print();	
	}
	
	public void print() {
		System.out.println("This is super class print method");
	}
}

interface DefaultPrint{
	default void print() {
		System.out.println("This is default print method");
	}
}