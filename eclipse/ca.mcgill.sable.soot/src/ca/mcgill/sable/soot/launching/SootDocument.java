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

package ca.mcgill.sable.soot.launching;

import org.eclipse.jface.text.*;
import org.eclipse.ui.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.ui.*;

/**
 * Document for SootOutputView
 */
public class SootDocument extends Document implements ISootOutputEventListener {

	private SootOutputView viewer;
	private int newStreamWriteEnd = 0;
	private int oldStreamWriteEnd = 0;
	private boolean viewShown = false;
	
	public SootDocument() {
	}
	
	public void startUp() {
		SootPlugin.getDefault().addSootOutputEventListener(this);
	}
	
	
	public void handleSootOutputEvent(SootOutputEvent event) {
		if (!viewShown) {
			showSootOutputView();
			notifySootOutputView();
			getViewer().getTextViewer().setDocument(this);
			viewShown = true;
		}
		switch (event.getEventType()) {
			case ISootOutputEventConstants.SOOT_CLEAR_EVENT: 
				clearText();
				break;
			case ISootOutputEventConstants.SOOT_NEW_TEXT_EVENT:
				appendText(event.getTextToAppend());
				break;
			default:
				break;
		}
	}
	
	private void notifySootOutputView() {
		IWorkbench workbench= PlatformUI.getWorkbench();
		IWorkbenchWindow[] windows= workbench.getWorkbenchWindows();
		for (int i = 0; i < windows.length; i++) {
			IWorkbenchWindow iWorkbenchWindow = windows[i];
			IWorkbenchPage[] pages= iWorkbenchWindow.getPages();
			for (int j = 0; j < pages.length; j++) {
				IWorkbenchPage iWorkbenchPage = pages[j];
				IViewPart part= iWorkbenchPage.findView(ISootConstants.SOOT_OUTPUT_VIEW_ID);
				if (part == null) {
					IViewReference refs [] = iWorkbenchPage.getViewReferences();
				}
				if (part instanceof SootOutputView) {
					setViewer((SootOutputView)part);
					this.addDocumentListener(getViewer());
				}
			}
		}
	}
	
	private void clearText() {
		set(new String());
		setNewStreamWriteEnd(0);
		setOldStreamWriteEnd(0);
		showSootOutputView();
	}
	
	private void appendText(final String text) {
		
		update(new Runnable() {
			public void run() {
				int appendedLength= text.length();
				setNewStreamWriteEnd(getOldStreamWriteEnd() + appendedLength);
				replace0(getOldStreamWriteEnd(), 0, text);
				setOldStreamWriteEnd(getNewStreamWriteEnd());
				getViewer().getTextViewer().setTopIndex(getNumberOfLines());
			}
		});
		
		
	}
	
	protected void replace0(int pos, int replaceLength, String text) {
		try {		
			super.replace(pos, replaceLength, text);
		} catch (BadLocationException ble) {
		}
	}
	
	 
	private void showSootOutputView() {
		IWorkbenchWindow window = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();		
		
		if (window != null) {
			IWorkbenchPage page= window.getActivePage();
			if (page != null) {
				try {
					IViewPart sootOutputViewer = page.findView("ca.mcgill.sable.soot.ui.sootoutputview.view");
					if(sootOutputViewer == null) {
						IWorkbenchPart activePart= page.getActivePart();
						page.showView("ca.mcgill.sable.soot.ui.sootoutputview.view");
						//restore focus stolen by the creation of the console
						page.activate(activePart);
						sootOutputViewer = page.findView("ca.mcgill.sable.soot.ui.sootoutputview.view");
					
					} 
					else {
						page.bringToTop(sootOutputViewer);
					}
				} 
				catch (PartInitException pie) {
					System.out.println(pie.getMessage());	
				}
			}
		}
	}
	
	private void update(Runnable runnable) {
		if (getViewer().getTextViewer() != null && getViewer().getTextViewer().getControl() != null && getViewer().getControl().getDisplay() != null) {
			getViewer().getTextViewer().getControl().getDisplay().asyncExec(runnable);
		}
	}
	
	/**
	 * Returns the viewer.
	 * @return SootOutputView
	 */
	public SootOutputView getViewer() {
		return viewer;
	}

	/**
	 * Sets the viewer.
	 * @param viewer The viewer to set
	 */
	public void setViewer(SootOutputView viewer) {
		this.viewer = viewer;
	}

	/**
	 * Returns the newStreamWriteEnd.
	 * @return int
	 */
	public int getNewStreamWriteEnd() {
		return newStreamWriteEnd;
	}

	/**
	 * Returns the oldStreamWriteEnd.
	 * @return int
	 */
	public int getOldStreamWriteEnd() {
		return oldStreamWriteEnd;
	}

	/**
	 * Sets the newStreamWriteEnd.
	 * @param newStreamWriteEnd The newStreamWriteEnd to set
	 */
	public void setNewStreamWriteEnd(int newStreamWriteEnd) {
		this.newStreamWriteEnd = newStreamWriteEnd;
	}

	/**
	 * Sets the oldStreamWriteEnd.
	 * @param oldStreamWriteEnd The oldStreamWriteEnd to set
	 */
	public void setOldStreamWriteEnd(int oldStreamWriteEnd) {
		this.oldStreamWriteEnd = oldStreamWriteEnd;
	}

}
