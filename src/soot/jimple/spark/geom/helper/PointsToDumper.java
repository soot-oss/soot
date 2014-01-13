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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import soot.Scene;
import soot.jimple.spark.geom.dataRep.CallsiteContextVar;
import soot.jimple.spark.geom.dataRep.IntervalContextVar;
import soot.jimple.spark.geom.geomPA.CgEdge;
import soot.jimple.spark.geom.geomPA.Constants;
import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.geom.geomPA.IVarAbstraction;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.LocalVarNode;
import soot.jimple.spark.pag.Node;

/**
 * Output the points-to matrix in various formats.
 * This is used to facilitate the experiments based on points-to analysis result outside SOOT.
 * 
 * @author xiao
 *
 */
public class PointsToDumper 
{	
	private GeomPointsTo ptsProvider;
	
	public PointsToDumper()
	{
		ptsProvider = (GeomPointsTo)Scene.v().getPointsToAnalysis();
		// We map our full sensitive result to the 1CFA result
		ContextTranslator.build_1cfa_map( ptsProvider );
	}
	
	/**
	 * Dump the context insensitive points-to result generated from the geometric analysis by throwing away the context information.
	 * @param ptsProvider
	 */
	public void dump_context_insensitive_mapped_result(  )
	{
		try {
			final PrintWriter file = new PrintWriter( ptsProvider.createOutputFile("p0_o0.ptm") );
			
			// rows and columns
			file.println( ptsProvider.getNumberOfPointers() + " " + ptsProvider.getNumberOfObjects() );
			
			for (IVarAbstraction pn : ptsProvider.pointers) {
				pn = pn.getRepresentative();
				Set<AllocNode> objSet = pn.get_all_points_to_objects();
				
				file.print( objSet.size() );
				
				for ( AllocNode obj : objSet ) {
					IVarAbstraction pobj = ptsProvider.findInternalNode(obj);
					file.print( " " + pobj.getNumber() );
				}
				
				file.println();
			}
			
			file.close();
        } catch( IOException e ) {
            throw new RuntimeException( "Couldn't dump solution."+e );
        }
	}
	
	/**
	 * Dump the 1-CFA pointer context sensitive but object insensitive result.
	 */
	public void dump_pointer_1cfa_object_insensitive_mapped_result( )
	{
		int var_num;
		final List<AllocNode> list = new ArrayList<AllocNode>();

		try {
			final PrintWriter file = new PrintWriter( ptsProvider.createOutputFile("p1_o0.ptm") );
			
			// points-to matrix rows and columns
			var_num = ContextTranslator.pts_1cfa_map.size();
			file.println( var_num + " " + ptsProvider.getNumberOfObjects() );
			
			for ( CallsiteContextVar cvar : ContextTranslator.pts_1cfa_map ) {
				Node v = cvar.var;
				IVarAbstraction pn = ptsProvider.findInternalNode( v );
				pn = pn.getRepresentative();

				if ( v instanceof LocalVarNode ) {
					// We map the local pointer to its 1-cfa versions
					CgEdge p = cvar.context;
					
					long l = p.map_offset;
					long r = l + ptsProvider.max_context_size_block[p.s];
					list.clear();
					
					for ( AllocNode obj : pn.get_all_points_to_objects() ) {
						if ( pn.pointer_interval_points_to(l, r, obj) )
							list.add(obj);
					}
					
					file.print( list.size() );
				
					for ( AllocNode obj : list ) {
						IVarAbstraction po = ptsProvider.findInternalNode(obj);
						file.print( " " + po.getNumber() );
					}

					file.println();
				}
				else {
					Set<AllocNode> objSet = pn.get_all_points_to_objects();
					file.print( objSet.size() );
					
					for ( AllocNode obj : objSet ) {
						IVarAbstraction po = ptsProvider.findInternalNode(obj);
						file.print( " " + po.getNumber() );
					}
					
					file.println();
				}
			}
			
			file.close();
        } catch( IOException e ) {
            throw new RuntimeException( "Couldn't dump solution."+e );
        }
	}
	
	/**
	 * Dump the pointer insensitive but object 1-CFA context sensitive points-to result.
	 */
	public void dump_pointer_insensitive_object_1cfa_mapped_result( )
	{
		int var_num, obj_num;
		Obj_1cfa_extractor objs_1cfa = new Obj_1cfa_extractor();
		
		try {
			final PrintWriter file = new PrintWriter( ptsProvider.createOutputFile("p0_o1.ptm") );
			
			var_num = ptsProvider.getNumberOfPointers();
			obj_num = ContextTranslator.objs_1cfa_map.size();
			
			// The points-to matrix rows and columns
			file.println( var_num + " " + obj_num );
			
			// The points-to matrix
			for (IVarAbstraction pn : ptsProvider.pointers ) {
				pn = pn.getRepresentative();
				objs_1cfa.prepare();
				pn.get_all_context_sensitive_objects(1, Constants.MAX_CONTEXTS, objs_1cfa);
				
				// output
				file.print( objs_1cfa.outList.size() );
				for ( CallsiteContextVar cobj : objs_1cfa.outList ) {
					file.print( " " + cobj.getNumber() );
				}
				
				file.println();
			}
			
			// The context objects-to-callsites mapping table
			obj_num = ptsProvider.getNumberOfObjects();
			file.println( obj_num );
			int i = 1, j = 0;
			
			while ( true ) {
				// We first identify all the objects that are mapped to the same callsite
				CallsiteContextVar first_obj = ContextTranslator.objs_1cfa_map.get(j);
				while ( i < obj_num ) {
					CallsiteContextVar cobj = ContextTranslator.objs_1cfa_map.get(i);
					if ( cobj.var != first_obj.var )
						break;
					++i;
				}
				
				// output
				file.print( (i-j) );
				while ( j < i ) {
					file.print(" " + j);
					++j;
				}
				file.println();
				
				if ( i == obj_num )
					break;
			}
			
			file.close();
        } catch( IOException e ) {
            throw new RuntimeException( "Couldn't dump solution."+e );
        }
		finally {
			objs_1cfa = null;
		}
	}
	
	/**
	 * Dump both the pointer and object 1-CFA context sensitive points-to result.
	 */
	public void dump_pointer_object_1cfa_mapped_result( )
	{
		int var_num, obj_num;
		long l, r;
		Obj_1cfa_extractor objs_1cfa = new Obj_1cfa_extractor();
		
		try {
			final PrintWriter file = new PrintWriter( ptsProvider.createOutputFile("p1_o1.ptm") );
			
			// Matrix rows and columns
			var_num = ContextTranslator.pts_1cfa_map.size();
			obj_num = ContextTranslator.objs_1cfa_map.size();
			
			file.println( var_num + " " + obj_num );
			
			for ( CallsiteContextVar cvar : ContextTranslator.pts_1cfa_map ) {
				Node v = cvar.var;
				IVarAbstraction pn = ptsProvider.findInternalNode( v );
				pn = pn.getRepresentative();
				objs_1cfa.prepare();
				
				if ( cvar.context != null ) {
					CgEdge p = cvar.context;
					l = p.map_offset;
					r = l + ptsProvider.max_context_size_block[p.s];
				}
				else {
					l = 1;
					r = Constants.MAX_CONTEXTS;
				}
					
				pn.get_all_context_sensitive_objects(l, r, objs_1cfa);
				
				// output
				file.print( objs_1cfa.outList.size() );
				for ( CallsiteContextVar cobj : objs_1cfa.outList ) {
					file.print( " " + cobj.getNumber() );
				}
				
				file.println();
			}
			
			file.close();
        } catch( IOException e ) {
            throw new RuntimeException( "Couldn't dump solution."+e );
        }
		finally {
			objs_1cfa = null;
		}
	}
	
	/**
	 * The pointers are mapped to 1cfa manner but the objects are kept full sensitive.
	 * We map the result to matrix form so it looks like so complicated....
	 */
	public void dump_pointer_1cfa_object_full_result()
	{
		int var_num;
		long l, r;
		Obj_full_extractor objs_full = new Obj_full_extractor();
		Map<Integer, TreeSet<Long> > endPointsTree = new HashMap<Integer, TreeSet<Long>>();
		Map<Integer, long[] > endPointsList = new HashMap<Integer, long[] >();
		int[] offset;
		
		try {
			final PrintWriter file = new PrintWriter( ptsProvider.createOutputFile("p1_oN.ptm") );
			
			// Pass 1: We collect the intervals for every object
			for ( CallsiteContextVar cvar : ContextTranslator.pts_1cfa_map ) {
				Node v = cvar.var;
				IVarAbstraction pn = ptsProvider.findInternalNode( v );
				pn = pn.getRepresentative();
				objs_full.prepare();
				
				// For the context of this pointer, we calculate its interval context range
				if ( cvar.context != null ) {
					CgEdge p = cvar.context;
					l = p.map_offset;
					r = l + ptsProvider.max_context_size_block[p.s];
				}
				else {
					l = 1;
					r = Constants.MAX_CONTEXTS;
				}
				
				// Then obtain a list of interval context described objects
				pn.get_all_context_sensitive_objects(l, r, objs_full);
				objs_full.finish();
				
				// We collect the end points of these intervals
				// They are collected on the per object basis
				for ( IntervalContextVar icv : objs_full.outList ) {
					IVarAbstraction pobj = ptsProvider.findInternalNode(icv.var);
					TreeSet<Long> tree = endPointsTree.get(pobj.id);
					if ( tree == null ) {
						tree = new TreeSet<Long>();
						endPointsTree.put(pobj.id, tree);
					}
					tree.add(icv.L);
					tree.add(icv.R);
				}
			}
			
			// Pass 2: We sort the end points
			// offset is used to number the slabs globally
			int num_obj = ptsProvider.getNumberOfObjects();
			offset = new int[ num_obj + 1 ];
			offset[0] = 0;
			
			for ( int i = 0; i < num_obj; i++ ) {
				TreeSet<Long> tree = endPointsTree.get(i);
				offset[i+1] = offset[i];
				if ( tree == null ) continue;
				
				// The points are sorted already
				int j = 0;
				long[] list = new long[ tree.size() ];
				offset[i+1] += tree.size();
				for ( Long v : tree ) {
					// Every slab is created for every two consecutive points
					// All the end points are sorted already
					list[j] = v.longValue();
					++j;
				}
				
				tree.clear();
				endPointsList.put(i, list);
			}
			
			endPointsTree = null;
			objs_full.prepare();
			System.gc(); System.gc(); System.gc();
			
			// Output the matrix size
			var_num = ContextTranslator.pts_1cfa_map.size();
			file.println( var_num + " " + offset[num_obj] );
			
			// Pass 3: We use binary search to build the points-to targets for every pointer
			Set<Integer> outList = new TreeSet<Integer>();
			for ( CallsiteContextVar cvar : ContextTranslator.pts_1cfa_map ) {
				Node v = cvar.var;
				IVarAbstraction pn = ptsProvider.findInternalNode( v );
				pn = pn.getRepresentative();
				objs_full.prepare();
				
				if ( cvar.context != null ) {
					CgEdge p = cvar.context;
					l = p.map_offset;
					r = l + ptsProvider.max_context_size_block[p.s];
				}
				else {
					l = 1;
					r = Constants.MAX_CONTEXTS;
				}
				
				pn.get_all_context_sensitive_objects(l, r, objs_full);
				objs_full.finish();
				
				// binary search and build new slabs
				outList.clear();
				for ( IntervalContextVar icv : objs_full.outList ) {
					IVarAbstraction pobj = ptsProvider.findInternalNode(icv.var);
					long[] list = endPointsList.get(pobj.id);
					int i = Arrays.binarySearch(list, icv.L);
					int j = Arrays.binarySearch(list, icv.R);
					assert i >= 0;
					assert j > i;
					
					// The global ID of the slab [i, i+1) is just the local index i plus the offset of this object
					while ( i < j ) {
						outList.add( i + offset[pobj.id] );
						++i;
					}
				}
				
				// output
				file.print( outList.size() );
				for ( Integer id : outList ) file.print( " " + id );
				file.println();
			}
			
			file.close();
		} catch( IOException e ) {
            throw new RuntimeException( "Couldn't dump solution."+e );
        }
		finally {
			objs_full = null;
			endPointsList = null;
		}
	}
}