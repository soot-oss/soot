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

public class CallGraphExample {

	public static void main(String[] args) {
		Class1 o = new Class1();
		o.foo();
		o.goo();
		
		o = new Class2();
		o.foo();
		
		CallGraphExample cge = new CallGraphExample();
		cge.bar(o);
	}
	
	public void bar (Class1 o){
		o.foo();
	}
}

class Class1 {

    public void foo() {
        System.out.println("Class1: foo");
    }

    public void goo() {
        System.out.println("Class1: goo");
    }
}

class Class2 extends Class1 {

    public void foo() {
        System.out.println("Class2: foo");
    }

    public void goo() {
        System.out.println("Class2: goo");
    }

}


