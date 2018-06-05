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
public class FinallyAndReturnsLJH {

  static String note = "original";

    public static void main(String[] args) {
        if (m() != "hi") 
          System.out.println("call to m() bad");
        if (note != "finally") 
          System.out.println("note left from m() bad, note is " + note);
        if (m1() != "hi1") 
          System.out.println("call to m1() bad");
        if (note != "trying") 
          System.out.println("note left from m1() bad, note is " +note);
    }

    public static String m() {
        try {
            return "hi";
        } finally {
             note = "finally";
        }
    }

    public static String m1() {
        try { note = "trying";
        } finally {
            return "hi1";
        }
    }

}
