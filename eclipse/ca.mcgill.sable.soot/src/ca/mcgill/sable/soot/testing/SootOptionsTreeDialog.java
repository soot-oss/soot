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

package ca.mcgill.sable.soot.testing;

//import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.*;
//import org.eclipse.jface.dialogs.IDialogSettings;
//import org.eclipse.jface.preference.PreferenceDialog;
//import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.*;

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

/*
 * this class is not used
 */
public class SootOptionsTreeDialog extends TitleAreaDialog {
	
	private SashForm sashForm;
	
	public SootOptionsTreeDialog(Shell parentShell) {
		super(parentShell);
	}

	private Composite createSelectionComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		//System.out.println("creating selection composite");
		Tree optionsTree = new Tree(parent, SWT.SINGLE);
		
		TreeItem test1 = new TreeItem(optionsTree, SWT.NONE);
		test1.setText("Jimple Body Creation");
		TreeItem test2 = new TreeItem(optionsTree, SWT.NONE);
		test2.setText("Jimple Optimization Pack");
		TreeItem test3 = new TreeItem(test2, SWT.NONE);
		test3.setText("Busy Code Motion");
		
		return parent;
	}

	private Composite createDataComposite(Composite parent) {
		//Composite composite = new Composite(parent, SWT.NONE);
		//System.out.println("creating data composite");
		Label l1 = new Label(parent, SWT.NONE);
		l1.setText("Smile");
		
		return parent;
	}
	
	protected Control createDialogArea(Composite parent) {
		GridData gd;
		
		Composite dialogComp = (Composite)super.createDialogArea(parent);
		Composite topComp = new Composite(dialogComp, SWT.NONE);
		
		gd = new GridData(GridData.FILL_BOTH);
		topComp.setLayoutData(gd);
		GridLayout topLayout = new GridLayout();
		topLayout.numColumns = 2;
		topLayout.marginHeight = 5;
		topLayout.marginWidth = 0;
		topComp.setLayout(topLayout);
		
		// Set the things that TitleAreaDialog takes care of
		setTitle("Soot Launching Options"); 
		setMessage(""); 
		

		// Create the SashForm that contains the selection area on the left,
		// and the edit area on the right
		setSashForm(new SashForm(topComp, SWT.NONE));
		getSashForm().setOrientation(SWT.HORIZONTAL);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		getSashForm().setLayoutData(gd);
		
		Composite selection = createSelectionComposite(getSashForm());
		gd = new GridData(GridData.FILL_VERTICAL);
		selection.setLayoutData(gd);
		
		Composite data = createDataComposite(getSashForm());
		gd = new GridData(GridData.FILL_BOTH);
		data.setLayoutData(gd);
		
		Label separator = new Label(topComp, SWT.HORIZONTAL | SWT.SEPARATOR);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		separator.setLayoutData(gd);
		
		dialogComp.layout(true);
		
		return dialogComp;
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
