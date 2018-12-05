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

class TopClass {

    public TopClass(int x){
    }
    public TopClass(){
    }
    public int getB(){
        return 2;
    }
    public int getC(){
        return 3;
    }
}

public class AnonClass {

    public static void main(String args[] ) {
        AnonClass ac = new AnonClass();
        final int h = 9; 
        for (int i = 0; i < 5; i++){
            ac.run(i);
        }
    }

    public void run(int y){
        Object o;
        final int x = y;
        go(o = new AnonClass().new AnotherClass(){
            public int getB(){
                return x;
            }
            public int getC(){
                return 24;
            }
            public void run(){
                System.out.println("hi");
            }
            public int hi(int i){
                return 8*i;
            }
        });

        
        Object obj = new TopClass(){
            public int getB(){
                return 20;
            }
            public int getC(){
                return 30;
            }
        };
        
    }

    public void go(Object o){
    
    }

    public class AnotherClass {
    
        public int hi(int i){
            return 7*i;
        }
    }
}

