package soot.toDex;

import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.Type;

/**
 * A register for the Dalvik VM. It has a number and a type.
 */
public class Register implements Cloneable {
	
	public static final int MAX_REG_NUM_UNCONSTRAINED = 65535;

	public static final int MAX_REG_NUM_SHORT = 255;
	
	public static final int MAX_REG_NUM_BYTE = 15;

	public static final Register EMPTY_REGISTER = new Register(IntType.v(), 0);
	
	private static boolean fitsInto(int regNumber, int maxNumber, boolean isWide) {
		if (isWide) {
			// reg occupies number and number + 1, hence the "<"
			return regNumber >= 0 && regNumber < maxNumber;
		}
		return regNumber >= 0 && regNumber <= maxNumber;
	}
	
	public static boolean fitsUnconstrained(int regNumber, boolean isWide) {
		return fitsInto(regNumber, MAX_REG_NUM_UNCONSTRAINED, isWide);
	}
	
	public static boolean fitsShort(int regNumber, boolean isWide) {
		return fitsInto(regNumber, MAX_REG_NUM_SHORT, isWide);
	}
	
	public static boolean fitsByte(int regNumber, boolean isWide) {
		return fitsInto(regNumber, MAX_REG_NUM_BYTE, isWide);
	}
	
	private final Type type;
	
	private int number;
	
	public Register(Type type, int number) {
		this.type = type;
		this.number = number;
	}
	
	public boolean isEmptyReg() {
		return this == EMPTY_REGISTER;
	}
	
	public boolean isWide() {
		return SootToDexUtils.isWide(type);
	}
	
	public boolean isObject() {
		return SootToDexUtils.isObject(type);
	}
	
	public boolean isFloat() {
		return type instanceof FloatType;
	}
	
	public boolean isDouble() {
		return type instanceof DoubleType;
	}
	
	public Type getType() {
		return type;
	}
	
	public String getTypeString() {
		return type.toString();
	}
	
	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		if (isEmptyReg()) {
			// number of empty register stays at zero - that's part of its purpose
			return;
		}
		this.number = number;
	}
	
	private boolean fitsInto(int maxNumber) {
		if (isEmptyReg()) {
			// empty reg fits into anything
			return true;
		}
		return fitsInto(number, maxNumber, isWide());
	}
	
	public boolean fitsUnconstrained() {
		return fitsInto(MAX_REG_NUM_UNCONSTRAINED);
	}
	
	public boolean fitsShort() {
		return fitsInto(MAX_REG_NUM_SHORT);
	}
	
	public boolean fitsByte() {
		return fitsInto(MAX_REG_NUM_BYTE);
	}
	
	@Override
	public Register clone() {
		return new Register(this.type, this.number);
	}
	
	@Override
	public String toString() {
		if (isEmptyReg()) {
			return "the empty reg";
		}
		return "reg(" + number + "):" + type.toString();
	}
}