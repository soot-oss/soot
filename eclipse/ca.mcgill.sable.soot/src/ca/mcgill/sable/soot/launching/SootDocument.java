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
 * @author jlhotak
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SootDocument extends Document implements ISootOutputEventListener {

	private SootOutputView viewer;
	private int newStreamWriteEnd = 0;
	private int oldStreamWriteEnd = 0;
	private boolean viewShown = false;
	
	public SootDocument() {
	}
	
	public void startUp() {
		//System.out.println("Soot doc registers as listener");
		SootPlugin.getDefault().addSootOutputEventListener(this);
		//notifySootOutputView();
	}
	
	
	public void handleSootOutputEvent(SootOutputEvent event) {
		//System.out.println(event.getEventType());
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
		//notifySootOutputView();
		//System.out.println("handling Soot Event");
		if (!viewShown) {
			//System.out.println("view wasn't shown and now will be");
			showSootOutputView();
			notifySootOutputView();
			getViewer().getTextViewer().setDocument(this);
			//showSootOutputView();
			viewShown = true;
		}
		//showSootOutputView();
	}
	
	private void notifySootOutputView() {
		IWorkbench workbench= PlatformUI.getWorkbench();
		IWorkbenchWindow[] windows= workbench.getWorkbenchWindows();
		//System.out.println(windows.length);
		for (int i = 0; i < windows.length; i++) {
			IWorkbenchWindow iWorkbenchWindow = windows[i];
			IWorkbenchPage[] pages= iWorkbenchWindow.getPages();
			//System.out.println(pages.length);
			for (int j = 0; j < pages.length; j++) {
				//System.out.println(pages[j]);
				IWorkbenchPage iWorkbenchPage = pages[j];
				IViewPart part= iWorkbenchPage.findView(ISootConstants.SOOT_OUTPUT_VIEW_ID);
				//System.out.println("asked to find part");
				if (part == null) {
					
					IViewReference refs [] = iWorkbenchPage.getViewReferences();
					for (int k = 0; k < refs.length; k++) {
						System.out.println(refs[k].getPart(true).getClass().toString());
					}
					//System.out.println("part is null");
				}
				//System.out.println(part.getClass().toString());
				if (part instanceof SootOutputView) {
					//System.out.println("part is a SootOutputView");
					setViewer((SootOutputView)part);
					//getViewer().setSootDocument(this);
					//System.out.println("notifying and adding listener to view");
					this.addDocumentListener(getViewer());
				}
			}
		}
	}
	
	private void clearText() {
		//System.out.println("clearing text");
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
				//updateOutputStyleRanges(source, getLength() + appendedLength, fLastStreamWriteEnd, fNewStreamWriteEnd);
				replace0(getOldStreamWriteEnd(), 0, text);
				setOldStreamWriteEnd(getNewStreamWriteEnd());
				//setAppendInProgress(false);
				getViewer().getTextViewer().setTopIndex(getNumberOfLines());
			}
		});
		
		
	}
	
	protected void replace0(int pos, int replaceLength, String text) {
		try {		
			super.replace(pos, replaceLength, text);
		} catch (BadLocationException ble) {
			//DebugUIPlugin.log(ble);
		}
	}
	
	 
	private void showSootOutputView() {
		//System.out.println("in showSootOutputView");
		//System.out.println(SootPlugin.getDefault().getWorkbench());
		IWorkbenchWindow window = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();		
		
		if (window != null) {
			//System.out.println("window not null");
			IWorkbenchPage page= window.getActivePage();
			if (page != null) {
				try {
					IViewPart sootOutputViewer = page.findView("ca.mcgill.sable.soot.ui.sootoutputview.view");
					if(sootOutputViewer == null) {
						//System.out.println("page not null");
						IWorkbenchPart activePart= page.getActivePart();
						//System.out.println("active part gotten");
						page.showView("ca.mcgill.sable.soot.ui.sootoutputview.view");
						//restore focus stolen by the creation of the console
						page.activate(activePart);
						//System.out.println("activated part");
					} 
					else {
						page.bringToTop(sootOutputViewer);
						//System.out.println("brought part to top");
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
			//notifySootOutputView();
		} else {
			//Display display= SootPlugin.getDefault().getDefault().getStandardDisplay();
			//if (display != null && !display.isDisposed()) {
			//	display.asyncExec(runnable);
			//}
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
