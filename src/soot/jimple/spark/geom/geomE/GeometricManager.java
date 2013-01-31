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

import soot.jimple.spark.geom.geomPA.IFigureManager;
import soot.jimple.spark.geom.geomPA.RectangleNode;
import soot.jimple.spark.geom.geomPA.SegmentNode;

/**
 * This class implements the figure manager.
 * 
 * Currently, we apply a naive management strategy:
 * For each type of object, we maintain a linked list. If we insert a new object, we don't test if
 * all the geometric objects on the plane together can cover the new object. Instead, we test if there is 
 * one object already covers the new object.
 * 
 * @author xiao
 *
 */
public class GeometricManager extends IFigureManager
{
	public static final int Divisions = 2;

	// The type ID for different figures
	public static final int ONE_TO_ONE = 0;
	public static final int MANY_TO_MANY = 1;
	public static final int Undefined_Mapping = -1;
	
	// Private fields for each instance
	private SegmentNode header[] = { null, null };
	private int size[] = { 0, 0 };
	private boolean hasNewFigure = false;

	
	public SegmentNode[] getFigures() 
	{
		return header;
	}

	public int[] getSizes()
	{
		return size;
	}
	
	public boolean isThereUnprocessedFigures()
	{
		return hasNewFigure;
	}
	
	/**
	 * Remove the new labels for all the figures.
	 */
	public void flush()
	{
		hasNewFigure = false;
		
		for ( int i = 0; i < Divisions; ++i ) {
			SegmentNode p = header[i];
			while ( p != null && p.is_new == true ) {
				p.is_new = false;
				p = p.next;
			}
		}
	}
	
	/**
	 * Insert a new figure into this manager if it is not covered by any exisiting figure.
	 */
	public SegmentNode addNewFigure(int code, RectangleNode pnew) 
	{
		SegmentNode p;
		
		// We first check if there is an existing object contains this new object
		if ( checkRedundancy( code, pnew ) )
			return null;
		
		// Oppositely, we check if any existing objects are obsoleted
		filterOutDuplicates( code, pnew );
		
		// Ok, now we generate a copy
		if ( code == GeometricManager.ONE_TO_ONE ) {
			p = getSegmentNode();
			p.copySegment(pnew);
		}
		else {
			p = getRectangleNode();
			((RectangleNode)p).copyRectangle(pnew);
		}
		
		hasNewFigure = true;
		p.next = header[code];
		header[code] = p;
		size[code]++;
		
		return p;
	}

	/**
	 * Merge the set of objects in the same category into one.
	 */
	public void mergeFigures( int buget_size ) 
	{
		RectangleNode p;
		
		// We don't merge the figures if there are no new figures in this geometric manager
		if ( !hasNewFigure ) return;
		
		for ( int i = 0; i < Divisions; ++i ) {
			p = null;

			if ( size[i] > buget_size && header[i].is_new == true ) {
				// Merging is finding the bounding rectangles for every type of objects
				
				switch ( i ) {
				case GeometricManager.ONE_TO_ONE:
					p = mergeOneToOne();
					break;
				
				case GeometricManager.MANY_TO_MANY:
					p = mergeManyToMany();
					break;
				}
			}
			
			if ( p != null ) {
				if ( i == GeometricManager.ONE_TO_ONE ) {
					if ( checkRedundancy(GeometricManager.MANY_TO_MANY, p) ) continue;
					filterOutDuplicates(GeometricManager.MANY_TO_MANY, p);
				}
				
				p.next = header[GeometricManager.MANY_TO_MANY];
				header[GeometricManager.MANY_TO_MANY] = p;
				size[GeometricManager.MANY_TO_MANY]++;
			}
		}
	}
	
	/**
	 * The lines that are included in some rectangles can be deleted.
	 */
	public void removeUselessSegments() 
	{
		SegmentNode p = header[GeometricManager.ONE_TO_ONE];
		SegmentNode q = null;
		int countAll = 0;
		
		while (p != null) {
			SegmentNode temp = p.next;
			if (!isContainedInRectangles(p)) {
				p.next = q;
				q = p;
				++countAll;
			}
			else {
				reclaimSegmentNode(p);
			}
			p = temp;
		}

		size[GeometricManager.ONE_TO_ONE] = countAll;
		header[GeometricManager.ONE_TO_ONE] = q;
	}
	
	/**
	 * Is the input line covered by any rectangle?
	 * @param pnew, must be a line
	 * @return
	 */
	private boolean isContainedInRectangles(SegmentNode pnew)
	{
		SegmentNode p = header[ GeometricManager.MANY_TO_MANY ];
		
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
	private boolean checkRedundancy(int code, RectangleNode pnew) 
	{
		// Expand it temporarily
		if (code == GeometricManager.ONE_TO_ONE)
			pnew.L_prime = pnew.L;
		
		// Check redundancy
		for ( int i = code; i <= GeometricManager.MANY_TO_MANY; ++i ) {
			SegmentNode p = header[i];
			
			while ( p != null ) {
				switch ( i ) {
				case GeometricManager.ONE_TO_ONE:
					if ( (p.I2 - p.I1) == (pnew.I2 - pnew.I1) ) {
						// Have the same intercept and it is completely contained in an existing segment
						if ( pnew.I1 >= p.I1 && (pnew.I1 + pnew.L) <= (p.I1 + p.L) )
							return true;
					}
					break;
					
				case GeometricManager.MANY_TO_MANY:
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
	private void filterOutDuplicates(int code, SegmentNode p) 
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
				case GeometricManager.ONE_TO_ONE:
					if ( code == GeometricManager.MANY_TO_MANY ) {
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
					
				case GeometricManager.MANY_TO_MANY:
					if ( pold.I1 >= p.I1 && pold.I2 >= p.I2 ) {
						if ( (pold.I1 + pold.L) <= (p.I1 + p.L) &&
								(pold.I2 + ((RectangleNode)pold).L_prime) <= (p.I2 + ((RectangleNode)p).L_prime) )
							flag = true;
					}
					break;
				}
				
				if ( flag == false ) {
					// We keep this figure
					if ( q_head == null )
						q_head = pold;
					else
						q_tail.next = pold;
					q_tail = pold;
					
					++countAll;
					pold = pold.next;
				}
				else {
					// We reclaim this figure
					if ( i == GeometricManager.ONE_TO_ONE )
						pold = reclaimSegmentNode(pold);
					else
						pold = reclaimRectangleNode(pold);
				}
			}
			
			if ( q_tail != null ) q_tail.next = null;
			
			header[i] = q_head;
			size[i] = countAll;
		}
	}

	/**
	 * Find the bounding rectangle for all the rectangle figures.
	 * @return
	 */
	private RectangleNode mergeManyToMany() 
	{
		long x_min = Long.MAX_VALUE, y_min = Long.MAX_VALUE;
		long x_max = Long.MIN_VALUE, y_max = Long.MIN_VALUE;
		
		RectangleNode p = (RectangleNode)header[GeometricManager.MANY_TO_MANY];
		header[GeometricManager.MANY_TO_MANY] = null;
		size[GeometricManager.MANY_TO_MANY] = 0;
		
		while ( p != null ) {
			if ( p.I1 < x_min ) x_min = p.I1;
			if ( p.I2 < y_min ) y_min = p.I2;
			if ( p.I1 + p.L > x_max ) x_max = p.I1 + p.L;
			if ( p.I2 + p.L_prime > y_max ) y_max = p.I2 + p.L_prime;
			p = (RectangleNode)reclaimRectangleNode(p);
		}
		
		// We assume the list has at least one element
		p = getRectangleNode();
		p.I1 = x_min;
		p.I2 = y_min;
		p.L = x_max - x_min;
		p.L_prime = y_max - y_min;
		p.next = null;
		
		return p;
	}

	/**
	 * Find the bounding rectangle for all segment figures.
	 * @return
	 */
	private RectangleNode mergeOneToOne() 
	{
		long x_min = Long.MAX_VALUE, y_min = Long.MAX_VALUE;
		long x_max = Long.MIN_VALUE, y_max = Long.MIN_VALUE;
		
		SegmentNode p = header[GeometricManager.ONE_TO_ONE];
		header[GeometricManager.ONE_TO_ONE] = null;
		size[GeometricManager.ONE_TO_ONE] = 0;
		
		while ( p != null ) {
			if ( p.I1 < x_min ) x_min = p.I1;
			if ( p.I2 < y_min ) y_min = p.I2;
			if ( p.I1 + p.L > x_max ) x_max = p.I1 + p.L;
			if ( p.I2 + p.L > y_max ) y_max = p.I2 + p.L;
			p = reclaimSegmentNode(p);
		}
		
		RectangleNode q = getRectangleNode();
		q.I1 = x_min;
		q.I2 = y_min;
		q.L = x_max - x_min;
		q.L_prime = y_max - y_min;
		
		return q;
	}
}
