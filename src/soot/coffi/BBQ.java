/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997 Clark Verbrugge
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */







package soot.coffi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/** A queue of BasicBlocks.
 * @author Clark Verbrugge
 * @see BasicBlock
 */
final class BBQ {

   private ArrayList q = new ArrayList();

   /** Adds a block to the end of the queue, but only if its <i>inq</i> flag is false.
    * @param b the Basic Block in question.
    * @see BasicBlock#inq
    */
   public void push(BasicBlock b) {
      if (b.inq!=true) {  // ensure only in queue once...
         b.inq = true;
         q.add(b);
      }
   }

   /** Removes the first block in the queue (and resets its <i>inq</i> flag).
    * @return BasicBlock which was first.
    * @exception java.util.NoSuchElementException if the queue is empty.
    * @see BasicBlock#inq
    */
   public BasicBlock pull() throws NoSuchElementException {
      if(q.size()==0)
         throw new
            NoSuchElementException("Pull from empty BBQ");
      BasicBlock b = (BasicBlock)(q.get(0));
      q.remove(0);
      b.inq = false;
      return b;
   }

   /** Answers whether a block is in the queue or not.
    * @param BasicBlock in question.
    * @return <i>true</i> if it is, <i>false</i> if it ain't.
    * @see BasicBlock#inq
    */
   public boolean contains(BasicBlock b) { return b.inq; }
   /** Answers the size of the queue.
    * @return size of the queue.
    */
   public int size() { return q.size(); }
   /** Answers whether the queue is empty
    * @return <i>true</i> if it is, <i>false</i> if it ain't.
    */
   public boolean isEmpty() { return q.isEmpty(); }

   /** Empties the queue of all blocks (and resets their <i>inq</i> flags). */
   public void clear() {
      BasicBlock b;
      for (Iterator e = q.iterator();e.hasNext();) {
         b = (BasicBlock)(e.next());
         b.inq = false;
      }
      q.clear();
   }
}

