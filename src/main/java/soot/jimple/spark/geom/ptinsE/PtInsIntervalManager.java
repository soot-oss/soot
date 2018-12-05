package soot.jimple.spark.geom.ptinsE;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2012 Richard Xiao
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
 * The figure manager for the PtIns descriptors. The implementation is almost same to the HeapIns manager, please refer to
 * HeapInsIntervalManager for more detailed comments.
 *
 * @author xiao
 *
 */
public class PtInsIntervalManager extends IFigureManager {
  public static final int Divisions = 3;
  public static final int ALL_TO_ALL = -1; // A special case
  public static final int ALL_TO_MANY = 0;
  public static final int MANY_TO_ALL = 1;
  public static final int ONE_TO_ONE = 2;

  int size[] = { 0, 0, 0 };
  SegmentNode header[] = { null, null, null };
  private boolean hasNewObject = false;

  public SegmentNode[] getFigures() {
    return header;
  }

  public int[] getSizes() {
    return size;
  }

  public boolean isThereUnprocessedFigures() {
    return hasNewObject;
  }

  public void flush() {
    hasNewObject = false;

    for (int i = 0; i < Divisions; ++i) {
      SegmentNode p = header[i];
      while (p != null && p.is_new == true) {
        p.is_new = false;
        p = p.next;
      }
    }
  }

  public SegmentNode addNewFigure(int code, RectangleNode pnew) {
    SegmentNode p;

    if (code == ALL_TO_ALL) {
      // Directly clean all the existing intervals
      if (header[0] != null && header[0].I2 == 0) {
        return null;
      }

      p = new SegmentNode();

      p.I1 = p.I2 = 0;
      p.L = Constants.MAX_CONTEXTS;
      for (int i = 0; i < Divisions; ++i) {
        size[i] = 0;
        header[i] = null;
      }
    } else {
      // Duplicate testing

      if (code == ALL_TO_MANY || code == ONE_TO_ONE) {
        p = header[ALL_TO_MANY];
        while (p != null) {
          if ((p.I2 <= pnew.I2) && (p.I2 + p.L >= pnew.I2 + pnew.L)) {
            return null;
          }
          p = p.next;
        }
      }

      if (code == MANY_TO_ALL || code == ONE_TO_ONE) {
        p = header[MANY_TO_ALL];
        while (p != null) {
          if ((p.I1 <= pnew.I1) && (p.I1 + p.L >= pnew.I1 + pnew.L)) {
            return null;
          }
          p = p.next;
        }
      }

      // Be careful of this!
      if (code == ONE_TO_ONE) {
        p = header[ONE_TO_ONE];
        while (p != null) {
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

    hasNewObject = true;
    size[code]++;
    p.next = header[code];
    header[code] = p;
    return p;
  }

  public void mergeFigures(int upperSize) {
    if (size[ONE_TO_ONE] > upperSize && header[ONE_TO_ONE].is_new == true) {
      // After the merging, we must propagate this interval, thus it has to be a new interval

      SegmentNode p = generate_all_to_many(header[ONE_TO_ONE]);
      clean_garbage_all_to_many(p);
      p.next = header[ALL_TO_MANY];
      header[ALL_TO_MANY] = p;
      header[ONE_TO_ONE] = null;
      size[ALL_TO_MANY]++;
      size[ONE_TO_ONE] = 0;
    }

    if (size[MANY_TO_ALL] > upperSize && header[MANY_TO_ALL].is_new == true) {

      header[MANY_TO_ALL] = generate_many_to_all(header[MANY_TO_ALL]);
      size[MANY_TO_ALL] = 1;
    }

    if (size[ALL_TO_MANY] > upperSize && header[ALL_TO_MANY].is_new == true) {

      header[0] = generate_all_to_many(header[ALL_TO_MANY]);
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
      for (i = 0; i < 2; ++i) {
        temp = header[i];
        while (temp != null) {
          if (temp.I1 == 0 || ((temp.I1 <= p.I1) && (temp.I1 + temp.L >= p.I1 + p.L))) {
            if (temp.I2 == 0 || ((temp.I2 <= p.I2) && (temp.I2 + temp.L >= p.I2 + p.L))) {
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
   * Merge all the context sensitive intervals. The result is in the form (p, q, 0, I, L).
   */
  private SegmentNode generate_all_to_many(SegmentNode mp) {
    long left, right, t;
    SegmentNode p;

    left = mp.I2;
    right = left + mp.L;
    p = mp.next;

    while (p != null) {
      if (p.I2 < left) {
        left = p.I2;
      }
      t = p.I2 + p.L;
      if (t > right) {
        right = t;
      }
      p = p.next;
    }

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
    long left, right, t;
    SegmentNode p;

    left = mp.I1;
    right = left + mp.L;
    p = mp.next;

    while (p != null) {
      if (p.I1 < left) {
        left = p.I1;
      }
      t = p.I1 + p.L;
      if (t > right) {
        right = t;
      }
      p = p.next;
    }

    // Note, left could be 0. In that case, the propagation along this edge becomes totally insensitive
    mp.I1 = left;
    mp.I2 = 0;
    mp.L = right - left;
    mp.next = null;

    return mp;
  }

  // Clean garbages in list that the information is already covered by mp
  // BTW, we do some simple concatenation
  private void clean_garbage_many_to_all(SegmentNode mp) {
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
          if (list.I1 + list.L > right) {
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
      if (q == null) {
        p = q = list;
      } else {
        q.next = list;
        q = list;
      }

      ++num;
      list = list.next;
    }

    mp.I1 = left;
    mp.L = right - left;
    if (q != null) {
      q.next = null;
    }
    header[1] = p;
    size[1] = num;
  }

  private void clean_garbage_all_to_many(SegmentNode mp) {
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
          if (list.I2 + list.L > right) {
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
      if (q == null) {
        p = q = list;
      } else {
        q.next = list;
        q = list;
      }

      ++num;
      list = list.next;
    }

    mp.I2 = left;
    mp.L = right - left;
    if (q != null) {
      q.next = null;
    }
    header[0] = p;
    size[0] = num;
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
