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

public class DeeplyNestedAnon {

    public static void main(String [] args){
        DeeplyNestedAnon d = new DeeplyNestedAnon();
        d.run();
    }
    
    public void run(){
    
        final int x = 8;

        Object o = new TopClass(5) {
          
            public int getB(){
                
                final int y = 9;

                Object obj = new TopClass(){
                    public int getB(){
                        return y;
                    }
                };
                return 4;
                
            }
            public int getC(){
                return 6;
            }
        };

        o = new TopClass(){
            public int getB(){
                return 7;
            }
        };
    }

   
}
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

