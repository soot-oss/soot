package soot.interfaceTesting;

public class TestSuperClassInterfaceSameSignature extends TestSuperClassImplementsInterface implements PrintInterface {	
	
	public void main() {	
		TestSuperClassInterfaceSameSignature main = new TestSuperClassInterfaceSameSignature();
		main.print();
	}	
}

interface PrintInterface{
	default void print() {
		System.out.println("This is default Print Interface Print Method");
	}
}



