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
class Foo {
    public static void main(String [] agrs){
        Foo f = new Foo();
        f.sum(new int [] {2,3,4,2,3,3,21,3});
    }
    
  public void sum(int[] a) {
	int total = 0;
	int i=0;
	int b = a[0];
	String j = null;

	for (i=0; i<a.length; i++) {
	  total += a[i];
	}
	System.out.println(j);
	int c = a[i-1];
  }
}
