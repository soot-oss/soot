/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Navindra Umanee
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

package soot.shimple.internal;
import java.util.*;
import soot.util.*;

/**
 * A map with ordered sets as values, HashMap implementation.
 *
 * @author Navindra Umanee
 **/
public class SHashMultiMap<K,V> extends HashMultiMap<K,V>
{
    public SHashMultiMap()
    {
        super();
    }

    public SHashMultiMap(MultiMap<K,V> m)
    {
	super( m );
    }

    protected Set<V> newSet()
    {
	return new LinkedHashSet<V>(4);
    }
}
