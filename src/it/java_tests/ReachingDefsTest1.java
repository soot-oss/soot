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
public class ReachingDefsTest1 {

    public static void main (String [] args) {
        ReachingDefsTest1 rdt1 = new ReachingDefsTest1();
        rdt1.m(8);
    }

    public void m(int i){
        int x = 4;
        int y = 3;
        if (i < 10) {
            x = 5;
        }
        else if (i == 10){
            x = 6;
        }
        else {
            x = 7;
        }
        int j = x * y;
        
    }
    
    public void n(int i){
        int x = 4;
        if (i < 10){
            x = 5;
        }
        else if (x == 10){
            x = 6;
        }
        
        System.out.println(x);
    }
}
