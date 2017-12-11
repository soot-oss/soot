package soot.asm.backend.targets;

public class ArithmeticLib {
	
	private int rInt;
	private float rFloat;
	private long rLong;
	private double rDouble;
	
	private short rShort;
	private char rChar;
	private byte rByte;

	final int cInt = 1;
	final float cFloat = 1;
	final long cLong = 1;
	final double cDouble = 1;
	
	public int doCompInt(int i){
		rInt = i / -1;
		rFloat = i * 17;
		rLong = i + 5;
		rDouble = i - 2;
		return rInt;
	}
	
	public float doCompFloat(float f){
		rInt = (int) (f / 13f);
		rFloat = f * 3f;
		rLong = (long) (f - 2f);
		rDouble = f + 1f;
		return rFloat;
	}
	
	public long doCompLong(long l){
		rInt = (int) (l * 5l);
		rFloat = l + 2l;
		rLong = l * 6l;
		rDouble = l / 6l;
		return rLong;
	}
	
	public double doCompDouble(double d){
		rInt = (int) (d / 1.0);
		rLong = (long) (d * 6.0);
		rFloat = (float) (d + 0.0);
		rDouble = d - 4.0;
		return rDouble;
	}
	
	public short castInt2Short(){
		rShort = (short) rInt;
		return rShort;
	}
	
	public char castInt2Char(){
		rChar = (char) rInt;
		return rChar;
	}
	
	public byte castInt2Byte(){
		rByte = (byte) rInt;
		return rByte;
	}
}
