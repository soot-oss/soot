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

public class Test41 {

    public static void main(String [] args){
        Test41 t = new Test41();
        t.run();
    }

    public Test41(){
        System.out.println("init for Test41");
    }
    
    public void run (){
        Inner in = new Inner();
        in.run();
    }

    public class Inner extends Test41{
        
        public Inner(){
            this(4);
        }

        public Inner(int x){
            super();
            System.out.println("Second Init Inner");
            run();
        }
        
        public void run(){
            System.out.println("Smile");
        }
    }
}
