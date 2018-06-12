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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SynchronizedRegionFlowPair {
  private static final Logger logger = LoggerFactory.getLogger(SynchronizedRegionFlowPair.class);
  // Information about the transactional region
  public CriticalSection tn;
  public boolean inside;

  SynchronizedRegionFlowPair(CriticalSection tn, boolean inside) {
    this.tn = tn;
    this.inside = inside;
  }

  SynchronizedRegionFlowPair(SynchronizedRegionFlowPair tfp) {
    this.tn = tfp.tn;
    this.inside = tfp.inside;
  }

  public void copy(SynchronizedRegionFlowPair tfp) {
    tfp.tn = this.tn;
    tfp.inside = this.inside;
  }

  public SynchronizedRegionFlowPair clone() {
    return new SynchronizedRegionFlowPair(tn, inside);
  }

  public boolean equals(Object other) {
    // logger.debug(".");
    if (other instanceof SynchronizedRegionFlowPair) {
      SynchronizedRegionFlowPair tfp = (SynchronizedRegionFlowPair) other;
      if (this.tn.IDNum == tfp.tn.IDNum) {
        return true;
      }
    }
    return false;
  }

  public String toString() {
    return "[" + (inside ? "in," : "out,") + tn.toString() + "]";
  }
}
