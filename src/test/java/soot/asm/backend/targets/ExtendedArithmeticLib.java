package soot.asm.backend.targets;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

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
