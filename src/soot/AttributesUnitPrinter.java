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
import soot.tagkit.*;

/**
* AttributesUnitPrinter implementation for normal (full) Jimple, Grimp, and Baf
* that tracks positions of variables and expressions for printing highlighting
* of attributes
*/
public class AttributesUnitPrinter extends NormalUnitPrinter {

	private int offset = 1;
	private int startOffset;
	
	AttributesUnitPrinter( Map labels, String indent) {
        super(labels, indent);
    }

    public void startUnit( Unit u ) {
		super.startUnit(u);
		offset = 1;
		offset = offset + indent.length();
	}
    public void endUnit( Unit u ) {}
    public void startUnitBox( UnitBox u ) {}
    public void endUnitBox( UnitBox u ) {}
    public void startValueBox( ValueBox u ) {
		startOffset = offset;
	}
    public void endValueBox( ValueBox u ) {
		u.addTag(new PositionTag(startOffset, offset));
		
	}

    public void literal( String s ) { 
		super.literal(s);
		offset = offset + s.length(); 
	}
    public void newline() { 
		super.newline();
		offset = offset + "\n".length() + indent.length();
	}
    public void local( Local l ) { 
		super.local(l);
		offset = offset + l.getName().length();
	}
    public void type( Type t ) { 
		super.type(t);
		offset = offset + t.toString().length();
	}
    public void method( SootMethod m ) { 
		super.method(m);
		offset = offset + m.getSignature().length();
	}
    public void constant( Constant c ) { 
		super.constant(c);
		offset = offset + c.toString().length(); 
	}
    public void fieldRef( SootField f ) { 
        super.fieldRef(f);
		offset = offset + f.getSignature().length();
    }
    public void unitRef( Unit u ) {
        String label = (String) labels.get( u );
        if( label == null || label.equals( "<unnamed>" ) )
            label = "[?= "+u+"]";
        output.append(label);
		offset = offset + label.length();
    }

    public int getOffset(){
		return offset;
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


