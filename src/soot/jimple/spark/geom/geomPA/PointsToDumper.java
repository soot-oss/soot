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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import soot.SootMethod;
import soot.jimple.spark.geom.geomPA.CallsiteContextVar;
import soot.jimple.spark.geom.geomPA.CgEdge;
import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.geom.geomPA.IVarAbstraction;
import soot.jimple.spark.geom.geomPA.ZArrayNumberer;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.LocalVarNode;
import soot.jimple.spark.pag.Node;

/**
 * Output the points-to matrix in various formats.
 * This is used to facilitate the pointer analysis experimenting outside SOOT.
 * 
 * @author xiao
 *
 */
public class PointsToDumper 
{	
	/**
	 * Dump the context insensitive points-to result generated from the geometric analysis by throwing away the context information.
	 * @param ptsProvider
	 */
	public static void dump_context_insensitive_mapped_result( GeomPointsTo ptsProvider )
	{
		try {
			final PrintWriter file = new PrintWriter(new FileOutputStream(
					new File("pags", ptsProvider.dump_file_name + ".ptm")));
			
			// First dump a descriptor
			// 0 is the result of pointer and object insensitive matrix
			file.println( "0" );
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
	public static void dump_pointer_1cfa_object_insensitive_mapped_result( GeomPointsTo ptsProvider )
	{
		int var_num;
		final List<AllocNode> list = new ArrayList<AllocNode>();

		try {
			final PrintWriter file = new PrintWriter(new FileOutputStream(
					new File("pags", ptsProvider.dump_file_name + ".ptm")));
			
			// First dump a descriptor
			// 0 is the result of pointer and object insensitive matrix
			file.println( "16" );		// 0x00000010
			
			var_num = 0;
			for ( IVarAbstraction pn : ptsProvider.pointers ) {
				pn = pn.getRepresentative();
				Node v = pn.getWrappedNode();
				if ( v instanceof LocalVarNode ) {
					LocalVarNode lvn = (LocalVarNode)v;
					SootMethod sm = lvn.getMethod();
					int sm_int = ptsProvider.getIDFromSootMethod(sm);
					var_num += ptsProvider.getCallEdgesInto(sm_int).size();
				}
				else
					var_num++;
			}
			
			file.println( var_num + " " + ptsProvider.getNumberOfObjects() );
			
			for (IVarAbstraction pn : ptsProvider.pointers ) {
				pn = pn.getRepresentative();
				Node v = pn.getWrappedNode();

				if ( v instanceof LocalVarNode ) {
					// We map the local pointer to its 1-cfa versions
					LocalVarNode lvn = (LocalVarNode)v;
					SootMethod sm = lvn.getMethod();
					int sm_int = ptsProvider.getIDFromSootMethod(sm);
					LinkedList<CgEdge> edges = ptsProvider.getCallEdgesInto( sm_int );
					
					for ( Iterator<CgEdge> it = edges.iterator(); it.hasNext(); ) {
						CgEdge p = it.next();
					
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
	public static void dump_pointer_insensitive_object_1cfa_mapped_result( GeomPointsTo ptsProvider )
	{
		int var_num, obj_num;
		ZArrayNumberer<CallsiteContextVar> ct_sens_objs = new ZArrayNumberer<CallsiteContextVar>();
		Vector<CallsiteContextVar> outList = new Vector<CallsiteContextVar>();
		CallsiteContextVar context_obj = null;

		try {
			final PrintWriter file = new PrintWriter(new FileOutputStream(
					new File("pags", ptsProvider.dump_file_name + ".ptm")));
			
			// First dump a descriptor
			// 0 is the result of pointer and object insensitive matrix
			file.println( "1" );		//0x00000001
			
			var_num = 0;
			obj_num = 0;
			
			for ( IVarAbstraction pn : ptsProvider.pointers ) {
				if ( pn == pn.getRepresentative() )
					var_num++;
			}
			
			// Construct all the context sensitive objects
			for ( IVarAbstraction pobj : ptsProvider.allocations ) {
				AllocNode obj = (AllocNode)pobj.getWrappedNode();
				SootMethod sm = obj.getMethod();
				
				if ( sm == null ) {
					obj_num++;
					context_obj = new CallsiteContextVar(null, obj);
					ct_sens_objs.add(context_obj);
				}
				else {
					int sm_int = ptsProvider.getIDFromSootMethod(sm);
					if ( ptsProvider.isReachableMethod(sm_int) ) {
						// We also temporarily build the 1cfa object
						List<CgEdge> edges = ptsProvider.getCallEdgesInto(sm_int);
						obj_num += edges.size();
						
						for ( CgEdge ce : edges ) {
							context_obj = new CallsiteContextVar(ce, obj);
							ct_sens_objs.add(context_obj);
						}
					}
				}
			}
			
			// The points-to matrix rows and columns
			file.println( var_num + " " + obj_num );
			
			// The points-to matrix
			for (IVarAbstraction pn : ptsProvider.pointers ) {
				if ( pn != pn.getRepresentative() )
					continue;
				
				file.print( pn.get_all_context_sensitive_objects(1, GeomPointsTo.MAX_CONTEXTS, ct_sens_objs, outList) );
				
				for ( CallsiteContextVar cobj : outList ) {
					cobj.inQ = false;
					file.print( " " + cobj.getNumber() );
				}
				file.println();
			}
			
			// The context objects to callsites table
			obj_num = ptsProvider.getNumberOfObjects();
			file.println( obj_num );
			int i = 1, j = 0;
			
			while ( true ) {
				// We first identify all the objects that are mapped to the same callsite
				CallsiteContextVar first_obj = ct_sens_objs.get(j);
				while ( i < obj_num ) {
					CallsiteContextVar cobj = ct_sens_objs.get(i);
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
	}
	
	/**
	 * Dump both the pointer and object 1-CFA context sensitive points-to result.
	 */
	public static void dump_pointer_object_1cfa_mapped_result( GeomPointsTo ptsProvider )
	{
		int var_num, obj_num;
		int sm_int;
		ZArrayNumberer<CallsiteContextVar> ct_sens_objs = new ZArrayNumberer<CallsiteContextVar>();
		Vector<CallsiteContextVar> outList = new Vector<CallsiteContextVar>();
		CallsiteContextVar context_obj = null;
		
		try {
			final PrintWriter file = new PrintWriter(new FileOutputStream(
					new File("pags", ptsProvider.dump_file_name + ".ptm")));
			
			// First dump a descriptor
			// 0 is the result of pointer and object insensitive matrix
			file.println( "17" );		//0x00000011
			
			var_num = 0;
			obj_num = 0;
			
			for ( IVarAbstraction pn : ptsProvider.pointers ) {
//				pn = pn.getRepresentative();
				if ( pn != pn.getRepresentative() )
					continue;
				Node v = pn.getWrappedNode();
				if ( v instanceof LocalVarNode ) {
					LocalVarNode lvn = (LocalVarNode)v;
					SootMethod sm = lvn.getMethod();
					sm_int = ptsProvider.getIDFromSootMethod(sm);
					var_num += ptsProvider.getCallEdgesInto(sm_int).size();
				}
				else
					var_num++;
			}
			
			for ( IVarAbstraction pobj : ptsProvider.allocations ) {
				AllocNode obj = (AllocNode)pobj.getWrappedNode();
				SootMethod sm = obj.getMethod();
				
				if ( sm == null ) {
					obj_num++;
					context_obj = new CallsiteContextVar(null, obj);
					ct_sens_objs.add(context_obj);
				}
				else {
					sm_int = ptsProvider.getIDFromSootMethod(sm);
					if ( ptsProvider.isReachableMethod(sm_int) ) {
						// We also temporarily build the 1cfa object
						List<CgEdge> edges = ptsProvider.getCallEdgesInto(sm_int);
						obj_num += edges.size();
						
						for ( CgEdge ce : edges ) {
							context_obj = new CallsiteContextVar(ce, obj);
							ct_sens_objs.add(context_obj);
						}
					}
				}
			}
			
			// Matrix rows and columns
			file.println( var_num + " " + obj_num );
			
			for (IVarAbstraction pn : ptsProvider.pointers) {
//				pn = pn.getRepresentative();
				if ( pn != pn.getRepresentative() )
					continue;
				Node v = pn.getWrappedNode();
				
				if ( v instanceof LocalVarNode ) {
					// We map the local pointer to its 1-cfa versions
					LocalVarNode lvn = (LocalVarNode)v;
					SootMethod sm = lvn.getMethod();
					sm_int = ptsProvider.getIDFromSootMethod(sm);
					LinkedList<CgEdge> edges = ptsProvider.getCallEdgesInto(sm_int);
					
					for ( Iterator<CgEdge> it = edges.iterator(); it.hasNext(); ) {
						CgEdge p = it.next();
					
						long l = p.map_offset;
						long r = l + ptsProvider.max_context_size_block[p.s];
						
						file.print( pn.get_all_context_sensitive_objects(l, r, ct_sens_objs, outList) );
						
						for ( CallsiteContextVar cobj : outList ) {
							cobj.inQ = false;
							file.print( " " + cobj.getNumber() );
						}
						file.println();
					}
				}
				else {
					file.print( pn.get_all_context_sensitive_objects(1, GeomPointsTo.MAX_CONTEXTS, ct_sens_objs, outList) );
					
					for ( CallsiteContextVar cobj : outList ) {
						cobj.inQ = false;
						file.print( " " + cobj.getNumber() );
					}
					file.println();
				}
			}
			
			file.close();
        } catch( IOException e ) {
            throw new RuntimeException( "Couldn't dump solution."+e );
        }
	}
}
