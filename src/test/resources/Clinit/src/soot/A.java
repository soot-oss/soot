package soot;

public class A implements IA {
	private static String a;
	static {
		a = "A";
		System.out.println("<soot.A: void clinit()>");
	}
}
