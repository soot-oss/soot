package soot.util;

import java.util.*;

public class IteratorableMap implements Map
{
    private HashMap content_map, back_map;
    private HashChain key_chain, value_chain;

    public IteratorableMap()
    {
	this( 7, 0.7f);
    }

    public IteratorableMap( int initialCapacity)
    {
	this( initialCapacity, 0.7f);
    }

    public IteratorableMap( int initialCapacity, float loadFactor)
    {
	content_map = new HashMap( initialCapacity, loadFactor);
	back_map    = new HashMap( initialCapacity, loadFactor);
	key_chain   = new HashChain();
	value_chain = new HashChain();
    }

    public void clear()
    {
	Iterator kcit = key_chain.iterator();
	while (kcit.hasNext())
	    content_map.remove( kcit.next());

	Iterator vcit = value_chain.iterator();
	while (vcit.hasNext())
	    back_map.remove( vcit.next());
	
	key_chain.clear();
	value_chain.clear();
    }
    
    public Iterator iterator()
    {
	return key_chain.iterator();
    }
    
    public boolean containsKey(Object key) 
    {
	return key_chain.contains( key);
    }
    
    public boolean containsValue(Object value)
    {
	return value_chain.contains( value);
    }
    
    public Set entrySet()
    {
	return content_map.entrySet();
    }

    public boolean equals( Object o)
    {
	if (o == this)
	    return true;

	if ((o instanceof IteratorableMap) == false)
	    return false;
	
	IteratorableMap other = (IteratorableMap) o;

	if (key_chain.equals( other.key_chain) == false)
	    return false;
	
	// check that the other has our mapping
	Iterator kcit = key_chain.iterator();
	while (kcit.hasNext()) {
	    Object ko = kcit.next();

	    if (other.content_map.get( ko) != content_map.get( ko))
		return false;
	}

	return true;
    }

    public Object get( Object key)
    {
	return content_map.get( key);
    }

    public int hashCode() 
    {
	return content_map.hashCode();
    }

    public boolean isEmpty()
    {
	return key_chain.isEmpty();
    }

    private transient Set keySet = null;
    private transient Set valueSet = null;
    private transient Collection values = null;
    
    public Set keySet()
    {
        if (keySet == null) {
            keySet = new AbstractSet() {
                public Iterator iterator() {
                    return key_chain.iterator();
                }
                public int size() {
                    return key_chain.size();
                }
                public boolean contains(Object o) {
                    return key_chain.contains(o);
                }
                public boolean remove(Object o) {
		    if (key_chain.contains(o) == false) {
			return false;
		    }

		    if (IteratorableMap.this.content_map.get( o) == null) {
			IteratorableMap.this.remove(o);
			return true;
		    }

                    return (IteratorableMap.this.remove(o) != null);
                }
                public void clear() {
		    IteratorableMap.this.clear();
                }
            };
        }
        return keySet;
    }

    public Set valueSet()
    {
        if (valueSet == null) {
            valueSet = new AbstractSet() {
                public Iterator iterator() {
                    return value_chain.iterator();
                }
                public int size() {
                    return value_chain.size();
                }
                public boolean contains(Object o) {
                    return value_chain.contains(o);
                }

                public boolean remove(Object o) {
		    if (value_chain.contains( o) == false) {
			return false;
		    }

		    HashChain c = (HashChain) IteratorableMap.this.back_map.get( o);
		    Iterator it = c.snapshotIterator();
		    while (it.hasNext()) {
			Object ko = it.next();

			if (IteratorableMap.this.content_map.get( o) == null) {
			    IteratorableMap.this.remove(ko);
			}
			else if (IteratorableMap.this.remove( ko) == null) {
			    return false;
			}
		    }
		    return true;
                }
                public void clear() {
		    IteratorableMap.this.clear();
                }
            };
        }
        return valueSet;
     }

    public Object put( Object key, Object value)
    {
	if (key_chain.contains( key)) {

	    Object old_value = content_map.get( key);

	    if (old_value == value)
		return value;

	    HashChain kc = (HashChain) back_map.get( old_value);
	    kc.remove( key);

	    if (kc.isEmpty()) {
		value_chain.remove( old_value);
		back_map.remove( old_value);
	    }

	    kc = (HashChain) back_map.get( value);
	    if (kc == null) {
		kc = new HashChain();
		back_map.put( value, kc);
		value_chain.add( value);
	    }
	    kc.add( key);

	    return old_value;

	}
	else {

	    key_chain.add(key);
	    content_map.put( key, value);
	    
	    HashChain kc = (HashChain) back_map.get( value);
	    if (kc == null) {
		kc = new HashChain();
		back_map.put( value, kc);
		value_chain.add( value);
	    }
	    kc.add( key);
	    
	    return null;
	}
    }

    public void putAll( Map t)
    {
	Iterator kit = (t instanceof IteratorableMap) ? ((IteratorableMap) t).key_chain.iterator() : t.keySet().iterator();

	while (kit.hasNext()) {
	    Object key = kit.next();
	    put( key, t.get( key));
	}
    }


    public Object remove( Object key)
    {
	if (key_chain.contains( key) == false)
	    return null;

	key_chain.remove( key);
	Object value = content_map.remove( key);
	HashChain c = (HashChain) back_map.get( value);
	c.remove( key);
	if (c.size() == 0)
	    back_map.remove( value);

	return value;
    }

    public int size()
    {
	return key_chain.size();
    }

    public Collection values()
    {
        if (values==null) {
            values = new AbstractCollection() {
                public Iterator iterator() {
		    return new Mapping_Iterator( IteratorableMap.this.key_chain, IteratorableMap.this.content_map);
                }
                public int size() {
                    return key_chain.size();
                }
                public boolean contains(Object o) {
                    return value_chain.contains(o);
                }
                public void clear() {
		    IteratorableMap.this.clear();
                }
            };
        }
        return values;
    }

    public class Mapping_Iterator implements Iterator
    {
	private Iterator it;
	private HashMap m;

	public Mapping_Iterator( HashChain c, HashMap m)
	{
	    it = c.iterator();
	    this.m = m;
	}

        public boolean hasNext() 
        {
	    return it.hasNext();
        }
            
        public Object next() throws NoSuchElementException
        {
	    return m.get( it.next());
        }

        public void remove() throws UnsupportedOperationException 
        {
	    throw new UnsupportedOperationException("You cannot remove from an Iterator on the values() for an IteratorableMap.");
        }
    }

}
