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
public class StaticConstantsTest {

    public static int MAX = 10;
    public int x = 10;

    {
        System.out.println("hi");
    }

    static {
        System.out.println();
    }
    
    public StaticConstantsTest() {
        System.out.println(x);
    }
    
    public StaticConstantsTest(int i) {
        System.out.println(i*x);
    }
    
    public static void main (String [] args) {
        StaticConstantsTest sct = new StaticConstantsTest();
        sct.run();
    }

    public void run() {
    
        int [] arr = new int[MAX];
        int i = x;
        x = i * i;
    }
} 
