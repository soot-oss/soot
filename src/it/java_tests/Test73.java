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
interface MyListener {
    public void action();
}
public class Test73 {

    public static void main (String [] args){
        Test73 t73 = new Test73();
        t73.run(new Integer(7), new Integer(8), new Integer(9));
    }

    public void run(final Object o1, final Object o2, final Object o3){
        new MyListener () {
            public void action(){
                System.out.println("Smile: "+o1);
            }
        }.action();
        new MyListener () {
            public void action(){
                System.out.println("Smile: "+o2);
            }
        }.action();
        class MyClass {
            public void action(){
                System.out.println("Smile: "+o3);
            }
        };
        MyClass mc = new MyClass();
        mc.action();
    }
}
