package soot.interfaceTesting;

public class TestSuperClassImplementsInterface implements DefaultPrint{
	
	public void main() {
		TestSuperClassImplementsInterface main = new TestSuperClassImplementsInterface();
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