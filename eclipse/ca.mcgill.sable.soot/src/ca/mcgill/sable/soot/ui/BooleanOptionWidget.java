package ca.mcgill.sable.soot.ui;

import java.util.*;

import org.eclipse.swt.widgets.*;
//import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.*;
//import org.eclipse.swt.layout.*;

//import ca.mcgill.sable.soot.SootPlugin;

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
public class BooleanOptionWidget implements ISootOptionWidget {//extends Composite {

	private Button button;
	private String alias;
	private String labelText;
	private Composite dialogParent;
	private OptionData data;
	
	/**
	 * Constructor for BooleanOptionWidget.
	 * @param parent
	 * @param style
	 */
	//public BooleanOptionWidget(Composite parent, int style) {
	//	super(parent, style);
	//}
	
	public BooleanOptionWidget(Composite parent, int style,
	 OptionData data){
		//super(parent, style);
		setAlias(data.getRealAlias());
		setData(data);
		//setLabelText(data.getText());
		
		
				
       	//GridData gridData = new GridData(GridData.FILL_HORIZONTAL);

        //gridData.horizontalSpan = 4;
		//IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		//boolean defaultVal = false;
		//defaultVal = settings.getBoolean(getAlias());
				
		setButton(new Button(parent, SWT.CHECK));
		//getButton().setSelection(defaultVal);
		getButton().setSelection(data.isDefaultVal());
		getButton().setText(data.getText());
		getButton().setToolTipText(data.getTooltip());
		
		//getButton().setLayoutData(gridData);
	}
	
	public ArrayList getControls() {
		ArrayList controls = new ArrayList();
		controls.add(getButton());
		return controls;
	}
	
	public String getId(){
		return getAlias();
	}
	
	public void getWidget() {
		//setButton(newButton(parent
	}

	/**
	 * Returns the button.
	 * @return Button
	 */
	public Button getButton() {
		return button;
	}

	/**
	 * Sets the button.
	 * @param button The button to set
	 */
	public void setButton(Button button) {
		this.button = button;
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

	/**
	 * Returns the labelText.
	 * @return String
	 */
	public String getLabelText() {
		return labelText;
	}

	/**
	 * Sets the labelText.
	 * @param labelText The labelText to set
	 */
	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}

	/**
	 * @return
	 */
	public OptionData getData() {
		return data;
	}

	/**
	 * @param data
	 */
	public void setData(OptionData data) {
		this.data = data;
	}

}
