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
import soot.options.*;

import java.util.*;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import soot.tagkit.*;

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

    Map<Instruction, Stmt> instructionToFirstStmt;
    Map<Instruction, Stmt> instructionToLastStmt;
    SootMethod jmethod;
    Scene cm;

    Instruction firstInstruction;
    Instruction lastInstruction;

    private Instruction sentinel;
    private Hashtable<Instruction, BasicBlock> h2bb, t2bb;
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

	//	printInstructions();
	//	printExceptionTable();

    eliminateJsrRets();
    
	//	printInstructions();
	//	printExceptionTable();

	buildBBCFG();

	// printBBs();
	// printBBCFGSucc();

	cfg.beginCode = true;

	m.cfg = this;

	if(cfg != null)
	    firstInstruction = cfg.head;
	else
	    firstInstruction = null;

	// calculate complexity metrics
    if (soot.jbco.Main.metrics) complexity();
    
    /*	
	if (m.code_attr != null)
	{
	    for (int i=0; i<m.code_attr.attributes.length; i++)
	    {
		if (m.code_attr.attributes[i] 
		    instanceof LineNumberTable_attribute)
		{
		    G.v().out.print(m.code_attr.attributes[i]);
		}
	    }
	}
	*/
    }

    public static HashMap<SootMethod, int[]> methodsToVEM = new HashMap<SootMethod, int[]>();
    private void complexity() 
    {
      // ignore all non-app classes
      if (!method.jmethod.getDeclaringClass().isApplicationClass()) return;
      
      BasicBlock b = this.cfg;
      HashMap<BasicBlock, Integer> block2exc = new HashMap<BasicBlock, Integer>();
      int tmp, nodes = 0, edges = 0, highest = 0;
      
      while (b != null) {
        tmp = 0;
        for (exception_table_entry element : method.code_attr.exception_table) {
          Instruction start = element.start_inst;
          Instruction end = element.start_inst;
          if ((start.label >= b.head.label && start.label <= b.tail.label) ||
                (end.label > b.head.label && (b.tail.next == null || end.label <= b.tail.next.label)))
            tmp++;
        }
        block2exc.put(b, new Integer(tmp));
        b = b.next;
      }
      
      b = this.cfg;
      while ( b!= null )
      {
        nodes++;
        tmp = b.succ.size() + block2exc.get(b).intValue();
        
        // exceptions are not counted in succs and preds so we need to do so manually
        int deg = b.pred.size() + tmp + (b.beginException ? 1 : 0);
        if (deg > highest)
          highest = deg;
        edges += tmp;
        b = b.next;
      }
      methodsToVEM.put(method.jmethod,new int[]{nodes, edges, highest});
    }

    // Constructs the actual control flow graph. Assumes the hash table
    // currently associates leaders with BasicBlocks, this function
    // builds the next[] and prev[] pointer arrays.
    private void buildBBCFG() 
    {
	Object branches[];
	Code_attribute ca = method.locate_code_attribute();

	{
	    h2bb = new Hashtable<Instruction, BasicBlock>(100,25);
	    t2bb = new Hashtable<Instruction, BasicBlock>(100,25);

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
		    HashSet<Instruction> ethandlers = new HashSet<Instruction>();

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

		    for (Object element : branches) {
			if ( element!=null ) {
			    BasicBlock bb = h2bb.get(element);
                 
			    if (bb == null)
			    {                 
				G.v().out.println("Warning: "
					       +"target of a branch is null");
				G.v().out.println ( insn );
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
	    BasicBlock bb = h2bb.get(
					 ca.exception_table[i].handler_inst);
	    if ( bb == null )
	    {
		G.v().out.println("Warning: No basic block found for" +
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

    /* We only handle simple cases. */
    Map<Instruction, Instruction> jsr2astore = new HashMap<Instruction, Instruction>();
    Map<Instruction, Instruction> astore2ret = new HashMap<Instruction, Instruction>();
    
    LinkedList<Instruction> jsrorder = new LinkedList<Instruction>();

    /* Eliminate subroutines ( JSR/RET instructions ) by inlining the 
       routine bodies. */
    private boolean eliminateJsrRets()
    {
	Instruction insn = this.sentinel;

	// find the last instruction, for copying blocks.
	while (insn.next != null) {
	    insn = insn.next;
	}
	this.lastInstruction = insn;

	HashMap<Instruction, Instruction> todoBlocks = new HashMap<Instruction, Instruction>();
	todoBlocks.put(this.sentinel.next, this.lastInstruction);
	LinkedList<Instruction> todoList = new LinkedList<Instruction>();
	todoList.add(this.sentinel.next);

	while (!todoList.isEmpty()) {
	    Instruction firstInsn = todoList.removeFirst();
	    Instruction lastInsn  = todoBlocks.get(firstInsn);

	    jsrorder.clear();
	    jsr2astore.clear();
	    astore2ret.clear();

	    if (findOutmostJsrs(firstInsn, lastInsn)) {
		HashMap<Instruction, Instruction> newblocks = inliningJsrTargets();
		todoBlocks.putAll(newblocks);
		todoList.addAll(newblocks.keySet());
	    }
	}

	/* patch exception table and others.*/
	{
	    method.instructions = this.sentinel.next;

	    adjustExceptionTable();
      	    adjustLineNumberTable();
	    adjustBranchTargets();
	}

	// we should prune the code and exception table here.
	// remove any exception handler whose region is in a jsr/ret block.
      	// pruneExceptionTable();

	return true;
    }

    // find outmost jsr/ret pairs in a code area, all information is
    // saved in jsr2astore, and astore2ret
    // start : start instruction, inclusively.
    // end   : the last instruction, inclusively.
    // return the last instruction encounted ( before end )
    // the caller cleans jsr2astore, astore2ret
    private boolean findOutmostJsrs(Instruction start, Instruction end) {
	// use to put innerJsrs.
	HashSet<Instruction> innerJsrs = new HashSet<Instruction>();
	boolean unusual = false;

	Instruction insn = start;
	do {
	    if (insn instanceof Instruction_Jsr
		|| insn instanceof Instruction_Jsr_w)
	    {
		if (innerJsrs.contains(insn)) {
		    // skip it
		    insn = insn.next;
		    continue;
		}    

		Instruction astore = ((Instruction_branch)insn).target;
		if (! (astore instanceof Interface_Astore))
		{
		    unusual = true;
		    break;
		}
		
		Instruction ret = findMatchingRet(astore, insn, innerJsrs);

		/*
		if (ret == null)
		{
		    unusual = true;
		    break;
		}
		*/

		jsrorder.addLast(insn);
		jsr2astore.put(insn, astore);
		astore2ret.put(astore, ret);
	    }
 
	    insn = insn.next;
	   
	} while (insn != end.next);

	if (unusual)
	{
	    G.v().out.println("Sorry, I cannot handle this method.");
	    return false;
	}
	
	return true;
    }

    private Instruction findMatchingRet(Instruction astore, 
					Instruction jsr,
					HashSet<Instruction>     innerJsrs)
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
		innerJsrs.add(insn);
	    }

	    insn = insn.next;
	}

	return null;
    }

    // make copies of jsr/ret blocks
    // return new blocks
    private HashMap<Instruction, Instruction> inliningJsrTargets()
    {
	/*
	for (int i=0, n=jsrorder.size(); i<n; i++) {
	    Instruction jsr    = (Instruction)jsrorder.get(i);
	    Instruction astore = (Instruction)jsr2astore.get(jsr);
	    Instruction ret    = (Instruction)astore2ret.get(astore);
	    G.v().out.println("jsr"+jsr.label+"\t"
			       +"as"+astore.label+"\t"
			       +"ret"+ret.label);
	}
	*/
	HashMap<Instruction, Instruction> newblocks = new HashMap<Instruction, Instruction>();

	while (!jsrorder.isEmpty())
	{
	    Instruction jsr = jsrorder.removeFirst();	    
	    Instruction astore = jsr2astore.get(jsr);

	    Instruction ret = astore2ret.get(astore);

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

	    // just quick hack
	    if (ret != null) {
		newblocks.put(newhead, this.lastInstruction);
	    }
	}

	return newblocks;
    }

    /* make a copy of code between from and to exclusively, 
     * fixup targets of branch instructions in the code.
     */
    private Instruction makeCopyOf(Instruction astore,
				   Instruction ret,
				   Instruction target)
    {
	// do a quick hacker for ret == null
	if (ret == null) {
	    return astore.next;
	}

	Instruction last = this.lastInstruction;
	Instruction headbefore = last;

	int curlabel = this.lastInstruction.label;

	// mapping from original instructions to new instructions.
	HashMap<Instruction, Instruction> insnmap = new HashMap<Instruction, Instruction>(); 
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
		G.v().out.println("Error !");
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

	// fixup targets in new instruction (only in the scope of 
	//  new instructions).
	// do not forget set target labelled as TRUE
	insn = headbefore.next;
	while (insn != last)
	{
	    if (insn instanceof Instruction_branch)
	    {
		Instruction oldtgt = ((Instruction_branch)insn).target;
		Instruction newtgt = insnmap.get(oldtgt);
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
		
		Instruction newdefault = insnmap.get(switchinsn.default_inst);
		if (newdefault != null)
		{
		    switchinsn.default_inst = newdefault;
		    newdefault.labelled = true;
		}

		for (int i=0; i<switchinsn.match_insts.length; i++)
		{
		    Instruction newtgt = insnmap.get(switchinsn.match_insts[i]);
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
		
		Instruction newdefault = insnmap.get(switchinsn.default_inst);
		if (newdefault != null)
		{
		    switchinsn.default_inst = newdefault;
		    newdefault.labelled = true;
		}

		for (int i=0; i<switchinsn.jump_insts.length; i++)
		{
		    Instruction newtgt = insnmap.get(switchinsn.jump_insts[i]);
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
	// new exception table has new exception range, 
	// and the new exception handler.
	{
	    Code_attribute ca = method.locate_code_attribute();

	    LinkedList<exception_table_entry> newentries = new LinkedList<exception_table_entry>();

	    int orig_start_of_subr = astore.next.originalIndex; // inclusive
	    int orig_end_of_subr = ret.originalIndex; // again, inclusive

	    for (int i=0; i<ca.exception_table_length; i++) 
	    {
		exception_table_entry etentry =
		    ca.exception_table[i];

		int orig_start_of_trap = etentry.start_pc; // inclusive
		int orig_end_of_trap = etentry.end_pc; // exclusive
		if ( orig_start_of_trap < orig_end_of_subr &&
		     orig_end_of_trap > orig_start_of_subr) {
		    // At least a portion of the cloned subroutine is trapped.
		    exception_table_entry newone = 
			new exception_table_entry();
		    if (orig_start_of_trap <= orig_start_of_subr) {
			newone.start_inst = headbefore.next;
		    } else {
		    	Instruction ins = insnmap.get(etentry.start_inst);
		    	if(ins!=null)
		    		newone.start_inst = insnmap.get(etentry.start_inst);
		    	else 
		    		newone.start_inst = etentry.start_inst;
		    }
		    if (orig_end_of_trap > orig_end_of_subr) {
			newone.end_inst = null; // Representing the insn after
						// the last instruction in the
						// subr; we need to fix it if
						// we inline another subr.
		    } else {
			newone.end_inst = insnmap.get(etentry.end_inst);
		    }

		    newone.handler_inst = insnmap.get(etentry.handler_inst);
		    if (newone.handler_inst == null)
			newone.handler_inst = etentry.handler_inst;

		    // We can leave newone.start_pc == 0 and newone.end_pc == 0.
		    // since that cannot overlap the range of any other
		    // subroutines that get inlined later.

		    newentries.add(newone);
		}
		// Finally, fix up the old entry if its protected area
		// ran to the end of the method we have just lengthened:
		// patch its end marker to be the first
		// instruction in the subroutine we've just inlined.
		if (etentry.end_inst == null) {
		    etentry.end_inst = headbefore.next;
		}
	    }

	    if (newentries.size() > 0)
	    {
		ca.exception_table_length += newentries.size();
		exception_table_entry[] newtable = new exception_table_entry[ca.exception_table_length];
		System.arraycopy(ca.exception_table, 0, newtable, 0, ca.exception_table.length);
		for (int i=0, j=ca.exception_table.length; i<newentries.size(); i++, j++)
		{
		    newtable[j] = newentries.get(i);
		}
		
		ca.exception_table = newtable;
	    }
	}
	
	return headbefore.next;
    }

    /* if a jsr/astore/ret is replaced by some other instruction, it will be put on this table. */
    private final Hashtable<Instruction, Instruction_Goto> replacedInsns = new Hashtable<Instruction, Instruction_Goto>();
    /* bootstrap methods table */
    private BootstrapMethods_attribute bootstrap_methods_attribute;
    /* do not forget set the target labelled as TRUE.*/
    private void adjustBranchTargets()
    {
	Instruction insn = this.sentinel.next;
	while (insn != null)
	{
	    if (insn instanceof Instruction_branch)
	    {
		Instruction_branch binsn = (Instruction_branch)insn;
		Instruction newtgt = replacedInsns.get(binsn.target);
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
		
		Instruction newdefault = 
		    replacedInsns.get(switchinsn.default_inst);
		if (newdefault != null)
		{
		    switchinsn.default_inst = newdefault;
		    newdefault.labelled = true;
		}
		
		for (int i=0; i<switchinsn.npairs; i++)
		{
		    Instruction newtgt = 
			replacedInsns.get(switchinsn.match_insts[i]);
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
		
		Instruction newdefault = replacedInsns.get(switchinsn.default_inst);
		if (newdefault != null)
		{
		    switchinsn.default_inst = newdefault;
		    newdefault.labelled = true;
		}

		for (int i=0; i<=switchinsn.high-switchinsn.low; i++)
		{
		    Instruction newtgt = 
			replacedInsns.get(switchinsn.jump_insts[i]);
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
	    Instruction newinsn = replacedInsns.get(oldinsn);
	    if (newinsn != null) 
		entry.start_inst = newinsn;

	    oldinsn = entry.end_inst;
	    if (entry.end_inst != null)
	    {
		newinsn = replacedInsns.get(oldinsn);	    
		if (newinsn != null)
		    entry.end_inst = newinsn;
	    }

	    oldinsn = entry.handler_inst;
	    newinsn = replacedInsns.get(oldinsn);
	    if (newinsn != null)
		entry.handler_inst = newinsn;
	}
    }

    private void adjustLineNumberTable()
    {
	if (!Options.v().keep_line_number())
	    return;
	if (method.code_attr == null)
	    return;

	attribute_info[] attributes = method.code_attr.attributes;

	for (attribute_info element : attributes) {
	    if (element instanceof LineNumberTable_attribute)
	    {
		LineNumberTable_attribute lntattr =
		    (LineNumberTable_attribute)element;
		for (line_number_table_entry element0 : lntattr.line_number_table) {
		    Instruction oldinst = 
			element0.start_inst;
		    Instruction newinst =
			replacedInsns.get(oldinst);
		    if (newinst != null)
			element0.start_inst = newinst;
		}
	    }
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

   /** Main.v() entry point for converting list of Instructions to Jimple statements;
    * performs flow analysis, constructs Jimple statements, and fixes jumps.
    * @param constant_pool constant pool of ClassFile.
    * @param this_class constant pool index of the CONSTANT_Class_info object for
    * this' class.
    * @param bootstrap_methods_attribute 
    * @return <i>true</i> if all ok, <i>false</i> if there was an error.
    * @see Stmt
    */
    public boolean jimplify(cp_info constant_pool[],int this_class, BootstrapMethods_attribute bootstrap_methods_attribute, JimpleBody listBody)
   {
        this.bootstrap_methods_attribute = bootstrap_methods_attribute;

        Chain units = listBody.getUnits();

        this.listBody = listBody;
        this.units = units;
        instructionToFirstStmt = new HashMap<Instruction, Stmt>();
        instructionToLastStmt = new HashMap<Instruction, Stmt>();

        jmethod = listBody.getMethod();
        cm = Scene.v();
        
        //TypeArray.setClassManager(cm);
        //TypeStack.setClassManager(cm);

        Set initialLocals = new ArraySet();

        List parameterTypes = jmethod.getParameterTypes();

        // Initialize nameToLocal which is an index*Type->Local map, which is used
        // to determine local in bytecode references.
        {
            Code_attribute ca = method.locate_code_attribute();
            LocalVariableTable_attribute la = ca.findLocalVariableTable();
            LocalVariableTypeTable_attribute lt = ca.findLocalVariableTypeTable();

            Util.v().bodySetup(la,lt,constant_pool);
            
            Type thisType = RefType.v(jmethod.getDeclaringClass().getName());
            boolean isStatic = Modifier.isStatic(jmethod.getModifiers());

            int currentLocalIndex = 0;

            // Initialize the 'this' variable
            {
                if(!isStatic)
                {
                    Local local = Util.v().getLocalForParameter(listBody, currentLocalIndex);
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
                    Local local = Util.v().getLocalForParameter(listBody, currentLocalIndex);
                    Type type = (Type) typeIt.next();
                    initialLocals.add(local);

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

            Util.v().resetEasyNames();
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
		Vector<BasicBlock> bsucc = block.succ;
		int size = bsucc.size();
		Instruction[] succs = new Instruction[size];

		for(int i = 0; i<size; i++)
		    succs[i] = bsucc.elementAt(i).head;		    
		insn.succs = succs;			      
	    } 

	    block = block.next;
	}	
    }

    /** Main.v() entry point for converting list of Instructions to Jimple statements;
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
        Set<Instruction> handlerInstructions = new ArraySet();

        Map<Instruction, SootClass> handlerInstructionToException = new HashMap<Instruction, SootClass>();
        Map<Instruction, TypeStack> instructionToTypeStack;
        Map<Instruction, TypeStack> instructionToPostTypeStack;

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

        Set<Instruction> reachableInstructions = new HashSet<Instruction>();
        
        // Mark all the reachable instructions
        {
            LinkedList<Instruction> instructionsToVisit = new LinkedList<Instruction>();
            
            reachableInstructions.add(firstInstruction);
            instructionsToVisit.addLast(firstInstruction);
            
            while( !instructionsToVisit.isEmpty())
            {
                Instruction ins = instructionsToVisit.removeFirst();

		Instruction[] succs = ins.succs;
	       
		for (Instruction succ : succs) {
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
            instructionToTypeStack = new HashMap<Instruction, TypeStack>();
            instructionToPostTypeStack = new HashMap<Instruction, TypeStack>();

            Set<Instruction> visitedInstructions = new HashSet<Instruction>();
            List<Instruction> changedInstructions = new ArrayList<Instruction>();

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
                    Instruction ins = changedInstructions.get(0);

                    changedInstructions.remove(0);

                    OutFlow ret = processFlow(ins, instructionToTypeStack.get(ins),
                        constant_pool);

                    instructionToPostTypeStack.put(ins, ret.typeStack);

                    Instruction[] successors = ins.succs;

                    for (Instruction s : successors) {
                        if(!visitedInstructions.contains(s))
                        {
                            // Special case for the first time visiting.

                            if(handlerInstructions.contains(s))
                            {
                                TypeStack exceptionTypeStack = (TypeStack.v()).push(RefType.v(
                                    handlerInstructionToException.get(s).getName()));

                                instructionToTypeStack.put(s, exceptionTypeStack);
                            }
                            else {
                                instructionToTypeStack.put(s, ret.typeStack);
                            }

                            visitedInstructions.add(s);
                            changedInstructions.add(s);

                            // G.v().out.println("adding successor: " + s);
                        }
                        else {
                            // G.v().out.println("considering successor: " + s);
                        
							TypeStack newTypeStack,
                                oldTypeStack = instructionToTypeStack.get(s);

                            if(handlerInstructions.contains(s))
                            {
                                // The type stack for an instruction handler should always be that of
                                // single object on the stack.

                                TypeStack exceptionTypeStack = (TypeStack.v()).push(RefType.v(
                                    handlerInstructionToException.get(s).getName()));

                                newTypeStack = exceptionTypeStack;
                            }
                            else
							{
								try {
                                	newTypeStack = ret.typeStack.merge(oldTypeStack);
								} catch (RuntimeException re)
								{
									G.v().out.println("Considering "+s);
									throw re;
								}
							}
                            if(!newTypeStack.equals(oldTypeStack))
                            {
                                changedInstructions.add(s);
                                // G.v().out.println("requires a revisit: " + s);
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

     //       G.v().out.println();

            while(ins != null)
            {
                TypeStack typeStack = instructionToTypeStack.get(ins);
                // TypeArray typeArray = (TypeArray) instructionToLocalArray.get(ins);
/*
                G.v().out.println("[TypeArray]");
                typeArray.print(G.v().out);
                G.v().out.println();

                G.v().out.println("[TypeStack]");
                typeStack.print(G.v().out);
                G.v().out.println();

                G.v().out.println(ins.toString());
*/

                ins = ins.next;
/*

                G.v().out.println();
                G.v().out.println();
*/

            }
        }


        // G.v().out.println("Producing Jimple code...");

        // Jimplify each statement
        {
            BasicBlock b = cfg;

            while(b != null)
            {
                Instruction ins = b.head;
                b.statements = new ArrayList<Stmt>();

                List<Stmt> blockStatements = b.statements;

		for (;;)
		{
                    List<Stmt> statementsForIns = new ArrayList<Stmt>();

                    if(reachableInstructions.contains(ins))
                        generateJimple(ins, instructionToTypeStack.get(ins),
                            instructionToPostTypeStack.get(ins), constant_pool,
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

        jimpleTargetFixup();  // fix up jump targets

        /*
        // Print out basic blocks
        {
            BasicBlock b = cfg;

            G.v().out.println("Basic blocks for: " + jmethod.getName());

            while(b != null)
            {
                Instruction ins = b.head;

                G.v().out.println();

                while(ins != null)
                {
                    G.v().out.println(ins.toString());
                    ins = ins.next;
                }

                b = b.next;
            }
        }
        */ 

        // Insert beginCatch/endCatch statements for exception handling
        {
            Map<Stmt, Stmt> targetToHandler = new HashMap<Stmt, Stmt>();
            
	    for(int i = 0; i < codeAttribute.exception_table_length; i++)
	    {
		Instruction startIns = 
		    codeAttribute.exception_table[i].start_inst;
		Instruction endIns = 
		    codeAttribute.exception_table[i].end_inst;
		Instruction targetIns = 
		    codeAttribute.exception_table[i].handler_inst;

		if(!instructionToFirstStmt.containsKey(startIns) ||
		   (endIns != null && (!instructionToLastStmt.containsKey(endIns))))
                {
		    throw new RuntimeException("Exception range does not coincide with jimple instructions");
		}

		if(!instructionToFirstStmt.containsKey(targetIns))
                {
		    throw new RuntimeException
			("Exception handler does not coincide with jimple instruction");
		}

		SootClass exception;

		// Determine exception to catch
		{
		    int catchType = 
			codeAttribute.exception_table[i].catch_type;
		    if(catchType != 0)
                    {
			CONSTANT_Class_info classinfo = (CONSTANT_Class_info)
			    constant_pool[catchType];

			String name = ((CONSTANT_Utf8_info) 
				       (constant_pool[classinfo.name_index])).convert();
			name = name.replace('/', '.');
			exception = cm.getSootClass(name);
		    }
		    else
			exception = cm.getSootClass("java.lang.Throwable");
		}

		Stmt newTarget;

		// Insert assignment of exception
		{
		    Stmt firstTargetStmt = 
			instructionToFirstStmt.get(targetIns);
                        
		    if(targetToHandler.containsKey(firstTargetStmt))
			newTarget = 
			    targetToHandler.get(firstTargetStmt);
		    else
                    {
			Local local = 
			    Util.v().getLocalCreatingIfNecessary(listBody, "$stack0",UnknownType.v());
			
			newTarget = Jimple.v().newIdentityStmt(local, Jimple.v().newCaughtExceptionRef());

			// changed to account for catch blocks which are also part of normal control flow
            //units.insertBefore(newTarget, firstTargetStmt);			
            ((PatchingChain)units).insertBeforeNoRedirect(newTarget, firstTargetStmt);

			targetToHandler.put(firstTargetStmt, newTarget);
            if (units.getFirst()!=newTarget) {
              Unit prev = (Unit)units.getPredOf(newTarget);
              if (prev != null && prev.fallsThrough())
                units.insertAfter(Jimple.v().newGotoStmt(firstTargetStmt), prev);
            }
		    }
		}

		// Insert trap
		{
		    Stmt firstStmt = instructionToFirstStmt.get(startIns);
		    Stmt afterEndStmt;
		    if (endIns == null) {
			// A kludge which isn't really correct, but
			// gets us closer to correctness (until we
			// clean up the rest of Soot to properly
			// represent Traps which extend to the end 
			// of a method): if the protected code extends
			// to the end of the method, use the last Stmt
			// as the endUnit of the Trap, even though
			// that will leave the last unit outside
			// the protected area.
			afterEndStmt = (Stmt) units.getLast();
		    } else {
			afterEndStmt = instructionToLastStmt.get(endIns);
			IdentityStmt catchStart = 
			    (IdentityStmt) targetToHandler.get(afterEndStmt); 
			                    // (Cast to IdentityStmt as an assertion check.)
			if (catchStart != null) {
			    // The protected region extends to the beginning of an
			    // exception handler, so we need to reset afterEndStmt
			    // to the identity statement which we have inserted
			    // before the old afterEndStmt.
			    if (catchStart != units.getPredOf(afterEndStmt)) {
				throw new IllegalStateException("Assertion failure: catchStart != pred of afterEndStmt");
			    }
			    afterEndStmt = catchStart;
			}
		    }

		    Trap trap = Jimple.v().newTrap(exception, 
						   firstStmt, 
						   afterEndStmt, 
						   newTarget);
		    listBody.getTraps().add(trap);
		}
	    }
        }

	/* convert line number table to tags attached to statements */
	if (Options.v().keep_line_number())
	{
	    HashMap<Stmt, Tag> stmtstags = new HashMap<Stmt, Tag>();
	    LinkedList<Stmt> startstmts = new LinkedList<Stmt>();

	    attribute_info[] attrs = codeAttribute.attributes;
	    for (attribute_info element : attrs) {
		if (element instanceof LineNumberTable_attribute)
		{
		    LineNumberTable_attribute lntattr =
			(LineNumberTable_attribute)element;
		    for (line_number_table_entry element0 : lntattr.line_number_table) {
			Stmt start_stmt = instructionToFirstStmt.get(
				 element0.start_inst);

			if (start_stmt != null)
			{
			    LineNumberTag lntag= new LineNumberTag(
		   		    element0.line_number);
			    stmtstags.put(start_stmt, lntag);
			    startstmts.add(start_stmt);
			}
		    }
		}
	    }

	    /* if the predecessor of a statement is a caughtexcetionref,
             * give it the tag of its successor */
            for( Iterator<Stmt> stmtIt = new ArrayList<Stmt>(stmtstags.keySet()).iterator(); stmtIt.hasNext(); ) {
                final Stmt stmt = stmtIt.next();
                Stmt pred = stmt;
                Tag tag = stmtstags.get(stmt);
                while(true) {
                    pred = (Stmt)units.getPredOf(pred);
                    if( pred == null ) break;
                    if(!(pred instanceof IdentityStmt)) break;
                    stmtstags.put(pred, tag);
                    pred.addTag(tag);
                }
            }

	    /* attach line number tag to each statement. */
	    for (int i=0; i<startstmts.size(); i++)
	    {
		Stmt stmt = startstmts.get(i);
		Tag tag = stmtstags.get(stmt);
		
		stmt.addTag(tag);
		
		stmt = (Stmt)units.getSuccOf(stmt);
		while (stmt != null 
		       && !stmtstags.containsKey(stmt))
		{
		    stmt.addTag(tag);
		    stmt = (Stmt)units.getSuccOf(stmt);
		}
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
        x = ((ins.code))&0xff;

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
            Type baseType = jimpleTypeOfAtype(((Instruction_Newarray)ins).atype);

            typeStack = typeStack.push(ArrayType.v(baseType, 1));
            break;
         }

        case ByteCode.ANEWARRAY:
        {
            CONSTANT_Class_info c = (CONSTANT_Class_info) constant_pool[
                ((Instruction_Anewarray)ins).arg_i];

            String name = ((CONSTANT_Utf8_info) (constant_pool[c.name_index])).convert();
            name = name.replace('/', '.');

            Type baseType;

            if(name.startsWith("[")) {
                String baseName = getClassName(constant_pool, ((Instruction_Anewarray)ins).arg_i);
                baseType = Util.v().jimpleTypeOfFieldDescriptor(baseName);
            } else {
                baseType = RefType.v(name);
            }

            typeStack = popSafe(typeStack, IntType.v());
            typeStack = typeStack.push(baseType.makeArrayType());
            break;
        }

        case ByteCode.MULTIANEWARRAY:
        {
            int bdims = (((Instruction_Multianewarray)ins).dims);


            CONSTANT_Class_info c = (CONSTANT_Class_info) constant_pool[
               ((Instruction_Multianewarray)ins).arg_i];

            String arrayDescriptor = ((CONSTANT_Utf8_info) (constant_pool[c.name_index])).convert();

            ArrayType arrayType = (ArrayType)
                Util.v().jimpleTypeOfFieldDescriptor(arrayDescriptor);

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

         case ByteCode.INVOKEDYNAMIC:
         {
             Instruction_Invokedynamic iv = (Instruction_Invokedynamic)ins;
   	      	 CONSTANT_InvokeDynamic_info iv_info = (CONSTANT_InvokeDynamic_info) constant_pool[iv.invoke_dynamic_index];
             int args = cp_info.countParams(constant_pool,iv_info.name_and_type_index);
             Type returnType = byteCodeTypeOf(jimpleReturnTypeOfNameAndType(cm,
                 constant_pool, iv_info.name_and_type_index));

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
                castType = Util.v().jimpleTypeOfFieldDescriptor(getClassName(constant_pool,
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

        return Util.v().jimpleTypeOfFieldDescriptor(fieldDescriptor);
    }

    private Type jimpleReturnTypeOfNameAndType(Scene cm,
            cp_info[] constant_pool, int index)
    {
        CONSTANT_NameAndType_info nat = (CONSTANT_NameAndType_info)
            (constant_pool[index]);

        String methodDescriptor = ((CONSTANT_Utf8_info)
            (constant_pool[nat.descriptor_index])).convert();

        return Util.v().jimpleReturnTypeOfMethodDescriptor(methodDescriptor);
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

        return Util.v().jimpleReturnTypeOfMethodDescriptor(methodDescriptor);
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

        return Util.v().jimpleReturnTypeOfMethodDescriptor(methodDescriptor);
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
        else if (c instanceof CONSTANT_Class_info){
            CONSTANT_Class_info info = (CONSTANT_Class_info)c;
            String name = ((CONSTANT_Utf8_info) (constant_pool[info.name_index])).convert();
            name = name.replace('/', '.');
            if (name.charAt(0) == '['){
                int dim = 0;
                while (name.charAt(dim) == '['){
                    dim++;
                }
                // array type
                Type baseType = null;
                char typeIndicator = name.charAt(dim);
                switch (typeIndicator){
                    case 'I': baseType = IntType.v(); break;
                    case 'C': baseType = CharType.v(); break;
                    case 'F': baseType = FloatType.v(); break;          
                    case 'D': baseType = DoubleType.v(); break;          
                    case 'B': baseType = ByteType.v(); break;          
                    case 'S': baseType = ShortType.v(); break;          
                    case 'Z': baseType = BooleanType.v(); break;          
                    case 'J': baseType = LongType.v(); break;          
                    case 'L': baseType = RefType.v(
                                      name.substring(dim+1, name.length()-1));
                              break;
                    default : throw new RuntimeException("Unknown Array Base Type in Class Constant");
                }
                typeStack = typeStack.push(ArrayType.v(baseType, dim));
            }
            else {
                typeStack = typeStack.push(RefType.v(name));
            }
        }
        else
            throw new RuntimeException("Attempting to push a non-constant cp entry"+c.getClass());

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

                    ((GotoStmt)s).setTarget(b.succ.firstElement().getHeadJStmt());
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
		    G.v().out.println("Error :");
		    for (int i=0; i<b.statements.size(); i++)
			G.v().out.println(b.statements.get(i));
		    
		    throw new RuntimeException(b +" has "+b.succ.size()+" successors.");		    
                }
            }
            else if (s instanceof IfStmt)
            {
               if (b.succ.size()!=2)
                  G.v().out.println("How can an if not have 2 successors?");

               if((b.succ.firstElement())==b.next)
               {
                  ((IfStmt)s).setTarget(b.succ.elementAt(1).getHeadJStmt());
               }
               else
               {
                  ((IfStmt)s).setTarget(b.succ.firstElement().getHeadJStmt());
               }

            }
            else if (s instanceof TableSwitchStmt)
            {
               int count=0;
               TableSwitchStmt sts = (TableSwitchStmt)s;
               // Successors of the basic block ending with a switch statement
               // are listed in the successor vector in order, with the
               // default as the very first (0-th entry)

               for (BasicBlock basicBlock : b.succ) {
                  p = (basicBlock);
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

               for (BasicBlock basicBlock : b.succ) {
                  p = (basicBlock);
                  if (count==0) {
                     sls.setDefaultTarget(p.getHeadJStmt());
                  } else {
                     sls.setTarget(count-1, p.getHeadJStmt());
                  }
                  count++;
               }
            }

         b.done = false;
         for (BasicBlock basicBlock : b.succ) {
            p = (basicBlock);
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
			G.v().out.println("Error 2nd processing exception block.");
			break;
		    }
		}
	    }
	}
    }

   private void generateJimpleForCPEntry(cp_info constant_pool[], int i,
                            TypeStack typeStack, TypeStack postTypeStack,
                            SootMethod jmethod, List<Stmt> statements)
   {
      Stmt stmt;
      Value rvalue;

      cp_info c = constant_pool[i];

      if (c instanceof CONSTANT_Integer_info)
      {
         CONSTANT_Integer_info ci = (CONSTANT_Integer_info)c;

         rvalue = IntConstant.v((int) ci.bytes);
         stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
            postTypeStack.topIndex()), rvalue);
      }
      else if (c instanceof CONSTANT_Float_info)
      {
         CONSTANT_Float_info cf = (CONSTANT_Float_info)c;

         rvalue = FloatConstant.v(cf.convert());
         stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
            postTypeStack.topIndex()), rvalue);
      }
      else if (c instanceof CONSTANT_Long_info)
      {
         CONSTANT_Long_info cl = (CONSTANT_Long_info)c;

         rvalue = LongConstant.v(cl.convert());
         stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
            postTypeStack.topIndex()), rvalue);
      }
      else if (c instanceof CONSTANT_Double_info)
      {
         CONSTANT_Double_info cd = (CONSTANT_Double_info)c;

         rvalue = DoubleConstant.v(cd.convert());

         stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
            postTypeStack.topIndex()), rvalue);
      }
      else if (c instanceof CONSTANT_String_info)
      {
         CONSTANT_String_info cs = (CONSTANT_String_info)c;

         String constant = cs.toString(constant_pool);

         if(constant.startsWith("\"") && constant.endsWith("\""))
            constant = constant.substring(1, constant.length() - 1);

         rvalue = StringConstant.v(constant);
         stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
            postTypeStack.topIndex()), rvalue);
      }
      else if (c instanceof CONSTANT_Utf8_info)
      {
         CONSTANT_Utf8_info cu = (CONSTANT_Utf8_info)c;

         String constant = cu.convert();

         if(constant.startsWith("\"") && constant.endsWith("\""))
            constant = constant.substring(1, constant.length() - 1);

         rvalue = StringConstant.v(constant);
         stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
            postTypeStack.topIndex()), rvalue);
      }
      else if (c instanceof CONSTANT_Class_info){

        String className = ((CONSTANT_Utf8_info) (constant_pool[((CONSTANT_Class_info)c).name_index])).convert();


        rvalue = ClassConstant.v(className); 
        stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack, postTypeStack.topIndex()), rvalue);
      }
      else {
        throw new RuntimeException("Attempting to push a non-constant cp entry"+c);
      }

      statements.add(stmt);
    }

    void generateJimple(Instruction ins, TypeStack typeStack, TypeStack postTypeStack,
        cp_info constant_pool[],
        List<Stmt> statements, BasicBlock basicBlock)
   {
      Value[] params;
      Local l1 = null, l2 = null, l3 = null, l4 = null;

      Expr rhs=null;
      ConditionExpr co = null;

      ArrayRef a=null;
      int args;
      Value rvalue;

      //      int localIndex;

      Stmt stmt = null;

      int x = ((ins.code))&0xff;
      
      switch(x)
      {
         case ByteCode.BIPUSH:
            rvalue = IntConstant.v(((Instruction_Bipush)ins).arg_b);
            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rvalue);
            break;

         case ByteCode.SIPUSH:
            rvalue = IntConstant.v(((Instruction_Sipush)ins).arg_i);
            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
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
            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
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
            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rvalue);
            break;

         case ByteCode.LCONST_0:
         case ByteCode.LCONST_1:
            rvalue = LongConstant.v(x-ByteCode.LCONST_0);
            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rvalue);
            break;

         case ByteCode.FCONST_0:
         case ByteCode.FCONST_1:
         case ByteCode.FCONST_2:
            rvalue = FloatConstant.v((x - ByteCode.FCONST_0));
            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rvalue);
            break;

         case ByteCode.DCONST_0:
         case ByteCode.DCONST_1:
            rvalue = DoubleConstant.v((x-ByteCode.DCONST_0));
            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rvalue);
            break;

         case ByteCode.ILOAD:
         {
            Local local = Util.v().getLocalForIndex(listBody, ((Instruction_bytevar) ins).arg_b, ins);

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), local);
            break;
         }

         case ByteCode.FLOAD:
         {
            Local local = Util.v().getLocalForIndex(listBody, ((Instruction_bytevar)ins).arg_b, ins);

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), local);
            break;
         }

         case ByteCode.ALOAD:
         {
            Local local =
                Util.v().getLocalForIndex(listBody, ((Instruction_bytevar)ins).arg_b, ins);

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), local);
            break;
         }

         case ByteCode.DLOAD:
         {
            Local local =
                Util.v().getLocalForIndex(listBody, ((Instruction_bytevar)ins).arg_b, ins);

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), local);
            break;
         }

         case ByteCode.LLOAD:
         {
            Local local =
                Util.v().getLocalForIndex(listBody, ((Instruction_bytevar)ins).arg_b, ins);

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), local);
            break;
         }

         case ByteCode.ILOAD_0:
         case ByteCode.ILOAD_1:
         case ByteCode.ILOAD_2:
         case ByteCode.ILOAD_3:
         {
            Local local =
                Util.v().getLocalForIndex(listBody, (x - ByteCode.ILOAD_0), ins);

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), local);
            break;
         }

         case ByteCode.FLOAD_0:
         case ByteCode.FLOAD_1:
         case ByteCode.FLOAD_2:
         case ByteCode.FLOAD_3:
         {
            Local local =
                Util.v().getLocalForIndex(listBody, (x - ByteCode.FLOAD_0), ins);

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), local);
            break;
         }

         case ByteCode.ALOAD_0:
         case ByteCode.ALOAD_1:
         case ByteCode.ALOAD_2:
         case ByteCode.ALOAD_3:
         {
            Local local =
                Util.v().getLocalForIndex(listBody, (x - ByteCode.ALOAD_0), ins);

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), local);
            break;
         }

         case ByteCode.LLOAD_0:
         case ByteCode.LLOAD_1:
         case ByteCode.LLOAD_2:
         case ByteCode.LLOAD_3:
         {
            Local local =
                Util.v().getLocalForIndex(listBody, (x - ByteCode.LLOAD_0), ins);

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), local);
            break;
         }

         case ByteCode.DLOAD_0:
         case ByteCode.DLOAD_1:
         case ByteCode.DLOAD_2:
         case ByteCode.DLOAD_3:
         {
            Local local =
                Util.v().getLocalForIndex(listBody, (x - ByteCode.DLOAD_0), ins);

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), local);
            break;
         }

         case ByteCode.ISTORE:
         {  
            Local local =
                Util.v().getLocalForIndex(listBody,
                ((Instruction_bytevar)ins).arg_b, ins);

            stmt = Jimple.v().newAssignStmt(local, Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;
         }

         case ByteCode.FSTORE:
         {  
            Local local =
                Util.v().getLocalForIndex(listBody,
                ((Instruction_bytevar)ins).arg_b, ins);

            stmt = Jimple.v().newAssignStmt(local, Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;
         }

         case ByteCode.ASTORE:
         {  
            Local local =
                Util.v().getLocalForIndex(listBody,
                ((Instruction_bytevar)ins).arg_b, ins);

            stmt = Jimple.v().newAssignStmt(local, Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;
         }

         case ByteCode.LSTORE:
         {  
            Local local =
                Util.v().getLocalForIndex(listBody,
                ((Instruction_bytevar)ins).arg_b, ins);

            stmt = Jimple.v().newAssignStmt(local, Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;
         }

         case ByteCode.DSTORE:
         {  
            Local local =
                Util.v().getLocalForIndex(listBody,
                ((Instruction_bytevar)ins).arg_b, ins);

            stmt = Jimple.v().newAssignStmt(local, Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;
         }

         case ByteCode.ISTORE_0:
         case ByteCode.ISTORE_1:
         case ByteCode.ISTORE_2:
         case ByteCode.ISTORE_3:
         {
            Local local =
                Util.v().getLocalForIndex(listBody, (x - ByteCode.ISTORE_0), ins);

            stmt = Jimple.v().newAssignStmt(local, Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;
         }

         case ByteCode.FSTORE_0:
         case ByteCode.FSTORE_1:
         case ByteCode.FSTORE_2:
         case ByteCode.FSTORE_3:
         {
            Local local = Util.v().getLocalForIndex(listBody, (x - ByteCode.FSTORE_0), ins);

            stmt = Jimple.v().newAssignStmt(local, Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;
         }

         case ByteCode.ASTORE_0:
         case ByteCode.ASTORE_1:
         case ByteCode.ASTORE_2:
         case ByteCode.ASTORE_3:
         {
            Local local = Util.v().getLocalForIndex(listBody, (x - ByteCode.ASTORE_0), ins);

            stmt = Jimple.v().newAssignStmt(local, Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;
         }

         case ByteCode.LSTORE_0:
         case ByteCode.LSTORE_1:
         case ByteCode.LSTORE_2:
         case ByteCode.LSTORE_3:
         {
            Local local =
                Util.v().getLocalForIndex(listBody, (x - ByteCode.LSTORE_0), ins);

            stmt = Jimple.v().newAssignStmt(local, Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;
         }

         case ByteCode.DSTORE_0:
         case ByteCode.DSTORE_1:
         case ByteCode.DSTORE_2:
         case ByteCode.DSTORE_3:
         {
            Local local =
                Util.v().getLocalForIndex(listBody, (x - ByteCode.DSTORE_0), ins);

            stmt = Jimple.v().newAssignStmt(local, Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;
         }

         case ByteCode.IINC:
         {
            Local local =
                Util.v().getLocalForIndex(listBody,
                ((Instruction_Iinc)ins).arg_b, ins);

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
            Type baseType = jimpleTypeOfAtype(((Instruction_Newarray)ins).atype);

            rhs = Jimple.v().newNewArrayExpr(baseType,
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody,
                postTypeStack, postTypeStack.topIndex()), rhs);

            break;
         }

         case ByteCode.ANEWARRAY:
         {
            String baseName = getClassName(constant_pool, ((Instruction_Anewarray)ins).arg_i);

            Type baseType;

            if(baseName.startsWith("["))
                baseType = Util.v().jimpleTypeOfFieldDescriptor(getClassName(constant_pool, ((Instruction_Anewarray)ins).arg_i));
            else
                baseType = RefType.v(baseName);

            rhs = Jimple.v().newNewArrayExpr(baseType, Util.v().getLocalForStackOp(listBody,
                typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody,
                postTypeStack, postTypeStack.topIndex()),rhs);
            break;
         }

         case ByteCode.MULTIANEWARRAY:
         {
               int bdims = (((Instruction_Multianewarray)ins).dims);
               List dims = new ArrayList();

               for (int j=0; j < bdims; j++)
                  dims.add(Util.v().getLocalForStackOp(listBody, typeStack,
                    typeStack.topIndex() - bdims + j + 1));

               String mstype = constant_pool[((Instruction_Multianewarray)ins).arg_i].
                  toString(constant_pool);

               ArrayType jimpleType = (ArrayType) Util.v().jimpleTypeOfFieldDescriptor(mstype);

               rhs = Jimple.v().newNewMultiArrayExpr(jimpleType, dims);

               stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()),rhs);
            break;
         }


         case ByteCode.ARRAYLENGTH:
            rhs = Jimple.v().newLengthExpr(
                    Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
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
            a = Jimple.v().newArrayRef(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1),
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody,
                postTypeStack, postTypeStack.topIndex()), a);

            break;

         case ByteCode.IASTORE:
         case ByteCode.FASTORE:
         case ByteCode.AASTORE:
         case ByteCode.BASTORE:
         case ByteCode.CASTORE:
         case ByteCode.SASTORE:
            a = Jimple.v().newArrayRef(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 2), Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1));

            stmt = Jimple.v().newAssignStmt(a, Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;

         case ByteCode.LASTORE:
         case ByteCode.DASTORE:
            a = Jimple.v().newArrayRef(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 3), Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 2));

            stmt = Jimple.v().newAssignStmt(a, Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;


         case ByteCode.NOP:
            stmt = Jimple.v().newNopStmt();
            break;

         case ByteCode.POP:
         case ByteCode.POP2:
            stmt = Jimple.v().newNopStmt();
            break;

         case ByteCode.DUP:
            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));
            break;

         case ByteCode.DUP2:
            if(typeSize(typeStack.top()) == 2)
            {
                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 1),
                    Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 1));
            }
            else {
                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 1),
                    Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 1));

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex()), Util.v().getLocalForStackOp(listBody,
                    typeStack, typeStack.topIndex()));

                statements.add(stmt);

                stmt = null;
            }
            break;

         case ByteCode.DUP_X1:
            l1 = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex());
            l2 = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1);

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), l1);

            statements.add(stmt);

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex() - 1), l2);

            statements.add(stmt);

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex() - 2), Util.v().getLocalForStackOp(listBody,
                postTypeStack, postTypeStack.topIndex()));

            statements.add(stmt);

            stmt = null;
            break;

         case ByteCode.DUP_X2:
            if(typeSize(typeStack.get(typeStack.topIndex() - 2)) == 2)
            {
                l3 = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 2);
                l1 = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex());

                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 2), l3);

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 3), l1);

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex()), l1);

                statements.add(stmt);

                stmt = null;
            }
            else {
                l3 = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 2);
                l2 = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 1);
                l1 = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex());

                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex()), l1);

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 1), l2);

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 2), l3);

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 3), Util.v().getLocalForStackOp(
                    listBody, postTypeStack, postTypeStack.topIndex()));

                statements.add(stmt);

                stmt = null;
            }
            break;
            
        case ByteCode.DUP2_X1:
            if(typeSize(typeStack.get(typeStack.topIndex() - 1)) == 2)
            {
                l2 = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 1);
                l3 = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 2);

                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() -1), l2);

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 2), l3);
                
                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 4), 
                    Util.v().getLocalForStackOp(listBody, postTypeStack, postTypeStack.topIndex() - 1));

                statements.add(stmt);

                stmt = null;
            }
            else {
                l3 = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 2);
                l2 = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 1);
                l1 = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex());

                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex()), l1);

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 1), l2);

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 2), l3);

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 3), Util.v().getLocalForStackOp(
                    listBody, postTypeStack, postTypeStack.topIndex()));

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 4), Util.v().getLocalForStackOp(
                    listBody, postTypeStack, postTypeStack.topIndex() - 1));

                statements.add(stmt);

                stmt = null;
            }
            break;

         case ByteCode.DUP2_X2:
            if(typeSize(typeStack.get(typeStack.topIndex() - 1)) == 2)
            {
                l2 = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 1);

                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 1), l2);

                statements.add(stmt);
            }
            else {
                l1 = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex());
                l2 = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 1);

                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 1), l2);

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex()), l1);

                statements.add(stmt);

            }

            if(typeSize(typeStack.get(typeStack.topIndex() - 3)) == 2)
            {
                l4 = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 3);

                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 3), l4);

                statements.add(stmt);
            }
            else {
                l4 = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 3);
                l3 = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex() - 2);

                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 3), l4);

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 2), l3);

                statements.add(stmt);

            }

            if(typeSize(typeStack.get(typeStack.topIndex() - 1)) == 2)
            {
                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 5), Util.v().getLocalForStackOp(
                    listBody, postTypeStack, postTypeStack.topIndex() - 1));

                statements.add(stmt);
            }
            else {
                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 5), Util.v().getLocalForStackOp(
                    listBody, postTypeStack, postTypeStack.topIndex() - 1));

                statements.add(stmt);

                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex() - 4), Util.v().getLocalForStackOp(
                    listBody, postTypeStack, postTypeStack.topIndex()));

                statements.add(stmt);
            }
               stmt = null;
            break;

         case ByteCode.SWAP:
         {
            Local first;

            typeStack = typeStack.push(typeStack.top());
            first = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex());
            typeStack = typeStack.pop();
                // generation of a free temporary

            Local second = Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex());

            Local third = Util.v().getLocalForStackOp(listBody, postTypeStack,
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
            rhs = Jimple.v().newAddExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1), Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.DADD:
         case ByteCode.LADD:
            rhs = Jimple.v().newAddExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 3), Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.FSUB:
         case ByteCode.ISUB:
            rhs = Jimple.v().newSubExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1), Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.DSUB:
         case ByteCode.LSUB:
            rhs = Jimple.v().newSubExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 3), Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.FMUL:
         case ByteCode.IMUL:
            rhs = Jimple.v().newMulExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1), Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.DMUL:
         case ByteCode.LMUL:
            rhs = Jimple.v().newMulExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 3), Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.FDIV:
         case ByteCode.IDIV:
            rhs = Jimple.v().newDivExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1), Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.DDIV:
         case ByteCode.LDIV:
            rhs = Jimple.v().newDivExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 3), Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.FREM:
         case ByteCode.IREM:
            rhs = Jimple.v().newRemExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1), Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.DREM:
         case ByteCode.LREM:
            rhs = Jimple.v().newRemExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 3), Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.INEG:
         case ByteCode.LNEG:
         case ByteCode.FNEG:
         case ByteCode.DNEG:
            rhs = Jimple.v().newNegExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));
            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()),rhs);
            break;

         case ByteCode.ISHL:
            rhs = Jimple.v().newShlExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1), Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.ISHR:
            rhs = Jimple.v().newShrExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1), Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.IUSHR:
            rhs = Jimple.v().newUshrExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1), Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.LSHL:
            rhs = Jimple.v().newShlExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 2), Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.LSHR:
            rhs = Jimple.v().newShrExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 2), Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.LUSHR:
            rhs = Jimple.v().newUshrExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 2), Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.IAND:
            rhs = Jimple.v().newAndExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1), Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.LAND:
            rhs = Jimple.v().newAndExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 3), Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.IOR:
            rhs = Jimple.v().newOrExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1), Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.LOR:
            rhs = Jimple.v().newOrExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 3), Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.IXOR:
            rhs = Jimple.v().newXorExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1), Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.LXOR:
            rhs = Jimple.v().newXorExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 3), Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex() - 1));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.D2L:
         case ByteCode.F2L:
         case ByteCode.I2L:
            rhs = Jimple.v().newCastExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), LongType.v());

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.D2F:
         case ByteCode.L2F:
         case ByteCode.I2F:
            rhs = Jimple.v().newCastExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), FloatType.v());

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.I2D:
         case ByteCode.L2D:
         case ByteCode.F2D:
            rhs = Jimple.v().newCastExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), DoubleType.v());

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.L2I:
         case ByteCode.F2I:
         case ByteCode.D2I:
            rhs = Jimple.v().newCastExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), IntType.v());

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.INT2BYTE:
            rhs = Jimple.v().newCastExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), ByteType.v());

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.INT2CHAR:
            rhs = Jimple.v().newCastExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), CharType.v());

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.INT2SHORT:
            rhs = Jimple.v().newCastExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), ShortType.v());

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.IFEQ:
            co = Jimple.v().newEqExpr(Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()),
                IntConstant.v(0));

               stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IFNULL:
            co = Jimple.v().newEqExpr(Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()),
                NullConstant.v());

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IFLT:
            co = Jimple.v().newLtExpr(Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()),
                IntConstant.v(0));

               stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IFLE:
            co = Jimple.v().newLeExpr(Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()),
                    IntConstant.v(0));

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IFNE:
            co = Jimple.v().newNeExpr(Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()),
                    IntConstant.v(0));

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IFNONNULL:
            co = Jimple.v().newNeExpr(Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()),
                NullConstant.v());

                stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IFGT:
            co = Jimple.v().newGtExpr(Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()),
                    IntConstant.v(0));

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IFGE:
            co = Jimple.v().newGeExpr(Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()),
                IntConstant.v(0));

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IF_ICMPEQ:
            co = Jimple.v().newEqExpr(
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1),
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IF_ICMPLT:
            co = Jimple.v().newLtExpr(
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1),
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IF_ICMPLE:
            co = Jimple.v().newLeExpr(
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1),
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IF_ICMPNE:
            co = Jimple.v().newNeExpr(
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1),
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IF_ICMPGT:
            co = Jimple.v().newGtExpr(
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1),
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IF_ICMPGE:
            co = Jimple.v().newGeExpr(
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1),
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.LCMP:
            rhs = Jimple.v().newCmpExpr(
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-3),
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), rhs);
            break;

         case ByteCode.FCMPL:
            rhs = Jimple.v().newCmplExpr(
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1),
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody,
                postTypeStack, postTypeStack.topIndex()),rhs);
            break;

         case ByteCode.FCMPG:
            rhs = Jimple.v().newCmpgExpr(
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1),
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody,
                postTypeStack, postTypeStack.topIndex()),rhs);
            break;

         case ByteCode.DCMPL:
            rhs = Jimple.v().newCmplExpr(
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-3),
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody,
                postTypeStack, postTypeStack.topIndex()),rhs);
            break;

         case ByteCode.DCMPG:
            rhs = Jimple.v().newCmpgExpr(
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-3),
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody,
                postTypeStack, postTypeStack.topIndex()),rhs);
            break;

         case ByteCode.IF_ACMPEQ:
            co = Jimple.v().newEqExpr(
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1),
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

            stmt = Jimple.v().newIfStmt(co, new FutureStmt());
            break;

         case ByteCode.IF_ACMPNE:
            co = Jimple.v().newNeExpr(
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()-1),
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()));

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
             stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
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
                Util.v().getLocalForIndex(listBody, ((Instruction_Ret)ins).arg_b, ins);

            stmt = Jimple.v().newRetStmt(local);
            break;
         }

         case ByteCode.RET_W:
         {
            Local local =
                Util.v().getLocalForIndex(listBody, ((Instruction_Ret_w)ins).arg_i, ins);


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
            stmt = Jimple.v().newReturnStmt(Util.v().getLocalForStackOp(listBody,
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
                    Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()),
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
                Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex()),
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

            Type fieldType = Util.v().jimpleTypeOfFieldDescriptor(fieldDescriptor);
                
            SootClass bclass = cm.getSootClass(className);

            SootFieldRef fieldRef = Scene.v().makeFieldRef(bclass, fieldName, fieldType, false);

            InstanceFieldRef fr =
                Jimple.v().newInstanceFieldRef(Util.v().getLocalForStackOp(listBody,
                typeStack, typeStack.topIndex() - typeSize(typeStack.top())), fieldRef);

            rvalue = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex());
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

	    if (className.charAt(0) == '[')
	    	className = "java.lang.Object";

            SootClass bclass = cm.getSootClass(className);

            
            Type fieldType = Util.v().jimpleTypeOfFieldDescriptor(fieldDescriptor);
            SootFieldRef fieldRef = Scene.v().makeFieldRef(bclass, fieldName, fieldType, false);

            fr = Jimple.v().newInstanceFieldRef(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), fieldRef);

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
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

            Type fieldType = Util.v().jimpleTypeOfFieldDescriptor(fieldDescriptor);
            
            SootClass bclass = cm.getSootClass(className);
            SootFieldRef fieldRef = Scene.v().makeFieldRef(bclass, fieldName, fieldType, true);

            fr = Jimple.v().newStaticFieldRef(fieldRef);

            stmt = Jimple.v().newAssignStmt(fr, Util.v().getLocalForStackOp(listBody, typeStack,
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

            Type fieldType = Util.v().jimpleTypeOfFieldDescriptor(fieldDescriptor);
            
            SootClass bclass = cm.getSootClass(className);
            SootFieldRef fieldRef = Scene.v().makeFieldRef(bclass, fieldName, fieldType, true);

            fr = Jimple.v().newStaticFieldRef(fieldRef);

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), fr);
            break;
         }

         case ByteCode.INVOKEDYNAMIC:
         {
            Instruction_Invokedynamic iv = (Instruction_Invokedynamic)ins;
   	      	CONSTANT_InvokeDynamic_info iv_info = (CONSTANT_InvokeDynamic_info) constant_pool[iv.invoke_dynamic_index];
            args = cp_info.countParams(constant_pool,iv_info.name_and_type_index);
             
			SootMethodRef bootstrapMethodRef;
			List bootstrapArgs = new LinkedList();
			{
				short[] bootstrapMethodTable = bootstrap_methods_attribute.method_handles;
				short methodSigIndex = bootstrapMethodTable[iv_info.bootstrap_method_index];
				CONSTANT_MethodHandle_info mhInfo = (CONSTANT_MethodHandle_info) constant_pool[methodSigIndex];
				CONSTANT_Methodref_info bsmInfo = (CONSTANT_Methodref_info) constant_pool[mhInfo.target_index];
				bootstrapMethodRef = createMethodRef(constant_pool, bsmInfo,
						false);

				short[] bsmArgIndices = bootstrap_methods_attribute.arg_indices[iv_info.bootstrap_method_index];
				if (bsmArgIndices.length > 0) {
					// G.v().out.println("Soot does not currently support static arguments to bootstrap methods. They will be stripped.");
					for (short bsmArgIndex : bsmArgIndices) {
					      cp_info cpEntry = constant_pool[bsmArgIndex];
					      Value val = cpEntry.createJimpleConstantValue(constant_pool);
					      bootstrapArgs.add(val);
					}
				}
			}			 
			 
        	 SootMethodRef methodRef = null;

        	 CONSTANT_NameAndType_info nameAndTypeInfo = (CONSTANT_NameAndType_info) constant_pool[iv_info.name_and_type_index];
        	 
        	 String methodName = ((CONSTANT_Utf8_info) (constant_pool[nameAndTypeInfo.name_index])).convert();
        	 String methodDescriptor = ((CONSTANT_Utf8_info) (constant_pool[nameAndTypeInfo.descriptor_index])).
        	 convert();

        	 SootClass bclass = cm.getSootClass(SootClass.INVOKEDYNAMIC_DUMMY_CLASS_NAME);
        	 
        	 List parameterTypes;
        	 Type returnType;

        	 // Generate parameters & returnType & parameterTypes
        	 {
        		 Type[] types = Util.v().jimpleTypesOfFieldOrMethodDescriptor(methodDescriptor);

        		 parameterTypes = new ArrayList();

        		 for(int k = 0; k < types.length - 1; k++)
        		 {
        			 parameterTypes.add(types[k]);
        		 }

        		 returnType = types[types.length - 1];
        	 }
        	 //we always model invokeDynamic method refs as static method references of methods on the type SootClass.INVOKEDYNAMIC_DUMMY_CLASS_NAME
        	 methodRef = Scene.v().makeMethodRef(bclass, methodName, parameterTypes, returnType, true);

        	 // build Vector of parameters
        	 params = new Value[args];
        	 for (int j=args-1;j>=0;j--)
        	 {
        		 params[j] = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex());

        		 if(typeSize(typeStack.top()) == 2)
        		 {
        			 typeStack = typeStack.pop();
        			 typeStack = typeStack.pop();
        		 }
        		 else
        			 typeStack = typeStack.pop();
        	 }

        	 rvalue = Jimple.v().newDynamicInvokeExpr(bootstrapMethodRef, bootstrapArgs,
        			 methodRef, Arrays.asList(params));

        	 if(!returnType.equals(VoidType.v()))
        	 {
        		 stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
        				 postTypeStack.topIndex()),rvalue);
        	 }
        	 else
        		 stmt = Jimple.v().newInvokeStmt(rvalue);

        	 break;
         }  	 
 
         case ByteCode.INVOKEVIRTUAL:
         {
            Instruction_Invokevirtual iv = (Instruction_Invokevirtual)ins;
            args = cp_info.countParams(constant_pool,iv.arg_i);

    		CONSTANT_Methodref_info methodInfo =
    		    (CONSTANT_Methodref_info) constant_pool[iv.arg_i];

            SootMethodRef methodRef = createMethodRef(constant_pool, methodInfo, false);

            Type returnType = methodRef.returnType();
            // build array of parameters
                params = new Value[args];
                for (int j=args-1;j>=0;j--)
                {
                   params[j] = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex());

                   if(typeSize(typeStack.top()) == 2)
                   {
                      typeStack = typeStack.pop();
                      typeStack = typeStack.pop();
                   }
                   else
                      typeStack = typeStack.pop();
                }

            rvalue = Jimple.v().newVirtualInvokeExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), methodRef, Arrays.asList(params));

            if(!returnType.equals(VoidType.v()))
            {
                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex()),rvalue);
            }
            else
               stmt = Jimple.v().newInvokeStmt(rvalue);
            break;
        }

        case ByteCode.INVOKENONVIRTUAL:
         {
            Instruction_Invokenonvirtual iv = (Instruction_Invokenonvirtual)ins;
            args = cp_info.countParams(constant_pool,iv.arg_i);

            CONSTANT_Methodref_info methodInfo =
            	(CONSTANT_Methodref_info) constant_pool[iv.arg_i];

            SootMethodRef methodRef = createMethodRef(constant_pool, methodInfo, false);

            Type returnType = methodRef.returnType();

            // build array of parameters
                params = new Value[args];
                for (int j=args-1;j>=0;j--)
                {
                   params[j] = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex());

                   if(typeSize(typeStack.top()) == 2)
                   {
                      typeStack = typeStack.pop();
                      typeStack = typeStack.pop();
                   }
                   else
                      typeStack = typeStack.pop();
                }

            rvalue = Jimple.v().newSpecialInvokeExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), methodRef, Arrays.asList(params));

            if(!returnType.equals(VoidType.v()))
            {
                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex()), rvalue);
            }
            else
                stmt = Jimple.v().newInvokeStmt(rvalue);
            break;
        }

         case ByteCode.INVOKESTATIC:
         {
            Instruction_Invokestatic is = (Instruction_Invokestatic)ins;
            args = cp_info.countParams(constant_pool,is.arg_i);

            CONSTANT_Methodref_info methodInfo =
            	(CONSTANT_Methodref_info) constant_pool[is.arg_i];

            SootMethodRef methodRef = createMethodRef(constant_pool, methodInfo, true);

            Type returnType = methodRef.returnType();

            // build Vector of parameters
                   params = new Value[args];
                for (int j=args-1;j>=0;j--)
                {
                    /* G.v().out.println("BeforeTypeStack");
                    typeStack.print(G.v().out);

                    G.v().out.println("AfterTypeStack");
                    postTypeStack.print(G.v().out);
                    */

                   params[j] = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex());

                   if(typeSize(typeStack.top()) == 2)
                   {
                      typeStack = typeStack.pop();
                      typeStack = typeStack.pop();
                   }
                   else
                      typeStack = typeStack.pop();
                }

            rvalue = Jimple.v().newStaticInvokeExpr(methodRef, Arrays.asList(params));

            if(!returnType.equals(VoidType.v()))
            {
                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex()),rvalue);
            }
            else
               stmt = Jimple.v().newInvokeStmt(rvalue);

            break;
         }

         case ByteCode.INVOKEINTERFACE:
         {
            Instruction_Invokeinterface ii = (Instruction_Invokeinterface)ins;
            args = cp_info.countParams(constant_pool,ii.arg_i);

            CONSTANT_InterfaceMethodref_info methodInfo =
                (CONSTANT_InterfaceMethodref_info) constant_pool[ii.arg_i];

            SootMethodRef methodRef = createMethodRef(constant_pool, methodInfo, false);

            Type returnType = methodRef.returnType();

            // build Vector of parameters
                params = new Value[args];
                for (int j=args-1;j>=0;j--)
                {
                   params[j] = Util.v().getLocalForStackOp(listBody, typeStack, typeStack.topIndex());

                   if(typeSize(typeStack.top()) == 2)
                   {
                      typeStack = typeStack.pop();
                      typeStack = typeStack.pop();
                   }
                   else
                      typeStack = typeStack.pop();
                }

            rvalue = Jimple.v().newInterfaceInvokeExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), methodRef, Arrays.asList(params));

            if(!returnType.equals(VoidType.v()))
            {
                stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                    postTypeStack.topIndex()), rvalue);
            }
            else
               stmt = Jimple.v().newInvokeStmt(rvalue);
            break;
        }

         case ByteCode.ATHROW:
            stmt = Jimple.v().newThrowStmt(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));
            break;

         case ByteCode.NEW:
         {
            SootClass bclass = cm.getSootClass(getClassName(constant_pool,
                ((Instruction_New)ins).arg_i));

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()), Jimple.v().newNewExpr(RefType.v(bclass.getName())));
            break;
         }

         case ByteCode.CHECKCAST:
         {
            String className = getClassName(constant_pool, ((Instruction_Checkcast)ins).arg_i);

            Type castType;

            if(className.startsWith("["))
                castType = Util.v().jimpleTypeOfFieldDescriptor(getClassName(constant_pool,
                    ((Instruction_Checkcast)ins).arg_i));
            else
                castType = RefType.v(className);

            rhs = Jimple.v().newCastExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), castType);

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()),rhs);
            break;
         }

         case ByteCode.INSTANCEOF:
         {
            Type checkType;

            String className = getClassName(constant_pool, ((Instruction_Instanceof)ins).arg_i);

            if(className.startsWith("["))
                checkType = Util.v().jimpleTypeOfFieldDescriptor(getClassName(constant_pool,
                ((Instruction_Instanceof)ins).arg_i));
            else
                checkType = RefType.v(className);

            rhs = Jimple.v().newInstanceOfExpr(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()), checkType);

            stmt = Jimple.v().newAssignStmt(Util.v().getLocalForStackOp(listBody, postTypeStack,
                postTypeStack.topIndex()),rhs);
            break;
         }

         case ByteCode.MONITORENTER:
            stmt = Jimple.v().newEnterMonitorStmt(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));
            break;
         case ByteCode.MONITOREXIT:
            stmt = Jimple.v().newExitMonitorStmt(Util.v().getLocalForStackOp(listBody, typeStack,
                typeStack.topIndex()));
            break;

         default:
            throw new RuntimeException("Unrecognized bytecode instruction: " + x);
        }

      if(stmt != null) {
	if (Options.v().keep_offset()) {
	  stmt.addTag(new BytecodeOffsetTag(ins.label));
	}
        statements.add(stmt);
      }
   }

    private SootMethodRef createMethodRef(cp_info[] constant_pool,
			ICONSTANT_Methodref_info methodInfo, boolean isStatic) {
		SootMethodRef methodRef;

		CONSTANT_Class_info c =
		    (CONSTANT_Class_info) constant_pool[methodInfo.getClassIndex()];

		String className = ((CONSTANT_Utf8_info) (constant_pool[c.name_index])).convert();
		    className = className.replace('/', '.');

		CONSTANT_NameAndType_info i =
		    (CONSTANT_NameAndType_info) constant_pool[methodInfo.getNameAndTypeIndex()];

		String methodName = ((CONSTANT_Utf8_info) (constant_pool[i.name_index])).convert();
		String methodDescriptor = ((CONSTANT_Utf8_info) (constant_pool[i.descriptor_index])).
		    convert();

         if (className.charAt(0) == '[')
		   className = "java.lang.Object";

		SootClass bclass = cm.getSootClass(className);

		List parameterTypes;
		Type returnType;
		// Generate parameters & returnType & parameterTypes
		{
		    Type[] types = Util.v().jimpleTypesOfFieldOrMethodDescriptor(methodDescriptor);

		    parameterTypes = new ArrayList();

		    for(int k = 0; k < types.length - 1; k++)
		    {
		        parameterTypes.add(types[k]);
		    }

		    returnType = types[types.length - 1];
		}

		methodRef = Scene.v().makeMethodRef(bclass, methodName, parameterTypes, returnType, isStatic);
		return methodRef;
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
