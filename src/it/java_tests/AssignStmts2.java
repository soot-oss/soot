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
public class AssignStmts2 {

    int x = 0;
    public static void main(String [] args) {
    

        int i = 8;
        int j = 9;

        int k = 7;
        int f = 4;

        i = j + k + f + 1;
        i = j + 1;
        System.out.println(i);
        i += j;
        System.out.println(i);
        i -= j;
        System.out.println(i);
        i *= j;
        System.out.println(i);
        i /= j;
        System.out.println(i);
        i %= j;
        System.out.println(i);
        i <<= j;
        System.out.println(i);
        i >>= j;
        System.out.println(i);
        i >>>= j;
        System.out.println(i);
        i &= j;
        System.out.println(i);
        i |= j;
        System.out.println(i);
        i ^= j;
        System.out.println(i);
            
        AssignStmts2 as = new AssignStmts2(); 
        as.run();
    }

    public void run() {
        x += 1;
        System.out.println(x);
    }
}
