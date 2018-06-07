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
public class Test7 {

    public static void main (String [] args){
        Test7 t7 = new Test7();
        t7.run();
    }

    public void run(){
        final String atMe = getString();
        class MyListener1 implements MyListener{
            public void action(){
                System.out.println("Smile: "+atMe);
            }
        };
        new MyListener1().action();
    }

    public String getString(){
        return "atMe";
    }
}
