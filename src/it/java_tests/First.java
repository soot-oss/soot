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

public class First {

	public static void main(String args[]) {
		int sum = 0;
		
		First f = new First();
		f.foo();
		
		for (int i = 1; i < 10; i++) {
			int x, y;
			x = i + 1;
			y = i + 1;
			sum = sum + x + y + 1;
		}
		System.out.println(sum);
	}
	
	public void foo() {
		System.out.println("Hi there!");
	}
}
