/*
 * Created on Nov 6, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.resources;

import org.eclipse.ui.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.*;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ui.*;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import ca.mcgill.sable.soot.attributes.*;
import ca.mcgill.sable.soot.editors.JimpleEditor;
import ca.mcgill.sable.soot.ui.AnalysisKeyView;
import ca.mcgill.sable.soot.*;


/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SootPartManager {
	
	private boolean updateForOpen;
	
	public void updatePart(IEditorPart part){
		
		System.out.println("part in update: "+part);
		if (part == null) return;
		
		if (part instanceof JimpleEditor){
			AbstractAttributesComputer aac = new JimpleAttributesComputer();
			SootAttributesJimpleColorer sajc = new SootAttributesJimpleColorer();
			SootAttrJimpleIconGenerator saji = new SootAttrJimpleIconGenerator();
			
			SourceViewer viewer = (SourceViewer)((AbstractTextEditor)part).getAdapter(ITextOperationTarget.class);
			SootAttributesHandler handler = aac.getAttributesHandler((AbstractTextEditor)part);
			
			if (handler != null){
				if (handler.isUpdate() || isUpdateForOpen()){
				
					sajc.setEditorPart(part);
					sajc.setViewer(viewer);
					sajc.setHandler(handler);
					Thread cThread = new Thread(sajc);
					cThread.start();
				
					
					
					saji.setHandler(handler);
					saji.setRec((IFile)aac.getRec());
					Thread iThread = new Thread(saji);
					iThread.start();
				}
			}
			//handleKeys(handler);
		}
		else {
			
			IEditorInput input= ((AbstractTextEditor)part).getEditorInput();
			IJavaElement jElem = (IJavaElement) ((IAdaptable) input).getAdapter(IJavaElement.class);
			if (!(jElem instanceof ICompilationUnit)) return;
			AbstractAttributesComputer aac = new JavaAttributesComputer();
			
			SootAttributesJavaColorer sajc = new SootAttributesJavaColorer();
			SootAttrJavaIconGenerator saji = new SootAttrJavaIconGenerator();
			
			SourceViewer viewer = (SourceViewer)((AbstractTextEditor)part).getAdapter(ITextOperationTarget.class);
			SootAttributesHandler handler = aac.getAttributesHandler((AbstractTextEditor)part);
			if (handler != null){
				if (handler.isUpdate() || isUpdateForOpen()){
				
					System.out.println("updating colors");
					//saji.removeOldMarkers((IFile)aac.getRec());
					sajc.setEditorPart(part);
					sajc.setViewer(viewer);
					sajc.setHandler(handler);
					Thread cThread = new Thread(sajc);
					cThread.start();
				
				
					//sajc.computeColors();//handler, viewer, part);
					saji.setHandler(handler);
					saji.setRec((IFile)aac.getRec());
					Thread iThread = new Thread(saji);
					iThread.start();
					//saji.addSootAttributeMarkers();//handler, (IFile)aac.getRec());
				}
				
			}
	
			System.out.println("active Ed: "+part.getTitle());
			//handleKeys(handler);
		}
		setUpdateForOpen(false);
	}
	
	private void handleKeys(SootAttributesHandler handler){
		// make a new view and put it in properties 
		// area (bring to top if necessary - make list of keys
		IWorkbenchPage page = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart viewPart = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ISootConstants.ANALYSIS_KEY_VIEW_ID);
		if ((handler.getKeyList() == null) || handler.getKeyList().isEmpty()){
			page.hideView(viewPart);
		}
		else { 
			try {
				if (viewPart == null) {
					//System.out.println("view part was null");
					IWorkbenchPart activePart = page.getActivePart();
					page.showView(ISootConstants.ANALYSIS_KEY_VIEW_ID);
					//restore focus stolen by the creation of the console
					IViewPart shownPart = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ISootConstants.ANALYSIS_KEY_VIEW_ID);
		
					if (shownPart != null){
						((AnalysisKeyView)shownPart).setInputKeys(handler.getKeyList());
					}		
					page.activate(activePart);
				} 
				else {
					if (viewPart != null){
						//System.out.println("view part was not null");
						((AnalysisKeyView)viewPart).setInputKeys(handler.getKeyList());
					}
					page.bringToTop(viewPart);
				}
			} 
			catch (PartInitException pie) {
				System.out.println(pie.getMessage());	
			}
			if (viewPart != null){
				((AnalysisKeyView)viewPart).setInputKeys(handler.getKeyList());
			}
		}
	}
	/**
	 * @return
	 */
	public boolean isUpdateForOpen() {
		return updateForOpen;
	}

	/**
	 * @param b
	 */
	public void setUpdateForOpen(boolean b) {
		updateForOpen = b;
	}

}
