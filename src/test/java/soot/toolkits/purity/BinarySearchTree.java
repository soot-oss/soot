package soot.toolkits.purity;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

/**
 * This example is from the article A Combined Pointer and Purity Analysis for
 * Java Programs" by Alexandru Salcianu and Martin Rinard.
 * It is supposed to demonstrate the purity analysis (-annot-purity)
 *
 * by Antoine Mine, 2005/02/08
 */

import java.util.*;

public class BinarySearchTree {
    Node root;
    int size;

    static class Node {
	Node left;
	Node right;
	Comparable info;
    }

    static final class Wrapper {
	Object o;
	Wrapper(Object o) {
	    this.o = o;
	}
	public boolean equals(Object o) {
	    if (!(o instanceof Wrapper)) return false;
	    return this.o == ((Wrapper)o).o;
	}
	public int hashCode() {
	    return System.identityHashCode(o);
	}
    }

    boolean repOk() {
	if (root==null) return size==0;
	if (!isTree()) return false;
	if (numNodes(root)!=size) return false;
	if (!isOrdered(root,null,null)) return false;
	return true;
    }

    boolean isTree() {
	Set visited = new HashSet();
	visited.add(new Wrapper(root));
	LinkedList workList = new LinkedList();
	workList.add(root);
	while (!workList.isEmpty()) {
	    Node current = (Node)workList.removeFirst();
	    if (current.left!=null) {
		if (!visited.add(new Wrapper(current.left))) return false;
		workList.add(current.left);
	    }
	    if (current.right!=null) {
		if (!visited.add(new Wrapper(current.right))) return false;
		workList.add(current.right);
	    }
	}
	return true;
    }

    int numNodes(Node n) {
	if (n==null) return 0;
	return 1 + numNodes(n.left) + numNodes(n.right);
    }

    boolean isOrdered(Node n, Comparable min, Comparable max) {
	if ((min!=null && n.info.compareTo(min)<=0) ||
	    (max!=null && n.info.compareTo(max)>=0)) return false;
	if (n.left!=null)
	    if (!isOrdered(n.left,min,n.info)) return false;
	if (n.right!=null)
	    if (!isOrdered(n.right,n.info,max)) return false;
	return true;
    }

    static Node create(int i) {
	if (i==0) return null;
	Node n = new Node();
	n.left  = create(i-1);
	n.right = create(i-1);
	return n;
    }

    public static void main(String[] args) {
	BinarySearchTree x = new BinarySearchTree();
	x.root = create(5);
	x.repOk();
    }

}
