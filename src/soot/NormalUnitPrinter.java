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
 * License along with cl library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot;
import soot.jimple.*;
import java.util.*;

/**
* UnitPrinter implementation for normal (full) Jimple, Grimp, and Baf
*/
public class NormalUnitPrinter implements UnitPrinter {
    Map labels;
    String indent;
    StringBuffer output = new StringBuffer();

    NormalUnitPrinter( Map labels, String indent ) {
        this.labels = labels;
        this.indent = indent;
    }

    public void startUnit( Unit u ) { output.append(indent); }
    public void endUnit( Unit u ) {}
    public void startUnitBox( UnitBox u ) {}
    public void endUnitBox( UnitBox u ) {}
    public void startValueBox( ValueBox u ) {}
    public void endValueBox( ValueBox u ) {}

    public void literal( String s ) { output.append( s ); }
    public void newline() { output.append("\n"+indent); }
    public void local( Local l ) { output.append( l.getName() ); }
    public void type( Type t ) { output.append( t.toString() ); }
    public void method( SootMethod m ) { output.append( m.getSignature() ); }
    public void constant( Constant c ) { output.append( c.toString() ); }
    public void fieldRef( SootField f ) { 
        output.append(f.getSignature());
    }
    public void unitRef( Unit u ) {
        String label = (String) labels.get( u );
        if( label == null || label.equals( "<unnamed>" ) )
            label = "[?= "+u+"]";
        output.append(label);
    }
    public void identityRef( IdentityRef r ) {
        if( r instanceof ThisRef ) {
            literal("@this: ");
            type(r.getType());
        } else if( r instanceof ParameterRef ) {
            ParameterRef pr = (ParameterRef) r;
            literal("@parameter"+pr.getIndex()+": ");
            type(r.getType());
        } else if( r instanceof CaughtExceptionRef ) {
            literal("@caughtexception");
        } else throw new RuntimeException();
    }

    public String toString() {
        // strip trailing space
        while( output.length() > 0 
        && output.charAt( output.length()-1 ) == ' ' ) {
            output.deleteCharAt( output.length()-1 );
        }
        String ret = output.toString();
        output = new StringBuffer();
        return ret;
    }
}


