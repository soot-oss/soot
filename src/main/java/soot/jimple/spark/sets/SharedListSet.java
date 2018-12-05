package soot.jimple.spark.sets;

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

import soot.Type;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.util.BitVector;

/*
 * Reference counting was amazingly difficult to get right, for the number of lines of
 * code it makes up.
 * Reference counting keeps track of how many things are pointing to a ListNode.  When
 * it has no more things pointing to it, it needs to be deleted from the HashMap.
 *
 * I think I finally got the correct algorithm for when to increase and when to decrease
 * a node's reference count.  It is:
 * -When a new node is created (in makeNode), its "next" pointer will have an extra thing
 * pointing at it, so increase its reference count.
 * -When a new top-level pointer (a "SharedListSet.data member) is assigned to a node, it
 * has one extra thing pointing at it, so increase its reference count.
 * -Reference count decreases when a chain is detached.  Detachment is bit of a complicated
 * process; it should be described in my thesis.
 *
 */

//Can't use java.lang.ref.WeakReferences instead because:
//The HashMap will now map WeakReferences (containing
//Pairs) to ListNodes, so its object will be null.  But will it then hash to the same
//value after its object has become null?
//And it still seems like I'd have to remove the WeakReferences from the HashMap whose references
//have become null.

/*
 * Ideas to speed things up:
 * -For contains(), first check the index of all nodes to see whether the node exists
 * in *any* points-to set.
 * -I don't think this one will be very fast because if 2 lists are similar but differ in an
 * element at the end of them, they can't be shared.
 */
/*
List is sorted.
Therefore:
contains: O(n)
add: O(n), and might add things to other lists too

 */

/**
 * Implementation of a points-to set as a sorted list of elements, but where similar lists share parts of their data.
 */
public class SharedListSet extends PointsToSetInternal {
  public SharedListSet(Type type, PAG pag) {
    super(type);
    this.pag = pag;
  }

  // Ripped from the other points-to sets - returns a factory that can be
  // used to construct SharedHybridSets
  public final static P2SetFactory getFactory() {
    return new P2SetFactory() {
      public final PointsToSetInternal newSet(Type type, PAG pag) {
        return new SharedListSet(type, pag);
      }
    };
  }

  public boolean contains(Node n) {
    for (ListNode i = data; i != null; i = i.next) {
      if (i.elem == n) {
        return true;
      }
    }
    return false;
  }

  public boolean isEmpty() {
    return data == null;
  }

  public boolean forall(P2SetVisitor v) {
    for (ListNode i = data; i != null; i = i.next) {
      v.visit(i.elem);
    }
    return v.getReturnValue();
  }

  private ListNode advanceExclude(ListNode exclude, ListNode other) {
    // If exclude's node is less than other's first node, then there
    // are elements to exclude that aren't in other, so keep advancing exclude
    final int otherNum = other.elem.getNumber();
    while (exclude != null && exclude.elem.getNumber() < otherNum) {
      exclude = exclude.next;
    }
    return exclude;
  }

  private boolean excluded(ListNode exclude, ListNode other, BitVector mask) {
    return (exclude != null && other.elem == exclude.elem) || (mask != null && (!(mask.get(other.elem.getNumber()))));
  }

  private ListNode union(ListNode first, ListNode other, ListNode exclude, BitVector mask, boolean detachChildren) {
    // This algorithm must be recursive because we don't know whether to detach until
    // we know the rest of the list.

    if (first == null) {
      if (other == null) {
        return null;
      }

      // Normally, we could just return other in this case.
      // But the problem is that there might be elements to exclude from other.
      // We also can't modify other and remove elements from it, because that would
      // remove elements from the points-to set whose elements are being added to this
      // one.
      // So we have to create a new list from scratch of the copies of the elements
      // of other.
      if (exclude == null && mask == null) {
        return makeNode(other.elem, other.next);
        // Can't just do:
        // return other;
        // because of the reference counting going on. (makeNode might increment
        // the reference count.)
      } else {
        exclude = advanceExclude(exclude, other);
        if (excluded(exclude, other, mask))
        // If the first element of other is to be excluded
        {
          return union(first, other.next, exclude, mask, detachChildren);
        } else {
          return makeNode(other.elem, union(first, other.next, exclude, mask, detachChildren));
        }
      }
    } else if (other == null) {
      return first;
    } else if (first == other) {
      // We've found sharing - don't need to add any more
      return first; // Doesn't matter what's being excluded, since it's all in first
    } else {
      ListNode retVal;
      if (first.elem.getNumber() > other.elem.getNumber()) {
        // Adding a new node, other.elem. But we might not have to add it if
        // it's to be excluded.

        exclude = advanceExclude(exclude, other);
        if (excluded(exclude, other, mask))
        // If the first element of other is to be excluded
        {
          retVal = union(first, other.next, exclude, mask, detachChildren);
        } else {
          retVal = makeNode(other.elem, union(first, other.next, exclude, mask, detachChildren));
        }
      } else {
        if (first.refCount > 1) {
          // if we're dealing with a shared node, stop detaching.
          detachChildren = false;
        }
        // Doesn't matter whether it's being excluded; just add it once and advance
        // both lists to the next node
        if (first.elem == other.elem) {
          // if both lists contain the element being added, only add it once
          other = other.next;
        }
        retVal = makeNode(first.elem, union(first.next, other, exclude, mask, detachChildren));
        if (detachChildren && first != retVal && first.next != null) {
          first.next.decRefCount(); // When we advance "first" and copy its node into the
          // output list, the old "first" will eventually be thrown away (unless other
          // stuff points to it).
        }
      }
      return retVal;
    }

  }

  // Common function to prevent repeated code in add and addAll
  private boolean addOrAddAll(ListNode first, ListNode other, ListNode exclude, BitVector mask) {
    ListNode result = union(first, other, exclude, mask, true);
    if (result == data) {
      return false;
    } else {
      // result is about to have an extra thing pointing at it, and data is about to
      // have one less thing pointing at it, so adjust the reference counts.
      result.incRefCount();
      if (data != null) {
        data.decRefCount();
      }
      data = result;
      return true;
    }
  }

  public boolean add(Node n) {

    // Prevent repeated code by saying add() is just a union() with one element in the
    // set being merged in. add isn't called frequently anyway.
    // However, we have to call makeNode() to get the node, in case it was already there
    // and because union() assumes "other" is an existing node in another set. So we
    // create the node for the duration of the call to addOrAddAll(), after which we
    // delete it unless it was already there
    ListNode other = makeNode(n, null);
    other.incRefCount();
    boolean added = addOrAddAll(data, other, null, null);
    other.decRefCount(); // undo its creation
    return added;
  }

  public boolean addAll(PointsToSetInternal other, PointsToSetInternal exclude) {
    if (other == null) {
      return false;
    }

    if ((!(other instanceof SharedListSet)) || (exclude != null && !(exclude instanceof SharedListSet))) {
      return super.addAll(other, exclude);
    } else {
      SharedListSet realOther = (SharedListSet) other, realExclude = (SharedListSet) exclude;

      BitVector mask = getBitMask(realOther, pag);

      ListNode excludeData = (realExclude == null) ? null : realExclude.data;

      return addOrAddAll(data, realOther.data, excludeData, mask);
    }
  }

  // Holds pairs of (Node, ListNode)
  public class Pair {
    public Node first;
    public ListNode second;

    public Pair(Node first, ListNode second) {
      this.first = first;
      this.second = second;
    }

    public int hashCode() {
      // I don't think the Node will ever be null
      if (second == null) {
        return first.hashCode();
      } else {
        return first.hashCode() + second.hashCode();
      }
    }

    public boolean equals(Object other) {
      if (!(other instanceof Pair)) {
        return false;
      }
      Pair o = (Pair) other;
      return ((first == null && o.first == null) || first == o.first)
          && ((second == null && o.second == null) || second == o.second);
    }
  }

  // It's a bit confusing because there are nodes in the list and nodes in the PAG.
  // Node means a node in the PAG, ListNode is for nodes in the list (each of which
  // contains a Node as its data)
  public class ListNode {
    private Node elem;
    private ListNode next = null;
    public long refCount;

    public ListNode(Node elem, ListNode next) {
      this.elem = elem;
      this.next = next;
      refCount = 0; // When it's first created, it's being used exactly once
    }

    public void incRefCount() {
      ++refCount;
      // Get an idea of how much sharing is taking place
      // System.out.println(refCount);
    }

    public void decRefCount() {
      if (--refCount == 0)
      // if it's not being shared
      {
        // Remove the list from the HashMap if it's no longer used; otherwise
        // the sharing won't really gain us memory.
        AllSharedListNodes.v().allNodes.remove(new Pair(elem, next));
      }
    }
  }

  // I wanted to make this a static method of ListNode, but it
  // wasn't working for some reason
  private ListNode makeNode(Node elem, ListNode next) {
    Pair p = new Pair(elem, next);
    ListNode retVal = (AllSharedListNodes.v().allNodes.get(p));
    if (retVal == null)
    // if it's not an existing node
    {
      retVal = new ListNode(elem, next);
      if (next != null) {
        next.incRefCount(); // next now has an extra
      }
      // thing pointing at it (the newly created node)
      AllSharedListNodes.v().allNodes.put(p, retVal);
    }

    return retVal;
  }

  // private final Map allNodes = AllSharedListNodes.v().allNodes;
  // private static Map allNodes = new HashMap();
  private PAG pag; // I think this is needed to get the size of the bit
  // vector and the mask for casting

  private ListNode data = null;

}
