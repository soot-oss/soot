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

import java.lang.*;
import java.util.*;

import soot.*;
import soot.jimple.*;
import soot.baf.*;
import soot.util.*;

/** A Control Flow Graph.
 * @author Clark Verbrugge
 */
public class CFG {

    /** Method for which this is a control flow graph.
     * @see method_info
     */
    private method_info method;
    /** Ordered list of BasicBlocks comprising the code of this CFG.
     */
    BasicBlock cfg;

    Chain units;
    JimpleBody listBody;

    Map instructionToFirstStmt;
    Map instructionToLastStmt;
    SootMethod jmethod;
    Scene cm;

    Instruction firstInstruction;
    Instruction lastInstruction;

    private short wide;                 // convert indices when parsing jimple

    private Instruction sentinel;
    private Hashtable h2bb, t2bb;
    private int bbcount;        // statistics, number of BBs processed

    /** Constructs a new control flow graph for the given method.
     * @param m the method in question.
     * @see method_info
     */
    public CFG(method_info m) 
    {
	this.method = m;

	this.sentinel = new Instruction_Nop();
	this.sentinel.next = m.instructions;
	m.instructions.prev = this.sentinel;

	eliminateJsrRets();
	
	buildBBCFG();

	//	printBBCFGSucc();
	//	printBBs();

	cfg.beginCode = true;

	m.cfg = this;

	if(cfg != null)
	    firstInstruction = cfg.head;
	else
	    firstInstruction = null;
    }

    private void printBBCFGSucc()
    {
	BasicBlock b = this.cfg;
	while ( b!= null )
	{
	    System.out.print(b.id +" -> ");
	    for (int i=0; i<b.succ.size(); i++)
	    {
		BasicBlock bs = (BasicBlock)b.succ.elementAt(i);
		System.out.print(bs.id+" ");
	    }
	    System.out.println();
	    b = b.next;
	}
    }

    private void printBBCFGPred()
    {
	BasicBlock b = this.cfg;
	while ( b!= null )
	{
	    System.out.print(b.id +" <- ");
	    for (int i=0; i<b.pred.size(); i++)
	    {
		BasicBlock bs = (BasicBlock)b.pred.elementAt(i);
		System.out.print(bs.id+" ");
	    }
	    System.out.println();
	    b = b.next;
	}
    }

    private void printOneBasicBlock(BasicBlock b)
    {
	System.out.println("Block "+b.id);
	
	Instruction insn = b.head;
	System.out.println(insn);
	while (insn != b.tail && insn != null)
	{
	    insn = insn.next;
	    System.out.println(insn);
	}

	System.out.println();	
    }

    private void printBBHeadTail(BasicBlock fb)
    {
	BasicBlock b = fb;
	while (b != null)
	{
	    System.out.println(b.head);
	    System.out.println(b.tail+"\n");
	    b = b.next;
	}	
    }

    private void printBBs()
    {
	BasicBlock bb = this.cfg;
	while (bb != null)
	{
	    printOneBasicBlock(bb);
	    bb = bb.next;
	}
    }

    private void printInstructions()
    {
	Instruction insn = method.instructions;
	while (insn != null)
	{
	    System.out.println(insn + "\t <- "+ insn.prev + "\t -> "+insn.next);
	    insn = insn.next;
	}
    }

    private void printExceptionTable()
    {
	Code_attribute ca = this.method.locate_code_attribute();
	
	System.out.println("\nException table :");
	for (int i=0; i<ca.exception_table.length; i++)
	{
	    exception_table_entry ete = ca.exception_table[i];
	    System.out.println(ete.start_inst + " \t " + ete.end_inst + " \t "+ete.handler_inst);
	}
    }
    // Constructs the actual control flow graph. Assumes the hash table
    // currently associates leaders with BasicBlocks, this function
    // builds the next[] and prev[] pointer arrays.
    private void buildBBCFG() 
    {
	Object branches[], nextinsn;
	Code_attribute ca = method.locate_code_attribute();

	{
	    h2bb = new Hashtable(100,25);
	    t2bb = new Hashtable(100,25);

	    Instruction insn = this.sentinel.next;
	    BasicBlock blast = null;
	    if (insn != null)
	    {
		Instruction tail = buildBasicBlock(insn);
		cfg = new BasicBlock(insn, tail);
		h2bb.put(insn, cfg);
		t2bb.put(tail, cfg);
		insn = tail.next;
		blast = cfg;
	    }	

	    while (insn != null)
	    {
		Instruction tail = buildBasicBlock(insn);
		BasicBlock block = new BasicBlock(insn, tail);
		blast.next = block;
		blast = block;
		h2bb.put(insn, block);
		t2bb.put(tail, block);
		insn = tail.next;
	    }
	}

	BasicBlock block = cfg;

	while (block != null) 
	{
	    Instruction insn = block.tail;

	    if (insn.branches) 
	    {
		if (insn instanceof Instruction_Athrow)
		{
		    // see how many targets it can reach.  Note that this is a
		    // subset of the exception_table.
		    HashSet ethandlers = new HashSet();

		    // not quite a subset---could also be that control 
		    // exits this method, so start icount at 1
		    for (int i=0; i<ca.exception_table_length; i++) 
		    {
			exception_table_entry etentry =
			    ca.exception_table[i];

			if (insn.label >= etentry.start_inst.label 
			    && (etentry.end_inst==null 
				|| insn.label < etentry.end_inst.label)) 
			{
			    ethandlers.add(etentry.handler_inst);
			}
		    }

		    branches = ethandlers.toArray();
		} 
		else
		{
		    branches = insn.branchpoints(insn.next);              
		}

		if (branches != null)
		{
		    block.succ.ensureCapacity(block.succ.size()+branches.length);

		    for (int i=0; i<branches.length; i++) 
		    {
			if ( branches[i]!=null ) {
			    BasicBlock bb = (BasicBlock)h2bb.get(branches[i]);
                 
			    if (bb == null)
			    {                 
				System.out.println("Warning: "
					       +"target of a branch is null");
				System.out.println ( insn );
			    }
			    else 
			    {
				block.succ.addElement(bb);
				bb.pred.addElement(block);
			    }
			}
		    }
		}
	    } 
	    else 
	    if (block.next!=null) 
	    { // BB ended not with a branch, so just go to next
		block.succ.addElement(block.next);
		block.next.pred.addElement(block);
	    }
	    block = block.next;
	}

	// One final step, run through exception handlers and mark which
	// basic blocks begin their code
	for (int i=0; i<ca.exception_table_length; i++) 
	{
	    BasicBlock bb = (BasicBlock)h2bb.get(
					 ca.exception_table[i].handler_inst);
	    if ( bb == null )
	    {
		System.out.println("Warning: No basic block found for" +
				   " start of exception handler code.");
	    }
	    else 
	    {
		bb.beginException = true;
		ca.exception_table[i].b = bb;
	    }
	}
    }

    /* given the list of instructions head, this pulls off the front
     * basic block, terminates it with a null, and returns the next
     * instruction after.
     */
    private static Instruction buildBasicBlock(Instruction head) 
    {
	Instruction insn, next;
	insn = head;
	next = insn.next;

	if (next == null)
	    return insn;

	do 
	{	    
	    if (insn.branches || next.labelled)
		break;
	    else
	    {
		insn = next;
		next = insn.next;
	    }
	} while (next != null);

	return insn;
    }

    /* get a set of reachable instructions from an astore to matching ret. 
     * it does not consider the exception handler as reachable now.
     */     
    private Set getReachableInsns(Instruction from, Instruction to)
    {
	Code_attribute codeAttribute = method.locate_code_attribute();

	/* find all reachable blocks. */
	Set reachableinsns = new HashSet();
	LinkedList tovisit = new LinkedList();

	reachableinsns.add(from);
	tovisit.add(from);
	
	while (!tovisit.isEmpty())
	{
	    Instruction insn = (Instruction)tovisit.removeFirst();

	    if (insn == to)
		continue;

	    Instruction[] bps = null;
	    if (insn.branches)
	    {
		bps = insn.branchpoints(insn.next);	
	    }
	    else
	    {
		bps = new Instruction[1];
		bps[0] = insn.next;
	    }

	    if (bps != null)
	    {
		for (int i=0; i<bps.length; i++)
		{
		    Instruction bp = bps[i];

		    if (bp != null
			&& !reachableinsns.contains(bp))
		    {
			reachableinsns.add(bp);
			tovisit.add(bp);
		    }
		}
	    }	    
	}

	return reachableinsns;
    }

    /* We only handle simple cases. */
    Map jsr2astore = new HashMap();
    Map astore2ret = new HashMap();
    
    LinkedList jsrorder = new LinkedList();

    /* Eliminate subroutines ( JSR/RET instructions ) by inlining the routine bodies. 
     */
    private boolean eliminateJsrRets()
    {
	boolean unusual = false;

	// go through instructions, find all jsr/astore/ret pair.
	Instruction insn = this.sentinel;

	do 
	{
	    insn = insn.next;

	    if (insn instanceof Instruction_Jsr
		|| insn instanceof Instruction_Jsr_w)
	    {
		Instruction astore = ((Instruction_branch)insn).target;
		if (! (astore instanceof Interface_Astore))
		{
		    unusual = true;
		    break;
		}
		
		if (!jsrorder.contains(insn))
		    jsrorder.addFirst(insn);

		Instruction ret = findMatchingRet(astore, insn);
		if (ret == null)
		{
		    unusual = true;
		    break;
		}
		
		jsr2astore.put(insn, astore);
		astore2ret.put(astore, ret);
	    }
	} while (insn.next != null);
	
	this.lastInstruction = insn;

	if (unusual)
	{
	    System.err.println("Sorry, I cannot handle this method.");
	    return false;
	}

	inliningJsrTargets ();

	/* patch exception table and others.*/
	{
	    method.instructions = this.sentinel.next;

	    adjustExceptionTable();

	    adjustBranchTargets();
	}
	return true;
    }

    private Instruction findMatchingRet(Instruction astore, Instruction jsr)
    {
	int astorenum = ((Interface_Astore)astore).getLocalNumber();
	
	Instruction insn = astore.next;
	while (insn != null)
	{
	    if (insn instanceof Instruction_Ret
		|| insn instanceof Instruction_Ret_w)
	    {
		int retnum = ((Interface_OneIntArg)insn).getIntArg();
		if (astorenum == retnum)
		    return insn;
	    }
	    else
	    /* adjust the jsr inlining order. */
	    if (insn instanceof Instruction_Jsr
		|| insn instanceof Instruction_Jsr_w)
	    {
		if (!jsrorder.contains(insn))
		    jsrorder.addFirst(insn);
		else
		{
		    int jindex = jsrorder.indexOf(jsr);
		    int cindex = jsrorder.indexOf(insn);
		    if (jindex < cindex)
		    {
			jsrorder.remove(jsr);
			// because of removal of jsr, correct index should be cindex, not cindex-1
			jsrorder.add(cindex, jsr);
		    }
		}
	    }

	    insn = insn.next;
	}

	return null;
    }

    private void inliningJsrTargets()
    {
	while (!jsrorder.isEmpty())
	{
	    Instruction jsr = (Instruction)jsrorder.removeFirst();
	    
	    Instruction astore = (Instruction)jsr2astore.get(jsr);
	    Instruction ret = (Instruction)astore2ret.get(astore);

	    // make a copy of the code, append to the last instruction.     
	    Instruction newhead = makeCopyOf(astore, ret, jsr.next);	

	    // jsr is replaced by goto newhead
	    // astore has been removed
	    // ret is replaced by goto jsr.next
	    Instruction_Goto togo = new Instruction_Goto();
	    togo.target = newhead;
	    newhead.labelled = true;
	    togo.label = jsr.label;
	    togo.labelled = jsr.labelled;
	    togo.prev = jsr.prev;
	    togo.next = jsr.next;
	    togo.prev.next = togo;
	    togo.next.prev = togo;

	    replacedInsns.put(jsr, togo); 
	}
    }

    /* make a copy of code between from and to exclusively, 
     * fixup targets of branch instructions in the code.
     */
    private Instruction makeCopyOf(Instruction astore,
				   Instruction ret,
				   Instruction target)
    {
	Instruction last = this.lastInstruction;
	Instruction headbefore = last;

	int curlabel = this.lastInstruction.label;

	HashMap insnmap = new HashMap(); // mapping from original instructions to new instructions.
	Instruction insn = astore.next;
	
	while (insn != ret && insn != null)
	{
	    try {
		Instruction newone = (Instruction)insn.clone();

		newone.label = ++curlabel;
		newone.prev = last;
		last.next = newone;
		last = newone;

		insnmap.put(insn, newone);
	    } catch (CloneNotSupportedException e)
	    {
		System.out.println("Error !");
	    }
	    insn = insn.next;   
	}

	// replace ret by a goto
	Instruction_Goto togo = new Instruction_Goto();
	togo.target = target;
	target.labelled = true;
	togo.label = ++curlabel;
	last.next = togo;
	togo.prev = last;
	last = togo;

	this.lastInstruction = last;

	// The ret instruction is removed, 
	insnmap.put(astore, headbefore.next);
	insnmap.put(ret, togo);

	// fixup targets in new instruction (only in the scope of new instructions).
	// do not forget set target labelled as TRUE
	insn = headbefore.next;
	while (insn != last)
	{
	    if (insn instanceof Instruction_branch)
	    {
		Instruction oldtgt = ((Instruction_branch)insn).target;
		Instruction newtgt = (Instruction)insnmap.get(oldtgt);
		if (newtgt != null)
		{
		    ((Instruction_branch)insn).target = newtgt;
		    newtgt.labelled = true;
		}
	    }
	    else
	    if (insn instanceof Instruction_Lookupswitch)
	    {
		Instruction_Lookupswitch switchinsn = 
		    (Instruction_Lookupswitch)insn;
		
		Instruction newdefault = (Instruction)insnmap.get(switchinsn.default_inst);
		if (newdefault != null)
		{
		    switchinsn.default_inst = newdefault;
		    newdefault.labelled = true;
		}

		for (int i=0; i<switchinsn.match_insts.length; i++)
		{
		    Instruction newtgt = (Instruction)insnmap.get(switchinsn.match_insts[i]);
		    if (newtgt != null)
		    {
			switchinsn.match_insts[i] = newtgt;
			newtgt.labelled = true;
		    }
		}
	    }
	    else
	    if (insn instanceof Instruction_Tableswitch)
	    {
		Instruction_Tableswitch switchinsn = 
		    (Instruction_Tableswitch)insn;
		
		Instruction newdefault = (Instruction)insnmap.get(switchinsn.default_inst);
		if (newdefault != null)
		{
		    switchinsn.default_inst = newdefault;
		    newdefault.labelled = true;
		}

		for (int i=0; i<switchinsn.jump_insts.length; i++)
		{
		    Instruction newtgt = (Instruction)insnmap.get(switchinsn.jump_insts[i]);
		    if (newtgt != null)
		    {
			switchinsn.jump_insts[i] = newtgt;
			newtgt.labelled = true;
		    }
		}
	    }

	    insn = insn.next;
	}
	
	// do we need to copy a new exception table entry? 
	// new exception table has new exception range, but use old handler
	{
	    Code_attribute ca = method.locate_code_attribute();

	    LinkedList newentries = new LinkedList();

	    for (int i=0; i<ca.exception_table_length; i++) 
	    {
		exception_table_entry etentry =
		    ca.exception_table[i];

		if ( insnmap.containsKey(etentry.start_inst))
		{
		    exception_table_entry newone 
			= new exception_table_entry();
		    newone.start_inst = (Instruction)insnmap.get(etentry.start_inst);
       		    if (etentry.end_inst == null)
			newone.end_inst = null;
		    else
			newone.end_inst = (Instruction)insnmap.get(etentry.end_inst);

		    newone.handler_inst = (Instruction)insnmap.get(etentry.handler_inst);
		    if (newone.handler_inst == null)
			newone.handler_inst = etentry.handler_inst;

		    newentries.add(newone);
		}
	    }

	    if (newentries.size() > 0)
	    {
		ca.exception_table_length += newentries.size();
		exception_table_entry[] newtable = new exception_table_entry[ca.exception_table_length];
		System.arraycopy(ca.exception_table, 0, newtable, 0, ca.exception_table.length);
		for (int i=0, j=ca.exception_table.length; i<newentries.size(); i++, j++)
		{
		    newtable[j] = (exception_table_entry)newentries.get(i);
		}
		
		ca.exception_table = newtable;
	    }
	}
	
	return headbefore.next;
    }


    /* if a jsr/astore/ret is replaced by some other instruction, it will be put on this table. */
    private Hashtable replacedInsns = new Hashtable();
    private void dumpReplacedInsns()
    {
	System.out.println("replaced table:");
	Set keys = replacedInsns.keySet();
	Iterator keyIt = keys.iterator();
	while (keyIt.hasNext())
	{
	    Object key = keyIt.next();
	    Object value = replacedInsns.get(key);
	    System.out.println(key + " ==> "+ value);
	}
    }

    /* do not forget set the target labelled as TRUE.*/
    private void adjustBranchTargets()
    {
	Instruction insn = this.sentinel.next;
	while (insn != null)
	{
	    if (insn instanceof Instruction_branch)
	    {
		Instruction_branch binsn = (Instruction_branch)insn;
		Instruction newtgt = (Instruction)replacedInsns.get(binsn.target);
		if (newtgt != null)
		{
		    binsn.target = newtgt;
		    newtgt.labelled = true;
		}
	    }
	    else
	    if (insn instanceof Instruction_Lookupswitch)
	    {
		Instruction_Lookupswitch switchinsn = 
		    (Instruction_Lookupswitch)insn;
		
		Instruction newdefault = (Instruction)replacedInsns.get(switchinsn.default_inst);
		if (newdefault != null)
		{
		    switchinsn.default_inst = newdefault;
		    newdefault.labelled = true;
		}

		for (int i=0; i<switchinsn.match_insts.length; i++)
		{
		    Instruction newtgt = (Instruction)replacedInsns.get(switchinsn.match_insts[i]);
		    if (newtgt != null)
		    {
			switchinsn.match_insts[i] = newtgt;
			newtgt.labelled = true;
		    }
		}
	    }
	    else
	    if (insn instanceof Instruction_Tableswitch)
	    {
		Instruction_Tableswitch switchinsn = 
		    (Instruction_Tableswitch)insn;
		
		Instruction newdefault = (Instruction)replacedInsns.get(switchinsn.default_inst);
		if (newdefault != null)
		{
		    switchinsn.default_inst = newdefault;
		    newdefault.labelled = true;
		}

		for (int i=0; i<switchinsn.jump_insts.length; i++)
		{
		    Instruction newtgt = (Instruction)replacedInsns.get(switchinsn.jump_insts[i]);
		    if (newtgt != null)
		    {
			switchinsn.jump_insts[i] = newtgt;
			newtgt.labelled = true;
		    }
		}
	    }
	    
	    insn = insn.next;
	}
    }


    private void adjustExceptionTable() 
    {
	Code_attribute codeAttribute = method.locate_code_attribute();

	for(int i = 0; i < codeAttribute.exception_table_length; i++)
	{
	    exception_table_entry entry = codeAttribute.exception_table[i];

	    Instruction oldinsn = entry.start_inst;
	    Instruction newinsn = (Instruction)replacedInsns.get(oldinsn);
	    if (newinsn != null) 
		entry.start_inst = newinsn;

	    oldinsn = entry.end_inst;
	    if (entry.end_inst != null)
	    {
		newinsn = (Instruction)replacedInsns.get(oldinsn);	    
		if (newinsn != null)
		    entry.end_inst = newinsn;
	    }

	    oldinsn = entry.handler_inst;
	    newinsn = (Instruction)replacedInsns.get(oldinsn);
	    if (newinsn != null)
		entry.handler_inst = newinsn;
	}
   }

   /** Reconstructs the instruction stream by appending the Instruction
    * lists associated with each basic block.
    * <p>
    * Note that this joins up the basic block Instruction lists, and so
    * they will no longer end with <i>null</i> after this.
    * @return the head of the list of instructions.
    */
    public Instruction reconstructInstructions() 
    {
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

    private void buildInsnCFGfromBBCFG()
    {
	BasicBlock block = cfg;

	while(block != null)
	{
	    Instruction insn = block.head;
	    while (insn != block.tail)
	    {		
		Instruction[] succs = new Instruction[1];
		succs[0] = insn.next;
		insn.succs = succs;
		insn = insn.next;
	    }   
 
	    {
		// The successors are the ones from the basic block.
		Vector bsucc = block.succ;
		int size = bsucc.size();
		Instruction[] succs = new Instruction[size];

		for(int i = 0; i<size; i++)
		    succs[i] = ((BasicBlock)bsucc.elementAt(i)).head;		    
		insn.succs = succs;			      
	    } 

	    block = block.next;
	}	
    }

    private void printInsnCFG()
    {
	Instruction insn = cfg.head;
	while (insn != null)
	{
	    System.out.println(insn + " --> " + makeString(insn.succs));
	    insn = insn.next;
	}
    }

    private String makeString(Object[] objs)
    {
	String buf = "";
	for (int i=0; i<objs.length; i++)
	    buf += " , "+objs[i];

	return buf;
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
        Code_attribute codeAttribute = method.locate_code_attribute();
        Set handlerInstructions = new ArraySet();

        Map handlerInstructionToException = new HashMap();
        Map instructionToTypeStack;
        Map instructionToPostTypeStack;

        {
            // build graph in 
	    buildInsnCFGfromBBCFG();

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
                        Instruction[] succs = ins.succs;
			Instruction[] newsuccs = new Instruction[succs.length+1];

			System.arraycopy(succs, 0, newsuccs, 0, succs.length);

			newsuccs[succs.length] = handlerIns;
       			ins.succs = newsuccs;

                        ins = ins.next;
                        if (ins == endIns || ins == null)                         
                            break;
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
            
            while( !instructionsToVisit.isEmpty())
            {
                Instruction ins = (Instruction) instructionsToVisit.removeFirst();

		Instruction[] succs = ins.succs;
	       
		for (int i=0; i<succs.length; i++)
		{
		    Instruction succ = succs[i];
                    
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
            }

            {
                while(!changedInstructions.isEmpty())
                {
                    Instruction ins = (Instruction) changedInstructions.get(0);

                    changedInstructions.remove(0);

                    OutFlow ret = processFlow(ins, (TypeStack) instructionToTypeStack.get(ins),
                        constant_pool);

                    instructionToPostTypeStack.put(ins, ret.typeStack);

                    Instruction[] successors = ins.succs;

                    for(int i = 0; i < successors.length; i++)
                    {
                        Instruction s = successors[i];

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
							{
								try {
                                	newTypeStack = ret.typeStack.merge(oldTypeStack);
								} catch (RuntimeException re)
								{
									System.out.println("Considering "+s);
									throw re;
								}
							}
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

                ins = ins.next;
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

		for (;;)
		{
                    List statementsForIns = new ArrayList();

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

		    if (ins == b.tail)
			break;

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
	    } catch(NoSuchElementException e) 
	    { break; }

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
		    /*
                    if((BasicBlock)(b.succ.firstElement())==b.next)
                        ((GotoStmt)s).setTarget(((BasicBlock) b.succ.elementAt(1)).getHeadJStmt());
                    else
                        ((GotoStmt)s).setTarget(((BasicBlock) b.succ.firstElement()).getHeadJStmt());	
		    */
		    System.err.println(b.head +" has "+b.succ.size()+" successors.");
		    for (int i=0; i<b.statements.size(); i++)
			System.out.println(b.statements.get(i));
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
    void jimpleTargetFixup() 
    {
	BasicBlock b;
	BBQ bbq = new BBQ();

	Code_attribute c = method.locate_code_attribute();
	if (c==null) 
	    return;

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
	if (bbq.isEmpty()) 
	{
	    int i;
	    for (i=0;i<c.exception_table_length;i++) 
	    {
		b = c.exception_table[i].b;
		// if block hasn't yet been processed...
		if (b!=null && b.done) 
		{
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
                matches.add(IntConstant.v( ((Instruction_Lookupswitch)ins).match_offsets[j*2]));

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
        if (type.equals(LongType.v()) || type.equals(DoubleType.v()) ||
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
