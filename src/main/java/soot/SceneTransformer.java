package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Patrick Lam
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

import java.util.HashMap;
import java.util.Map;

/** An abstract class which acts on the whole Scene. */
public abstract class SceneTransformer extends Transformer {
  /** Performs the transformation on the Scene, under the given phaseName. */
  public final void transform(String phaseName, Map<String, String> options) {
    if (!PhaseOptions.getBoolean(options, "enabled")) {
      return;
    }

    internalTransform(phaseName, options);
  }

  public final void transform(String phaseName) {
    HashMap<String, String> dummyOptions = new HashMap<String, String>();
    dummyOptions.put("enabled", "true");
    transform(phaseName, dummyOptions);
  }

  public final void transform() {
    transform("");
  }

  /** Performs the transformation on the Scene, under the given phaseName and with the given Options. */
  protected abstract void internalTransform(String phaseName, Map<String, String> options);

}
