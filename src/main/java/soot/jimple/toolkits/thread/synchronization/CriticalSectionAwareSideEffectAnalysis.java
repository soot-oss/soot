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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import soot.Local;
import soot.MethodOrMethodContext;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.NewExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Filter;
import soot.jimple.toolkits.callgraph.TransitiveTargets;
import soot.jimple.toolkits.pointer.CodeBlockRWSet;
import soot.jimple.toolkits.pointer.FullObjectSet;
import soot.jimple.toolkits.pointer.RWSet;
import soot.jimple.toolkits.pointer.SideEffectAnalysis;
import soot.jimple.toolkits.pointer.StmtRWSet;
import soot.jimple.toolkits.thread.EncapsulatedObjectAnalysis;
import soot.jimple.toolkits.thread.ThreadLocalObjectsAnalysis;

/**
 * Generates side-effect information from a PointsToAnalysis. Uses various heuristic rules to filter out side-effects that
 * are not visible to other threads in a Transactional program.
 */
class WholeObject {
  Type type;

  public WholeObject(Type type) {
    this.type = type;
  }

  public WholeObject() {
    this.type = null;
  }

  public String toString() {
    return "All Fields" + (type == null ? "" : " (" + type + ")");
  }

  public int hashCode() {
    if (type == null) {
      return 1;
    }
    return type.hashCode();
  }

  public boolean equals(Object o) {
    if (type == null) {
      return true;
    }
    if (o instanceof WholeObject) {
      WholeObject other = (WholeObject) o;
      if (other.type == null) {
        return true;
      } else {
        return (type == other.type);
      }
    } else if (o instanceof FieldRef) {
      return type == ((FieldRef) o).getType();
    } else if (o instanceof SootFieldRef) {
      return type == ((SootFieldRef) o).type();
    } else if (o instanceof SootField) {
      return type == ((SootField) o).getType();
    } else {
      return true;
    }
  }
}

public class CriticalSectionAwareSideEffectAnalysis {
  PointsToAnalysis pa;
  CallGraph cg;
  Map<SootMethod, CodeBlockRWSet> methodToNTReadSet = new HashMap<SootMethod, CodeBlockRWSet>();
  Map<SootMethod, CodeBlockRWSet> methodToNTWriteSet = new HashMap<SootMethod, CodeBlockRWSet>();
  int rwsetcount = 0;
  CriticalSectionVisibleEdgesPred tve;
  TransitiveTargets tt;
  TransitiveTargets normaltt;
  SideEffectAnalysis normalsea;
  Collection<CriticalSection> criticalSections;
  EncapsulatedObjectAnalysis eoa;
  ThreadLocalObjectsAnalysis tlo;

  public Vector sigBlacklist;
  public Vector sigReadGraylist;
  public Vector sigWriteGraylist;
  public Vector subSigBlacklist;

  public void findNTRWSets(SootMethod method) {
    if (methodToNTReadSet.containsKey(method) && methodToNTWriteSet.containsKey(method)) {
      return;
    }

    CodeBlockRWSet read = null;
    CodeBlockRWSet write = null;
    for (Iterator sIt = method.retrieveActiveBody().getUnits().iterator(); sIt.hasNext();) {
      final Stmt s = (Stmt) sIt.next();

      boolean ignore = false;

      // Ignore Reads/Writes inside another transaction
      if (criticalSections != null) {
        Iterator<CriticalSection> tnIt = criticalSections.iterator();
        while (tnIt.hasNext()) {
          CriticalSection tn = tnIt.next();
          if (tn.units.contains(s) || tn.prepStmt == s) {
            ignore = true;
            break;
          }
        }
      }

      if (!ignore) {
        RWSet ntr = ntReadSet(method, s);
        if (ntr != null) {
          if (read == null) {
            read = new CodeBlockRWSet();
          }
          read.union(ntr);
        }
        RWSet ntw = ntWriteSet(method, s);
        if (ntw != null) {
          if (write == null) {
            write = new CodeBlockRWSet();
          }
          write.union(ntw);
        }
        if (s.containsInvokeExpr()) {
          InvokeExpr ie = s.getInvokeExpr();
          SootMethod calledMethod = ie.getMethod();

          // if it's an invoke on certain lib methods
          if (calledMethod.getDeclaringClass().toString().startsWith("java.util")
              || calledMethod.getDeclaringClass().toString().startsWith("java.lang")) {
            // then it gets approximated
            Local base = null;
            if (ie instanceof InstanceInvokeExpr) {
              base = (Local) ((InstanceInvokeExpr) ie).getBase();
            }

            if (tlo == null || base == null || !tlo.isObjectThreadLocal(base, method)) {
              // add its approximated read set to read
              RWSet r;
              // String InvokeSig = calledMethod.getSubSignature();
              // if( InvokeSig.equals("void notify()") || InvokeSig.equals("void notifyAll()") ||
              // InvokeSig.equals("void wait()") || InvokeSig.equals("void wait(long)") || InvokeSig.equals("void
              // wait(long,int)"))
              // r = approximatedReadSet(method, s, base, true);
              // else
              r = approximatedReadSet(method, s, base, true);
              if (read == null) {
                read = new CodeBlockRWSet();
              }
              if (r != null) {
                read.union(r);
              }

              // add its approximated write set to write
              RWSet w;
              // if( InvokeSig.equals("void notify()") || InvokeSig.equals("void notifyAll()") ||
              // InvokeSig.equals("void wait()") || InvokeSig.equals("void wait(long)") || InvokeSig.equals("void
              // wait(long,int)"))
              // w = approximatedWriteSet(method, s, base, true);
              // else
              w = approximatedWriteSet(method, s, base, true);
              if (write == null) {
                write = new CodeBlockRWSet();
              }
              if (w != null) {
                write.union(w);
              }
            }
          }
        }
      }
    }
    methodToNTReadSet.put(method, read);
    methodToNTWriteSet.put(method, write);
  }

  public void setExemptTransaction(CriticalSection tn) {
    tve.setExemptTransaction(tn);
  }

  public RWSet nonTransitiveReadSet(SootMethod method) {
    findNTRWSets(method);
    return methodToNTReadSet.get(method);
  }

  public RWSet nonTransitiveWriteSet(SootMethod method) {
    findNTRWSets(method);
    return methodToNTWriteSet.get(method);
  }

  public CriticalSectionAwareSideEffectAnalysis(PointsToAnalysis pa, CallGraph cg,
      Collection<CriticalSection> criticalSections, ThreadLocalObjectsAnalysis tlo) {
    this.pa = pa;
    this.cg = cg;
    this.tve = new CriticalSectionVisibleEdgesPred(criticalSections);
    this.tt = new TransitiveTargets(cg, new Filter(tve));
    this.normaltt = new TransitiveTargets(cg, null);
    this.normalsea = new SideEffectAnalysis(pa, cg);
    this.criticalSections = criticalSections;
    this.eoa = new EncapsulatedObjectAnalysis();
    this.tlo = tlo; // can be null

    sigBlacklist = new Vector(); // Signatures of methods known to have effective read/write sets of size 0
    // Math does not have any synchronization risks, we think :-)
    /*
     * sigBlacklist.add("<java.lang.Math: double abs(double)>");
     * sigBlacklist.add("<java.lang.Math: double min(double,double)>");
     * sigBlacklist.add("<java.lang.Math: double sqrt(double)>");
     * sigBlacklist.add("<java.lang.Math: double pow(double,double)>"); //
     */
    // sigBlacklist.add("");

    sigReadGraylist = new Vector(); // Signatures of methods whose effects must be approximated
    sigWriteGraylist = new Vector();

    /*
     * sigReadGraylist.add("<java.util.Vector: boolean remove(java.lang.Object)>");
     * sigWriteGraylist.add("<java.util.Vector: boolean remove(java.lang.Object)>");
     *
     * sigReadGraylist.add("<java.util.Vector: boolean add(java.lang.Object)>");
     * sigWriteGraylist.add("<java.util.Vector: boolean add(java.lang.Object)>");
     *
     * sigReadGraylist.add("<java.util.Vector: java.lang.Object clone()>"); //
     * sigWriteGraylist.add("<java.util.Vector: java.lang.Object clone()>");
     *
     * sigReadGraylist.add("<java.util.Vector: java.lang.Object get(int)>"); //
     * sigWriteGraylist.add("<java.util.Vector: java.lang.Object get(int)>");
     *
     * sigReadGraylist.add("<java.util.Vector: java.util.List subList(int,int)>"); //
     * sigWriteGraylist.add("<java.util.Vector: java.util.List subList(int,int)>");
     *
     * sigReadGraylist.add("<java.util.List: void clear()>"); sigWriteGraylist.add("<java.util.List: void clear()>"); //
     */
    subSigBlacklist = new Vector(); // Subsignatures of methods on all objects known to have read/write sets of size 0
    /*
     * subSigBlacklist.add("java.lang.Class class$(java.lang.String)"); subSigBlacklist.add("void notify()");
     * subSigBlacklist.add("void notifyAll()"); subSigBlacklist.add("void wait()"); subSigBlacklist.add("void <clinit>()");
     * //
     */
  }

  private RWSet ntReadSet(SootMethod method, Stmt stmt) {
    if (stmt instanceof AssignStmt) {
      AssignStmt a = (AssignStmt) stmt;
      Value r = a.getRightOp();
      if (r instanceof NewExpr) {
        return null;
      }
      return addValue(r, method, stmt);
    }
    return null;
  }

  private HashMap<Stmt, RWSet> RCache = new HashMap<Stmt, RWSet>();

  public RWSet approximatedReadSet(SootMethod method, Stmt stmt, Value specialRead, boolean allFields) { // used for stmts
                                                                                                         // with method calls
                                                                                                         // where the
                                                                                                         // effect of the
                                                                                                         // method call
                                                                                                         // should be
                                                                                                         // approximated by 0
                                                                                                         // or 1 reads (plus
                                                                                                         // reads
                                                                                                         // of
                                                                                                         // all args)
    CodeBlockRWSet ret = new CodeBlockRWSet();
    if (specialRead != null) {
      if (specialRead instanceof Local) {
        Local vLocal = (Local) specialRead;
        PointsToSet base = pa.reachingObjects(vLocal);

        // Get an RWSet containing all fields
        // Set possibleTypes = base.possibleTypes();
        // for(Iterator pTypeIt = possibleTypes.iterator(); pTypeIt.hasNext(); )
        // {
        Type pType = vLocal.getType(); // (Type) pTypeIt.next();
        if (pType instanceof RefType) {
          SootClass baseTypeClass = ((RefType) pType).getSootClass();
          if (!baseTypeClass.isInterface()) {
            List<SootClass> baseClasses = Scene.v().getActiveHierarchy().getSuperclassesOfIncluding(baseTypeClass);
            if (!baseClasses.contains(RefType.v("java.lang.Exception").getSootClass())) {
              for (SootClass baseClass : baseClasses) {
                for (Iterator baseFieldIt = baseClass.getFields().iterator(); baseFieldIt.hasNext();) {
                  SootField baseField = (SootField) baseFieldIt.next();
                  if (!baseField.isStatic()) {
                    ret.addFieldRef(base, baseField);
                  }
                }
              }
            }
          }
        }
        // }

        // If desired, prune to just actually-read fields
        if (!allFields) {
          // Should actually get a list of fields of this object that are read/written
          // make fake RW set of <base, all fields> (use a special class)
          // intersect with the REAL RW set of this stmt
          CodeBlockRWSet allRW = ret;
          ret = new CodeBlockRWSet();
          RWSet normalRW;
          if (RCache.containsKey(stmt)) {
            normalRW = RCache.get(stmt);
          } else {
            normalRW = normalsea.readSet(method, stmt);
            RCache.put(stmt, normalRW);
          }
          if (normalRW != null) {
            for (Iterator fieldsIt = normalRW.getFields().iterator(); fieldsIt.hasNext();) {
              Object field = fieldsIt.next();
              if (allRW.containsField(field)) {
                PointsToSet otherBase = normalRW.getBaseForField(field);
                if (otherBase instanceof FullObjectSet) {
                  ret.addFieldRef(otherBase, field);
                } else {
                  if (base.hasNonEmptyIntersection(otherBase)) {
                    ret.addFieldRef(base, field); // should use intersection of bases!!!
                  }
                }
              }
            }
          }
        }
      } else if (specialRead instanceof FieldRef) {
        ret.union(addValue(specialRead, method, stmt));
      }
    }
    if (stmt.containsInvokeExpr()) {
      int argCount = stmt.getInvokeExpr().getArgCount();
      for (int i = 0; i < argCount; i++) {
        ret.union(addValue(stmt.getInvokeExpr().getArg(i), method, stmt));
      }
    }
    if (stmt instanceof AssignStmt) {
      AssignStmt a = (AssignStmt) stmt;
      Value r = a.getRightOp();
      ret.union(addValue(r, method, stmt));
    }
    return ret;
  }

  public RWSet readSet(SootMethod method, Stmt stmt, CriticalSection tn, Set uses) {
    boolean ignore = false;
    if (stmt.containsInvokeExpr()) {
      InvokeExpr ie = stmt.getInvokeExpr();
      SootMethod calledMethod = ie.getMethod();
      if (ie instanceof StaticInvokeExpr) {
        // ignore = false;
      } else if (ie instanceof InstanceInvokeExpr) {
        if (calledMethod.getSubSignature().startsWith("void <init>") && eoa.isInitMethodPureOnObject(calledMethod)) {
          ignore = true;
        } else if (tlo != null && !tlo.hasNonThreadLocalEffects(method, ie)) {
          ignore = true;
        }
      }
    }

    boolean inaccessibleUses = false;
    RWSet ret = new CodeBlockRWSet();
    tve.setExemptTransaction(tn);
    Iterator<MethodOrMethodContext> targets = tt.iterator(stmt);
    while (!ignore && targets.hasNext()) {
      SootMethod target = (SootMethod) targets.next();
      // if( target.isNative() ) {
      // if( ret == null ) ret = new SiteRWSet();
      // ret.setCallsNative();
      // } else
      if (target.isConcrete()) {

        // Special treatment for java.util and java.lang... their children are filtered out by the ThreadVisibleEdges filter
        // An approximation of their behavior must be performed here
        if (target.getDeclaringClass().toString().startsWith("java.util")
            || target.getDeclaringClass().toString().startsWith("java.lang")) {
          /*
           * RWSet ntr; if(stmt.getInvokeExpr() instanceof InstanceInvokeExpr) { Local base =
           * (Local)((InstanceInvokeExpr)stmt.getInvokeExpr()).getBase();
           *
           * // Add base object and args to set of possibly contributing uses at this stmt if(!inaccessibleUses) {
           * uses.add(base); int argCount = stmt.getInvokeExpr().getArgCount(); for(int i = 0; i < argCount; i++) {
           * if(addValue( stmt.getInvokeExpr().getArg(i), method, stmt ) != null) uses.add(stmt.getInvokeExpr().getArg(i)); }
           * }
           *
           * // Add base object to read set String InvokeSig = target.getSubSignature(); if(
           * InvokeSig.equals("void notify()") || InvokeSig.equals("void notifyAll()") || InvokeSig.equals("void wait()") ||
           * InvokeSig.equals("void wait(long)") || InvokeSig.equals("void wait(long,int)")) { ntr =
           * approximatedReadSet(method, stmt, base, target, true); } else { ntr = approximatedReadSet(method, stmt, base,
           * target, false); } } else { ntr = approximatedReadSet(method, stmt, null, target, false); } ret.union(ntr);
           */ } else { // note that all library functions have already been filtered out (by name) via the filter
                       // passed to the TransitiveTargets constructor.
          RWSet ntr = nonTransitiveReadSet(target);
          if (ntr != null) {
            // uses.clear();
            // inaccessibleUses = true;
            ret.union(ntr);
          }
        }
      }
    }
    RWSet ntr = ntReadSet(method, stmt);
    if (inaccessibleUses == false && ntr != null && stmt instanceof AssignStmt) {
      AssignStmt a = (AssignStmt) stmt;
      Value r = a.getRightOp();
      if (r instanceof InstanceFieldRef) {
        uses.add(((InstanceFieldRef) r).getBase());
      } else if (r instanceof StaticFieldRef) {
        uses.add(r);
      } else if (r instanceof ArrayRef) {
        uses.add(((ArrayRef) r).getBase());
      }
    }
    ret.union(ntr);

    if (stmt.containsInvokeExpr()) {
      InvokeExpr ie = stmt.getInvokeExpr();
      SootMethod calledMethod = ie.getMethod();

      // if it's an invoke on certain lib methods
      if (calledMethod.getDeclaringClass().toString().startsWith("java.util")
          || calledMethod.getDeclaringClass().toString().startsWith("java.lang")) {
        // then it gets approximated as a NTReadSet
        Local base = null;
        if (ie instanceof InstanceInvokeExpr) {
          base = (Local) ((InstanceInvokeExpr) ie).getBase();
        }

        if (tlo == null || base == null || !tlo.isObjectThreadLocal(base, method)) {
          // add its approximated read set to read
          RWSet r;
          // String InvokeSig = calledMethod.getSubSignature();
          // if( InvokeSig.equals("void notify()") || InvokeSig.equals("void notifyAll()") ||
          // InvokeSig.equals("void wait()") || InvokeSig.equals("void wait(long)") || InvokeSig.equals("void
          // wait(long,int)"))
          // r = approximatedReadSet(method, stmt, base, true);
          // else
          r = approximatedReadSet(method, stmt, base, true);
          if (r != null) {
            ret.union(r);
          }

          int argCount = stmt.getInvokeExpr().getArgCount();
          for (int i = 0; i < argCount; i++) {
            uses.add(ie.getArg(i));
          }
          if (base != null) {
            uses.add(base);
          }
        }
      }
    }

    return ret;
  }

  private RWSet ntWriteSet(SootMethod method, Stmt stmt) {
    if (stmt instanceof AssignStmt) {
      AssignStmt a = (AssignStmt) stmt;
      Value l = a.getLeftOp();
      return addValue(l, method, stmt);
    }
    return null;
  }

  private HashMap<Stmt, RWSet> WCache = new HashMap<Stmt, RWSet>();

  public RWSet approximatedWriteSet(SootMethod method, Stmt stmt, Value v, boolean allFields) { // used for stmts with method
                                                                                                // calls where the effect
                                                                                                // of
                                                                                                // the method call should be
                                                                                                // approximated by 0 or 1
                                                                                                // writes
    CodeBlockRWSet ret = new CodeBlockRWSet();
    if (v != null) {
      if (v instanceof Local) {
        Local vLocal = (Local) v;
        PointsToSet base = pa.reachingObjects(vLocal);

        // Get an RWSet containing all fields
        // Set possibleTypes = base.possibleTypes();
        // for(Iterator pTypeIt = possibleTypes.iterator(); pTypeIt.hasNext(); )
        // {
        Type pType = vLocal.getType(); // (Type) pTypeIt.next();
        if (pType instanceof RefType) {
          SootClass baseTypeClass = ((RefType) pType).getSootClass();
          if (!baseTypeClass.isInterface()) {
            List<SootClass> baseClasses = Scene.v().getActiveHierarchy().getSuperclassesOfIncluding(baseTypeClass);
            if (!baseClasses.contains(RefType.v("java.lang.Exception").getSootClass())) {
              for (SootClass baseClass : baseClasses) {
                for (Iterator baseFieldIt = baseClass.getFields().iterator(); baseFieldIt.hasNext();) {
                  SootField baseField = (SootField) baseFieldIt.next();
                  if (!baseField.isStatic()) {
                    ret.addFieldRef(base, baseField);
                  }
                }
              }
            }
          }
        }
        // }

        // If desired, prune to just actually-written fields
        if (!allFields) {
          // Should actually get a list of fields of this object that are read/written
          // make fake RW set of <base, all fields> (use a special class)
          // intersect with the REAL RW set of this stmt
          CodeBlockRWSet allRW = ret;
          ret = new CodeBlockRWSet();
          RWSet normalRW;
          if (WCache.containsKey(stmt)) {
            normalRW = WCache.get(stmt);
          } else {
            normalRW = normalsea.writeSet(method, stmt);
            WCache.put(stmt, normalRW);
          }
          if (normalRW != null) {
            for (Iterator fieldsIt = normalRW.getFields().iterator(); fieldsIt.hasNext();) {
              Object field = fieldsIt.next();
              if (allRW.containsField(field)) {
                PointsToSet otherBase = normalRW.getBaseForField(field);
                if (otherBase instanceof FullObjectSet) {
                  ret.addFieldRef(otherBase, field);
                } else {
                  if (base.hasNonEmptyIntersection(otherBase)) {
                    ret.addFieldRef(base, field); // should use intersection of bases!!!
                  }
                }
              }
            }
          }
        }
      } else if (v instanceof FieldRef) {
        ret.union(addValue(v, method, stmt));
      }
    }
    if (stmt instanceof AssignStmt) {
      AssignStmt a = (AssignStmt) stmt;
      Value l = a.getLeftOp();
      ret.union(addValue(l, method, stmt));
    }
    return ret;
  }

  public RWSet writeSet(SootMethod method, Stmt stmt, CriticalSection tn, Set uses) {
    boolean ignore = false;
    if (stmt.containsInvokeExpr()) {
      InvokeExpr ie = stmt.getInvokeExpr();
      SootMethod calledMethod = ie.getMethod();
      if (ie instanceof StaticInvokeExpr) {
        // ignore = false;
      } else if (ie instanceof InstanceInvokeExpr) {
        if (calledMethod.getSubSignature().startsWith("void <init>") && eoa.isInitMethodPureOnObject(calledMethod)) {
          ignore = true;
        } else if (tlo != null && !tlo.hasNonThreadLocalEffects(method, ie)) {
          ignore = true;
        }
      }
    }

    boolean inaccessibleUses = false;
    RWSet ret = new CodeBlockRWSet();
    tve.setExemptTransaction(tn);
    Iterator<MethodOrMethodContext> targets = tt.iterator(stmt);
    while (!ignore && targets.hasNext()) {
      SootMethod target = (SootMethod) targets.next();
      // if( target.isNative() ) {
      // if( ret == null ) ret = new SiteRWSet();
      // ret.setCallsNative();
      // } else
      if (target.isConcrete()) {
        if (target.getDeclaringClass().toString().startsWith("java.util")
            || target.getDeclaringClass().toString().startsWith("java.lang")) {
          /*
           * RWSet ntw; if(stmt.getInvokeExpr() instanceof InstanceInvokeExpr) { Local base =
           * (Local)((InstanceInvokeExpr)stmt.getInvokeExpr()).getBase();
           *
           * // Add base object to set of possibly contributing uses at this stmt if(!inaccessibleUses) uses.add(base);
           *
           * // Add base object to write set String InvokeSig = target.getSubSignature(); if(
           * InvokeSig.equals("void notify()") || InvokeSig.equals("void notifyAll()") || InvokeSig.equals("void wait()") ||
           * InvokeSig.equals("void wait(long)") || InvokeSig.equals("void wait(long,int)")) { ntw =
           * approximatedWriteSet(method, stmt, base, true); } else { ntw = approximatedWriteSet(method, stmt, base, false);
           * } } else { ntw = approximatedWriteSet(method, stmt, null, false); } ret.union(ntw);
           */
        } else {
          RWSet ntw = nonTransitiveWriteSet(target);
          if (ntw != null) {
            // inaccessibleUses = true;
            // uses.clear();
            ret.union(ntw);
          }
        }
      }
    }

    RWSet ntw = ntWriteSet(method, stmt);
    if (!inaccessibleUses && ntw != null && stmt instanceof AssignStmt) {
      AssignStmt a = (AssignStmt) stmt;
      Value l = a.getLeftOp();
      if (l instanceof InstanceFieldRef) {
        uses.add(((InstanceFieldRef) l).getBase());
      } else if (l instanceof StaticFieldRef) {
        uses.add(l);
      } else if (l instanceof ArrayRef) {
        uses.add(((ArrayRef) l).getBase());
      }
    }
    ret.union(ntw);

    if (stmt.containsInvokeExpr()) {
      InvokeExpr ie = stmt.getInvokeExpr();
      SootMethod calledMethod = ie.getMethod();

      // if it's an invoke on certain lib methods
      if (calledMethod.getDeclaringClass().toString().startsWith("java.util")
          || calledMethod.getDeclaringClass().toString().startsWith("java.lang")) {
        // then it gets approximated as a NTReadSet
        Local base = null;
        if (ie instanceof InstanceInvokeExpr) {
          base = (Local) ((InstanceInvokeExpr) ie).getBase();
        }

        if (tlo == null || base == null || !tlo.isObjectThreadLocal(base, method)) {
          // add its approximated read set to read
          RWSet w;
          // String InvokeSig = calledMethod.getSubSignature();
          // if( InvokeSig.equals("void notify()") || InvokeSig.equals("void notifyAll()") ||
          // InvokeSig.equals("void wait()") || InvokeSig.equals("void wait(long)") || InvokeSig.equals("void
          // wait(long,int)"))
          // w = approximatedWriteSet(method, stmt, base, true);
          // else
          w = approximatedWriteSet(method, stmt, base, true);
          if (w != null) {
            ret.union(w);
          }

          if (base != null) {
            uses.add(base);
          }
        }
      }
    }

    return ret;
  }

  public RWSet valueRWSet(Value v, SootMethod m, Stmt s, CriticalSection tn) {
    RWSet ret = null;

    if (tlo != null) {
      // fields/elements of local objects may be read/written w/o visible
      // side effects if the base object is local, or if the base is "this"
      // and the field itself is local (since "this" is always assumed shared)
      if (v instanceof InstanceFieldRef) {
        InstanceFieldRef ifr = (InstanceFieldRef) v;
        if (m.isConcrete() && !m.isStatic() && m.retrieveActiveBody().getThisLocal().equivTo(ifr.getBase())
            && tlo.isObjectThreadLocal(ifr, m)) {
          return null;
        } else if (tlo.isObjectThreadLocal(ifr.getBase(), m)) {
          return null;
        }
      } else if (v instanceof ArrayRef && tlo.isObjectThreadLocal(((ArrayRef) v).getBase(), m)) {
        return null;
      }
    }

    if (v instanceof InstanceFieldRef) {
      InstanceFieldRef ifr = (InstanceFieldRef) v;
      PointsToSet base = pa.reachingObjects((Local) ifr.getBase());
      ret = new StmtRWSet();
      ret.addFieldRef(base, ifr.getField());
    } else if (v instanceof StaticFieldRef) {
      StaticFieldRef sfr = (StaticFieldRef) v;
      ret = new StmtRWSet();
      ret.addGlobal(sfr.getField());
    } else if (v instanceof ArrayRef) {
      ArrayRef ar = (ArrayRef) v;
      PointsToSet base = pa.reachingObjects((Local) ar.getBase());
      ret = new StmtRWSet();
      ret.addFieldRef(base, PointsToAnalysis.ARRAY_ELEMENTS_NODE);
    } else if (v instanceof Local) {
      Local vLocal = (Local) v;
      PointsToSet base = pa.reachingObjects(vLocal);
      ret = new CodeBlockRWSet();
      CodeBlockRWSet stmtRW = new CodeBlockRWSet();
      RWSet rSet = readSet(m, s, tn, new HashSet());
      if (rSet != null) {
        stmtRW.union(rSet);
      }
      RWSet wSet = writeSet(m, s, tn, new HashSet());
      if (wSet != null) {
        stmtRW.union(wSet);
      }
      // Should actually get a list of fields of this object that are read/written
      // make fake RW set of <base, all fields> (use a special class)
      // intersect with the REAL RW set of this stmt
      for (Iterator fieldsIt = stmtRW.getFields().iterator(); fieldsIt.hasNext();) {
        Object field = fieldsIt.next();
        PointsToSet fieldBase = stmtRW.getBaseForField(field);
        if (base.hasNonEmptyIntersection(fieldBase)) {
          ret.addFieldRef(base, field); // should use intersection of bases!!!
        }
      }
    } else {
      return null;
    }
    return ret;
  }

  protected RWSet addValue(Value v, SootMethod m, Stmt s) {
    RWSet ret = null;

    if (tlo != null) {
      // fields/elements of local objects may be read/written w/o visible
      // side effects if the base object is local, or if the base is "this"
      // and the field itself is local (since "this" is always assumed shared)
      if (v instanceof InstanceFieldRef) {
        InstanceFieldRef ifr = (InstanceFieldRef) v;
        if (m.isConcrete() && !m.isStatic() && m.retrieveActiveBody().getThisLocal().equivTo(ifr.getBase())
            && tlo.isObjectThreadLocal(ifr, m)) {
          return null;
        } else if (tlo.isObjectThreadLocal(ifr.getBase(), m)) {
          return null;
        }
      } else if (v instanceof ArrayRef && tlo.isObjectThreadLocal(((ArrayRef) v).getBase(), m)) {
        return null;
      }
    }

    // if(tlo != null &&
    // (( v instanceof InstanceFieldRef && tlo.isObjectThreadLocal(((InstanceFieldRef)v).getBase(), m) ) ||
    // ( v instanceof ArrayRef && tlo.isObjectThreadLocal(((ArrayRef)v).getBase(), m) )))
    // return null;

    if (v instanceof InstanceFieldRef) {
      InstanceFieldRef ifr = (InstanceFieldRef) v;
      Local baseLocal = (Local) ifr.getBase();
      PointsToSet base = pa.reachingObjects(baseLocal);
      if (baseLocal.getType() instanceof RefType) {
        SootClass baseClass = ((RefType) baseLocal.getType()).getSootClass();
        if (Scene.v().getActiveHierarchy().isClassSubclassOfIncluding(baseClass,
            RefType.v("java.lang.Exception").getSootClass())) {
          return null;
        }
      }
      ret = new StmtRWSet();
      ret.addFieldRef(base, ifr.getField());
    } else if (v instanceof StaticFieldRef) {
      StaticFieldRef sfr = (StaticFieldRef) v;
      ret = new StmtRWSet();
      ret.addGlobal(sfr.getField());
    } else if (v instanceof ArrayRef) {
      ArrayRef ar = (ArrayRef) v;
      PointsToSet base = pa.reachingObjects((Local) ar.getBase());
      ret = new StmtRWSet();
      ret.addFieldRef(base, PointsToAnalysis.ARRAY_ELEMENTS_NODE);
    }
    return ret;
  }

  public String toString() {
    return "TransactionAwareSideEffectAnalysis: PA=" + pa + " CG=" + cg;
  }
}
