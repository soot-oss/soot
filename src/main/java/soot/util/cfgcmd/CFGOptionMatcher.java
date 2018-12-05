package soot.util.cfgcmd;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 John Jorgensen
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.CompilationDeathException;

/**
 * A class used by CFG utilities that need to match different option strings with classes that implement those options.
 *
 * A <code>CFGOptionMatcher</code> maintains a set of named options, and provides a means for matching abbreviated option
 * values against those names.
 */

public class CFGOptionMatcher {
  private static final Logger logger = LoggerFactory.getLogger(CFGOptionMatcher.class);

  /**
   * The type stored within a <code>CFGOptionMatcher</code>. Options to be stored in a <code>CFGOptionMatcher</code> must
   * extend this class.
   */
  public static abstract class CFGOption {
    private final String name;

    protected CFGOption(String name) {
      this.name = name;
    }

    public String name() {
      return name;
    }
  };

  private CFGOption[] options;

  /**
   * Creates a CFGOptionMatcher.
   *
   * @param options
   *          The set of command options to be stored.
   */
  public CFGOptionMatcher(CFGOption[] options) {
    this.options = options;
  }

  /**
   * Searches the options in this <code>CFGOptionMatcher</code> looking for one whose name begins with the passed string
   * (ignoring the case of letters in the string).
   *
   * @param quarry
   *          The string to be matched against the stored option names.
   *
   * @return The matching {@link CFGOption}, if exactly one of the stored option names begins with <code>quarry</code>.
   *
   * @throws soot.CompilationDeathException
   *           if <code>quarry</code> matches none of the option names, or if it matches more than one.
   */
  public CFGOption match(String quarry) throws soot.CompilationDeathException {
    String uncasedQuarry = quarry.toLowerCase();
    int match = -1;
    for (int i = 0; i < options.length; i++) {
      String uncasedName = options[i].name().toLowerCase();
      if (uncasedName.startsWith(uncasedQuarry)) {
        if (match == -1) {
          match = i;
        } else {
          logger.debug("" + quarry + " is ambiguous; it matches " + options[match].name() + " and " + options[i].name());
          throw new CompilationDeathException(CompilationDeathException.COMPILATION_ABORTED, "Option parse error");
        }
      }
    }
    if (match == -1) {
      logger.debug("\"" + quarry + "\"" + " does not match any value.");
      throw new CompilationDeathException(CompilationDeathException.COMPILATION_ABORTED, "Option parse error");
    } else {
      return options[match];
    }
  }

  /**
   * Returns a string containing the names of all the options in this <code>CFGOptionMatcher</code>, separated by '|'
   * characters. The string is intended for use in help messages.
   *
   * @param initialIndent
   *          The number of blank spaces to insert at the beginning of the returned string. Ignored if negative.
   *
   * @param rightMargin
   *          If positive, newlines will be inserted to try to keep the length of each line in the returned string less than
   *          or equal to <code>rightMargin</code>.
   *
   * @param hangingIndent
   *          If positive, this number of spaces will be inserted immediately after each newline inserted to respect the
   *          <code>rightMargin</code>.
   */
  public String help(int initialIndent, int rightMargin, int hangingIndent) {

    StringBuffer newLineBuf = new StringBuffer(2 + rightMargin);
    newLineBuf.append('\n');
    if (hangingIndent < 0) {
      hangingIndent = 0;
    }
    for (int i = 0; i < hangingIndent; i++) {
      newLineBuf.append(' ');
    }
    String newLine = newLineBuf.toString();

    StringBuffer result = new StringBuffer();
    int lineLength = 0;
    for (int i = 0; i < initialIndent; i++) {
      lineLength++;
      result.append(' ');
    }

    for (int i = 0; i < options.length; i++) {
      if (i > 0) {
        result.append('|');
        lineLength++;
      }
      String name = options[i].name();
      int nameLength = name.length();
      if ((lineLength + nameLength) > rightMargin) {
        result.append(newLine);
        lineLength = hangingIndent;
      }
      result.append(name);
      lineLength += nameLength;
    }
    return result.toString();
  }
}
