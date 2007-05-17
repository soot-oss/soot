/****************************************
 * 
 * Copyright (c) 2006, University of California, Berkeley.
 * All rights reserved.
 *
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions 
 * are met:
 *
 * - Redistributions of source code must retain the above copyright 
 *   notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright 
 *   notice, this list of conditions and the following disclaimer in the 
 *   documentation and/or other materials provided with the 
 *   distribution.
 * - Neither the name of the University of California, Berkeley nor the 
 *   names of its contributors may be used to endorse or promote 
 *   products derived from this software without specific prior written 
 *   permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS 
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 ***************************************/ 

package soot.jimple.spark.pag;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;




import soot.SootField;
import soot.SootMethod;
import soot.jimple.spark.ondemand.genericutil.Predicate;
import soot.jimple.spark.sets.P2SetVisitor;

/**
 * @author lexinshan
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class PagToDotDumper {

	public static final int TRACE_MAX_LVL = 99;
	private PAG pag;

	private PrintStream ps;

	private Node curNode;

	private HashMap<Node, Node[]> vmatches;

	private HashMap<Node, Node[]> invVmatches;

	public PagToDotDumper(PAG pag) {
		this.pag = pag;
		this.vmatches = new HashMap<Node, Node[]>();
		this.invVmatches = new HashMap<Node, Node[]>();
	}
	
	public void debugEmptyP2Sets() {
		traceNode("java.util.Hashtable$ValueCollection", "contains","r0");

		if (false) {
			for (Iterator iter = pag.getVarNodeNumberer().iterator(); iter
					.hasNext();) {
				VarNode vNode = (VarNode) iter.next();

				if (vNode.getP2Set().isEmpty()) {
					System.err.println(vNode);
					System.err.println(vNode.getP2Set());
				}
			}
		}
	}

	/**
	 * Build vmatchEdges and store them in vmatches field
	 *  
	 */
	private void buildVmatchEdges() {
		// for each store and load pair
		for (Iterator iter = pag.loadSourcesIterator(); iter.hasNext();) {
			final FieldRefNode frn1 = (FieldRefNode) iter.next();
			for (Iterator iter2 = pag.storeInvSourcesIterator(); iter2
					.hasNext();) {
				final FieldRefNode frn2 = (FieldRefNode) iter2.next();
				VarNode base1 = frn1.getBase();
				VarNode base2 = frn2.getBase();


//				debug(frn1, frn2, base1, base2);

				// if they store & load the same field
				if (frn1.getField().equals(frn2.getField())) {

					if (base1.getP2Set().hasNonEmptyIntersection(
							base2.getP2Set())) {

						//						System.err.println("srcs:");
						Node[] src = pag.loadLookup(frn1);
						Node[] dst = pag.storeInvLookup(frn2);

						for (int i = 0; i < src.length; i++) {
							//														System.err.println(src[i]);
							vmatches.put(src[i], dst);
						}
						//						System.err.println("dst:");
						for (int i = 0; i < dst.length; i++) {
							//														System.err.println(dst[i]);
							invVmatches.put(dst[i], src);
						}

					}
				}

			}
		}
	}

	/**
	 * @param frn1
	 * @param frn2
	 * @param base1
	 * @param base2
	 * @param lvn
	 * @param mName
	 *            TODO
	 */
	@SuppressWarnings("unused")
  private void debug(final FieldRefNode frn1, final FieldRefNode frn2,
			VarNode base1, VarNode base2) {
		if (base1 instanceof LocalVarNode && base2 instanceof LocalVarNode) {
			LocalVarNode lvn1 = (LocalVarNode) base1;
			LocalVarNode lvn2 = (LocalVarNode) base2;
			if (lvn1.getMethod().getDeclaringClass().getName().equals(
					"java.util.Hashtable$ValueCollection")
					&& lvn1.getMethod().getName().equals("contains")
					&& lvn2.getMethod().getDeclaringClass().getName().equals(
					"java.util.Hashtable$ValueCollection")
					&& lvn2.getMethod().getName().equals("<init>")
					) {
				System.err.println("Method: " + lvn1.getMethod().getName());
				System.err.println(makeLabel(frn1));
				System.err.println("Base: " + base1.getVariable());
				System.err.println("Field: " + frn1.getField());
				System.err.println(makeLabel(frn2));
				System.err.println("Base: " + base2.getVariable());
				System.err.println("Field: " + frn2.getField());
				
				if (frn1.getField().equals(frn2.getField())) {
					System.err.println("field match");
					if (base1.getP2Set().hasNonEmptyIntersection(
							base2.getP2Set())) {
						System.err.println("non empty");
					} else {
						System.err.println("b1: " + base1.getP2Set());
						System.err.println("b2: " + base2.getP2Set());
					}
				}
			}
		}

	}

	/**
	 * @param lvNode
	 * @param node
	 * @return
	 */
	public static String translateEdge(Node src, Node dest, String label) {
		return makeNodeName(src) + " -> " + makeNodeName(dest) + " [label=\""
				+ label + "\"];";
	}

	private final static Predicate<Node> emptyP2SetPred = new Predicate<Node>() {
        public boolean test(Node n) {
            return !(n instanceof AllocNode) && n.getP2Set().isEmpty();
        }
    };
	/**
	 * @param node
	 * @return
	 */
	public static String translateLabel(Node n, Predicate<Node> p) {
		String color = "";
		String label;

		if (p.test(n))
			color = ", color=red";
		if (n instanceof LocalVarNode) {
			label = makeLabel((LocalVarNode) n);
		} else if (n instanceof AllocNode) {
			label = makeLabel((AllocNode) n);
		} else if (n instanceof FieldRefNode) {
			label = makeLabel((FieldRefNode) n);
		} else {
			label = n.toString();
		}
		return makeNodeName(n) + "[label=\"" + label + "\"" + color + "];";

	}

	private static String translateLabel(Node n) { 
	    return translateLabel(n, emptyP2SetPred);
	}
	/**
	 * @param lvNode
	 * @param cName
	 * @param mName
	 * @return
	 */
	private boolean isDefinedIn(LocalVarNode lvNode, String cName, String mName) {
		return lvNode.getMethod() != null
				&& lvNode.getMethod().getDeclaringClass().getName().equals(
						cName) && lvNode.getMethod().getName().equals(mName);
	}

	public static String format(LocalVarNode v) {
		return v.getVariable().toString();
	}

	public void printOneNodeByID(int id) {
		buildVmatchEdges();

		for (Iterator iter = pag.getVarNodeNumberer().iterator(); iter
				.hasNext();) {
			Node n = (Node) iter.next();

			if (n.getNumber() == id) {
				printOneNode((VarNode) n);
			}
		}
		
	}
	
	public void printOneAllocNodeByID(int id) {
		for (Iterator iter = pag.getAllocNodeNumberer().iterator(); iter.hasNext(); ) {
			final AllocNode n = (AllocNode) iter.next();
			if (n.getNumber() == id) {
				System.err.println(id + ": " + n.getNewExpr().toString());	
			}
		}
	}

	private void printOneNode(VarNode node) {
		PrintStream ps = System.err;

		ps.println(makeLabel(node));
		Node[] succs = pag.simpleInvLookup(node);
		ps.println("assign");
		ps.println("======");
		for (int i = 0; i < succs.length; i++) {
			ps.println(succs[i]);
		}

		succs = pag.allocInvLookup(node);
		ps.println("new");
		ps.println("======");
		for (int i = 0; i < succs.length; i++) {
			ps.println(succs[i]);

		}

		
		succs = pag.loadInvLookup(node);
		ps.println("load");
		ps.println("======");
		for (int i = 0; i < succs.length; i++) {
			ps.println(succs[i]);
		}

		succs = pag.storeLookup(node);
		ps.println("store");
		ps.println("======");
		for (int i = 0; i < succs.length; i++) {
			ps.println(succs[i]);
		}

	}

	public void dumpP2Set(String fName, String cName, String mName) {

		PrintStream ps;

		try {
			FileOutputStream fos = new FileOutputStream(new File(fName));
			ps = new PrintStream(fos);
			ps.println("digraph G {");

			dumpLocalP2Set(cName, mName, ps);

			ps.print("}");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void dumpLocalP2Set(String cName, String mName, final PrintStream ps) {

		this.ps = ps;
		for (Iterator iter = pag.getVarNodeNumberer().iterator(); iter
				.hasNext();) {
			VarNode vNode = (VarNode) iter.next();

			if (vNode instanceof LocalVarNode) {
				final LocalVarNode lvNode = (LocalVarNode) vNode;
				curNode = lvNode;

				if (lvNode.getMethod() != null
						&& lvNode.getMethod().getName().equals(mName)) {

					ps.println("\t" + makeNodeName(lvNode) + " [label=\""
							+ makeLabel(lvNode) + "\"];");
					lvNode.getP2Set().forall(new P2SetToDotPrinter());

				}
			}
		}
	}

	public void dumpPAG(String cName, String mName) {
		String mName2 = mName;
		if (mName.indexOf('<') == 0)
			mName2 = mName.substring(1, mName.length() - 1);
		dumpPAG(cName + "." + mName2 + ".dot", cName, mName);
	}

	public void dumpPAG(String fName, String cName, String mName) {
		PrintStream ps;

		try {
			FileOutputStream fos = new FileOutputStream(new File(fName));
			ps = new PrintStream(fos);
			ps.println("digraph G {");
			ps.println("\trankdir=LR;");
			dumpLocalPAG(cName, mName, ps);

			ps.print("}");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void dumpLocalPAG(String cName, String mName, final PrintStream ps) {
//		this.queryMethod = mName;
		// iterate over all variable nodes
		for (Iterator iter = pag.getVarNodeNumberer().iterator(); iter
				.hasNext();) {
			final Node node = (Node) iter.next();

			if (!(node instanceof LocalVarNode))
				continue;

			final LocalVarNode lvNode = (LocalVarNode) node;

			// nodes that is defined in the specified class and method
			if (isDefinedIn(lvNode, cName, mName)) {

				dumpForwardReachableNodesFrom(lvNode, ps);

			}
		}

		//		for (Iterator iter = pag.getFieldRefNodeNumberer().iterator(); iter
		//				.hasNext();) {
		//			final FieldRefNode frNode = (FieldRefNode) iter.next();
		//			
		//			if (frNode.getBase().)
		//			Node[] succs = pag.storeInvLookup(frNode);
		//			for (int i = 0; i < succs.length; i++) {
		//				ps.println("\t" + translateLabel(succs[i]));
		//				// print edge
		//				ps.println("\t" + translateEdge(frNode, succs[i], "store"));
		//			}
		//		}

	}

	/**
	 * @param lvNode
	 * @param ps
	 */
	private void dumpForwardReachableNodesFrom(final LocalVarNode lvNode,
			final PrintStream ps) {
		ps.println("\t" + translateLabel(lvNode));

		Node[] succs = pag.simpleInvLookup(lvNode);
		for (int i = 0; i < succs.length; i++) {
			ps.println("\t" + translateLabel(succs[i]));
			// print edge
			ps.println("\t" + translateEdge(lvNode, succs[i], "assign"));
		}

		succs = pag.allocInvLookup(lvNode);
		for (int i = 0; i < succs.length; i++) {
			ps.println("\t" + translateLabel(succs[i]));
			// print edge
			ps.println("\t" + translateEdge(lvNode, succs[i], "new"));
		}

		succs = pag.loadInvLookup(lvNode);
		for (int i = 0; i < succs.length; i++) {
			final FieldRefNode frNode = (FieldRefNode) succs[i];
			ps.println("\t" + translateLabel(frNode));
			ps.println("\t" + translateLabel(frNode.getBase()));

			// print edge
			ps.println("\t" + translateEdge(lvNode, frNode, "load"));
			ps.println("\t"
					+ translateEdge(frNode, frNode.getBase(), "getBase"));

		}

		succs = pag.storeLookup(lvNode);

		for (int i = 0; i < succs.length; i++) {
			final FieldRefNode frNode = (FieldRefNode) succs[i];
			ps.println("\t" + translateLabel(frNode));
			ps.println("\t" + translateLabel(frNode.getBase()));
			// print edge
			ps.println("\t" + translateEdge(frNode, lvNode, "store"));
			ps.println("\t"
					+ translateEdge(frNode, frNode.getBase(), "getBase"));
		}
	}

	public void traceNode(int id) {
		buildVmatchEdges();
		String fName = "trace." + id + ".dot";
		try {
			FileOutputStream fos = new FileOutputStream(new File(fName));
			ps = new PrintStream(fos);
			ps.println("digraph G {");

			// iterate over all variable nodes
			for (Iterator iter = pag.getVarNodeNumberer().iterator(); iter
					.hasNext();) {
				final VarNode n = (VarNode) iter.next();

				if (n.getNumber() == id) {
					LocalVarNode lvn = (LocalVarNode) n;

					printOneNode(lvn);

					trace(lvn, ps, new HashSet<Node>(), TRACE_MAX_LVL);
				}
			}
			
			ps.print("}");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void traceNode(String cName, String mName, String varName) {
		String mName2 = mName;
		if (mName.indexOf('<') == 0)
			mName2 = mName.substring(1, mName.length() - 1);

		traceLocalVarNode("trace." + cName + "." + mName2 + "." + varName
				+ ".dot", cName, mName, varName);
	}

	public void traceLocalVarNode(String fName, String cName, String mName,
			String varName) {
		PrintStream ps;

		buildVmatchEdges();

		try {
			FileOutputStream fos = new FileOutputStream(new File(fName));
			ps = new PrintStream(fos);
			ps.println("digraph G {");

			// iterate over all variable nodes
			for (Iterator iter = pag.getVarNodeNumberer().iterator(); iter
					.hasNext();) {
				final VarNode n = (VarNode) iter.next();

				if (!(n instanceof LocalVarNode))
					continue;
				LocalVarNode lvn = (LocalVarNode) n;
				// HACK
				if (lvn.getMethod() == null)
					continue;
				if (isDefinedIn(lvn, cName, mName)) {
					//	System.err.println("class match");
					//	System.err.println(lvn.getVariable());
					if (lvn.getVariable().toString().equals(varName)) {
						// System.err.println(lvn);

						trace(lvn, ps, new HashSet<Node>(), 10);
					}
				}

			}
			ps.print("}");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Do a DFS traversal
	 * 
	 * @param name
	 * @param name2
	 * @param ps2
	 */
	private void trace(VarNode node, PrintStream ps, HashSet<Node> visitedNodes, int level) {
		if (level < 1)
			return;
		ps.println("\t" + translateLabel(node));

//		// assign value to others
//		Node[] preds = pag.storeLookup(node);
//		for (int i = 0; i < preds.length; i++) {
//			if (visitedNodes.contains(preds[i]))
//				continue;
//			ps.println("\t" + translateLabel(preds[i]));
//			// print edge
//			ps.println("\t" + translateEdge(preds[i], node, "store"));
//			visitedNodes.add(preds[i]);
//			//			trace((VarNode) preds[i], ps, visitedNodes);
//		}

		// get other's value
		Node[] succs = pag.simpleInvLookup(node);
		for (int i = 0; i < succs.length; i++) {
			if (visitedNodes.contains(succs[i]))
				continue;
			ps.println("\t" + translateLabel(succs[i]));
			// print edge
			ps.println("\t" + translateEdge(node, succs[i], "assign"));
			visitedNodes.add(succs[i]);
			trace((VarNode) succs[i], ps, visitedNodes, level-1);
		}

		succs = pag.allocInvLookup(node);
		for (int i = 0; i < succs.length; i++) {
			if (visitedNodes.contains(succs[i]))
				continue;
			ps.println("\t" + translateLabel(succs[i]));
			// print edge
			ps.println("\t" + translateEdge(node, succs[i], "new"));
		}

		succs = vmatches.get(node);
		if (succs != null) {
			//			System.err.println(succs.length);
			for (int i = 0; i < succs.length; i++) {
				//				System.err.println(succs[i]);
				if (visitedNodes.contains(succs[i]))
					continue;
				ps.println("\t" + translateLabel(succs[i]));
				// print edge
				ps.println("\t" + translateEdge(node, succs[i], "vmatch"));
				trace((VarNode) succs[i], ps, visitedNodes, level-1);
			}
		}
		//		succs = pag.loadInvLookup(node);
		//		for (int i = 0; i < succs.length; i++) {
		//			if (visitedNodes.contains(succs[i]))
		//				continue;
		//			final FieldRefNode frNode = (FieldRefNode) succs[i];
		//			// ps.println("\t" + translateLabel(frNode));
		//			// print edge
		//			ps.println("\t"
		//					+ translateEdge(node, frNode.getBase(), "getfield\\n"
		//							+ frNode.getField()));
		//			visitedNodes.add(frNode.getBase());
		//			trace(frNode.getBase(), ps, visitedNodes);
		//		}
		//
		//		succs = pag.storeLookup(node);
		//
		//		for (int i = 0; i < succs.length; i++) {
		//			if (visitedNodes.contains(succs[i]))
		//				continue;
		//			final FieldRefNode frNode = (FieldRefNode) succs[i];
		//			// ps.println("\t" + translateLabel(frNode.getBase()));
		//			// print edge
		//			ps.println("\t"
		//					+ translateEdge(frNode.getBase(), node, "putfield\\n"
		//							+ frNode.getField()));
		//
		//			visitedNodes.add(frNode.getBase());
		//			trace(frNode.getBase(), ps, visitedNodes);
		//		}

	}

	public static String makeNodeName(Node n) {
		return "node_" + n.getNumber();
	}

	public static String makeLabel(AllocNode n) {
		return n.getNewExpr().toString();
	}

	public static String makeLabel(LocalVarNode n) {
		SootMethod sm = n.getMethod();
		return "LV " + n.getVariable().toString() + " " + n.getNumber() + "\\n"
				+ sm.getDeclaringClass() + "\\n" + sm.getName();
	}

	/**
	 * @param node
	 * @return
	 */
	public static String makeLabel(FieldRefNode node) {
		if (node.getField() instanceof SootField) {
			final SootField sf = (SootField) node.getField();
			return "FNR " + makeLabel(node.getBase()) + "." + sf.getName();
		} else
			return "FNR " + makeLabel(node.getBase()) + "." + node.getField();
	}

	/**
	 * @param base
	 * @return
	 */
	public static String makeLabel(VarNode base) {
		if (base instanceof LocalVarNode)
			return makeLabel((LocalVarNode) base);
		else
			return base.toString();
	}
	
	

	class P2SetToDotPrinter extends P2SetVisitor {

		public void visit(Node n) {
			ps.println("\t" + makeNodeName(n) + " [label=\""
					+ makeLabel((AllocNode) n) + "\"];");

			ps.print("\t" + makeNodeName(curNode) + " -> ");
			ps.println(makeNodeName(n) + ";");
		}

	}

}