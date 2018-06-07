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
public class LoopTest {

    public static void main (String [] args){
        
        int x = 9;
        
        for (int i = 9; i < 10; i++){
            x++;    
        }

        for (;;){
            if (x < 4) break;
            x--;
        }

        do {
            x += 2;
        }while(x < 15);

        while (x > 10){
            x -= 3;
        }

        while (true){
            if (x > 47) break;
            x *= 3;
        }

        while (x > 4) {
            x--;
            if (x % 3 != 0) continue;
            System.out.println(x);
        }

        int [] arr = new int[9];
        for (int m = 0; m < 10; m++){
            x = 4;
            arr[4] = 8;
            arr[4] = m;
            arr[x] = 8;
        }
    }
}
