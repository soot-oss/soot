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
public class Test72 {

    public static void main(String [] args) {
        Test72 t72 = new Test72();
        t72.run();
    }

    private void run() {
        class MyClass {
            public void run(int x){
                if (x % 3 == 0){
                    System.out.println(x+" is a multiple of 3");
                }
                else if (x % 3 == 1) {
                    System.out.println(x+" mod 3 is 1");
                }
                else {
                    assert x % 3 == 2: x;
                    System.out.println(x+" mod 3 is 2");
                }
            }
        };

        MyClass mc = new MyClass();
        mc.run(7);
        
    }
}
