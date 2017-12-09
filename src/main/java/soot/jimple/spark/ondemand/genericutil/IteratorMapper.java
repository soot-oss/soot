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
package soot.jimple.spark.ondemand.genericutil;

import java.util.Iterator;

public class IteratorMapper<T,U> implements Iterator<U> {

    private final Mapper<T,U> mapper;
    
    private final Iterator<T> delegate;
    
    
    public IteratorMapper(final Mapper<T,U> mapper, final Iterator<T> delegate) {
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
