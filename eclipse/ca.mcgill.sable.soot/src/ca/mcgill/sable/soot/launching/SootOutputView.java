package ca.mcgill.sable.soot.launching;


import org.eclipse.jface.viewers.*;
import org.eclipse.jface.text.*;
import org.eclipse.jface.action.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.*;
import org.eclipse.swt.*;
import org.eclipse.ui.*;

/**
 * @author jlhotak
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SootOutputView extends ViewPart {//implements IDocumentListener {
	private TextViewer viewer;
	private SootDocument doc;
	//private int first_index = 0;
		
	public SootOutputView() {
		super();
	}

	public void createPartControl(Composite parent) {
		viewer = new TextViewer(parent, getSWTStyles());
		viewer.setEditable(false);
	}
	
	private static int getSWTStyles() {
		int styles= SWT.H_SCROLL | SWT.V_SCROLL;
		return styles;
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	public void setSootDocument(SootDocument sootDoc) {
		doc = sootDoc;
		viewer.setDocument(doc);
		viewer.setTopIndex(doc.getNumberOfLines());
		
	}
	
}
