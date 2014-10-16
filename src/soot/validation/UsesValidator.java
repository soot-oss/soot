package soot.validation;

import java.util.Iterator;
import java.util.List;

import soot.Body;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.toolkits.exceptions.PedanticThrowAnalysis;
import soot.toolkits.exceptions.ThrowAnalysis;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.LocalDefs;
import soot.toolkits.scalar.SimpleLiveLocals;
import soot.toolkits.scalar.SmartLocalDefs;

public class UsesValidator implements BodyValidator {
	public static UsesValidator INSTANCE;
	
	
	public static UsesValidator v() {
		if (INSTANCE == null)
		{
			INSTANCE = new UsesValidator();
		}
		return INSTANCE;
	}


	@Override
    /** Verifies that each use in this Body has a def. */
	public void validate(Body body, List<ValidationException> exception) {
        // Conservative validation of uses: add edges to exception handlers 
        // even if they are not reachable.
        //
        // class C {
        //   int a;
        //   public void m() {
        //     try {
        //      a = 2;
        //     } catch (Exception e) {
        //      System.out.println("a: "+ a);
        //     }
        //   }
        // }
        //
        // In a graph generated from the Jimple representation there would 
        // be no edge from "a = 2;" to "catch..." because "a = 2" can only 
        // generate an Error, a subclass of Throwable and not of Exception. 
        // Use of 'a' in "System.out.println" would thus trigger a 'no defs 
        // for value' RuntimeException. 
        // To avoid this  we create an ExceptionalUnitGraph that considers all 
        // exception handlers (even unreachable ones as the one in the code 
        // snippet above) by using a PedanticThrowAnalysis and setting the 
        // parameter 'omitExceptingUnitEdges' to false.
        // 
        // Note that unreachable traps can be removed by setting jb.uce's 
        // "remove-unreachable-traps" option to true.
		
        ThrowAnalysis throwAnalysis = PedanticThrowAnalysis.v();
        UnitGraph g = new ExceptionalUnitGraph(body, throwAnalysis, false);
        LocalDefs ld = new SmartLocalDefs(g, new SimpleLiveLocals(g));

        for (Unit u : body.getUnits()) {
            Iterator<ValueBox> useBoxIt = u.getUseBoxes().iterator();
            while (useBoxIt.hasNext())
            {
                Value v = (useBoxIt.next()).getValue();
                if (v instanceof Local)
                {
                    // This throws an exception if there is
                    // no def already; we check anyhow.
                    List<Unit> l = ld.getDefsOfAt((Local)v, u);
                    if (l.size() == 0){
                        exception.add(new ValidationException(u, "There is no path from a definition of " + v + " to this statement.", 
                        		"("+ body.getMethod() +") no defs for value: " + v + "!"));
                    }
                }
            }
        }
    }

	@Override
	public boolean isBasicValidator() {
		return false;
	}
}
