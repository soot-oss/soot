/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot;
import soot.coffi.*;
import java.io.PrintStream;
import java.util.*;
import soot.jimple.toolkits.pointer.util.NativeHelper;
import soot.jimple.spark.sets.P2SetFactory;
import soot.jimple.toolkits.invoke.*;
import soot.jimple.toolkits.pointer.UnionFactory;
import soot.shimple.*;

/** A class to group together all the global variables in Soot. */
public class G extends Singletons 
{
    private static G instance = new G();
    public static G v() { return instance; }
    public static void reset() { instance = new G(); }

    public PrintStream out = System.out;

    public class Global {
    }

    public long coffi_BasicBlock_ids = 0;
    public Utf8_Enumeration coffi_CONSTANT_Utf8_info_e1 = new Utf8_Enumeration();
    public Utf8_Enumeration coffi_CONSTANT_Utf8_info_e2 = new Utf8_Enumeration();
    public int SETNodeLabel_uniqueId = 0;
    public HashMap SETBasicBlock_binding = new HashMap();
    public boolean ASTAnalysis_modified;
    public NativeHelper NativeHelper_helper = null;
    public P2SetFactory newSetFactory;
    public P2SetFactory oldSetFactory;
    public HashMap Parm_pairToElement = new HashMap();
    public int SparkNativeHelper_tempVar = 0;
    public int PaddleNativeHelper_tempVar = 0;
    public boolean PointsToSetInternal_warnedAlready = false;
    public HashMap MethodPAG_methodToPag = new HashMap();
    public Set MethodRWSet_allGlobals = new HashSet();
    public Set MethodRWSet_allFields = new HashSet();
    public int GeneralConstObject_counter = 0;
    public UnionFactory Union_factory = null;
    public HashMap Array2ndDimensionSymbol_pool = new HashMap();
    public Map AbstractUnit_allMapToUnnamed = Collections.unmodifiableMap(new AbstractUnitAllMapTo("<unnamed>"));
    public List Timer_outstandingTimers = new ArrayList();
    public boolean Timer_isGarbageCollecting;
    public Timer Timer_forcedGarbageCollectionTimer = new Timer("gc");
    public int Timer_count;
    public final Map ClassHierarchy_classHierarchyMap = new HashMap();
    public final Map MethodContext_map = new HashMap();

    public ShimpleFactory shimpleFactory = new DefaultShimpleFactory();

    
    public boolean ASTTransformations_modified;
    
    /*
     * 16th Feb 2006 Nomair
     * The AST transformations are unfortunately non-monotonic.
     * Infact one transformation on each iteration simply reverses the bodies of an if-else
     * To make the remaining transformations monotonic this transformation is handled with
     * a separate flag...clumsy but works
     */
    public boolean ASTIfElseFlipped;
    
    
    
    // hack for J2ME, patch provided by Stephen Chen
    // by default, this is set as false, to use SOOT with J2ME library
    // flag isJ2ME true. Type system works around Clonable, Serializeable.
    // see changes in: 
    //           soot/jimple/toolkits/typing/ClassHierarchy.java
    //           soot/jimple/toolkits/typing/TypeResolver.java
    //           soot/jimple/toolkits/typing/TypeVariable.java
    //           soot/jimple/toolkits/typing/TypeNode.java
    public final boolean isJ2ME = false;

    /*
     * Nomair A. Naeem January 15th 2006
     * Added For Dava.toolkits.AST.transformations.SuperFirstStmtHandler
     *
     * The SootMethodAddedByDava is checked by the PackManager after
     * decompiling methods for a class. If any additional methods
     * were added by the decompiler (refer to filer SuperFirstStmtHandler)
     * SootMethodsAdded ArrayList contains these method. These
     * methods are then added to the SootClass
     * 
     * Some of these newly added methods make use of an object of 
     * a static inner class DavaSuperHandler which is to be output 
     * in the decompilers
     * output. The class is marked to need a DavaSuperHandlerClass
     * by adding it into the SootClassNeedsDavaSuperHandlerClass list.
     * The DavaPrinter when printing out the class checks this list and
     * if this class's name exists in the list prints out an implementation
     * of DavSuperHandler
     */
    public boolean SootMethodAddedByDava;
    public ArrayList SootClassNeedsDavaSuperHandlerClass = new ArrayList();
    public ArrayList SootMethodsAdded = new ArrayList();
    
    //ASTMetrics Data
    public ArrayList ASTMetricsData = new ArrayList();
}

