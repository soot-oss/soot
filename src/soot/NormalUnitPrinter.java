/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003, 2004 Ondrej Lhotak
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
import soot.jimple.*;

/**
* UnitPrinter implementation for normal (full) Jimple, Grimp, and Baf
*/
public class NormalUnitPrinter extends LabeledUnitPrinter {
    public NormalUnitPrinter( Body body ) {
        super(body);
    }

    public void type( Type t ) { 
        handleIndent();
        String s = t==null ? "<null>"  : t.toString();
        if( t instanceof RefType ) s = Scene.v().quotedNameOf(s);
        output.append( s );
    }
    public void methodRef( SootMethodRef m ) {
        handleIndent();
        output.append( m.getSignature() );
    }
    public void fieldRef( SootFieldRef f ) { 
        handleIndent();
        output.append(f.getSignature());
    }
    public void identityRef( IdentityRef r ) {
        handleIndent();
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
    public void literal( String s ) {
        handleIndent();
        output.append( s );
    }
}


