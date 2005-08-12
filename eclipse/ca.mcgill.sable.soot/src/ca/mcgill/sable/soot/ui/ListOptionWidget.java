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

import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;

public class ListOptionWidget {

	private Label label;
	private Text text;
	private String alias;
	
	
	/**
	 * Constructor for PathOptionClass.
	 * @param parent
	 * @param style
	 */
	public ListOptionWidget(Composite parent, int style, 
	  OptionData data) {
		
		setAlias(data.getRealAlias());
		
		Group path = new Group(parent, SWT.RIGHT);
		GridLayout gl = new GridLayout();
		gl.numColumns = 3;	
		
		path.setLayout(gl);

		// this makes widget fill horizontal space
		GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
       	gridData2.horizontalSpan = 3;

       	path.setLayoutData(gridData2);
        			
		setLabel(new Label(path, SWT.NONE));
		setLabelText(data.getText());
		setText(new Text(path,  SWT.MULTI | SWT.BORDER | SWT.V_SCROLL));
		

		if ((data.getInitText() == null) || (data.getInitText().length() == 0)){
			getText().setText("");
		}
		else {
			getText().setText(data.getInitText());
		}
		String listMessage = " Separate values on different lines.";
		getText().setToolTipText(data.getTooltip().trim()+listMessage);		
	
		// this makes textbox fill available space		
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
		gridData.heightHint = 35;
		getText().setLayoutData(gridData);
		
		
	}


	public void setLabelText(String text) {
		getLabel().setText(text);
	}

	/**
	 * Returns the label.
	 * @return Label
	 */
	public Label getLabel() {
		return label;
	}

	/**
	 * Returns the text.
	 * @return Text
	 */
	public Text getText() {
		return text;
	}

	/**
	 * Sets the label.
	 * @param label The label to set
	 */
	public void setLabel(Label label) {
		this.label = label;
	}

	/**
	 * Sets the text.
	 * @param text The text to set
	 */
	public void setText(Text text) {
		this.text = text;
	}

	

	/**
	 * Returns the alias.
	 * @return String
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * Sets the alias.
	 * @param alias The alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

}
