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
package soot.jimple.spark.geom.ptinsE;

import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.geom.geomPA.SegmentNode;

public class PtInsIntervalManager {
	public static final int Divisions = 3;

	int size[] = {0, 0, 0};
	SegmentNode header[] = {null, null, null};
	private boolean hasNewObject = false;
	
	public SegmentNode[] get_intervals()
	{
		return header;
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
	
	public SegmentNode add_new_interval( long I1, long I2, long L )
	{
		int k;
		SegmentNode p;
		
		if ( I1 == 0 && I2 == 0 ) {
			// Directly clean all the existing intervals
			if ( header[0] != null && header[0].I2 == 0 )
				return null;
			
			p = new SegmentNode();
			
			k = 0;
			p.I1 = p.I2 = 0;
			size[0] = size[1] = size[2] = 0;
			header[0] = header[1] = header[2] = null;
			p.L = GeomPointsTo.MAX_CONTEXTS;
		}
		else {
			// Duplicate testing
			
			if ( I1 == 0 || I2 != 0 ) {
				p = header[0];
				while ( p != null ) {
					if ( (p.I2 <= I2) && (p.I2 + p.L >= I2 + L) )
						return null;
					p = p.next;
				}
			}
			
			if ( I2 == 0 || I1 != 0 ) {
				p = header[1];
				while ( p != null ) {
					if ( (p.I1 <= I1) && (p.I1 + p.L >= I1 + L) )
						return null;
					p = p.next;
				}
			}
			
			// Be careful of this!
			if ( I1 != 0 && I2 != 0 ) {
				p = header[2];
				while ( p != null ) {
					if (p.I1 - p.I2 == I1 - I2) {
						// On the same line
						if (p.I1 <= I1 && p.I1 + p.L >= I1 + L)
							return null;
					}
					
					p = p.next;
				}
			}
			
			k = (I1 == 0 ? 0 : (I2 == 0 ? 1 : 2) );
	
			// Insert the new interval immediately, and we delay the merging until necessary
			p = new SegmentNode(I1, I2, L);
			if ( k == 0 )
				clean_garbage_I1_is_zero(p);
			else if ( k == 1 )
				clean_garbage_I2_is_zero(p);
		}
		
		hasNewObject = true;
		size[k]++;
		p.next = header[k];
		header[k] = p;
		return p;
	}
	
	public void merge_points_to_tuples()
	{
		if ( size[2] > GeomPointsTo.max_cons_budget && 
				header[2].is_new == true ) {
			// After the merging, we must propagate this interval, thus it has to be a new interval
			
			SegmentNode p = collapse_I1( header[2] );
			clean_garbage_I1_is_zero(p);
			p.next = header[0];
			header[0] = p;
			header[2] = null;
			size[0]++;
			size[2] = 0;
		}

		if ( size[1] > GeomPointsTo.max_cons_budget &&
				header[1].is_new == true ) {
			
			header[1] = collapse_I2( header[1] );
			size[1] = 1;
		}
		
		if ( size[0] > GeomPointsTo.max_cons_budget &&
				header[0].is_new == true ) {
			
			header[0] = collapse_I1( header[0] );
			size[0] = 1;
		}
	}
	
	public void merge_flow_edges()
	{
		if ( size[2] > GeomPointsTo.max_pts_budget && 
				header[2].is_new == true ) {
			// After the merging, we must propagate this interval, thus it has to be a new interval
			
			SegmentNode p = collapse_I2( header[2] );
			clean_garbage_I2_is_zero(p);
			p.next = header[1];
			header[1] = p;
			header[2] = null;
			size[1]++;
			size[2] = 0;
		}

		if ( size[1] > GeomPointsTo.max_pts_budget &&
				header[1].is_new == true ) {
			
			header[1] = collapse_I2( header[1] );
			size[1] = 1;
		}
		
		if ( size[0] > GeomPointsTo.max_pts_budget &&
				header[0].is_new == true ) {
			
			header[0] = collapse_I1( header[0] );
			size[0] = 1;
		}
	}
	
	public void remove_useless_intervals()
	{
		int i;
		SegmentNode p, q, temp;
		
		p = header[2];
		size[2] = 0;
		q = null;
		while ( p != null ) {
			boolean contained = false;
			for ( i = 0; i < 2; ++i ) {
				temp = header[i];
				while ( temp != null ) {
					if ( temp.I1 == 0 || ((temp.I1 <= p.I1) && (temp.I1 + temp.L >= p.I1 + p.L)) ) {
						if (temp.I2 == 0
								|| ((temp.I2 <= p.I2) && (temp.I2 + temp.L >= p.I2 + p.L))) {
							contained = true;
							break;
						}
					}
					
					temp = temp.next;
				}
			}
			
			temp = p.next;
			if ( contained == false ) {
				p.next = q;
				q = p;
				++size[2];
			}
			p = temp;
		}
		
		header[2] = q;
	}
	
	/**
	 * Merge all the context sensitive intervals. The result is
	 * in the form (p, q, 0, I, L).
	 */
	private SegmentNode collapse_I1( SegmentNode mp ) 
	{
		long left, right, t;
		SegmentNode p;

		left = mp.I2;
		right = left + mp.L;
		p = mp.next;

		while (p != null) {
			if (p.I2 < left) left = p.I2;
			t = p.I2 + p.L;
			if ( t > right ) right = t;
			p = p.next;
		}

		//System.err.println( "~~~~~~~~~~~~Sorry, it happens~~~~~~~~" );
		mp.I1 = 0;
		mp.I2 = left;
		mp.L = right - left;
		mp.next = null;

		return mp;
	}
	
	/** The result is in the form: (p, q, I, 0, L)
	 */
	private SegmentNode collapse_I2( SegmentNode mp ) 
	{
		long left, right, t;
		SegmentNode p;

		left = mp.I1;
		right = left + mp.L;
		p = mp.next;

		while (p != null) {
			if (p.I1 < left) left = p.I1;
			t = p.I1 + p.L;
			if ( t > right ) right = t;
			p = p.next;
		}

		//System.err.println( "~~~~~~~~~~~~Sorry, it happens~~~~~~~~" );
		
		// Note, left could be 0. In that case, the propagation along this edge
		// becomes totally insensitive
		mp.I1 = left;
		mp.I2 = 0;
		mp.L = right - left;
		mp.next = null;
		
		return mp;
	}
	
	// Clean garbages in list that the information is already covered by mp
	// BTW, we do some simple concatenation 
	private void clean_garbage_I2_is_zero(SegmentNode mp) 
	{
		SegmentNode p, q, list;
		int num;
		long right, left;

		list = header[1];
		p = q = null;
		num = 0;
		left = mp.I1;
		right = left + mp.L;

		while (list != null) {
			if (list.I1 >= left) {
				if (list.I1 <= right) {
					if ( list.I1 + list.L > right ) {
						// We extend mp to the right
						right = list.I1 + list.L;
					}
					
					list = list.next;
					continue;
				}
			} else if (list.I1 + list.L >= left) {
				// We extend mp to the left
				left = list.I1;
				list = list.next;
				continue;
			}
			
			// No intersection, no overlap
			// Notice that, we have to preserve the order of the list
			// Because the unprocessed points-to tuples are headed at the list
			if ( q == null ) {
				p = q = list;
			}
			else {
				q.next = list;
				q = list;
			}
			
			++num;
			list = list.next;
		}

		mp.I1 = left;
		mp.L = right - left;
		if ( q != null ) q.next = null;
		header[1] = p;
		size[1] = num;
	}
	
	private void clean_garbage_I1_is_zero(SegmentNode mp) 
	{
		SegmentNode p, q, list;
		int num;
		long right, left;

		list = header[0];
		p = q = null;
		num = 0;
		left = mp.I2;
		right = mp.I2 + mp.L;

		while (list != null) {
			if (list.I2 >= left) {
				if (list.I2 <= right) {
					if ( list.I2 + list.L > right ) {
						// We extend mp to the right
						right = list.I2 + list.L;
					}

					list = list.next;
					continue;
				}
			} else if (list.I2 + list.L >= left) {
				// We extend mp to the left
				left = list.I2;
				list = list.next;
				continue;
			}

			// No intersection, no overlap
			// Notice that, we have to preserve the order of the list
			// Because the unprocessed points-to tuples are headed at the list
			if ( q == null ) {
				p = q = list;
			}
			else {
				q.next = list;
				q = list;
			}
			
			++num;
			list = list.next;
		}

		mp.I2 = left;
		mp.L = right - left;
		if ( q != null ) q.next = null;
		header[0] = p;
		size[0] = num;
	}
}
