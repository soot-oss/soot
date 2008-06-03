package dk.brics.soot.intermediate.foo;

public class Foo {

	private int i;
	
	public Foo() {
		i = 7;
	}
	
	public void foo(int j) {
		i = j;
	}
	
	public int getInt() {
		return i;
	}
	
}
