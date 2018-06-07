package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Ondrej Lhotak
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
 * A generic interface to any type of pointer analysis.
 * 
 * @author Ondrej Lhotak
 */

public interface PointsToAnalysis {
  /** Returns the set of objects pointed to by variable l. */
  public PointsToSet reachingObjects(Local l);

  /** Returns the set of objects pointed to by variable l in context c. */
  public PointsToSet reachingObjects(Context c, Local l);

  /** Returns the set of objects pointed to by static field f. */
  public PointsToSet reachingObjects(SootField f);

  /**
   * Returns the set of objects pointed to by instance field f of the objects in the PointsToSet s.
   */
  public PointsToSet reachingObjects(PointsToSet s, SootField f);

  /**
   * Returns the set of objects pointed to by instance field f of the objects pointed to by l.
   */
  public PointsToSet reachingObjects(Local l, SootField f);

  /**
   * Returns the set of objects pointed to by instance field f of the objects pointed to by l in context c.
   */
  public PointsToSet reachingObjects(Context c, Local l, SootField f);

  /**
   * Returns the set of objects pointed to by elements of the arrays in the PointsToSet s.
   */
  public PointsToSet reachingObjectsOfArrayElement(PointsToSet s);

  public static final String THIS_NODE = "THIS_NODE";
  public static final int RETURN_NODE = -2;
  public static final String THROW_NODE = "THROW_NODE";
  public static final String ARRAY_ELEMENTS_NODE = "ARRAY_ELEMENTS_NODE";
  public static final String CAST_NODE = "CAST_NODE";
  public static final String STRING_ARRAY_NODE = "STRING_ARRAY_NODE";
  public static final String STRING_NODE = "STRING_NODE";
  public static final String STRING_NODE_LOCAL = "STRING_NODE_LOCAL";
  public static final String EXCEPTION_NODE = "EXCEPTION_NODE";
  public static final String RETURN_STRING_CONSTANT_NODE = "RETURN_STRING_CONSTANT_NODE";
  public static final String STRING_ARRAY_NODE_LOCAL = "STRING_ARRAY_NODE_LOCAL";
  public static final String MAIN_THREAD_NODE = "MAIN_THREAD_NODE";
  public static final String MAIN_THREAD_NODE_LOCAL = "MAIN_THREAD_NODE_LOCAL";
  public static final String MAIN_THREAD_GROUP_NODE = "MAIN_THREAD_GROUP_NODE";
  public static final String MAIN_THREAD_GROUP_NODE_LOCAL = "MAIN_THREAD_GROUP_NODE_LOCAL";
  public static final String MAIN_CLASS_NAME_STRING = "MAIN_CLASS_NAME_STRING";
  public static final String MAIN_CLASS_NAME_STRING_LOCAL = "MAIN_CLASS_NAME_STRING_LOCAL";
  public static final String DEFAULT_CLASS_LOADER = "DEFAULT_CLASS_LOADER";
  public static final String DEFAULT_CLASS_LOADER_LOCAL = "DEFAULT_CLASS_LOADER_LOCAL";
  public static final String FINALIZE_QUEUE = "FINALIZE_QUEUE";
  public static final String CANONICAL_PATH = "CANONICAL_PATH";
  public static final String CANONICAL_PATH_LOCAL = "CANONICAL_PATH_LOCAL";
  public static final String PRIVILEGED_ACTION_EXCEPTION = "PRIVILEGED_ACTION_EXCEPTION";
  public static final String PRIVILEGED_ACTION_EXCEPTION_LOCAL = "PRIVILEGED_ACTION_EXCEPTION_LOCAL";
  public static final String PHI_NODE = "PHI_NODE";
}
