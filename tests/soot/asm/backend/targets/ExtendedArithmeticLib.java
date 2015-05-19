package soot.asm.backend.targets;

public class ExtendedArithmeticLib {

	private int i1;
	private float f1;
	private long l1;
	private double d1;
	private short s1;
	private byte b1;

	private int i2;
	private float f2;
	private long l2;
	private double d2;

	private int i3;
	private float f3;
	private long l3;
	private double d3;

	public void doMod() {
		i1 = i2 % i3;
		f1 = f2 % f3;
		l1 = l2 % l3;
		d1 = d2 % d3;
	}

	public void doSub() {
		i1 = i2 - i3;
		f1 = f2 - f3;
		l1 = l2 - l3;
		d1 = d2 - d3;
	}

	public int doINeg(int i) {
		return -i;
	}

	public int doCNeg(char c) {
		return -c;
	}

	public int doSNeg(short s) {
		return -s;
	}

	public int doBNeg(byte b) {
		return -b;
	}

	public long doLNeg(long l) {
		return l;
	}

	public double doDNeg(double d) {
		return -d;
	}

	public float doFNeg(float f) {
		return -f;
	}

	public int doInc() {
		int j = 0;
		for (int i = 0; i < 100; i++) {
			j+=4;
		}
		return j;
	}
}
