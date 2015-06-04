package soot.asm.backend.targets;

public class Stores {

	public int doSth(){
		int i;
		double d;
		float f;
		short s;
		boolean b;
		byte bb;
		long l;
		char c;
		Object o;
		int[] a;
		
		
		i = 2343249;
		d = 3.14324;
		f = 3.143f;
		s = 4636;
		b = System.currentTimeMillis() > 0;
		bb = (byte) i;
		l = 314435665;
		c = 123;
		o = new Object();
		a = new int[3];

		a[1] = 24355764;
		
		System.out.println(i + d + f + s + "" +b + bb + l + c + " " + o);
		
		return i;
	}
}
