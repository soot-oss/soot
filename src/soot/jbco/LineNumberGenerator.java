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

package soot.jbco;

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Transform;
import soot.PackManager;
import soot.PatchingChain;
import soot.Unit;
import soot.tagkit.LineNumberTag;

public class LineNumberGenerator {
  
  BafLineNumberer bln = new BafLineNumberer();
  
  public static void main(String[] argv)
  {
    // if you do not want soot to output new class files, run with comman line option "-f n"
    
    // if you want the source code line numbers for jimple statements, use this:
    PackManager.v().getPack("jtp").add(new Transform("jtp.lnprinter",new LineNumberGenerator().bln));
    
    // if you want the source code line numbers for baf instructions, use this:
    PackManager.v().getPack("bb").add(new Transform("bb.lnprinter",new LineNumberGenerator().bln));
    
    soot.Main.main(argv);
  }
  
  class BafLineNumberer extends BodyTransformer {
    protected void internalTransform(Body b, String phaseName, Map options) {
      
      System.out.println("Printing Line Numbers for: " + b.getMethod().getSignature());
      
      PatchingChain units = b.getUnits(); // get the method code
      Iterator it = units.iterator();
      while (it.hasNext()) { // for each jimple statement or baf instruction
        Unit u = (Unit)it.next();
        if (u.hasTag("LineNumberTag")) { // see if a LineNumberTag exists (it will if you use -keep-line-number)
          LineNumberTag tag = (LineNumberTag)u.getTag(("LineNumberTag"));
          System.out.println(u + " has Line Number: " + tag.getLineNumber()); // print out the unit and line number
        } else {
          System.out.println(u + " has no Line Number");
        }
      }
      
      System.out.println("\n");
    }
  }
}