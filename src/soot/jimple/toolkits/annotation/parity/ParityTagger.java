package soot.jimple.toolkits.annotation.parity;
import soot.*;
import java.util.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.tagkit.*;
import soot.jimple.*;

/** A body transformer that records parity analysis 
 * information in tags. */
public class ParityTagger extends BodyTransformer
{ 
	public ParityTagger( Singletons.Global g ) {}
    public static ParityTagger v() { return G.v().ParityTagger(); }

    protected void internalTransform(
            Body b, String phaseName, Map options)
    {
        ParityAnalysis a = new ParityAnalysis(
		new BriefUnitGraph( b ) );

        Iterator sIt = b.getUnits().iterator();
        while( sIt.hasNext() ) {

            Stmt s = (Stmt) sIt.next();

            HashMap parityVars = (HashMap) a.getFlowAfter( s );

            Iterator it = parityVars.keySet().iterator();
            while( it.hasNext() ) {
			
                final Value variable = (Value) it.next();

                StringTag t = new StringTag(
                        "Parity variable: "+variable+" "+parityVars.get(variable) );
                s.addTag( t );
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
    }

	private void addColorTag(ValueBox vb, String type) {
		if (type.equals("bottom")){
			//yellow
			vb.addTag(new ColorTag(255,248,35));
		}
		else if (type.equals("top")){
			//red
			vb.addTag(new ColorTag(255,0,0));
		}
		else if (type.equals("even")){
			//green
			vb.addTag(new ColorTag(255,248,35));
		}
		else if (type.equals("odd")){
			//blue
			vb.addTag(new ColorTag(174,210,255));
		}
	}
}


