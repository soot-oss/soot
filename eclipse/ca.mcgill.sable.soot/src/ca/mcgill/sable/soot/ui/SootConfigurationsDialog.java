package ca.mcgill.sable.soot.ui;

import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;

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
public class SootConfigurationsDialog
	extends TitleAreaDialog
	implements ISelectionListener {

	private SashForm sashForm;

	/**
	 * Constructor for SootConfigurationsDialog.
	 * @param parentShell
	 */
	public SootConfigurationsDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/**
	 * creates a sash form - one side for a list of configurations 
	 * and the other for the new, edit, delete, etc buttons 
	 */
	protected Control createDialogArea(Composite parent) {
		
		
		Composite composite = (Composite)super.createDialogArea(parent);
		GridLayout topLayout = new GridLayout();
		composite.setLayout(topLayout);
		
		// Set the things that TitleAreaDialog takes care of
		// TODO: externalize this title
		setTitle("Soot Configurations Manager");
		setMessage(""); 

		setSashForm(new SashForm(composite, SWT.NONE));
		getSashForm().setOrientation(SWT.HORIZONTAL);
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		getSashForm().setLayoutData(gd);
		
		return composite;
	}
	/**
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
	}

	/**
	 * Returns the sashForm.
	 * @return SashForm
	 */
	public SashForm getSashForm() {
		return sashForm;
	}

	/**
	 * Sets the sashForm.
	 * @param sashForm The sashForm to set
	 */
	public void setSashForm(SashForm sashForm) {
		this.sashForm = sashForm;
	}

}
