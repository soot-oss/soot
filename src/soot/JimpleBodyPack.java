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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot;

import soot.jimple.*;
import java.util.*;
import soot.toolkits.scalar.*;
import soot.jimple.toolkits.scalar.*;
import soot.jimple.toolkits.typing.*;
import soot.jimple.toolkits.base.*;


/** A wrapper object for a pack of optimizations.
 * Provides chain-like operations, except that the key is the phase name.
 * This is a specific one for the very messy jb phase. */
public class JimpleBodyPack extends BodyPack
{
    public JimpleBodyPack() {
        super("jb");
    }


    /** Applies the transformations corresponding to the given options. */
    public void applyPhaseOptions(JimpleBody b, Map options) 
    { 
        boolean noSplitting = PackManager.getBoolean(options, "no-splitting");
        boolean noTyping = PackManager.getBoolean(options, "no-typing");
        boolean aggregateAllLocals = PackManager.getBoolean(options, "aggregate-all-locals");
        boolean noAggregating = PackManager.getBoolean(options, "no-aggregating");
        boolean useOriginalNames = PackManager.getBoolean(options, "use-original-names");
        boolean usePacking = PackManager.getBoolean(options, "pack-locals");
        boolean noCopyPropagating = PackManager.getBoolean(options, "no-cp");
        
        boolean noNopElimination = PackManager.getBoolean(options, "no-nop-elimination");
        boolean noUcElimination = PackManager.getBoolean(options, "no-unreachable-code-elimination");

        boolean verbatim = PackManager.getBoolean(options, "verbatim");

        if (verbatim)
            return;

        if(useOriginalNames)
            PackManager.v().setPhaseOptionIfUnset( "jb.lns", "only-stack-locals");

        
        if(!noSplitting)
        {
            if(Main.v().opts.time())
                Timers.v().splitTimer.start();

            PackManager.v().getTransform( "jb.ls" ).apply( b );

            if(Main.v().opts.time())
                Timers.v().splitTimer.end();

            if(!noTyping)
            {
	        if(aggregateAllLocals)
		{
                    PackManager.v().getTransform( "jb.a" ).apply( b );
                    PackManager.v().getTransform( "jb.ule" ).apply( b );
		}
		else if (!noAggregating)
		{
                    PackManager.v().getTransform( "jb.asv" ).apply( b );
                    PackManager.v().getTransform( "jb.ule" ).apply( b );
		}

                if(Main.v().opts.time())
                    Timers.v().assignTimer.start();

                PackManager.v().getTransform( "jb.tr" ).apply( b );
		
                if(Main.v().opts.time())
                    Timers.v().assignTimer.end();

		if(typingFailed(b))
		  throw new RuntimeException("type inference failed!");
            }
        }
        
        
        if(aggregateAllLocals)
        {
            PackManager.v().getTransform( "jb.a" ).apply( b );
            PackManager.v().getTransform( "jb.ule" ).apply( b );
        }
        else if(!noAggregating)
        {
            PackManager.v().getTransform( "jb.asv" ).apply( b );
            PackManager.v().getTransform( "jb.ule" ).apply( b );
        }

        if(!useOriginalNames)
            PackManager.v().getTransform( "jb.lns" ).apply( b );
        else
        {   
            PackManager.v().getTransform( "jb.ulp" ).apply( b );
            PackManager.v().getTransform( "jb.lns" ).apply( b );
        }

        if(!noCopyPropagating)
        {
            PackManager.v().getTransform( "jb.cp" ).apply( b );
            PackManager.v().getTransform( "jb.dae" ).apply( b );
            PackManager.v().getTransform( "jb.cp-ule" ).apply( b );
        }
        
        if(usePacking)
        {
            PackManager.v().getTransform( "jb.lp" ).apply( b );
        }

        if(!noNopElimination)
            PackManager.v().getTransform( "jb.ne" ).apply( b );

        if (!noUcElimination)
            PackManager.v().getTransform( "jb.uce" ).apply( b );
                    
        if(soot.Main.v().opts.time())
            Timers.v().stmtCount += b.getUnits().size();
    }

    private boolean typingFailed(JimpleBody b)
    {
        // Check to see if any locals are untyped
        {
            Iterator localIt = b.getLocals().iterator();

            while(localIt.hasNext())
            {
                Local l = (Local) localIt.next();

                  if(l.getType().equals(UnknownType.v()) ||
                    l.getType().equals(ErroneousType.v()))
                {
		  return true;
                }
            }
        }
        
        return false;
    }



    public void apply(Body b)
    {
        applyPhaseOptions( (JimpleBody) b,
                PackManager.v().getPhaseOptions( getPhaseName() ) );
    }
}
