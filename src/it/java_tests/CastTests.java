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
public class CastTests {
    private Vector myList = new Vector(27);
    private Vector myCharacterList = new Vector(27);

    public static void main(String [] args) {
        CastTests ct = new CastTests();
        ct.run('p', 0.9);
    }

    private boolean run(char y, double x) {
        
        boolean result = false;
       
        if (x >= 0.8) {
            result &= this.myCharacterList.add(new Character(y)) && this.myList.add(new Double(x));
        }
        else if (x == 0.3) {
            result = true;
        }
        else if (x < 0.4) {
            result = true;
        }
        return result;
    }
}
