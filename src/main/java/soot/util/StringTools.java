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

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/** Utility methods for string manipulations commonly used in Soot. */
public class StringTools {

  /** Convenience field storing the system line separator. */
  public final static String lineSeparator = System.getProperty("line.separator");

  /**
   * Returns fromString, but with non-isalpha() characters printed as <code>'\\unnnn'</code>. Used by SootClass to generate
   * output.
   */
  public static String getEscapedStringOf(String fromString) {
    StringBuilder whole = new StringBuilder();

    assert (!lineSeparator.isEmpty());
    final int cr = lineSeparator.charAt(0);
    final int lf = (lineSeparator.length() == 2) ? lineSeparator.charAt(1) : -1;
    for (char ch : fromString.toCharArray()) {
      int asInt = ch;
      if (asInt != '\\' && ((asInt >= 32 && asInt <= 126) || asInt == cr || asInt == lf)) {
        whole.append(ch);
      } else {
        whole.append(getUnicodeStringFromChar(ch));
      }
    }

    return whole.toString();
  }

  /**
   * Returns fromString, but with certain characters printed as if they were in a Java string literal. Used by
   * StringConstant.toString()
   */
  public static String getQuotedStringOf(String fromString) {
    final int fromStringLen = fromString.length();
    // We definitely need fromStringLen + 2, but let's have some additional space
    StringBuilder toStringBuffer = new StringBuilder(fromStringLen + 20);
    toStringBuffer.append("\"");
    for (int i = 0; i < fromStringLen; i++) {
      char ch = fromString.charAt(i);
      switch (ch) {
        case '\\':
          toStringBuffer.append("\\\\");
          break;
        case '\'':
          toStringBuffer.append("\\\'");
          break;
        case '\"':
          toStringBuffer.append("\\\"");
          break;
        case '\n':
          toStringBuffer.append("\\n");
          break;
        case '\t':
          toStringBuffer.append("\\t");
          break;
        case '\r':
          /*
           * 04.04.2006 mbatch added handling of \r, as compilers throw error if unicode
           */
          toStringBuffer.append("\\r");
          break;
        case '\f':
          /*
           * 10.04.2006 Nomait A Naeem added handling of \f, as compilers throw error if unicode
           */
          toStringBuffer.append("\\f");
          break;
        default:
          if (ch >= 32 && ch <= 126) {
            toStringBuffer.append(ch);
          } else {
            toStringBuffer.append(getUnicodeStringFromChar(ch));
          }
          break;
      }
    }
    toStringBuffer.append("\"");
    return toStringBuffer.toString();
  }

  /**
   * Returns a String containing the escaped <code>\\unnnn</code> representation for <code>ch</code>.
   */
  public static String getUnicodeStringFromChar(char ch) {
    String s = Integer.toHexString(ch);
    switch (s.length()) {
      case 1:
        return "\\u" + "000" + s;
      case 2:
        return "\\u" + "00" + s;
      case 3:
        return "\\u" + "0" + s;
      case 4:
        return "\\u" + "" + s;
      default:
        // hex value of a char never exceeds 4 characters since char is 2 bytes
        throw new AssertionError("invalid hex string '" + s + "' from char '" + ch + "'");
    }
  }

  /**
   * Returns a String de-escaping the <code>\\unnnn</code> representation for any escaped characters in the string.
   */
  public static String getUnEscapedStringOf(String str) {
    StringBuilder buf = new StringBuilder();
    CharacterIterator iter = new StringCharacterIterator(str);
    for (char ch = iter.first(); ch != CharacterIterator.DONE; ch = iter.next()) {
      if (ch != '\\') {
        buf.append(ch);
      } else { // enter escaped mode
        ch = iter.next();
        char format;

        if (ch == '\\') {
          buf.append(ch);
        } else if ((format = getCFormatChar(ch)) != '\0') {
          buf.append(format);
        } else if (ch == 'u') { // enter unicode mode
          StringBuilder mini = new StringBuilder(4);
          for (int i = 0; i < 4; i++) {
            mini.append(iter.next());
          }
          buf.append((char) Integer.parseInt(mini.toString(), 16));
        } else {
          throw new RuntimeException("Unexpected char: " + ch);
        }
      }
    }
    return buf.toString();
  }

  /** Returns the canonical C-string representation of c. */
  public static char getCFormatChar(char c) {
    switch (c) {
      case 'n':
        return '\n';
      case 't':
        return '\t';
      case 'r':
        return '\r';
      case 'b':
        return '\b';
      case 'f':
        return '\f';
      case '\"':
        return '\"';
      case '\'':
        return '\'';
      default:
        return '\0';
    }
  }
}
