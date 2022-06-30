package soot.jimple.spark.geom.geomPA;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2011 - 2014 Richard Xiao
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

/**
 * Parameters to control the behaviors of geom points-to solver.
 * 
 * @author xiao
 *
 */
public class Parameters {
  // The parameters that are used to tune the precision and performance tradeoff
  public static int max_cons_budget = 40;
  public static int max_pts_budget = 80;
  public static int cg_refine_times = 1;

  // Parameters for offline processing
  public static int seedPts = Constants.seedPts_allUser;

  // Querying parameters: budget size for collecting contexts intervals
  public static int qryBudgetSize = max_pts_budget / 2;
}
