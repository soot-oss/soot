/* Soot - a J*va Optimization Framework
 * Copyright (C) 2007 Eric Bodden
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
package soot.jimple.toolkits.annotation.logic;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import soot.Unit;
import soot.jimple.Stmt;
import soot.toolkits.graph.UnitGraph;

/**
 * A (natural) loop in Jimple. A back-edge (t,h) is a control-flog edge for which
 * h dominates t. In this case h is the header and the loop consists of all statements
 * s which reach t without passing through h. 
 *
 * @author Eric Bodden
 */
public class Loop {
    
    protected final Stmt header;
    protected final Stmt backJump;
    protected final List<Stmt> loopStatements;
    protected final UnitGraph g;
    protected Collection<Stmt> loopExists;

    /**
     * Creates a new loop. Expects that the last statement in the list is the loop head
     * and the second-last statement is the back-jump to the head. {@link LoopFinder} will
     * normally guarantee this.
     * @param head the loop header
     * @param loopStatements an ordered list of loop statements, ending with the header
     * @param g the unit graph according to which the loop exists
     */
    Loop(Stmt head, List<Stmt> loopStatements, UnitGraph g) {
        this.header = head;
        this.g = g;

        //put header to the top
        loopStatements.remove(head);
        loopStatements.add(0, head);
        
        //last statement
        this.backJump = loopStatements.get(loopStatements.size()-1);
        
        assert g.getSuccsOf(this.backJump).contains(head); //must branch back to the head

        this.loopStatements = loopStatements;
    }

    /**
     * @return the loop head
     */
    public Stmt getHead() {
        return header;
    }

    /**
     * Returns the statement that jumps back to the head, thereby constituing the loop.
     */
    public Stmt getBackJumpStmt() {
        return backJump;
    }

    /**
     * @return all statements of the loop, including the header;
     * the header will be the first element returned and then the
     * other statements follow in the natural ordering of the loop
     */
    public List<Stmt> getLoopStatements() {
        return loopStatements;
    }
    
    /**
     * Returns all loop exists.
     * A loop exit is a statement which has a successor that is not contained in the loop.
     */
    public Collection<Stmt> getLoopExits() {
        if(loopExists==null) {
            loopExists = new HashSet<Stmt>();
            for (Stmt s : loopStatements) {
                for (Unit succ : g.getSuccsOf(s)) {
                    if(!loopStatements.contains(succ)) {
                        loopExists.add(s);
                    }
                }
            }
        }
        return loopExists;
    }
    
    /**
     * Computes all targets of the given loop exit, i.e. statements that the exit jumps to but which are not
     * part of this loop.
     */
    public Collection<Stmt> targetsOfLoopExit(Stmt loopExit) {
        assert getLoopExits().contains(loopExit);
        List<Unit> succs = g.getSuccsOf(loopExit);
        Collection<Stmt> res = new HashSet<Stmt>();
        for (Unit u : succs) {
            Stmt s = (Stmt)u;
            res.add(s);            
        }
        res.removeAll(loopStatements);
        return res;
    }
    
    /**
     * Returns <code>true</code> if this loop certainly loops forever, i.e. if it has not exit.
     * @see #getLoopExits()
     */
    public boolean loopsForever() {
        return getLoopExits().isEmpty();
    }

    /**
     * Returns <code>true</code> if this loop has a single exit statement.
     * @see #getLoopExits()
     */
    public boolean hasSingleExit() {
        return getLoopExits().size()==1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((header == null) ? 0 : header.hashCode());
        result = prime * result
                + ((loopStatements == null) ? 0 : loopStatements.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Loop other = (Loop) obj;
        if (header == null) {
            if (other.header != null)
                return false;
        } else if (!header.equals(other.header))
            return false;
        if (loopStatements == null) {
            if (other.loopStatements != null)
                return false;
        } else if (!loopStatements.equals(other.loopStatements))
            return false;
        return true;
    }

}
