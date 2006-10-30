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

package soot.jbco.bafTransformations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.jbco.IJbcoTransform;
import soot.util.Chain;

/**
 * @author Michael Batchelder 
 * 
 * Created on 16-Jun-2006 
 */
public class Jimple2BafLocalBuilder extends BodyTransformer implements IJbcoTransform {

  public static String dependancies[] = new String[] {"jtp.jbco_jl","bb.jbco_j2bl","bb.lp"};

  public String[] getDependancies() {
    return dependancies;
  }
  
  public static String name = "bb.jbco_j2bl";
  
  public String getName() {
    return name;
  }
  
  public void outputSummary() {}

  private static boolean runOnce = false;
  
  protected void internalTransform(Body b, String phaseName, Map options) {
    if (soot.jbco.Main.methods2JLocals.size() == 0) { 
      if (!runOnce) {
        runOnce = true;
        out.println("[Jimple2BafLocalBuilder]:: Jimple Local Lists have not been built");
        out.println("                           Skipping Jimple To Baf Builder\n");
      }
      return;
    }
      
    Chain bLocals = b.getLocals();
    HashMap bafToJLocals = new HashMap();
    Iterator jlocIt = ((ArrayList) soot.jbco.Main.methods2JLocals.get(b.getMethod())).iterator();
    while (jlocIt.hasNext()) {
      Local jl = (Local) jlocIt.next();
      Iterator blocIt = bLocals.iterator();
      while (blocIt.hasNext()) {
        Local bl = (Local) blocIt.next();
        if (bl.getName().equals(jl.getName())) {
          bafToJLocals.put(bl, jl);
          break;
        }
      }
    }
    soot.jbco.Main.methods2Baf2JLocals.put(b.getMethod(),bafToJLocals);
  }
}
