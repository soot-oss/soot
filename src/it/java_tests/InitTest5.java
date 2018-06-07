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
public class InitTest5 {

    public class Inner1 {
        public void m() {
            System.out.println("hello from inner 1");
        }
    }

    public static class Inner2 {
        public void m() {
            System.out.println("hello from inner 2");
        }
    }

    public static interface InnerInterface1 {
        public void go();
    }
    
    public interface InnerInterface2 {
        public void go();
    }
    
    public static void main(String [] args){
        InitTest5 it = new InitTest5();
        it.run();
        //Inner1 in = new Inner1();
        class MyClass extends Inner2{
            public void m() {
                System.out.println("hello from anon subtype of inner 1");
            }
        };
        /*class MyClass1 extends MyStaticClass1{
            public void m() {
                System.out.println("hello from anon subtype of inner 1");
            }
        };*/

        new Inner2 () {
            public void m(){
                System.out.println("hello from inner 2 again");
            }
        }.m();

        new InnerInterface1 () {
            public void go(){
                System.out.println("go");
            }
        }.go();

        new InnerInterface2 () {
            public void go(){
                System.out.println("go2");
            }
        }.go();

        //int y = 9;
        new MyObject1(){//y) {
            public void go(){
                System.out.println("anon of non inner class");
            }
            
        };
    }

    public void run(){
        new Inner1 () {
            public void m() {
                System.out.println("hello from anon subtype of inner 1");
            }
        }.m();
        new Inner2 () {
            public void m() {
                System.out.println("class to extend is inner static but not in static method");
            }
        }.m();
    }
}

class MyObject1 {

    public void go2(){
        System.out.println("go2");
    }
}
