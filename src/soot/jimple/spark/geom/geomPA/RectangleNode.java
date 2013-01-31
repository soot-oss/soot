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

import soot.jimple.spark.geom.geomPA.SegmentNode;

/**
 * The rectangle figure for encoding the many-to-many relation.
 * 
 * @author xiao
 *
 */
public class RectangleNode extends SegmentNode {

	// I1 : the starting x coordinate
	// I2 : the starting y coordinate
	// L  : the length of the x-axis side
	// L_prime : the length of the y-axis side
	public long L_prime;
	
	public RectangleNode() {}

	public RectangleNode( RectangleNode other )
	{
		copyRectangle(other);
	}
	
	public void copyRectangle( RectangleNode other )
	{
		I1 = other.I1;
		I2 = other.I2;
		L = other.L;
		L_prime = other.L_prime;
	}
	
	public RectangleNode( long I1, long I2, long L, long LL )
	{
		super( I1, I2, L );
		L_prime = LL;
	}
	
	public boolean equals( RectangleNode other )
	{
		if ( I1 == other.I1 &&
				I2 == other.I2 &&
				L == other.L &&
				L_prime == other.L_prime )
			return true;
		
		return false;
	}
}
