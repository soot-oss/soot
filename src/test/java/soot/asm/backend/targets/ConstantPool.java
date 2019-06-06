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
