package soot.toolkits.graph.pdg;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 - 2010 Hossein Sadat-Mohtasham
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.options.Options;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.UnitGraph;

/**
 * This represents a region of control dependence obtained by constructing a program dependence graph. A PDGRegion is
 * slightly different than a weak or strong region; the loops and conditional relations between regions are explicitly
 * represented in the PDGRegion.
 *
 * @author Hossein Sadat-Mohtasham Sep 2009
 */

public class PDGRegion implements IRegion, Iterable<PDGNode> {
  private static final Logger logger = LoggerFactory.getLogger(PDGRegion.class);

  private SootClass m_class = null;
  private SootMethod m_method = null;
  private List<PDGNode> m_nodes = null;
  private List<Unit> m_units = null;
  private LinkedHashMap<Unit, PDGNode> m_unit2pdgnode = null;
  private int m_id = -1;
  private UnitGraph m_unitGraph = null;
  private PDGNode m_corrspondingPDGNode = null;
  // The following are needed to create a tree of regions based on the containment (dependency)
  // relation between regions.
  private IRegion m_parent = null;
  // The following keeps the child regions
  private List<IRegion> m_children = new ArrayList<IRegion>();

  public PDGRegion(int id, SootMethod m, SootClass c, UnitGraph ug, PDGNode node) {
    this(id, new ArrayList<PDGNode>(), m, c, ug, node);

  }

  public PDGRegion(int id, List<PDGNode> nodes, SootMethod m, SootClass c, UnitGraph ug, PDGNode node) {

    this.m_nodes = nodes;
    this.m_id = id;
    this.m_method = m;
    this.m_class = c;
    this.m_unitGraph = ug;
    this.m_units = null;
    this.m_corrspondingPDGNode = node;

    if (Options.v().verbose()) {
      logger.debug("New pdg region create: " + id);
    }

  }

  public PDGRegion(PDGNode node) {
    this(((IRegion) node.getNode()).getID(), (List<PDGNode>) new ArrayList<PDGNode>(),
        ((IRegion) node.getNode()).getSootMethod(), ((IRegion) node.getNode()).getSootClass(),
        ((IRegion) node.getNode()).getUnitGraph(), node);

  }

  public PDGNode getCorrespondingPDGNode() {
    return this.m_corrspondingPDGNode;
  }

  @SuppressWarnings("unchecked")
  public Object clone() {
    PDGRegion r = new PDGRegion(this.m_id, this.m_method, this.m_class, this.m_unitGraph, m_corrspondingPDGNode);
    r.m_nodes = (List<PDGNode>) ((ArrayList<PDGNode>) this.m_nodes).clone();

    return r;

  }

  public SootMethod getSootMethod() {
    return this.m_method;
  }

  public SootClass getSootClass() {
    return this.m_class;
  }

  public List<PDGNode> getNodes() {
    return this.m_nodes;
  }

  public UnitGraph getUnitGraph() {
    return this.m_unitGraph;
  }

  /**
   * This is an iterator that knows how to follow the control flow in a region. It only iterates through the dependent nodes
   * that contribute to the list of units in a region as defined by a weak region.
   *
   */
  class ChildPDGFlowIterator implements Iterator<PDGNode> {
    List<PDGNode> m_list = null;
    PDGNode m_current = null;
    boolean beginning = true;

    public ChildPDGFlowIterator(List<PDGNode> list) {
      m_list = list;
    }

    public boolean hasNext() {
      if (beginning) {
        if (m_list.size() > 0) {
          return true;
        }
      }

      return (m_current != null && m_current.getNext() != null);
    }

    public PDGNode next() {

      if (beginning) {
        beginning = false;
        m_current = m_list.get(0);
        // Find the first node in the control flow

        /*
         * There cannot be more than one CFG node in a region unless there is a control flow edge between them. However,
         * there could be a CFG node and other region nodes (back dependency, or other.) In such cases, the one CFG node
         * should be found and returned, and other region nodes should be ignored. Unless it's a LoopedPDGNode (in which case
         * control flow edge should still exist if there are other sibling Looped PDGNodes or CFG nodes.)
         *
         */
        while (m_current.getPrev() != null) {
          m_current = m_current.getPrev();
        }

        if (m_current.getType() != PDGNode.Type.CFGNODE && m_current.getAttrib() != PDGNode.Attribute.LOOPHEADER) {
          /*
           * Look for useful dependence whose units are considered to be part of this region (loop header or CFG block
           * nodes.)
           *
           */

          for (Iterator<PDGNode> depItr = m_list.iterator(); depItr.hasNext();) {
            PDGNode dep = depItr.next();
            if (dep.getType() == PDGNode.Type.CFGNODE || dep.getAttrib() == PDGNode.Attribute.LOOPHEADER) {
              m_current = dep;
              // go to the beginning of the flow
              while (m_current.getPrev() != null) {
                m_current = m_current.getPrev();
              }
              break;
            }
          }
        }
        return m_current;
      }

      if (!hasNext()) {
        throw new RuntimeException("No more nodes!");
      }
      m_current = m_current.getNext();
      return m_current;
    }

    public void remove() {

    }

  }

  /**
   * return an iterator that know how to follow the control flow in a region. This actually returns a ChildPDGFlowIterator
   * that only iterates through the dependent nodes that contribute to the units that belong to a region as defined by a weak
   * region.
   *
   */
  public Iterator<PDGNode> iterator() {
    return new ChildPDGFlowIterator(this.m_nodes);
  }

  public List<Unit> getUnits() {
    if (this.m_units == null) {
      this.m_units = new LinkedList<Unit>();
      this.m_unit2pdgnode = new LinkedHashMap<Unit, PDGNode>();

      for (Iterator<PDGNode> itr = this.iterator(); itr.hasNext();) {
        PDGNode node = itr.next();

        if (node.getType() == PDGNode.Type.REGION) {
          // Actually, we should only get here if a loop header region is in this region's children list.
          // Or if the PDG is based on an ExceptionalUnitGraph, then this could be the region corresponding
          // to a handler, in which case it's ignored.
          // if(node.getAttrib() == PDGNode.Attribute.LOOPHEADER)
          if (node instanceof LoopedPDGNode) {
            LoopedPDGNode n = (LoopedPDGNode) node;
            PDGNode header = n.getHeader();
            Block headerBlock = (Block) header.getNode();
            for (Iterator<Unit> itr1 = headerBlock.iterator(); itr1.hasNext();) {
              Unit u = itr1.next();
              ((LinkedList<Unit>) this.m_units).addLast(u);
              this.m_unit2pdgnode.put(u, header);

            }
          }

        } else if (node.getType() == PDGNode.Type.CFGNODE) {
          Block b = (Block) node.getNode();
          for (Iterator<Unit> itr1 = b.iterator(); itr1.hasNext();) {
            Unit u = itr1.next();
            ((LinkedList<Unit>) this.m_units).addLast(u);
            this.m_unit2pdgnode.put(u, node);

          }

        } else {
          throw new RuntimeException("Exception in PDGRegion.getUnits: PDGNode's type is undefined!");
        }

      }

    }
    return this.m_units;
  }

  /**
   *
   * @param a
   *          Statement within the region
   *
   * @return The PDGNode that contains that unit, if this unit is in this region.
   */
  public PDGNode unit2PDGNode(Unit u) {
    if (this.m_unit2pdgnode.containsKey(u)) {
      return this.m_unit2pdgnode.get(u);
    } else {
      return null;
    }
  }

  public List<Unit> getUnits(Unit from, Unit to) {

    return m_units.subList(m_units.indexOf(from), m_units.indexOf(to));

  }

  public Unit getLast() {
    if (this.m_units != null) {
      if (this.m_units.size() > 0) {
        return ((LinkedList<Unit>) this.m_units).getLast();
      }
    }

    return null;
  }

  public Unit getFirst() {
    if (this.m_units != null) {
      if (this.m_units.size() > 0) {
        return ((LinkedList<Unit>) this.m_units).getFirst();
      }
    }

    return null;
  }

  // FIXME: return the real list of blocks
  public List<Block> getBlocks() {
    return new ArrayList<Block>();
  }

  public void addPDGNode(PDGNode node) {
    this.m_nodes.add(node);
  }

  public int getID() {
    return this.m_id;
  }

  public boolean occursBefore(Unit u1, Unit u2) {
    int i = this.m_units.lastIndexOf(u1);
    int j = this.m_units.lastIndexOf(u2);

    if (i == -1 || j == -1) {
      throw new RuntimeException("These units don't exist in the region!");
    }

    return i < j;
  }

  public void setParent(IRegion pr) {
    this.m_parent = pr;
  }

  public IRegion getParent() {
    return this.m_parent;
  }

  public void addChildRegion(IRegion chr) {
    if (!this.m_children.contains(chr)) {
      this.m_children.add(chr);
    }
  }

  public List<IRegion> getChildRegions() {
    return this.m_children;
  }

  public String toString() {
    String str = new String();
    str += "Begin-----------PDGRegion:  " + this.m_id + "-------------\n";
    if (this.m_parent != null) {
      str += "Parent is: " + this.m_parent.getID() + "----\n";
    }
    str += "Children Regions are: ";

    for (Iterator<IRegion> ritr = this.m_children.iterator(); ritr.hasNext();) {
      str += ((IRegion) ritr.next()).getID() + ", ";
    }

    str += "\nUnits are: \n";

    List<Unit> regionUnits = this.getUnits();
    for (Iterator<Unit> itr = regionUnits.iterator(); itr.hasNext();) {
      Unit u = itr.next();
      str += u + "\n";

    }
    str += "End of PDG Region " + this.m_id + " -----------------------------\n";

    return str;

  }

}
