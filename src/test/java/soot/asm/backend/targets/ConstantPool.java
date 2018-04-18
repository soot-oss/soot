package soot.asm.backend.targets;

public class ConstantPool {
	public static final String s1 = "H:mm:ss.SSS";
	public static final String s2 = null;

	public static final Object o1 = "O";
	public static final Object o2 = null;
	public static final Object o3 = 123;
	public static final Object o4 = 1234l;
	public static final Object o5 = 123.3d;

	public static final int i1 = 123;
	public static final int i2 = new Integer(123);
	
	public static final long l1 = 12233l;
	public static final long l2 = 123;
	public static final long l3 = new Long(12341l);
	
	public static final double d1 = 123.142;
	public static final double d2 = 1234.123f;
	public static final double d3 = new Double(1234.123);

}
