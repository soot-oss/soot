/*
 * Created on Nov 18, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.ui;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AnalysisKeyView extends ViewPart {

	private ArrayList inputKeys;
	private TableViewer viewer;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		viewer = new TableViewer(parent);
		viewer.setLabelProvider(new KeysLabelProvider());
		viewer.setContentProvider(new ArrayContentProvider());
	}
	
	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return
	 */
	public ArrayList getInputKeys() {
		return inputKeys;
	}

	/**
	 * @param list
	 */
	public void setInputKeys(ArrayList list) {
		inputKeys = list;
		viewer.setInput(inputKeys);
	}

	/*public void dispose(){
		System.out.println("lp class: "+viewer.getLabelProvider().getClass());
		if (viewer.getLabelProvider() instanceof KeysLabelProvider){
			((KeysLabelProvider)viewer.getLabelProvider()).dispose();
			System.out.println("has right label provider");
		}
		super.dispose();
	}*/
}
