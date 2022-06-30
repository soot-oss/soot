package soot.jimple.toolkits.thread;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.EquivalentValue;
import soot.SootClass;
import soot.SootMethod;
import soot.Value;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.Ref;
import soot.jimple.toolkits.infoflow.CallLocalityContext;
import soot.jimple.toolkits.infoflow.ClassInfoFlowAnalysis;
import soot.jimple.toolkits.infoflow.ClassLocalObjectsAnalysis;
import soot.jimple.toolkits.infoflow.InfoFlowAnalysis;
import soot.jimple.toolkits.infoflow.LocalObjectsAnalysis;
import soot.jimple.toolkits.infoflow.SmartMethodInfoFlowAnalysis;
import soot.jimple.toolkits.infoflow.SmartMethodLocalObjectsAnalysis;
import soot.jimple.toolkits.infoflow.UseFinder;
import soot.jimple.toolkits.thread.mhp.MhpTester;

// ThreadLocalObjectsAnalysis written by Richard L. Halpert, 2007-03-05
// Runs LocalObjectsAnalysis for the special case where we want to know
// if a reference is local to all threads from which it is reached.

public class ThreadLocalObjectsAnalysis extends LocalObjectsAnalysis implements IThreadLocalObjectsAnalysis {
  private static final Logger logger = LoggerFactory.getLogger(ThreadLocalObjectsAnalysis.class);
  MhpTester mhp;
  List<AbstractRuntimeThread> threads;
  InfoFlowAnalysis primitiveDfa;
  static boolean printDebug = false;

  Map valueCache;
  Map fieldCache;
  Map invokeCache;

  public ThreadLocalObjectsAnalysis(MhpTester mhp) // must include main class
  {
    super(new InfoFlowAnalysis(false, true, printDebug)); // ref-only, with inner fields
    this.mhp = mhp;
    this.threads = mhp.getThreads();
    this.primitiveDfa = new InfoFlowAnalysis(true, true, printDebug); // ref+primitive, with inner fields

    valueCache = new HashMap();
    fieldCache = new HashMap();
    invokeCache = new HashMap();
  }

  // Forces the majority of computation to take place immediately, rather than on-demand
  // might occasionally compute more than is necessary
  public void precompute() {
    for (AbstractRuntimeThread thread : threads) {
      for (Object item : thread.getRunMethods()) {
        SootMethod runMethod = (SootMethod) item;
        if (runMethod.getDeclaringClass().isApplicationClass()) {
          getClassLocalObjectsAnalysis(runMethod.getDeclaringClass());
        }
      }
    }
  }

  // override
  protected ClassLocalObjectsAnalysis newClassLocalObjectsAnalysis(LocalObjectsAnalysis loa, InfoFlowAnalysis dfa,
      UseFinder uf, SootClass sc) {
    // find the right run methods to use for threads of type sc
    List<SootMethod> runMethods = new ArrayList<SootMethod>();
    Iterator<AbstractRuntimeThread> threadsIt = threads.iterator();
    while (threadsIt.hasNext()) {
      AbstractRuntimeThread thread = threadsIt.next();
      Iterator<Object> runMethodsIt = thread.getRunMethods().iterator();
      while (runMethodsIt.hasNext()) {
        SootMethod runMethod = (SootMethod) runMethodsIt.next();
        if (runMethod.getDeclaringClass() == sc) {
          runMethods.add(runMethod);
        }
      }
    }

    return new ClassLocalObjectsAnalysis(loa, dfa, primitiveDfa, uf, sc, runMethods);
  }

  // Determines if a RefType Local or a FieldRef is Thread-Local
  public boolean isObjectThreadLocal(Value localOrRef, SootMethod sm) {
    if (threads.size() <= 1) {
      return true;
      // Pair cacheKey = new Pair(new EquivalentValue(localOrRef), sm);
      // if(valueCache.containsKey(cacheKey))
      // {
      // return ((Boolean) valueCache.get(cacheKey)).booleanValue();
      // }
    }

    if (printDebug) {
      logger.debug("- " + localOrRef + " in " + sm + " is...");
    }
    Collection<AbstractRuntimeThread> mhpThreads = mhp.getThreads();
    if (mhpThreads != null) {
      for (AbstractRuntimeThread thread : mhpThreads) {
        for (Object meth : thread.getRunMethods()) {
          SootMethod runMethod = (SootMethod) meth;

          if (runMethod.getDeclaringClass().isApplicationClass() && !isObjectLocalToContext(localOrRef, sm, runMethod)) {
            if (printDebug) {
              logger.debug("  THREAD-SHARED (simpledfa " + ClassInfoFlowAnalysis.methodCount + " smartdfa "
                  + SmartMethodInfoFlowAnalysis.counter + " smartloa " + SmartMethodLocalObjectsAnalysis.counter + ")");
            }
            // valueCache.put(cacheKey, Boolean.FALSE);
            // escapesThrough(localOrRef, sm);
            return false;
          }
        }
      }
    }
    if (printDebug) {
      logger.debug("  THREAD-LOCAL (simpledfa " + ClassInfoFlowAnalysis.methodCount + " smartdfa "
          + SmartMethodInfoFlowAnalysis.counter + " smartloa " + SmartMethodLocalObjectsAnalysis.counter + ")");// (" +
                                                                                                                // localOrRef
                                                                                                                // + " in " +
                                                                                                                // sm + ")");
    }
    // valueCache.put(cacheKey, Boolean.TRUE);
    return true;
  }

  /*
   * public boolean isFieldThreadLocal(SootField sf, SootMethod sm) // this is kind of meaningless..., if we're looking in a
   * particular method, we'd use isObjectThreadLocal { logger.debug("- Checking if " + sf + " in " + sm +
   * " is thread-local"); Iterator threadClassesIt = threadClasses.iterator(); while(threadClassesIt.hasNext()) { SootClass
   * threadClass = (SootClass) threadClassesIt.next(); if(!isFieldLocalToContext(sf, sm, threadClass)) {
   * logger.debug("  THREAD-SHARED"); return false; } } logger.debug("  THREAD-LOCAL");// (" + sf + " in " + sm + ")");
   * return true; }
   */

  public boolean hasNonThreadLocalEffects(SootMethod containingMethod, InvokeExpr ie) {
    if (threads.size() <= 1) {
      return true;
    }
    return true;
    /*
     * Pair cacheKey = new Pair(new EquivalentValue(ie), containingMethod); if(invokeCache.containsKey(cacheKey)) { return
     * ((Boolean) invokeCache.get(cacheKey)).booleanValue(); }
     *
     * logger.debug("- " + ie + " in " + containingMethod + " has "); Iterator threadsIt = threads.iterator();
     * while(threadsIt.hasNext()) { AbstractRuntimeThread thread = (AbstractRuntimeThread) threadsIt.next(); Iterator
     * runMethodsIt = thread.getRunMethods().iterator(); while(runMethodsIt.hasNext()) { SootMethod runMethod = (SootMethod)
     * runMethodsIt.next(); if( runMethod.getDeclaringClass().isApplicationClass() && hasNonLocalEffects(containingMethod,
     * ie, runMethod)) { logger.debug("THREAD-VISIBLE (simpledfa " + ClassInfoFlowAnalysis.methodCount + " smartdfa " +
     * SmartMethodInfoFlowAnalysis.counter + " smartloa " + SmartMethodLocalObjectsAnalysis.counter + ")");// (" + ie + " in
     * " + containingMethod + ")"); invokeCache.put(cacheKey, Boolean.TRUE); return true; } } } logger.debug("THREAD-PRIVATE
     * (simpledfa " + ClassInfoFlowAnalysis.methodCount + " smartdfa " + SmartMethodInfoFlowAnalysis.counter + " smartloa " +
     * SmartMethodLocalObjectsAnalysis.counter + ")");// (" + ie + " in " + containingMethod + ")");
     * invokeCache.put(cacheKey, Boolean.FALSE); return false; //
     */
  }

  /**
   * Returns a list of thread-shared sources and sinks. Returns empty list if not actually a shared value.
   */
  public List escapesThrough(Value sharedValue, SootMethod containingMethod) {
    List ret = new ArrayList();

    // The containingMethod might be called from multiple threads
    // It is possible for interestingValue to be thread-shared from some threads but not others,
    // so we must look at each thread separately.
    for (AbstractRuntimeThread thread : mhp.getThreads()) {
      // Each "abstract thread" from the MHP analysis actually represents a "Thread.start()" statement that could
      // be starting one of several different kinds of threads. We must consider each kind separately.
      for (Object meth : thread.getRunMethods()) {
        SootMethod runMethod = (SootMethod) meth;

        // We can only analyze application classes for TLO
        if (runMethod.getDeclaringClass().isApplicationClass()
            && !isObjectLocalToContext(sharedValue, containingMethod, runMethod)) {
          // This is one of the threads for which sharedValue is thread-shared
          // so now we will look for which object it escapes through
          ClassLocalObjectsAnalysis cloa = getClassLocalObjectsAnalysis(containingMethod.getDeclaringClass());
          CallLocalityContext clc = cloa.getMergedContext(containingMethod);

          // Get the method info flow analysis object
          SmartMethodInfoFlowAnalysis smifa = dfa.getMethodInfoFlowAnalysis(containingMethod);

          // Get an IFA node for our sharedValue
          EquivalentValue sharedValueEqVal;
          if (sharedValue instanceof InstanceFieldRef) {
            sharedValueEqVal = InfoFlowAnalysis.getNodeForFieldRef(containingMethod, ((FieldRef) sharedValue).getField());
          } else {
            sharedValueEqVal = new EquivalentValue(sharedValue);
          }

          // Get the sources of our interesting value
          List<EquivalentValue> sources = smifa.sourcesOf(sharedValueEqVal);
          for (EquivalentValue source : sources) {
            if (source.getValue() instanceof Ref) {
              if (clc != null && !clc.isFieldLocal(source)) // (bail out if clc is null)
              {
                ret.add(source);
                // System.out.println(sharedValue + " in " + containingMethod + " escapes through " + source);
              }
            }
          }
        }
      }
    }
    return ret;
  }
}
