package ca.mcgill.sable.soot.launching;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredList;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.*;

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
// not used
public class EditConfigDialog extends Dialog {

	private String [] initElems;
	private FilteredList list;
	
	/**
	 * Constructor for EditConfigDialog.
	 * @param parentShell
	 */
	public EditConfigDialog(Shell parentShell) {
		super(parentShell);
	}
	
	
	protected Control createDialogArea(Composite parent) {
	
		Composite comp = (Composite)super.createDialogArea(parent);
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		
		parent.setLayout(gl);
		
		setList(new FilteredList(parent, SWT.NONE, new LabelProvider(), true, false, false));
		getList().setElements(getInitElems());
		GridData gd = new GridData();
		gd.horizontalSpan = 1;
		getList().setLayoutData(gd);
		Button edit = new Button(parent, SWT.PUSH);
		edit.setText("Edit");
		Button remove = new Button(parent, SWT.PUSH);
		remove.setText("Remove");
		
		return comp;
		
		
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
		getList().getSelection();
	}
	
	private void removePressed() {
	
	}

	/**
	 * Returns the intiElems.
	 * @return String[]
	 */
	public String[] getInitElems() {
		return initElems;
	}

	/**
	 * Sets the intiElems.
	 * @param intiElems The intiElems to set
	 */
	public void setInitElems(String[] initElems) {
		this.initElems = initElems;
	}

	/**
	 * Returns the list.
	 * @return FilteredList
	 */
	public FilteredList getList() {
		return list;
	}

	/**
	 * Sets the list.
	 * @param list The list to set
	 */
	public void setList(FilteredList list) {
		this.list = list;
	}

}
