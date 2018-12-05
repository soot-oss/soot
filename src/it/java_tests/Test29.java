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
public class Test29 {

    public static void main(String [] args){
        Test29 t = new Test29();
        t.run();
    }
    
    public void run(){
        TL1 tl1 = new TL1();
        tl1.run();
        TL2 tl2 = new TL2();
        tl2.run();
    }
}

class TL1{
    public void run(){
        System.out.println("Top-level 1");
    }
}

class TL2{
    public void run(){
        System.out.println("Top-level 2");
    }
}
