/* Clara - Compile-time Approximation of Runtime Analyses
 * Copyright (C) 2009 Eric Bodden
 * 
 * This framework uses technology from Soot, abc, JastAdd and
 * others. 
 *
 * This framework is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This framework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this compiler, in the file LESSER-GPL;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package soot.jimple.interproc.ifds;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A fixed universe of values of type E. This can be used to generate memory-efficient maps for keys of this universe.
 * The maps can be cloned very efficiently. Their keys are compared by identity. A set implementation is also provided.
 * @author Eric Bodden
 */
public class FixedUniverse<E> {
    
    protected Map<E,Integer> elemToIndex;
    protected Set<E> keySet; //cached version of the keySet

    public FixedUniverse(Collection<E> universe) {
        elemToIndex = new HashMap<E, Integer>();
        int i = 0;
        for (E e : universe) {
            elemToIndex.put(e, i++);
        }
        elemToIndex = Collections.unmodifiableMap(elemToIndex);
        keySet = elemToIndex.keySet();
    }
    
    /**
     * Returns a new map from values of the universe to int values, initialized to 0.
     */
    public FixedUniverseMap<Integer> newMap() {
        return new FixedUniverseMap<Integer>();
    }
    
    /**
     * Returns a new map from values of the universe to int values, initialized to 0.
     */
    public FixedUniverseSet newSet() {
        return new FixedUniverseSet();
    }

    public class FixedUniverseSet extends AbstractSet<E> implements Cloneable {
		private FixedUniverseMap<Boolean> m; // The backing map
		private int size;

		FixedUniverseSet() {
			m = new FixedUniverseMap<Boolean>();
			size = 0;
		}

		public void clear() {
            throw new UnsupportedOperationException("Cannot clear, because we have a fixed universe.");
		}

		public int size() {
			return m.size();
		}

		public boolean isEmpty() {
			return size==0;
		}

		public boolean contains(Object o) {
			return m.get(o)==Boolean.TRUE;
		}

		@SuppressWarnings("unchecked")
		public boolean remove(Object o) {
			if(!contains(o)) {
				return false;
			}
			m.put((E) o, null);
			size--;
			return true;
		}

		public boolean add(E e) {
			boolean added = m.put(e, Boolean.TRUE) == null;
			if(added) size++;
			return added;
		}

		public Iterator<E> iterator() {
			final Iterator<E> i = keySet.iterator();
			return new Iterator<E>() {

				E currElem;
				
				public boolean hasNext() {
					while(i.hasNext()) {
						E elem = i.next();
						currElem = elem;
						if(m.get(elem)==Boolean.TRUE) {
							return true;
						}
					}
					currElem = null;
					return false;
				}

				public E next() {
					if(currElem!=null || hasNext()) {
						return currElem;
					} else {
						throw new IllegalStateException("No next element!");
					}
				}

				public void remove() {
					m.put(currElem,null);
				}
				
			};
		}

		public Object[] toArray() {
			return new HashSet<E>(this).toArray();
		}

		public <T> T[] toArray(T[] a) {
			return new HashSet<E>(this).toArray(a);
		}

		public FixedUniverseSet clone() throws CloneNotSupportedException {
			@SuppressWarnings("unchecked")
			FixedUniverseSet clone = (FixedUniverseSet) super.clone();
			clone.m = m.clone();
			return clone;
		}
		
		@Override
		public boolean equals(Object o) {
			if(!(o instanceof FixedUniverse.FixedUniverseSet)) {
				return false;
			}
			@SuppressWarnings("unchecked")
			FixedUniverseSet other = (FixedUniverseSet) o;			
			return super.equals(other.m.equals(m));
		}
		
		@Override
		public int hashCode() {
			return m.hashCode();
		}
	}
    
    public class FixedUniverseMap<V> implements Map<E,V>, Cloneable {

        protected V[] array;
        
        @SuppressWarnings("unchecked")
		private FixedUniverseMap() {
            array = (V[]) new Object[elemToIndex.size()];
        }
        
        /** 
         * Not supported.
         * @throws UnsupportedOperationException always
         */
        public void clear() {
            throw new UnsupportedOperationException("Cannot clear, because we have a fixed universe.");
        }

        /** 
         * Returns <code>true</code> if the element is in the universe.
         */
        public boolean containsKey(Object key) { 
            return elemToIndex.containsKey(key);
        }

        /** 
         * Returns <code>true</code> if any element in the universe is mapped to this value.
         */
        public boolean containsValue(Object value) {
            for(int i=0; i<array.length; i++) {
                if(array[i].equals(value)) {
                    return true;
                }
            }
            return false;
        }

        /** 
         * {@inheritDoc}
         */
        public Set<Map.Entry<E, V>> entrySet() {
            Set<Map.Entry<E, V>> set = new HashSet<Entry<E,V>>();
            for (final E key : keySet) {
                final int index = elemToIndex.get(key);
                set.add(new Entry<E, V> () {

                    public E getKey() {
                        return key;
                    }

                    public V getValue() {
                        return array[index];
                    }

                    public V setValue(V value) {
                        V oldValue = getValue();
                        array[index] = value;
                        return oldValue;
                    }
                    
                    public String toString() {
                        return getKey() + "=" + getValue();
                    }
                }
                );
            }            
            return set;
        }

        /** 
         * Returns <code>null</code> if the key is not in the universe or its associated
         * value otherwise.
         */
        public V get(Object key) {
            if(containsKey(key)) {
                return array[elemToIndex.get(key)];
            } else {
                return null;
            }
        }

        /** 
         * Returns <code>true</code> if and only if the universe is empty.
         */
        public boolean isEmpty() {
            return elemToIndex.isEmpty();
        }

        /** 
         * Returns an unmodifiable key set (the universe).
         */
        public Set<E> keySet() {
            return keySet;
        }

        /** 
         * Associates the key with the value.
         * @return the old associated value
         * @throws IllegalArgumentException if key is not in the universe
         */
        public V put(E key, V value) {
            V oldValue = get(key);
            Integer index = elemToIndex.get(key);            
            if(index==null) {
                throw new IllegalArgumentException("Element is not in the universe!");
            }
            array[index] = value;
            return oldValue;
        }

        /** 
         * Calls {@link #put(Object, V)} repeatedly on all elements in the map.
         */
        public void putAll(Map<? extends E, ? extends V> map) {
            for (Entry<? extends E,? extends V> entry : map.entrySet()) {
                put(entry.getKey(),entry.getValue());
            }            
        }

        /** 
         * Not supported.
         * @throws UnsupportedOperationException always
         */
        public V remove(Object key) {
            throw new UnsupportedOperationException("Cannot remove, because we have a fixed universe.");
        }

        /** 
         * Returns the size of the universe.
         */
        public int size() {
            assert elemToIndex.size()==array.length;
            return elemToIndex.size();
        }

        /** 
         * Returns a list of all associated integer values. The list can contain duplicates. 
         */
        public Collection<V> values() {
            List<V> res = new ArrayList<V>();
            for(int i=0; i<array.length; i++) {
                res.add(array[i]);
            }
            return Collections.unmodifiableList(res);
        }
        
        /** 
         * Clones this map (deep copy). It is still limited to the same fixed universe.
         */
        @SuppressWarnings("unchecked")
		@Override
        public FixedUniverseMap<V> clone() throws CloneNotSupportedException {
            FixedUniverseMap<V> clone = (FixedUniverseMap<V>) super.clone();
            clone.array = (V[]) new Object[array.length];
            System.arraycopy(array, 0, clone.array, 0, array.length);
            return clone;
        }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + FixedUniverse.this.hashCode();
			result = prime * result + Arrays.hashCode(array);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			@SuppressWarnings("rawtypes")
			FixedUniverseMap other = (FixedUniverseMap) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (!Arrays.equals(array, other.array))
				return false;
			return true;
		}

		private Object getOuterType() {
			return FixedUniverse.this;
		}
		
    }

}
