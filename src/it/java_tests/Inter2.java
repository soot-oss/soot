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
public class Inter2 {

    public static void main(String [] args){
        Inter2 i2 = new Inter2();
        i2.run();
        i2.go();
    }

    public void run(){
        Object o = new I3.C();
        Class c = o.getClass();
        System.out.println(c.getModifiers());

        o = new C2.C3();
        c = o.getClass();
        System.out.println(c.getModifiers());
    }

    public void go(){
        
        Object o = new C2.C3();
        Class c = o.getClass();
        System.out.println(c.getModifiers());
    }
}

interface I3 {

    class C{}
}

class C2 {

    static class C3{}
}
