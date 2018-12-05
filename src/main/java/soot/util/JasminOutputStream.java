package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Ondrej Lhotak
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An output stream that wraps an existing output stream, and converts Jasmin code written into a class file that gets
 * written to the original output stream. (Write Jasmin into this stream, and .class will come out.)
 */
public class JasminOutputStream extends ByteArrayOutputStream {
  final private OutputStream out;

  public JasminOutputStream(OutputStream out) {
    this.out = out;
  }

  public void flush() {
    ByteArrayInputStream bais = new ByteArrayInputStream(this.toByteArray());
    jasmin.Main.assemble(bais, out, false);
  }

  @Override
  public void close() throws IOException {
    this.out.close();
    super.close();
  }
}
