package soot.util.cfgcmd;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 John Jorgensen
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import soot.Body;
import soot.BriefUnitPrinter;
import soot.LabeledUnitPrinter;
import soot.Unit;
import soot.toolkits.exceptions.ThrowableSet;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.DominatorNode;
import soot.toolkits.graph.ExceptionalGraph;
import soot.toolkits.graph.ExceptionalGraph.ExceptionDest;
import soot.toolkits.graph.UnitGraph;
import soot.util.dot.DotGraph;
import soot.util.dot.DotGraphAttribute;
import soot.util.dot.DotGraphConstants;
import soot.util.dot.DotGraphEdge;
import soot.util.dot.DotGraphNode;

/**
 * Class that creates a {@link DotGraph} visualization of a control flow graph.
 */
public class CFGToDotGraph {

  private boolean onePage; // in one or several 8.5x11 pages.
  private boolean isBrief;
  private boolean showExceptions;
  private DotGraphAttribute unexceptionalControlFlowAttr;
  private DotGraphAttribute exceptionalControlFlowAttr;
  private DotGraphAttribute exceptionEdgeAttr;
  private DotGraphAttribute headAttr;
  private DotGraphAttribute tailAttr;

  /**
   * <p>
   * Returns a CFGToDotGraph converter which will draw the graph as a single arbitrarily-sized page, with full-length node
   * labels.
   * </p>
   *
   * <p>
   * If asked to draw a <code>ExceptionalGraph</code>, the converter will identify the exceptions that will be thrown. By
   * default, it will distinguish different edges by coloring regular control flow edges black, exceptional control flow
   * edges red, and thrown exception edges light gray. Head and tail nodes are filled in, head nodes with gray, and tail
   * nodes with light gray.
   * </p>
   */
  public CFGToDotGraph() {
    setOnePage(true);
    setBriefLabels(false);
    setShowExceptions(true);
    setUnexceptionalControlFlowAttr("color", "black");
    setExceptionalControlFlowAttr("color", "red");
    setExceptionEdgeAttr("color", "lightgray");
    setHeadAttr("fillcolor", "gray");
    setTailAttr("fillcolor", "lightgray");
  }

  /**
   * Specify whether to split the graph into pages.
   *
   * @param onePage
   *          indicates whether to produce the graph as a single, arbitrarily-sized page (if <code>onePage</code> is
   *          <code>true</code>) or several 8.5x11-inch pages (if <code>onePage</code> is <code>false</code>).
   */
  public void setOnePage(boolean onePage) {
    this.onePage = onePage;
  }

  /**
   * Specify whether to abbreviate the text in node labels. This is most relevant when the nodes represent basic blocks:
   * abbreviated node labels contain only a numeric label for the block, while unabbreviated labels contain the code of its
   * instructions.
   *
   * @param useBrief
   *          indicates whether to abbreviate the text of node labels.
   */
  public void setBriefLabels(boolean useBrief) {
    this.isBrief = useBrief;
  }

  /**
   * Specify whether the graph should depict the exceptions which each node may throw, in the form of an edge from the
   * throwing node to the handler (if any), labeled with the possible exception types. This parameter has an effect only when
   * drawing <code>ExceptionalGraph</code>s.
   *
   * @param showExceptions
   *          indicates whether to show possible exceptions and their handlers.
   */
  public void setShowExceptions(boolean showExceptions) {
    this.showExceptions = showExceptions;
  }

  /**
   * Specify the dot graph attribute to use for regular control flow edges. This parameter has an effect only when drawing
   * <code>ExceptionalGraph</code>s.
   *
   * @param id
   *          The attribute name, for example "style" or "color".
   *
   * @param value
   *          The attribute value, for example "solid" or "black".
   *
   * @see <a href="http://www.research.att.com/sw/tools/graphviz/dotguide.pdf">"Drawing graphs with dot"</a>
   */
  public void setUnexceptionalControlFlowAttr(String id, String value) {
    unexceptionalControlFlowAttr = new DotGraphAttribute(id, value);
  }

  /**
   * Specify the dot graph attribute to use for exceptional control flow edges. This parameter has an effect only when
   * drawing <code>ExceptionalGraph</code>s.
   *
   * @param id
   *          The attribute name, for example "style" or "color".
   *
   * @param value
   *          The attribute value, for example "dashed" or "red".
   *
   * @see <a href="http://www.research.att.com/sw/tools/graphviz/dotguide.pdf">"Drawing graphs with dot"</a>
   */
  public void setExceptionalControlFlowAttr(String id, String value) {
    exceptionalControlFlowAttr = new DotGraphAttribute(id, value);
  }

  /**
   * Specify the dot graph attribute to use for edges depicting the exceptions each node may throw, and their handlers. This
   * parameter has an effect only when drawing <code>ExceptionalGraph</code>s.
   *
   * @param id
   *          The attribute name, for example "style" or "color".
   *
   * @param value
   *          The attribute value, for example "dotted" or "lightgray".
   *
   * @see <a href="http://www.research.att.com/sw/tools/graphviz/dotguide.pdf">"Drawing graphs with dot"</a>
   */
  public void setExceptionEdgeAttr(String id, String value) {
    exceptionEdgeAttr = new DotGraphAttribute(id, value);
  }

  /**
   * Specify the dot graph attribute to use for head nodes (in addition to filling in the nodes).
   *
   * @param id
   *          The attribute name, for example "fillcolor".
   *
   * @param value
   *          The attribute value, for example "gray".
   *
   * @see <a href="http://www.research.att.com/sw/tools/graphviz/dotguide.pdf">"Drawing graphs with dot"</a>
   */
  public void setHeadAttr(String id, String value) {
    headAttr = new DotGraphAttribute(id, value);
  }

  /**
   * Specify the dot graph attribute to use for tail nodes (in addition to filling in the nodes).
   *
   * @param id
   *          The attribute name, for example "fillcolor".
   *
   * @param value
   *          The attribute value, for example "lightgray".
   *
   * @see <a href="http://www.research.att.com/sw/tools/graphviz/dotguide.pdf">"Drawing graphs with dot"</a>
   */
  public void setTailAttr(String id, String value) {
    tailAttr = new DotGraphAttribute(id, value);
  }

  /**
   * Returns an {@link Iterator} over a {@link Collection} which iterates over its elements in a specified order. Used to
   * order lists of destination nodes consistently before adding the corresponding edges to the graph. (Maintaining a
   * consistent ordering of edges makes it easier to diff the dot files output for different graphs of a given method.)
   *
   * @param coll
   *          The collection to iterator over.
   *
   * @param comp
   *          The comparator for the ordering.
   *
   * @return An iterator which presents the elements of <code>coll</code> in the order specified by <code>comp</code>.
   */
  private static <T> Iterator<T> sortedIterator(Collection<T> coll, Comparator<? super T> comp) {
    if (coll.size() <= 1) {
      return coll.iterator();
    } else {
      ArrayList<T> list = new ArrayList<T>(coll);
      Collections.sort(list, comp);
      return list.iterator();
    }
  }

  /**
   * Comparator used to order a list of nodes by the order in which they were labeled.
   */
  private static class NodeComparator<T> implements Comparator<T> {
    private final DotNamer<T> namer;

    NodeComparator(DotNamer<T> namer) {
      this.namer = namer;
    }

    @Override
    public int compare(T o1, T o2) {
      return (namer.getNumber(o1) - namer.getNumber(o2));
    }

    public boolean equal(T o1, T o2) {
      return (namer.getNumber(o1) == namer.getNumber(o2));
    }
  }

  /**
   * Comparator used to order a list of ExceptionDests by the order in which their handler nodes were labeled.
   */
  private static class ExceptionDestComparator<T> implements Comparator<ExceptionDest<T>> {
    private final DotNamer<T> namer;

    ExceptionDestComparator(DotNamer<T> namer) {
      this.namer = namer;
    }

    private int getValue(ExceptionDest<T> o) {
      T handler = o.getHandlerNode();
      if (handler == null) {
        return Integer.MAX_VALUE;
      } else {
        return namer.getNumber(handler);
      }
    }

    @Override
    public int compare(ExceptionDest<T> o1, ExceptionDest<T> o2) {
      return (getValue(o1) - getValue(o2));
    }

    public boolean equal(ExceptionDest<T> o1, ExceptionDest<T> o2) {
      return (getValue(o1) == getValue(o2));
    }
  }

  /**
   * Create a <code>DotGraph</code> whose nodes and edges depict a control flow graph without distinguished exceptional
   * edges.
   *
   * @param graph
   *          a <code>DirectedGraph</code> representing a CFG (probably an instance of {@link UnitGraph}, {@link BlockGraph},
   *          or one of their subclasses).
   *
   * @param body
   *          the <code>Body</code> represented by <code>graph</code> (used to format the text within nodes). If no body is
   *          available, pass <code>null</code>.
   *
   * @return a visualization of <code>graph</code>.
   */
  public <N> DotGraph drawCFG(DirectedGraph<N> graph, Body body) {
    DotGraph canvas = initDotGraph(body);
    DotNamer<N> namer = new DotNamer<N>((int) (graph.size() / 0.7f), 0.7f);
    NodeComparator<N> comparator = new NodeComparator<N>(namer);

    // To facilitate comparisons between different graphs of the same
    // method, prelabel the nodes in the order they appear
    // in the iterator, rather than the order that they appear in the
    // graph traversal (so that corresponding nodes are more likely
    // to have the same label in different graphs of a given method).
    for (N node : graph) {
      namer.getName(node);
    }
    for (N node : graph) {
      canvas.drawNode(namer.getName(node));
      for (Iterator<N> succsIt = sortedIterator(graph.getSuccsOf(node), comparator); succsIt.hasNext();) {
        N succ = succsIt.next();
        canvas.drawEdge(namer.getName(node), namer.getName(succ));
      }
    }
    setStyle(graph.getHeads(), canvas, namer, DotGraphConstants.NODE_STYLE_FILLED, headAttr);
    setStyle(graph.getTails(), canvas, namer, DotGraphConstants.NODE_STYLE_FILLED, tailAttr);
    if (!isBrief) {
      formatNodeText(body, canvas, namer);
    }

    return canvas;
  }

  /**
   * Create a <code>DotGraph</code> whose nodes and edges depict the control flow in a <code>ExceptionalGraph</code>, with
   * distinguished edges for exceptional control flow.
   *
   * @param graph
   *          the control flow graph
   *
   * @return a visualization of <code>graph</code>.
   */
  public <N> DotGraph drawCFG(ExceptionalGraph<N> graph) {
    Body body = graph.getBody();
    DotGraph canvas = initDotGraph(body);
    DotNamer<Object> namer = new DotNamer<Object>((int) (graph.size() / 0.7f), 0.7f);
    @SuppressWarnings("unchecked")
    NodeComparator<N> nodeComparator = new NodeComparator<N>((DotNamer<N>) namer);

    // Prelabel nodes in iterator order, to facilitate
    // comparisons between graphs of a given method.
    for (N node : graph) {
      namer.getName(node);
    }

    for (N node : graph) {
      canvas.drawNode(namer.getName(node));

      for (Iterator<N> succsIt = sortedIterator(graph.getUnexceptionalSuccsOf(node), nodeComparator); succsIt.hasNext();) {
        N succ = succsIt.next();
        DotGraphEdge edge = canvas.drawEdge(namer.getName(node), namer.getName(succ));
        edge.setAttribute(unexceptionalControlFlowAttr);
      }

      for (Iterator<N> succsIt = sortedIterator(graph.getExceptionalSuccsOf(node), nodeComparator); succsIt.hasNext();) {
        N succ = succsIt.next();
        DotGraphEdge edge = canvas.drawEdge(namer.getName(node), namer.getName(succ));
        edge.setAttribute(exceptionalControlFlowAttr);
      }

      if (showExceptions) {
        Collection<? extends ExceptionDest<N>> exDests = graph.getExceptionDests(node);
        if (exDests != null) {
          @SuppressWarnings("unchecked")
          ExceptionDestComparator<N> comp = new ExceptionDestComparator<>((DotNamer<N>) namer);
          for (Iterator<? extends ExceptionDest<N>> destsIt = sortedIterator(exDests, comp); destsIt.hasNext();) {
            ExceptionDest<N> dest = destsIt.next();
            Object handlerStart = dest.getHandlerNode();
            if (handlerStart == null) {
              // Giving each escaping exception its own, invisible
              // exceptional exit node produces a less cluttered
              // graph.
              handlerStart = new Object() {
                @Override
                public String toString() {
                  return "Esc";
                }
              };
              DotGraphNode escapeNode = canvas.drawNode(namer.getName(handlerStart));
              escapeNode.setStyle(DotGraphConstants.NODE_STYLE_INVISIBLE);
            }
            DotGraphEdge edge = canvas.drawEdge(namer.getName(node), namer.getName(handlerStart));
            edge.setAttribute(exceptionEdgeAttr);
            edge.setLabel(formatThrowableSet(dest.getThrowables()));
          }
        }
      }
    }
    setStyle(graph.getHeads(), canvas, namer, DotGraphConstants.NODE_STYLE_FILLED, headAttr);
    setStyle(graph.getTails(), canvas, namer, DotGraphConstants.NODE_STYLE_FILLED, tailAttr);
    if (!isBrief) {
      formatNodeText(graph.getBody(), canvas, namer);
    }
    return canvas;
  }

  /**
   * A utility method that initializes a DotGraph object for use in any variety of drawCFG().
   *
   * @param body
   *          The <code>Body</code> that the graph will represent (used in the graph's title). If no <code>Body</code> is
   *          available, pass <code>null</code>.
   *
   * @return a <code>DotGraph</code> for visualizing <code>body</code>.
   */
  private DotGraph initDotGraph(Body body) {
    String graphname = "cfg";
    if (body != null) {
      graphname = body.getMethod().getSubSignature();
    }
    DotGraph canvas = new DotGraph(graphname);
    canvas.setGraphLabel(graphname);
    if (!onePage) {
      canvas.setPageSize(8.5, 11.0);
    }
    if (isBrief) {
      canvas.setNodeShape(DotGraphConstants.NODE_SHAPE_CIRCLE);
    } else {
      canvas.setNodeShape(DotGraphConstants.NODE_SHAPE_BOX);
    }
    return canvas;
  }

  /**
   * A utility class for assigning unique names to DotGraph entities. It maintains a mapping from CFG <code>Object</code>s to
   * strings identifying the corresponding nodes in generated dot source.
   */
  @SuppressWarnings("serial")
  private static class DotNamer<N> extends HashMap<N, Integer> {
    private int nodecount = 0;

    DotNamer(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
    }

    String getName(N node) {
      Integer index = this.get(node);
      if (index == null) {
        index = nodecount++;
        this.put(node, index);
      }
      return index.toString();
    }

    int getNumber(N node) {
      Integer index = this.get(node);
      if (index == null) {
        index = nodecount++;
        this.put(node, index);
      }
      return index;
    }
  }

  /**
   * A utility method which formats the text for each node in a <code>DotGraph</code> representing a CFG.
   *
   * @param body
   *          the <code>Body</code> whose control flow is visualized in <code>canvas</code>.
   *
   * @param canvas
   *          the <code>DotGraph</code> for visualizing the CFG.
   *
   * @param namer
   *          provides a mapping from CFG objects to identifiers in generated dot source.
   */
  private <N> void formatNodeText(Body body, DotGraph canvas, DotNamer<N> namer) {
    LabeledUnitPrinter printer = null;
    if (body != null) {
      printer = new BriefUnitPrinter(body);
      printer.noIndent();
    }

    for (N node : namer.keySet()) {
      DotGraphNode dotnode = canvas.getNode(namer.getName(node));

      if (node instanceof DominatorNode<?>) {
        @SuppressWarnings("unchecked")
        DominatorNode<N> n = (DominatorNode<N>) node;
        node = n.getGode();
      }

      String nodeLabel;
      if (printer == null) {
        nodeLabel = node.toString();
      } else if (node instanceof Unit) {
        final Unit nodeUnit = (Unit) node;
        nodeUnit.toString(printer);
        String targetLabel = printer.labels().get(nodeUnit);
        if (targetLabel == null) {
          nodeLabel = printer.toString();
        } else {
          nodeLabel = targetLabel + ": " + printer.toString();
        }
      } else if (node instanceof Block) {
        Block block = (Block) node;
        StringBuilder buffer = new StringBuilder();
        buffer.append(block.toShortString()).append("\\n");
        for (Unit unit : block) {
          String targetLabel = printer.labels().get(unit);
          if (targetLabel != null) {
            buffer.append(targetLabel).append(":\\n");
          }
          unit.toString(printer);
          buffer.append(printer.toString()).append("\\l");
        }
        nodeLabel = buffer.toString();
      } else {
        nodeLabel = node.toString();
      }
      dotnode.setLabel(nodeLabel);
    }
  }

  /**
   * Utility routine for setting some common formatting style for the {@link DotGraphNode}s corresponding to some collection
   * of objects.
   *
   * @param objects
   *          is the collection of {@link Object}s whose nodes are to be styled.
   * @param canvas
   *          the {@link DotGraph} containing nodes corresponding to the collection.
   * @param namer
   *          maps from {@link Object} to the strings used to identify corresponding {@link DotGraphNode}s.
   * @param style
   *          the style to set for each of the nodes.
   * @param attrib
   *          if non-null, an additional attribute to associate with each of the nodes.
   */
  private <N> void setStyle(Collection<? extends N> objects, DotGraph canvas, DotNamer<N> namer, String style,
      DotGraphAttribute attrib) {
    // Fill the entry and exit nodes.
    for (N object : objects) {
      DotGraphNode objectNode = canvas.getNode(namer.getName(object));
      objectNode.setStyle(style);
      objectNode.setAttribute(attrib);
    }
  }

  /**
   * Utility routine to format the list of names in a ThrowableSet into a label for the edge showing where those Throwables
   * are handled.
   */
  private String formatThrowableSet(ThrowableSet set) {
    String input = set.toAbbreviatedString();

    // Insert line breaks between individual Throwables (dot seems to
    // orient these edges more or less vertically, most of the time).
    int inputLength = input.length();
    StringBuilder result = new StringBuilder(inputLength + 5);
    for (int i = 0; i < inputLength; i++) {
      char c = input.charAt(i);
      if (c == '+' || c == '-') {
        result.append("\\l");
      }
      result.append(c);
    }
    return result.toString();
  }
}
