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
public class FieldBaseTest {

    Point p1 = new Point();
    Point p2 = new Point();

    public static void main(String [] args){
        FieldBaseTest f = new FieldBaseTest();
        f.run();
    }
    
    public void run(){
        int i = 3;

        p1.x = 9;
        p1.y = 8;

        p2.x = i;
        p2.y = 4;

        if ((p1.x - p2.x) > (p1.y - p2.y)){
            p1.x = p1.y;
        }
    }

    public void test(Point p){
        if (p.x > 3){
            p.y = 3;
        } 
    }
}

class Point {

    public int x;
    public int y;
}
