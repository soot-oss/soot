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

package soot.options;
import java.util.*;

import soot.*;

/** Soot command-line options parser base class.
 * @author Ondrej Lhotak
 */

abstract class OptionsBase {
    private String pad( int initial, String opts, int tab, String desc ) {
        StringBuffer b = new StringBuffer();
        for( int i = 0; i < initial; i++ ) b.append( " " );
        b.append(opts);
        int i;
        if( tab <= opts.length() ) {
            b.append( "\n" );
            i = 0;
        } else i = opts.length()+initial;
        for( ; i <= tab; i++ ) {
            b.append(" ");
        }
        for( StringTokenizer t = new StringTokenizer( desc );
                t.hasMoreTokens(); )  {
            String s = t.nextToken();
            if( i + s.length() > 78 ) {
                b.append( "\n" );
                i = 0;
                for( ; i <= tab; i++ ) {
                    b.append(" ");
                }
            }
            b.append( s );
            b.append( " " );
            i += s.length() + 1;
        }
        b.append( "\n" );
        return b.toString();
    }

    protected String padOpt( String opts, String desc ) {
        return pad( 1, opts, 30, desc );
    }

    protected String padVal( String vals, String desc ) {
        return pad( 4, vals, 32, desc );
    }

    protected String getPhaseUsage() {
        StringBuffer b = new StringBuffer();
        b.append( "\nPhases and phase options:\n" );
        for (Pack p : PackManager.v().allPacks()) {
            b.append( padOpt( p.getPhaseName(), p.getDeclaredOptions() ) );
            for( Iterator phIt = p.iterator(); phIt.hasNext(); ) {
                final HasPhaseOptions ph = (HasPhaseOptions) phIt.next();
                b.append( padVal( ph.getPhaseName(), ph.getDeclaredOptions() ) );
            }
        }
        return b.toString();
    }

    private final LinkedList<String> options = new LinkedList<String>();
    protected void pushOptions( String s ) {
        options.addFirst( s );
    }

    protected boolean hasMoreOptions() { return !options.isEmpty(); }
    protected String nextOption() { return options.removeFirst(); }

    protected LinkedList classes = new LinkedList();
    public LinkedList classes() { return classes; }

    public boolean setPhaseOption( String phase, String option ) {
        return PhaseOptions.v().processPhaseOptions( phase, option );
    }

}
  
