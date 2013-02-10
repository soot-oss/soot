/* Soot - a J*va Optimization Framework
 * Copyright (C) 2011 Richard Xiao
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
package soot.jimple.spark.geom.geomPA;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import soot.Local;
import soot.SootField;
import soot.SootMethod;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.spark.geom.dataRep.CallsiteContextVar;
import soot.jimple.spark.geom.helper.Obj_1cfa_extractor;
import soot.jimple.spark.pag.AllocDotField;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.ArrayElement;
import soot.jimple.spark.pag.GlobalVarNode;
import soot.jimple.spark.pag.LocalVarNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.SparkField;
import soot.util.BitSetIterator;
import soot.util.BitVector;
import soot.util.Numberable;

/**
 * This class implements our own side effect algorithm.
 * The result is an inverted side effect matrix, in which each row represents an instance field (o.f) or static filed.
 * The content of each row is a set of statements that mod/ref the field represented by this row.
 * Every statement is mapped to two instances, one represents the mod set, one represents the ref set.
 * The mod/ref sets for the callsites are NOT computed yet.
 * 
 * @author xiao
 *
 */
public class SideEffectAnalyzer 
{
	/**
	 * Describes the set of variables that are referenced (mod or ref) by a statement.
	 * @author xiao
	 *
	 */
	class modRefSet implements Numberable
	{
		public static final int UNKNOWN = 0;
		public static final int MODSET = 1;
		public static final int REFSET = 2;
		
		private int id = -1;
		private int type = UNKNOWN;
		private Stmt stmt = null;
		private CgEdge context = null;							// The context of this statement
		private BitVector ref_vars = new BitVector();			// Referenced variables by this statement under specified context
		
		public modRefSet( int tp, Stmt st, CgEdge ctxt )
		{
			stmt = st;
			type = tp;
			context = ctxt;
		}
		
		@Override
		public void setNumber(int number) {
			id = number;
		}

		@Override
		public int getNumber() {
			return id;
		}
		
		@Override
		public String toString()
		{
			return stmt.toString();
		}
		
		public void addBit( CallsiteContextVar var )
		{
			ref_vars.set( var.getNumber() );
		}
		
		public void mergeBits( modRefSet other )
		{
			ref_vars.and(other.getBitVector());
		}
		
		public BitVector getBitVector()
		{
			return ref_vars;
		}
		
		// How many variables are referenced by this statement
		public int getNumberOfVars()
		{
			return ref_vars.cardinality();
		}
		
		public int getType()
		{
			return type;
		}
		
		public Stmt getStatement()
		{
			return stmt;
		}
		
		public CgEdge getContext()
		{
			return context;
		}
	}
	
	
	private GeomPointsTo ptsProvider;
	private ZArrayNumberer<modRefSet> allModRefSets = new ZArrayNumberer<modRefSet>();
	private Map<Stmt, modRefSet> stmt2ModRefSet = new HashMap<Stmt, modRefSet>();

	// The following mod/ref sets are not put into allModRefSets
	private Map<SootMethod, modRefSet> method2ModSet = new HashMap<SootMethod, modRefSet>();
	private Map<SootMethod, modRefSet> method2RefSet = new HashMap<SootMethod, modRefSet>();
	
	
	// Temporarily used for variable existence checking
	private CallsiteContextVar tmpVar = new CallsiteContextVar();
	// We want to get the variable instance that is used in the invertedMatrix mapping
	private ZArrayNumberer<CallsiteContextVar> selfMap = new ZArrayNumberer<CallsiteContextVar>();
	
	public SideEffectAnalyzer( GeomPointsTo pts ) {
		ptsProvider = pts;
	}
	
	public void compute()
	{
		Obj_1cfa_extractor objs_1cfa = new Obj_1cfa_extractor();
		
		/*
		 * Our algorithm works in two passes.
		 * In the first pass, we visit all the functions and collect the side effects for their assignment statements.
		 * In the second pass, we traverse the functions bottom-up and collect their transitive side effects.
		 * 
		 * Prerequisite:
		 * ContextTranslator.build_1cfa_map().
		 */
		for ( SootMethod sm : ptsProvider.getAllReachableMethods() ) {
//			if ( sm.isJavaLibraryMethod() )
//				continue;
			int sm_int = ptsProvider.getIDFromSootMethod(sm);
			if ( !ptsProvider.isReachableMethod(sm_int) )
				continue;
			if (!sm.isConcrete())
				continue;
			if (!sm.hasActiveBody()) {
				sm.retrieveActiveBody();
			}
			if ( !ptsProvider.isValidMethod(sm) )
				continue;
			
			// We first gather all the memory access expressions
			for (Iterator stmts = sm.getActiveBody().getUnits().iterator(); stmts.hasNext();) {
				Stmt st = (Stmt) stmts.next();
				if ( !(st instanceof AssignStmt) ) continue;
				
				// This is an ordinary statement that can make side effects
				AssignStmt as = (AssignStmt)st;
				Value lop = as.getLeftOp();
				Value rop = as.getRightOp();
				int type = loadOrStoreStmt(lop, rop);
				if ( type == -1 ) continue;
				
				// The part that is meaningful to us
				Value op = lop;
				if ( type == modRefSet.REFSET ) op = rop;
				
				// We specially handle the case of mod/ref to the static fields
				if ( op instanceof StaticFieldRef ) {
					// f = xxxxx or xxxxx = f
					SootField f = ((StaticFieldRef)op).getField();
					GlobalVarNode vn = ptsProvider.makeGlobalVarNode(f, f.getType());
					
					if ( ptsProvider.isExceptionPointer(vn) == false ) {
						modRefSet modOrRef = generateModRefSet(null, st, type);
						CallsiteContextVar cVar = getContextVar(null, vn);
						modOrRef.addBit( cVar );
					}
					continue;
				}
				
				// Now we loop over all the contexts of this statement
				LocalVarNode vn = null;
				SparkField fld = null;
				
				if (op instanceof InstanceFieldRef) {
					// p.f = xxxxx or xxxxx = p.f
					InstanceFieldRef ifr = (InstanceFieldRef) op;
					vn = ptsProvider.findLocalVarNode((Local) ifr.getBase());
					fld = ifr.getField();
				} else if (op instanceof ArrayRef) {
					// p[i] = xxxxx or xxxxx = p[i]
					ArrayRef arf = (ArrayRef) op;
					vn = ptsProvider.findLocalVarNode((Local) arf
							.getBase());
					fld = ArrayElement.v();
				}
				
				// Now we construct the mod/ref set
				if (vn != null
						&& ptsProvider.isExceptionPointer(vn) == false) {
					IVarAbstraction pn = ptsProvider.findInternalNode(vn);
					pn = pn.getRepresentative();
					
					if (pn.willUpdate == false) {
						// We make only one mod/ref set
						modRefSet modOrRef = generateModRefSet(null, st, type);
						objs_1cfa.prepare();
						pn.get_all_context_sensitive_objects(1, Constants.MAX_CONTEXTS, objs_1cfa);
						
						for (CallsiteContextVar can : objs_1cfa.outList) {
							AllocNode an = (AllocNode) can.var;
							AllocDotField adf = ptsProvider.makeAllocDotField(an, fld);
							// We construct the 1CFA o.f variables
							CallsiteContextVar cVar = getContextVar(can.context, adf);
							modOrRef.addBit(cVar);
						}
						continue;
					}
					
					List<CgEdge> edges = ptsProvider.getCallEdgesInto(sm_int);
					
					for (CgEdge cxtEdge : edges) {
						modRefSet modOrRef = generateModRefSet(cxtEdge, st, type);
						// We are going to obtain a set of 1CFA objects that
						// are modified under the context
						long l = cxtEdge.map_offset;
						long r = l
								+ ptsProvider.max_context_size_block[cxtEdge.s];
						objs_1cfa.prepare();
						pn.get_all_context_sensitive_objects(l, r, objs_1cfa);

						for (CallsiteContextVar can : objs_1cfa.outList) {
							AllocNode an = (AllocNode) can.var;
							AllocDotField adf = ptsProvider.makeAllocDotField(an, fld);
							// We construct the 1CFA o.f variables
							CallsiteContextVar cVar = getContextVar(can.context, adf);
							modOrRef.addBit(cVar);
						}
					}
//					if ( flag == false ) {
//						if ( vn.getP2Set().isEmpty() )
//							System.err.println( "soga" );
//						System.err.println( "Call edges:" );
//						for (CgEdge cxtEdge : edges) {
//							long l = cxtEdge.map_offset;
//							long r = l
//									+ ptsProvider.max_context_size_block[cxtEdge.s];
//							System.err.printf( "Edge: l = %d, r = %d\n", l, r );
//						}
//						EvalHelper.debug_context_sensitive_objects(pn, ptsProvider);
//						System.exit(-1);
//					}
				}
			}
		}
		
		/*
		 * pass 2: we topsort the functions (functions in the same SCC are put together) 
		 */
	}
	
	public void evaluateSideEffectMatrix()
	{
		int[] limits = new int[] { 1, 5, 10, 25, 50, 75, 100 };
		Histogram refSize = new Histogram(limits);
		
		for ( modRefSet mr : allModRefSets ) {
			refSize.addNumber( mr.getBitVector().cardinality() );
		}
		
		ptsProvider.ps.println();
		ptsProvider.ps.println( "--------------------Side Effect Matrix Information-------------------" );
		refSize.printResult(ptsProvider.ps, "Side-effect matrix reference size distribution");
	}
	
	/**
	 * Format:
	 * n m	-> n:rows, m:columns
	 * 0/1 k o_1 o_2 ... o_k	-> 0/1: mod/ref, k:number of variables in this modref set, o_1 ... o_k: referenced variables
	 */
	public void dumpSideEffectMatrix()
	{
		try {
			final PrintWriter file = new PrintWriter( ptsProvider.createOutputFile("geomModRef.txt") );
			
			// Rows and columns of the matrix
			file.println( allModRefSets.size() + " " + selfMap.size() );
			
			for ( modRefSet mr : allModRefSets ) {
				file.print( mr.getType() );
				
				BitVector vec = mr.getBitVector();
				file.print( " " + vec.cardinality() );
				
				for ( BitSetIterator bit = vec.iterator(); bit.hasNext(); ) {
					int v = bit.next();
					file.print( " " + v );
				}
				file.println();
			}
			
			file.close();
			
		} catch( IOException e ) {
            throw new RuntimeException( "Couldn't dump solution."+e );
        }
	}
	
	public void destroy()
	{
		allModRefSets.clear();
		stmt2ModRefSet.clear();
		selfMap.clear();
		tmpVar = null;
		System.gc(); System.gc(); System.gc();
	}
	
	
	// -----------------------------------------------------------------------------
	/**
	 * Is this statement a load or a store statment?
	 * @param lop
	 * @param rop
	 * @return -1 if it is neither a load or store. 1 if it is a store, 2 if it is a load.
	 */
	private int loadOrStoreStmt(Value lop, Value rop)
	{
		if ( lop instanceof StaticFieldRef ||
				lop instanceof InstanceFieldRef ||
				lop instanceof ArrayRef )
			return modRefSet.MODSET;
		else if ( rop instanceof StaticFieldRef ||
				rop instanceof InstanceFieldRef ||
				rop instanceof ArrayRef )
			return modRefSet.REFSET;
		
		return -1;
	}
	
	private CallsiteContextVar getContextVar( CgEdge ctxt, Node vn )
	{
		tmpVar.context = ctxt;
		tmpVar.var = vn;
		CallsiteContextVar ccVar = selfMap.searchFor(tmpVar);
		
		if ( ccVar == null ) {
			ccVar = new CallsiteContextVar(ctxt, vn);
			selfMap.add(ccVar);
		}
		
		return ccVar;
	}
	
	private modRefSet generateModRefSet( CgEdge ctxt, Stmt st, int type )
	{
		modRefSet set = new modRefSet( type, st, ctxt );
		allModRefSets.add( set );
		stmt2ModRefSet.put(st, set);
		return set;
	}
}
