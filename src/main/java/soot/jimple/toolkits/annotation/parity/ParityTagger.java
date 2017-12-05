/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jennifer Lhotak
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

package soot.jimple.toolkits.annotation.parity;
import soot.*;
import java.util.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.tagkit.*;
import soot.jimple.*;
import soot.options.*;

/** A body transformer that records parity analysis 
 * information in tags. */
public class ParityTagger extends BodyTransformer
{ 
	public ParityTagger( Singletons.Global g ) {}
    public static ParityTagger v() { return G.v().soot_jimple_toolkits_annotation_parity_ParityTagger(); }

    protected void internalTransform(
            Body b, String phaseName, Map options)
    {

        //System.out.println("parity tagger for method: "+b.getMethod().getName());
        boolean isInteractive = Options.v().interactive_mode();
        Options.v().set_interactive_mode(false);
        ParityAnalysis a;
        
        if (isInteractive){
            LiveLocals sll = new SimpleLiveLocals(new BriefUnitGraph(b));
            Options.v().set_interactive_mode(isInteractive);
        
            a = new ParityAnalysis(
		        new BriefUnitGraph( b ) , sll);
        }
        else {
            a = new ParityAnalysis(new BriefUnitGraph(b));
        }
        
        Iterator sIt = b.getUnits().iterator();
        while( sIt.hasNext() ) {

            Stmt s = (Stmt) sIt.next();

            HashMap parityVars = (HashMap) a.getFlowAfter( s );

            Iterator it = parityVars.keySet().iterator();
            while( it.hasNext() ) {
			
                final Value variable = (Value) it.next();
                if ((variable instanceof IntConstant) || (variable instanceof LongConstant)){
                    // don't add string tags (just color tags)
                }
                else{
                    StringTag t = new StringTag(
                        "Parity variable: "+variable+" "+
                        parityVars.get(variable) , "Parity Analysis");
                    s.addTag( t );
                }
            }

			HashMap parityVarsUses = (HashMap) a.getFlowBefore( s );
			HashMap parityVarsDefs = (HashMap) a.getFlowAfter( s );

			
			//uses
			
			Iterator valBoxIt = s.getUseBoxes().iterator();
			
			while (valBoxIt.hasNext()){
				ValueBox vb = (ValueBox)valBoxIt.next();
				if (parityVarsUses.containsKey(vb.getValue())){
					//G.v().out.println("Parity variable for: "+vb.getValue());
					String type = (String)parityVarsUses.get(vb.getValue());
					addColorTag(vb, type);
				}
			}

			// defs

			valBoxIt = s.getDefBoxes().iterator();
			
			while (valBoxIt.hasNext()){
				ValueBox vb = (ValueBox)valBoxIt.next();
				if (parityVarsDefs.containsKey(vb.getValue())){
					//G.v().out.println("Parity variable for: "+vb.getValue());
					String type = (String)parityVarsDefs.get(vb.getValue());
					addColorTag(vb, type);
				}
			}
        }

        // add key to class
        Iterator keyIt = b.getMethod().getDeclaringClass().getTags().iterator();
        boolean keysAdded = false;
        while (keyIt.hasNext()){
            Object next = keyIt.next();
            if (next instanceof KeyTag){
                if (((KeyTag)next).analysisType().equals("Parity Analysis")){
                    keysAdded = true;
                }
            }
        }
        if (!keysAdded){
            b.getMethod().getDeclaringClass().addTag(new KeyTag(255,0,0, "Parity: Top", "Parity Analysis"));
            b.getMethod().getDeclaringClass().addTag(new KeyTag(45,255,84, "Parity: Bottom", "Parity Analysis"));
            b.getMethod().getDeclaringClass().addTag(new KeyTag(255,248,35, "Parity: Even", "Parity Analysis"));
            b.getMethod().getDeclaringClass().addTag(new KeyTag(174,210,255, "Parity: Odd", "Parity Analysis"));
        }
    }

	private void addColorTag(ValueBox vb, String type) {
		if (type.equals("bottom")){
			//green
			vb.addTag(new ColorTag(ColorTag.GREEN, "Parity Analysis"));
		}
		else if (type.equals("top")){
			//red
			vb.addTag(new ColorTag(ColorTag.RED, "Parity Analysis"));
		}
		else if (type.equals("even")){
			//yellow
			vb.addTag(new ColorTag(ColorTag.YELLOW, "Parity Analysis"));
		}
		else if (type.equals("odd")){
			//blue
			vb.addTag(new ColorTag(ColorTag.BLUE, "Parity Analysis"));
		}
	}
}


