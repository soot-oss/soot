/* Soot - a J*va Optimization Framework
 * Copyright (C) 2007 Manu Sridharan
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package soot.jimple.spark.ondemand;

import soot.jimple.spark.ondemand.genericutil.ImmutableStack;
import soot.jimple.spark.pag.AllocNode;

public class AllocAndContext {

  public final AllocNode alloc;

  public final ImmutableStack<Integer> context;

  public AllocAndContext(AllocNode alloc, ImmutableStack<Integer> context) {
    this.alloc = alloc;
    this.context = context;
  }

  public String toString() {
    return alloc + ", context " + context;
  }

  public int hashCode() {
    final int PRIME = 31;
    int result = 1;
    result = PRIME * result + alloc.hashCode();
    result = PRIME * result + context.hashCode();
    return result;
  }

  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final AllocAndContext other = (AllocAndContext) obj;
    if (!alloc.equals(other.alloc))
      return false;
    if (!context.equals(other.context))
      return false;
    return true;
  }
}