/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrice Pominville
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.util;

import java.util.*;
import soot.*;

public interface Chain extends Collection
{
    public void insertAfter(List toInsert, Object point);
    public void insertAfter(Object toInsert, Object point);
    public void insertBefore(Object toInsert, Object point);
    public void swapWith(Object out, Object in);
    public boolean remove(Object u);
    public void addFirst(Object u);
    public void addLast(Object u);
    public void removeFirst();
    public void removeLast();
    public boolean follows(Object someObject, Object someReferenceObject);
    
    public Object getFirst();
    public Object getLast();
    
    public Object getSuccOf(Object point);
    public Object getPredOf(Object point);
    public Iterator snapshotIterator();
    public Iterator iterator();
    public Iterator iterator(Object u);
    public Iterator iterator(Object head, Object tail);
    public int size();   
}

