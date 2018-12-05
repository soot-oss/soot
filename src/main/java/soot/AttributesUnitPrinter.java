package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import java.util.Iterator;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.tagkit.ColorTag;
import soot.tagkit.Host;
import soot.tagkit.JimpleLineNumberTag;
import soot.tagkit.PositionTag;
import soot.tagkit.Tag;

/**
 * Adds PositionTags to ValueBoxes to identify their position in the output.
 */
public class AttributesUnitPrinter {
  private static final Logger logger = LoggerFactory.getLogger(AttributesUnitPrinter.class);

  private Stack<Integer> startOffsets;
  private int endOffset;
  private int startStmtOffset;
  private int startLn;
  private int currentLn;
  private int lastNewline;
  private UnitPrinter printer;

  public AttributesUnitPrinter(int currentLnNum) {
    this.currentLn = currentLnNum;
  }

  public void startUnit(Unit u) {
    startLn = currentLn;
    startStmtOffset = output().length() - lastNewline;
  }

  public void endUnit(Unit u) {
    int endStmtOffset = output().length() - lastNewline;
    // logger.debug("u: "+u.toString());
    if (hasTag(u)) {
      // logger.debug("u: "+u.toString()+" has tag");
      u.addTag(new JimpleLineNumberTag(startLn, currentLn));
    }
    if (hasColorTag(u)) {
      u.addTag(new PositionTag(startStmtOffset, endStmtOffset));
    }
  }

  public void startValueBox(ValueBox u) {
    if (startOffsets == null) {
      startOffsets = new Stack<Integer>();
    }
    startOffsets.push(new Integer(output().length() - lastNewline));
  }

  public void endValueBox(ValueBox u) {
    endOffset = output().length() - lastNewline;
    if (hasColorTag(u)) {
      u.addTag(new PositionTag(startOffsets.pop().intValue(), endOffset));
    }
  }

  private boolean hasTag(Host h) {
    if (h instanceof Unit) {
      Iterator<ValueBox> usesAndDefsIt = ((Unit) h).getUseAndDefBoxes().iterator();
      while (usesAndDefsIt.hasNext()) {
        if (hasTag(usesAndDefsIt.next())) {
          return true;
        }
      }
    }
    if (h.getTags().isEmpty()) {
      return false;
    }
    return true;
  }

  private boolean hasColorTag(Host h) {
    for (Tag t : h.getTags()) {
      if (t instanceof ColorTag) {
        return true;
      }
    }
    return false;
  }

  public void setEndLn(int ln) {
    currentLn = ln;
  }

  public int getEndLn() {
    return currentLn;
  }

  public void newline() {
    currentLn++;
    lastNewline = output().length();
  }

  private StringBuffer output() {
    return printer.output();
  }

  public void setUnitPrinter(UnitPrinter up) {
    printer = up;
  }
}
