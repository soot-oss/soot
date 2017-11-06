package soot.util;

/**
 * An unmodifiable version of the IterableSet class
 * 
 * @author Steven Arzt
 *
 */
public class UnmodifiableIterableSet<E> extends IterableSet<E> {
	
	public UnmodifiableIterableSet() {
		super();
	}
	
	/**
	 * Creates a new unmodifiable iterable set as a copy of an existing one
	 * @param original The original set to copy
	 */
	public UnmodifiableIterableSet(IterableSet<E> original) {
		for (E e : original)
			super.add(e);
	}
	
	@Override
	public boolean add(E o) {
		throw new RuntimeException("This set cannot be modified");
	}

	@Override
	public boolean remove(Object o) {
		throw new RuntimeException("This set cannot be modified");
	}
	
	public boolean forceRemove(Object o) {
		return super.remove(o);
	}

}
