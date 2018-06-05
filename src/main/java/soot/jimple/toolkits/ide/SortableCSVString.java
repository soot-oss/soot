package soot.jimple.toolkits.ide;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2013 Christian Fritz and others
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

public class SortableCSVString implements Comparable<SortableCSVString> {
  String value;
  int position;

  public SortableCSVString(String str, int pos) {
    value = str;
    position = pos;
  }

  public int compareTo(SortableCSVString anotherString) {
    // "@"+i+";
    int result;
    String subString = value.substring(0, value.indexOf(';'));
    String anotherSubString = anotherString.value.substring(0, anotherString.value.indexOf(';'));

    result = subString.compareTo(anotherSubString);
    if (result == 0) {
      if (position < anotherString.position) {
        return -1;
      }
      if (position > anotherString.position) {
        return 1;
      }
      return 0;
    } else {
      return result;
    }
  }
}
