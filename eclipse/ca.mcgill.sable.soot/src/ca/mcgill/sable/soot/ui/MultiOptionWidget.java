package ca.mcgill.sable.soot.ui;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;
//import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import java.util.*;


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
public class MultiOptionWidget implements ISootOptionWidget {//extends Composite {

	private String alias;
	private OptionData [] dataVals;
	private String [] values;
	private String [] aliases;
	private Button [] buttons;
	//private String defaultVal;
	
	/**
	 * Constructor for MultiOptionWidget.
	 * @param parent
	 * @param style
	 */
	//public MultiOptionWidget(Composite parent, int style) {
	//	super(parent, style);
	//}
	
	/**
	 * Constructor for MultiOptionWidget.
	 * @param parent
	 * @param style
	 */
	public MultiOptionWidget(Composite parent, int style, 
		OptionData [] dataVals, OptionData data) {
		
		//super(parent, style);
		setAlias(data.getRealAlias());
		
		Group multi = new Group(parent, SWT.NONE);
		//FillLayout fl = new FillLayout();
		GridLayout gl = new GridLayout();
		gl.numColumns = 4;
		
		multi.setLayout(gl);
		
		GridData gridData2 = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
       	gridData2.horizontalSpan = 2;
       	//gridData2.verticalSpan = 4;
       	multi.setLayoutData(gridData2);
		
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);

        //gridData.verticalSpan = 4;
        //gridData.horizontalSpan = 1;

		GridData gd = new GridData(GridData.CENTER );
		gd.horizontalSpan = 4;      
		Label label = new Label(multi, SWT.NONE);
		label.setText(data.getText());
		label.setLayoutData(gd);
		label.setToolTipText(data.getTooltip());
		
		setButtons(new Button [dataVals.length]);
		setDataVals(dataVals);
		//setValues(values);
		//setAliases(aliases);
				
		for (int i = 0; i < dataVals.length; i++) {
			buttons[i] = new Button(multi, SWT.RADIO);
			buttons[i].setText(dataVals[i].getText());
			buttons[i].setToolTipText(dataVals[i].getTooltip());
			//buttons[i].setEnabled(false);
			//System.out.println(defaultVal);
			if (dataVals[i].isDefaultVal()) {
				buttons[i].setSelection(true);
			}
		}
		
		
		/*List categories = new List(multi, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
        categories.setItems(values);
			
		String [] temp = new String [] { defaultVal };
		categories.setSelection(temp);		        
        int listHeight = categories.getItemHeight() * 6;

		Rectangle trim = categories.computeTrim(0, 0, 0, listHeight);

 		gridData.heightHint = trim.height;

        categories.setLayoutData(gridData);*/
	}
	
	public ArrayList getControls(){
		ArrayList controls = new ArrayList();
		for (int i = 0; i < getDataVals().length; i++){
			controls.add(buttons[i]);
		}
		return controls;
	}
	
	public String getId(){
		return getAlias();
	}
		
	public void setDef(String id) {
		for (int i = 0; i < buttons.length; i++) {
			if (dataVals[i].getAlias().equals(id)) {
				buttons[i].setSelection(true);
			}
			else {
				buttons[i].setSelection(false);
			}
		}
	}
	
	public String getSelectedAlias() {
		for (int i = 0; i < buttons.length; i++) {
			if (buttons[i].getSelection()) {
				return dataVals[i].getAlias();
			}
		}
		return "";
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
	 * Returns the aliases.
	 * @return String[]
	 */
	public String[] getAliases() {
		return aliases;
	}

	/**
	 * Returns the buttons.
	 * @return Button[]
	 */
	public Button[] getButtons() {
		return buttons;
	}

	/**
	 * Returns the values.
	 * @return String[]
	 */
	public String[] getValues() {
		return values;
	}

	/**
	 * Sets the aliases.
	 * @param aliases The aliases to set
	 */
	public void setAliases(String[] aliases) {
		this.aliases = aliases;
	}

	/**
	 * Sets the buttons.
	 * @param buttons The buttons to set
	 */
	public void setButtons(Button[] buttons) {
		this.buttons = buttons;
	}

	/**
	 * Sets the values.
	 * @param values The values to set
	 */
	public void setValues(String[] values) {
		this.values = values;
	}

	/**
	 * Returns the defaultVal.
	 * @return String
	 */
	/*public String getDefaultVal() {
		return defaultVal;
	}

	/**
	 * Sets the defaultVal.
	 * @param defaultVal The defaultVal to set
	 */
	/*public void setDefaultVal(String defaultVal) {
		this.defaultVal = defaultVal;
	}

	/**
	 * Returns the dataVals.
	 * @return OptionData[]
	 */
	public OptionData[] getDataVals() {
		return dataVals;
	}

	/**
	 * Sets the dataVals.
	 * @param dataVals The dataVals to set
	 */
	public void setDataVals(OptionData[] dataVals) {
		this.dataVals = dataVals;
	}

}
