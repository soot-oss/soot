package ca.mcgill.sable.soot;

import org.eclipse.ui.plugin.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.resources.*;
import org.eclipse.jface.preference.*;
import org.eclipse.jface.resource.*;

import java.net.*;
import java.util.*;

import ca.mcgill.sable.soot.launching.*;
import ca.mcgill.sable.soot.resources.*;

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
	
		try {
			resourceBundle= ResourceBundle.getBundle(ISootConstants.SOOT_PLUGIN_RESOURCES_ID);
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		
		// maybe should go in startUp method
		// resource manager
		setManager(new SootResourceManager());
		//getManager().initialize();
		
	}
	
	// used for getting any needed images for content outline
	// and possibly for attribute markers
	public static ImageDescriptor getImageDescriptor(String name){
		try {
			URL installURL = getDefault().getDescriptor().getInstallURL();
			URL iconURL = new URL(installURL, ISootConstants.ICON_PATH + name);
			return ImageDescriptor.createFromURL(iconURL);
		}
		catch (MalformedURLException e){
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}
	
	
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		// These settings will show up when Preference dialog
		// opens up for the first time.
		store.setDefault("classes", "soot.Main");
		store.setDefault("selected", "soot.Main");
	}	

	private SootResourceManager manager;

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
	 * @return
	 */
	public SootResourceManager getManager() {
		return manager;
	}

	/**
	 * @param manager
	 */
	public void setManager(SootResourceManager manager) {
		this.manager = manager;
	}

}
