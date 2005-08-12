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


package ca.mcgill.sable.soot.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.*;
import org.eclipse.core.runtime.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;
import ca.mcgill.sable.soot.*;




public class SootOptionsLauncherDialog extends Dialog {
	protected IAdaptable input;
	private Text text_input;
	private Label text_label;

	public SootOptionsLauncherDialog(Shell parentShell, IAdaptable input) {
		super(parentShell);
		this.input = input;
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
	
		text_label = new Label(composite, SWT.LEFT);			
		text_label.setText("Set Soot Command Line:");
	
		text_input = new Text(composite, SWT.CENTER);
		IDialogSettings settings = null;
		try {
			settings = SootPlugin.getDefault().getDialogSettings();
		}
		catch (Exception e1) {
			System.out.println(e1.getMessage());	
		}
		if (settings != null) {
			String input = settings.get("text_input");
			if (input != null) {
				text_input.setText(input);
			}
		}
		text_input.setSize(200,20);
				
		return composite;
		
	}
	
	protected void okPressed() {
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		settings.put("text_input", text_input.getText());
		super.okPressed();
			
	}
	
	
}
