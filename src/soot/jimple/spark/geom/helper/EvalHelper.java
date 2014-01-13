/* Soot - a J*va Optimization Framework
 * Copyright (C) 2012 Richard Xiao
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
package soot.jimple.spark.geom.helper;

import java.util.Iterator;
import java.util.Set;

import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.jimple.spark.geom.geomPA.Constants;
import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.geom.geomPA.IVarAbstraction;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.sets.P2SetVisitor;

/**
 * This class provides various methods to explore the internals of the geometric points-to result.
 * The major purpose for this class is to help programmers debug the your clients that use points-to information.
 * 
 * @author xiao
 *
 */
public class EvalHelper 
{
	public static void debug_succint_pointsto_info(Node vn, GeomPointsTo ptsProvider )
	{
		IVarAbstraction pn = ptsProvider.makeInternalNode(vn);
		ptsProvider.ps.println("My objects(" + pn.num_of_diff_objs() + ") :");
	}
	
	public static void debug_spark_pointsto( Node vn, final GeomPointsTo ptsProvider )
	{
		int N = vn.getP2Set().size();
		
		ptsProvider.ps.println("SPARK objects(" + N + ") :");
		if ( N > 10 ) return;
		
		vn.getP2Set().forall( new P2SetVisitor() {
			@Override
			public void visit(Node n) {
				// TODO Auto-generated method stub
				ptsProvider.ps.println( n.toString() );
			}
		});
	}
	
	// Print the context insensitive objects
	public static void debug_context_insensitive_points_to(Node vn, final GeomPointsTo ptsProvider) 
	{
		ptsProvider.ps.println("VarNode : " + vn + ", Type : " + vn.getType());
		IVarAbstraction pn = ptsProvider.makeInternalNode(vn);
		ptsProvider.ps.println("My objects(" + pn.num_of_diff_objs() + ") :");
		
		if ( vn.getType() instanceof RefType ) {
			SootClass sc = ((RefType)vn.getType()).getSootClass();
			if ( !sc.isInterface() && Scene.v().getActiveHierarchy().isClassSubclassOfIncluding(
					sc, Constants.exeception_type.getSootClass()) ) {
				ptsProvider.ps.println( "An exeception receiver!" );
				ptsProvider.ps.println();
				return;
			}
		}
		
		if (  pn.num_of_diff_objs() <= 100000 ) {
		Set<AllocNode> set = pn.get_all_points_to_objects();
			for (Iterator<AllocNode> it = set.iterator(); it.hasNext();) {
				// ps.print(" " + it.next().getNumber());
				AllocNode an = it.next();
				if ( an.getMethod() != null && !an.getMethod().isJavaLibraryMethod() )
					ptsProvider.ps.println(an.toString());
			}
		}
		
		ptsProvider.ps.println();

		/*
		ps.print("Spark objects(" + vn.getP2Set().size() + ") :");
		vn.getP2Set().forall(new P2SetVisitor() {
			public final void visit(Node n) {
				ps.print(" " + ((AllocNode) n).getNumber());
			}
		});
		ps.println();
		*/
	}

	public static void debug_context_sensitive_objects( IVarAbstraction pn, final GeomPointsTo ptsProvider )
	{
		Node vn = pn.getWrappedNode();
//		if ( pn.num_of_diff_objs() >= 50 ) {
//			print_context_insensitive_points_to(vn);
//			return;
//		}
		
		ptsProvider.ps.println("VarNode : " + vn + ", Type : " + vn.getType() );
//		ps.println("My objects(" + pn.num_of_diff_objs() + ") :");
		if ( vn.getType() instanceof RefType ) {
			SootClass sc = ((RefType)vn.getType()).getSootClass();
			if ( !sc.isInterface() && Scene.v().getActiveHierarchy().isClassSubclassOfIncluding(
					sc, Constants.exeception_type.getSootClass()) ) {
				ptsProvider.ps.println( "An exeception receiver!" );
				ptsProvider.ps.println();
				return;
			}
		}
		
		pn.print_context_sensitive_points_to(ptsProvider.ps);
		ptsProvider.ps.println();
	}
}
