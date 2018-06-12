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
public class Compare {
    public static void main(String [] args) {
    
        Compare c = new Compare();
        c.run();
    }

    private void run() {
        int i = 9;
        long l = 10;
        long l2 = 11;
        short s = 4;
        byte by = 2;

        if (s < by) {
        }

        if (by < i) {
        }
        
        if (l2 >= l) {
            l2 = l2 - 1;
        }
        
        if (l != i) {
            i = i + 1;
        }
        else if (l == i) {
            i = i - 1;
        }


        float f = 0.9F;
        double d = 0.9;

        if (d == f) {
            d += f;
        }
        
        if (f <= d) {
            d += f;
        }

        if (f < i) {
            i *= i;
        }

        if (f < l) {
            l = i + l;
        }

        char c = 'c';

        if (c < i ) {
            i++;
        }
    }
}
