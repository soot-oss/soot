package soot.jimple.spark.geom.helper;

import java.util.ArrayList;
import java.util.List;

import soot.SootMethod;
import soot.jimple.spark.geom.dataRep.CallsiteContextVar;
import soot.jimple.spark.geom.geomPA.CgEdge;
import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.geom.geomPA.IVarAbstraction;
import soot.jimple.spark.geom.geomPA.ZArrayNumberer;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.LocalVarNode;
import soot.jimple.spark.pag.Node;

/**
 * It provides the utilities to translate our numbering context representation to other commonly used representation.
 * For example, k-cfa, 1-object, etc.
 * 
 * @author xiao
 *
 */
public class ContextTranslator {
	
	/*
	 * The sets of the 1-cfa context sensitive pointers and objects.
	 */
	public static ZArrayNumberer<CallsiteContextVar> pts_1cfa_map = null;
	public static ZArrayNumberer<CallsiteContextVar> objs_1cfa_map = null;
	
	/**
	 * We map the geometric encoded result to the 1CFA form.
	 * @param ptsProvider
	 */
	public static void build_1cfa_map( GeomPointsTo ptsProvider )
	{
		if ( is_1cfa_built() ) return;
		
		pts_1cfa_map = new ZArrayNumberer<CallsiteContextVar>();
		objs_1cfa_map = new ZArrayNumberer<CallsiteContextVar>();
		
		// We first transform the pointers
		for ( IVarAbstraction pn : ptsProvider.pointers ) {
			if ( pn.willUpdate == false ) continue;
			Node v = pn.getWrappedNode();
			
			if ( v instanceof LocalVarNode ) {
				// Get the set of call edges for the enclosing function of the pointer
				LocalVarNode lvn = (LocalVarNode)v;
				SootMethod sm = lvn.getMethod();
				int sm_int = ptsProvider.getIDFromSootMethod(sm);
				List<CgEdge> edges = ptsProvider.getCallEdgesInto(sm_int);
				
				for ( CgEdge ce : edges ) {
					CallsiteContextVar context_var = new CallsiteContextVar(ce, v);
					pts_1cfa_map.add(context_var);
				}
			}
			else {
				CallsiteContextVar context_var = new CallsiteContextVar(null, v);
				pts_1cfa_map.add(context_var);
			}
		}
		
		// we then transform the objects
		for ( IVarAbstraction pobj : ptsProvider.allocations ) {
			AllocNode obj = (AllocNode)pobj.getWrappedNode();
			SootMethod sm = obj.getMethod();
			
			if ( sm == null ) {
				CallsiteContextVar context_obj = new CallsiteContextVar(null, obj);
				objs_1cfa_map.add(context_obj);
			}
			else {
				int sm_int = ptsProvider.getIDFromSootMethod(sm);
				List<CgEdge> edges = ptsProvider.getCallEdgesInto(sm_int);
				
				for ( CgEdge ce : edges ) {
					CallsiteContextVar context_obj = new CallsiteContextVar(ce, obj);
					objs_1cfa_map.add(context_obj);
				}
			}
		}
	}
	
	public static boolean is_1cfa_built()
	{
		return pts_1cfa_map != null && objs_1cfa_map != null;
	}
}
