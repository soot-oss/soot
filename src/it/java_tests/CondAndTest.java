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
import java.util.*;

public class CondAndTest {

    private Vector charsSet = new Vector();
    private Vector charsProb = new Vector();
    
    public static void main(String [] args) {
    
        boolean x = true;
        boolean y = false;

        if (x && y) {
            System.out.println("Both True");
        }

        CondAndTest cdt = new CondAndTest();
        cdt.addChar('i', 0.9);
    }

    private boolean addChar(char c, double d) {
        boolean result = false;

        if (d >= 0) {
            result &= this.charsSet.add(new Character(c)) && this.charsProb.add(new Double(d));
        }

        return result;
    }
}
