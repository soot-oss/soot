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
public class Test69 {
    public static void main (String [] args){
        Test69 t69 = new Test69();
        try {
            t69.run(4, 5);
        }
        catch (MyException e){
        }
    }

    public void run(int x, int y) throws MyException{
        if (x < y){
            throw new MyException("my exception from outer");
        }
        else {
            System.out.println(x);
        }
        Inner in = new Inner();
        in.run(8, 7);
    }

    public class MyException extends Throwable{
        public MyException(String s){
            super(s);
        }
    }

    public class Inner {
        public void run(int x, int y) throws MyException{
            if (x < y) throw new MyException("x too small");
            else {
                x = x - y;
            }
        }
    }
}
