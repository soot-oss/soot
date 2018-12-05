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
public class X {

    public static void main(String [] args){
        X x = new X();
        x.m1();
        x.m4();
        x.m6();
        W w = new W();
        w.m9();
    }
    
    public void m1(){
        class Y{
            public void m2(){
                System.out.println("Y 1 m2");
            }
        }
        Y y = new Y();
        y.m2();
        class Z{
            public void m3(){
                System.out.println("Z 1 m3");
            }
        }
        Z z = new Z();
        z.m3();
    }

    public void m4(){
        class Y{
            public void m5(){
                System.out.println("Y 2 m5");
            }
        }
        Y y = new Y();
        y.m5();
    }

    public void m6(){
        new Object(){
            public void m7(){
                System.out.println("Anon 1 in X m7");
            }
        }.m7();
        new Object(){
            public void m8(){
                System.out.println("Anon 2 in X m8");
            }
        }.m8();
    }
}
class W {

    public void m9(){
        new Object(){
            public void m10(){
                System.out.println("Anon 1 in W m10");
            }
        }.m10();
    } 
}
