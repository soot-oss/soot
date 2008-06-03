package dk.brics.soot.intermediate.foo;

public class FooTest {
	
	public static void main(String[] args) {
		FooTest ft = new FooTest();
		System.out.println(ft.test());
	}
	
	public int test() {
		Foo f = new Foo();
		f.foo(9);
		FooTest ft = new FooTest();
		f = g(42);
		int i = m(f);
		return f.getInt();
	}
	
	public int m(Foo f) {
		int s = f.getInt();
		return s;
	}
	
	public Foo g(int x) {
		Foo d = new Foo();
		d.foo(x);
		return d;
	}
	
	
}
