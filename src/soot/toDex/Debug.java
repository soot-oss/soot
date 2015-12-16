// 
// (c) 2012 University of Luxembourg - Interdisciplinary Centre for 
// Security Reliability and Trust (SnT) - All rights reserved
//
// Author: Alexandre Bartel
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 2.1 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>. 
//

package soot.toDex;

import soot.options.Options;

public class Debug {
  public static boolean TODEX_DEBUG;
  
  public static void printDbg (String s, Object...objects) {
    TODEX_DEBUG = Options.v().verbose();
    if (TODEX_DEBUG) {
      for (Object o: objects) {
        s += o.toString();
      }
      System.out.println (s);
    }
      
  }
  
  public static void printDbg (boolean c, String s, Object...objects) {
      if (c)
          printDbg(s, objects);
  }
}
