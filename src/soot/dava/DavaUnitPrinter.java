/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
 * Copyright (C) 2004-2005 Nomair A. Naeem
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

package soot.dava;
import soot.*;
import soot.jimple.*;

/**
 * UnitPrinter implementation for Dava.
 */
public class DavaUnitPrinter extends AbstractUnitPrinter {
    public void methodRef( SootMethodRef m ) {
        handleIndent();
        output.append( m.name() );
    }
    public void fieldRef( SootFieldRef f ) { 
        handleIndent();
        output.append(f.name());
    }
    public void identityRef( IdentityRef r ) {
        handleIndent();
        if( r instanceof ThisRef ) {
            literal("this");
        } else throw new RuntimeException();
    }

    private boolean eatSpace = false;
    public void literal( String s ) {
        handleIndent();
        if( eatSpace && s.equals(" ") ) {
            eatSpace = false;
            return;
        }
        eatSpace = false;
        if( false
        ||  s.equals( Jimple.v().STATICINVOKE )
        ||  s.equals( Jimple.v().VIRTUALINVOKE )
        ||  s.equals( Jimple.v().INTERFACEINVOKE )
          ) {
            eatSpace = true;
            return;
        }
        output.append(s);
    }
    public void type( Type t ) {
        handleIndent();
        if( t instanceof RefType ) {
            output.append( ((RefType) t).getSootClass().getJavaStyleName() );
        } else if( t instanceof ArrayType ) {
            ((ArrayType) t).toString( this );
        } else {
            output.append( t.toString() );
        }
    }
    public void unitRef( Unit u, boolean branchTarget ) {
        throw new RuntimeException( "Dava doesn't have unit references!" );
    }






    public void addNot() {
        output.append(" !");
    }
    public void addAggregatedOr() {
        output.append(" || ");
    }
    public void addAggregatedAnd() {
        output.append(" && " );
    }
    public void addLeftParen() {
        output.append(" (" );
    }
    public void addRightParen() {
        output.append(") " );
    }

    public void printString(String s){
	output.append(s);
    }

}


