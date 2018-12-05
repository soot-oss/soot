package soot.jimple.spark.geom.geomPA;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2011 Richard Xiao
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

import soot.jimple.spark.geom.dataRep.RectangleNode;
import soot.jimple.spark.geom.dataRep.SegmentNode;

/**
 * An interface to standardize the functionality of a figure manager.
 * 
 * @author xiao
 *
 */
public abstract class IFigureManager {
  // We implement an internal memory manager here
  private static SegmentNode segHeader = null;
  private static SegmentNode rectHeader = null;

  /**
   * Generate a segment node from our own cache.
   * 
   * @return
   */
  protected static SegmentNode getSegmentNode() {
    SegmentNode ret = null;

    if (segHeader != null) {
      ret = segHeader;
      segHeader = ret.next;
      ret.next = null;
      ret.is_new = true;
    } else {
      ret = new SegmentNode();
    }

    return ret;
  }

  /**
   * Generate a rectangle node from our own cache.
   * 
   * @return
   */
  protected static RectangleNode getRectangleNode() {
    RectangleNode ret = null;

    if (rectHeader != null) {
      ret = (RectangleNode) rectHeader;
      rectHeader = ret.next;
      ret.next = null;
      ret.is_new = true;
    } else {
      ret = new RectangleNode();
    }

    return ret;
  }

  /**
   * Return the segment node to cache.
   * 
   * @param p
   * @return
   */
  protected static SegmentNode reclaimSegmentNode(SegmentNode p) {
    SegmentNode q = p.next;
    p.next = segHeader;
    segHeader = p;
    return q;
  }

  /**
   * Return the rectangle node to cache.
   * 
   * @param p
   * @return
   */
  protected static SegmentNode reclaimRectangleNode(SegmentNode p) {
    SegmentNode q = p.next;
    p.next = rectHeader;
    rectHeader = p;
    return q;
  }

  /**
   * We return the cached memory to garbage collector.
   */
  public static void cleanCache() {
    segHeader = null;
    rectHeader = null;
  }

  // Get the information of the figures
  public abstract SegmentNode[] getFigures();

  public abstract int[] getSizes();

  public abstract boolean isThereUnprocessedFigures();

  public abstract void flush();

  // Deal with the figures
  public abstract SegmentNode addNewFigure(int code, RectangleNode pnew);

  public abstract void mergeFigures(int size);

  public abstract void removeUselessSegments();

}
