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
public class CharOps {

    public static void main(String [] agrs){
        char c1 = 'j';
        System.out.println(charOp("" + (char) 0, (char) 0));
        System.out.println( ("" + (char) 0) + (char) 0);
        System.out.println((short)0);
        System.out.println("Max Char: "+Character.MAX_VALUE);
        System.out.println(Character.MAX_VALUE);
        System.out.println(charOp("" + Character.MAX_VALUE, (char) 0));
        System.out.println(("" + Character.MAX_VALUE) + (char) 0);
        System.out.println(charOp(""+(char)1 , Character.MAX_VALUE));
        System.out.println((""+(char)1) + Character.MAX_VALUE);

        System.out.println();
        System.out.println(charOp("" + Character.MAX_VALUE, (char) 0));
        System.out.println("" + Character.MAX_VALUE + (char) 0);     
    }
    

    static String charOp(String x, char y) { return x + y; }
}
