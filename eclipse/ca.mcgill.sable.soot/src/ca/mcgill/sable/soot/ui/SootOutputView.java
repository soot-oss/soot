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


import org.eclipse.jface.text.*;
import org.eclipse.jface.action.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.*;
import org.eclipse.swt.*;
import org.eclipse.ui.*;


public class SootOutputView extends ViewPart implements ITextListener, IDocumentListener {
	private TextViewer textViewer;
	
	private Control control;
	private Action selectAllAction;
	private Action copyAction;
		
	public SootOutputView() {
		super();
	}
	
	public void createPartControl(Composite parent) {
		setTextViewer(new TextViewer(parent, getSWTStyles()));
		getTextViewer().setEditable(false);
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
		
	}
	
	public void documentAboutToBeChanged(DocumentEvent e) {
    }
	
	public void documentChanged(DocumentEvent e) {
		
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
