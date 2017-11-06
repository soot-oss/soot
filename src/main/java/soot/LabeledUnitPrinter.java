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
import soot.util.*;

import java.util.*;

/**
* UnitPrinter implementation for representations that have labelled stmts,
* such as Jimple, Grimp, and Baf
*/
public abstract class LabeledUnitPrinter extends AbstractUnitPrinter {
    /** branch targets **/
    protected Map<Unit, String> labels;
    /** for unit references in Phi nodes **/
    protected Map<Unit, String> references;

    protected String labelIndent = "\u0020\u0020\u0020\u0020\u0020";
    
    public LabeledUnitPrinter( Body b ) {
        createLabelMaps(b);
    }
    

    public Map<Unit, String> labels() { 
    	return labels; 
    }
    	
    public Map<Unit, String> references() { 
    	return references; 
	}

    public abstract void literal( String s );
    public abstract void methodRef( SootMethodRef m );
    public abstract void fieldRef( SootFieldRef f );
    public abstract void identityRef( IdentityRef r );
    public abstract void type( Type t );

    public void unitRef( Unit u, boolean branchTarget ) {
        String oldIndent = getIndent();
        
        // normal case, ie labels
        if (branchTarget) {
            setIndent(labelIndent);
            handleIndent();
            setIndent(oldIndent);
            String label = labels.get( u );
            if (label == null || "<unnamed>".equals(label)) {
                label = "[?= "+u+"]";
            }
            output.append(label);
        }
        // refs to control flow predecessors (for Shimple)
        else{
            String ref = references.get(u);

            if (startOfLine) {
                String newIndent = "(" + ref + ")" +
                    indent.substring(ref.length() + 2);
                    
                setIndent(newIndent);
                handleIndent();
                setIndent(oldIndent);
            }
            else {
                output.append(ref);
            }
        }        
    }
    
    private void createLabelMaps(Body body) {
        Chain<Unit> units = body.getUnits();

        labels = new HashMap<Unit, String>(units.size() * 2 + 1, 0.7f);
        references = new HashMap<Unit, String>(units.size() * 2 + 1, 0.7f);
        
        // Create statement name table        
        Set<Unit> labelStmts = new HashSet<Unit>();
        Set<Unit> refStmts = new HashSet<Unit>();
                    
        // Build labelStmts and refStmts
        for (UnitBox box : body.getAllUnitBoxes() ) {
        	Unit stmt = box.getUnit();

            if (box.isBranchTarget()) {
                labelStmts.add(stmt);
            }
            else {
                refStmts.add(stmt);
            }
        }

		// left side zero padding for all labels 
		// this simplifies debugging the jimple code in simple editors, as it 
		// avoids the situation where a label is the prefix of another label		
    	final int maxDigits = 1 + (int) Math.log10(labelStmts.size());            	
    	final String formatString = "label%0" + maxDigits + "d";
    	
        int labelCount = 0;
        int refCount = 0;
        
        // Traverse the stmts and assign a label if necessary        
        for ( Unit s : units ) {                	
            if (labelStmts.contains(s)) 
                labels.put(s, String.format(formatString, ++labelCount));

            if (refStmts.contains(s))
                references.put(s, Integer.toString(refCount++));
        }
    }
}

