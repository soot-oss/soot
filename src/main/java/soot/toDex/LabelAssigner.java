package soot.toDex;

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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jf.dexlib2.builder.Label;
import org.jf.dexlib2.builder.MethodImplementationBuilder;

import soot.jimple.Stmt;
import soot.toDex.instructions.AbstractPayload;
import soot.toDex.instructions.SwitchPayload;

public class LabelAssigner {

  private final MethodImplementationBuilder builder;

  private int lastLabelId = 0;

  private Map<Stmt, Label> stmtToLabel = new HashMap<Stmt, Label>();
  private Map<Stmt, String> stmtToLabelName = new HashMap<Stmt, String>();

  private Map<AbstractPayload, Label> payloadToLabel = new HashMap<AbstractPayload, Label>();
  private Map<AbstractPayload, String> payloadToLabelName = new HashMap<AbstractPayload, String>();

  public LabelAssigner(MethodImplementationBuilder builder) {
    this.builder = builder;
  }

  public Label getOrCreateLabel(Stmt stmt) {
    if (stmt == null) {
      throw new RuntimeException("Cannot create label for NULL statement");
    }

    Label lbl = stmtToLabel.get(stmt);
    if (lbl == null) {
      String labelName = "l" + lastLabelId++;
      lbl = builder.getLabel(labelName);
      stmtToLabel.put(stmt, lbl);
      stmtToLabelName.put(stmt, labelName);
    }
    return lbl;
  }

  public Label getOrCreateLabel(AbstractPayload payload) {
    if (payload == null) {
      throw new RuntimeException("Cannot create label for NULL payload");
    }

    Label lbl = payloadToLabel.get(payload);
    if (lbl == null) {
      String labelName = "l" + lastLabelId++;
      lbl = builder.getLabel(labelName);
      payloadToLabel.put(payload, lbl);
      payloadToLabelName.put(payload, labelName);
    }
    return lbl;
  }

  public Label getLabel(Stmt stmt) {
    Label lbl = getLabelUnsafe(stmt);
    if (lbl == null) {
      throw new RuntimeException("Statement has no label: " + stmt);
    }
    return lbl;
  }

  public Label getLabelUnsafe(Stmt stmt) {
    return stmtToLabel.get(stmt);
  }

  public Label getLabel(SwitchPayload payload) {
    Label lbl = payloadToLabel.get(payload);
    if (lbl == null) {
      throw new RuntimeException("Switch payload has no label: " + payload);
    }
    return lbl;
  }

  public String getLabelName(Stmt stmt) {
    return stmtToLabelName.get(stmt);
  }

  public String getLabelName(AbstractPayload payload) {
    return payloadToLabelName.get(payload);
  }

  public Label getLabelAtAddress(int address) {
    for (Label lb : stmtToLabel.values()) {
      if (lb.isPlaced() && lb.getCodeAddress() == address) {
        return lb;
      }
    }
    return null;
  }

  public Collection<Label> getAllLabels() {
    return stmtToLabel.values();
  }

}
