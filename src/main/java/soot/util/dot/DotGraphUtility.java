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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DotGraphUtility {
  private static final Logger logger = LoggerFactory.getLogger(DotGraphUtility.class);

  /**
   * Replace any {@code "} with {@code \"}. If the {@code "} character was already escaped (i.e. {@code \"}), then the escape
   * character is also escaped (i.e. {@code \\\"}).
   *
   * @param original
   *
   * @return
   */
  public static String replaceQuotes(String original) {
    byte[] ord = original.getBytes();
    int quotes = 0;
    boolean escapeActive = false;
    for (byte element : ord) {
      switch (element) {
        case '\\':
          escapeActive = true;
          break;
        case '\"':
          quotes++;
          if (escapeActive) {
            quotes++;
          }
          // fallthrough
        default:
          escapeActive = false;
          break;
      }
    }

    if (quotes == 0) {
      return original;
    }

    byte[] newsrc = new byte[ord.length + quotes];
    for (int i = 0, j = 0, n = ord.length; i < n; i++, j++) {
      if (ord[i] == '\"') {
        if (i > 0 && ord[i - 1] == '\\') {
          newsrc[j++] = (byte) '\\';
        }
        newsrc[j++] = (byte) '\\';
      }
      newsrc[j] = ord[i];
    }

    /*
     * logger.debug("before "+original); logger.debug("after  "+(new String(newsrc)));
     */
    return new String(newsrc);
  }

  /**
   * Replace any return ({@code \n}) with {@code \\n}.
   *
   * @param original
   *
   * @return
   */
  public static String replaceReturns(String original) {
    byte[] ord = original.getBytes();
    int quotes = 0;
    for (byte element : ord) {
      if (element == '\n') {
        quotes++;
      }
    }

    if (quotes == 0) {
      return original;
    }

    byte[] newsrc = new byte[ord.length + quotes];
    for (int i = 0, j = 0, n = ord.length; i < n; i++, j++) {
      if (ord[i] == '\n') {
        newsrc[j++] = (byte) '\\';
        newsrc[j] = (byte) 'n';
      } else {
        newsrc[j] = ord[i];
      }
    }

    /*
     * logger.debug("before "+original); logger.debug("after  "+(new String(newsrc)));
     */
    return new String(newsrc);
  }

  public static void renderLine(OutputStream out, String content, int indent) throws IOException {
    for (int i = 0; i < indent; i++) {
      out.write(' ');
    }
    content += "\n";
    out.write(content.getBytes());
  }
}
