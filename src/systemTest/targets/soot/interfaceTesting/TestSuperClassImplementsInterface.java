package soot.interfaceTesting;

public class TestSuperClass implements defaultPrint{
	
	public void main() {
		TestSuperClass main = new TestSuperClass();
		main.print();	
	}
	
	public void print() {
		System.out.println("This is super class print method");
	}
}

interface defaultPrint{
	default void print() {
		System.out.println("This is default print method");
	}
}