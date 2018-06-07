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
public class Constants {
    
    public static void main (String [] args) {
        int i = 3 + 4 + 5 + 7;
        int j = -(3 + 4);
        System.out.println(i);
        System.out.println(j);
        int [] arr = new int [] {3, 4, 5, 4, 5, 8, 4};
        System.out.println(arr[1+1+1]);
        System.out.println(!false);
        switches(1+4+5);
    }

    public static void switches(int i){
        switch (i){
            
            case 3+7: {System.out.println(10); break;}
        }
    }
}
