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
public class FinalStaticTest {

    public static final int x = 9;
    public static final long y = 982L;
    public static final double z = 9.0;
    public static final float a = 9.23e3f;
    public static final char c = 'c';
    public static final byte by = 1;
    public static final short sh = 2;
    
    public static final String b = "Jennifer";
    public static final String q = "string with \" quotes";
    public static final int [] arr = new int [] {1, 1, 2, 3, 5, 8, 13, 21};
    public static final Object o = new Object();
    
    public static void main(String [] args){
        System.out.println(x);
        System.out.println(y);
        System.out.println(z);
        System.out.println(a);
        System.out.println(b);
        System.out.println("quotes \" in string");
    }
}
