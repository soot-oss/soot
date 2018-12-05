package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Ondrej Lhotak
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

import heros.ThreadSafe;

import java.util.HashMap;
import java.util.Map;

/**
 * A class that numbers strings, so they can be placed in bitsets.
 *
 * @author Ondrej Lhotak
 */

@ThreadSafe
public class StringNumberer extends ArrayNumberer<NumberedString> {
  private Map<String, NumberedString> stringToNumbered = new HashMap<String, NumberedString>(1024);

  public synchronized NumberedString findOrAdd(String s) {
    NumberedString ret = stringToNumbered.get(s);
    if (ret == null) {
      ret = new NumberedString(s);
      stringToNumbered.put(s, ret);
      add(ret);
    }
    return ret;
  }

  public NumberedString find(String s) {
    return stringToNumbered.get(s);
  }

}
