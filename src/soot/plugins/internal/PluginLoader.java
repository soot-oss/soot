/* Soot - a J*va Optimization Framework
 * 
 * Copyright (C) 2013 Bernhard J. Berger
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

package soot.plugins.internal;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import soot.G;
import soot.PackManager;
import soot.Transform;
import soot.plugins.SootPhasePlugin;
import soot.plugins.model.PhasePluginDescription;
import soot.plugins.model.PluginDescription;
import soot.plugins.model.Plugins;

/**
 * Class for loading xml-based plugin configuration files.
 * 
 * @author Bernhard J. Berger
 */
public class PluginLoader {

	/**
	 * Loads the plugin configuration file {@code file} and registers the plugins.
	 * 
	 * @param file the plugin configuration file.
	 * @return {@code true} on success.
	 */
	public static boolean load(final String file) {
		final File configFile = new File(file);
		
		if(!configFile.exists()) {
			System.err.println("The configuration file '" + configFile + "' does not exist.");
			return false;
		}

		if(!configFile.canRead()) {
			System.err.println("Cannot read the configuration file '" + configFile + "'.");
			return false;
		}
		
		try {
		    final JAXBContext context = JAXBContext.newInstance(Plugins.class, PluginDescription.class, PhasePluginDescription.class);
		    final Unmarshaller unmarshaller = context.createUnmarshaller();
		   	final Object root = unmarshaller.unmarshal(configFile);
	
		   	if(!(root instanceof Plugins)) {
		   		System.err.println("Expected a root node of type Plugins got " + root.getClass());
		   		return false;
		   	}
		   	
		   	loadPlugins((Plugins)root);
		} catch(final RuntimeException e) {
   			System.err.println("Failed to load plugin correctly.");
   			e.printStackTrace(System.err);
   			return false;
   		} catch(final JAXBException e) {
			System.err.println("An error occured while loading plugin configuration '" + file + "'.");
			e.printStackTrace(System.err);
			return false;
		}
		
		return true;
	}

	/**
	 * Load all plugins. Can be called by a custom main function.
	 * @param plugins the plugins to load.
	 * 
	 * @throws RuntimeException if an error occurs during loading.
	 */
	public static void loadPlugins(final Plugins plugins) throws RuntimeException {
		for(final PluginDescription plugin : plugins.getPluginDescriptions()) {
			if(plugin instanceof PhasePluginDescription) {
				handlePhasePlugin((PhasePluginDescription)plugin);
			} else {
				G.v().out.println("[Warning] Unhandled plugin of type '" + plugin.getClass() + "'");
			}
		}
	}

	/**
	 * Loads the phase plugin and adds it to PackManager.
	 * @param pluginDescription the plugin description instance read from configuration file.
	 */
	private static void handlePhasePlugin(final PhasePluginDescription pluginDescription) {
		try {
			Class<?> clazz = Class.forName(pluginDescription.getClassName());
			Object instance = clazz.newInstance();
			
			if(!(instance instanceof SootPhasePlugin)) {
				throw new RuntimeException("The plugin class '" + pluginDescription.getClassName() + "' does not implement SootPhasePlugin.");
			}
			
			final SootPhasePlugin phasePlugin = (SootPhasePlugin)instance;
			phasePlugin.setDescription(pluginDescription);
			
			final String packName = getPackName(pluginDescription.getPhaseName());
			
			Transform transform = new Transform(pluginDescription.getPhaseName(), phasePlugin.getTransformer());
			transform.setDeclaredOptions(concat(appendEnabled(phasePlugin.getDeclaredOptions())));
			transform.setDefaultOptions(concat(phasePlugin.getDefaultOptions()));
			PackManager.v().getPack(packName).add(transform);
			
		} catch (final ClassNotFoundException e) {
			throw new RuntimeException("Failed to load plugin class for " + pluginDescription + ".", e);
		} catch (final InstantiationException e) {
			throw new RuntimeException("Failed to instanciate plugin class for " + pluginDescription + ".", e);
		} catch (final IllegalAccessException e) {
			throw new RuntimeException("Not allowed to access plugin class for " + pluginDescription + ".", e);
		}		
	}

	/**
	 * Each phase has to support the enabled option. We will add it if necessary.
	 * @param declaredOptions Options declared by the plugin.
	 * @return option list definitly containing enabled.
	 */
	private static String[] appendEnabled(final String [] options) {
		for(final String option : options) {
			if(option.equals("enabled")) {
				return options;
			}
		}
		
		String [] result = new String[options.length + 1];
		result[0] = "enabled";
		System.arraycopy(options, 0, result, 1, options.length);

		return result;
	}

	/**
	 * Creates a space separated list from {@code declaredOptions}.
	 * @param options the list to transform.
	 * @return a string containing all options separated by a space.
	 */
	private static String concat(final String[] options) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		
		for(final String option : options) {
			if(!first) {
				sb.append(" ");
			}
			first = false;
			sb.append(option);
		}

		return sb.toString();
	}

	/**
	 * Splits a phase name and returns the pack name.
	 * 
	 * @param phaseName Name of the phase.
	 * @return the name of the pack.
	 */
	private static String getPackName(final String phaseName) {
		if(!phaseName.contains(".")) {
			throw new RuntimeException("Name of phase '" + phaseName + "'does not contain a dot.");
		}
		
		return phaseName.substring(0, phaseName.indexOf('.'));
	}
}