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
class HelperWithParams {

    public static void main(String [] args){
    }
    public HelperWithParams(int x, int y){
        
    }
    
    public HelperWithParams(){
        
    }
    
    public void action(){
        System.out.println("Helper");
    }
}
public class Test24 {

    public static void main (String [] args){
        Test24 t24 = new Test24();    
    }

    public void run(final int j){
        class MyHelper1 extends HelperWithParams {

            public MyHelper1(){
            }
            public MyHelper1(int x, int y){
            }
            public void action(){
                System.out.println("Smile Anon: "+j);
            }
        };
        new MyHelper1(2, 3).action();
    }
}
