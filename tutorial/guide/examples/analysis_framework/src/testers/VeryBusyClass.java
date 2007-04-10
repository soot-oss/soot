package testers;

public class VeryBusyClass
{
	public static void main(String[] args) {	
		int x = 10;
		int a = x - 1;
		int b = x - 2;

		while (x > 0) {
			System.out.println(a*b - x);
			x = x - 1;
		}
		System.out.println(a*b);
	}
}
