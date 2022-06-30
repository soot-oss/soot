package soot.dava.toolkits.base.finders;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jerome Miecznikowski
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

import java.util.Comparator;
import java.util.TreeSet;

class IndexSetComparator implements Comparator {
  public int compare(Object o1, Object o2) {
    if (o1 == o2) {
      return 0;
    }

    o1 = ((TreeSet) o1).last();
    o2 = ((TreeSet) o2).last();

    if (o1 instanceof String) {
      return 1;
    }

    if (o2 instanceof String) {
      return -1;
    }

    return ((Integer) o1).intValue() - ((Integer) o2).intValue();
  }

  public boolean equals(Object o) {
    return (o instanceof IndexSetComparator);
  }
}
