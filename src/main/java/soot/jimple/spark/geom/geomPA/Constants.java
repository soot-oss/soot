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

import soot.RefType;

/**
 * Named constants used in the geomPA.
 * 
 * @author xiao
 *
 */
public class Constants 
{
	// Available encodings
	public static final String geomE = "Geom";
	public static final String heapinsE = "HeapIns";
	public static final String ptinsE = "PtIns";
	
	// Evaluation level
	public static final int eval_nothing = 0;
	public static final int eval_basicInfo = 1;
	public static final int eval_simpleClients = 2;
	
	// The constants for the constraints type identification
	public static final int NEW_CONS = 0;
	public static final int ASSIGN_CONS = 1;
	public static final int LOAD_CONS = 2;
	public static final int STORE_CONS = 3;
	public static final int FIELD_ADDRESS = 4;
	
	// The constants for the call graph
	public static final int SUPER_MAIN = 0;
	public static final int UNKNOWN_FUNCTION = -1;
	
	// The number of contexts that is natively supported by Java (2^63)
	// Using big integer would not bring too many benefits.
	public static final long MAX_CONTEXTS = Long.MAX_VALUE - 1;
	
	// Some commonly referred to information
	public static final RefType exeception_type = RefType.v( "java.lang.Throwable" );
	
	// The seed pointers for running constraints distillation
	public static final int seedPts_allUser = 0x0000000f;
	public static final int seedPts_all = 0x7fffffff;
}
