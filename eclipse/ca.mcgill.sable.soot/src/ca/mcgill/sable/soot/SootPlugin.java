package ca.mcgill.sable.soot;

import org.eclipse.ui.plugin.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.resources.*;
import java.util.*;

import ca.mcgill.sable.soot.attributes.SootAttributeFilesReader;
import ca.mcgill.sable.soot.attributes.SootAttributesHandler;
import ca.mcgill.sable.soot.launching.*;

/**
 * @author jlhotak
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

/**
 * The main plugin class to be used in the desktop.
 */
public class SootPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static SootPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	// used for showing soot ouptut
	private SootDocument soot_output_doc;
	
	// listeners for soot output events
	private Vector sootOutputEventListeners = new Vector();
	
	// attribute handler - only one
	private SootAttributesHandler sootAttributesHandler;
	/**
	 * Method addSootOutputEventListener.
	 * @param listener
	 */
	public void addSootOutputEventListener(ISootOutputEventListener listener) {
		sootOutputEventListeners.add(listener);
	}
	
	/**
	 * Method removeSootOutputEventListener.
	 * @param listener
	 */
	public void removeSootOutputEventListener(ISootOutputEventListener listener) {
		sootOutputEventListeners.remove(listener);
	}
	
	/**
	 * Method fireSootOutputEvent.
	 * @param event
	 */
	public void fireSootOutputEvent(SootOutputEvent event) {
		Iterator it = sootOutputEventListeners.iterator();
		while (it.hasNext()) {
			((ISootOutputEventListener)it.next()).handleSootOutputEvent(event);
		}
	}
	
	/**
	 * The constructor.
	 */
	public SootPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		// should work from startUp method
		soot_output_doc = new SootDocument();
		soot_output_doc.startUp();
		// should go somewhere else - here for testing
		//setSootAttributesHandler(new SootAttributesHandler());
		//SootAttributeFilesReader safr = new SootAttributeFilesReader();
		//safr.readFiles();
		try {
			resourceBundle= ResourceBundle.getBundle("ca.mcgill.sable.soot.SootPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static SootPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle= SootPlugin.getDefault().getResourceBundle();
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
	
	/**
	 * Method startUp.
	 * @throws CoreException
	 */
	public void startUp() throws CoreException {
		super.startup();
		System.out.println("starting up plugin");
		soot_output_doc = new SootDocument();
		soot_output_doc.startUp();		
	}
	
	/**
	 * @see org.eclipse.core.runtime.Plugin#shutdown()
	 */
	public void shutdown() throws CoreException {	
		super.shutdown();
		sootOutputEventListeners.removeAllElements();
	}
	/**
	 * Returns the sootAttributesHandler.
	 * @return SootAttributesHandler
	 */
	public SootAttributesHandler getSootAttributesHandler() {
		return sootAttributesHandler;
	}

	/**
	 * Sets the sootAttributesHandler.
	 * @param sootAttributesHandler The sootAttributesHandler to set
	 */
	public void setSootAttributesHandler(SootAttributesHandler sootAttributesHandler) {
		this.sootAttributesHandler = sootAttributesHandler;
	}

}
