/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.jbco.util;

/**
 * @author Michael Batchelder 
 * 
 * Created on 17-Feb-2006 
 */
public class Rand {

  private static final java.util.Random r = new java.util.Random(1);//System.currentTimeMillis());
  
  public static int getInt(int n) {
    return r.nextInt(n);
  }
  
  public static int getInt() {
    return r.nextInt();
  }
  
  public static float getFloat() {
    return r.nextFloat();
  }
  
  public static long getLong() {
    return r.nextLong();
  }
  
  public static double getDouble() {
    return r.nextDouble();
  }
}
