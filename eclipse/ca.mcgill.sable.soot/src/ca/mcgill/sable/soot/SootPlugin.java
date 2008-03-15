/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jennifer Lhotak
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

package ca.mcgill.sable.soot;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ca.mcgill.sable.soot.editors.ColorManager;
import ca.mcgill.sable.soot.interaction.DataKeeper;
import ca.mcgill.sable.soot.launching.ISootOutputEventListener;
import ca.mcgill.sable.soot.launching.SootDocument;
import ca.mcgill.sable.soot.launching.SootOutputEvent;
import ca.mcgill.sable.soot.resources.SootPartManager;
import ca.mcgill.sable.soot.resources.SootResourceManager;
import ca.mcgill.sable.soot.resources.SootWorkbenchListener;



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
	
	// list of jimple editor viewers
	private ArrayList editorViewers = new ArrayList();
	
	private SootPartManager partManager;
	
	private ColorManager colorManager;
	
	private DataKeeper dataKeeper;
	
	private Font sootFont = new Font(null, "Arial", 8, SWT.NORMAL);
	
	private IProject currentProject;

    private MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}
    
    public MessageConsole getConsole() {
    	if(console==null) {
    		console = findConsole("Soot");
    	}
		return console;
    }
    
    public void showConsole() {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		conMan.showConsoleView(getConsole());
    }
	
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
		
		PlatformUI.getWorkbench().addWindowListener(new SootWorkbenchListener());
		setPartManager(new SootPartManager());
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
		store.setDefault(Messages.getString("SootPlugin.classes"), "soot.Main"); //$NON-NLS-1$ //$NON-NLS-2$
		store.setDefault(Messages.getString("SootPlugin.selected"), "soot.Main"); //$NON-NLS-1$ //$NON-NLS-2$
	}	

	private SootResourceManager manager;
	private MessageConsole console;

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
	
	public void addEditorViewer(ISourceViewer viewer) {
		viewer.addTextListener(getManager());
		getEditorViewers().add(viewer);
	}

	/**
	 * @return
	 */
	public ArrayList getEditorViewers() {
		return editorViewers;
	}

	/**
	 * @param list
	 */
	public void setEditorViewers(ArrayList list) {
		editorViewers = list;
	}

	/**
	 * @return
	 */
	public SootPartManager getPartManager() {
		return partManager;
	}

	/**
	 * @param manager
	 */
	public void setPartManager(SootPartManager manager) {
		partManager = manager;
	}

	/**
	 * @return
	 */
	public ColorManager getColorManager() {
		if (colorManager == null ){
			colorManager = new ColorManager();
		}
		return colorManager;
	}

	/**
	 * @param manager
	 */
	public void setColorManager(ColorManager manager) {
		colorManager = manager;
	}

	/**
	 * @return
	 */
	public DataKeeper getDataKeeper() {
		return dataKeeper;
	}

	/**
	 * @param keeper
	 */
	public void setDataKeeper(DataKeeper keeper) {
		dataKeeper = keeper;
	}

	/**
	 * @return
	 */
	public Font getSootFont() {
		return sootFont;
	}

	/**
	 * @param font
	 */
	public void setSootFont(Font font) {
		sootFont = font;
	}

	/**
	 * @return
	 */
	public IProject getCurrentProject() {
		return currentProject;
	}

	/**
	 * @param project
	 */
	public void setCurrentProject(IProject project) {
		currentProject = project;
	}

}
