/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * Java is a trademark of Sun Microsystems, Inc.                     *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
  
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Coffi, a bytecode parser for the Java(TM) language.               *
 * Copyright (C) 1996, 1997 Clark Verbrugge (clump@sable.mcgill.ca). *
 * All rights reserved.                                              *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * Java is a trademark of Sun Microsystems, Inc.                     *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*
 Reference Version
 -----------------
 This is the latest official version on which this file is based.
 The reference version is: $CoffiVersion: 1.1 $
                           $JimpleVersion: 0.5 $

 Change History
 --------------
 A) Notes:

 Please use the following template.  Most recent changes should
 appear at the top of the list.

 - Modified on [date (March 1, 1900)] by [name]. [(*) if appropriate]
   [description of modification].

 Any Modification flagged with "(*)" was done as a project of the
 Sable Research Group, School of Computer Science,
 McGill University, Canada (http://www.sable.mcgill.ca/).

 You should add your copyright, using the following template, at
 the top of this file, along with other copyrights.

 *                                                                   *
 * Modifications by [name] are                                       *
 * Copyright (C) [year(s)] [your name (or company)].  All rights     *
 * reserved.                                                         *
 *                                                                   *

 B) Changes:

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot.coffi;

import java.util.Enumeration;
import java.util.Vector;

/** Represents one basic block in a control flow graph.
 * @see CFG
 * @see ClassFile#parse
 * @author Clark Verbrugge
 */
class BasicBlock {
   /** Number of instructions in this block. */
   public int size;
   /** Head of the list of instructions. */
   public Instruction head;
   /** Tail of the list of instructions. 
    * <p>
    * Normally, the last instruction will have a next pointer with value
    * <i>null</i>.  After a Instruction sequences are reconstructed though, 
    * the instruction lists 
    * are rejoined in order, and so the tail instruction will not
    * have a <i>null</i> next pointer.
    * @see CFG#reconstructInstructions
    */
   public Instruction tail;
   /** Vector of predecessor BasicBlocks.
    * @see java.util.Vector
    */
   public Vector succ;
   /** Vector of successor BasicBlocks.
    * @see java.util.Vector
    */
   public Vector pred;
   
   public boolean inq;
   /** Flag for whether starting an exception or not. */
   public boolean beginException;
   /** Flag for whether starting main code block or not. */
   public boolean beginCode;
   /** Flag for semantic stack analysis fixup pass.
    * @see CFG#jimplify
    */
     
   boolean done;
   
   /** Next BasicBlock in the CFG, in the parse order. */
   public BasicBlock next;
   /** Unique (among basic blocks) id. */
   public long id;                   // unique id
   
   private short wide;                 // convert indices when parsing jimple

   private static long ids;           // for generating unique ids
   
   /** Constructs a BasicBlock consisting of the given list of Instructions. 
    * @param insts list of instructions composing this basic block.
    */
    
   private ca.mcgill.sable.soot.jimple.Stmt stmt;  // statement generated 
   
   ca.mcgill.sable.util.List statements; 
   ca.mcgill.sable.util.Set addressesToFixup = new ca.mcgill.sable.util.VectorSet();
       
   ca.mcgill.sable.soot.jimple.Stmt getHeadJStmt()
   {
      return (ca.mcgill.sable.soot.jimple.Stmt) statements.get(0);
   }
   
   ca.mcgill.sable.soot.jimple.Stmt getTailJStmt()
   {
      return (ca.mcgill.sable.soot.jimple.Stmt) statements.get(statements.size() - 1);
   }
   
   public BasicBlock(Instruction insts) {
      id = ids++;
      head = insts;
      tail = head;
      size = 0;
      if (head!=null) {
         size++;
         while (tail.next!=null) {
            size++;
            tail = tail.next;
         }
      }
      succ = new Vector(2,10);
      pred = new Vector(2,3);
   }

   /** Computes a hash code for this block from the label of the
    * first instruction in its contents.
    * @return the hash code.
    * @see Instruction#label
    */
   public int hashCode() {
      return (new Integer(head.label)).hashCode();
   }

   /** True if this block represents the same piece of code.  Basically
    * compares labels of the head instructions.
    * @param b block to compare against.
    * @return <i>true</i> if they do, <i>false</i> if they don't.
    */
   public boolean equals(BasicBlock b) {
      if (b.head.label == head.label) return true;
      return false;
   }

   /** For printing the string "BB: " + id.
    */
   public String toString() { return "BB: " + id; }

   // Returns the index of b given the current wide
   // wide to 0
   private int wideIndex(short b) {
      int i = ((((int)wide)<<8)&0xff00) | (((int)b)&0xff);
      wide = (byte)0;
      return i;
   }
   
}
