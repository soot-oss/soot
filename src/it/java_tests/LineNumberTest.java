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
public class LineNumberTest {
    
    public static void main(String [] args) {

        //Object o = new Integer(9);
        int i = 10;
        int x = 9;
        int y = 9;
        int z = 6;
        if (i == 2) {
            i = x + 1;
            i = y - 1;
            i = z * 2;
        }
        else if (i == 5){
            z = 3;
            y = 4;
        }
        while (i > 10 ) {
            i = i - 3;
        }
    }

    public LineNumberTest(){
        super();
    }
}
