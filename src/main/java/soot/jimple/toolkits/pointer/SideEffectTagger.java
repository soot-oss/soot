package soot.jimple.toolkits.pointer;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.PhaseOptions;
import soot.Scene;
import soot.Singletons;
import soot.Unit;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

public class SideEffectTagger extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(SideEffectTagger.class);

  public SideEffectTagger(Singletons.Global g) {
  }

  public static SideEffectTagger v() {
    return G.v().soot_jimple_toolkits_pointer_SideEffectTagger();
  }

  public int numRWs = 0;
  public int numWRs = 0;
  public int numRRs = 0;
  public int numWWs = 0;
  public int numNatives = 0;
  public Date startTime = null;
  boolean optionNaive = false;
  private CallGraph cg;

  protected class UniqueRWSets implements Iterable<RWSet> {
    protected ArrayList<RWSet> l = new ArrayList<RWSet>();

    RWSet getUnique(RWSet s) {
      if (s == null) {
        return s;
      }
      for (RWSet ret : l) {
        if (ret.isEquivTo(s)) {
          return ret;
        }
      }
      l.add(s);
      return s;
    }

    @Override
    public Iterator<RWSet> iterator() {
      return l.iterator();
    }

    short indexOf(RWSet s) {
      short i = 0;
      for (RWSet ret : l) {
        if (ret.isEquivTo(s)) {
          return i;
        }
        i++;
      }
      return -1;
    }
  }

  protected void initializationStuff(String phaseName) {
    G.v().Union_factory = new UnionFactory() {
      // ReallyCheapRasUnion ru = new ReallyCheapRasUnion();
      // public Union newUnion() { return new RasUnion(); }
      @Override
      public Union newUnion() {
        return new MemoryEfficientRasUnion();
      }
    };

    if (startTime == null) {
      startTime = new Date();
    }
    cg = Scene.v().getCallGraph();
  }

  protected Object keyFor(Stmt s) {
    if (s.containsInvokeExpr()) {
      if (optionNaive) {
        throw new RuntimeException("shouldn't get here");
      }
      Iterator<Edge> it = cg.edgesOutOf(s);
      if (!it.hasNext()) {
        return Collections.emptyList();
      }
      ArrayList<Edge> ret = new ArrayList<Edge>();
      while (it.hasNext()) {
        ret.add(it.next());
      }
      return ret;
    } else {
      return s;
    }
  }

  @Override
  protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
    initializationStuff(phaseName);
    SideEffectAnalysis sea = Scene.v().getSideEffectAnalysis();
    optionNaive = PhaseOptions.getBoolean(options, "naive");
    if (!optionNaive) {
      sea.findNTRWSets(body.getMethod());
    }
    HashMap<Object, RWSet> stmtToReadSet = new HashMap<Object, RWSet>();
    HashMap<Object, RWSet> stmtToWriteSet = new HashMap<Object, RWSet>();
    UniqueRWSets sets = new UniqueRWSets();
    final boolean justDoTotallyConservativeThing = "<clinit>".equals(body.getMethod().getName());
    for (Unit next : body.getUnits()) {
      final Stmt stmt = (Stmt) next;
      if (justDoTotallyConservativeThing || (optionNaive && stmt.containsInvokeExpr())) {
        stmtToReadSet.put(stmt, sets.getUnique(new FullRWSet()));
        stmtToWriteSet.put(stmt, sets.getUnique(new FullRWSet()));
        continue;
      }
      Object key = keyFor(stmt);
      if (!stmtToReadSet.containsKey(key)) {
        stmtToReadSet.put(key, sets.getUnique(sea.readSet(body.getMethod(), stmt)));
        stmtToWriteSet.put(key, sets.getUnique(sea.writeSet(body.getMethod(), stmt)));
      }
    }
    DependenceGraph graph = new DependenceGraph();
    for (RWSet outer : sets) {
      for (RWSet inner : sets) {
        if (inner == outer) {
          break;
        }
        if (outer.hasNonEmptyIntersection(inner)) {
          // logger.debug(""+ "inner set is: "+inner );
          // logger.debug(""+ "outer set is: "+outer );
          graph.addEdge(sets.indexOf(outer), sets.indexOf(inner));
        }
      }
    }
    body.getMethod().addTag(graph);
    for (Unit next : body.getUnits()) {
      final Stmt stmt = (Stmt) next;
      Object key = (optionNaive && stmt.containsInvokeExpr()) ? stmt : keyFor(stmt);
      RWSet read = stmtToReadSet.get(key);
      RWSet write = stmtToWriteSet.get(key);
      if (read != null || write != null) {
        DependenceTag tag = new DependenceTag();
        if (read != null && read.getCallsNative()) {
          tag.setCallsNative();
          numNatives++;
        } else if (write != null && write.getCallsNative()) {
          tag.setCallsNative();
          numNatives++;
        }
        tag.setRead(sets.indexOf(read));
        tag.setWrite(sets.indexOf(write));
        stmt.addTag(tag);

        // The loop below is just for calculating stats.
        /*
         * if( !justDoTotallyConservativeThing ) { for( Iterator innerIt = body.getUnits().iterator(); innerIt.hasNext(); ) {
         * final Stmt inner = (Stmt) innerIt.next(); Object ikey; if( optionNaive && inner.containsInvokeExpr() ) { ikey =
         * inner; } else { ikey = keyFor( inner ); } RWSet innerRead = (RWSet) stmtToReadSet.get( ikey ); RWSet innerWrite =
         * (RWSet) stmtToWriteSet.get( ikey ); if( graph.areAdjacent( sets.indexOf( read ), sets.indexOf( innerWrite ) ) )
         * numRWs++; if( graph.areAdjacent( sets.indexOf( write ), sets.indexOf( innerRead ) ) ) numWRs++; if( inner == stmt
         * ) continue; if( graph.areAdjacent( sets.indexOf( write ), sets.indexOf( innerWrite ) ) ) numWWs++; if(
         * graph.areAdjacent( sets.indexOf( read ), sets.indexOf( innerRead ) ) ) numRRs++; } }
         */
      }
    }
  }
}
