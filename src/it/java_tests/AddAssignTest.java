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


public class AddAssignTest {

    private Vector charSet = new Vector(27);
    String abc = "abcdefghijklmnopqrstuvwxyzJ";
    {
        for (int i = 0; i < abc.length(); i++){
            charSet.add((new Character(abc.charAt(i))).toString());
        }
    }
    public static void main(String [] args) {
        AddAssignTest aat = new AddAssignTest();
        aat.run();
    }

    private void run() {
        String result = new String();

        result += this.charSet.elementAt(3);
    }
}
