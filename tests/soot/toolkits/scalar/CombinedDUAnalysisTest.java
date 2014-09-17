package soot.toolkits.scalar;

import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import soot.Local;
import soot.Unit;
import soot.toolkits.graph.UnitGraph;

public class CombinedDUAnalysisTest {
	
    public static CombinedAnalysis v(final UnitGraph graph) {
        return new CombinedAnalysis() {
            CombinedDUAnalysis combined = new CombinedDUAnalysis(graph);
            SimpleLocalDefs defs = new SimpleLocalDefs(graph);
            SimpleLocalUses uses = new SimpleLocalUses(graph, defs);
            SimpleLiveLocals live = new SimpleLiveLocals(graph);
            
            public List<Unit> getDefsOfAt(Local l, Unit s) {
                HashSet<Unit> hs1 = new HashSet<Unit>(combined.getDefsOfAt(l, s));
                HashSet<Unit> hs2 = new HashSet<Unit>(defs.getDefsOfAt(l, s));
                if( !hs1.equals(hs2) ) throw new RuntimeException(
                        "Defs of "+l+" in "+s+"\ncombined: "+hs1+"\nsimple: "+hs2);
                return combined.getDefsOfAt(l, s);
            }
            
            public List<UnitValueBoxPair> getUsesOf(Unit u) {
                HashSet<UnitValueBoxPair> hs1 = new HashSet<UnitValueBoxPair>(combined.getUsesOf(u));
                HashSet<UnitValueBoxPair> hs2 = new HashSet<UnitValueBoxPair>(uses.getUsesOf(u));
                if( !hs1.equals(hs2) ) throw new RuntimeException(
                        "Uses of "+u+"\ncombined: "+hs1+"\nsimple: "+hs2);
                return combined.getUsesOf(u);
            }
            
            public List<Local> getLiveLocalsBefore(Unit u) {
                HashSet<Local> hs1 = new HashSet<Local>(combined.getLiveLocalsBefore(u));
                HashSet<Local> hs2 = new HashSet<Local>(live.getLiveLocalsBefore(u));
                if( !hs1.equals(hs2) ) throw new RuntimeException(
                        "llb of "+u+"\ncombined: "+hs1+"\nsimple: "+hs2);
                return combined.getLiveLocalsBefore(u);
            }
            
            public List<Local> getLiveLocalsAfter(Unit u) {
                HashSet<Local> hs1 = new HashSet<Local>(combined.getLiveLocalsAfter(u));
                HashSet<Local> hs2 = new HashSet<Local>(live.getLiveLocalsAfter(u));
                if( !hs1.equals(hs2) ) throw new RuntimeException(
                        "lla of "+u+"\ncombined: "+hs1+"\nsimple: "+hs2);
                return combined.getLiveLocalsAfter(u);
            }
        }
        ;
    }

	@Test
	public void test() {
		// TODO: build a proper test harness
	}

}
