package ca.mcgill.sable.soot.ui;

import java.util.*;


//import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.jface.dialogs.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.launching.SootSavedConfiguration;
import ca.mcgill.sable.soot.testing.*;

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
public class EditSavedConfigDialog extends ElementListSelectionDialog {

	private HashMap editMap;
	private ArrayList deleteList;
	private ArrayList configElements;
	/**
	 * Constructor for EditSavedConfigDialog.
	 * @param parent
	 * @param renderer
	 */

	public EditSavedConfigDialog(Shell parent, ILabelProvider renderer, ArrayList configElements) {
		super(parent, renderer);
		setConfigElements(configElements);
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, 0, "Edit", false);
		createButton(parent, 1, "Remove", false);
		// create OK and Cancel buttons by default
		createButton(parent, 2, "Apply", true);
		createButton(parent, 3, "Revert", false);
	}
	
	protected void buttonPressed(int id) {
		switch (id) {
			case 0: {
				editPressed();
				break;
			}
			case 1: {
				removePressed();
				break;
			}
			case 2: {
				okPressed();
				break;
			}
			case 3: {
				cancelPressed();
				break;
			}
			 
		}
	}
	
	private void editPressed() {
		Object [] temp = this.getSelectedElements();
		String result = (String)temp[0];
		System.out.println("result selected: "+result);
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		String saved = settings.get(result);
		System.out.println("saved: "+saved);
		SootSavedConfiguration ssc = new SootSavedConfiguration(result, saved);
		HashMap structConfig = ssc.toHashMap();
		PhaseOptionsDialog dialog = new PhaseOptionsDialog(getShell());
		addEclipseDefsToDialog(dialog);
		System.out.println("created dialog");
		Iterator it = structConfig.keySet().iterator();
		while (it.hasNext()) {
			String key = (String)it.next();
			String val = (String)structConfig.get(key);
			if ((val.equals("true")) || (val.equals("false"))) {
				dialog.addToDefList(key, new Boolean(val));
			}
			else {
				dialog.addToDefList(key, val);
			}
		}
		System.out.println("added defaults to dialog");
		dialog.setConfigName(result);
		dialog.setCanRun(false);
		dialog.open();
		
		setEditMap(dialog.getEditMap());
		//setDeleteList(dialog.getDeleteList());
		// here need to do something if dialog ok pressed (run soot)
		
		// use hashmap to set init vals in phaseoptionsdialog and open dialog
		
	}
	
	private void removePressed() {
		Object [] temp = this.getSelectedElements();
		String result = (String)temp[0];
		if (getDeleteList() == null) {
			setDeleteList(new ArrayList());
		}
		getDeleteList().add(result);
		System.out.println("before removing size: "+getConfigElements().size());
		getConfigElements().remove(result);
		System.out.println("after removing size: "+getConfigElements().size());
		this.setElements(getConfigElements().toArray());
		this.getShell().redraw();
		
		
	}
	
	private void addEclipseDefsToDialog(PhaseOptionsDialog dialog) {
		if (getEclipseDefList() == null) return;
		Iterator it = getEclipseDefList().keySet().iterator();
		while (it.hasNext()) {
			String key = (String)it.next();
			dialog.addToEclipseDefList(key, getEclipseDefList().get(key));
		}
	}
	
	private HashMap eclipseDefList;
	
	
	public void addToEclipseDefList(String key, Object val) {
		if (getEclipseDefList() == null) {
			setEclipseDefList(new HashMap());
		}
		getEclipseDefList().put(key, val);
	}

	/**
	 * Returns the eclipseDefList.
	 * @return HashMap
	 */
	public HashMap getEclipseDefList() {
		return eclipseDefList;
	}

	/**
	 * Sets the eclipseDefList.
	 * @param eclipseDefList The eclipseDefList to set
	 */
	public void setEclipseDefList(HashMap eclipseDefList) {
		this.eclipseDefList = eclipseDefList;
	}

	/**
	 * Returns the deleteList.
	 * @return ArrayList
	 */
	public ArrayList getDeleteList() {
		return deleteList;
	}

	/**
	 * Returns the editList.
	 * @return HashMap
	 */
	public HashMap getEditMap() {
		return editMap;
	}

	/**
	 * Sets the deleteList.
	 * @param deleteList The deleteList to set
	 */
	public void setDeleteList(ArrayList deleteList) {
		this.deleteList = deleteList;
	}

	/**
	 * Sets the editList.
	 * @param editList The editList to set
	 */
	public void setEditMap(HashMap editMap) {
		this.editMap = editMap;
	}

	/**
	 * Returns the configElements.
	 * @return String[]
	 */
	public ArrayList getConfigElements() {
		return configElements;
	}

	/**
	 * Sets the configElements.
	 * @param configElements The configElements to set
	 */
	public void setConfigElements(ArrayList configElements) {
		this.configElements = configElements;
	}

}
