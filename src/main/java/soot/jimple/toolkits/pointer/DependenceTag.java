package soot.jimple.toolkits.pointer;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import soot.tagkit.Tag;

public class DependenceTag implements Tag {
  private final static String NAME = "DependenceTag";
  protected short read = -1;
  protected short write = -1;
  protected boolean callsNative = false;

  public boolean setCallsNative() {
    boolean ret = !callsNative;
    callsNative = true;
    return ret;
  }

  protected void setRead(short s) {
    read = s;
  }

  protected void setWrite(short s) {
    write = s;
  }

  public String getName() {
    return NAME;
  }

  public byte[] getValue() {
    byte[] ret = new byte[5];
    ret[0] = (byte) ((read >> 8) & 0xff);
    ret[1] = (byte) (read & 0xff);
    ret[2] = (byte) ((write >> 8) & 0xff);
    ret[3] = (byte) (write & 0xff);
    ret[4] = (byte) (callsNative ? 1 : 0);
    return ret;
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
    if (callsNative) {
      buf.append("SECallsNative\n");
    }
    if (read >= 0) {
      buf.append("SEReads : " + read + "\n");
    }
    if (write >= 0) {
      buf.append("SEWrites: " + write + "\n");
    }
    return buf.toString();
  }
}
