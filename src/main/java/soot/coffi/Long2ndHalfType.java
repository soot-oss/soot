package soot.coffi;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 Clark Verbrugge
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

import soot.G;
import soot.Singletons;
import soot.Type;

public class Long2ndHalfType extends Type {

  public Long2ndHalfType(Singletons.Global g) {
  }

  public static Long2ndHalfType v() {
    return G.v().soot_coffi_Long2ndHalfType();
  }

  public boolean equals(Type otherType) {
    return otherType instanceof Long2ndHalfType;
  }

  @Override
  public String toString() {
    return "long2ndhalf";
  }
}
