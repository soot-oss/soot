package soot.jimple.spark.geom.heapinsE;

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
import soot.jimple.spark.geom.geomPA.Constants;
import soot.jimple.spark.geom.geomPA.IFigureManager;

/**
 * An abstraction for the management of all the heap insensitive encoding figures. We employ the naive management strategy,
 * which is a linked list based manager.
 *
 * For HeapIns analysis, we have four types of figures: Type | Index all-to-many | 0 many-to-all | 1 one-to-one | 2
 * all-to-all all-to-all is special because whenever it was presented, all others can be deleted (its semantics is context
 * insensitive). The corresponding index means header[0] stores all the all-to-one figures, and so on.
 *
 * @author xiao
 *
 */
public class HeapInsIntervalManager extends IFigureManager {
  public static int Divisions = 3;

  // Type IDs for the figures
  public static final int ALL_TO_ALL = -1; // A special case
  public static final int ALL_TO_MANY = 0;
  public static final int MANY_TO_ALL = 1;
  public static final int ONE_TO_ONE = 2;

  // Recording the size of each type of figure
  private int size[] = { 0, 0, 0 };
  // Recording the geometric figures, categorized by the type IDs.
  private SegmentNode header[] = { null, null, null };
  private boolean hasNewFigure = false;

  public SegmentNode[] getFigures() {
    return header;
  }

  public int[] getSizes() {
    return size;
  }

  public boolean isThereUnprocessedFigures() {
    return hasNewFigure;
  }

  public void flush() {
    hasNewFigure = false;

    for (int i = 0; i < Divisions; ++i) {
      SegmentNode p = header[i];
      while (p != null && p.is_new == true) {
        p.is_new = false;
        p = p.next;
      }
    }
  }

  /**
   * Delete all the shapes recorded.
   */
  public void clear() {
    for (int i = 0; i < Divisions; ++i) {
      size[i] = 0;
      header[i] = null;
    }

    hasNewFigure = false;
  }

  /*
   * pnew.L < 0 is a special case we used to indicate a square: L = L_prime This case is specially handled because it is very
   * common in the program. And, treating it as a MANY-TO-ALL is loss of precision.
   */
  public SegmentNode addNewFigure(int code, RectangleNode pnew) {
    SegmentNode p;

    if (code == ALL_TO_ALL) {
      // The input figure is a all-to-all figure
      // Directly clean all the existing intervals unless the all-to-all figure is existing.
      if (header[ALL_TO_MANY] != null && header[ALL_TO_MANY].I2 == 0) {
        return null;
      }

      p = new SegmentNode();

      code = ALL_TO_MANY;
      p.I1 = p.I2 = 0;
      p.L = Constants.MAX_CONTEXTS;
      for (int i = 0; i < Divisions; ++i) {
        size[i] = 0;
        header[i] = null;
      }
    } else {
      // Before inserting into the figure list, we do duplicate testing

      // This is a all-to-many or one-to-one figure
      if (code == ALL_TO_MANY || code == ONE_TO_ONE) {
        p = header[ALL_TO_MANY];
        while (p != null) {
          if ((p.I2 <= pnew.I2) && (p.I2 + p.L >= pnew.I2 + pnew.L)) {
            return null;
          }
          p = p.next;
        }
      }

      // This is a many-to-all or one-to-one figure
      if (code == MANY_TO_ALL || code == ONE_TO_ONE) {
        p = header[MANY_TO_ALL];
        while (p != null) {
          if ((p.I1 <= pnew.I1) && (p.I1 + p.L >= pnew.I1 + pnew.L)) {
            return null;
          }
          p = p.next;
        }
      }

      // This is a one-to-one figure
      if (code == ONE_TO_ONE) {
        p = header[ONE_TO_ONE];
        while (p != null) {
          // We don't process the case: the input figure is a square but the tested figure is a segment
          if (p.I1 - p.I2 == pnew.I1 - pnew.I2) {
            // On the same line
            if (p.I1 <= pnew.I1 && p.I1 + p.L >= pnew.I1 + pnew.L) {
              return null;
            }
          }

          p = p.next;
        }
      }

      // Insert the new interval immediately, and we delay the merging until necessary
      p = new SegmentNode(pnew);

      if (code == ALL_TO_MANY) {
        clean_garbage_all_to_many(p);
      } else if (code == MANY_TO_ALL) {
        clean_garbage_many_to_all(p);
      } else {
        clean_garbage_one_to_one(p);
      }
    }

    hasNewFigure = true;
    size[code]++;
    p.next = header[code];
    header[code] = p;
    return p;
  }

  // This function tries to do the geometric merging
  public void mergeFigures(int upperSize) {
    if (!hasNewFigure) {
      return;
    }

    /*
     * We start the merging from ONE_TO_ONE, because the generated figure may be merged with those figures in MANY_TO_ALL
     */
    if (size[ONE_TO_ONE] > upperSize && header[ONE_TO_ONE].is_new == true) {

      // We prefer to generate a heap insensitive figure
      SegmentNode p = generate_many_to_all(header[ONE_TO_ONE]);
      clean_garbage_many_to_all(p);
      p.next = header[MANY_TO_ALL];
      header[MANY_TO_ALL] = p;
      header[ONE_TO_ONE] = null;
      size[MANY_TO_ALL]++;
      size[ONE_TO_ONE] = 0;
    }

    if (size[MANY_TO_ALL] > upperSize && header[MANY_TO_ALL].is_new == true) {

      header[MANY_TO_ALL] = generate_many_to_all(header[MANY_TO_ALL]);
      size[MANY_TO_ALL] = 1;
    }

    if (size[ALL_TO_MANY] > upperSize && header[ALL_TO_MANY].is_new == true) {

      header[ALL_TO_MANY] = generate_all_to_many(header[ALL_TO_MANY]);
      size[ALL_TO_MANY] = 1;
    }
  }

  public void removeUselessSegments() {
    int i;
    SegmentNode p, q, temp;

    p = header[ONE_TO_ONE];
    size[ONE_TO_ONE] = 0;
    q = null;

    while (p != null) {
      boolean contained = false;
      long L = p.L;

      for (i = 0; i < 2; ++i) {
        temp = header[i];
        while (temp != null) {
          if (temp.I1 == 0 || ((temp.I1 <= p.I1) && (temp.I1 + temp.L >= p.I1 + L))) {
            if (temp.I2 == 0 || ((temp.I2 <= p.I2) && (temp.I2 + temp.L >= p.I2 + L))) {
              contained = true;
              break;
            }
          }

          temp = temp.next;
        }
      }

      temp = p.next;
      if (contained == false) {
        p.next = q;
        q = p;
        ++size[ONE_TO_ONE];
      }
      p = temp;
    }

    header[ONE_TO_ONE] = q;
  }

  /**
   * Merge all the ONE_TO_ONE figures pointed to by mp. The result is in the form (p, q, 0, I, L).
   */
  private SegmentNode generate_all_to_many(SegmentNode mp) {
    long left, right;
    SegmentNode p;

    left = mp.I2;
    right = left + mp.L;
    p = mp.next;

    while (p != null) {
      if (p.I2 < left) {
        left = p.I2;
      }
      long t = p.I2 + p.L;
      if (t > right) {
        right = t;
      }
      p = p.next;
    }

    // We reuse the first element in the list mp
    mp.I1 = 0;
    mp.I2 = left;
    mp.L = right - left;
    mp.next = null;

    return mp;
  }

  /**
   * The result is in the form: (p, q, I, 0, L)
   */
  private SegmentNode generate_many_to_all(SegmentNode mp) {
    long left, right;
    SegmentNode p;

    left = mp.I1;
    right = left + mp.L;
    p = mp.next;

    while (p != null) {
      if (p.I1 < left) {
        left = p.I1;
      }
      long t = p.I1 + p.L;
      if (t > right) {
        right = t;
      }
      p = p.next;
    }

    mp.I1 = left;
    mp.I2 = 0;
    mp.L = right - left;
    mp.next = null;

    return mp;
  }

  // Clean garbages in the MANY_TO_ALL list that the information is already covered by mp
  // BTW, we also do simple adjacent figures concatenation
  private void clean_garbage_many_to_all(SegmentNode predator) {
    SegmentNode p, q, list;
    int num;
    long right, left;

    list = header[MANY_TO_ALL];
    p = q = null;
    num = 0;
    left = predator.I1;
    right = left + predator.L;

    while (list != null) {

      // We first process the overlapped cases
      if (list.I1 >= left) {
        if (list.I1 <= right) {
          if (list.I1 + list.L > right) {
            // We extend predator to the right
            right = list.I1 + list.L;
          }
          // else, this figure is completely contained in predator, we swallow it

          list = list.next;
          continue;
        }
        // else, this figure has no overlap with the predator
      } else if (list.I1 + list.L >= left) {
        // We extend predator to the left
        left = list.I1;
        list = list.next;
        continue;
      }

      // No intersection, no overlap
      // Notice that, we have to preserve the order of the list
      // Because the newly inserted figures are headed at the list
      if (q == null) {
        p = q = list;
      } else {
        q.next = list;
        q = list;
      }

      ++num;
      list = list.next;
    }

    predator.I1 = left;
    predator.L = right - left;
    if (q != null) {
      q.next = null;
    }
    header[MANY_TO_ALL] = p;
    size[MANY_TO_ALL] = num;
  }

  // Clean the ALL_TO_MANY list
  private void clean_garbage_all_to_many(SegmentNode predator) {
    SegmentNode p, q, list;
    int num;
    long right, left;

    list = header[ALL_TO_MANY];
    p = q = null;
    num = 0;
    left = predator.I2;
    right = predator.I2 + predator.L;

    while (list != null) {
      if (list.I2 >= left) {
        if (list.I2 <= right) {
          if (list.I2 + list.L > right) {
            // We extend predator to the right
            right = list.I2 + list.L;
          }

          list = list.next;
          continue;
        }
      } else if (list.I2 + list.L >= left) {
        // We extend predator to the left
        left = list.I2;
        list = list.next;
        continue;
      }

      // No intersection, no overlap
      // Notice that, we have to preserve the order of the list
      // Because the unprocessed points-to tuples are headed at the list
      if (q == null) {
        p = q = list;
      } else {
        q.next = list;
        q = list;
      }

      ++num;
      list = list.next;
    }

    predator.I2 = left;
    predator.L = right - left;
    if (q != null) {
      q.next = null;
    }
    header[ALL_TO_MANY] = p;
    size[ALL_TO_MANY] = num;
  }

  /*
   * Eliminate the redundant ONE_TO_ONE figures
   */
  private void clean_garbage_one_to_one(SegmentNode predator) {
    SegmentNode p, q, list;
    int num;

    list = header[ONE_TO_ONE];
    p = q = null;
    num = 0;

    while (list != null) {
      long L = list.L;
      if ((predator.I2 - predator.I1 == list.I2 - list.I1) && predator.I1 <= list.I1
          && (predator.I1 + predator.L >= list.I2 + L)) {
        // The checked figure is completely contained in the predator
        // So we ignore it
        ;
      } else {
        if (q == null) {
          p = q = list;
        } else {
          q.next = list;
          q = list;
        }

        ++num;
      }

      list = list.next;
    }

    if (q != null) {
      q.next = null;
    }
    header[ONE_TO_ONE] = p;
    size[ONE_TO_ONE] = num;
  }
}
