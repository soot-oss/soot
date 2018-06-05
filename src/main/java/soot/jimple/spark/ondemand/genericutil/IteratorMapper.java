package soot.jimple.spark.ondemand.genericutil;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2007 Manu Sridharan
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

import java.util.Iterator;

public class IteratorMapper<T, U> implements Iterator<U> {

  private final Mapper<T, U> mapper;

  private final Iterator<T> delegate;

  public IteratorMapper(final Mapper<T, U> mapper, final Iterator<T> delegate) {
    this.mapper = mapper;
    this.delegate = delegate;
  }

  public boolean hasNext() {
    return delegate.hasNext();
  }

  public U next() {
    // TODO Auto-generated method stub
    return mapper.map(delegate.next());
  }

  public void remove() {
    delegate.remove();
  }

}
