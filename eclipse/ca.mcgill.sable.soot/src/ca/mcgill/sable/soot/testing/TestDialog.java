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



/*
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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import ca.mcgill.sable.soot.SootPlugin;

public class TestDialog extends Dialog {
	protected IAdaptable input;
	
	private Label text_label;
	
		private Button jimp_button;
	
		private Button jasmin_button;
	
		private Button njimple_button;
	
		private Button jimple_button;
	
		private Button baf_button;
	
		private Button b_button;
	
		private Button grimp_button;
	
		private Button grimple_button;
	
		private Button class_button;
	
		private Button dava_button;
	
		private Button xml_button;
	
		private Label annotation_label;
		private Text annotation_text;
	

	public TestDialog(Shell parentShell, IAdaptable input) {
		super(parentShell);
		
		this.input = input;
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
	
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);
		this.getShell().setText("Select Soot Output Type:");
		
		jimp_button = new Button(composite, SWT.RADIO);
		jimp_button.setText("jimp");

		jasmin_button = new Button(composite, SWT.RADIO);
		jasmin_button.setText("jasmin");

		njimple_button = new Button(composite, SWT.RADIO);
		njimple_button.setText("njimple");

		jimple_button = new Button(composite, SWT.RADIO);
		jimple_button.setText("jimple");

		baf_button = new Button(composite, SWT.RADIO);
		baf_button.setText("baf");

		b_button = new Button(composite, SWT.RADIO);
		b_button.setText("b");

		grimp_button = new Button(composite, SWT.RADIO);
		grimp_button.setText("grimp");

		grimple_button = new Button(composite, SWT.RADIO);
		grimple_button.setText("grimple");

		class_button = new Button(composite, SWT.RADIO);
		class_button.setText("class");

		dava_button = new Button(composite, SWT.RADIO);
		dava_button.setText("dava");

		xml_button = new Button(composite, SWT.RADIO);
		xml_button.setText("xml");

		annotation_label = new Label(composite, SWT.CENTER);
		annotation_label.setText("annotation");
			
		annotation_text = new Text(composite, SWT.CENTER | SWT.BORDER);
		annotation_text.setSize(200,15);
								
		return composite;
		
	}
	
	protected void okPressed() {
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		
		
		super.okPressed();
				
	}

}

