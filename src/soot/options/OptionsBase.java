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

/** Soot command-line options parser base class.
 * @author Ondrej Lhotak
 */

abstract class OptionsBase {
    public static final String PATH_SEPARATOR = System.getProperty( "path.separator" );

    private String pad( int initial, String opts, int tab, String desc ) {
        StringBuffer b = new StringBuffer();
        for( int i = 0; i < initial; i++ ) b.append( " " );
        b.append(opts);
        int i;
        if( tab <= opts.length() ) {
            b.append( "\n" );
            i = -1;
        } else i = opts.length();
        for( ; i <= tab; i++ ) {
            b.append(" ");
        }
        for( StringTokenizer t = new StringTokenizer( desc );
                t.hasMoreTokens(); )  {
            String s = t.nextToken();
            if( i + s.length() > 78 ) {
                b.append( "\n" );
                i = -1;
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

    private LinkedList options = new LinkedList();
    protected void pushOptions( String s ) {
        StringTokenizer t = new StringTokenizer( s );
        while( t.hasMoreTokens() ) options.addFirst( t.nextToken() );
    }

    protected boolean hasMoreOptions() { return !options.isEmpty(); }
    protected String nextOption() { return (String) options.removeFirst(); }

    protected LinkedList classes = new LinkedList();
    public LinkedList classes() { return classes; }

    protected boolean setPhaseOption( String phase, String option ) {
        soot.Main.processPhaseOptions( phase, option );
        return true;
    }

}
  
