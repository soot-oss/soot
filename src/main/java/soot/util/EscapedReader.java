package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A FilterReader which catches escaped characters (<code>\\unnnn</code>) in the input and de-escapes them. Used in the
 * Jimple Parser.
 */
public class EscapedReader extends FilterReader {
  private static final Logger logger = LoggerFactory.getLogger(EscapedReader.class);

  /** Constructs an EscapedReader around the given Reader. */
  public EscapedReader(Reader fos) {
    super(fos);
  }

  private StringBuffer mini = new StringBuffer();

  boolean nextF;
  int nextch = 0;

  /** Reads a character from the input. */
  public int read() throws IOException {
    /* if you already read the char, just return it */
    if (nextF) {
      nextF = false;
      return nextch;
    }

    int ch = super.read();

    if (ch != '\\') {
      return ch;
    }

    /* we may have an escape sequence here .. */
    mini = new StringBuffer();

    ch = super.read();
    if (ch != 'u') {
      nextF = true;
      nextch = ch;
      return '\\';
    }

    mini.append("\\u");
    while (mini.length() < 6) {
      ch = super.read();
      mini.append((char) ch);
    }

    // logger.debug(""+mini.toString());
    ch = Integer.parseInt(mini.substring(2).toString(), 16);

    return ch;
  }
}
