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
class Helper {

    public static void main(String [] args){
    }

    public void action(){
        System.out.println("Helper");
    }
}
public class Test76{

    public static void main(String [] args){
        Test76 t76 = new Test76();
        t76.run(5);
    }
    
    public void run(final int x){
        new Helper(){
        
            public void action(){
                
                class MyHelper1 {
                    public void action(){
                        System.out.println(x);

                        new Helper(){
                            public void action(){
                                System.out.println(x*x);
                                class MyHelper2{
                                    public void action(){
                                        System.out.println(x*x*x*x*x);
                                    }
                                };
                                MyHelper2 m2 = new MyHelper2();
                                m2.action();
                            }
                        }.action();
                    }
                };
                MyHelper1 m1 = new MyHelper1();
                m1.action();
            }
        }.action();
    }
}
