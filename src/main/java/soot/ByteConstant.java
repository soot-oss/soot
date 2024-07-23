package soot;

import soot.jimple.IntConstant;

public class ByteConstant extends IntConstant {
	private static final long serialVersionUID = 0L;
	public static final ByteConstant ZERO = new ByteConstant(0);
	public static final ByteConstant ONE = new ByteConstant(1);

	private static final int MAX_CACHE = 128;
	private static final int MIN_CACHE = -127;
	private static final int ABS_MIN_CACHE = Math.abs(MIN_CACHE);
	private static final ByteConstant[] CACHED = new ByteConstant[1 + MAX_CACHE + ABS_MIN_CACHE];

	public ByteConstant(byte value) {
		super(value);
	}

	public ByteConstant(int value) {
		super((byte) value);
	}

	public static ByteConstant v(int value) {
		if (value >= MIN_CACHE && value <= MAX_CACHE) {
			int idx = value + ABS_MIN_CACHE;
			ByteConstant c = CACHED[idx];
			if (c != null) {
				return c;
			}
			c = new ByteConstant(value);
			CACHED[idx] = c;
			return c;
		}
		return new ByteConstant(value);
	}

	public byte getByte() {
		return (byte) value;
	}

	@Override
	public Type getType() {
		return ByteType.v();
	}
}
