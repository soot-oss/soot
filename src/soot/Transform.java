/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai and Patrick Lam
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
 * Modified by the Sable Research Group and others 1997-2003.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot;

import java.util.*;
import soot.util.*;
import soot.options.Options;

/** Maintains the pair (phaseName, singleton) needed for a
 * transformation. */
public class Transform implements HasPhaseOptions
{
    final private boolean DEBUG = true;
    String phaseName;
    Transformer t;
    
    public Transform(String phaseName, Transformer t)
    {
        this.phaseName = phaseName;
        this.t = t;
    }

    public String getPhaseName() { return phaseName; }
    public Transformer getTransformer() { return t; }

    private String declaredOpts;
    private String defaultOpts;
    public String getDeclaredOptions() { 
        if( declaredOpts != null ) return declaredOpts;
        return Options.getDeclaredOptionsForPhase( phaseName );
    }
    public String getDefaultOptions() { 
        if( defaultOpts != null ) return defaultOpts;
        return Options.getDefaultOptionsForPhase( phaseName );
    }

    /** Allows user-defined phases to have options other than just enabled
     * without having to mess with the XML. 
     * Call this method with a space-separated list of options declared
     * for this Transform.  Only declared options may be passed to this
     * transform as a phase option. */
    public void setDeclaredOptions( String options ) {
        declaredOpts = options;
    }

    /** Allows user-defined phases to have options other than just
     * enabled without having to mess with the XML.  Call this method
     * with a space-separated list of option:value pairs that this
     * Transform is to use as default parameters (eg
     * `enabled:off').  */
    public void setDefaultOptions( String options ) {
        defaultOpts = options;
    }

    public void apply() {
        Map options = PhaseOptions.v().getPhaseOptions( phaseName );
        if( PhaseOptions.getBoolean( options, "enabled" ) ) {
            if( Options.v().verbose()  ) {
                G.v().out.println( "Applying phase "+phaseName+" to the scene." );
            }
        }
	if (DEBUG)
	    PhaseDumper.v().dumpBefore(getPhaseName());

        ((SceneTransformer) t).transform( phaseName, options );

	if (DEBUG)
	    PhaseDumper.v().dumpAfter(getPhaseName());
    }
    public void apply(Body b) {
        Map options = PhaseOptions.v().getPhaseOptions( phaseName );
        if( PhaseOptions.getBoolean( options, "enabled" ) ) {
            if( Options.v().verbose() ) {
                G.v().out.println( "Applying phase "+phaseName+" to "+b.getMethod()+"." );
            }
        }
	if (DEBUG)
	    PhaseDumper.v().dumpBefore(b, getPhaseName());

        ((BodyTransformer) t).transform( b, phaseName, options );

	if (DEBUG)
	    PhaseDumper.v().dumpAfter(b, getPhaseName());
    }
}
