package soot.jimple.toolkits.thread.mhp.stmt;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam
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

import soot.SootMethod;
import soot.Unit;
import soot.tagkit.AbstractHost;
import soot.toolkits.graph.UnitGraph;

// *** USE AT YOUR OWN RISK ***
// May Happen in Parallel (MHP) analysis by Lin Li.
// This code should be treated as beta-quality code.
// It was written in 2003, but not incorporated into Soot until 2006.
// As such, it may contain incorrect assumptions about the usage
// of certain Soot classes.
// Some portions of this MHP analysis have been quality-checked, and are
// now used by the Transactions toolkit.
//
// -Richard L. Halpert, 2006-11-30

public abstract class JPegStmt extends AbstractHost

// public class JPegStmt implements CommunicationStmt
// public class JPegStmt extends AbstractStmt implements CommunicationStm
// public class JPegStmt extends AbstractStm

{

  protected String object;
  protected String name;
  protected String caller;
  protected Unit unit = null;
  protected UnitGraph unitGraph = null;
  // add for build dot file
  protected SootMethod sootMethod = null;

  // end add for build dot file
  protected JPegStmt() {
  }

  protected JPegStmt(String obj, String na, String ca) {

    this.object = obj;
    this.name = na;
    this.caller = ca;
  }

  protected JPegStmt(String obj, String na, String ca, SootMethod sm) {
    this.object = obj;
    this.name = na;
    this.caller = ca;
    this.sootMethod = sm;
  }

  protected JPegStmt(String obj, String na, String ca, UnitGraph ug, SootMethod sm) {
    this.object = obj;
    this.name = na;
    this.caller = ca;
    this.unitGraph = ug;
    this.sootMethod = sm;
  }

  protected JPegStmt(String obj, String na, String ca, Unit un, UnitGraph ug, SootMethod sm) {
    this.object = obj;
    this.name = na;
    this.caller = ca;
    this.unit = un;
    this.unitGraph = ug;
    this.sootMethod = sm;
  }

  protected void setUnit(Unit un) {
    unit = un;
  }

  protected void setUnitGraph(UnitGraph ug) {
    unitGraph = ug;
  }

  public UnitGraph getUnitGraph() {
    if (!containUnitGraph()) {
      throw new RuntimeException("This statement does not contain UnitGraph!");
    }

    return unitGraph;
  }

  public boolean containUnitGraph() {
    if (unitGraph == null) {
      return false;
    } else {
      return true;
    }
  }

  public Unit getUnit() {
    if (!containUnit()) {
      throw new RuntimeException("This statement does not contain Unit!");
    }

    return unit;
  }

  public boolean containUnit() {
    if (unit == null) {
      return false;
    } else {
      return true;
    }
  }

  public String getObject() {
    return object;
  }

  protected void setObject(String ob) {
    object = ob;
  }

  public String getName() {
    return name;
  }

  protected void setName(String na) {
    name = na;
  }

  public String getCaller() {
    return caller;
  }

  protected void setCaller(String ca) {
    caller = ca;
  }

  public SootMethod getMethod() {
    return sootMethod;
  }

  /*
   * public void apply(Switch sw) { ((StmtSwitch) sw).caseCommunicationStmt(this); }
   */

  /*
   * public Object clone() { if (containUnit()){ } return new JPegStmt(object,name,caller, ); }
   */

  public String toString() {

    if (sootMethod != null) {
      return "(" + getObject() + ", " + getName() + ", " + getCaller() + "," + sootMethod + ")";
    } else {
      return "(" + getObject() + ", " + getName() + ", " + getCaller() + ")";
    }

  }

  public String testToString() {
    if (containUnit()) {
      if (sootMethod != null) {
        return "(" + getObject() + ", " + getName() + ", " + getCaller() + ", " + getUnit() + "," + sootMethod + ")";
      } else {
        return "(" + getObject() + ", " + getName() + ", " + getCaller() + ", " + getUnit() + ")";
      }
    } else {
      return "(" + getObject() + ", " + getName() + ", " + getCaller() + ")";
    }

  }

}
