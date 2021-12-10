package soot.toolkits.graph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 John Jorgensen
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

import java.util.Collection;
import java.util.List;

import soot.Body;
import soot.Trap;
import soot.toolkits.exceptions.ThrowableSet;

/**
 * <p>
 * Defines the interface for navigating a control flow graph which distinguishes exceptional control flow.
 * </p>
 *
 * @param <N>
 *          node type
 */
public interface ExceptionalGraph<N> extends DirectedBodyGraph<N> {
  /**
   * <p>
   * Data structure to represent the fact that a given {@link Trap} will catch some subset of the exceptions which may be
   * thrown by a given graph node.
   * </p>
   *
   * <p>
   * Note that these ``destinations'' are different from the edges in the CFG proper which are returned by
   * <code>getSuccsOf()</code> and <code>getPredsOf()</code>. An edge from <code>a</code> to <code>b</code> in the CFG
   * represents the fact that after node <code>a</code> executes (perhaps only partially, if it throws an exception after
   * producing a side effect), execution may proceed to node <code>b</code>. An ExceptionDest from <code>a</code> to
   * <code>b</code>, on the other hand, says that when <code>a</code> fails to execute, execution may proceed to
   * <code>b</code> instead.
   * </p>
   * 
   * @param <N>
   */
  public interface ExceptionDest<N> {

    /**
     * Returns the trap corresponding to this destination.
     *
     * @return either a {@link Trap} representing the handler that catches the exceptions, if there is such a handler within
     *         the method, or <code>null</code> if there is no such handler and the exceptions cause the method to terminate
     *         abruptly.
     */
    public Trap getTrap();

    /**
     * Returns the exceptions thrown to this destination.
     *
     * @return a {@link ThrowableSet} representing the exceptions which may be caught by this <code>ExceptionDest</code>'s
     *         trap.
     */
    public ThrowableSet getThrowables();

    /**
     * Returns the CFG node corresponding to the beginning of the exception handler that catches the exceptions (that is, the
     * node that includes {@link trap().getBeginUnit()}).
     *
     * @return the node in this graph which represents the beginning of the handler which catches these exceptions, or
     *         <code>null</code> if there is no such handler and the exceptions cause the method to terminate abruptly.
     */
    // Maybe we should define an interface for Unit and Block to
    // implement, and return an instance of that, rather than
    // an Object. We chose Object because that's what DirectedGraph
    // deals in.
    public N getHandlerNode();
  }

  /**
   * Returns the {@link Body} from which this graph was built.
   *
   * @return the <code>Body</code> from which this graph was built.
   */
  @Override
  public Body getBody();

  /**
   * Returns a list of nodes which are predecessors of a given node when only unexceptional control flow is considered.
   *
   * @param n
   *          The node whose predecessors are to be returned.
   *
   * @return a {@link List} of the nodes in this graph from which there is an unexceptional edge to <code>n</code>.
   */
  public List<N> getUnexceptionalPredsOf(N n);

  /**
   * Returns a list of nodes which are successors of a given node when only unexceptional control flow is considered.
   *
   * @param n
   *          The node whose successors are to be returned.
   *
   * @return a {@link List} of nodes in this graph to which there is an unexceptional edge from <code>n</code>.
   */
  public List<N> getUnexceptionalSuccsOf(N n);

  /**
   * Returns a list of nodes which are predecessors of a given node when only exceptional control flow is considered.
   *
   * @param n
   *          The node whose predecessors are to be returned.
   *
   * @return a {@link List} of nodes in this graph from which there is an exceptional edge to <code>n</code>.
   */
  public List<N> getExceptionalPredsOf(N n);

  /**
   * Returns a list of nodes which are successors of a given node when only exceptional control flow is considered.
   *
   * @param n
   *          The node whose successors are to be returned.
   *
   * @return a {@link List} of nodes in this graph to which there is an exceptional edge from <code>n</code>.
   */
  public List<N> getExceptionalSuccsOf(N n);

  /**
   * Returns a collection of {@link ExceptionalGraph.ExceptionDest ExceptionDest} objects which represent how exceptions
   * thrown by a specified node will be handled.
   *
   * @param n
   *          The node for which to provide exception information.
   *
   * @return a collection of <code>ExceptionDest</code> objects describing the traps and handlers, if any, which catch the
   *         exceptions which may be thrown by <code>n</code>.
   */
  public Collection<? extends ExceptionDest<N>> getExceptionDests(N n);
}
