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
import soot.jimple.*;
import java.util.*;
import soot.tagkit.*;

/**
 * Adds PositionTags to ValueBoxes to identify their position in the output.
 */
public class AttributesUnitPrinter {

    private int startOffset;
	private Stack startOffsets;
	private int endOffset;
	private int startStmtOffset;
    private int startLn;
	private int currentLn;
    private int lastNewline;
    private UnitPrinter printer;
    
	public AttributesUnitPrinter( int currentLnNum ) {
		this.currentLn = currentLnNum;
	}
	public void startUnit( Unit u ) {
		startLn = currentLn;
		startStmtOffset = output().length() - lastNewline;
	}
	public void endUnit( Unit u ) {
		int endStmtOffset = output().length() - lastNewline;
        //G.v().out.println("u: "+u.toString());
		if (hasTag(u)){
            //G.v().out.println("u: "+u.toString()+" has tag");
			u.addTag( new JimpleLineNumberTag( startLn, currentLn ));
		}
		if (hasColorTag(u)) {
			u.addTag( new PositionTag(startStmtOffset, endStmtOffset) );
		}
	}
    public void startValueBox( ValueBox u ) {
		if (startOffsets == null) {
			startOffsets = new Stack();
		}
        startOffsets.push(new Integer(output().length() - lastNewline));
    }
    public void endValueBox( ValueBox u ) {
        endOffset = output().length() - lastNewline;
        if (hasColorTag(u)) {
			u.addTag(new PositionTag(((Integer)startOffsets.pop()).intValue(), endOffset));
		}
    }

	private boolean hasTag(Host h) {
        if (h instanceof Unit) {
            Iterator usesAndDefsIt = ((Unit)h).getUseAndDefBoxes().iterator();
            while (usesAndDefsIt.hasNext()){
                if (hasTag((ValueBox)usesAndDefsIt.next())) return true;
            }
        }
		if (h.getTags().isEmpty()) return false;
		return true;
	}
	
	private boolean hasColorTag(Host h) {
		Iterator it = h.getTags().iterator();
		while (it.hasNext()){
			if (it.next() instanceof ColorTag) return true;
		}
		return false;
	}
	
    public void setEndLn(int ln){
        currentLn = ln;
    }
    public int getEndLn() {
        return currentLn;
    }
    public void newline() { 
        currentLn++;
        lastNewline = output().length();
    }
    private StringBuffer output() {
        return printer.output();
    }
    public void setUnitPrinter( UnitPrinter up ) {
        printer = up;
    }
}


