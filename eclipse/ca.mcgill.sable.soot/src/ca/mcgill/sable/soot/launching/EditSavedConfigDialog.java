package ca.mcgill.sable.soot.launching;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.jface.dialogs.*;
import ca.mcgill.sable.soot.*;
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

	/**
	 * Constructor for EditSavedConfigDialog.
	 * @param parent
	 * @param renderer
	 */
	public EditSavedConfigDialog(Shell parent, ILabelProvider renderer) {
		super(parent, renderer);
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, 0, "Edit", false);
		createButton(parent, 1, "Remove", false);
		// create OK and Cancel buttons by default
		createButton(parent, 2, IDialogConstants.OK_LABEL, true);
		createButton(parent, 3, IDialogConstants.CANCEL_LABEL, false);
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
		dialog.open();
		
		// use hashmap to set init vals in phaseoptionsdialog and open dialog
		
	}
	
	private void removePressed() {
	
	}

}
