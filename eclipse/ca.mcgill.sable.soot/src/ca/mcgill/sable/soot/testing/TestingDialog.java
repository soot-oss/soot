package ca.mcgill.sable.soot.testing;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;



/**
 * @author jlhotak
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class TestingDialog extends Dialog {
	protected IAdaptable input;
	
	private Label text_label;
	private Button jimple_button;
	private Button grimp_button;

	public TestingDialog(Shell parentShell, IAdaptable input) {
		super(parentShell);
		
		this.input = input;
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
	
		Layout layout = new RowLayout();
		composite.setLayout(layout);
		text_label = new Label(composite, SWT.LEFT);			
		text_label.setText("Select Soot Output Type:");
		
		jimple_button = new Button(composite, SWT.RADIO);
		
				
		return composite;
		
	}
	
	protected void okPressed() {
		
		super.okPressed();
			
	}

}
