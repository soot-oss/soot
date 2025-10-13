package soot.util;

import com.google.common.collect.Iterators;

import java.util.Iterator;

public class IteratorConcatElement<T> implements Iterator<T> {

  private Iterator<T> it;
  private T element;
  private boolean shownSingleElement;

  public IteratorConcatElement(Iterator<T> it, T element) {
    this.it = it;
    this.element = element;
  }

  public static <T> Iterator<T> v(Iterator<T> it, T element) {
    if (!it.hasNext()) {
      return Iterators.singletonIterator(element);
    } else {
      return new IteratorConcatElement<T>(it, element);
    }
  }

  @Override
  public boolean hasNext() {
    boolean b = it.hasNext();
    if (!b && !shownSingleElement) {
      return true;
    }
    return b;
  }

  @Override
  public T next() {
    if (!it.hasNext()) {
      if (!shownSingleElement) {
        shownSingleElement = true;
        return element;
      }
      throw new IllegalStateException("No more elements");
    }
    return it.next();
  }

}
