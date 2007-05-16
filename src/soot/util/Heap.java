/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ondrej Lhotak
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

package soot.util;
import java.util.*;

/** A heap (priority queue) implementation.
 * @author Ondrej Lhotak
 */
public class Heap 
{ 
    public interface Keys {
        public int key(Object o);
    }
    final Keys keys;
    final ArrayList<Object> list = new ArrayList<Object>();
    final HashSet<Object> contents = new HashSet<Object>();
    private int size;
    public int size() { return size; }
    public boolean isEmpty() { return size <= 0; }
    public Heap(Keys keys) {
        this.keys = keys;
        list.add(null);
        list.add(null);
    }
    public boolean contains(Object o) {
        return contents.contains(o);
    }
    public boolean add(Object o) {
        if(!contents.add(o)) return false;
    	insert(o);
    	return true;   
    }
    private void insert(Object o) {
        size++;
        int i = size;
        while(list.size() <= size) list.add(null);
        while( i > 1 && key(parent(i)) > key(o) ) {
            list.set(i, list.get(parent(i)));
            i = parent(i);
        }
        list.set(i, o);
    }
    private int left(int i) { return 2*i; }
    private int right(int i) { return 2*i+1; }
    private int parent(int i) { return i/2; }
    private void heapify(int i) {
        int l = left(i);
        int r = right(i);
        int largest;
        if( l <= size && key(l) < key(i) ) {
            largest = l;
        } else {
            largest = i;
        }
        if( r <= size && key(r) < key(largest) ) {
            largest = r;
        }
        if( largest != i ) {
            Object iEdge = list.get(i);
            Object largestEdge = list.get(largest);
            list.set(i, largestEdge);
            list.set(largest, iEdge);
            heapify(largest);
        }
    }
    public Object min() {
        return list.get(1);
    }
    public Object removeMin() {
        if(size == 0) throw new NoSuchElementException();
        Object ret = list.get(1);
        contents.remove(ret);
        list.set(1, list.get(size));
        list.set(size, null);
        size--;
        heapify(1);
        return ret;
    }
    public void heapify() {
        for( int i = size; i > 0; i-- ) heapify(i);
    }
    private int key(Object o) { return keys.key(o); }
    private int key(int i) { return keys.key(list.get(i)); }
}

