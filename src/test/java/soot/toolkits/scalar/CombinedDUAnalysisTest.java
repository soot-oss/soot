package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

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

			@Override
			public List<Unit> getDefsOf(Local l) {
                HashSet<Unit> hs1 = new HashSet<Unit>(combined.getDefsOf(l));
                HashSet<Unit> hs2 = new HashSet<Unit>(defs.getDefsOf(l));
                if( !hs1.equals(hs2) ) throw new RuntimeException(
                        "Defs of "+l+"\ncombined: "+hs1+"\nsimple: "+hs2);
                return combined.getDefsOf(l);
			}
			
        }
        ;
    }

	@Test
	public void test() {
		// TODO: build a proper test harness
	}

}
