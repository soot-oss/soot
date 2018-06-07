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
import java.util.*;

public class Simple2 {
    private int x;
    
    public static void main(String[] args) {
        int j = 1;
        int i = 2;
        ;
        if (i + j > 1) { System.out.println("Hello" + i); }
        Simple2.add(1, 2);
        Simple2 simple = new Simple2();
        System.out.println(Simple2.add(2, 3));
        simple.run();
    }
    
    public static String getString() { return "Hello"; }
    
    public static int add(int i, int j) { return i + j; }
    
    public Simple2() { this(8); }
    
    public Simple2(int y) {
        super();
        this.x = y;
        System.out.println(this.x);
    }
    
    public void run() { int[] arr = { 9, 0, 8 }; }
    
}
