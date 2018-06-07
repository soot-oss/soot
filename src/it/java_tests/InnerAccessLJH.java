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
import java.util.*;

public class InnerAccessLJH {
    public static void main(String[] args) {
      if (new C().getCount() == 3)
        System.out.println("correct");
      else
        System.out.println("incorrect");
    }
}


class C {
    protected int i = 2;
    private String s = "hi";

    Runnable r = new Runnable() {
	    public void run() {
		s += "s";       
		//s = s + "s";       
	    }
	};

    public int getCount() {
	return new Object() {
		public int m() {
		    r.run();
		    return s.length();
		}
	    }.m();
    }
}

class DI extends D.Inner {
}


class D implements Map.Entry {
    public Object getKey() { return null; }
    public Object getValue() { return null; }
    public Object setValue(Object o) { return o; }

    static class Inner {}
}


class Outer {
    class Middle {
	class Inner {
	    void m() {
		Inner.this.m1();
		Middle.this.m1();
		Outer.this.m1();
	    }

	    void m1() {}
	}
	void m1() {}
    }
    void m1() {}
}

