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

import java.util.Arrays;

public class ImmutableStack<T> {

    private static final ImmutableStack<Object> EMPTY = new ImmutableStack<Object>(
            new Object[0]);

    private static final int MAX_SIZE = Integer.MAX_VALUE;

    public static int getMaxSize() {
        return MAX_SIZE;
    }
    @SuppressWarnings("unchecked")
    public static final <T> ImmutableStack<T> emptyStack() {
        return (ImmutableStack<T>) EMPTY;
    }

    final private T[] entries;

    private ImmutableStack(T[] entries) {
        this.entries = entries;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o != null && o instanceof ImmutableStack) {
            ImmutableStack other = (ImmutableStack) o;
            return Arrays.equals(entries, other.entries);
        }
        return false;
    }

    public int hashCode() {
        return Util.hashArray(this.entries);
    }

    @SuppressWarnings("unchecked")
    public ImmutableStack<T> push(T entry) {
        assert entry != null;
        if (MAX_SIZE == 0) {
            return emptyStack();
        }
        int size = entries.length + 1;
        T[] tmpEntries = null;
        if (size <= MAX_SIZE) {
            tmpEntries = (T[]) new Object[size];
            System.arraycopy(entries, 0, tmpEntries, 0, entries.length);
            tmpEntries[size - 1] = entry;
        } else {
            tmpEntries = (T[]) new Object[MAX_SIZE];
            System.arraycopy(entries, 1, tmpEntries, 0, entries.length - 1);
            tmpEntries[MAX_SIZE - 1] = entry;

        }
        return new ImmutableStack<T>(tmpEntries);
    }

    public T peek() {
        assert entries.length != 0;
        return entries[entries.length - 1];
    }

    @SuppressWarnings("unchecked")
    public ImmutableStack<T> pop() {
        assert entries.length != 0;
        int size = entries.length - 1;
        T[] tmpEntries = (T[]) new Object[size];
        System.arraycopy(entries, 0, tmpEntries, 0, size);
        return new ImmutableStack<T>(tmpEntries);
    }

    public boolean isEmpty() {
        return entries.length == 0;
    }

    public int size() {
        return entries.length;
    }

    public T get(int i) {
        return entries[i];
    }

    public String toString() {
        String objArrayToString = Util.objArrayToString(entries);
        assert entries.length <= MAX_SIZE : objArrayToString;
        return objArrayToString;
    }

    public boolean contains(T entry) {
        return Util.arrayContains(entries, entry, entries.length);
    }

    public boolean topMatches(ImmutableStack<T> other) {
        if (other.size() > size())
            return false;
        for (int i = other.size() - 1, j = this.size() - 1; i >= 0; i--, j--) {
            if (!other.get(i).equals(get(j)))
                return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public ImmutableStack<T> reverse() {
        T[] tmpEntries = (T[]) new Object[entries.length];
        for (int i = entries.length - 1, j = 0; i >= 0; i--, j++) {
            tmpEntries[j] = entries[i];
        }
        return new ImmutableStack<T>(tmpEntries);
    }

    @SuppressWarnings("unchecked")
    public ImmutableStack<T> popAll(ImmutableStack<T> other) {
        // TODO Auto-generated method stub
        assert topMatches(other);
        int size = entries.length - other.entries.length;
        T[] tmpEntries = (T[]) new Object[size];
        System.arraycopy(entries, 0, tmpEntries, 0, size);
        return new ImmutableStack<T>(tmpEntries);
    }

    @SuppressWarnings("unchecked")
    public ImmutableStack<T> pushAll(ImmutableStack<T> other) {
        // TODO Auto-generated method stub
        int size = entries.length + other.entries.length;
        T[] tmpEntries = null;
        if (size <= MAX_SIZE) {
            tmpEntries = (T[]) new Object[size];
            System.arraycopy(entries, 0, tmpEntries, 0, entries.length);
            System.arraycopy(other.entries, 0, tmpEntries, entries.length,
                    other.entries.length);
        } else {
            tmpEntries = (T[]) new Object[MAX_SIZE];
            // other has size at most MAX_SIZE
            // must keep all in other
            // top MAX_SIZE - other.size from this
            int numFromThis = MAX_SIZE - other.entries.length;
            System.arraycopy(entries, entries.length - numFromThis, tmpEntries, 0, numFromThis);
            System.arraycopy(other.entries, 0, tmpEntries, numFromThis, other.entries.length);            
        }
        return new ImmutableStack<T>(tmpEntries);
    }
}
