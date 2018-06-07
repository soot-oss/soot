package soot.plugins;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2013 Bernhard J. Berger
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

import soot.Transformer;
import soot.plugins.model.PhasePluginDescription;

/**
 * An interface every phase plugin has to implement. The plugin has to support basic functionality, such as creating the
 * transformer and support a list of possible options. The first method that is called after object creation is
 * {@code setDescription} then the list of possible parameters is called and the last step is the call to
 * {@code getTransformer}.
 *
 * @author Bernhard J. Berger
 */
public interface SootPhasePlugin {
  /**
   * Default option for enabling a plugin.
   */
  public final String ENABLED_BY_DEFAULT = "enabled:true";

  /**
   * @return a list of phase options.
   */
  public abstract String[] getDeclaredOptions();

  /**
   * Returns a list of default values for initializing the parameters. Each entry in the list is of kind
   * "<parameter-name>:<default-value>". Please note, that you have to add the {@code ENABLED_BY_DEFAULT} option if you want
   * the plugin to be enabled.
   *
   * @return a list of default values.
   */
  public abstract String[] getDefaultOptions();

  /**
   * Creates a new transformer instance (either SceneTransformer or BodyTransformer). The method will be called just once.
   *
   * @return a new transformer instance.
   */
  public abstract Transformer getTransformer();

  public abstract void setDescription(final PhasePluginDescription pluginDescription);
}
