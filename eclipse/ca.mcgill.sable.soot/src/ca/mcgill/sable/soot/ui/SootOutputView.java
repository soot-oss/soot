package ca.mcgill.sable.soot.ui;


//import org.eclipse.jface.viewers.*;
import org.eclipse.jface.text.*;
import org.eclipse.jface.action.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.*;
import org.eclipse.swt.*;
import org.eclipse.ui.*;

//import ca.mcgill.sable.soot.launching.SootDocument;

/**
 * @author jlhotak
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SootOutputView extends ViewPart implements ITextListener, IDocumentListener {
	private TextViewer textViewer;
	//private SootDocument doc;
	//private int first_index = 0;
	private Control control;
	private Action selectAllAction;
	private Action copyAction;
		
	public SootOutputView() {
		super();
	}
	
	public void createPartControl(Composite parent) {
		setTextViewer(new TextViewer(parent, getSWTStyles()));
		getTextViewer().setEditable(false);
		//viewer.addTextListener(this);
		setControl(parent);
		createActions();
		createContextMenu();
		hookGlobalActions();
	}
	
	private void createActions() {
		selectAllAction = new Action("selectAll"){
			public void run() {
				selectAll();
			}
		};
		copyAction = new Action("copy"){
			public void run() {
				copy();
			}
		};
	}
	
	private void selectAll() {
		getTextViewer().setSelection(new TextSelection(getTextViewer().getTopIndexStartOffset(), getTextViewer().getDocument().getLength()));
	}
	
	private void copy() {
		getTextViewer().doOperation(ITextOperationTarget.COPY);
	}
	

	private void createContextMenu() {
    	// Create menu manager.
        MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
        	public void menuAboutToShow(IMenuManager mgr) {
        		fillContextMenu(mgr);
        	}
        });
                
        // Create menu.
        Menu menu = menuMgr.createContextMenu(getTextViewer().getControl());
        getTextViewer().getControl().setMenu(menu);
                
        // Register menu for extension.
        getSite().registerContextMenu(menuMgr, getTextViewer());
    }
    
    private void fillContextMenu(IMenuManager mgr) {
    	mgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        mgr.add(new Separator());
        mgr.add(copyAction);
        mgr.add(selectAllAction);
        mgr.add(new Separator());

    }
    
    private void hookGlobalActions() {
    	IActionBars bars = getViewSite().getActionBars();
        bars.setGlobalActionHandler(IWorkbenchActionConstants.COPY, copyAction);
        bars.setGlobalActionHandler(IWorkbenchActionConstants.SELECT_ALL, selectAllAction);
        
    }
    

    
	private static int getSWTStyles() {
		int styles= SWT.H_SCROLL | SWT.V_SCROLL;
		return styles;
	}

	public void setFocus() {
		getTextViewer().getControl().setFocus();
	}
	
	/*public void setSootDocument(SootDocument sootDoc) {
		doc = sootDoc;
		viewer.setDocument(doc);
		viewer.setTopIndex(doc.getNumberOfLines());
		
	}
	
	/**
	 * Returns the control.
	 * @return Control
	 */
	public Control getControl() {
		return control;
	}

	/**
	 * Sets the control.
	 * @param control The control to set
	 */
	public void setControl(Control control) {
		this.control = control;
	}

	public void textChanged(TextEvent e) {
		//System.out.println("Text Changed Event: "+e.getText());
	}
	
	public void documentAboutToBeChanged(DocumentEvent e) {
		///System.out.println("Document About to be Changed Event");
	}
	
	public void documentChanged(DocumentEvent e) {
		//System.out.println("Document Changed Event: "+e.getText());
		//viewer.setTopIndex(doc.getNumberOfLines());
	}

	/**
	 * Returns the viewer.
	 * @return TextViewer
	 */
	public TextViewer getTextViewer() {
		return textViewer;
	}

	/**
	 * Sets the viewer.
	 * @param viewer The viewer to set
	 */
	public void setTextViewer(TextViewer textViewer) {
		this.textViewer = textViewer;
	}

}
