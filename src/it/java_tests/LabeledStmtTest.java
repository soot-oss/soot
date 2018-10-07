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
public class LabeledStmtTest {

    public static void main(String [] args){
    
        int i = 0;
        int j = 0;
        outer:while (i < 100) {
        
            inner: while (j < 100) {
                j = j + 1;
                if (j * i < 50) continue;
                System.out.println(j);
                if (j == 75) break inner;
            }
             
            i = i + 1;

            if (i == 20) break outer;
        }
    }
}
