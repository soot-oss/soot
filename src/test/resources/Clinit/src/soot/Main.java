package soot;

public class Main {

	public static void main(String[] args) {
		A1 a1 = new A1(); 
		// query? => EntryPoints::clinitOf(A1).
		// should return "<soot.A: void clinit()>" and "<soot.IA: void clinit()>"
		// but current implementation will return an empty set.
		System.out.println(a1.a1);
	}

}
