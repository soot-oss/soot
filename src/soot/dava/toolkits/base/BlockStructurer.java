/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Jerome Miecznikowski
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.dava.toolkits.base;

import soot.*;
import soot.jimple.*;
import soot.dava.*;
import soot.toolkits.graph.*;
import java.util.*;

public class BlockStructurer extends BodyTransformer {
  private static BlockStructurer instance = new BlockStructurer();
  private BlockStructurer() {}
  
  public static BlockStructurer v() { return instance; }
  
  protected void internalTransform(Body bd, String phaseName, Map options) {
    Block b, conditionBlock;
    Unit u, u2;
    Iterator it, succIt, bit, uit;
    BlockTrunk bt, bt2;
    boolean takeTailOnly;
    List l;
    
    DavaBody body = (DavaBody) bd;    
    
    if(Main.isVerbose)
      System.out.println("[" + body.getMethod().getName() + "] Structuring blocks...");
    
    BriefBlockGraph bbg = new BriefBlockGraph( body);

    // create a mirror of the block graph in the trunk graph
    // pass through the list of blocks creating one BlockTrunk for each Block

    HashMap mapping_b2t = new HashMap();

    it = bbg.getBlocks().iterator();
    while (it.hasNext()) {
      b  = (Block) it.next();
      bt = new BlockTrunk(b);
      mapping_b2t.put( b, bt);
    }

    takeTailOnly = false;

    // loop through the list of blocks hooking up the trunks
    it = bbg.getBlocks().iterator();
    while (it.hasNext()) {

      b = (Block) it.next();      
      bt = (BlockTrunk) mapping_b2t.get(b);

      // mirror the succesors
      bit = b.getSuccs().iterator();
      while (bit.hasNext()) 
	  bt.addSuccessor((BlockTrunk) mapping_b2t.get( bit.next()));

      // mirror the predecessors
      bit = b.getPreds().iterator();
      while (bit.hasNext()) 
	  bt.addPredecessor((BlockTrunk) mapping_b2t.get( bit.next()));

      
      // give the BlockTrunks the content chains from the Blocks
      Stmt lastStmt = (Stmt) b.getTail();
      Stmt firstStmt = (Stmt) b.getHead();
      Iterator sit = b.iterator();
      while (sit.hasNext()) {
	Stmt s = (Stmt) sit.next();

	if ((firstStmt == lastStmt) || (s != lastStmt) || (!(s instanceof IfStmt))) {
	    bt.addContents( s);
	    if (s instanceof IfStmt) {
		bt.setCondition( (ConditionExpr) ((IfStmt)s).getCondition());
		bt.targetS = ((IfStmt)s).getTarget();
		bt.Branches = true;
	    }
	}
      }

      // put the new trunk in the DavaBody
      body.addTrunk( bt);      
    }


    // now make a second pass creating new trunks for those blocks that end with IfStmt
    it = bbg.getBlocks().iterator();
    while (it.hasNext()) {
	
      b = (Block) it.next();
      u = b.getTail();

      // now create the extra trunk if the current block branches and link it in
      if ((u != b.getHead()) && (((Stmt)u) instanceof IfStmt)) {

	  bt = (BlockTrunk) mapping_b2t.get(b);
	  ArrayList al = new ArrayList();
	  bt2 = new BlockTrunk();
	  bt2.Branches = true;
	  bt2.setCondition( (ConditionExpr) ((IfStmt)u).getCondition());
	  bt2.targetS = ((IfStmt)u).getTarget();
	  
	  bt2.addPredecessor( bt);
	  bt2.setSuccessorList( bt.getSuccessors());

	  al.add( bt2);
	  bt.setSuccessorList( al);

	  bt2.addContents( u);
	  
	  Iterator tempit = bt2.getSuccessors().iterator();
	  while (tempit.hasNext()) {
	      Trunk tempTrunk = (Trunk) tempit.next();
	      int index;
	      if ((index = tempTrunk.getPredecessors().indexOf( bt)) != -1) {
		  tempTrunk.getPredecessors().remove( index);
		  tempTrunk.addPredecessor( bt2);
	      } 
	      else {
		  System.out.println( "Warning: Trunk graph is not correct");
	      }
	  }
	  body.addTrunk( bt2);
      }
    }
  }
}




