/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Sable Research Group
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


/* @author Feng Qian */

package soot.util.dot;

import java.io.*;

/**
 * Encodes general Dot commands.
 */
public class DotGraphCommand implements Renderable{
  String command;

  /**
   * @param cmd a dot dommand string
   */
  public DotGraphCommand(String cmd) {
    this.command = cmd;
  }

  /**
   * Implements Renderable interface.
   * @param out the output stream
   * @param indent the number of indent space 
   * @see Renderable
   */
  public void render(OutputStream out, int indent) throws IOException {
    DotGraphUtility.renderLine(out, command, indent);
  }
}
