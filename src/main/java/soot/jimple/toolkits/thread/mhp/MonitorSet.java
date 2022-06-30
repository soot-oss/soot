
package soot.jimple.toolkits.thread.mhp;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import java.util.Iterator;

import soot.toolkits.scalar.ArraySparseSet;

// *** USE AT YOUR OWN RISK ***
// May Happen in Parallel (MHP) analysis by Lin Li.
// This code should be treated as beta-quality code.
// It was written in 2003, but not incorporated into Soot until 2006.
// As such, it may contain incorrect assumptions about the usage
// of certain Soot classes.
// Some portions of this MHP analysis have been quality-checked, and are
// now used by the Transactions toolkit.
//
// -Richard L. Halpert, 2006-11-30

public class MonitorSet extends ArraySparseSet {

  // int size = 0;

  MonitorSet() {
    super();
  }

  public Object getMonitorDepth(String objName) {
    Iterator<?> it = iterator();
    while (it.hasNext()) {
      Object obj = it.next();
      if (obj instanceof MonitorDepth) {
        MonitorDepth md = (MonitorDepth) obj;
        if (md.getObjName().equals(objName)) {
          return md;
        }
      }
    }
    return null;
  }

  public MonitorSet clone() {
    MonitorSet newSet = new MonitorSet();
    newSet.union(this);
    return newSet;
  }
  /*
   * public void copy(MonitorSet dest){ System.out.println("====begin copy"); dest.clear(); Iterator iterator = iterator();
   * while (iterator.hasNext()){ Object obj = iterator.next(); if (obj instanceof MonitorDepth) {
   * System.out.println("obj: "+((MonitorDepth)obj).getObjName());
   * System.out.println("depth: "+((MonitorDepth)obj).getDepth()); } else System.out.println("obj: "+obj); if
   * (!dest.contains(obj)) dest.add(obj); else System.out.println("dest contains "+obj); }
   * System.out.println("===finish copy==="); }
   */

  /**
   * Returns the union (join) of this MonitorSet and <code>other</code>, putting result into <code>this</code>.
   */
  public void union(MonitorSet other) {

  }

  /**
   * Returns the union (join) of this MonitorSet and <code>other</code>, putting result into <code>dest</code>.
   * <code>dest</code>, <code>other</code> and <code>this</code> could be the same object.
   */
  /*
   * ublic void union(MonitorSet other, MonitorSet dest){ other.copy(dest); Iterator iterator = iterator(); while
   * (iterator.hasNext()){
   *
   * MonitorDepth md = (MonitorDepth)iterator.next(); Object obj = dest.getMonitorDepth(md.getObjName()); if ( obj == null){
   * dest.add(md); } else{ if (obj instanceof MonitorDepth){ if (md.getDepth() != ((MonitorDepth)obj).getDepth()) throw new
   * RuntimeException("Find different monitor depth at merge point!");
   *
   * } else throw new RuntimeException("MonitorSet contains non MonitorDepth element!"); }
   *
   * }
   *
   * }
   */
  public void intersection(MonitorSet other, MonitorSet dest) {
    /*
     * System.out.println("this:"); this.test(); System.out.println("other:"); other.test();
     */
    if (other.contains("&")) {

      this.copy(dest);
      // System.out.println("copy this to dest: ");
      // dest.test();
    } else if (this.contains("&")) {
      other.copy(dest);
      // System.out.println("copy other to dest: ");
      // dest.test();
    } else {
      Iterator<?> it = iterator();
      while (it.hasNext()) {
        Object o = it.next();
        if (o instanceof MonitorDepth) {
          MonitorDepth md = (MonitorDepth) o;
          Object obj = dest.getMonitorDepth(md.getObjName());
          if (obj != null) {
            if (md.getDepth() != ((MonitorDepth) obj).getDepth()) {
              throw new RuntimeException("stmt inside different monitor depth !");
            } else {
              dest.add(obj);
            }
          }
        }
      }

    }

  }

  public void test() {
    System.out.println("====MonitorSet===");
    Iterator<?> it = iterator();
    while (it.hasNext()) {
      Object obj = it.next();
      if (obj instanceof MonitorDepth) {
        MonitorDepth md = (MonitorDepth) obj;
        ;
        System.out.println("obj: " + md.getObjName());
        System.out.println("depth: " + md.getDepth());
      } else {
        System.out.println(obj);
      }
    }
    System.out.println("====MonitorSet end====");
  }

}
