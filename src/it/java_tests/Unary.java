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
public class Unary {

    
    public static void main (String [] args){
        int i = 0;
        i++;
        i--;
        ++i;
        --i;
        i = +i;
        i = -i;
        i = ~i;
        boolean j = false;
        boolean k = !j;

        int n = 0;

        for (int m = 0; m < 10; m++){
            n = 9+m;
        }

        int [] arr = new int [] {3, 4, 5};
        int h = 0;
        int x = arr[h++];
        h = 0;
        int y = arr[++h];
    }
}
