package soot.jimple.toolkits.thread.synchronization;

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

import java.util.ArrayList;
import java.util.List;

import soot.jimple.Stmt;
import soot.toolkits.scalar.Pair;

public class SynchronizedRegion {
  public Stmt prepStmt;
  public Stmt entermonitor;
  public Stmt beginning; // first stmt of body
  public List<Pair<Stmt, Stmt>> earlyEnds; // list of <return/branch stmt, exitmonitor> pairs
  public Pair<Stmt, Stmt> exceptionalEnd; // <throw stmt, exitmonitor> pair
  public Pair<Stmt, Stmt> end; // <goto stmt, exitmonitor> pair
  public Stmt last; // the last stmt before exception handling (usually a goto, return, or branch stmt from one of the ends)
  public Stmt after;

  public SynchronizedRegion() {
    this.prepStmt = null;
    this.entermonitor = null;
    this.beginning = null;
    this.earlyEnds = new ArrayList<Pair<Stmt, Stmt>>();
    this.exceptionalEnd = null;
    this.end = null;
    this.last = null;
    this.after = null;
  }

  public SynchronizedRegion(SynchronizedRegion sr) {
    this.prepStmt = sr.prepStmt;
    this.entermonitor = sr.entermonitor;
    this.beginning = sr.beginning;
    this.earlyEnds = new ArrayList<Pair<Stmt, Stmt>>();
    this.earlyEnds.addAll(sr.earlyEnds);
    this.exceptionalEnd = null;
    this.end = sr.end;
    this.last = sr.last;
    this.after = sr.after;
  }

  protected Object clone() {
    return new SynchronizedRegion(this);
  }
}
