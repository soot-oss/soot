/* Soot - a J*va Optimization Framework
 * Copyright (C) 2011-2014 Richard Xiao
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

/**
 * Parameters to control the behaviors of geom points-to solver.
 * 
 * @author xiao
 *
 */
public class Parameters 
{
	// The parameters that are used to tune the precision and performance tradeoff
	public static int max_cons_budget = 40;
	public static int max_pts_budget = 80;
	public static int cg_refine_times = 1;
	
	// Parameters for offline processing
	public static int seedPts = Constants.seedPts_allUser;
	
	// Querying parameters: budget size for collecting contexts intervals
	public static int qryBudgetSize = max_pts_budget/2;
}
