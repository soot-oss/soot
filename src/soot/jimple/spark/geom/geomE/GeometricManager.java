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
package soot.jimple.spark.geom.geomE;

import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.geom.geomPA.RectangleNode;
import soot.jimple.spark.geom.geomPA.SegmentNode;

/**
 * Currently, we apply a naive management strategy:
 * For each type of object, we maintain a linked list. If we insert a new object, we don't test if
 * all the geometric objects on the plane together can cover the new object. Instead, we test if there is 
 * one object already covers the new object.
 * 
 * @author richardxx
 * @since 1/11/2010
 */
public class GeometricManager 
{
	public static final int Divisions = 2;
//	public static final int sizeIncrement = (1<<16) + 1;
	private SegmentNode header[] = { null, null };
	/*
	 * The size field is divided into two sections. 
	 * The upper 16-bits records the number of shapes, the lower 16-bits records how many of them are newly added.
	 */
	private int size[] = { 0, 0 };
	private boolean hasNewObject = false;
	
	public SegmentNode[] getObjects() 
	{
		return header;
	}

	public int[] getSizes()
	{
		return size;
	}
	
	public boolean isThereUnprocessedObject()
	{
		return hasNewObject;
	}
	
	public void flush()
	{
		hasNewObject = false;
		
		for ( int i = 0; i < Divisions; ++i ) {
			SegmentNode p = header[i];
			while ( p != null && p.is_new == true ) {
				p.is_new = false;
				p = p.next;
			}
		}
	}
	
	public SegmentNode addNewObject(int code, RectangleNode pnew) 
	{
		SegmentNode p;
		
		// We first check if there is an existing object contains this new object
		if ( check_redundancy( code, pnew ) )
			return null;
		
		// Oppositely, we check if any existing objects are obsoleted
		filter_out_duplicates( code, pnew );
		
		// Ok, now we generate a copy
		if ( code == GeomPointsTo.ONE_TO_ONE )
			p = new SegmentNode( pnew );
		else
			p = new RectangleNode( pnew );
		
		hasNewObject = true;
		p.next = header[code];
		header[code] = p;
		size[code]++;
		
		return p;
	}

	/**
	 * merge the set of objects in the same category into one.
	 * @param buget_size
	 */
	public void mergeObjects( int buget_size ) 
	{
		RectangleNode p;
		
		for ( int i = 0; i < Divisions; ++i ) {
			p = null;

			if ( size[i] > buget_size && header[i].is_new == true ) {
				// Merging is finding the bounding rectangles for every type of objects
				
				switch ( i ) {
				case GeomPointsTo.ONE_TO_ONE:
					p = merge_one_to_one();
					break;
				
				case GeomPointsTo.MANY_TO_MANY:
					p = merge_many_to_many();
					break;
				}
			}
			
			if ( p != null ) {
				if ( i == GeomPointsTo.ONE_TO_ONE ) {
					if ( check_redundancy(GeomPointsTo.MANY_TO_MANY, p) ) continue;
					filter_out_duplicates(GeomPointsTo.MANY_TO_MANY, p);
				}
				
				p.next = header[GeomPointsTo.MANY_TO_MANY];
				header[GeomPointsTo.MANY_TO_MANY] = p;
				size[GeomPointsTo.MANY_TO_MANY]++;
			}
		}
	}
	
	/**
	 * The lines that are included in some rectangles can be deleted.
	 */
	public void remove_useless_lines() 
	{
		SegmentNode pnew = header[GeomPointsTo.ONE_TO_ONE];
		SegmentNode q = null;
		int countAll = 0;
		
		while (pnew != null) {
			SegmentNode temp = pnew.next;
			if (!is_contained_in_rectangles(pnew)) {
				pnew.next = q;
				q = pnew;
				++countAll;
			}
			pnew = temp;
		}

//		size[IntervalPointsTo.One_To_One] = (countAll) << 16;
		size[GeomPointsTo.ONE_TO_ONE] = countAll;
		header[GeomPointsTo.ONE_TO_ONE] = q;
	}
	
	/**
	 * Is the input line covered by any rectangle?
	 * @param pnew, must be a line
	 * @return
	 */
	private boolean is_contained_in_rectangles(SegmentNode pnew)
	{
		SegmentNode p = header[ GeomPointsTo.MANY_TO_MANY ];
		
		while ( p != null ) {
			if (pnew.I1 >= p.I1 && pnew.I2 >= p.I2) {
				if ((pnew.I1 + pnew.L) <= (p.I1 + p.L)
						&& (pnew.I2 + pnew.L) <= (p.I2 + ((RectangleNode) p).L_prime))
					return true;
			}
			
			p = p.next;
		}
		
		return false;
	}
	
	/**
	 * Judge if the newly added geometric shape is redundant.
	 * @param code
	 * @param pnew
	 * @return
	 */
	private boolean check_redundancy(int code, RectangleNode pnew) 
	{
		// Expand it temporarily
		if (code == GeomPointsTo.ONE_TO_ONE)
			pnew.L_prime = pnew.L;
		
		// Check redundancy
		for ( int i = code; i <= GeomPointsTo.MANY_TO_MANY; ++i ) {
			SegmentNode p = header[i];
			
			while ( p != null ) {
				switch ( i ) {
				case GeomPointsTo.ONE_TO_ONE:
					if ( (p.I2 - p.I1) == (pnew.I2 - pnew.I1) ) {
						// Have the same intercept and it is completely contained in an existing segment
						if ( pnew.I1 >= p.I1 && (pnew.I1 + pnew.L) <= (p.I1 + p.L) )
							return true;
					}
					break;
					
				case GeomPointsTo.MANY_TO_MANY:
					if ( pnew.I1 >= p.I1 && pnew.I2 >= p.I2 ) {
						if ( (pnew.I1 + pnew.L) <= (p.I1 + p.L) &&
								(pnew.I2 + pnew.L_prime) <= (p.I2 + ((RectangleNode)p).L_prime) )
							return true;
					}
					break;
				}
				
				p = p.next;
			}
		}
		
		return false;
	}
	
	/**
	 * Drop the redundant existing objects. 
	 * @param code
	 * @param p
	 */
	private void filter_out_duplicates(int code, SegmentNode p) 
	{
		boolean flag;
		SegmentNode q_head, q_tail;
		SegmentNode pold;
		int countAll;
	
		for ( int i = code; i > -1; --i ) {
			pold = header[i];
			q_head = null;
			q_tail = null;
			countAll = 0;
			
			while ( pold != null ) {
				flag = false;
				
				switch ( i ) {
				case GeomPointsTo.ONE_TO_ONE:
					if ( code == GeomPointsTo.MANY_TO_MANY ) {
						if (pold.I1 >= p.I1 && pold.I2 >= p.I2) {
							if ((pold.I1 + pold.L) <= (p.I1 + p.L)
									&& (pold.I2 + pold.L) <= (p.I2 + ((RectangleNode) p).L_prime))
								flag = true;
						}
					}
					else {
						if ( (p.I2 - p.I1) == (pold.I2 - pold.I1) ) {
							if ( pold.I1 >= p.I1 && (pold.I1 + pold.L) <= (p.I1 + p.L) )
								flag = true;
						}
					}
					break;
					
				case GeomPointsTo.MANY_TO_MANY:
					if ( pold.I1 >= p.I1 && pold.I2 >= p.I2 ) {
						if ( (pold.I1 + pold.L) <= (p.I1 + p.L) &&
								(pold.I2 + ((RectangleNode)pold).L_prime) <= (p.I2 + ((RectangleNode)p).L_prime) )
							flag = true;
					}
					break;
				}
				
				if ( flag == false ) {
					if ( q_head == null )
						q_head = pold;
					else
						q_tail.next = pold;
					q_tail = pold;
					
					++countAll;
				}
				
				pold = pold.next;
			}
			
			if ( q_tail != null )
				q_tail.next = null;
			
			header[i] = q_head;
			size[i] = countAll;
		}
	}

	private RectangleNode merge_many_to_many() 
	{
		long x_min = Long.MAX_VALUE, y_min = Long.MAX_VALUE;
		long x_max = Long.MIN_VALUE, y_max = Long.MIN_VALUE;
		
		RectangleNode p = (RectangleNode)header[GeomPointsTo.MANY_TO_MANY];
		
		while ( p != null ) {
			if ( p.I1 < x_min ) x_min = p.I1;
			if ( p.I2 < y_min ) y_min = p.I2;
			if ( p.I1 + p.L > x_max ) x_max = p.I1 + p.L;
			if ( p.I2 + p.L_prime > y_max ) y_max = p.I2 + p.L_prime;
			p = (RectangleNode)p.next;
		}
		
		// We assume the list has at least one element
		p = (RectangleNode)header[GeomPointsTo.MANY_TO_MANY];
		header[GeomPointsTo.MANY_TO_MANY] = null;
		size[GeomPointsTo.MANY_TO_MANY] = 0;
		p.I1 = x_min;
		p.I2 = y_min;
		p.L = x_max - x_min;
		p.L_prime = y_max - y_min;
		p.next = null;
		
		return p;
	}

	private RectangleNode merge_one_to_one() 
	{
		long x_min = Long.MAX_VALUE, y_min = Long.MAX_VALUE;
		long x_max = Long.MIN_VALUE, y_max = Long.MIN_VALUE;
		
		SegmentNode p = header[GeomPointsTo.ONE_TO_ONE];
		header[GeomPointsTo.ONE_TO_ONE] = null;
		size[GeomPointsTo.ONE_TO_ONE] = 0;
		
		while ( p != null ) {
			if ( p.I1 < x_min ) x_min = p.I1;
			if ( p.I2 < y_min ) y_min = p.I2;
			if ( p.I1 + p.L > x_max ) x_max = p.I1 + p.L;
			if ( p.I2 + p.L > y_max ) y_max = p.I2 + p.L;
			p = p.next;
		}
		
		RectangleNode q = new RectangleNode();
		q.I1 = x_min;
		q.I2 = y_min;
		q.L = x_max - x_min;
		q.L_prime = y_max - y_min;
		
		return q;
	}
}
