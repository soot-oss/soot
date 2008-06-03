
public class CallGraphExample {

	public static void main(String[] args) {
		Class1 o = new Class1();
		o.foo();
		o.goo();
		
		o = new Class2();
		o.foo();
		
		CallGraphExample cge = new CallGraphExample();
		cge.bar(o);
	}
	
	public void bar (Class1 o){
		o.foo();
	}
}

class Class1 {

    public void foo() {
        System.out.println("Class1: foo");
    }

    public void goo() {
        System.out.println("Class1: goo");
    }
}

class Class2 extends Class1 {

    public void foo() {
        System.out.println("Class2: foo");
    }

    public void goo() {
        System.out.println("Class2: goo");
    }

}


