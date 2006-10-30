/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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

package soot.jbco.jimpleTransformations;

import java.util.*;

import soot.*;
import soot.jbco.IJbcoTransform;
import soot.jbco.util.*;
import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.util.Chain;

/**
 * @author Michael Batchelder
 * 
 * Created on 7-Feb-2006
 */
public class CollectJimpleLocals extends BodyTransformer implements IJbcoTransform {

  public void outputSummary() {}

  public static String dependancies[] = new String[] {"jtp.jbco_jl"};

  public String[] getDependancies() {
    return dependancies;
  }
  
  public static String name = "jtp.jbco_jl";
  
  public String getName() {
    return name;
  }
  
  protected void internalTransform(Body body, String phaseName, Map options) {
    Chain locals = body.getLocals();
    ArrayList locs = new ArrayList();
    locs.addAll(locals);
    
    soot.jbco.Main.methods2JLocals.put(body.getMethod(), locs);
  }
}