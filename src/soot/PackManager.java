/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot;
import java.util.*;
import soot.jimple.toolkits.invoke.*;
import soot.jimple.toolkits.base.*;
import soot.grimp.toolkits.base.*;
import soot.baf.toolkits.base.*;
import soot.jimple.toolkits.typing.*;
import soot.jimple.toolkits.scalar.*;
import soot.jimple.toolkits.scalar.pre.*;
import soot.options.Options;
import soot.toolkits.scalar.*;
import soot.jimple.spark.SparkTransformer;

/** Manages the Packs containing the various phases and their options. */
public class PackManager {
    private static PackManager constant = new PackManager();

    private Map phaseToOptionMap = new HashMap();

    private Map packNameToPack = new HashMap();

    public static PackManager v() {
        return constant;
    }

    private void addPack( Pack p ) {
        packNameToPack.put( p.getPhaseName(), p );
    }

    private PackManager() {
        Pack p;

        // Jimple body creation
        addPack(p = new JimpleBodyPack());
        {
            p.add(new Transform("jb.ls", LocalSplitter.v()));
            p.add(new Transform("jb.a", Aggregator.v()));
            p.add(new Transform("jb.asv", Aggregator.v()));
            p.add(new Transform("jb.ule", UnusedLocalEliminator.v()));
            p.add(new Transform("jb.tr", TypeAssigner.v()));
            p.add(new Transform("jb.lns", LocalNameStandardizer.v()));
            p.add(new Transform("jb.ulp", LocalPacker.v()));
            p.add(new Transform("jb.cp", CopyPropagator.v()));
            p.add(new Transform("jb.dae", DeadAssignmentEliminator.v()));
            p.add(new Transform("jb.cp-ule", UnusedLocalEliminator.v()));
            p.add(new Transform("jb.lp", LocalPacker.v()));
            p.add(new Transform("jb.ne", NopEliminator.v()));
            p.add(new Transform("jb.uce", UnreachableCodeEliminator.v()));
        }

        // Grimp body creation
        addPack(p = new GrimpBodyPack());
        {
            p.add(new Transform("gb.a", Aggregator.v()));
            p.add(new Transform("gb.asv1", Aggregator.v()));
            p.add(new Transform("gb.cf", ConstructorFolder.v()));
            p.add(new Transform("gb.asv2", Aggregator.v()));
            p.add(new Transform("gb.ule", UnusedLocalEliminator.v()));
        }

        // Baf body creation
        addPack(p = new BodyPack("bb"));
        {
            p.add(new Transform("bb.lso", LoadStoreOptimizer.v()));
            p.add(new Transform("bb.pho", PeepholeOptimizer.v()));
            p.add(new Transform("bb.ule", UnusedLocalEliminator.v()));
            p.add(new Transform("bb.lp", LocalPacker.v()));
        }

        // Shimple transformation pack
        addPack(p = new BodyPack("stp"));

        // Shimple optimization pack (-O)
        addPack(p = new BodyPack("sop"));

        // Jimple transformation pack
        addPack(p = new BodyPack("jtp"));

        // Jimple optimization pack (-O)
        addPack(p = new BodyPack("jop"));
        {
            p.add(new Transform("jop.cse", CommonSubexpressionEliminator.v()));
            p.add(new Transform("jop.bcm", BusyCodeMotion.v()));
            p.add(new Transform("jop.lcm", LazyCodeMotion.v()));
            p.add(new Transform("jop.cp", CopyPropagator.v()));
            p.add(new Transform("jop.cpf", ConstantPropagatorAndFolder.v()));
            p.add(new Transform("jop.cbf", ConditionalBranchFolder.v()));
            p.add(new Transform("jop.dae", DeadAssignmentEliminator.v()));
            p.add(new Transform("jop.uce1", UnreachableCodeEliminator.v()));
            p.add(new Transform("jop.ubf1", UnconditionalBranchFolder.v()));
            p.add(new Transform("jop.uce2", UnreachableCodeEliminator.v()));
            p.add(new Transform("jop.ubf2", UnconditionalBranchFolder.v()));
            p.add(new Transform("jop.ule", UnusedLocalEliminator.v()));
        }

        // Jimple annotation pack
        addPack(p = new BodyPack("jap"));

        // Call graph pack
        addPack(p = new ScenePack("cg"));
        {
        }

        // Whole-Jimple transformation pack (--app)
        addPack(p = new ScenePack("wjtp"));
        {
            p.add(new Transform("wjtp.Spark", SparkTransformer.v()));
        }

        // Whole-Jimple Optimization pack (--app -W)
        addPack(p = new ScenePack("wjop"));
        {
            p.add(new Transform("wjop.smb", StaticMethodBinder.v()));
            p.add(new Transform("wjop.si", StaticInliner.v()));
        }

        // Whole-Shimple transformation pack (--app)
        addPack(p = new ScenePack("wstp"));

        // Whole-Shimple Optimization pack (--app -W)
        addPack(p = new ScenePack("wstp"));

        // Give another chance to do Whole-Jimple transformation
        // The RectangularArrayFinder will be put into this package.
        addPack(p = new ScenePack("wjtp2"));

        // Baf optimization pack
        addPack(p = new BodyPack("bop"));

        // Grimp optimization pack
        addPack(p = new BodyPack("gop"));

        // Code attribute tag aggregation pack
        addPack(p = new CodeAttributeGenerator());
    }

    public boolean hasPack(String phaseName) {
        Pack p = (Pack) packNameToPack.get(phaseName);
        return p != null;
    }

    public Pack getPack(String phaseName) {
        Pack p = (Pack) packNameToPack.get(phaseName);
        if (p == null)
            throw new RuntimeException(
                "tried to get nonexistant pack " + phaseName);
        return p;
    }

    public boolean hasPhase(String phaseName) {
        return getPhase(phaseName) != null;
    }

    public HasPhaseOptions getPhase(String phaseName) {
        int index = phaseName.indexOf( "." );
        if( index < 0 ) return getPack( phaseName );
        String packName = phaseName.substring(0,index);
        if( !hasPack( packName ) ) return null;
        return getPack( packName ).get( phaseName );
    }

    public Transform getTransform(String phaseName) {
        return (Transform) getPhase( phaseName );
    }

    public Map getPhaseOptions(String phaseName) {
        return getPhaseOptions(getPhase(phaseName));
    }

    public Map getPhaseOptions(HasPhaseOptions phase) {
        Map ret = (Map) phaseToOptionMap.get(phase);
        if( ret == null ) ret = new HashMap();
        else ret = new HashMap( ret );
        StringTokenizer st = new StringTokenizer( phase.getDefaultOptions() );
        while( st.hasMoreTokens() ) {
            String opt = st.nextToken();
            String key = getKey( opt );
            String value = getValue( opt );
            if( !ret.containsKey( key ) ) ret.put( key, value );
        }
        return Collections.unmodifiableMap(ret);
    }

    public boolean processPhaseOptions(String phaseName, String option) {
        StringTokenizer st = new StringTokenizer(option, ",");
        while (st.hasMoreTokens()) {
            if( !setPhaseOption( phaseName, st.nextToken() ) ) {
                return false;
            }
        }
        return true;
    }

    /** This method returns true iff key "name" is in options 
        and maps to "true". */
    public static boolean getBoolean(Map options, String name)
    {
        return options.containsKey(name) &&
            options.get(name).equals("true");
    }



    /** This method returns the value of "name" in options 
        or "" if "name" is not found. */
    public static String getString(Map options, String name)
    {
        return options.containsKey(name) ?
            (String)options.get(name) : "";
    }



    /** This method returns the float value of "name" in options 
        or 1.0 if "name" is not found. */
    public static float getFloat(Map options, String name)
    {
        return options.containsKey(name) ?
            new Float((String)options.get(name)).floatValue() : 1.0f;
    }



    /** This method returns the integer value of "name" in options 
        or 0 if "name" is not found. */
    public static int getInt(Map options, String name)
    {
        return options.containsKey(name) ?
            new Integer((String)options.get(name)).intValue() : 0;
    }


    private Map mapForPhase( String phaseName ) {
        HasPhaseOptions phase = getPhase( phaseName );
        if( phase == null ) return null;
        Map optionMap = (Map) phaseToOptionMap.get( phase );
        if( optionMap == null ) {
            phaseToOptionMap.put( phase, optionMap = new HashMap() );
        }
        return optionMap;
    }

    private String getKey( String option ) {
        int delimLoc = option.indexOf(":");
        if (delimLoc < 0) {
            return option;
        } else {
            return option.substring(0, delimLoc);
        }
    }
    private String getValue( String option ) {
        int delimLoc = option.indexOf(":");
        if (delimLoc < 0) {
            return "true";
        } else {
            return option.substring(delimLoc+1);
        }
    }
    public boolean setPhaseOption( String phaseName, String option ) {
        Map optionMap = mapForPhase( phaseName );
        if( optionMap == null ) {
            System.out.println( "Option "+option+" given for nonexistent"
                    +" phase "+phaseName );
            return false;
        }
        optionMap.put( getKey( option ), getValue( option ) );
        return true;
    }

    public void setPhaseOptionIfUnset( String phaseName, String option ) {
        Map optionMap = mapForPhase( phaseName );
        if( optionMap == null )
            throw new RuntimeException( "No such phase "+phaseName );
        if( optionMap.containsKey( getKey( option ) ) ) return;
        optionMap.put( getKey( option ), getValue( option ) );
    }
}
