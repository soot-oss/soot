package soot.jbco.name;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import soot.jbco.util.Rand;

/**
 * Abstract class that implements {@link NameGenerator#generateName(int)}.
 *
 * @author p.nesterovich
 * @since 21.03.18
 */
public abstract class AbstractNameGenerator implements NameGenerator {

  @Override
  public String generateName(final int size) {
    if (size > NAME_MAX_LENGTH) {
      throw new IllegalArgumentException("Cannot generate junk name: too long for JVM.");
    }

    final char[][] chars = getChars();

    final int index = Rand.getInt(chars.length);
    final int length = chars[index].length;

    char newName[] = new char[size];
    do {
      newName[0] = chars[index][Rand.getInt(length)];
    } while (!Character.isJavaIdentifierStart(newName[0]));

    // generate random string
    for (int i = 1; i < newName.length; i++) {
      int rand = Rand.getInt(length);
      newName[i] = chars[index][rand];
    }
    return String.valueOf(newName);
  }

  protected abstract char[][] getChars();

}
