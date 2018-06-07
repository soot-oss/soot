package soot.jimple.toolkits.thread.synchronization;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import java.util.Collection;
import java.util.Iterator;

import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.callgraph.EdgePredicate;

/**
 * A predicate that accepts edges that are not part of the class library and do not have a source statement that falls inside
 * a transaction.
 *
 * @author Richard L. Halpert
 */
public class CriticalSectionVisibleEdgesPred implements EdgePredicate {
  Collection<CriticalSection> tns;
  CriticalSection exemptTn;

  public CriticalSectionVisibleEdgesPred(Collection<CriticalSection> tns) {
    this.tns = tns;
  }

  public void setExemptTransaction(CriticalSection exemptTn) {
    this.exemptTn = exemptTn;
  }

  /** Returns true iff the edge e is wanted. */
  public boolean want(Edge e) {
    String tgtMethod = e.tgt().toString();
    String tgtClass = e.tgt().getDeclaringClass().toString();
    String srcMethod = e.src().toString();
    String srcClass = e.src().getDeclaringClass().toString();

    // Remove Deep Library Calls
    if (tgtClass.startsWith("sun.")) {
      return false;
    }
    if (tgtClass.startsWith("com.sun.")) {
      return false;
    }

    // Remove static initializers
    if (tgtMethod.endsWith("void <clinit>()>")) {
      return false;
    }

    // Remove calls to equals in the library
    if ((tgtClass.startsWith("java.") || tgtClass.startsWith("javax."))
        && e.tgt().toString().endsWith("boolean equals(java.lang.Object)>")) {
      return false;
    }

    // Remove anything in java.util
    // these calls will be treated as a non-transitive RW to the receiving object
    if (tgtClass.startsWith("java.util") || srcClass.startsWith("java.util")) {
      return false;
    }

    // Remove anything in java.lang
    // these calls will be treated as a non-transitive RW to the receiving object
    if (tgtClass.startsWith("java.lang") || srcClass.startsWith("java.lang")) {
      return false;
    }

    if (tgtClass.startsWith("java")) {
      return false; // filter out the rest!
    }

    if (e.tgt().isSynchronized()) {
      return false;
    }

    // I THINK THIS CHUNK IS JUST NOT NEEDED... TODO: REMOVE IT
    // Remove Calls from within a transaction
    // one transaction is exempt - so that we may analyze calls within it
    if (tns != null) {
      Iterator<CriticalSection> tnIt = tns.iterator();
      while (tnIt.hasNext()) {
        CriticalSection tn = tnIt.next();
        if (tn != exemptTn && tn.units.contains(e.srcStmt())) // if this method call originates inside a transaction...
        {
          return false; // ignore it
        }
      }
    }

    return true;
  }
}
