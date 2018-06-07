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
public class PrivateAddAssign {
    private int x = 9;
    private int [] a = new int [] {3, 4, 5};
    
    class Inner {
        public void run(){
            PrivateAddAssign bar = new PrivateAddAssign();
            bar.x += 1;
            System.out.println("bar.x: "+bar.x);
            int i = 0;
            a[i++] += 1;
            System.out.println("a[0]: "+a[0]);
            System.out.println("a[1]: "+a[1]);
            System.out.println("a[2]: "+a[2]);
        }
    }

    public static void main(String [] args){
        PrivateAddAssign a = new PrivateAddAssign();
        a.run();
    }

    private void run(){
        Inner i = new Inner();
        i.run();
    }
}
