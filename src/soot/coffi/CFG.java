/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997 Clark Verbrugge
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







package soot.coffi;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;
import java.util.NoSuchElementException;

import soot.*;
import soot.jimple.*;
import soot.baf.*;
import soot.util.*;
import java.util.*;

/** A Control Flow Graph.
 * @author Clark Verbrugge
 */
public class CFG {

/*
    protected void finalize() throws Throwable 
    {
        if(soot.Main.isVerbose)
            System.out.println("***CFG has been garbage collected for method: " + jmethod.getSignature() + "***");
    }
  */
    
   /** Method for which this is a control flow graph.
    * @see method_info
    */
    method_info method;
   /** Ordered list of BasicBlocks comprising the code of this CFG.
    */
    BasicBlock cfg;
   /** For associating Instruction leaders with basic blocks. */
   private java.util.Hashtable h;
   private int bbcount;        // statistics, number of BBs processed

   Chain units;
   JimpleBody listBody;

   Map instructionToFirstStmt;
   Map instructionToLastStmt;
   SootMethod jmethod;
   Scene cm;

   Map instructionToNext;
   Instruction firstInstruction;

   private short wide;                 // convert indices when parsing jimple

   /** Constructs a new control flow graph for the given method.
    * @param m the method in question.
    * @see method_info
    */
    public CFG(method_info m) {
      Instruction i,head;
      BasicBlock bb,blast;
      method = m;

      // Copy all the instructions to a list
      {
        Instruction ins = m.instructions;

        m.instructionList = new ArrayList();
 
        while(ins != null && ins.next != null)
        {
            m.instructionList.add(ins);

             if (ins instanceof Instruction_Jsr)
               JsrToNext.put (ins, ins.next);

           ins = ins.next;
        }
      }

      h = new java.util.Hashtable(100,25);
      if (m.instructions!=null) {
         i = buildBasicBlock(m.instructions);

         cfg = new BasicBlock(m.instructions);
         blast = cfg;
         h.put(m.instructions,cfg);
         while (i != null) {
            head = buildBasicBlock(i);

            bb = new BasicBlock(i);
            blast.next = bb;
            blast = bb;
            h.put(i,bb);
    
            i = head;
         }

         buildCFG();

         endofBBList = getEndOfBBList();

         // Vijay's JSR eliminator

         {

            buildJsrRetPairs();
        
            fixupJsrRets();
    
            JsrEliminate();
  
            fixupTargets();
  
            adjustExceptionTable();    

            prepareForGC();

         }
  

             
         cfg.beginCode = true;
      }
      m.cfg = this;

      if(cfg != null)
        firstInstruction = cfg.head;
      else
        firstInstruction = null;

        // Build the instructionToNext table
        {
            instructionToNext = new HashMap();

            BasicBlock b = cfg;
            Instruction last = null;

            while (b != null)
            {
                Instruction ins = b.head;

                while(ins != null)
                {
                    
                    if(ins.next != null)
                        instructionToNext.put(ins, ins.next);
                    else if(b.next != null)
                       {

                        instructionToNext.put(ins, b.next.head);

                       }

                    ins = ins.next;
                }

                b = b.next;
            }
        }

   }



   HashMap JsrToNext = new HashMap();

   HashMap RetToJsr = new HashMap();

   HashMap RetToJsrBB = new HashMap();

   HashMap RetToOrigJsr = new HashMap();

   HashMap RetToOrigJsrBB = new HashMap();

   HashMap RetToOrigRetBB = new HashMap(); 

   HashMap RetToRetBB = new HashMap();

   HashMap RetToJsrSucc = new HashMap();

   HashMap RetToOrigJsrSucc = new HashMap();

   BasicBlock endofBBList;

   BasicBlock highestBlock;





  private void prepareForGC () {

   RetToJsr = null;
   RetToJsrBB = null;
   RetToJsrSucc = null;
   RetToRetBB = null;
   RetToOrigJsrSucc = null;
   RetToOrigJsr = null;
   RetToOrigJsrBB = null;
   RetToOrigRetBB = null;
   JsrToNext = null;
   replacedinstructionHT = null;
   clonedHT = null;
   endofBBList = null;
   highestBlock = null;

   // reconstructInstructions();

  }













  private BasicBlock getEndOfBBList() {
   
    BasicBlock b = cfg;

    BasicBlock prev = cfg;

    while ( b != null )
    {

     prev = b;

     b = b.next;

    }

    return prev;

  }








 private void adjustExceptionTable() {

   Code_attribute codeAttribute = method.locate_code_attribute();

   for(int i = 0; i < codeAttribute.exception_table_length; i++)
   {
    Instruction startIns = codeAttribute.exception_table[i].start_inst;

    if ( ( ( Instruction ) replacedinstructionHT.get ( startIns ) ) != null ) 
    codeAttribute.exception_table[i].start_inst = ( Instruction ) replacedinstructionHT.get ( startIns );

    Instruction endIns = codeAttribute.exception_table[i].end_inst;

    if ( ( ( Instruction ) replacedinstructionHT.get ( endIns ) ) != null ) 
    codeAttribute.exception_table[i].end_inst = ( Instruction ) replacedinstructionHT.get ( endIns );

    Instruction targetIns = codeAttribute.exception_table[i].handler_inst;

    if ( ( ( Instruction ) replacedinstructionHT.get ( targetIns ) ) != null ) 
 codeAttribute.exception_table[i].handler_inst = ( Instruction ) replacedinstructionHT.get ( targetIns );

   }

 }







 private void fixupTargets() {

  BasicBlock b = cfg;

  Instruction i = null;

  while ( b != null )
  {

   i = b.head;

   while ( i != null ) 
   {

    if ( i.branches )
    {

     Instruction tgt = null;

     if ( i instanceof Instruction_intbranch )
     tgt = (( Instruction_intbranch ) i ).target; 
     else if ( i instanceof Instruction_longbranch )
     tgt = (( Instruction_longbranch ) i ).target; 

     if ( tgt != null )
     {

      if ( ( ( Instruction ) replacedinstructionHT.get ( tgt ) ) != null ) 
      {

       if ( i instanceof Instruction_intbranch )
       {
         ( ( Instruction_intbranch ) i ).target = ( Instruction ) replacedinstructionHT.get ( tgt );
         // ( ( Instruction_intbranch ) i ) = ( Instruction ) replacedinstructionHT.get ( tgt );

       }
       else if ( i instanceof Instruction_longbranch )
       ( ( Instruction_longbranch ) i ).target = ( Instruction ) replacedinstructionHT.get ( tgt );

      }

     }

     if ( i instanceof Instruction_Lookupswitch )
     {
      
      Instruction_Lookupswitch ilookup = ( Instruction_Lookupswitch ) i;

      if ( ( ( Instruction ) replacedinstructionHT.get ( ilookup.default_inst ) ) != null )
      ilookup.default_inst = ( Instruction ) replacedinstructionHT.get ( ilookup.default_inst );

      for(int cnt=0;cnt<ilookup.npairs;cnt++)
      {

       if ( ( ( Instruction ) replacedinstructionHT.get ( ilookup.match_insts[cnt]) ) != null )
       ilookup.match_insts[cnt] = ( Instruction ) replacedinstructionHT.get ( ilookup.match_insts[cnt] );

      }

     }

     if ( i instanceof Instruction_Tableswitch )
     {
      
      Instruction_Tableswitch tlookup = ( Instruction_Tableswitch ) i;

      if ( ( ( Instruction ) replacedinstructionHT.get ( tlookup.default_inst ) ) != null )
      tlookup.default_inst = ( Instruction ) replacedinstructionHT.get ( tlookup.default_inst );

      for(int cnt=0;cnt<(tlookup.high - tlookup.low + 1);cnt++)
      {

       if ( ( ( Instruction ) replacedinstructionHT.get ( tlookup.jump_insts[cnt]) ) != null )
       tlookup.jump_insts[cnt] = ( Instruction ) replacedinstructionHT.get ( tlookup.jump_insts[cnt] );

      }

     }

    }

    i = i.next;

   }

   b = b.next;

  }

 }







 private void buildJsrRetPairs() {
     
  BasicBlock b = cfg;

  Instruction i = null;

  while ( b != null )
  {

   i = b.tail;

   if ( i instanceof Instruction_Jsr)
   {                 

    boolean retNotFound = true;

    Set successors = new ArraySet();

    java.util.Vector succ = b.succ;

    for(int k = 0; k < succ.size(); k++)
    successors.add((BasicBlock) succ.elementAt(k));

    Iterator succIt = successors.iterator();

    while ( retNotFound && succIt.hasNext() )
    {

     BasicBlock succBB = (BasicBlock) succIt.next();

     Instruction BBtail = succBB.tail;

     if ( BBtail instanceof Instruction_Ret )
     {

      retNotFound = false;

      if ( ( ( Instruction ) RetToJsr.get ( BBtail ) ) == null )
      {

  //      System.out.println ( "RET "+BBtail+" JSR "+i );

        RetToJsr.put ( BBtail, i );
        RetToJsrBB.put ( BBtail, b );
        RetToJsrSucc.put ( BBtail, b.succ.elementAt(0) );
        RetToRetBB.put ( BBtail, succBB );
        RetToOrigJsrSucc.put ( BBtail, b.succ.elementAt(0) );
        RetToOrigJsr.put ( BBtail, i );
        RetToOrigJsrBB.put ( BBtail, b );
        RetToOrigRetBB.put ( BBtail, succBB );
   
       }
       else       
       {

         try {

//          System.out.println ( "Ret with 2 possible Jsr's" );

          setHighestBlock ( ( BasicBlock ) b.succ.elementAt(0) );

          endofBBList = getEndOfBBList();

          highestBlock.next = endofBBList.next;

          endofBBList.next = highestBlock;    

          BasicBlock clonedjsrtargetBB = cloneJsrTargetBB( succBB, ( BasicBlock ) b.succ.elementAt(0) );

          arrangeclonedBBinorder();

          Code_attribute codeAttribute = method.locate_code_attribute();

          int nextind = codeAttribute.exception_table_length;

          for(int j = 0; j < codeAttribute.exception_table_length; j++)
          {

           Instruction startIns = codeAttribute.exception_table[j].start_inst;

           Instruction endIns = codeAttribute.exception_table[j].end_inst;

           Instruction targetIns = codeAttribute.exception_table[j].handler_inst;

           if ( ( ( ( Instruction ) clonedstmtsHT.get ( startIns ) ) != null ) 
              ||( ( ( Instruction ) clonedstmtsHT.get ( endIns ) ) != null )  
              ||( ( ( Instruction ) clonedstmtsHT.get ( targetIns ) ) != null ) ) 

           {

            exception_table_entry[] newexception_table = new exception_table_entry[nextind+1];

            for(int k = 0; k < nextind; k++)
            newexception_table[k] = codeAttribute.exception_table[k]; 

            newexception_table[nextind] = new exception_table_entry();

            codeAttribute.exception_table = newexception_table;

            codeAttribute.exception_table_length++;

            codeAttribute.exception_table[nextind].catch_type = codeAttribute.exception_table[j].catch_type;

            if ( ( ( Instruction ) clonedstmtsHT.get ( startIns ) ) != null ) 
            codeAttribute.exception_table[nextind].start_inst = ( Instruction ) clonedstmtsHT.get ( startIns );
            else
            codeAttribute.exception_table[nextind].start_inst = startIns;

            if ( ( ( Instruction ) clonedstmtsHT.get ( endIns ) ) != null ) 
            codeAttribute.exception_table[nextind].end_inst = ( Instruction ) clonedstmtsHT.get ( endIns );
            else
            codeAttribute.exception_table[nextind].end_inst = endIns;

            if ( ( ( Instruction ) clonedstmtsHT.get ( targetIns ) ) != null ) 
            {
             codeAttribute.exception_table[nextind].handler_inst = ( Instruction ) clonedstmtsHT.get ( targetIns );

             codeAttribute.exception_table[nextind].b = (BasicBlock) h.get ( ( Instruction ) clonedstmtsHT.get ( targetIns ) );
            }
            else
            {
             codeAttribute.exception_table[nextind].handler_inst = targetIns;

             codeAttribute.exception_table[nextind].b = (BasicBlock) h.get ( targetIns );
            }

            nextind++;

           }

          } // FOR


          Iterator clonedstmtit = clonedstmtsHT.entrySet().iterator();

          while ( clonedstmtit.hasNext() )
          {

           Instruction instrn = ( Instruction ) ( ( Map.Entry ) clonedstmtit.next() ).getKey();

           Instruction tgt = null;

           if ( instrn instanceof Instruction_intbranch )
           tgt = (( Instruction_intbranch ) instrn ).target; 
           else if ( i instanceof Instruction_longbranch )
           tgt = (( Instruction_longbranch ) instrn ).target; 

           if ( tgt != null )
           {

            Instruction clonedinstrn = ( Instruction ) clonedstmtsHT.get ( instrn );
      
            if ( clonedstmtsHT.get ( tgt ) != null )
            {

             Instruction clonedtgt = ( Instruction ) clonedstmtsHT.get ( tgt ); 

             if ( instrn instanceof Instruction_intbranch )
             (( Instruction_intbranch ) clonedinstrn ).target = clonedtgt; 
             else if ( i instanceof Instruction_longbranch )
             (( Instruction_longbranch ) clonedinstrn ).target = clonedtgt; 

            }

           }

           if ( instrn instanceof Instruction_Lookupswitch )
           {
      
            Instruction_Lookupswitch ilookup = ( Instruction_Lookupswitch ) instrn;

            if ( ( ( Instruction ) clonedstmtsHT.get ( ilookup.default_inst ) ) != null )
            ilookup.default_inst = ( Instruction ) clonedstmtsHT.get ( ilookup.default_inst );

            for(int cnt=0;cnt<ilookup.npairs;cnt++)
            {

             if ( ( ( Instruction ) clonedstmtsHT.get ( ilookup.match_insts[cnt]) ) != null )
             ilookup.match_insts[cnt] = ( Instruction ) clonedstmtsHT.get ( ilookup.match_insts[cnt] );

            }

           }

           if ( instrn instanceof Instruction_Tableswitch )
           {
      
            Instruction_Tableswitch tlookup = ( Instruction_Tableswitch ) instrn;

            if ( ( ( Instruction ) clonedstmtsHT.get ( tlookup.default_inst ) ) != null )
            tlookup.default_inst = ( Instruction ) clonedstmtsHT.get ( tlookup.default_inst );

            for(int cnt=0;cnt<(tlookup.high - tlookup.low + 1);cnt++)
            {

             if ( ( ( Instruction ) clonedstmtsHT.get ( tlookup.jump_insts[cnt]) ) != null )
             tlookup.jump_insts[cnt] = ( Instruction ) clonedstmtsHT.get ( tlookup.jump_insts[cnt] );

            }

           }

          } // WHILE
   
          RetToOrigJsr.put ( clonedjsrtargetBB.tail, ( Instruction ) RetToJsr.get ( BBtail ) );
          
          RetToOrigJsrBB.put ( clonedjsrtargetBB.tail, ( BasicBlock ) RetToJsrBB.get ( BBtail ) );

          RetToOrigRetBB.put ( clonedjsrtargetBB.tail, succBB );

          RetToJsr.put ( clonedjsrtargetBB.tail, i );

//          System.out.println ( "RET "+clonedjsrtargetBB.tail+" JSR "+i );

          RetToJsrBB.put ( clonedjsrtargetBB.tail, b );

          RetToRetBB.put ( clonedjsrtargetBB.tail, clonedjsrtargetBB );

          RetToJsrSucc.put ( clonedjsrtargetBB.tail, highestBlock ); 

          RetToOrigJsrSucc.put ( clonedjsrtargetBB.tail, b.succ.elementAt(0) );

         } catch (  java.lang.CloneNotSupportedException e ) {

              System.out.println ( "CLONE UNSUCCESSFUL" ); }
       }

     }
     else  // LAST STMT OF BB WAS NOT A RET 
     {

       java.util.Vector succsuccBB = succBB.succ; 

       for(int n = 0; n < succsuccBB.size(); n++)
       {

        BasicBlock BBnext = (BasicBlock) succsuccBB.elementAt(n);

        successors.add( BBnext );

      }

     }

    } // WHILE 

   }

   b = b.next;

  }

 }



  private HashMap clonedstmtsHT = new HashMap(); 



  private void setHighestBlock ( BasicBlock highestBB ) throws java.lang.CloneNotSupportedException {

   // CLONE THE HIGHEST BB ( IMMEDIATE SUCC OF JSRBB ) TO BE CLONED FIRST 

   clonedHT = new HashMap();

   clonedstmtsHT = new HashMap();

   Instruction prev = highestBB.head;

   Instruction clonedprev = ( Instruction ) prev.clone();

   clonedstmtsHT.put ( prev, clonedprev );

   Instruction clonedhead = clonedprev;

   method.instructionList.add ( clonedhead );

   Instruction clonedcurrent = null;

   while ( prev != highestBB.tail )
   {

    Instruction current = prev.next;
   
    clonedcurrent = ( Instruction ) current.clone();

    clonedstmtsHT.put ( current, clonedcurrent );

    // System.out.println ( "CLONED "+ clonedcurrent );

    method.instructionList.add ( clonedcurrent );

    clonedprev.next = clonedcurrent;

    prev = current;

    clonedprev = clonedcurrent;

   }

   buildBasicBlock( clonedhead );
   
   highestBlock = new BasicBlock(clonedhead);

   h.put ( clonedhead, highestBlock );

   clonedHT.put ( highestBB, highestBlock );

   orighighestBlock = highestBB;

  }








  Map clonedHT = new HashMap(); 

  BasicBlock orighighestBlock;






  
  private BasicBlock cloneJsrTargetBB( BasicBlock lowestBB, BasicBlock highestBB ) throws java.lang.CloneNotSupportedException { 

  BasicBlock clonedBB = null;

  if ( lowestBB == highestBB )
  return highestBlock;
  else
  {

   if ( ( ( BasicBlock ) clonedHT.get ( lowestBB ) == null ) )
   {

    // NOT YET CLONED EVERYTHING, SO CLONE THIS BB

    Instruction prev = lowestBB.head;

    Instruction clonedprev = ( Instruction ) prev.clone();

    clonedstmtsHT.put ( prev, clonedprev );

    Instruction clonedhead = clonedprev;

    // System.out.println ( "CLONED "+ clonedhead );

    method.instructionList.add ( clonedhead );

    Instruction clonedcurrent = null;

    while ( prev != lowestBB.tail )
    {

     Instruction current = prev.next;
   
     clonedcurrent = ( Instruction ) current.clone();

     clonedstmtsHT.put ( current, clonedcurrent );

     method.instructionList.add ( clonedcurrent );

     clonedprev.next = clonedcurrent;

     prev = current;

     clonedprev = clonedcurrent;

    }

    buildBasicBlock( clonedhead );
   
    clonedBB = new BasicBlock(clonedhead);

    h.put ( clonedhead, clonedBB );

/*
    clonedBB.next = highestBlock.next;

    highestBlock.next = clonedBB;
*/
   
    clonedHT.put ( lowestBB, clonedBB );

    java.util.Vector lowestpreds = lowestBB.pred; 

    for(int n = 0; n < lowestpreds.size(); n++)
    {

     BasicBlock BBnext = (BasicBlock) lowestpreds.elementAt(n);

     if ( lowestBB != BBnext )
     {

      // CLONE ALL THE PRED BBs TILL HIGHEST BB IS REACHED 
      // RECURSION

      BasicBlock clonedJsrTargetBB = cloneJsrTargetBB ( BBnext, highestBB );

      clonedBB.pred.addElement ( clonedJsrTargetBB );

      clonedJsrTargetBB.succ.addElement ( clonedBB );

     }

    }

   }
   else  // ALREADY BEEN CLONED BEFORE
   clonedBB = ( BasicBlock ) clonedHT.get ( lowestBB );

  }

  return clonedBB;

 }









 private void arrangeclonedBBinorder() {

  ArrayList alreadyarranged = new ArrayList();

  BasicBlock nextBB = orighighestBlock.next; 
 
  BasicBlock nextclonedBB = ( BasicBlock ) clonedHT.get ( nextBB ); 

  BasicBlock currentclonedBB = highestBlock;

  alreadyarranged.add ( orighighestBlock );

  while ( nextclonedBB != null )
  {

   alreadyarranged.add ( nextBB );

   currentclonedBB.next = nextclonedBB;

   currentclonedBB = currentclonedBB.next;

   nextBB = nextBB.next;
   
   nextclonedBB = ( BasicBlock ) clonedHT.get ( nextBB ); 

  } 

  Iterator keysit = clonedHT.entrySet().iterator();
  
  while ( keysit.hasNext() )
  {

   BasicBlock bb = ( BasicBlock ) (( Map.Entry ) keysit.next()).getKey(); 
  
   if ( ! alreadyarranged.contains ( bb ) )
   {

    nextclonedBB = ( BasicBlock ) clonedHT.get ( bb );
  
    currentclonedBB.next = nextclonedBB;

    currentclonedBB = currentclonedBB.next;

   }

  }

 }






 







 
 private void JsrEliminate() {

  BasicBlock b = cfg;

  Instruction i = null;

  while ( b != null )
  {

   i = b.tail;
 
   if ( i instanceof Instruction_Ret )
   {

    Instruction originstruction = null;

    BasicBlock matchingjsrBB = ( BasicBlock ) RetToJsrBB.get ( i );

    BasicBlock matchingjsrnextBB = null;

    matchingjsrnextBB = ( BasicBlock) h.get ( (Instruction ) JsrToNext.get ( matchingjsrBB.tail ) );

     b.succ.removeAllElements();

     b.succ.addElement ( matchingjsrnextBB );

    for ( int k= matchingjsrnextBB.pred.size() - 1; k > -1;k-- ) 
    { 

     BasicBlock tempBB = ( BasicBlock ) matchingjsrnextBB.pred.elementAt ( k );

     if ( tempBB.tail instanceof Instruction_Ret )
     matchingjsrnextBB.pred.removeElement ( tempBB );

    } 


    matchingjsrnextBB.pred.addElement ( b );

    BasicBlock matchingjsrsuccBB = ( BasicBlock ) RetToJsrSucc.get( i );

    Instruction temp = b.head;

    if ( b.head == b.tail )  // 1 INSTRUCTION IN BB
    {

     originstruction = b.tail;

     b.head = new Instruction_Goto();
 
     b.head.branchpoints ( matchingjsrnextBB.head );

     ( ( Instruction_Goto ) b.head).target = matchingjsrnextBB.head;

     if ( originstruction.labelled ) 
     b.head.labelled = true;

     Iterator entriesIt = JsrToNext.entrySet().iterator();

     while ( entriesIt.hasNext() )
     {

      Instruction entryins = ( Instruction ) ( ( Map.Entry ) entriesIt.next() ).getKey();

      if ( ( ( Instruction ) JsrToNext.get ( entryins ) ) ==  originstruction )
      {

       JsrToNext.put ( entryins, b.head );

      }

     }

     replacedinstructionHT.put ( originstruction, b.head ); 

     method.instructionList.add ( /* method.instructionList.indexOf ( originstruction ), */ b.head );

     h.put ( b.head, b );

     b.tail = b.head;

    }
    else
    { 

     originstruction = b.tail;

     while ( temp.next != b.tail )
     {

      temp = temp.next;

     }

     temp.next = new Instruction_Goto();

     temp.next.branchpoints ( matchingjsrnextBB.head );

     ((Instruction_Goto) temp.next).target = matchingjsrnextBB.head;

     if ( originstruction.labelled ) 
     temp.next.labelled = true;


     Iterator entriesIt = JsrToNext.entrySet().iterator();

     while ( entriesIt.hasNext() )
     {

      Instruction entryins = ( Instruction ) ( ( Map.Entry ) entriesIt.next() ).getKey();

      if ( ( ( Instruction ) JsrToNext.get ( entryins ) ) ==  originstruction )
      {

       JsrToNext.put ( entryins, temp.next );

      }

     }

     replacedinstructionHT.put ( originstruction, temp.next );

     method.instructionList.add ( /* method.instructionList.indexOf ( originstruction ), */ temp.next );

     b.tail = temp.next;

    }

    b.tail.next = null;

    method.instructionList.remove ( originstruction );

    temp = matchingjsrBB.head;

    if ( matchingjsrBB.head == matchingjsrBB.tail )
    {

     originstruction = matchingjsrBB.tail;

     matchingjsrBB.head = new Instruction_Goto();

     matchingjsrBB.head.branchpoints ( matchingjsrsuccBB.head.next );

     ((Instruction_Goto)matchingjsrBB.head).target = matchingjsrsuccBB.head.next;

     if ( originstruction.labelled ) 
     matchingjsrBB.head.labelled = true;



     Iterator entriesIt = JsrToNext.entrySet().iterator();

     while ( entriesIt.hasNext() )
     {

      Instruction entryins = ( Instruction ) ( ( Map.Entry ) entriesIt.next() ).getKey();

      if ( ( ( Instruction ) JsrToNext.get ( entryins ) ) ==  originstruction )
      {

       JsrToNext.put ( entryins, matchingjsrBB.head );

      }

     }

     replacedinstructionHT.put ( originstruction, matchingjsrBB.head );

     method.instructionList.add ( /* method.instructionList.indexOf ( originstruction ) , */ matchingjsrBB.head );

     h.put ( matchingjsrBB.head, matchingjsrBB );

     matchingjsrBB.tail = matchingjsrBB.head;

    }
    else
    { 

     originstruction = matchingjsrBB.tail;

     while ( temp.next != matchingjsrBB.tail )
     {

      temp = temp.next;

     }

     temp.next = new Instruction_Goto();

     temp.next.branchpoints ( matchingjsrsuccBB.head.next );


     ((Instruction_Goto)temp.next).target = matchingjsrsuccBB.head.next;

     if ( originstruction.labelled ) 
     temp.next.labelled = true;


     Iterator entriesIt = JsrToNext.entrySet().iterator();

     while ( entriesIt.hasNext() )
     {

      Instruction entryins = ( Instruction ) ( ( Map.Entry ) entriesIt.next() ).getKey();

      if ( ( ( Instruction ) JsrToNext.get ( entryins ) ) ==  originstruction )
      {

       JsrToNext.put ( entryins, temp.next );

      }

     }


     replacedinstructionHT.put ( originstruction, temp.next );

     method.instructionList.add ( /* method.instructionList.indexOf ( originstruction ), */ temp.next );

     matchingjsrBB.tail = temp.next;

    }

    matchingjsrBB.tail.next = null;

    method.instructionList.remove ( originstruction );

    temp = matchingjsrsuccBB.head;

    Iterator entriesIt = JsrToNext.entrySet().iterator();

    while ( entriesIt.hasNext() )
    {

     Instruction entryins = ( Instruction ) ( ( Map.Entry ) entriesIt.next() ).getKey();

     if ( ( ( Instruction ) JsrToNext.get ( entryins ) ) ==  originstruction )
     {

      JsrToNext.put ( entryins, temp.next );

     }

    }

    replacedinstructionHT.put ( temp, temp.next );

    matchingjsrsuccBB.head = temp.next; 

    h.put ( temp.next, matchingjsrsuccBB );

    method.instructionList.remove ( temp );

   }

   b = b.next;

  }

 }






 private HashMap replacedinstructionHT = new HashMap(6, 0.7f);
 







 private void fixupJsrRets() {

  BasicBlock b = cfg;

  Instruction i = null;

  while ( b != null )
  {

   i = b.tail;
 
   if ( i instanceof Instruction_Ret )
   {

    Instruction_Jsr matchingjsr = ( Instruction_Jsr) RetToJsr.get ( i );
    
    BasicBlock matchingjsrBB = ( BasicBlock ) RetToJsrBB.get ( i );

    java.util.Vector succOfmatchingjsr = matchingjsrBB.succ; 

    BasicBlock WrongsuccBB = null;

    BasicBlock OrigsuccBB = (BasicBlock) succOfmatchingjsr.elementAt(0);

    BasicBlock NewsuccBB = ( BasicBlock ) RetToJsrSucc.get ( i );

    if ( OrigsuccBB != NewsuccBB )
    WrongsuccBB = OrigsuccBB;

    if ( WrongsuccBB != null )
    {

     BasicBlock OrigretBB = ( BasicBlock ) RetToOrigRetBB.get ( i );

     BasicBlock OrigjsrBB = ( BasicBlock ) RetToOrigJsrBB.get ( i );

     BasicBlock OrigjsrnextBB = null;

     OrigjsrnextBB = ( BasicBlock) h.get ( (Instruction ) JsrToNext.get ( OrigjsrBB.tail) );

     BasicBlock matchingjsrnextBB = null;

     matchingjsrnextBB = ( BasicBlock) h.get ( (Instruction ) JsrToNext.get ( matchingjsrBB.tail ) );

     OrigsuccBB.pred.removeElement ( matchingjsrBB );
    
     OrigretBB.succ.removeElement ( matchingjsrnextBB );

     matchingjsrBB.succ.addElement ( NewsuccBB );

     matchingjsrBB.succ.removeElement ( OrigsuccBB );

     matchingjsrnextBB.pred.addElement ( b );

     matchingjsrnextBB.pred.removeElement ( OrigretBB );

     for ( int k=NewsuccBB.pred.size() -1; k > -1;k-- ) 
     { 

      BasicBlock tempBB = ( BasicBlock ) NewsuccBB.pred.elementAt ( k );

      if ( tempBB.tail instanceof Instruction_Jsr )
      NewsuccBB.pred.removeElement ( tempBB );

     }

     NewsuccBB.pred.addElement ( matchingjsrBB );

     b.succ.removeAllElements();

     b.succ.addElement ( matchingjsrnextBB );

    }

   }

   b = b.next;

  }

 }




   // Constructs the actual control flow graph. Assumes the hash table
   // currently associates leaders with BasicBlocks, this function
   // builds the next[] and prev[] pointer arrays.
   private void buildCFG() {
      BasicBlock b,bb;
      Instruction i,branches[],nexti;
      int numb,k;
      Code_attribute ca = method.locate_code_attribute();
      b = cfg;
      // System.out.println("Building CFG...");
      while (b!=null) {
         /*for (Enumeration e = h.elem ents();e.hasMoreElements();) {
           b = (BasicBlock)(e.nextElement());*/
         i = b.tail;
         if (i.branches) {
            // must look out for athrow, which can call an exception handler

            if(i instanceof Instruction_Ret)
            {
                // Must make the ret point back to all the instructions
                // past a Jsr

                ListIterator instructionIt = method.instructionList.listIterator();
                List branchesList = new ArrayList();

                while(instructionIt.hasNext())
                {
                    Instruction ins = (Instruction) instructionIt.next();

                    if(ins instanceof Instruction_Jsr)
                    {
                        ListIterator succIt = method.instructionList.listIterator(
                            instructionIt.nextIndex());

                        if(succIt.hasNext())
                            branchesList.add(succIt.next());
                    }
                }

                branches = new Instruction[branchesList.size()];

                for(k = 0; k < branches.length; k++)
                    branches[k] = (Instruction) branchesList.get(k);
            } else if (i instanceof Instruction_Athrow) {
               // see how many targets it can reach.  Note that this is a
               // subset of the exception_table.
               int icount = 1;
               // not quite a subset---could also be that control exits this
               // method, so start icount at 1
               for (k = 0; k<ca.exception_table_length;k++) {
                  if (i.label >= ca.exception_table[k].start_inst.label &&
                      (ca.exception_table[k].end_inst==null ||
                       i.label < ca.exception_table[k].end_inst.label)) {
                     icount++;
                  }
               }
               branches = new Instruction[icount];
               branches[0] = null;
               icount = 1;
               for (k = 0; k<ca.exception_table_length;k++) {
                  if (i.label >= ca.exception_table[k].start_inst.label &&
                      (ca.exception_table[k].end_inst==null ||
                       i.label < ca.exception_table[k].end_inst.label)) {
                     branches[icount] = ca.exception_table[k].handler_inst;
                  }
               }
            } else {
               nexti = (i.next==null) ? ((b.next==null) ? null : b.next.head) : i.next;

               branches = i.branchpoints(nexti);
              
            }
            if (i.calls) numb = 1;
            else numb = 0;
            if (branches!=null)
               numb += branches.length;
            b.succ.ensureCapacity(b.succ.size()+numb);
            // System.out.println(i.label + "(" + i + " has " + numb + " branches)");
            if (i.calls && b.next!=null) {
               b.succ.addElement(b.next);
               b.next.pred.addElement(b);
            }
            if (branches!=null) {
               int j;
               for (j=0;j<branches.length;j++) {
 
                  if (branches[j]!=null) {
                     bb = (BasicBlock)(h.get(branches[j]));
                 
                     if (bb==null)
                     {
                 
                        System.out.println("Warning: target of a branch is null");
                        System.out.println ( i );

                     }
                     else {
                        b.succ.addElement(bb);
                        bb.pred.addElement(b);
                     }
                  }
               }
            }
         } else if (b.next!=null) { // BB ended not with a branch, so just go to next
            b.succ.addElement(b.next);
            b.next.pred.addElement(b);
         }
         b = b.next;
      }
      // One final step, run through exception handlers and mark which
      // basic blocks begin their code
      for (k=0;k<ca.exception_table_length;k++) {
         bb = (BasicBlock)(h.get(ca.exception_table[k].handler_inst));
         if (bb==null)
            System.out.println("Warning: No basic block found for" +
                               " start of exception handler code.");
         else {
            bb.beginException = true;
            ca.exception_table[k].b = bb;
         }
      }
   }

   // given the list of instructions head, this pulls off the front
   // basic block, terminates it with a null, and returns the next
   // instruction after.
   private static Instruction buildBasicBlock(Instruction head) {
      Instruction newhead,i;
      i = head;
      while (i!=null) {

         if (i.branches || (i.next!=null && i.next.labelled)) {
            newhead = i.next;
            i.next = null;
            return newhead;
         }

         i = i.next;
      }
      return null;
   }

   /** Reconstructs the instruction stream by appending the Instruction
    * lists associated with each basic block.
    * <p>
    * Note that this joins up the basic block Instruction lists, and so
    * they will no longer end with <i>null</i> after this.
    * @return the head of the list of instructions.
    */
    public Instruction reconstructInstructions() {
      BasicBlock b;
      Instruction last = null;

      b = cfg;
      while (b!=null) {
         if (b.tail!=null) {
            if (last!=null) last.next = b.head;
            last = b.tail;
         }
         b = b.next;
      }
      if (cfg != null)
        return cfg.head;
      else
        return null;
   }

   /** Main entry point for converting list of Instructions to Jimple statements;
    * performs flow analysis, constructs Jimple statements, and fixes jumps.
    * @param constant_pool constant pool of ClassFile.
    * @param this_class constant pool index of the CONSTANT_Class_info object for
    * this' class.
    * @return <i>true</i> if all ok, <i>false</i> if there was an error.
    * @see Stmt
    */
    public boolean jimplify(cp_info constant_pool[],int this_class, JimpleBody listBody)
   {
        Util.setClassNameToAbbreviation(new HashMap());

        Chain units = listBody.getUnits();

        this.listBody = listBody;
        this.units = units;
        instructionToFirstStmt = new HashMap();
        instructionToLastStmt = new HashMap();

        jmethod = listBody.getMethod();
        cm = Scene.v();
        
        Util.setActiveClassManager(cm);
        TypeArray.setClassManager(cm);
        TypeStack.setClassManager(cm);

        Set initialLocals = new ArraySet();

        List parameterTypes = jmethod.getParameterTypes();

        // Initialize nameToLocal which is an index*Type->Local map, which is used
        // to determine local in bytecode references.
        {
            Code_attribute ca = method.locate_code_attribute();
            LocalVariableTable_attribute la = ca.findLocalVariableTable();

            Util.activeVariableTable = la;
            
            Util.activeConstantPool = constant_pool;
            
            Type thisType = RefType.v(jmethod.getDeclaringClass().getName());
            boolean isStatic = Modifier.isStatic(jmethod.getModifiers());

            int currentLocalIndex = 0;

            // Initialize the 'this' variable
            {
                if(!isStatic)
                {
                    String name;
                    
                    if(!Util.useFaithfulNaming || la == null)
                        name = "l0";
                    else
                        name = la.getLocalVariableName(constant_pool, 0);
                        
                    Local local = Jimple.v().newLocal(name, UnknownType.v());

                    listBody.getLocals().add(local);

                    currentLocalIndex++;

                    units.add(Jimple.v().newIdentityStmt(local, Jimple.v().newThisRef(jmethod.getDeclaringClass().getType())));
                }
            }

            // Initialize parameters
            {
                Iterator typeIt = parameterTypes.iterator();
                int argCount = 0;

                while(typeIt.hasNext())
                {
                    String name;
                    Type type = (Type) typeIt.next();

                    if(!Util.useFaithfulNaming || la == null)
                        name = "l" + currentLocalIndex;
                    else
                        name = la.getLocalVariableName(constant_pool, currentLocalIndex);

                    Local local = Jimple.v().newLocal(name, UnknownType.v());
                    initialLocals.add(local);
                    listBody.getLocals().add(local);

                    units.add(Jimple.v().newIdentityStmt(local, Jimple.v().newParameterRef(type, argCount)));

                    if(type.equals(DoubleType.v()) ||
                        type.equals(LongType.v()))
                    {
                        currentLocalIndex += 2;
                    }
                    else {
                        currentLocalIndex += 1;
                    }

                    argCount++;
                }
            }

            Util.resetEasyNames();
        }

        jimplify(constant_pool,this_class);

        return true;
   }

   /** Main entry point for converting list of Instructions to Jimple statements;
    * performs flow analysis, constructs Jimple statements, and fixes jumps.
    * @param constant_pool constant pool of ClassFile.
    * @param this_class constant pool index of the CONSTANT_Class_info object for
    * this' class.
    * @param clearStacks if <i>true</i> semantic stacks will be deleted after
    * the process is complete.
    * @return <i>true</i> if all ok, <i>false</i> if there was an error.
    * @see CFG#jimplify(cp_info[], int)
    * @see Stmt
    */

     void jimplify(cp_info constant_pool[],int this_class)
    {
        Map instructionToSuccessors = new HashMap();
        Code_attribute codeAttribute = method.locate_code_attribute();
        Set handlerInstructions = new ArraySet();

        Map handlerInstructionToException = new HashMap();
        Map instructionToTypeStack;
        Map instructionToPostTypeStack;

        // System.out.println("Starting to jimplify: " + jmethod.getName());

        // Build up instructionToSuccessors table
        {
            // Put in all regular basic block successors
            {
                BasicBlock b = cfg;

                while(b != null)
                {
                    Instruction ins = b.head;

                    while(ins != null)
                    {

                        //System.out.println("ins:" + ins.toString());
                        
                        //if(ins instanceof Instruction_Goto)
                        //{
                        //    System.out.println("targets: ");
                        //}
                        
                        if(ins.next != null)
                        {
                            Set successors = new ArraySet();

                            successors.add(ins.next);

                            instructionToSuccessors.put(ins, successors);
                        }
                        else
                        {
                            // The successors are the ones from the basic block.

                            Set successors = new ArraySet();
                            java.util.Vector succ = b.succ;

                            for(int i = 0; i < succ.size(); i++)
                            {
                                 successors.add(((BasicBlock) succ.elementAt(i)).head);

                             }    
                            instructionToSuccessors.put(ins, successors);
                        }

                        ins = ins.next;
                    }

                    b = b.next;
                }
            }

            // Put in successors due to exception handlers
            {
                for(int i = 0; i < codeAttribute.exception_table_length; i++)
                {
                    Instruction startIns = codeAttribute.exception_table[i].start_inst;
                    Instruction endIns = codeAttribute.exception_table[i].end_inst;
                    Instruction handlerIns = codeAttribute.exception_table[i].handler_inst;

                    handlerInstructions.add(handlerIns);

                    // Determine exception to catch
                    {
                        int catchType = codeAttribute.exception_table[i].catch_type;

                        SootClass exception;

                        if(catchType != 0)
                        {
                            CONSTANT_Class_info classinfo = (CONSTANT_Class_info)
                                constant_pool[catchType];

                            String name = ((CONSTANT_Utf8_info) (constant_pool[classinfo.name_index])).
                                convert();
                            name = name.replace('/', '.');

                            exception = cm.getSootClass(name);
                        }
                        else
                            exception = cm.getSootClass("java.lang.Throwable");

                        handlerInstructionToException.put(handlerIns, exception);
                    }


                    if(startIns == endIns)
                        throw new RuntimeException("Empty catch range for exception handler");

                    Instruction ins = startIns;

                    for(;;)
                    {

                  
                        Set successors = (Set) instructionToSuccessors.get(ins);

                        successors.add(handlerIns);

                        ins = (Instruction) instructionToNext.get(ins);

                        if ( (ins == endIns ) ) 
                        {

                            break;
                        }
          
                    }
                }
            }
        }

        Set reachableInstructions = new HashSet();
        
        // Mark all the reachable instructions
        {
            LinkedList instructionsToVisit = new LinkedList();
            
            reachableInstructions.add(firstInstruction);
            instructionsToVisit.addLast(firstInstruction);
            
            while(!instructionsToVisit.isEmpty())
            {
                Instruction ins = (Instruction) instructionsToVisit.removeLast();
                Set s = (Set)instructionToSuccessors.get(ins);
                if (s.size() == 0)
                  continue;
                
                Iterator succIt = s.iterator();
                
                while(succIt.hasNext())
                {
                    Instruction succ = (Instruction) succIt.next();
                    
                    if(!reachableInstructions.contains(succ))
                    {
                        reachableInstructions.add(succ);
                        instructionsToVisit.addLast(succ);
                    }
                }
            }
        }
            
        /*
        // Check to see if any instruction is unmarked.
        {
            BasicBlock b = cfg;

             while(b != null)
            {
                Instruction ins = b.head;

                 while(ins != null)
                {
                    if(!reachableInstructions.contains(ins))
                        throw new RuntimeException("Method to jimplify contains unreachable code!  (not handled for now)");

                     ins = ins.next;
                }

                 b = b.next;
            }
        }
        */
        
        // Perform the flow analysis, and build up instructionToTypeStack and instructionToLocalArray
        {
            instructionToTypeStack = new HashMap();
            instructionToPostTypeStack = new HashMap();

            Set visitedInstructions = new HashSet();
            List changedInstructions = new ArrayList();

            TypeStack initialTypeStack;

            // Build up initial type stack and initial local array (for the first instruction)
            {
                initialTypeStack = TypeStack.v();
                    // the empty stack with nothing on it.
            }

            // Get the loop cranked up.
            {
                instructionToTypeStack.put(firstInstruction, initialTypeStack);

                visitedInstructions.add(firstInstruction);
                changedInstructions.add(firstInstruction);

                // System.out.println("firstInstruction:" + firstInstruction);
            }

            // Do the flow-analysis loop
            {
                while(!changedInstructions.isEmpty())
                {
                    Instruction ins = (Instruction) changedInstructions.get(0);

                    /*
                    // Some debugging info
                    {
                        System.out.println("Visiting: "  + ins);

                        System.out.println("[BeforeTypeStack]");
                        TypeStack typeStack = (TypeStack) instructionToTypeStack.get(ins);
                        typeStack.print(System.out);

                        //System.out.println("[BeforeLocalArray]");
                        //TypeArray localArray = (TypeArray) instructionToLocalArray.get(ins);

                        //localArray.print(System.out);
                    }
                    */
                    
                    changedInstructions.remove(0);

                    // System.out.println(ins);

                    OutFlow ret = processFlow(ins, (TypeStack) instructionToTypeStack.get(ins),
                        constant_pool);

                    instructionToPostTypeStack.put(ins, ret.typeStack);

                    /*
                    // More debugging info
                    {
                        System.out.println("[AfterTypeStack]");
                        ret.typeStack.print(System.out);

                        System.out.println("[AfterLocalArray]");
                        ret.localArray.print(System.out);
                    }
                      */

                    Object[] successors = ((Set) instructionToSuccessors.get(ins)).toArray();

                    /*
                    if(successors.length != 1)
                        System.out.println();
                      */


                    for(int i = 0; i < successors.length; i++)
                    {
                        Instruction s = (Instruction) successors[i];

                        if(!visitedInstructions.contains(s))
                        {
                            // Special case for the first time visiting.

                            if(handlerInstructions.contains(s))
                            {
                                TypeStack exceptionTypeStack = (TypeStack.v()).push(RefType.v(
                                    ((SootClass) handlerInstructionToException.get(s)).getName()));

                                instructionToTypeStack.put(s, exceptionTypeStack);
                            }
                            else {
                                instructionToTypeStack.put(s, ret.typeStack);
                            }

                            visitedInstructions.add(s);
                            changedInstructions.add(s);

                            // System.out.println("adding successor: " + s);
                        }
                        else {
                             // System.out.println("considering successor: " + s);
                            TypeStack newTypeStack,
                                oldTypeStack = (TypeStack) instructionToTypeStack.get(s);

                            if(handlerInstructions.contains(s))
                            {
                                // The type stack for an instruction handler should always be that of
                                // single object on the stack.

                                TypeStack exceptionTypeStack = (TypeStack.v()).push(RefType.v(
                                    ((SootClass) handlerInstructionToException.get(s)).getName()));

                                newTypeStack = exceptionTypeStack;
                            }
                            else
                                newTypeStack = ret.typeStack.merge(oldTypeStack);

                            if(!newTypeStack.equals(oldTypeStack))
                            {
                                changedInstructions.add(s);
                                // System.out.println("requires a revisit: " + s);
                            }

                            instructionToTypeStack.put(s, newTypeStack);
                        }
                    }
                }
            }
        }

        // Print out instructions + their localArray + typeStack
        {
            Instruction ins = firstInstruction;

     //       System.out.println();

            while(ins != null)
            {
                TypeStack typeStack = (TypeStack) instructionToTypeStack.get(ins);
                // TypeArray typeArray = (TypeArray) instructionToLocalArray.get(ins);
/*
                System.out.println("[TypeArray]");
                typeArray.print(System.out);
                System.out.println();

                System.out.println("[TypeStack]");
                typeStack.print(System.out);
                System.out.println();

                System.out.println(ins.toString());
*/

                ins = (Instruction) instructionToNext.get(ins);
/*

                System.out.println();
                System.out.println();
*/

            }
        }


        // System.out.println("Producing Jimple code...");

        // Jimplify each statement
        {
            BasicBlock b = cfg;

            while(b != null)
            {
                Instruction ins = b.head;
                b.statements = new ArrayList();

                List blockStatements = b.statements;

                while(ins != null)
                {
                    List statementsForIns = new ArrayList();

//                    System.out.println ( ins ); 

                    if(reachableInstructions.contains(ins))
                        generateJimple(ins, (TypeStack) instructionToTypeStack.get(ins),
                            (TypeStack) instructionToPostTypeStack.get(ins), constant_pool,
                            statementsForIns, b);
                    else
                        statementsForIns.add(Jimple.v().newNopStmt()); 

                    if(!statementsForIns.isEmpty())
                    {
                        for(int i = 0; i < statementsForIns.size(); i++)
                        {
                            units.add(statementsForIns.get(i));
                            blockStatements.add(statementsForIns.get(i));
                        }

                        instructionToFirstStmt.put(ins, statementsForIns.get(0));
                        instructionToLastStmt.put(ins, statementsForIns.get(statementsForIns.size() - 1));
                    }

                    ins = ins.next;
                }

                b = b.next;
            }
        }

        /*
        // Print out basic blocks
        {
            BasicBlock b = cfg;

            System.out.println("Basic blocks for: " + jmethod.getName());

            while(b != null)
            {
                Instruction ins = b.head;

                System.out.println();

                while(ins != null)
                {
                    System.out.println(ins.toString());
                    ins = ins.next;
                }

                b = b.next;
            }
        }
        */

        jimpleTargetFixup();  // fix up jump targets

        // Insert beginCatch/endCatch statements for exception handling
        {
            Map targetToHandler = new HashMap();
            
              for(int i = 0; i < codeAttribute.exception_table_length; i++)
              {
                    Instruction startIns = codeAttribute.exception_table[i].start_inst;
                    Instruction endIns = codeAttribute.exception_table[i].end_inst;
                    Instruction targetIns = codeAttribute.exception_table[i].handler_inst;

                    if(!instructionToFirstStmt.containsKey(startIns) ||
                        !instructionToLastStmt.containsKey(endIns))
                    {
                        throw new RuntimeException("Exception range does not coincide with jimple instructions");
                    }

                    Stmt firstStmt = (Stmt) instructionToFirstStmt.get(startIns);
                    Stmt lastStmt;

                    // Determine the last stmt
                    {
                        lastStmt = (Stmt)units.getPredOf(instructionToLastStmt.get(endIns));
                    }

                    if(!instructionToFirstStmt.containsKey(targetIns))
                    {
                        throw new RuntimeException
                            ("Exception handler does not coincide with jimple instruction");
                    }

                    SootClass exception;

                    // Determine exception to catch
                    {
                        int catchType = codeAttribute.exception_table[i].catch_type;

                        if(catchType != 0)
                        {
                            CONSTANT_Class_info classinfo = (CONSTANT_Class_info)
                                constant_pool[catchType];

                            String name = ((CONSTANT_Utf8_info) (constant_pool[classinfo.name_index])).
                                convert();
                            name = name.replace('/', '.');

                            exception = cm.getSootClass(name);
                        }
                        else
                            exception = cm.getSootClass("java.lang.Throwable");

                    }

                    Stmt newTarget;

                    // Insert assignment of exception
                    {
                        Stmt firstTargetStmt = (Stmt) instructionToFirstStmt.get(targetIns);
                        
                        if(targetToHandler.containsKey(firstTargetStmt))
                            newTarget = (Stmt) targetToHandler.get(firstTargetStmt);
                        else
                        {
                            Local local = Util.getLocalCreatingIfNecessary(listBody, "$stack0",
                                UnknownType.v());
    
                            newTarget = Jimple.v().newIdentityStmt(local, Jimple.v().newCaughtExceptionRef());
    
                            units.insertBefore(newTarget, firstTargetStmt);
                            
                            targetToHandler.put(firstTargetStmt, newTarget);
                        }
                    }

                    // Insert trap
                    {
                        Stmt afterEndStmt = (Stmt)units.getSuccOf(lastStmt);

                        Trap trap = Jimple.v().newTrap(exception, firstStmt, afterEndStmt, newTarget);
                        listBody.getTraps().add(trap);
                    }

                    /*
                    // Insert begincatch
                    {
                        Stmt beginCatchStmt = new BeginCatchStmt(exception, newTarget);
                        int startIndex = stmtList.indexOf(firstStmt);

                        stmtList.add(startIndex, beginCatchStmt);
                    }

                    // Insert endcatch
                    {
                        Stmt endCatchStmt = new EndCatchStmt(exception);
                        int endIndex = stmtList.indexOf(lastStmt);

                        stmtList.add(endIndex + 1, endCatchStmt);
                    } */
              }
        }
    }

    private Type byteCodeTypeOf(Type type)
    {
        if(type.equals(ShortType.v()) ||
            type.equals(CharType.v()) ||
            type.equals(ByteType.v()) ||
            type.equals(BooleanType.v()))
        {
            return IntType.v();
        }
        else
            return type;
    }

     OutFlow processFlow(Instruction ins, TypeStack typeStack,
        cp_info[] constant_pool)
    {
        int x;
        x = ((int)(ins.code))&0xff;

        //        System.out.println(ins.toString());
        switch(x)
        {
         case ByteCode.BIPUSH:
            typeStack = typeStack.push(IntType.v());
            break;

         case ByteCode.SIPUSH:
            typeStack = typeStack.push(IntType.v());
            break;

         case ByteCode.LDC1:
            return processCPEntry(constant_pool,
                ((Instruction_Ldc1)ins).arg_b, typeStack, jmethod);

         case ByteCode.LDC2:
         case ByteCode.LDC2W:
            return processCPEntry(constant_pool,
                ((Instruction_intindex)ins).arg_i, typeStack, jmethod);

         case ByteCode.ACONST_NULL:
            typeStack = typeStack.push(RefType.v("java.lang.Object"));
            break;

         case ByteCode.ICONST_M1:
         case ByteCode.ICONST_0:
         case ByteCode.ICONST_1:
         case ByteCode.ICONST_2:
         case ByteCode.ICONST_3:
         case ByteCode.ICONST_4:
         case ByteCode.ICONST_5:
            typeStack = typeStack.push(IntType.v());
            break;
         case ByteCode.LCONST_0:
         case ByteCode.LCONST_1:
            typeStack = typeStack.push(LongType.v());
            typeStack = typeStack.push(Long2ndHalfType.v());
            break;
         case ByteCode.FCONST_0:
         case ByteCode.FCONST_1:
         case ByteCode.FCONST_2:
            typeStack = typeStack.push(FloatType.v());
            break;
         case ByteCode.DCONST_0:
         case ByteCode.DCONST_1:
            typeStack = typeStack.push(DoubleType.v());
            typeStack = typeStack.push(Double2ndHalfType.v());
            break;
         case ByteCode.ILOAD:
            typeStack = typeStack.push(IntType.v());
            break;

         case ByteCode.FLOAD:
            typeStack = typeStack.push(FloatType.v());
            break;

         case ByteCode.ALOAD:
            typeStack = typeStack.push(RefType.v("java.lang.Object"));
                // this is highly imprecise
            break;

         case ByteCode.DLOAD:
            typeStack = typeStack.push(DoubleType.v());
            typeStack = typeStack.push(Double2ndHalfType.v());
            break;

         case ByteCode.LLOAD:
            typeStack = typeStack.push(LongType.v());
            typeStack = typeStack.push(Long2ndHalfType.v());
            break;

         case ByteCode.ILOAD_0:
         case ByteCode.ILOAD_1:
         case ByteCode.ILOAD_2:
         case ByteCode.ILOAD_3:
            typeStack = typeStack.push(IntType.v());
            break;

         case ByteCode.FLOAD_0:
         case ByteCode.FLOAD_1:
         case ByteCode.FLOAD_2:
         case ByteCode.FLOAD_3:
            typeStack = typeStack.push(FloatType.v());
            break;

         case ByteCode.ALOAD_0:
         case ByteCode.ALOAD_1:
         case ByteCode.ALOAD_2:
         case ByteCode.ALOAD_3:
            typeStack = typeStack.push(RefType.v("java.lang.Object"));
                // this is highly imprecise
            break;

         case ByteCode.LLOAD_0:
         case ByteCode.LLOAD_1:
         case ByteCode.LLOAD_2:
         case ByteCode.LLOAD_3:
            typeStack = typeStack.push(LongType.v());
            typeStack = typeStack.push(Long2ndHalfType.v());
            break;

         case ByteCode.DLOAD_0:
         case ByteCode.DLOAD_1:
         case ByteCode.DLOAD_2:
         case ByteCode.DLOAD_3:
            typeStack = typeStack.push(DoubleType.v());
            typeStack = typeStack.push(Double2ndHalfType.v());
            break;

         case ByteCode.ISTORE:
            typeStack = popSafe(typeStack, IntType.v());
            break;

         case ByteCode.FSTORE:
            typeStack = popSafe(typeStack, FloatType.v());
            break;

         case ByteCode.ASTORE:
            typeStack = typeStack.pop();
            break;

         case ByteCode.LSTORE:
            typeStack = popSafe(typeStack, Long2ndHalfType.v());
            typeStack = popSafe(typeStack, LongType.v());
            break;

         case ByteCode.DSTORE:
            typeStack = popSafe(typeStack, Double2ndHalfType.v());
            typeStack = popSafe(typeStack, DoubleType.v());
            break;

         case ByteCode.ISTORE_0:
         case ByteCode.ISTORE_1:
         case ByteCode.ISTORE_2:
         case ByteCode.ISTORE_3:
            typeStack = popSafe(typeStack, IntType.v());
            break;

         case ByteCode.FSTORE_0:
         case ByteCode.FSTORE_1:
         case ByteCode.FSTORE_2:
         case ByteCode.FSTORE_3:
            typeStack = popSafe(typeStack, FloatType.v());
            break;

         case ByteCode.ASTORE_0:
         case ByteCode.ASTORE_1:
         case ByteCode.ASTORE_2:
         case ByteCode.ASTORE_3:
            if(!(typeStack.top() instanceof StmtAddressType) &&
                !(typeStack.top() instanceof RefType) &&
                !(typeStack.top() instanceof ArrayType))
            {
                throw new RuntimeException("Astore failed, invalid stack type: " + typeStack.top());
            }

            typeStack = typeStack.pop();
            break;

         case ByteCode.LSTORE_0:
         case ByteCode.LSTORE_1:
         case ByteCode.LSTORE_2:
         case ByteCode.LSTORE_3:
            typeStack = popSafe(typeStack, Long2ndHalfType.v());
            typeStack = popSafe(typeStack, LongType.v());
            break;

         case ByteCode.DSTORE_0:
         case ByteCode.DSTORE_1:
         case ByteCode.DSTORE_2:
         case ByteCode.DSTORE_3:
            typeStack = popSafe(typeStack, Double2ndHalfType.v());
            typeStack = popSafe(typeStack, DoubleType.v());
            break;

         case ByteCode.IINC:
            break;

         case ByteCode.WIDE:
            throw new RuntimeException("Wide instruction should not be encountered");
            // break;

         case ByteCode.NEWARRAY:
         {
            typeStack = popSafe(typeStack, IntType.v());
            BaseType baseType = (BaseType) jimpleTypeOfAtype(((Instruction_Newarray)ins).atype);

            typeStack = typeStack.push(ArrayType.v(baseType, 1));
            break;
         }

        case ByteCode.ANEWARRAY:
        {
            CONSTANT_Class_info c = (CONSTANT_Class_info) constant_pool[
                ((Instruction_Anewarray)ins).arg_i];

            String name = ((CONSTANT_Utf8_info) (constant_pool[c.name_index])).convert();
            name = name.replace('/', '.');

            typeStack = popSafe(typeStack, IntType.v());
            typeStack = typeStack.push(ArrayType.v(
                RefType.v(name), 1));
            break;
        }

        case ByteCode.MULTIANEWARRAY:
        {
            int bdims = (int)(((Instruction_Multianewarray)ins).dims);


            CONSTANT_Class_info c = (CONSTANT_Class_info) constant_pool[
               ((Instruction_Multianewarray)ins).arg_i];

            String arrayDescriptor = ((CONSTANT_Utf8_info) (constant_pool[c.name_index])).convert();

            ArrayType arrayType = (ArrayType)
                Util.jimpleTypeOfFieldDescriptor(cm, arrayDescriptor);

            for (int j=0;j<bdims;j++)
                typeStack = popSafe(typeStack, IntType.v());

            typeStack = typeStack.push(arrayType);
            break;
        }

         case ByteCode.ARRAYLENGTH:
            typeStack = popSafeRefType(typeStack);
            typeStack = typeStack.push(IntType.v());
            break;

         case ByteCode.IALOAD:
         case ByteCode.BALOAD:
         case ByteCode.CALOAD:
         case ByteCode.SALOAD:
            typeStack = popSafe(typeStack, IntType.v());
            typeStack = popSafeRefType(typeStack);
            typeStack = typeStack.push(IntType.v());
            break;
         case ByteCode.FALOAD:
            typeStack = popSafe(typeStack, FloatType.v());
            typeStack = popSafeRefType(typeStack);
            typeStack = typeStack.push(FloatType.v());
            break;

         case ByteCode.AALOAD:
         {

            typeStack = popSafe(typeStack, IntType.v());

            if(typeStack.top() instanceof ArrayType)
            {
                ArrayType arrayType = (ArrayType) typeStack.top();
                typeStack = popSafeRefType(typeStack);

                if(arrayType.numDimensions == 1)
                    typeStack = typeStack.push(arrayType.baseType);
                else
                    typeStack = typeStack.push(ArrayType.v(arrayType.baseType, arrayType.numDimensions - 1));
            }
            else {
                // it's a null object

                typeStack = popSafeRefType(typeStack);

                typeStack = typeStack.push(RefType.v("java.lang.Object"));
            }

            break;
         }
         case ByteCode.LALOAD:
            typeStack = popSafe(typeStack, IntType.v());
            typeStack = popSafeRefType(typeStack);
            typeStack = typeStack.push(LongType.v());
            typeStack = typeStack.push(Long2ndHalfType.v());
            break;

         case ByteCode.DALOAD:
            typeStack = popSafe(typeStack, IntType.v());
            typeStack = popSafeRefType(typeStack);
            typeStack = typeStack.push(DoubleType.v());
            typeStack = typeStack.push(Double2ndHalfType.v());
            break;

         case ByteCode.IASTORE:
         case ByteCode.BASTORE:
         case ByteCode.CASTORE:
         case ByteCode.SASTORE:
            typeStack = popSafe(typeStack, IntType.v());
            typeStack = popSafe(typeStack, IntType.v());
            typeStack = popSafeRefType(typeStack);
            break;

         case ByteCode.AASTORE:
            typeStack = popSafeRefType(typeStack);
            typeStack = popSafe(typeStack, IntType.v());
            typeStack = popSafeRefType(typeStack);
            break;

         case ByteCode.FASTORE:
            typeStack = popSafe(typeStack, FloatType.v());
            typeStack = popSafe(typeStack, IntType.v());
            typeStack = popSafeRefType(typeStack);
            break;

         case ByteCode.LASTORE:
            typeStack = popSafe(typeStack, Long2ndHalfType.v());
            typeStack = popSafe(typeStack, LongType.v());
            typeStack = popSafe(typeStack, IntType.v());
            typeStack = popSafeRefType(typeStack);
            break;

         case ByteCode.DASTORE:
            typeStack = popSafe(typeStack, Double2ndHalfType.v());
            typeStack = popSafe(typeStack, DoubleType.v());
            typeStack = popSafe(typeStack, IntType.v());
            typeStack = popSafeRefType(typeStack);
            break;

         case ByteCode.NOP:
            break;

         case ByteCode.POP:
            typeStack = typeStack.pop();
            break;

         case ByteCode.POP2:
            typeStack = typeStack.pop();
            typeStack = typeStack.pop();
            break;

         case ByteCode.DUP:
            typeStack = typeStack.push(typeStack.top());
            break;

         case ByteCode.DUP2:
         {
            Type topType = typeStack.get(typeStack.topIndex()),
                              secondType = typeStack.get(typeStack.topIndex()-1);
            typeStack = (typeStack.push(secondType)).push(topType);
            break;
         }

         case ByteCode.DUP_X1:
         {
            Type topType = typeStack.get(typeStack.topIndex()),
                              secondType = typeStack.get(typeStack.topIndex()-1);

            typeStack = typeStack.pop().pop();

            typeStack = typeStack.push(topType).push(secondType).push(topType);
            break;
         }

         case ByteCode.DUP_X2:
         {
            Type topType = typeStack.get(typeStack.topIndex()),
                              secondType = typeStack.get(typeStack.topIndex()-1),
                              thirdType = typeStack.get(typeStack.topIndex()-2);

            typeStack = typeStack.pop().pop().pop();

            typeStack = typeStack.push(topType).push(thirdType).push(secondType).push(topType);
            break;
         }

         case ByteCode.DUP2_X1:
         {
            Type topType = typeStack.get(typeStack.topIndex()),
                              secondType = typeStack.get(typeStack.topIndex()-1),
                              thirdType = typeStack.get(typeStack.topIndex()-2);

            typeStack = typeStack.pop().pop().pop();

            typeStack = typeStack.push(secondType).push(topType).
                push(thirdType).push(secondType).push(topType);
            break;
         }

         case ByteCode.DUP2_X2:
         {
            Type topType = typeStack.get(typeStack.topIndex()),
                              secondType = typeStack.get(typeStack.topIndex()-1),
                              thirdType = typeStack.get(typeStack.topIndex()-2),
                              fourthType = typeStack.get(typeStack.topIndex()-3);

            typeStack = typeStack.pop().pop().pop().pop();

            typeStack = typeStack.push(secondType).push(topType).
                push(fourthType).push(thirdType).push(secondType).push(topType);
            break;
         }

         case ByteCode.SWAP:
         {
            Type topType = typeStack.top();

            typeStack = typeStack.pop();

            Type secondType = typeStack.top();

            typeStack = typeStack.pop();

            typeStack = typeStack.push(topType);
            typeStack = typeStack.push(secondType);
            break;
         }


         case ByteCode.IADD:
         case ByteCode.ISUB:
         case ByteCode.IMUL:
         case ByteCode.IDIV:
         case ByteCode.IREM:
         case ByteCode.ISHL:
         case ByteCode.ISHR:
         case ByteCode.IUSHR:
         case ByteCode.IAND:
         case ByteCode.IOR:
         case ByteCode.IXOR:
            typeStack = popSafe(typeStack, IntType.v());
            typeStack = popSafe(typeStack, IntType.v());
            typeStack = typeStack.push(IntType.v());
            break;

         case ByteCode.LUSHR:
         case ByteCode.LSHR:
         case ByteCode.LSHL:
            typeStack = popSafe(typeStack, IntType.v());
            typeStack = popSafe(typeStack, Long2ndHalfType.v());
            typeStack = popSafe(typeStack, LongType.v());
            typeStack = typeStack.push(LongType.v());
            typeStack = typeStack.push(Long2ndHalfType.v());
            break;

         case ByteCode.LREM:
         case ByteCode.LDIV:
         case ByteCode.LMUL:
         case ByteCode.LSUB:
         case ByteCode.LADD:
         case ByteCode.LAND:
         case ByteCode.LOR:
         case ByteCode.LXOR:
            typeStack = popSafe(typeStack, Long2ndHalfType.v());
            typeStack = popSafe(typeStack, LongType.v());
            typeStack = popSafe(typeStack, Long2ndHalfType.v());
            typeStack = popSafe(typeStack, LongType.v());
            typeStack = typeStack.push(LongType.v());
            typeStack = typeStack.push(Long2ndHalfType.v());
            break;

         case ByteCode.FREM:
         case ByteCode.FDIV:
         case ByteCode.FMUL:
         case ByteCode.FSUB:
         case ByteCode.FADD:
            typeStack = popSafe(typeStack, FloatType.v());
            typeStack = popSafe(typeStack, FloatType.v());
            typeStack = typeStack.push(FloatType.v());
            break;

         case ByteCode.DREM:
         case ByteCode.DDIV:
         case ByteCode.DMUL:
         case ByteCode.DSUB:
         case ByteCode.DADD:
            typeStack = popSafe(typeStack, Double2ndHalfType.v());
            typeStack = popSafe(typeStack, DoubleType.v());
            typeStack = popSafe(typeStack, Double2ndHalfType.v());
            typeStack = popSafe(typeStack, DoubleType.v());
            typeStack = typeStack.push(DoubleType.v());
            typeStack = typeStack.push(Double2ndHalfType.v());
            break;

         case ByteCode.INEG:
         case ByteCode.LNEG:
         case ByteCode.FNEG:
         case ByteCode.DNEG:
            // Doesn't check to see if the required types are on the stack, but it should
            // if it wanted to be safe.
            break;

         case ByteCode.I2L:
            typeStack = popSafe(typeStack, IntType.v());
            typeStack = typeStack.push(LongType.v());
            typeStack = typeStack.push(Long2ndHalfType.v());
            break;

         case ByteCode.I2F:
            typeStack = popSafe(typeStack, IntType.v());
            typeStack = typeStack.push(FloatType.v());
            break;

         case ByteCode.I2D:
            typeStack = popSafe(typeStack, IntType.v());
            typeStack = typeStack.push(DoubleType.v());
            typeStack = typeStack.push(Double2ndHalfType.v());
            break;

         case ByteCode.L2I:
            typeStack = popSafe(typeStack, Long2ndHalfType.v());
            typeStack = popSafe(typeStack, LongType.v());
            typeStack = typeStack.push(IntType.v());
            break;

         case ByteCode.L2F:
            typeStack = popSafe(typeStack, Long2ndHalfType.v());
            typeStack = popSafe(typeStack, LongType.v());
            typeStack = typeStack.push(FloatType.v());
            break;

         case ByteCode.L2D:
            typeStack = popSafe(typeStack, Long2ndHalfType.v());
            typeStack = popSafe(typeStack, LongType.v());
            typeStack = typeStack.push(DoubleType.v());
            typeStack = typeStack.push(Double2ndHalfType.v());
            break;

         case ByteCode.F2I:
            typeStack = popSafe(typeStack, FloatType.v());
            typeStack = typeStack.push(IntType.v());
            break;

         case ByteCode.F2L:
            typeStack = popSafe(typeStack, FloatType.v());
            typeStack = typeStack.push(LongType.v());
            typeStack = typeStack.push(Long2ndHalfType.v());
            break;

         case ByteCode.F2D:
            typeStack = popSafe(typeStack, FloatType.v());
            typeStack = typeStack.push(DoubleType.v());
            typeStack = typeStack.push(Double2ndHalfType.v());
            break;

         case ByteCode.D2I:
            typeStack = popSafe(typeStack, Double2ndHalfType.v());
            typeStack = popSafe(typeStack, DoubleType.v());
            typeStack = typeStack.push(IntType.v());
            break;

         case ByteCode.D2L:
            typeStack = popSafe(typeStack, Double2ndHalfType.v());
            typeStack = popSafe(typeStack, DoubleType.v());
            typeStack = typeStack.push(LongType.v());
            typeStack = typeStack.push(Long2ndHalfType.v());
            break;

         case ByteCode.D2F:
            typeStack = popSafe(typeStack, Double2ndHalfType.v());
            typeStack = popSafe(typeStack, DoubleType.v());
            typeStack = typeStack.push(FloatType.v());
            break;

         case ByteCode.INT2BYTE:
            break;
         case ByteCode.INT2CHAR:
            break;
         case ByteCode.INT2SHORT:
            break;

         case ByteCode.IFEQ:
         case ByteCode.IFGT:
         case ByteCode.IFLT:
         case ByteCode.IFLE:
         case ByteCode.IFNE:
         case ByteCode.IFGE:
            typeStack = popSafe(typeStack, IntType.v());
            break;

         case ByteCode.IFNULL:
         case ByteCode.IFNONNULL:
            typeStack = popSafeRefType(typeStack);
            break;

         case ByteCode.IF_ICMPEQ:
         case ByteCode.IF_ICMPLT:
         case ByteCode.IF_ICMPLE:
         case ByteCode.IF_ICMPNE:
         case ByteCode.IF_ICMPGT:
         case ByteCode.IF_ICMPGE:
            typeStack = popSafe(typeStack, IntType.v());
            typeStack = popSafe(typeStack, IntType.v());
            break;

         case ByteCode.LCMP:
            typeStack = popSafe(typeStack, Long2ndHalfType.v());
            typeStack = popSafe(typeStack, LongType.v());
            typeStack = popSafe(typeStack, Long2ndHalfType.v());
            typeStack = popSafe(typeStack, LongType.v());
            typeStack = typeStack.push(IntType.v());
            break;

         case ByteCode.FCMPL:
         case ByteCode.FCMPG:
            typeStack = popSafe(typeStack, FloatType.v());
            typeStack = popSafe(typeStack, FloatType.v());
            typeStack = typeStack.push(IntType.v());
            break;

         case ByteCode.DCMPL:
         case ByteCode.DCMPG:
            typeStack = popSafe(typeStack, Double2ndHalfType.v());
            typeStack = popSafe(typeStack, DoubleType.v());
            typeStack = popSafe(typeStack, Double2ndHalfType.v());
            typeStack = popSafe(typeStack, DoubleType.v());
            typeStack = typeStack.push(IntType.v());
            break;

         case ByteCode.IF_ACMPEQ:
         case ByteCode.IF_ACMPNE:
            typeStack = popSafeRefType(typeStack);
            typeStack = popSafeRefType(typeStack);
            break;

         case ByteCode.GOTO:
         case ByteCode.GOTO_W:
            break;

         case ByteCode.JSR:
         case ByteCode.JSR_W:
            typeStack = typeStack.push(StmtAddressType.v());
            break;

         case ByteCode.RET:
            break;

         case ByteCode.RET_W:
            break;

         case ByteCode.RETURN:
            break;

         case ByteCode.IRETURN:
            typeStack = popSafe(typeStack, IntType.v());
            break;

         case ByteCode.FRETURN:
            typeStack = popSafe(typeStack, FloatType.v());
            break;

         case ByteCode.ARETURN:
             typeStack = popSafeRefType(typeStack);
            break;

         case ByteCode.DRETURN:
            typeStack = popSafe(typeStack, Double2ndHalfType.v());
            typeStack = popSafe(typeStack, DoubleType.v());
            break;

         case ByteCode.LRETURN:
            typeStack = popSafe(typeStack, Long2ndHalfType.v());
            typeStack = popSafe(typeStack, LongType.v());
            break;

         case ByteCode.BREAKPOINT:
            break;

         case ByteCode.TABLESWITCH:
            typeStack = popSafe(typeStack, IntType.v());
            break;

         case ByteCode.LOOKUPSWITCH:
            typeStack = popSafe(typeStack, IntType.v());
            break;

         case ByteCode.PUTFIELD:
         {
            Type type = byteCodeTypeOf(jimpleTypeOfFieldInFieldRef(cm, constant_pool,
                ((Instruction_Putfield)ins).arg_i));

            if(type.equals(DoubleType.v()))
            {
                typeStack = popSafe(typeStack, Double2ndHalfType.v());
                typeStack = popSafe(typeStack, DoubleType.v());
            }
            else if(type.equals(LongType.v()))
            {
                typeStack = popSafe(typeStack, Long2ndHalfType.v());
                typeStack = popSafe(typeStack, LongType.v());
            }
            else if(type instanceof RefType)
                typeStack = popSafeRefType(typeStack);
            else
                typeStack = popSafe(typeStack, type);

            typeStack = popSafeRefType(typeStack);
            break;
         }

         case ByteCode.GETFIELD:
         {
            Type type = byteCodeTypeOf(jimpleTypeOfFieldInFieldRef(cm, constant_pool,
                ((Instruction_Getfield)ins).arg_i));

            typeStack = popSafeRefType(typeStack);

            if (type.equals(DoubleType.v()))
            {
                typeStack = typeStack.push(DoubleType.v());
                typeStack = typeStack.push(Double2ndHalfType.v());
            }
            else if(type.equals(LongType.v()))
            {
                typeStack = typeStack.push(LongType.v());
                typeStack = typeStack.push(Long2ndHalfType.v());
            }
            else
                typeStack = typeStack.push(type);
            break;
         }

         case ByteCode.PUTSTATIC:
         {
            Type type = byteCodeTypeOf(jimpleTypeOfFieldInFieldRef(cm, constant_pool,
                ((Instruction_Putstatic)ins).arg_i));

            if(type.equals(DoubleType.v()))
            {
                typeStack = popSafe(typeStack, Double2ndHalfType.v());
                typeStack = popSafe(typeStack, DoubleType.v());
            }
            else if(type.equals(LongType.v()))
            {
                typeStack = popSafe(typeStack, Long2ndHalfType.v());
                typeStack = popSafe(typeStack, LongType.v());
            }
            else if(type instanceof RefType)
                typeStack = popSafeRefType(typeStack);
            else
                typeStack = popSafe(typeStack, type);

            break;
         }

         case ByteCode.GETSTATIC:
         {
            Type type = byteCodeTypeOf(jimpleTypeOfFieldInFieldRef(cm, constant_pool,
                ((Instruction_Getstatic)ins).arg_i));

            if (type.equals(DoubleType.v()))
            {
                typeStack = typeStack.push(DoubleType.v());
                typeStack = typeStack.push(Double2ndHalfType.v());
            }
            else if(type.equals(LongType.v()))
            {
                typeStack = typeStack.push(LongType.v());
                typeStack = typeStack.push(Long2ndHalfType.v());
            }
            else
                typeStack = typeStack.push(type);
            break;
         }

         case ByteCode.INVOKEVIRTUAL:
         {
            Instruction_Invokevirtual iv = (Instruction_Invokevirtual)ins;
            int args = cp_info.countParams(constant_pool,iv.arg_i);
            Type returnType = byteCodeTypeOf(jimpleReturnTypeOfMethodRef(cm,
                constant_pool, iv.arg_i));

            // pop off parameters.
                for (int j=args-1;j>=0;j--)
                {
                    if(typeStack.top().equals(Long2ndHalfType.v()))
                    {
                        typeStack = popSafe(typeStack, Long2ndHalfType.v());
                        typeStack = popSafe(typeStack, LongType.v());

                    }
                    else if(typeStack.top().equals(Double2ndHalfType.v()))
                    {
                        typeStack = popSafe(typeStack, Double2ndHalfType.v());
                        typeStack = popSafe(typeStack, DoubleType.v());
                    }
                    else
                        typeStack = popSafe(typeStack, typeStack.top());
                }

            typeStack = popSafeRefType(typeStack);

            if(!returnType.equals(VoidType.v()))
                typeStack = smartPush(typeStack, returnType);
            break;
        }

        case ByteCode.INVOKENONVIRTUAL:
        {
            Instruction_Invokenonvirtual iv = (Instruction_Invokenonvirtual)ins;
            int args = cp_info.countParams(constant_pool,iv.arg_i);
            Type returnType = byteCodeTypeOf(jimpleReturnTypeOfMethodRef(cm,
                constant_pool, iv.arg_i));

            // pop off parameters.
                for (int j=args-1;j>=0;j--)
                {
                    if(typeStack.top().equals(Long2ndHalfType.v()))
                    {
                        typeStack = popSafe(typeStack, Long2ndHalfType.v());
                        typeStack = popSafe(typeStack, LongType.v());

                    }
                    else if(typeStack.top().equals(Double2ndHalfType.v()))
                    {
                        typeStack = popSafe(typeStack, Double2ndHalfType.v());
                        typeStack = popSafe(typeStack, DoubleType.v());
                    }
                    else
                        typeStack = popSafe(typeStack, typeStack.top());
                }

            typeStack = popSafeRefType(typeStack);

            if(!returnType.equals(VoidType.v()))
                typeStack = smartPush(typeStack, returnType);
            break;
        }

         case ByteCode.INVOKESTATIC:
         {
            Instruction_Invokestatic iv = (Instruction_Invokestatic)ins;
            int args = cp_info.countParams(constant_pool,iv.arg_i);
            Type returnType = byteCodeTypeOf(jimpleReturnTypeOfMethodRef(cm,
                constant_pool, iv.arg_i));

            // pop off parameters.
                for (int j=args-1;j>=0;j--)
                {
                    if(typeStack.top().equals(Long2ndHalfType.v()))
                    {
                        typeStack = popSafe(typeStack, Long2ndHalfType.v());
                        typeStack = popSafe(typeStack, LongType.v());

                    }
                    else if(typeStack.top().equals(Double2ndHalfType.v()))
                    {
                        typeStack = popSafe(typeStack, Double2ndHalfType.v());
                        typeStack = popSafe(typeStack, DoubleType.v());
                    }
                    else
                        typeStack = popSafe(typeStack, typeStack.top());
                }

            if(!returnType.equals(VoidType.v()))
                typeStack = smartPush(typeStack, returnType);
            break;
         }

         case ByteCode.INVOKEINTERFACE:
         {
            Instruction_Invokeinterface iv = (Instruction_Invokeinterface) ins;
            int args = cp_info.countParams(constant_pool,iv.arg_i);
            Type returnType = byteCodeTypeOf(jimpleReturnTypeOfInterfaceMethodRef(cm,
                constant_pool, iv.arg_i));

            // pop off parameters.
                for (int j=args-1;j>=0;j--)
                {
                    if(typeStack.top().equals(Long2ndHalfType.v()))
                    {
                        typeStack = popSafe(typeStack, Long2ndHalfType.v());
                        typeStack = popSafe(typeStack, LongType.v());

                    }
                    else if(typeStack.top().equals(Double2ndHalfType.v()))
                    {
                        typeStack = popSafe(typeStack, Double2ndHalfType.v());
                        typeStack = popSafe(typeStack, DoubleType.v());
                    }
                    else
                        typeStack = popSafe(typeStack, typeStack.top());
                }

            typeStack = popSafeRefType(typeStack);

            if(!returnType.equals(VoidType.v()))
                typeStack = smartPush(typeStack, returnType);
            break;
         }

         case ByteCode.ATHROW:
            // technically athrow leaves the stack in an undefined
            // state.  In fact, the top value is the one we actually
            // throw, but it should stay on the stack since the exception
            // handler expects to start that way, at least in the real JVM.
            break;

         case ByteCode.NEW:
         {
            Type type = RefType.v(getClassName(constant_pool, ((Instruction_New)ins).arg_i));

            typeStack = typeStack.push(type);
            break;
         }

         case ByteCode.CHECKCAST:
         {
            String className = getClassName(constant_pool, ((Instruction_Checkcast)ins).arg_i);

            Type castType;

            if(className.startsWith("["))
                castType = Util.jimpleTypeOfFieldDescriptor(cm, getClassName(constant_pool,
                ((Instruction_Checkcast)ins).arg_i));
            else
                castType = RefType.v(className);

            typeStack = popSafeRefType(typeStack);
            typeStack = typeStack.push(castType);
            break;
         }

         case ByteCode.INSTANCEOF:
         {
            typeStack = popSafeRefType(typeStack);
            typeStack = typeStack.push(IntType.v());
            break;
         }

         case ByteCode.MONITORENTER:
            typeStack = popSafeRefType(typeStack);
            break;
         case ByteCode.MONITOREXIT:
            typeStack = popSafeRefType(typeStack);
            break;

         default:
            throw new RuntimeException("processFlow failed: Unknown bytecode instruction: " + x);
         }

         return new OutFlow(typeStack);
    }

    private Type jimpleTypeOfFieldInFieldRef(Scene cm,
        cp_info[] constant_pool, int index)
    {
        CONSTANT_Fieldref_info fr = (CONSTANT_Fieldref_info)
                (constant_pool[index]);

        CONSTANT_NameAndType_info nat = (CONSTANT_NameAndType_info)
            (constant_pool[fr.name_and_type_index]);

        String fieldDescriptor = ((CONSTANT_Utf8_info)
        (constant_pool[nat.descriptor_index])).convert();

        return Util.jimpleTypeOfFieldDescriptor(cm, fieldDescriptor);
    }

    private Type jimpleReturnTypeOfMethodRef(Scene cm,
        cp_info[] constant_pool, int index)
    {
        CONSTANT_Methodref_info mr = (CONSTANT_Methodref_info)
                (constant_pool[index]);

        CONSTANT_NameAndType_info nat = (CONSTANT_NameAndType_info)
            (constant_pool[mr.name_and_type_index]);

        String methodDescriptor = ((CONSTANT_Utf8_info)
            (constant_pool[nat.descriptor_index])).convert();

        return Util.jimpleReturnTypeOfMethodDescriptor(cm, methodDescriptor);
    }

    private Type jimpleReturnTypeOfInterfaceMethodRef(Scene cm,
        cp_info[] constant_pool, int index)
    {
        CONSTANT_InterfaceMethodref_info mr = (CONSTANT_InterfaceMethodref_info)
                (constant_pool[index]);

        CONSTANT_NameAndType_info nat = (CONSTANT_NameAndType_info)
            (constant_pool[mr.name_and_type_index]);

        String methodDescriptor = ((CONSTANT_Utf8_info)
            (constant_pool[nat.descriptor_index])).convert();

        return Util.jimpleReturnTypeOfMethodDescriptor(cm, methodDescriptor);
    }

    private OutFlow processCPEntry(cp_info constant_pool[],int i,
                            TypeStack typeStack,
                            SootMethod jmethod)
    {
        cp_info c = constant_pool[i];

        if (c instanceof CONSTANT_Integer_info)
            typeStack = typeStack.push(IntType.v());
        else if (c instanceof CONSTANT_Float_info)
            typeStack = typeStack.push(FloatType.v());
        else if (c instanceof CONSTANT_Long_info)
        {
            typeStack = typeStack.push(LongType.v());
            typeStack = typeStack.push(Long2ndHalfType.v());
        }
        else if (c instanceof CONSTANT_Double_info)
        {
            typeStack = typeStack.push(DoubleType.v());
            typeStack = typeStack.push(Double2ndHalfType.v());
        }
        else if (c instanceof CONSTANT_String_info)
            typeStack = typeStack.push(RefType.v("java.lang.String"));
        else if (c instanceof CONSTANT_Utf8_info)
            typeStack = typeStack.push(RefType.v("java.lang.String"));
        else
            throw new RuntimeException("Attempting to push a non-constant cp entry");

        return new OutFlow(typeStack);
    }

   TypeStack smartPush(TypeStack typeStack, Type type)
   {
        if(type.equals(LongType.v()))
        {
            typeStack = typeStack.push(LongType.v());
            typeStack = typeStack.push(Long2ndHalfType.v());
        }
        else if(type.equals(DoubleType.v()))
        {
            typeStack = typeStack.push(DoubleType.v());
            typeStack = typeStack.push(Double2ndHalfType.v());
        }
        else
            typeStack = typeStack.push(type);

        return typeStack;
   }

   TypeStack popSafeRefType(TypeStack typeStack)
   {
        /*
        if(!(typeStack.top() instanceof RefType) &&
            !(typeStack.top() instanceof ArrayType))
        {
            throw new RuntimeException("popSafe failed; top: " + typeStack.top() +
                    " required: RefType");
        }
        */

        return typeStack.pop();
   }

   TypeStack popSafeArrayType(TypeStack typeStack)
   {
    /*
        if(!(typeStack.top() instanceof ArrayType) &&
            !(RefType.v("null").equals(typeStack.top())))
        {
            throw new RuntimeException("popSafe failed; top: " + typeStack.top() +
                    " required: ArrayType");
        }
      */

        return typeStack.pop();
   }

   TypeStack popSafe(TypeStack typeStack, Type requiredType)
   {
    /*
        if(!typeStack.top().equals(requiredType))
            throw new RuntimeException("popSafe failed; top: " + typeStack.top() +
            " required: " + requiredType);
      */

        return typeStack.pop();
   }

   void confirmType(Type actualType, Type requiredType)
   {
    /*
        if(!actualType.equals(requiredType))
            throw new RuntimeException("confirmType failed; actualType: " + actualType +
                "  required: " + requiredType);*/
   }

   String getClassName(cp_info[] constant_pool, int index)
   {
        CONSTANT_Class_info c = (CONSTANT_Class_info) constant_pool[index];

        String name = ((CONSTANT_Utf8_info) (constant_pool[c.name_index])).convert();

        return name.replace('/', '.');
   }

   void confirmRefType(Type actualType)
   {
    /*
        if(!(actualType instanceof RefType) &&
            !(actualType instanceof ArrayType))
            throw new RuntimeException("confirmRefType failed; actualType: " + actualType);*/
   }

   /** Runs through the given bbq contents performing the target fix-up pass;
    * Requires all reachable blocks to have their done flags set to true, and
    * this resets them all back to false;
    * @param bbq queue of BasicBlocks to process.
    * @see jimpleTargetFixup
    */
   private void processTargetFixup(BBQ bbq)
   {
      BasicBlock b,p;
      Stmt s;
      while (!bbq.isEmpty()) {
         try {
            b = bbq.pull();
         } catch(NoSuchElementException e) { break; }

               s = b.getTailJStmt();

            if (s instanceof GotoStmt)
            {
               if (b.succ.size() == 1)
               {
                   // Regular goto

                    ((GotoStmt)s).setTarget(((BasicBlock) b.succ.firstElement()).getHeadJStmt());
                }
                else
                {
                    // Goto derived from a jsr bytecode

                    if((BasicBlock)(b.succ.firstElement())==b.next)
                        ((GotoStmt)s).setTarget(((BasicBlock) b.succ.elementAt(1)).getHeadJStmt());
                    else
                        ((GotoStmt)s).setTarget(((BasicBlock) b.succ.firstElement()).getHeadJStmt());
                }
            }
            else if (s instanceof IfStmt)
            {
               if (b.succ.size()!=2)
                  System.out.println("How can an if not have 2 successors?");

               if((BasicBlock)(b.succ.firstElement())==b.next)
               {
                  ((IfStmt)s).setTarget(((BasicBlock) b.succ.elementAt(1)).getHeadJStmt());
               }
               else
               {
                  ((IfStmt)s).setTarget(((BasicBlock) b.succ.firstElement()).getHeadJStmt());
               }

            }
            else if (s instanceof TableSwitchStmt)
            {
               int count=0;
               TableSwitchStmt sts = (TableSwitchStmt)s;
               // Successors of the basic block ending with a switch statement
               // are listed in the successor vector in order, with the
               // default as the very first (0-th entry)

               for (Enumeration e = b.succ.elements();e.hasMoreElements();) {
                  p = (BasicBlock)(e.nextElement());
                  if (count==0) {
                     sts.setDefaultTarget(p.getHeadJStmt());
                  } else {
                     sts.setTarget(count-1, p.getHeadJStmt());
                  }
                  count++;
               }
            } else if (s instanceof LookupSwitchStmt)
            {
               int count=0;
               LookupSwitchStmt sls = (LookupSwitchStmt)s;
               // Successors of the basic block ending with a switch statement
               // are listed in the successor vector in order, with the
               // default as the very first (0-th entry)

               for (Enumeration e = b.succ.elements();e.hasMoreElements();) {
                  p = (BasicBlock)(e.nextElement());
                  if (count==0) {
                     sls.setDefaultTarget(p.getHeadJStmt());
                  } else {
                     sls.setTarget(count-1, p.getHeadJStmt());
                  }
                  count++;
               }
            }

         b.done = false;
         for (Enumeration e = b.succ.elements();e.hasMoreElements();) {
            p = (BasicBlock)(e.nextElement());
            if (p.done) bbq.push(p);
         }
      }
   }

   /** After the initial jimple construction, a second pass is made to fix up
    * missing Stmt targets for <tt>goto</tt>s, <tt>if</tt>'s etc.
    * @param c code attribute of this method.
    * @see CFG#jimplify
    */
    void jimpleTargetFixup() {
      BasicBlock b;
      BBQ bbq = new BBQ();

      Code_attribute c = method.locate_code_attribute();
      if (c==null) return;

      // Reset all the dones to true
      {
            BasicBlock bb = cfg;

        while(bb != null)
        {
            bb.done = true;
            bb = bb.next;
        }
      }


      // first process the main code
      bbq.push(cfg);
      processTargetFixup(bbq);

      // then the exceptions
      if (bbq.isEmpty()) {
         int i;
         for (i=0;i<c.exception_table_length;i++) {
            b = c.exception_table[i].b;
            // if block hasn't yet been processed...
            if (b!=null && b.done) {
               bbq.push(b);
               processTargetFixup(bbq);
               if (!bbq.isEmpty()) {
                  System.out.println("Error 2nd processing exception block.");
                  break;
               }
            }
         }
      }
   }

   private void generateJimpleForCPEntry(cp_info constant_pool[], int i,
                            TypeStack typeStack, TypeStack postTypeStack,
                            SootMethod jmethod, List statements)
   {
      Expr e;
      Stmt stmt;
      Value rvalue;

      cp_info c = constant_pool[i];

      if (c instanceof CONSTANT_Integer_info)
      {
         CONSTANT_Integer_info ci = (CONSTANT_Integer_info)c;

         rvalue = IntConstant.v((int) ci.bytes);
         stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
            postTypeStack.topIndex()), rvalue);
      }
      else if (c instanceof CONSTANT_Float_info)
      {
         CONSTANT_Float_info cf = (CONSTANT_Float_info)c;

         rvalue = FloatConstant.v(cf.convert());
         stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
            postTypeStack.topIndex()), rvalue);
      }
      else if (c instanceof CONSTANT_Long_info)
      {
         CONSTANT_Long_info cl = (CONSTANT_Long_info)c;

         rvalue = LongConstant.v(cl.convert());
         stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
            postTypeStack.topIndex()), rvalue);
      }
      else if (c instanceof CONSTANT_Double_info)
      {
         CONSTANT_Double_info cd = (CONSTANT_Double_info)c;

         rvalue = DoubleConstant.v(cd.convert());

         stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
            postTypeStack.topIndex()), rvalue);
      }
      else if (c instanceof CONSTANT_String_info)
      {
         CONSTANT_String_info cs = (CONSTANT_String_info)c;

         String constant = cs.toString(constant_pool);

         if(constant.startsWith("\"") && constant.endsWith("\""))
            constant = constant.substring(1, constant.length() - 1);

         rvalue = StringConstant.v(constant);
         stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
            postTypeStack.topIndex()), rvalue);
      }
      else if (c instanceof CONSTANT_Utf8_info)
      {
         CONSTANT_Utf8_info cu = (CONSTANT_Utf8_info)c;

         String constant = cu.convert();

         if(constant.startsWith("\"") && constant.endsWith("\""))
            constant = constant.substring(1, constant.length() - 1);

         rvalue = StringConstant.v(constant);
         stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
            postTypeStack.topIndex()), rvalue);
      }
      else {
        throw new RuntimeException("Attempting to push a non-constant cp entry");
      }

      statements.add(stmt);
    }

    void generateJimple(Instruction ins, TypeStack typeStack, TypeStack postTypeStack,
        cp_info constant_pool[],
        List statements, BasicBlock basicBlock)
   {
      Value[] params;
      Value v1=null,v2=null,v3=null,v4=null;
      Local l1 = null, l2 = null, l3 = null, l4 = null;

      Expr e=null,rhs=null;
      BinopExpr b=null;
      ConditionExpr co = null;

      ArrayRef a=null;
      int args;
      Value rvalue;

      int localIndex;

      Stmt stmt = null;

      int x = ((int)(ins.code))&0xff;

      Util.activeOriginalIndex = ins.originalIndex;
      Util.isLocalStore = false;
      Util.isWideLocalStore = false;
      
      switch(x)
      {
         case ByteCode.BIPUSH:
            rvalue = IntConstant.v(((Instruction_Bipush)ins).arg_b);
            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rvalue);
            break;

         case ByteCode.SIPUSH:
            rvalue = IntConstant.v(((Instruction_Sipush)ins).arg_i);
            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rvalue);
            break;

         case ByteCode.LDC1:
            generateJimpleForCPEntry(constant_pool,((Instruction_Ldc1)ins).arg_b, typeStack, postTypeStack,
                jmethod, statements);
            break;

         case ByteCode.LDC2:
         case ByteCode.LDC2W:
            generateJimpleForCPEntry(constant_pool, ((Instruction_intindex)ins).arg_i,
                typeStack, postTypeStack, jmethod, statements);
            break;

         case ByteCode.ACONST_NULL:
            rvalue = NullConstant.v();
            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rvalue);
            break;

         case ByteCode.ICONST_M1:
         case ByteCode.ICONST_0:
         case ByteCode.ICONST_1:
         case ByteCode.ICONST_2:
         case ByteCode.ICONST_3:
         case ByteCode.ICONST_4:
         case ByteCode.ICONST_5:
            rvalue = IntConstant.v(x-ByteCode.ICONST_0);
            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rvalue);
            break;

         case ByteCode.LCONST_0:
         case ByteCode.LCONST_1:
            rvalue = LongConstant.v(x-ByteCode.LCONST_0);
            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rvalue);
            break;

         case ByteCode.FCONST_0:
         case ByteCode.FCONST_1:
         case ByteCode.FCONST_2:
            rvalue = FloatConstant.v((float)(x - ByteCode.FCONST_0));
            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rvalue);
            break;

         case ByteCode.DCONST_0:
         case ByteCode.DCONST_1:
            rvalue = DoubleConstant.v((double)(x-ByteCode.DCONST_0));
            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rvalue);
            break;

         case ByteCode.ILOAD:
         {
            Local local = (Local)
                Util.getLocalForIndex(listBody, ((Instruction_bytevar) ins).arg_b);

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), local);
            break;
         }

         case ByteCode.FLOAD:
         {
            Local local = (Local)
                Util.getLocalForIndex(listBody, ((Instruction_bytevar)ins).arg_b);

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), local);
            break;
         }

         case ByteCode.ALOAD:
         {
            Local local =
                Util.getLocalForIndex(listBody, ((Instruction_bytevar)ins).arg_b);

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), local);
            break;
         }

         case ByteCode.DLOAD:
         {
            Local local =
                Util.getLocalForIndex(listBody, ((Instruction_bytevar)ins).arg_b);

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), local);
            break;
         }

         case ByteCode.LLOAD:
         {
            Local local =
                Util.getLocalForIndex(listBody, ((Instruction_bytevar)ins).arg_b);

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), local);
            break;
         }

         case ByteCode.ILOAD_0:
         case ByteCode.ILOAD_1:
         case ByteCode.ILOAD_2:
         case ByteCode.ILOAD_3:
         {
            Local local =
                Util.getLocalForIndex(listBody, (x - ByteCode.ILOAD_0));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), local);
            break;
         }

         case ByteCode.FLOAD_0:
         case ByteCode.FLOAD_1:
         case ByteCode.FLOAD_2:
         case ByteCode.FLOAD_3:
         {
            Local local =
                Util.getLocalForIndex(listBody, (x - ByteCode.FLOAD_0));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), local);
            break;
         }

         case ByteCode.ALOAD_0:
         case ByteCode.ALOAD_1:
         case ByteCode.ALOAD_2:
         case ByteCode.ALOAD_3:
         {
            Local local =
                Util.getLocalForIndex(listBody, (x - ByteCode.ALOAD_0));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), local);
            break;
         }

         case ByteCode.LLOAD_0:
         case ByteCode.LLOAD_1:
         case ByteCode.LLOAD_2:
         case ByteCode.LLOAD_3:
         {
            Local local =
                Util.getLocalForIndex(listBody, (x - ByteCode.LLOAD_0));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), local);
            break;
         }

         case ByteCode.DLOAD_0:
         case ByteCode.DLOAD_1:
         case ByteCode.DLOAD_2:
         case ByteCode.DLOAD_3:
         {
            Local local =
                Util.getLocalForIndex(listBody, (x - ByteCode.DLOAD_0));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), local);
            break;
         }

         case ByteCode.ISTORE:
         {
            Util.isLocalStore = true;
            Util.isWideLocalStore = true;
            
            Local local =
                Util.getLocalForIndex(listBody,
                ((Instruction_bytevar)ins).arg_b);

            stmt = Jimple.v().newAssignStmt(local, Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;
         }

         case ByteCode.FSTORE:
         {
            Util.isLocalStore = true;
            Util.isWideLocalStore = true;
            
            Local local =
                Util.getLocalForIndex(listBody,
                ((Instruction_bytevar)ins).arg_b);

            stmt = Jimple.v().newAssignStmt(local, Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;
         }

         case ByteCode.ASTORE:
         {
            Util.isLocalStore = true;
            Util.isWideLocalStore = true;
            
            Local local =
                Util.getLocalForIndex(listBody,
                ((Instruction_bytevar)ins).arg_b);

            stmt = Jimple.v().newAssignStmt(local, Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;
         }

         case ByteCode.LSTORE:
         {
            Util.isLocalStore = true;
            Util.isWideLocalStore = true;
            
            Local local =
                Util.getLocalForIndex(listBody,
                ((Instruction_bytevar)ins).arg_b);

            stmt = Jimple.v().newAssignStmt(local, Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;
         }

         case ByteCode.DSTORE:
         {
            Util.isLocalStore = true;
            Util.isWideLocalStore = true;
            
            Local local =
                Util.getLocalForIndex(listBody,
                ((Instruction_bytevar)ins).arg_b);

            stmt = Jimple.v().newAssignStmt(local, Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;
         }

         case ByteCode.ISTORE_0:
         case ByteCode.ISTORE_1:
         case ByteCode.ISTORE_2:
         case ByteCode.ISTORE_3:
         {
            Util.isLocalStore = true;
            Local local =
                Util.getLocalForIndex(listBody, (x - ByteCode.ISTORE_0));

            stmt = Jimple.v().newAssignStmt(local, Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;
         }

         case ByteCode.FSTORE_0:
         case ByteCode.FSTORE_1:
         case ByteCode.FSTORE_2:
         case ByteCode.FSTORE_3:
         {
            Util.isLocalStore = true;
            Local local = (Local)
                Util.getLocalForIndex(listBody, (x - ByteCode.FSTORE_0));

            stmt = Jimple.v().newAssignStmt(local, Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;
         }

         case ByteCode.ASTORE_0:
         case ByteCode.ASTORE_1:
         case ByteCode.ASTORE_2:
         case ByteCode.ASTORE_3:
         {
            Util.isLocalStore = true;
            Local local = Util.getLocalForIndex(listBody, (x - ByteCode.ASTORE_0));

            stmt = Jimple.v().newAssignStmt(local, Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;
         }

         case ByteCode.LSTORE_0:
         case ByteCode.LSTORE_1:
         case ByteCode.LSTORE_2:
         case ByteCode.LSTORE_3:
         {
            Util.isLocalStore = true;
            Local local =
                Util.getLocalForIndex(listBody, (x - ByteCode.LSTORE_0));

            stmt = Jimple.v().newAssignStmt(local, Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;
         }

         case ByteCode.DSTORE_0:
         case ByteCode.DSTORE_1:
         case ByteCode.DSTORE_2:
         case ByteCode.DSTORE_3:
         {
            Util.isLocalStore = true;
            Local local =
                Util.getLocalForIndex(listBody, (x - ByteCode.DSTORE_0));

            stmt = Jimple.v().newAssignStmt(local, Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;
         }

         case ByteCode.IINC:
         {
            Local local =
                Util.getLocalForIndex(listBody,
                ((Instruction_Iinc)ins).arg_b);

            int amt = (((Instruction_Iinc)ins).arg_c);
            rhs = Jimple.v().newAddExpr(local, IntConstant.v(amt));
            stmt = Jimple.v().newAssignStmt(local,rhs);
            break;
         }

         case ByteCode.WIDE:
            throw new RuntimeException("WIDE instruction should not be encountered anymore");
            // break;

         case ByteCode.NEWARRAY:
         {
            BaseType baseType = (BaseType) jimpleTypeOfAtype(((Instruction_Newarray)ins).atype);

            rhs = Jimple.v().newNewArrayExpr(baseType,
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody,
                postTypeStack, postTypeStack.topIndex()), rhs);

            break;
         }

         case ByteCode.ANEWARRAY:
         {
            String baseName = getClassName(constant_pool, ((Instruction_Anewarray)ins).arg_i);

            Type baseType;

            if(baseName.startsWith("["))
                baseType = Util.jimpleTypeOfFieldDescriptor(cm,
                    getClassName(constant_pool, ((Instruction_Anewarray)ins).arg_i));
            else
                baseType = RefType.v(baseName);

            rhs = Jimple.v().newNewArrayExpr(baseType, Util.getLocalForStackOp(listBody,
                typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody,
                postTypeStack, postTypeStack.topIndex()),rhs);
            break;
         }

         case ByteCode.MULTIANEWARRAY:
         {
               int bdims = (int)(((Instruction_Multianewarray)ins).dims);
               List dims = new ArrayList();

               for (int j=0; j < bdims; j++)
                  dims.add(Util.getLocalForStackOp(listBody, typeStack,
                    typeStack.topIndex() - bdims + j + 1));

               String mstype = constant_pool[((Instruction_Multianewarray)ins).arg_i].
                  toString(constant_pool);

               ArrayType jimpleType = (ArrayType) Util.jimpleTypeOfFieldDescriptor(cm, mstype);

               rhs = Jimple.v().newNewMultiArrayExpr(jimpleType, dims);

               stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()),rhs);
            break;
         }


         case ByteCode.ARRAYLENGTH:
            rhs = Jimple.v().newLengthExpr(
                    Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()),rhs);
            break;

         case ByteCode.IALOAD:
         case ByteCode.BALOAD:
         case ByteCode.CALOAD:
         case ByteCode.SALOAD:
         case ByteCode.FALOAD:
         case ByteCode.LALOAD:
         case ByteCode.DALOAD:
         case ByteCode.AALOAD:
            a = Jimple.v().newArrayRef(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1),
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody,
                postTypeStack, postTypeStack.topIndex()), a);

            break;

         case ByteCode.IASTORE:
         case ByteCode.FASTORE:
         case ByteCode.AASTORE:
         case ByteCode.BASTORE:
         case ByteCode.CASTORE:
         case ByteCode.SASTORE:
            a = Jimple.v().newArrayRef(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 2), Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1));

            stmt = Jimple.v().newAssignStmt(a, Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;

         case ByteCode.LASTORE:
         case ByteCode.DASTORE:
            a = Jimple.v().newArrayRef(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 3), Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 2));

            stmt = Jimple.v().newAssignStmt(a, Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;


         case ByteCode.NOP:
            stmt = Jimple.v().newNopStmt();
            break;

         case ByteCode.POP:
         case ByteCode.POP2:
            stmt = Jimple.v().newNopStmt();
            break;

         case ByteCode.DUP:
            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;

         case ByteCode.DUP2:
            if(typeSize(typeStack.top()) == 2)
            {
                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 1),
                    Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 1));
            }
            else {
                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 1),
                    Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 1));

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex()), Util.getLocalForStackOp(listBody,
                    typeStack, typeStack.topIndex()));

                statements.add(stmt);

                stmt = null;
            }
            break;

         case ByteCode.DUP_X1:
            l1 = Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex());
            l2 = Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1);

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), l1);

            statements.add(stmt);

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex() - 1), l2);

            statements.add(stmt);

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex() - 2), Util.getLocalForStackOp(listBody,
                postTypeStack, postTypeStack.topIndex()));

            statements.add(stmt);

            stmt = null;
            break;

         case ByteCode.DUP_X2:
            if(typeSize(typeStack.get(typeStack.topIndex() - 2)) == 2)
            {
                l3 = Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 2);
                l1 = Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex());

                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 3), l1);

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 2), l3);

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex()), l1);

                statements.add(stmt);

                stmt = null;
            }
            else {
                l3 = Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 2);
                l2 = Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 1);
                l1 = Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex());

                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex()), l1);

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 1), l2);

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 2), l3);

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 3), Util.getLocalForStackOp(
                    listBody, postTypeStack, postTypeStack.topIndex()));

                statements.add(stmt);

                stmt = null;
            }
            break;
            
        case ByteCode.DUP2_X1:
            if(typeSize(typeStack.get(typeStack.topIndex() - 1)) == 2)
            {
                l2 = Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 1);
                l3 = Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 2);

                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() -1), l2);

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 2), l3);
                
                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 4), 
                    Util.getLocalForStackOp(listBody, postTypeStack, postTypeStack.topIndex() - 1));

                statements.add(stmt);

                stmt = null;
            }
            else {
                l3 = Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 2);
                l2 = Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 1);
                l1 = Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex());

                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex()), l1);

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 1), l2);

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 2), l3);

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 3), Util.getLocalForStackOp(
                    listBody, postTypeStack, postTypeStack.topIndex()));

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 4), Util.getLocalForStackOp(
                    listBody, postTypeStack, postTypeStack.topIndex() - 1));

                statements.add(stmt);

                stmt = null;
            }
            break;

         case ByteCode.DUP2_X2:
            if(typeSize(typeStack.get(typeStack.topIndex() - 1)) == 2)
            {
                l2 = Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 1);

                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 1), l2);

                statements.add(stmt);
            }
            else {
                l1 = Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex());
                l2 = Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 1);

                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 1), l2);

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex()), l1);

                statements.add(stmt);

            }

            if(typeSize(typeStack.get(typeStack.topIndex() - 3)) == 2)
            {
                l4 = Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 3);

                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 3), l4);

                statements.add(stmt);
            }
            else {
                l4 = Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 3);
                l3 = Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 2);

                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 3), l4);

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 2), l3);

                statements.add(stmt);

            }

            if(typeSize(typeStack.get(typeStack.topIndex() - 1)) == 2)
            {
                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 5), Util.getLocalForStackOp(
                    listBody, postTypeStack, postTypeStack.topIndex() - 1));

                statements.add(stmt);
            }
            else {
                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 5), Util.getLocalForStackOp(
                    listBody, postTypeStack, postTypeStack.topIndex() - 1));

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 4), Util.getLocalForStackOp(
                    listBody, postTypeStack, postTypeStack.topIndex()));

                statements.add(stmt);
            }
               stmt = null;
            break;

         case ByteCode.SWAP:
         {
            Local first;

            typeStack = typeStack.push(typeStack.top());
            first = Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex());
            typeStack = typeStack.pop();
                // generation of a free temporary

            Local second = Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex());

            Local third = Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex() - 1);

            stmt = Jimple.v().newAssignStmt(first, second);
            statements.add(stmt);

            stmt = Jimple.v().newAssignStmt(second, third);
            statements.add(stmt);

            stmt = Jimple.v().newAssignStmt(third, first);
            statements.add(stmt);

            stmt = null;
            break;
         }

         case ByteCode.FADD:
         case ByteCode.IADD:
            rhs = Jimple.v().newAddExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1), Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.DADD:
         case ByteCode.LADD:
            rhs = Jimple.v().newAddExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 3), Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.FSUB:
         case ByteCode.ISUB:
            rhs = Jimple.v().newSubExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1), Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.DSUB:
         case ByteCode.LSUB:
            rhs = Jimple.v().newSubExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 3), Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.FMUL:
         case ByteCode.IMUL:
            rhs = Jimple.v().newMulExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1), Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.DMUL:
         case ByteCode.LMUL:
            rhs = Jimple.v().newMulExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 3), Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.FDIV:
         case ByteCode.IDIV:
            rhs = Jimple.v().newDivExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1), Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.DDIV:
         case ByteCode.LDIV:
            rhs = Jimple.v().newDivExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 3), Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.FREM:
         case ByteCode.IREM:
            rhs = Jimple.v().newRemExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1), Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.DREM:
         case ByteCode.LREM:
            rhs = Jimple.v().newRemExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 3), Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.INEG:
         case ByteCode.LNEG:
         case ByteCode.FNEG:
         case ByteCode.DNEG:
            rhs = Jimple.v().newNegExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));
            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()),rhs);
            break;

         case ByteCode.ISHL:
            rhs = Jimple.v().newShlExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1), Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.ISHR:
            rhs = Jimple.v().newShrExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1), Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.IUSHR:
            rhs = Jimple.v().newUshrExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1), Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.LSHL:
            rhs = Jimple.v().newShlExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 2), Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.LSHR:
            rhs = Jimple.v().newShrExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 2), Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.LUSHR:
            rhs = Jimple.v().newUshrExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 2), Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.IAND:
            rhs = Jimple.v().newAndExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1), Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.LAND:
            rhs = Jimple.v().newAndExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 3), Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.IOR:
            rhs = Jimple.v().newOrExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1), Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.LOR:
            rhs = Jimple.v().newOrExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 3), Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.IXOR:
            rhs = Jimple.v().newXorExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1), Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.LXOR:
            rhs = Jimple.v().newXorExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 3), Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.D2L:
         case ByteCode.F2L:
         case ByteCode.I2L:
            rhs = Jimple.v().newCastExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), LongType.v());

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.D2F:
         case ByteCode.L2F:
         case ByteCode.I2F:
            rhs = Jimple.v().newCastExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), FloatType.v());

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.I2D:
         case ByteCode.L2D:
         case ByteCode.F2D:
            rhs = Jimple.v().newCastExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), DoubleType.v());

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.L2I:
         case ByteCode.F2I:
         case ByteCode.D2I:
            rhs = Jimple.v().newCastExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), IntType.v());

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.INT2BYTE:
            rhs = Jimple.v().newCastExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), ByteType.v());

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.INT2CHAR:
            rhs = Jimple.v().newCastExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), CharType.v());

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.INT2SHORT:
            rhs = Jimple.v().newCastExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), ShortType.v());

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.IFEQ:
            co = Jimple.v().newEqExpr(Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()),
                IntConstant.v(0));

               stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IFNULL:
            co = Jimple.v().newEqExpr(Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()),
                NullConstant.v());

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IFLT:
            co = Jimple.v().newLtExpr(Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()),
                IntConstant.v(0));

               stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IFLE:
            co = Jimple.v().newLeExpr(Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()),
                    IntConstant.v(0));

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IFNE:
            co = Jimple.v().newNeExpr(Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()),
                    IntConstant.v(0));

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IFNONNULL:
            co = Jimple.v().newNeExpr(Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()),
                NullConstant.v());

                stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IFGT:
            co = Jimple.v().newGtExpr(Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()),
                    IntConstant.v(0));

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IFGE:
            co = Jimple.v().newGeExpr(Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()),
                IntConstant.v(0));

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IF_ICMPEQ:
            co = Jimple.v().newEqExpr(
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1),
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IF_ICMPLT:
            co = Jimple.v().newLtExpr(
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1),
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IF_ICMPLE:
            co = Jimple.v().newLeExpr(
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1),
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IF_ICMPNE:
            co = Jimple.v().newNeExpr(
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1),
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IF_ICMPGT:
            co = Jimple.v().newGtExpr(
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1),
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IF_ICMPGE:
            co = Jimple.v().newGeExpr(
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1),
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.LCMP:
            rhs = Jimple.v().newCmpExpr(
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-3),
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.FCMPL:
            rhs = Jimple.v().newCmplExpr(
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1),
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody,
                postTypeStack, postTypeStack.topIndex()),rhs);
            break;

         case ByteCode.FCMPG:
            rhs = Jimple.v().newCmpgExpr(
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1),
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody,
                postTypeStack, postTypeStack.topIndex()),rhs);
            break;

         case ByteCode.DCMPL:
            rhs = Jimple.v().newCmplExpr(
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-3),
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody,
                postTypeStack, postTypeStack.topIndex()),rhs);
            break;

         case ByteCode.DCMPG:
            rhs = Jimple.v().newCmpgExpr(
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-3),
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody,
                postTypeStack, postTypeStack.topIndex()),rhs);
            break;

         case ByteCode.IF_ACMPEQ:
            co = Jimple.v().newEqExpr(
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1),
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IF_ACMPNE:
            co = Jimple.v().newNeExpr(
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1),
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.GOTO:
            stmt = Jimple.v().newGotoStmt(new FutureStmt());
             break;

         case ByteCode.GOTO_W:
            stmt = Jimple.v().newGotoStmt(new FutureStmt());
            break;
/*
         case ByteCode.JSR:
         case ByteCode.JSR_W:
         {
             stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), Jimple.v().newNextNextStmtRef());

             statements.add(stmt);

             stmt = Jimple.v().newGotoStmt(new FutureStmt());
             statements.add(stmt);

             stmt = null;
             break;
         }
*/

         case ByteCode.RET:
         {
            Local local =
                Util.getLocalForIndex(listBody, ((Instruction_Ret)ins).arg_b);

            stmt = Jimple.v().newRetStmt(local);
            break;
         }

         case ByteCode.RET_W:
         {
            Local local =
                Util.getLocalForIndex(listBody, ((Instruction_Ret_w)ins).arg_i);


            stmt = Jimple.v().newRetStmt(local);
            break;
         }

         case ByteCode.RETURN:
            stmt = Jimple.v().newReturnVoidStmt();
            break;

         case ByteCode.LRETURN:
         case ByteCode.DRETURN:
         case ByteCode.IRETURN:
         case ByteCode.FRETURN:
         case ByteCode.ARETURN:
            stmt = Jimple.v().newReturnStmt(Util.getLocalForStackOp(listBody,
                typeStack, typeStack.topIndex()));
            break;

         case ByteCode.BREAKPOINT:
            stmt = Jimple.v().newBreakpointStmt();
            break;

         case ByteCode.TABLESWITCH:
         {
            int lowIndex = ((Instruction_Tableswitch)ins).low,
                highIndex = ((Instruction_Tableswitch)ins).high;

            stmt = Jimple.v().newTableSwitchStmt(
                    Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()),
                    lowIndex,
                    highIndex,
                    Arrays.asList(new FutureStmt[highIndex - lowIndex + 1]),
                    new FutureStmt());
            break;
         }

         case ByteCode.LOOKUPSWITCH:
         {
            List matches = new ArrayList();
            int npairs = ((Instruction_Lookupswitch)ins).npairs;

            for (int j = 0; j < npairs; j++)
                matches.add(new Integer( ((Instruction_Lookupswitch)ins).match_offsets[j*2]));

            stmt = Jimple.v().newLookupSwitchStmt(
                Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex()),
                matches,
                Arrays.asList(new FutureStmt[npairs]),
                new FutureStmt());
            break;
         }

         case ByteCode.PUTFIELD:
         {
            CONSTANT_Fieldref_info fieldInfo =
                   (CONSTANT_Fieldref_info) constant_pool[((Instruction_Putfield)ins).arg_i];

            CONSTANT_Class_info c =
                (CONSTANT_Class_info) constant_pool[fieldInfo.class_index];

            String className = ((CONSTANT_Utf8_info) (constant_pool[c.name_index])).convert();
            className = className.replace('/', '.');

            CONSTANT_NameAndType_info i =
                (CONSTANT_NameAndType_info) constant_pool[fieldInfo.name_and_type_index];

            String fieldName = ((CONSTANT_Utf8_info) (constant_pool[i.name_index])).convert();
            String fieldDescriptor = ((CONSTANT_Utf8_info) (constant_pool[i.descriptor_index])).
                    convert();

            Type fieldType = Util.jimpleTypeOfFieldDescriptor(cm, fieldDescriptor);
                
            SootClass bclass = cm.getSootClass(className);

            SootField field = bclass.getField(fieldName, fieldType);

            InstanceFieldRef fr =
                Jimple.v().newInstanceFieldRef(Util.getLocalForStackOp(listBody,
                typeStack, typeStack.topIndex() - typeSize(typeStack.top())), field);

            rvalue = Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex());
            stmt = Jimple.v().newAssignStmt(fr,rvalue);
            break;
         }

         case ByteCode.GETFIELD:
         {
            InstanceFieldRef fr = null;

            CONSTANT_Fieldref_info fieldInfo =
                (CONSTANT_Fieldref_info) constant_pool[((Instruction_Getfield)ins).arg_i];

            CONSTANT_Class_info c =
                (CONSTANT_Class_info) constant_pool[fieldInfo.class_index];

            String className = ((CONSTANT_Utf8_info) (constant_pool[c.name_index])).convert();
            className = className.replace('/', '.');

            CONSTANT_NameAndType_info i =
                (CONSTANT_NameAndType_info) constant_pool[fieldInfo.name_and_type_index];

            String fieldName = ((CONSTANT_Utf8_info) (constant_pool[i.name_index])).convert();
            String fieldDescriptor = ((CONSTANT_Utf8_info) (constant_pool[i.descriptor_index])).
                convert();

            SootClass bclass = cm.getSootClass(className);

            
            Type fieldType = Util.jimpleTypeOfFieldDescriptor(cm, fieldDescriptor);
            SootField field = bclass.getField(fieldName, fieldType);

            fr = Jimple.v().newInstanceFieldRef(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), field);

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), fr);
            break;
         }


         case ByteCode.PUTSTATIC:
         {
            StaticFieldRef fr = null;

            CONSTANT_Fieldref_info fieldInfo =
                (CONSTANT_Fieldref_info) constant_pool[((Instruction_Putstatic)ins).arg_i];

            CONSTANT_Class_info c =
                (CONSTANT_Class_info) constant_pool[fieldInfo.class_index];

             String className = ((CONSTANT_Utf8_info) (constant_pool[c.name_index])).convert();
            className = className.replace('/', '.');

            CONSTANT_NameAndType_info i =
                (CONSTANT_NameAndType_info) constant_pool[fieldInfo.name_and_type_index];

            String fieldName = ((CONSTANT_Utf8_info) (constant_pool[i.name_index])).convert();
            String fieldDescriptor = ((CONSTANT_Utf8_info) (constant_pool[i.descriptor_index])).
                convert();

            Type fieldType = Util.jimpleTypeOfFieldDescriptor(cm, fieldDescriptor);
            
            SootClass bclass = cm.getSootClass(className);
            SootField field = bclass.getField(fieldName, fieldType);

            fr = Jimple.v().newStaticFieldRef(field);

            stmt = Jimple.v().newAssignStmt(fr, Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));
            break;
         }

         case ByteCode.GETSTATIC:
         {
            StaticFieldRef fr = null;

            CONSTANT_Fieldref_info fieldInfo =
                (CONSTANT_Fieldref_info) constant_pool[((Instruction_Getstatic)ins).arg_i];

            CONSTANT_Class_info c =
                (CONSTANT_Class_info) constant_pool[fieldInfo.class_index];

            String className = ((CONSTANT_Utf8_info) (constant_pool[c.name_index])).convert();
            className = className.replace('/', '.');

            CONSTANT_NameAndType_info i =
                (CONSTANT_NameAndType_info) constant_pool[fieldInfo.name_and_type_index];

            String fieldName = ((CONSTANT_Utf8_info) (constant_pool[i.name_index])).convert();
            String fieldDescriptor = ((CONSTANT_Utf8_info) (constant_pool[i.descriptor_index])).
                convert();

            Type fieldType = Util.jimpleTypeOfFieldDescriptor(cm, fieldDescriptor);
            
            SootClass bclass = cm.getSootClass(className);
            SootField field = bclass.getField(fieldName, fieldType);

            fr = Jimple.v().newStaticFieldRef(field);

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), fr);
            break;
         }


         case ByteCode.INVOKEVIRTUAL:
         {
            Instruction_Invokevirtual iv = (Instruction_Invokevirtual)ins;
            args = cp_info.countParams(constant_pool,iv.arg_i);

            SootMethod method = null;

            CONSTANT_Methodref_info methodInfo =
                (CONSTANT_Methodref_info) constant_pool[iv.arg_i];

            CONSTANT_Class_info c =
                (CONSTANT_Class_info) constant_pool[methodInfo.class_index];

             String className = ((CONSTANT_Utf8_info) (constant_pool[c.name_index])).convert();
                className = className.replace('/', '.');

            CONSTANT_NameAndType_info i =
                (CONSTANT_NameAndType_info) constant_pool[methodInfo.name_and_type_index];

            String methodName = ((CONSTANT_Utf8_info) (constant_pool[i.name_index])).convert();
            String methodDescriptor = ((CONSTANT_Utf8_info) (constant_pool[i.descriptor_index])).
                convert();

            SootClass bclass = cm.getSootClass(className);

            Local[] parameters;
            List parameterTypes;
            Type returnType;

            // Generate parameters & returnType & parameterTypes
            {
                Type[] types = Util.jimpleTypesOfFieldOrMethodDescriptor(cm,
                    methodDescriptor);

                parameterTypes = new ArrayList();

                for(int k = 0; k < types.length - 1; k++)
                {
                    parameterTypes.add(types[k]);
                }

                returnType = types[types.length - 1];
            }

            method = bclass.getMethod(methodName, parameterTypes, returnType);

            // build array of parameters
                params = new Value[args];
                for (int j=args-1;j>=0;j--)
                {
                   params[j] = Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex());

                   if(typeSize(typeStack.top()) == 2)
                   {
                      typeStack = typeStack.pop();
                      typeStack = typeStack.pop();
                   }
                   else
                      typeStack = typeStack.pop();
                }

            rvalue = Jimple.v().newVirtualInvokeExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), method, Arrays.asList(params));

            if(!returnType.equals(VoidType.v()))
            {
                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex()),rvalue);
            }
            else
               stmt = Jimple.v().newInvokeStmt((InvokeExpr) rvalue);
            break;
        }

        case ByteCode.INVOKENONVIRTUAL:
         {
            Instruction_Invokenonvirtual iv = (Instruction_Invokenonvirtual)ins;
            args = cp_info.countParams(constant_pool,iv.arg_i);

            SootMethod method = null;

                CONSTANT_Methodref_info methodInfo =
                    (CONSTANT_Methodref_info) constant_pool[iv.arg_i];

                CONSTANT_Class_info c =
                    (CONSTANT_Class_info) constant_pool[methodInfo.class_index];

                String className = ((CONSTANT_Utf8_info) (constant_pool[c.name_index])).convert();
                className = className.replace('/', '.');

                CONSTANT_NameAndType_info i =
                    (CONSTANT_NameAndType_info) constant_pool[methodInfo.name_and_type_index];

                String methodName = ((CONSTANT_Utf8_info) (constant_pool[i.name_index])).convert();
                String methodDescriptor = ((CONSTANT_Utf8_info) (constant_pool[i.descriptor_index])).
                    convert();

                SootClass bclass = cm.getSootClass(className);

                Local[] parameters;
                List parameterTypes;
                Type returnType;

                // Generate parameters & returnType & parameterTypes
                {
                    Type[] types = Util.jimpleTypesOfFieldOrMethodDescriptor(cm,
                        methodDescriptor);

                    parameterTypes = new ArrayList();

                    for(int k = 0; k < types.length - 1; k++)
                    {
                        parameterTypes.add(types[k]);
                    }

                    returnType = types[types.length - 1];
                }

                method = bclass.getMethod(methodName, parameterTypes, returnType);

            // build array of parameters
                params = new Value[args];
                for (int j=args-1;j>=0;j--)
                {
                   params[j] = Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex());

                   if(typeSize(typeStack.top()) == 2)
                   {
                      typeStack = typeStack.pop();
                      typeStack = typeStack.pop();
                   }
                   else
                      typeStack = typeStack.pop();
                }

            rvalue = Jimple.v().newSpecialInvokeExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), method, Arrays.asList(params));

            if(!returnType.equals(VoidType.v()))
            {
                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex()), rvalue);
            }
            else
                stmt = Jimple.v().newInvokeStmt((InvokeExpr) rvalue);
            break;
        }

         case ByteCode.INVOKESTATIC:
         {
            Instruction_Invokestatic is = (Instruction_Invokestatic)ins;
            args = cp_info.countParams(constant_pool,is.arg_i);

            SootMethod method = null;

                CONSTANT_Methodref_info methodInfo =
                    (CONSTANT_Methodref_info) constant_pool[is.arg_i];

                CONSTANT_Class_info c =
                    (CONSTANT_Class_info) constant_pool[methodInfo.class_index];

                String className = ((CONSTANT_Utf8_info) (constant_pool[c.name_index])).convert();
                className = className.replace('/', '.');

                CONSTANT_NameAndType_info i =
                    (CONSTANT_NameAndType_info) constant_pool[methodInfo.name_and_type_index];

                String methodName = ((CONSTANT_Utf8_info) (constant_pool[i.name_index])).convert();
                String methodDescriptor = ((CONSTANT_Utf8_info) (constant_pool[i.descriptor_index])).
                    convert();

                SootClass bclass = cm.getSootClass(className);

                Local[] parameters;
                List parameterTypes;
                Type returnType;

                // Generate parameters & returnType & parameterTypes
                {
                    Type[] types = Util.jimpleTypesOfFieldOrMethodDescriptor(cm,
                        methodDescriptor);

                    parameterTypes = new ArrayList();

                    for(int k = 0; k < types.length - 1; k++)
                    {
                        parameterTypes.add(types[k]);
                    }

                    returnType = types[types.length - 1];
                }

                method = bclass.getMethod(methodName, parameterTypes, returnType);

            // build Vector of parameters
                   params = new Value[args];
                for (int j=args-1;j>=0;j--)
                {
                    /* System.out.println("BeforeTypeStack");
                    typeStack.print(System.out);

                    System.out.println("AfterTypeStack");
                    postTypeStack.print(System.out);
                    */

                   params[j] = Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex());

                   if(typeSize(typeStack.top()) == 2)
                   {
                      typeStack = typeStack.pop();
                      typeStack = typeStack.pop();
                   }
                   else
                      typeStack = typeStack.pop();
                }

            rvalue = Jimple.v().newStaticInvokeExpr(method, Arrays.asList(params));

            if(!returnType.equals(VoidType.v()))
            {
                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex()),rvalue);
            }
            else
               stmt = Jimple.v().newInvokeStmt((InvokeExpr) rvalue);

            break;
         }

         case ByteCode.INVOKEINTERFACE:
         {
            Instruction_Invokeinterface ii = (Instruction_Invokeinterface)ins;
            args = cp_info.countParams(constant_pool,ii.arg_i);

            SootMethod method = null;

                CONSTANT_InterfaceMethodref_info methodInfo =
                    (CONSTANT_InterfaceMethodref_info) constant_pool[ii.arg_i];

                CONSTANT_Class_info c =
                    (CONSTANT_Class_info) constant_pool[methodInfo.class_index];

                String className = ((CONSTANT_Utf8_info) (constant_pool[c.name_index])).convert();
                className = className.replace('/', '.');

                CONSTANT_NameAndType_info i =
                    (CONSTANT_NameAndType_info) constant_pool[methodInfo.name_and_type_index];

                String methodName = ((CONSTANT_Utf8_info) (constant_pool[i.name_index])).convert();
                String methodDescriptor = ((CONSTANT_Utf8_info) (constant_pool[i.descriptor_index])).
                    convert();

                SootClass bclass = cm.getSootClass(className);

                Local[] parameters;
                List parameterTypes;
                Type returnType;

                // Generate parameters & returnType & parameterTypes
                {
                    Type[] types = Util.jimpleTypesOfFieldOrMethodDescriptor(cm,
                        methodDescriptor);

                    parameterTypes = new ArrayList();

                    for(int k = 0; k < types.length - 1; k++)
                    {
                        parameterTypes.add(types[k]);
                    }

                    returnType = types[types.length - 1];
                }

                method = bclass.getMethod(methodName, parameterTypes, returnType);

            // build Vector of parameters
                params = new Value[args];
                for (int j=args-1;j>=0;j--)
                {
                   params[j] = Util.getLocalForStackOp(listBody, typeStack, typeStack.topIndex());

                   if(typeSize(typeStack.top()) == 2)
                   {
                      typeStack = typeStack.pop();
                      typeStack = typeStack.pop();
                   }
                   else
                      typeStack = typeStack.pop();
                }

            rvalue = Jimple.v().newInterfaceInvokeExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), method, Arrays.asList(params));

            if(!returnType.equals(VoidType.v()))
            {
                stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex()), rvalue);
            }
            else
               stmt = Jimple.v().newInvokeStmt((InvokeExpr) rvalue);
            break;
        }

         case ByteCode.ATHROW:
            stmt = Jimple.v().newThrowStmt(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));
            break;

         case ByteCode.NEW:
         {
            SootClass bclass = cm.getSootClass(getClassName(constant_pool,
                ((Instruction_New)ins).arg_i));

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), Jimple.v().newNewExpr(RefType.v(bclass.getName())));
            break;
         }

         case ByteCode.CHECKCAST:
         {
            String className = getClassName(constant_pool, ((Instruction_Checkcast)ins).arg_i);

            Type castType;

            if(className.startsWith("["))
                castType = Util.jimpleTypeOfFieldDescriptor(cm, getClassName(constant_pool,
                    ((Instruction_Checkcast)ins).arg_i));
            else
                castType = RefType.v(className);

            rhs = Jimple.v().newCastExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), castType);

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()),rhs);
            break;
         }

         case ByteCode.INSTANCEOF:
         {
            Type checkType;

            String className = getClassName(constant_pool, ((Instruction_Instanceof)ins).arg_i);

            if(className.startsWith("["))
                checkType = Util.jimpleTypeOfFieldDescriptor(cm, getClassName(constant_pool,
                ((Instruction_Instanceof)ins).arg_i));
            else
                checkType = RefType.v(className);

            rhs = Jimple.v().newInstanceOfExpr(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), checkType);

            stmt = Jimple.v().newAssignStmt(Util.getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()),rhs);
            break;
         }

         case ByteCode.MONITORENTER:
            stmt = Jimple.v().newEnterMonitorStmt(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));
            break;
         case ByteCode.MONITOREXIT:
            stmt = Jimple.v().newExitMonitorStmt(Util.getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));
            break;

         default:
            throw new RuntimeException("Unrecognized bytecode instruction: " + x);
        }

    if(stmt != null)
        statements.add(stmt);
   }

     Type jimpleTypeOfAtype(int atype)
    {
        switch(atype)
        {
            case 4:
                return BooleanType.v();

            case 5:
                return CharType.v();

            case 6:
                return FloatType.v();

            case 7:
                return DoubleType.v();

            case 8:
                return ByteType.v();

            case 9:
                return ShortType.v();

            case 10:
                return IntType.v();

            case 11:
                return LongType.v();

            default:
                throw new RuntimeException("Undefined 'atype' in NEWARRAY byte instruction");
        }
   }

   int typeSize(Type type)
   {
        if(type.equals(LongType.v()) || type.equals(DoubleType.v()) ||
            type.equals(Long2ndHalfType.v()) || type.equals(Double2ndHalfType.v()))
        {
            return 2;
        }
        else
            return 1;
   }
}

class OutFlow
{
    TypeStack typeStack;

    OutFlow(TypeStack typeStack)
    {
        this.typeStack = typeStack;
    }
}
