package soot.util.dot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Sable Research Group
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

import java.io.IOException;
import java.io.OutputStream;

/**
 * Encodes general Dot commands.
 */
public class DotGraphCommand implements Renderable {
  String command;

  /**
   * @param cmd
   *          a dot dommand string
   */
  public DotGraphCommand(String cmd) {
    this.command = cmd;
  }

  /**
   * Implements Renderable interface.
   * 
   * @param out
   *          the output stream
   * @param indent
   *          the number of indent space
   * @see Renderable
   */
  public void render(OutputStream out, int indent) throws IOException {
    DotGraphUtility.renderLine(out, command, indent);
  }
}
