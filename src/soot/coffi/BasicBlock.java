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

import java.util.Enumeration;
import java.util.Vector;
import java.util.*;
import soot.util.*;
import soot.*;
import java.util.*;

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

   /** Constructs a BasicBlock consisting of the given list of Instructions.
    * @param insts list of instructions composing this basic block.
    */

   private soot.jimple.Stmt stmt;  // statement generated

   List statements;
   Set addressesToFixup = new ArraySet();

   soot.jimple.Stmt getHeadJStmt()
   {
      return (soot.jimple.Stmt) statements.get(0);
   }

   soot.jimple.Stmt getTailJStmt()
   {
      return (soot.jimple.Stmt) statements.get(statements.size() - 1);
   }

   public BasicBlock(Instruction insts) {
      id = G.v().coffi_BasicBlock_ids++;
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

    public BasicBlock(Instruction headinsn, Instruction tailinsn)
    {
	id = G.v().coffi_BasicBlock_ids++;
	head = headinsn;
	tail = tailinsn;
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
      return (this == b);
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
