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

import java.io.PrintStream;
import soot.Pack;
import soot.G;

/**
 * @author Michael Batchelder 
 * 
 * Created on 19-Jun-2006 
 */
public interface IJbcoTransform {

  public PrintStream out = soot.G.v().out;
  public boolean output = G.v().soot_options_Options().verbose() || soot.jbco.Main.jbcoVerbose;
  public boolean debug = soot.jbco.Main.jbcoDebug;
  public void outputSummary();
  public String[] getDependancies();
  public String getName();
}
