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



class Aaa {

    public class Ccc {
        public int yy;

	public Ccc() {
	    yy = 4;
	}

	public void bar() {
	System.out.println(Aaa.this.x + this.yy);
	class BBB {
	    int u = 0;
	    public void bar2() {
		ddd.bar3();
		System.out.println(Aaa.this.x + this.u + Aaa.Ccc.this.yy);
	     }
	    class Ddd {
		int v = 0;
		public void bar3() {
		    System.out.println(BBB.this.u + this.v);		}
	    }
	    public Ddd ddd;
            public BBB() {
		ddd = new Ddd();
            }
	}
	BBB b = new BBB();
	b.bar2();
	}
    }

    public int zz;

    public int x;

    public Ccc ccc;
    public Aaa() {
       ccc = new Ccc();
       x = 3;
    }
}

public class Test78 { 


    public static void main(String[] args) { 
	Aaa aaa = new Aaa();
	System.out.println(aaa.x);
	aaa.ccc.bar();
    } 
 
} 
