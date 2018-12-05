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
public class Compare4 {
    public static void main(String [] args) {
    
        Compare4 c = new Compare4();
        c.run();
    }

    private void run() {
        int i = 9;
        long l = 10;
        double d = 0.98F;

        float f = 19L;
        if (l >= i) {
            System.out.println(l);
        }
        else if (l < i) {
            System.out.println(i);
        }


        long j = i + l;
        float f2 = 0.978213F;
        double d2 = 0.9;

        if (d2 == f2) {
            d2 += f2;
        }
    }
}
