package soot.defaultInterfaceMethods;

public class SuperClassInterfaceSameSignature extends SuperClassImplementsInterface implements PrintInterface {	
	
	public void main() {	
		SuperClassInterfaceSameSignature main = new SuperClassInterfaceSameSignature();
		main.print();
	}	
}

interface PrintInterface{
	default void print() {
		System.out.println("This is default Print Interface Print Method");
	}
}



