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

public class Arrays {
	void doBool(){
		boolean[] bool = new boolean[3];
		
		boolean boolv = bool[1];
		bool[0] = boolv;
 	}
	
	void doByte(){
		byte[] b = new byte[4];
		
		byte bv = b[1];
		b[0] = bv;
	}
	
	void doChar(){
		char[] c = new char[5];	
		
		char cv = c[1];
		c[0] = cv;
	}
	
	void doDouble(){
		double[] d = new double[6];

		double dv = d[1];
		d[0] = dv;
	}
	
	void doFloat(){
		float[] f = new float[7];
		
		float fv = f[1];
		f[0] = fv;
	}
	
	void doInt(){
		int[] i = new int[8];
		int iv = i[1];
		i[0] = iv;
	}
	
	void doLong(){
		long[] l = new long[9];
		
		long lv = l[1];
		l[0] = lv;
	}
	
	void doShort(){
		short[] s = new short[10];
		
		short sv = s[1];
		s[0] = sv;
	}
	
	void doObject(){
		Object[] o = new Object[11];		
		
		Object ov = o[1];
		o[0] = ov;
		o[3] = null;
	}
	
	void doString(){
		String[] str = new String[12];
		
		String strv = str[1];
		str[0] = strv;
	}
	
	void doIntInt(){
		int[][] ii = new int[3][3];
		
		int[] iiv = ii[1];
		ii[0] = iiv;
		
		int iiiv = ii[2][1];
		ii[1][2] = iiiv;
	}
	
	void doObjectObject(){
		Object[][] oo = new Object[4][4];
		
		Object[] oov = oo[1];
		oo[0] = oov;
		
		Object ooov = oo[2][1];
		oo[1][2] = ooov;
	}

}
