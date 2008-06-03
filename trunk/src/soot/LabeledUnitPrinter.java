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
    protected Map labels;
    /** for unit references in Phi nodes **/
    protected Map<Unit, String> references;

    public LabeledUnitPrinter( Body b ) {
        createLabelMaps(b);
    }

    public Map labels() { return labels; }
    public Map<Unit, String> references() { return references; }

    public abstract void literal( String s );
    public abstract void methodRef( SootMethodRef m );
    public abstract void fieldRef( SootFieldRef f );
    public abstract void identityRef( IdentityRef r );
    public abstract void type( Type t );

    public void unitRef( Unit u, boolean branchTarget ) {
        String oldIndent = getIndent();
        
        // normal case, ie labels
        if(branchTarget){
            setIndent(labelIndent);
            handleIndent();
            setIndent(oldIndent);
            String label = (String) labels.get( u );
            if( label == null || label.equals( "<unnamed>" ) )
                label = "[?= "+u+"]";
            output.append(label);
        }
        // refs to control flow predecessors (for Shimple)
        else{
            String ref = references.get( u );

            if(startOfLine){
                String newIndent = "(" + ref + ")" +
                    indent.substring(ref.length() + 2);
                setIndent(newIndent);
                handleIndent();
                setIndent(oldIndent);
            }
            else
                output.append(ref);
        }
    }
    
    private void createLabelMaps(Body body) {
        Chain units = body.getUnits();

        labels = new HashMap(units.size() * 2 + 1, 0.7f);
        references = new HashMap<Unit, String>(units.size() * 2 + 1, 0.7f);
        
        // Create statement name table
        {
            Iterator boxIt = body.getAllUnitBoxes().iterator();

            Set<Unit> labelStmts = new HashSet<Unit>();
            Set<Unit> refStmts = new HashSet<Unit>();
            
            // Build labelStmts and refStmts
            {
                while (boxIt.hasNext()) {
                    UnitBox box = (UnitBox) boxIt.next();
                    Unit stmt = box.getUnit();

                    if(box.isBranchTarget())
                        labelStmts.add(stmt);
                    else
                        refStmts.add(stmt);
                }

            }

            // Traverse the stmts and assign a label if necessary
            {
                int labelCount = 0;
                int refCount = 0;
                
                Iterator stmtIt = units.iterator();

                while (stmtIt.hasNext()) {
                    Unit s = (Unit) stmtIt.next();

                    if (labelStmts.contains(s)) 
                        labels.put(s, "label" + (labelCount++));

                    if (refStmts.contains(s))
                        references.put(s, Integer.toString(refCount++));
                }
            }
        }
    }

    protected String labelIndent = "     ";
}

