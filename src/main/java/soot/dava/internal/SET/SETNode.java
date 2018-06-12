package soot.dava.internal.SET;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jerome Miecznikowski
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

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.G;
import soot.dava.DavaBody;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.toolkits.base.finders.AbruptEdgeFinder;
import soot.dava.toolkits.base.finders.LabeledBlockFinder;
import soot.dava.toolkits.base.finders.SequenceFinder;
import soot.util.IterableSet;
import soot.util.UnmodifiableIterableSet;

public abstract class SETNode {
  private static final Logger logger = LoggerFactory.getLogger(SETNode.class);
  private IterableSet<AugmentedStmt> body;
  private final SETNodeLabel label;

  protected SETNode parent;
  protected AugmentedStmt entryStmt;
  protected IterableSet predecessors, successors;
  protected LinkedList<IterableSet> subBodies;
  protected Map<IterableSet, IterableSet> body2childChain;

  public abstract IterableSet get_NaturalExits();

  public abstract ASTNode emit_AST();

  public abstract AugmentedStmt get_EntryStmt();

  protected abstract boolean resolve(SETNode parent);

  public SETNode(IterableSet<AugmentedStmt> body) {
    this.body = body;

    parent = null;
    label = new SETNodeLabel();
    subBodies = new LinkedList<IterableSet>();
    body2childChain = new HashMap<IterableSet, IterableSet>();
    predecessors = new IterableSet();
    successors = new IterableSet();
  }

  public void add_SubBody(IterableSet body) {
    subBodies.add(body);
    body2childChain.put(body, new IterableSet());
  }

  public Map<IterableSet, IterableSet> get_Body2ChildChain() {
    return body2childChain;
  }

  public List<IterableSet> get_SubBodies() {
    return subBodies;
  }

  public IterableSet<AugmentedStmt> get_Body() {
    return body;
  }

  public SETNodeLabel get_Label() {
    return label;
  }

  public SETNode get_Parent() {
    return parent;
  }

  public boolean contains(Object o) {
    return body.contains(o);
  }

  public IterableSet get_Successors() {
    return successors;
  }

  public IterableSet get_Predecessors() {
    return predecessors;
  }

  public boolean add_Child(SETNode child, IterableSet children) {
    if ((this == child) || (children.contains(child))) {
      return false;
    }

    children.add(child);
    child.parent = this;
    return true;
  }

  public boolean remove_Child(SETNode child, IterableSet children) {
    if ((this == child) || (children.contains(child) == false)) {
      return false;
    }

    children.remove(child);
    child.parent = null;
    return true;
  }

  public boolean insert_ChildBefore(SETNode child, SETNode point, IterableSet children) {
    if ((this == child) || (this == point) || (children.contains(point) == false)) {
      return false;
    }

    children.insertBefore(child, point);
    child.parent = this;
    return true;
  }

  public List<Object> emit_ASTBody(IterableSet children) {
    LinkedList<Object> l = new LinkedList<Object>();

    Iterator cit = children.iterator();
    while (cit.hasNext()) {
      ASTNode astNode = ((SETNode) cit.next()).emit_AST();

      if (astNode != null) {
        l.addLast(astNode);
      }
    }

    return l;
  }

  /*
   * Basic inter-SETNode utilities.
   */

  public IterableSet<AugmentedStmt> get_IntersectionWith(SETNode other) {
    return body.intersection(other.get_Body());
  }

  public boolean has_IntersectionWith(SETNode other) {
    for (AugmentedStmt as : other.get_Body()) {
      if (body.contains(as)) {
        return true;
      }
    }

    return false;
  }

  public boolean is_SupersetOf(SETNode other) {
    return body.isSupersetOf(other.get_Body());
  }

  public boolean is_StrictSupersetOf(SETNode other) {
    return body.isStrictSubsetOf(other.get_Body());
  }

  /*
   * Tree traversing utilities.
   */

  public void find_SmallestSETNode(AugmentedStmt as) {
    Iterator<IterableSet> sbit = subBodies.iterator();
    while (sbit.hasNext()) {
      Iterator it = body2childChain.get(sbit.next()).iterator();
      while (it.hasNext()) {
        SETNode child = (SETNode) it.next();

        if (child.contains(as)) {
          child.find_SmallestSETNode(as);
          return;
        }
      }
    }

    as.myNode = this;
  }

  public void find_LabeledBlocks(LabeledBlockFinder lbf) {
    Iterator<IterableSet> sbit = subBodies.iterator();
    while (sbit.hasNext()) {
      Iterator cit = body2childChain.get(sbit.next()).iterator();
      while (cit.hasNext()) {
        ((SETNode) cit.next()).find_LabeledBlocks(lbf);
      }
    }

    lbf.perform_ChildOrder(this);
    lbf.find_LabeledBlocks(this);
  }

  public void find_StatementSequences(SequenceFinder sf, DavaBody davaBody) {
    Iterator<IterableSet> sbit = subBodies.iterator();
    while (sbit.hasNext()) {

      IterableSet body = sbit.next();
      IterableSet children = body2childChain.get(body);
      HashSet<AugmentedStmt> childUnion = new HashSet<AugmentedStmt>();

      Iterator cit = children.iterator();
      while (cit.hasNext()) {
        SETNode child = (SETNode) cit.next();

        child.find_StatementSequences(sf, davaBody);
        childUnion.addAll(child.get_Body());
      }

      sf.find_StatementSequences(this, body, childUnion, davaBody);
    }
  }

  public void find_AbruptEdges(AbruptEdgeFinder aef) {
    Iterator<IterableSet> sbit = subBodies.iterator();
    while (sbit.hasNext()) {
      IterableSet body = sbit.next();
      IterableSet children = body2childChain.get(body);

      Iterator cit = children.iterator();
      while (cit.hasNext()) {
        ((SETNode) cit.next()).find_AbruptEdges(aef);
      }

      aef.find_Continues(this, body, children);
    }

    sbit = subBodies.iterator();
    while (sbit.hasNext()) {
      IterableSet children = body2childChain.get(sbit.next());

      Iterator cit = children.iterator();
      if (cit.hasNext()) {

        SETNode cur = (SETNode) cit.next(), prev = null;

        while (cit.hasNext()) {
          prev = cur;
          cur = (SETNode) cit.next();

          aef.find_Breaks(prev, cur);
        }
      }
    }
  }

  protected void remove_AugmentedStmt(AugmentedStmt as) {
    IterableSet childChain = body2childChain.remove(body);
    if (body instanceof UnmodifiableIterableSet) {
      ((UnmodifiableIterableSet<AugmentedStmt>) body).forceRemove(as);
    } else {
      body.remove(as);
    }
    if (childChain != null) {
      body2childChain.put(body, childChain);
    }

    for (IterableSet subBody : subBodies) {
      if (subBody.contains(as)) {

        childChain = body2childChain.remove(subBody);
        if (subBody instanceof UnmodifiableIterableSet) {
          ((UnmodifiableIterableSet<AugmentedStmt>) subBody).forceRemove(as);
        } else {
          subBody.remove(as);
        }
        if (childChain != null) {
          body2childChain.put(subBody, childChain);
        }

        return;
      }
    }
  }

  public boolean nest(SETNode other) {
    if (other.resolve(this) == false) {
      return false;
    }

    IterableSet otherBody = other.get_Body();

    Iterator<IterableSet> sbit = subBodies.iterator();
    while (sbit.hasNext()) {
      IterableSet subBody = sbit.next();

      if (subBody.intersects(otherBody)) {
        IterableSet childChain = body2childChain.get(subBody);

        Iterator ccit = childChain.snapshotIterator();
        while (ccit.hasNext()) {
          SETNode curChild = (SETNode) ccit.next();

          IterableSet childBody = curChild.get_Body();

          if (childBody.intersects(otherBody)) {

            if (childBody.isSupersetOf(otherBody)) {
              return curChild.nest(other);
            } else {
              remove_Child(curChild, childChain);

              Iterator<IterableSet> osbit = other.subBodies.iterator();
              while (osbit.hasNext()) {
                IterableSet otherSubBody = osbit.next();

                if (otherSubBody.isSupersetOf(childBody)) {
                  other.add_Child(curChild, other.get_Body2ChildChain().get(otherSubBody));
                  break;
                }
              }
            }
          }
        }

        add_Child(other, childChain);
      }
    }

    return true;
  }

  /*
   * Debugging stuff.
   */

  public void dump() {
    dump(G.v().out);
  }

  public void dump(PrintStream out) {
    dump(out, "");
  }

  private void dump(PrintStream out, String indentation) {
    String TOP = ".---", TAB = "|  ", MID = "+---", BOT = "`---";

    out.println(indentation);
    out.println(indentation + TOP);
    out.println(indentation + TAB + getClass());
    out.println(indentation + TAB);
    Iterator it = body.iterator();
    while (it.hasNext()) {
      out.println(indentation + TAB + ((AugmentedStmt) it.next()).toString());
    }

    Iterator<IterableSet> sbit = subBodies.iterator();
    while (sbit.hasNext()) {
      IterableSet subBody = sbit.next();

      out.println(indentation + MID);
      Iterator bit = subBody.iterator();
      while (bit.hasNext()) {
        out.println(indentation + TAB + ((AugmentedStmt) bit.next()).toString());
      }

      out.println(indentation + TAB);

      Iterator cit = body2childChain.get(subBody).iterator();
      while (cit.hasNext()) {
        ((SETNode) cit.next()).dump(out, TAB + indentation);
      }
    }
    out.println(indentation + BOT);
  }

  public void verify() {
    Iterator<IterableSet> sbit = subBodies.iterator();
    while (sbit.hasNext()) {
      IterableSet body = sbit.next();

      Iterator bit = body.iterator();
      while (bit.hasNext()) {
        if ((bit.next() instanceof AugmentedStmt) == false) {
          logger.debug("Error in body: " + getClass());
        }
      }

      Iterator cit = body2childChain.get(body).iterator();
      while (cit.hasNext()) {
        ((SETNode) cit.next()).verify();
      }
    }
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof SETNode == false) {
      return false;
    }
    SETNode typed_other = (SETNode) other;
    if (body.equals(typed_other.body) == false) {
      return false;
    }
    if (subBodies.equals(typed_other.subBodies) == false) {
      return false;
    }
    if (body2childChain.equals(typed_other.body2childChain) == false) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return 1;
  }
}
