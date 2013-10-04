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

package soot;
import java.util.*;

/** Manages the phase options of the various soot phases. */
public class PhaseOptions {
    /** Needed for preventing infinite recursion in constructor. 
     * Termination is assured: each constructor is called exactly once.
     * Here is a case analysis.
     *   a. PackManager used first.  Then its constructor needs PhaseOptions,
            which also needs a PackManager; OK because we store the 
            PackManager being initialized in a field.
         b. PhaseOptions used first.  Then getPM() calls PackManager.v(),
            which calls the constr, which sets the .pm field here, uses
            PhaseOptions (which uses PackManager), and returns.  OK. */
    private PackManager pm;
    public void setPackManager(PackManager m) { this.pm = m; }
    PackManager getPM()
    {
        if (pm == null)
            PackManager.v();
        return pm;
    }

    public PhaseOptions( Singletons.Global g ) { }
    public static PhaseOptions v() { return G.v().soot_PhaseOptions(); }

    private final Map<HasPhaseOptions, Map<String, String>> phaseToOptionMap = new HashMap<HasPhaseOptions, Map<String, String>>();

    public Map<String, String> getPhaseOptions(String phaseName) {
        return getPhaseOptions(getPM().getPhase(phaseName));
    }

    public Map<String, String> getPhaseOptions(HasPhaseOptions phase) {
        Map<String, String> ret = phaseToOptionMap.get(phase);
        if( ret == null ) ret = new HashMap<String, String>();
        else ret = new HashMap<String, String>( ret );
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
    public static boolean getBoolean(Map<String, String> options, String name)
    {
        return options.containsKey(name) &&
            options.get(name).equals("true");
    }



    /** This method returns the value of "name" in options 
        or "" if "name" is not found. */
    public static String getString(Map<String, String> options, String name)
    {
        return options.containsKey(name) ?
            (String)options.get(name) : "";
    }



    /** This method returns the float value of "name" in options 
        or 1.0 if "name" is not found. */
    public static float getFloat(Map<String, String> options, String name)
    {
        return options.containsKey(name) ?
            new Float(options.get(name)).floatValue() : 1.0f;
    }



    /** This method returns the integer value of "name" in options 
        or 0 if "name" is not found. */
    public static int getInt(Map<String, String> options, String name)
    {
        return options.containsKey(name) ?
            new Integer(options.get(name)).intValue() : 0;
    }


    private Map<String, String> mapForPhase( String phaseName ) {
        HasPhaseOptions phase = getPM().getPhase( phaseName );
        if( phase == null ) return null;
        return mapForPhase( phase );
    }

    private Map<String, String> mapForPhase( HasPhaseOptions phase ) {
        Map<String, String> optionMap = phaseToOptionMap.get( phase );
        if( optionMap == null ) {
            phaseToOptionMap.put( phase, optionMap = new HashMap<String, String>() );
        }
        return optionMap;
    }

    private String getKey( String option ) {
        int delimLoc = option.indexOf(":");
        if (delimLoc < 0) {
            if( option.equals("on") || option.equals("off") ) return "enabled";
            return option;
        } else {
            return option.substring(0, delimLoc);
        }
    }
    private String getValue( String option ) {
        int delimLoc = option.indexOf(":");
        if (delimLoc < 0) {
            if( option.equals("off") ) return "false";
            return "true";
        } else {
            return option.substring(delimLoc+1);
        }
    }
    private void resetRadioPack( String phaseName ) {
        for (Pack p : getPM().allPacks()) {
            if( !(p instanceof RadioScenePack) ) continue;
            if( p.get(phaseName) == null ) continue;
            for( Iterator<Transform> tIt = p.iterator(); tIt.hasNext(); ) {
                final Transform t = (Transform) tIt.next();
                setPhaseOption( t.getPhaseName(), "enabled:false" );
            }
        }
    }
    private boolean checkParentEnabled( String phaseName ) {
        if( true ) return true;
        for (Pack p : getPM().allPacks()) {
            if( getBoolean( getPhaseOptions( p ), "enabled" ) ) continue;
            for( Iterator tIt = p.iterator(); tIt.hasNext(); ) {
                final Transform t = (Transform) tIt.next();
                if( t.getPhaseName().equals( phaseName ) ) {
                    G.v().out.println( "Attempt to set option for phase "+phaseName+" of disabled pack "+p.getPhaseName() );
                    return false;

                }
            }
        }
        return true;
    }
    public boolean setPhaseOption( String phaseName, String option ) {
        HasPhaseOptions phase = getPM().getPhase( phaseName );
        if( phase == null ) {
            G.v().out.println( "Option "+option+" given for nonexistent"
                    +" phase "+phaseName );
            return false;
        }
        return setPhaseOption( phase, option );
    }
    public boolean setPhaseOption( HasPhaseOptions phase, String option ) {
        Map<String, String> optionMap = mapForPhase( phase );
        if( !checkParentEnabled( phase.getPhaseName() ) ) return false;
        if( optionMap == null ) {
            G.v().out.println( "Option "+option+" given for nonexistent"
                    +" phase "+phase.getPhaseName() );
            return false;
        }
        String key = getKey( option );
        if( key.equals( "enabled" ) && getValue( option ).equals( "true" ) ) {
            resetRadioPack( phase.getPhaseName() );
        }
        if( declaresOption( phase, key ) ) {
            optionMap.put( key, getValue( option ) );
            return true;
        }
        G.v().out.println( "Invalid option "+option+" for phase "+phase.getPhaseName() );
        return false;
    }

    private boolean declaresOption( String phaseName, String option ) {
        HasPhaseOptions phase = getPM().getPhase( phaseName );
        return declaresOption( phase, option );
    }
    private boolean declaresOption( HasPhaseOptions phase, String option ) {
        String declareds = phase.getDeclaredOptions();
        for( StringTokenizer st = new StringTokenizer( declareds );
                st.hasMoreTokens(); ) {
            if( st.nextToken().equals( option ) ) {
                return true;
            }
        }
        return false;
    }

    public void setPhaseOptionIfUnset( String phaseName, String option ) {
        Map<String, String> optionMap = mapForPhase( phaseName );
        if( optionMap == null )
            throw new RuntimeException( "No such phase "+phaseName );
        if( optionMap.containsKey( getKey( option ) ) ) return;
        if( !declaresOption( phaseName, getKey( option ) ) )
            throw new RuntimeException( "No option "+option+" for phase "+phaseName );
        optionMap.put( getKey( option ), getValue( option ) );
    }

}
