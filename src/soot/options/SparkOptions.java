
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

/* THIS FILE IS AUTO-GENERATED FROM soot_options.xml. DO NOT MODIFY. */

package soot.options;
import java.util.*;

/** Option parser for Spark. */
public class SparkOptions
{
    private Map options;

    public SparkOptions( Map options ) {
        this.options = options;
    }
    
    /** Disabled --  */
    public boolean disabled() {
        return soot.PackManager.getBoolean( options, "disabled" );
    }
    
    /** Verbose --  */
    public boolean verbose() {
        return soot.PackManager.getBoolean( options, "verbose" );
    }
    
    /** Ignore Types Entirely --  */
    public boolean ignoreTypesEntirely() {
        return soot.PackManager.getBoolean( options, "ignoreTypesEntirely" );
    }
    
    /** Force Garbages Collections --  */
    public boolean forceGCs() {
        return soot.PackManager.getBoolean( options, "forceGCs" );
    }
    
    /** Pre Jimplify --  */
    public boolean preJimplify() {
        return soot.PackManager.getBoolean( options, "preJimplify" );
    }
    
    /** VTA --  */
    public boolean VTA() {
        return soot.PackManager.getBoolean( options, "VTA" );
    }
    
    /** RTA --  */
    public boolean RTA() {
        return soot.PackManager.getBoolean( options, "RTA" );
    }
    
    /** Ignore Base Objects --  */
    public boolean ignoreBaseObjects() {
        return soot.PackManager.getBoolean( options, "ignoreBaseObjects" );
    }
    
    /** Types For Sites --  */
    public boolean typesForSites() {
        return soot.PackManager.getBoolean( options, "typesForSites" );
    }
    
    /** Merge String Buffer --  */
    public boolean mergeStringBuffer() {
        return soot.PackManager.getBoolean( options, "mergeStringBuffer" );
    }
    
    /** Simulate Natives --  */
    public boolean simulateNatives() {
        return soot.PackManager.getBoolean( options, "simulateNatives" );
    }
    
    /** Simple Edges Bidirectional --  */
    public boolean simpleEdgesBidirectional() {
        return soot.PackManager.getBoolean( options, "simpleEdgesBidirectional" );
    }
    
    /** On Fly Call Graph --  */
    public boolean onFlyCallGraph() {
        return soot.PackManager.getBoolean( options, "onFlyCallGraph" );
    }
    
    /** Parms As Fields --  */
    public boolean parmsAsFields() {
        return soot.PackManager.getBoolean( options, "parmsAsFields" );
    }
    
    /** Returns As Fields --  */
    public boolean returnsAsFields() {
        return soot.PackManager.getBoolean( options, "returnsAsFields" );
    }
    
    /** Simplify Offline --  */
    public boolean simplifyOffline() {
        return soot.PackManager.getBoolean( options, "simplifyOffline" );
    }
    
    /** Simplify SCCs --  */
    public boolean simplifySCCs() {
        return soot.PackManager.getBoolean( options, "simplifySCCs" );
    }
    
    /** Ignore Types For SCCs --  */
    public boolean ignoreTypesForSCCs() {
        return soot.PackManager.getBoolean( options, "ignoreTypesForSCCs" );
    }
    
    /** Dump HTML --  */
    public boolean dumpHTML() {
        return soot.PackManager.getBoolean( options, "dumpHTML" );
    }
    
    /** Dump PAG --  */
    public boolean dumpPAG() {
        return soot.PackManager.getBoolean( options, "dumpPAG" );
    }
    
    /** Dump Solution --  */
    public boolean dumpSolution() {
        return soot.PackManager.getBoolean( options, "dumpSolution" );
    }
    
    /** Topological Sort --  */
    public boolean topoSort() {
        return soot.PackManager.getBoolean( options, "topoSort" );
    }
    
    /** Dump Types --  */
    public boolean dumpTypes() {
        return soot.PackManager.getBoolean( options, "dumpTypes" );
    }
    
    /** Class Method Var --  */
    public boolean classMethodVar() {
        return soot.PackManager.getBoolean( options, "classMethodVar" );
    }
    
    /** Dump Answer --  */
    public boolean dumpAnswer() {
        return soot.PackManager.getBoolean( options, "dumpAnswer" );
    }
    
    /** Trim Invoke Graph --  */
    public boolean trimInvokeGraph() {
        return soot.PackManager.getBoolean( options, "trimInvokeGraph" );
    }
    
    /** Add Tags --  */
    public boolean addTags() {
        return soot.PackManager.getBoolean( options, "addTags" );
    }
    
    public static final int propagator_iter = 1;
    public static final int propagator_worklist = 2;
    public static final int propagator_merge = 3;
    public static final int propagator_alias = 4;
    public static final int propagator_none = 5;
    /** Propagator --  */
    public int propagator() {
        String s = soot.PackManager.getString( options, "propagator" );
        
        if( s.equalsIgnoreCase( "iter" ) )
            return propagator_iter;
        
        if( s.equalsIgnoreCase( "worklist" ) )
            return propagator_worklist;
        
        if( s.equalsIgnoreCase( "merge" ) )
            return propagator_merge;
        
        if( s.equalsIgnoreCase( "alias" ) )
            return propagator_alias;
        
        if( s.equalsIgnoreCase( "none" ) )
            return propagator_none;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option propagator" );
    }
    
    public static final int setImpl_hash = 1;
    public static final int setImpl_bit = 2;
    public static final int setImpl_hybrid = 3;
    public static final int setImpl_array = 4;
    public static final int setImpl_double = 5;
    /** Set Implementation --  */
    public int setImpl() {
        String s = soot.PackManager.getString( options, "setImpl" );
        
        if( s.equalsIgnoreCase( "hash" ) )
            return setImpl_hash;
        
        if( s.equalsIgnoreCase( "bit" ) )
            return setImpl_bit;
        
        if( s.equalsIgnoreCase( "hybrid" ) )
            return setImpl_hybrid;
        
        if( s.equalsIgnoreCase( "array" ) )
            return setImpl_array;
        
        if( s.equalsIgnoreCase( "double" ) )
            return setImpl_double;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option setImpl" );
    }
    
    public static final int doubleSetOld_hash = 1;
    public static final int doubleSetOld_bit = 2;
    public static final int doubleSetOld_hybrid = 3;
    public static final int doubleSetOld_array = 4;
    /** Double Set Old --  */
    public int doubleSetOld() {
        String s = soot.PackManager.getString( options, "doubleSetOld" );
        
        if( s.equalsIgnoreCase( "hash" ) )
            return doubleSetOld_hash;
        
        if( s.equalsIgnoreCase( "bit" ) )
            return doubleSetOld_bit;
        
        if( s.equalsIgnoreCase( "hybrid" ) )
            return doubleSetOld_hybrid;
        
        if( s.equalsIgnoreCase( "array" ) )
            return doubleSetOld_array;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option doubleSetOld" );
    }
    
    public static final int doubleSetNew_hash = 1;
    public static final int doubleSetNew_bit = 2;
    public static final int doubleSetNew_hybrid = 3;
    public static final int doubleSetNew_array = 4;
    /** Double Set New --  */
    public int doubleSetNew() {
        String s = soot.PackManager.getString( options, "doubleSetNew" );
        
        if( s.equalsIgnoreCase( "hash" ) )
            return doubleSetNew_hash;
        
        if( s.equalsIgnoreCase( "bit" ) )
            return doubleSetNew_bit;
        
        if( s.equalsIgnoreCase( "hybrid" ) )
            return doubleSetNew_hybrid;
        
        if( s.equalsIgnoreCase( "array" ) )
            return doubleSetNew_array;
        
        throw new RuntimeException( "Invalid value "+s+" of phase option doubleSetNew" );
    }
    
}
        