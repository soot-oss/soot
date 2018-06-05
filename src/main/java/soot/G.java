package soot;

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

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.coffi.Utf8_Enumeration;
import soot.dava.internal.SET.SETBasicBlock;
import soot.dava.internal.SET.SETNode;
import soot.dexpler.DalvikThrowAnalysis;
import soot.jimple.spark.pag.MethodPAG;
import soot.jimple.spark.pag.Parm;
import soot.jimple.spark.sets.P2SetFactory;
import soot.jimple.toolkits.annotation.arraycheck.Array2ndDimensionSymbol;
import soot.jimple.toolkits.pointer.UnionFactory;
import soot.jimple.toolkits.pointer.util.NativeHelper;
import soot.jimple.toolkits.typing.ClassHierarchy;
import soot.toolkits.astmetrics.ClassData;
import soot.toolkits.scalar.Pair;

/** A class to group together all the global variables in Soot. */
public class G extends Singletons {

  public static interface GlobalObjectGetter {
    public G getG();

    public void reset();
  }

  public static G v() {
    return objectGetter.getG();
  }

  public static void reset() {
    objectGetter.reset();
  }

  private static GlobalObjectGetter objectGetter = new GlobalObjectGetter() {

    private G instance = new G();

    @Override
    public G getG() {
      return instance;
    }

    @Override
    public void reset() {
      instance = new G();
    }
  };

  public static void setGlobalObjectGetter(GlobalObjectGetter newGetter) {
    objectGetter = newGetter;
  }

  /**
   * Deprecated use logging via slf4j instead
   */
  @Deprecated
  public PrintStream out = System.out;

  public class Global {
  }

  public long coffi_BasicBlock_ids = 0;
  public Utf8_Enumeration coffi_CONSTANT_Utf8_info_e1 = new Utf8_Enumeration();
  public Utf8_Enumeration coffi_CONSTANT_Utf8_info_e2 = new Utf8_Enumeration();
  public int SETNodeLabel_uniqueId = 0;
  public HashMap<SETNode, SETBasicBlock> SETBasicBlock_binding = new HashMap<SETNode, SETBasicBlock>();
  public boolean ASTAnalysis_modified;
  public NativeHelper NativeHelper_helper = null;
  public P2SetFactory newSetFactory;
  public P2SetFactory oldSetFactory;
  public Map<Pair<SootMethod, Integer>, Parm> Parm_pairToElement = new HashMap<Pair<SootMethod, Integer>, Parm>();
  public int SparkNativeHelper_tempVar = 0;
  public int PaddleNativeHelper_tempVar = 0;
  public boolean PointsToSetInternal_warnedAlready = false;
  public HashMap<SootMethod, MethodPAG> MethodPAG_methodToPag = new HashMap<SootMethod, MethodPAG>();
  public Set MethodRWSet_allGlobals = new HashSet();
  public Set MethodRWSet_allFields = new HashSet();
  public int GeneralConstObject_counter = 0;
  public UnionFactory Union_factory = null;
  public HashMap<Object, Array2ndDimensionSymbol> Array2ndDimensionSymbol_pool
      = new HashMap<Object, Array2ndDimensionSymbol>();
  public List<Timer> Timer_outstandingTimers = new ArrayList<Timer>();
  public boolean Timer_isGarbageCollecting;
  public Timer Timer_forcedGarbageCollectionTimer = new Timer("gc");
  public int Timer_count;
  public final Map<Scene, ClassHierarchy> ClassHierarchy_classHierarchyMap = new HashMap<Scene, ClassHierarchy>();
  public final Map<MethodContext, MethodContext> MethodContext_map = new HashMap<MethodContext, MethodContext>();

  public DalvikThrowAnalysis interproceduralDalvikThrowAnalysis = null;

  public DalvikThrowAnalysis interproceduralDalvikThrowAnalysis() {
    if (this.interproceduralDalvikThrowAnalysis == null) {
      this.interproceduralDalvikThrowAnalysis = new DalvikThrowAnalysis(g, true);
    }
    return this.interproceduralDalvikThrowAnalysis;
  }

  public boolean ASTTransformations_modified;

  /*
   * 16th Feb 2006 Nomair The AST transformations are unfortunately non-monotonic. Infact one transformation on each
   * iteration simply reverses the bodies of an if-else To make the remaining transformations monotonic this transformation
   * is handled with a separate flag...clumsy but works
   */
  public boolean ASTIfElseFlipped;

  /*
   * Nomair A. Naeem January 15th 2006 Added For Dava.toolkits.AST.transformations.SuperFirstStmtHandler
   *
   * The SootMethodAddedByDava is checked by the PackManager after decompiling methods for a class. If any additional methods
   * were added by the decompiler (refer to filer SuperFirstStmtHandler) SootMethodsAdded ArrayList contains these method.
   * These methods are then added to the SootClass
   *
   * Some of these newly added methods make use of an object of a static inner class DavaSuperHandler which is to be output
   * in the decompilers output. The class is marked to need a DavaSuperHandlerClass by adding it into the
   * SootClassNeedsDavaSuperHandlerClass list. The DavaPrinter when printing out the class checks this list and if this
   * class's name exists in the list prints out an implementation of DavSuperHandler
   */
  public boolean SootMethodAddedByDava;
  public ArrayList<SootClass> SootClassNeedsDavaSuperHandlerClass = new ArrayList<SootClass>();
  public ArrayList<SootMethod> SootMethodsAdded = new ArrayList<SootMethod>();

  // ASTMetrics Data
  public ArrayList<ClassData> ASTMetricsData = new ArrayList<ClassData>();

  public void resetSpark() {
    // We reset SPARK the hard way.
    for (Method m : getClass().getSuperclass().getDeclaredMethods()) {
      if (m.getName().startsWith("release_soot_jimple_spark_")) {
        try {
          m.invoke(this);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
          throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
          throw new RuntimeException(e);
        }
      }
    }

    // Reset some other stuff directly in this class
    MethodPAG_methodToPag.clear();
    MethodRWSet_allFields.clear();
    MethodRWSet_allGlobals.clear();
    newSetFactory = null;
    oldSetFactory = null;
    Parm_pairToElement.clear();

    // We need to reset the virtual call resolution table
    release_soot_jimple_toolkits_callgraph_VirtualCalls();
  }

}
