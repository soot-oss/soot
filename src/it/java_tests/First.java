
public class First {

	public static void main(String args[]) {
		int sum = 0;
		
		First f = new First();
		f.foo();
		
		for (int i = 1; i < 10; i++) {
			int x, y;
			x = i + 1;
			y = i + 1;
			sum = sum + x + y + 1;
		}
		System.out.println(sum);
	}
	
	public void foo() {
		System.out.println("Hi there!");
	}
}
