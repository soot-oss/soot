/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002,2003 Ondrej Lhotak
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

package soot.jimple.spark.solver;
import soot.Context;
import soot.Local;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.Type;
import soot.jimple.IntConstant;
import soot.jimple.NewArrayExpr;
import soot.jimple.spark.pag.AllocDotField;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.ArrayElement;
import soot.jimple.spark.pag.MethodPAG;
import soot.jimple.spark.pag.NewInstanceNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.pag.StringConstantNode;
import soot.jimple.spark.pag.VarNode;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.spark.sets.PointsToSetInternal;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.CallGraphBuilder;
import soot.jimple.toolkits.callgraph.ContextManager;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.callgraph.OnFlyCallGraphBuilder;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.util.queue.QueueReader;


/** The interface between the pointer analysis engine and the on-the-fly
 * call graph builder.
 * @author Ondrej Lhotak
 */

public class OnFlyCallGraph {
    private final OnFlyCallGraphBuilder ofcgb;
    private final ReachableMethods reachableMethods;
    private final QueueReader<MethodOrMethodContext> reachablesReader;
    private final QueueReader<Edge> callEdges;
    private final CallGraph callGraph;

    public ReachableMethods reachableMethods() { return reachableMethods; }
    public CallGraph callGraph() { return callGraph; }

    public OnFlyCallGraph( PAG pag, boolean appOnly ) {
        this.pag = pag;
        callGraph = new CallGraph();
        Scene.v().setCallGraph( callGraph );
        ContextManager cm = CallGraphBuilder.makeContextManager(callGraph);
        reachableMethods = Scene.v().getReachableMethods();
        ofcgb = new OnFlyCallGraphBuilder( cm, reachableMethods, appOnly );
        reachablesReader = reachableMethods.listener();
        callEdges = cm.callGraph().listener();
    }
    public void build() {
        ofcgb.processReachables();
        processReachables();
        processCallEdges();
    }
    private void processReachables() {
        reachableMethods.update();
        while(reachablesReader.hasNext()) {
            MethodOrMethodContext m = reachablesReader.next();
            MethodPAG mpag = MethodPAG.v( pag, m.method() );
            mpag.build();
            mpag.addToPAG(m.context());
        }
    }
    private void processCallEdges() {
        while(callEdges.hasNext()) {
            Edge e = callEdges.next();
            MethodPAG amp = MethodPAG.v( pag, e.tgt() );
            amp.build();
            amp.addToPAG( e.tgtCtxt() );
            pag.addCallTarget( e );
        }
    }

    public OnFlyCallGraphBuilder ofcgb() { return ofcgb; }
    
    public void updatedFieldRef(final AllocDotField df, PointsToSetInternal ptsi) {
    	if(df.getField() != ArrayElement.v()) {
    		return;
    	}
    	if(ofcgb.wantArrayField(df)) {
    		ptsi.forall(new P2SetVisitor() {
				@Override
				public void visit(Node n) {
					ofcgb.addInvokeArgType(df, null, n.getType());
				}
			});
    	}
    }

    public void updatedNode( VarNode vn ) {
        Object r = vn.getVariable();
        if( !(r instanceof Local) ) return;
        final Local receiver = (Local) r;
        final Context context = vn.context();

        PointsToSetInternal p2set = vn.getP2Set().getNewSet();
        if( ofcgb.wantTypes( receiver ) ) {
            p2set.forall( new P2SetVisitor() {
            public final void visit( Node n ) {
            	if (n instanceof AllocNode)
            		ofcgb.addType( receiver, context, n.getType(), (AllocNode) n );
            }} );
        }
        if( ofcgb.wantStringConstants( receiver ) ) {
            p2set.forall( new P2SetVisitor() {
            public final void visit( Node n ) {
                if( n instanceof StringConstantNode ) {
                    String constant = ((StringConstantNode)n).getString();
                    ofcgb.addStringConstant( receiver, context, constant );
                } else {
                    ofcgb.addStringConstant( receiver, context, null );
                }
            }} );
        }
        if(ofcgb.wantInvokeArg(receiver)) {
        	p2set.forall(new P2SetVisitor() {
				@Override
				public void visit(Node n) {
	            	if (n instanceof AllocNode) {
						AllocNode an = ((AllocNode)n);
						ofcgb.addInvokeArgDotField(receiver, an.dot(ArrayElement.v()));
						assert an.getNewExpr() instanceof NewArrayExpr;
						NewArrayExpr nae = (NewArrayExpr) an.getNewExpr();
						if(!(nae.getSize() instanceof IntConstant)) {
							ofcgb.setArgArrayNonDetSize(receiver, context);
						} else {
							IntConstant sizeConstant = (IntConstant) nae.getSize();
							ofcgb.addPossibleArgArraySize(receiver, sizeConstant.value, context);
						}
	            	}
				}
			});
        	for(Type ty : pag.reachingObjectsOfArrayElement(p2set).possibleTypes()) {
        		ofcgb.addInvokeArgType(receiver, context, ty);
        	}
        }
    }

    /** Node uses this to notify PAG that n2 has been merged into n1. */
    public void mergedWith( Node n1, Node n2 ) {
    }

    /* End of public methods. */
    /* End of package methods. */

    private PAG pag;
}



