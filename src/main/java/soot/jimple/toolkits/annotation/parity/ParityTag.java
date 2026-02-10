package soot.jimple.toolkits.annotation.parity;

import soot.jimple.toolkits.annotation.parity.ParityAnalysis.Parity;
/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Marc Miltenberger
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
import soot.tagkit.Tag;

/**
 * Contains information about the results of the parity analysis
 */
public class ParityTag implements Tag {

  private static final String NAME = "Parity";
  private Parity parity;

  public ParityTag(Parity parity) {
    this.parity = parity;
  }

  /**
   * Returns the parity
   * 
   * @return the parity
   */
  public Parity getParity() {
    return parity;
  }

  @Override
  public String getName() {
    return NAME;
  }

  public static Tag v(Parity result) {
    return new ParityTag(result);
  }

}
