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
import soot.AbstractUnitPrinter;
import soot.ArrayType;
import soot.RefType;
import soot.SootFieldRef;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.dava.toolkits.base.renamer.RemoveFullyQualifiedName;
import soot.jimple.ClassConstant;
import soot.jimple.Constant;
import soot.jimple.IdentityRef;
import soot.jimple.Jimple;
import soot.jimple.ThisRef;

/**
 * UnitPrinter implementation for Dava.
 */
public class DavaUnitPrinter extends AbstractUnitPrinter {
	DavaBody body;
	
	/*
	 * 30th March 2006, Nomair A Naeem
	 * Adding constructor so that the current methods DabaBody can be stored
	 */
	public DavaUnitPrinter(DavaBody body){
		this.body = body;
	}
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
        ||  s.equals( Jimple.STATICINVOKE )
        ||  s.equals( Jimple.VIRTUALINVOKE )
        ||  s.equals( Jimple.INTERFACEINVOKE )
          ) {
            eatSpace = true;
            return;
        }
        output.append(s);
    }
    public void type( Type t ) {
        handleIndent();
        if( t instanceof RefType ) {
        	
        	String name = ((RefType) t).getSootClass().getJavaStyleName();
        	/*
        	 * March 30th 2006, Nomair
        	 * Adding check to check that the fully qualified name can actually be removed
        	 */
        	if(!name.equals( ((RefType)t).getSootClass().toString())){
        		//means javaStyle name is probably shorter check that there is no class clash in imports for this
        		
        		//System.out.println(">>>>Type is"+t.toString());
        		//System.out.println(">>>>Name is"+name);
        		name = RemoveFullyQualifiedName.getReducedName(body.getImportList(),((RefType)t).getSootClass().toString(),t);
        	
        	}
            output.append(name);
        } 
        else if( t instanceof ArrayType ) {
            ((ArrayType) t).toString( this );
        } 
        else {
            output.append( t.toString() );
        }
    }
    public void unitRef( Unit u, boolean branchTarget ) {
        throw new RuntimeException( "Dava doesn't have unit references!" );
    }
    
    @Override
    public void constant( Constant c ) {
      if (c instanceof ClassConstant) {
        handleIndent();
        String fullClassName =
          ((ClassConstant)c).value.replaceAll("/", ".");
        output.append(fullClassName + ".class");
      } else {
        super.constant(c);
      }
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


