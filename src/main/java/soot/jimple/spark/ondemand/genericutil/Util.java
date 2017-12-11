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

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.Permission;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Miscellaneous utility functions.
 */
public class Util {

  /** The empty {@link BitSet}. */
  public static final BitSet EMPTY_BITSET = new BitSet();

  /** Factorial */
  public static long fact(long n_) {
    long result = 1;
    for (long i = 1; i <= n_; i++)
      result *= i;
    return result;
  }

  /** Factorial */
  public static BigInteger fact(BigInteger n_) {
    BigInteger result = BigInteger.ONE;
    for (BigInteger i = BigInteger.ONE; i.compareTo(n_) <= 0; i = i.add(BigInteger.ONE))
      result = result.multiply(i);
    return result;
  }

  /**
   * Factorial on doubles; avoids overflow problems present when using integers.
   * 
   * @param n_
   *          arg on which to compute factorial
   * @return (<code>double</code> approximation to) factorial of largest
   *         positive integer <= (n_ + epsilon)
   */
  public static double fact(double n_) {
    n_ += 1e-6;
    double result = 1.0;
    for (double i = 1; i <= n_; i += 1.0)
      result *= i;
    return result;
  }

  /** Factorial */
  public static int fact(int n_) {
    int result = 1;
    for (int i = 1; i <= n_; i++)
      result *= i;
    return result;
  }

  /** Binary log: finds the smallest power k such that 2^k>=n */
  public static int binaryLogUp(int n_) {
    int k = 0;
    while ((1 << k) < n_)
      k++;
    return k;
  }

  /** Binary log: finds the smallest power k such that 2^k>=n */
  public static int binaryLogUp(long n_) {
    int k = 0;
    while ((1L << k) < n_)
      k++;
    return k;
  }

  /** Convert an int[] to a {@link String} for printing */
  public static String str(int[] ints_) {
    StringBuffer s = new StringBuffer();
    s.append("[");
    for (int i = 0; i < ints_.length; i++) {
      if (i > 0)
        s.append(", ");
      s.append(ints_[i]);
    }
    s.append("]");
    return s.toString();
  }

  public static String objArrayToString(Object[] o) {
    return objArrayToString(o, "[", "]", ", ");
  }

  public static String objArrayToString(Object[] o, String start, String end, String sep) {
    StringBuffer s = new StringBuffer();
    s.append(start);
    for (int i = 0; i < o.length; i++) {
      if (o[i] != null) {
        if (i > 0)
          s.append(sep);
        s.append(o[i].toString());
      }
    }
    s.append(end);
    return s.toString();
  }

  /** Get a {@link String} representation of a {@link Throwable}. */
  public static String str(Throwable thrown_) {
    // create a memory buffer to which to dump the trace
    ByteArrayOutputStream traceDump = new ByteArrayOutputStream();
    PrintWriter w = new PrintWriter(traceDump);
    thrown_.printStackTrace(w);
    w.close();
    return traceDump.toString();
  }

  /**
   * Test whether <em>some</em> element of the given {@link Collection}
   * satisfies the given {@link Predicate}.
   */
  public static <T> boolean forSome(Collection<T> c_, Predicate<T> p_) {
    for (T t : c_) {
      if (p_.test(t)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Test whether <em>some</em> element of the given {@link Collection}
   * satisfies the given {@link Predicate}.
   * 
   * @return The first element satisfying the predicate; otherwise null.
   */
  public static <T> T find(Collection<T> c_, Predicate<T> p_) {
    for (Iterator<T> iter = c_.iterator(); iter.hasNext();) {
      T obj = iter.next();
      if (p_.test(obj))
        return obj;
    }

    return null;
  }

  /**
   * Test whether <em>some</em> element of the given {@link Collection}
   * satisfies the given {@link Predicate}.
   * 
   * @return All the elements satisfying the predicate
   */
  public static <T> Collection<T> findAll(Collection<T> c_, Predicate<T> p_) {
    Collection<T> result = new LinkedList<T>();

    for (Iterator<T> iter = c_.iterator(); iter.hasNext();) {
      T obj = iter.next();
      if (p_.test(obj))
        result.add(obj);
    }

    return result;
  }

  /**
   * Test whether <em>all</em> elements of the given {@link Collection}
   * satisfy the given {@link Predicate}.
   */
  public static <T> boolean forAll(Collection<T> c_, Predicate<T> p_) {
    for (T t : c_) {
      if (!p_.test(t))
        return false;
    }
    return true;
  }

  /**
   * Perform an action for all elements in a collection.
   * 
   * @param c_
   *          the collection
   * @param v_
   *          the visitor defining the action
   */
  public static <T> void doForAll(Collection<T> c_, ObjectVisitor<T> v_) {
    for (Iterator<T> iter = c_.iterator(); iter.hasNext();)
      v_.visit(iter.next());
  }

  /**
   * Map a list: generate a new list with each element mapped. The new list is
   * always an {@link ArrayList}; it would have been more precise to use
   * {@link java.lang.reflect reflection} to create a list of the same type as
   * 'srcList', but reflection works really slowly in some implementations, so
   * it's best to avoid it.
   */
  public static <T, U> List<U> map(List<T> srcList, Mapper<T, U> mapper_) {
    ArrayList<U> result = new ArrayList<U>();
    for (Iterator<T> srcIter = srcList.iterator(); srcIter.hasNext();)
      result.add(mapper_.map(srcIter.next()));
    return result;
  }

  /**
   * Filter a collection: generate a new list from an existing collection,
   * consisting of the elements satisfying some predicate. The new list is
   * always an {@link ArrayList}; it would have been more precise to use
   * {@link java.lang.reflect reflection} to create a list of the same type as
   * 'srcList', but reflection works really slowly in some implementations, so
   * it's best to avoid it.
   */
  public static <T> List<T> filter(Collection<T> src_, Predicate<T> pred_) {
    ArrayList<T> result = new ArrayList<T>();
    for (Iterator<T> srcIter = src_.iterator(); srcIter.hasNext();) {
      T curElem = srcIter.next();
      if (pred_.test(curElem))
        result.add(curElem);
    }
    return result;
  }

  /**
   * Filter a collection according to some predicate, placing the result in a
   * List
   * 
   * @param src_
   *          collection to be filtered
   * @param pred_
   *          the predicate
   * @param result_
   *          the list for the result. assumed to be empty
   */
  public static <T> void filter(Collection<T> src_, Predicate<T> pred_, List<T> result_) {
    for (T t : src_) {
      if (pred_.test(t)) {
        result_.add(t);
      }
    }
  }

  /**
   * Map a set: generate a new set with each element mapped. The new set is
   * always a {@link HashSet}; it would have been more precise to use
   * {@link java.lang.reflect reflection} to create a set of the same type as
   * 'srcSet', but reflection works really slowly in some implementations, so
   * it's best to avoid it.
   */
  public static <T, U> Set<U> mapToSet(Collection<T> srcSet, Mapper<T, U> mapper_) {
    HashSet<U> result = new HashSet<U>();
    for (Iterator<T> srcIter = srcSet.iterator(); srcIter.hasNext();)
      result.add(mapper_.map(srcIter.next()));
    return result;
  }

  /*
   * Grow an int[] -- i.e. allocate a new array of the given size, with the
   * initial segment equal to this int[].
   */
  public static int[] realloc(int[] data_, int newSize_) {
    if (data_.length < newSize_) {
      int[] newData = new int[newSize_];
      System.arraycopy(data_, 0, newData, 0, data_.length);
      return newData;
    } else
      return data_;
  }

  /** Clear a {@link BitSet}. */
  public static void clear(BitSet bitSet_) {
    bitSet_.and(EMPTY_BITSET);
  }

  /** Replace all occurrences of a given substring in a given {@link String}. */
  public static String replaceAll(String str_, String sub_, String newSub_) {
    if (str_.indexOf(sub_) == -1)
      return str_;
    int subLen = sub_.length();
    int idx;
    StringBuffer result = new StringBuffer(str_);
    while ((idx = result.toString().indexOf(sub_)) >= 0)
      result.replace(idx, idx + subLen, newSub_);
    return result.toString();
  }

  /** Remove all occurrences of a given substring in a given {@link String} */
  public static String removeAll(String str_, String sub_) {
    return replaceAll(str_, sub_, "");
  }

  /** Generate strings with fully qualified names or not */
  public static final boolean FULLY_QUALIFIED_NAMES = false;

  /** Write object fields to string */
  public static String objectFieldsToString(Object obj) {
    // Temporarily disable the security manager
    SecurityManager oldsecurity = System.getSecurityManager();
    System.setSecurityManager(new SecurityManager() {
      public void checkPermission(Permission perm) {
      }
    });

    Class c = obj.getClass();
    StringBuffer buf = new StringBuffer(FULLY_QUALIFIED_NAMES ? c.getName() : removePackageName(c.getName()));
    while (c != Object.class) {
      Field[] fields = c.getDeclaredFields();

      if (fields.length > 0)
        buf = buf.append(" (");

      for (int i = 0; i < fields.length; i++) {
        // Make this field accessible
        fields[i].setAccessible(true);

        try {
          Class type = fields[i].getType();
          String name = fields[i].getName();
          Object value = fields[i].get(obj);

          // name=value : type
          buf = buf.append(name);
          buf = buf.append("=");
          buf = buf.append(value == null ? "null" : value.toString());
          buf = buf.append(" : ");
          buf = buf.append(FULLY_QUALIFIED_NAMES ? type.getName() : removePackageName(type.getName()));
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }

        buf = buf.append(i + 1 >= fields.length ? ")" : ",");
      }
      c = c.getSuperclass();
    }
    // Reinstate the security manager
    System.setSecurityManager(oldsecurity);

    return buf.toString();
  }

  /** Remove the package name from a fully qualified class name */
  public static String removePackageName(String fully_qualified_name_) {
    if (fully_qualified_name_ == null)
      return null;

    int lastdot = fully_qualified_name_.lastIndexOf('.');

    if (lastdot < 0) {
      return "";
    } else {
      return fully_qualified_name_.substring(lastdot + 1);
    }
  }

  /**
   * @return
   */
  public static int hashArray(Object[] objs) {
    // stolen from java.util.AbstractList
    int ret = 1;
    for (int i = 0; i < objs.length; i++) {
      ret = 31 * ret + (objs[i] == null ? 0 : objs[i].hashCode());
    }
    return ret;

  }

  public static boolean arrayContains(Object[] arr, Object obj, int size) {
    assert obj != null;
    for (int i = 0; i < size; i++) {
      if (arr[i] != null && arr[i].equals(obj))
        return true;
    }
    return false;
  }

  public static String toStringNull(Object o) {
    return o == null ? "" : "[" + o.toString() + "]";
  }

  /**
   * checks if two sets have a non-empty intersection
   * 
   * @param s1
   * @param s2
   * @return <code>true</code> if the sets intersect; <code>false</code>
   *         otherwise
   */
  public static <T> boolean intersecting(final Set<T> s1, final Set<T> s2) {
    return forSome(s1, new Predicate<T>() {
      public boolean test(T obj) {
        return s2.contains(obj);
      }
    });
  }

  public static boolean stringContains(String str, String subStr) {
    return str.indexOf(subStr) != -1;
  }

  public static int getInt(Integer i) {
    return (i == null) ? 0 : i;
  }

  /**
   * given the name of a class C, returns the name of the top-most enclosing
   * class of class C. For example, given A$B$C, the method returns A
   * 
   * @param typeStr
   * @return
   */
  public static String topLevelTypeString(String typeStr) {
    int dollarIndex = typeStr.indexOf('$');
    String topLevelTypeStr = dollarIndex == -1 ? typeStr : typeStr.substring(0, dollarIndex);
    return topLevelTypeStr;
  }

  public static <T> void addIfNotNull(T val, Collection<T> vals) {
    if (val != null) {
      vals.add(val);
    }
  }
  
  public static <T> List<T> pickNAtRandom(List<T> vals, int n, long seed) {
    if (vals.size() <= n) {
      return vals;
    }
    HashSet<T> elems = new HashSet<T>();
    Random rand = new Random(seed);
    for (int i = 0; i < n; i++) {
      boolean added = true;
      do {
      int randIndex = rand.nextInt(n);
      added = elems.add(vals.get(randIndex));
      } while (!added);
      
    }
    return new ArrayList<T>(elems);    
  }
} // class Util
