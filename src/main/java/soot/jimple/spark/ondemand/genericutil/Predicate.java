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

/**
 * Interface for defining an arbitrary predicate on {@link Object}s.
 */
public abstract class Predicate<T> {
  public static final Predicate FALSE = new Predicate() {

    @Override
    public boolean test(Object obj_) {
      return false;
    }
  };

  public static final Predicate TRUE = FALSE.not();

  @SuppressWarnings("unchecked")
  public static <T> Predicate<T> truePred() {
    return (Predicate<T>) TRUE;
  }

  @SuppressWarnings("unchecked")
  public static <T> Predicate<T> falsePred() {
    return (Predicate<T>) FALSE;
  }

  /** Test whether an {@link Object} satisfies this {@link Predicate} */
  public abstract boolean test(T obj_);

  /** Return a predicate that is a negation of this predicate */
  public Predicate<T> not() {
    final Predicate<T> originalPredicate = this;
    return new Predicate<T>() {
      public boolean test(T obj_) {
        return !originalPredicate.test(obj_);
      }
    };
  }

  /**
   * Return a predicate that is a conjunction of this predicate and another predicate
   */
  public Predicate<T> and(final Predicate<T> conjunct_) {
    final Predicate<T> originalPredicate = this;
    return new Predicate<T>() {
      public boolean test(T obj_) {
        return originalPredicate.test(obj_) && conjunct_.test(obj_);
      }
    };
  }

  /**
   * Return a predicate that is a conjunction of this predicate and another predicate
   */
  public Predicate<T> or(final Predicate<T> disjunct_) {
    final Predicate<T> originalPredicate = this;
    return new Predicate<T>() {
      public boolean test(T obj_) {
        return originalPredicate.test(obj_) || disjunct_.test(obj_);
      }
    };
  }
} // class Predicate
