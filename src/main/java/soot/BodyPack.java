package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai and Patrick Lam
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.options.Options;
import soot.toolkits.graph.interaction.InteractionHandler;

/**
 * A wrapper object for a pack of optimizations. Provides chain-like operations, except that the key is the phase name.
 */
public class BodyPack extends Pack {
  private static final Logger logger = LoggerFactory.getLogger(BodyPack.class);

  public BodyPack(String name) {
    super(name);
  }

  protected void internalApply(Body b) {
    for (Iterator<Transform> tIt = this.iterator(); tIt.hasNext();) {
      final Transform t = tIt.next();
      if (Options.v().interactive_mode()) {
        // logger.debug("sending transform: "+t.getPhaseName()+" for body: "+b+" for body pack: "+this.getPhaseName());
        InteractionHandler.v().handleNewAnalysis(t, b);
      }
      t.apply(b);
      if (Options.v().interactive_mode()) {
        InteractionHandler.v().handleTransformDone(t, b);
      }
    }
  }

}
