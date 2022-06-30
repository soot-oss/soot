
package soot.jimple.toolkits.thread.mhp;

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

import java.util.Map;

import soot.G;
import soot.SceneTransformer;
import soot.Singletons;

/**
 *
 */

public class MhpTransformer extends SceneTransformer {
  public MhpTransformer(Singletons.Global g) {
  }

  public static MhpTransformer v() {
    return G.v().soot_jimple_toolkits_thread_mhp_MhpTransformer();
  }

  MhpTester mhpTester;

  protected void internalTransform(String phaseName, Map options) {
    getMhpTester().printMhpSummary();
  }

  public MhpTester getMhpTester() {
    if (mhpTester == null) {
      mhpTester = new SynchObliviousMhpAnalysis();
    }
    return mhpTester;
  }

  public void setMhpTester(MhpTester mhpTester) {
    this.mhpTester = mhpTester;
  }
}
