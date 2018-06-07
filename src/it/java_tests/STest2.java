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
public class STest2 extends SuperB {
    
    private int x = 9;
    
    public static void main(String [] args){
        STest2 st2 = new STest2();
        st2.run();
    }

    
    public void run(){
        System.out.println(super.z);
        super.go();
    }

    public void go(){
        System.out.println("going");
    }
}

class SuperB extends SuperA {
    public int z = 6;
    protected int w = 7;
    public void go(){
        System.out.println("go from SuperB");
    }
    public void go1(){
        System.out.println("go1 from SuperB");
    }
    protected void going1(){
        System.out.println("going1 from SuperB");
    }
}
class SuperA {
    public int x = 9;
    protected int y = 8;
    public void go(){
        System.out.println("go from SuperA");
    }
    protected void going(){
        System.out.println("going from SuperA");
    }
}

