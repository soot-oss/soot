
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
    
    /** verbose --  */
    public boolean verbose() {
        return soot.PackManager.getBoolean( options, "verbose" );
    }
    
    /** ignoreTypesEntirely --  */
    public boolean ignoreTypesEntirely() {
        return soot.PackManager.getBoolean( options, "ignoreTypesEntirely" );
    }
    
    /** forceGCs --  */
    public boolean forceGCs() {
        return soot.PackManager.getBoolean( options, "forceGCs" );
    }
    
    /** preJimplify --  */
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
    
    /** ignoreBaseObjects --  */
    public boolean ignoreBaseObjects() {
        return soot.PackManager.getBoolean( options, "ignoreBaseObjects" );
    }
    
    /** typesForSites --  */
    public boolean typesForSites() {
        return soot.PackManager.getBoolean( options, "typesForSites" );
    }
    
    /** mergeStringBuffer --  */
    public boolean mergeStringBuffer() {
        return soot.PackManager.getBoolean( options, "mergeStringBuffer" );
    }
    
    /** simulateNatives --  */
    public boolean simulateNatives() {
        return soot.PackManager.getBoolean( options, "simulateNatives" );
    }
    
    /** simpleEdgesBidirectional --  */
    public boolean simpleEdgesBidirectional() {
        return soot.PackManager.getBoolean( options, "simpleEdgesBidirectional" );
    }
    
    /** onFlyCallGraph --  */
    public boolean onFlyCallGraph() {
        return soot.PackManager.getBoolean( options, "onFlyCallGraph" );
    }
    
    /** parmsAsFields --  */
    public boolean parmsAsFields() {
        return soot.PackManager.getBoolean( options, "parmsAsFields" );
    }
    
    /** returnsAsFields --  */
    public boolean returnsAsFields() {
        return soot.PackManager.getBoolean( options, "returnsAsFields" );
    }
    
    /** simplifyOffline --  */
    public boolean simplifyOffline() {
        return soot.PackManager.getBoolean( options, "simplifyOffline" );
    }
    
    /** simplifySCCs --  */
    public boolean simplifySCCs() {
        return soot.PackManager.getBoolean( options, "simplifySCCs" );
    }
    
    /** ignoreTypesForSCCs --  */
    public boolean ignoreTypesForSCCs() {
        return soot.PackManager.getBoolean( options, "ignoreTypesForSCCs" );
    }
    
    /** dumpHTML --  */
    public boolean dumpHTML() {
        return soot.PackManager.getBoolean( options, "dumpHTML" );
    }
    
    /** dumpPAG --  */
    public boolean dumpPAG() {
        return soot.PackManager.getBoolean( options, "dumpPAG" );
    }
    
    /** dumpSolution --  */
    public boolean dumpSolution() {
        return soot.PackManager.getBoolean( options, "dumpSolution" );
    }
    
    /** topoSort --  */
    public boolean topoSort() {
        return soot.PackManager.getBoolean( options, "topoSort" );
    }
    
    /** dumpTypes --  */
    public boolean dumpTypes() {
        return soot.PackManager.getBoolean( options, "dumpTypes" );
    }
    
    /** classMethodVar --  */
    public boolean classMethodVar() {
        return soot.PackManager.getBoolean( options, "classMethodVar" );
    }
    
    /** dumpAnswer --  */
    public boolean dumpAnswer() {
        return soot.PackManager.getBoolean( options, "dumpAnswer" );
    }
    
    /** trimInvokeGraph --  */
    public boolean trimInvokeGraph() {
        return soot.PackManager.getBoolean( options, "trimInvokeGraph" );
    }
    
    /** addTags --  */
    public boolean addTags() {
        return soot.PackManager.getBoolean( options, "addTags" );
    }
    
    public static final int propagator_iter = 1;
    public static final int propagator_worklist = 2;
    public static final int propagator_merge = 3;
    public static final int propagator_alias = 4;
    public static final int propagator_none = 5;
    /** propagator --  */
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
    /** setImpl --  */
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
    /** doubleSetOld --  */
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
    /** doubleSetNew --  */
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
        
