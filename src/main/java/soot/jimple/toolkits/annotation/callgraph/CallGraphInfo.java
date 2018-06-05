package soot.jimple.toolkits.annotation.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Jennifer Lhotak
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

import soot.SootMethod;

public class CallGraphInfo {

  private ArrayList<MethInfo> inputs = new ArrayList<MethInfo>();
  private ArrayList<MethInfo> outputs = new ArrayList<MethInfo>();
  private SootMethod center;

  public CallGraphInfo(SootMethod sm, ArrayList<MethInfo> outputs, ArrayList<MethInfo> inputs) {
    setCenter(sm);
    setOutputs(outputs);
    setInputs(inputs);
  }

  public void setCenter(SootMethod sm) {
    center = sm;
  }

  public SootMethod getCenter() {
    return center;
  }

  public ArrayList<MethInfo> getInputs() {
    return inputs;
  }

  public void setInputs(ArrayList<MethInfo> list) {
    inputs = list;
  }

  public ArrayList<MethInfo> getOutputs() {
    return outputs;
  }

  public void setOutputs(ArrayList<MethInfo> list) {
    outputs = list;
  }

}
