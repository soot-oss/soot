package soot.interfaceTesting;

public class TestSuperClassInterfaceSameSignature extends TestSuperClass implements printInterface {	
	
	public static void main() {	
		TestSuperClassInterfaceSameSignature main = new TestSuperClassInterfaceSameSignature();
		main.print();
	}	
}

interface printInterface{
	default void print() {
		System.out.println("This is default Print Interface Print Method");
	}
}



