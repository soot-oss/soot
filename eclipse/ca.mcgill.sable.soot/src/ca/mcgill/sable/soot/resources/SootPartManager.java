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
import ca.mcgill.sable.soot.ui.*;
import ca.mcgill.sable.soot.*;
import java.util.*;


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
			//System.out.println("Part Manager for Jimple: Handler: "+handler);
			if (handler != null){
				//System.out.println("jimple hanlder is not null");
				//System.out.println("jimple should update for open?: "+isUpdateForOpen());
				//System.out.println("jimple should update?: "+handler.isUpdate());
				
				if (isUpdateForOpen() || handler.isUpdate()){
				
					sajc.setEditorPart(part);
					sajc.setViewer(viewer);
					sajc.setHandler(handler);
					//System.out.println("will set colors");
					Thread cThread = new Thread(sajc);
					cThread.start();
				
					
					//System.out.println("will set sa icons");
					saji.setHandler(handler);
					saji.setRec((IFile)aac.getRec());
					Thread iThread = new Thread(saji);
					iThread.start();
					handler.setUpdate(false);
				}
			}
			handleKeys(handler);
			handleTypes(handler, (IFile)aac.getRec());
		}
		else if (part instanceof AbstractTextEditor){
			
			IEditorInput input= ((AbstractTextEditor)part).getEditorInput();
			IJavaElement jElem = (IJavaElement) ((IAdaptable) input).getAdapter(IJavaElement.class);
			if (!(jElem instanceof ICompilationUnit)) return;
			AbstractAttributesComputer aac = new JavaAttributesComputer();
			
			SootAttributesJavaColorer sajc = new SootAttributesJavaColorer();
			SootAttrJavaIconGenerator saji = new SootAttrJavaIconGenerator();
			
			SourceViewer viewer = (SourceViewer)((AbstractTextEditor)part).getAdapter(ITextOperationTarget.class);
			SootAttributesHandler handler = aac.getAttributesHandler((AbstractTextEditor)part);
			System.out.println("Part Manager for Java: Handler: "+handler);
			if (handler != null){
				//System.out.println("java hanlder is not null");
				//System.out.println("java should update for open?: "+isUpdateForOpen());
				System.out.println("java should update?: "+handler.isUpdate());
				if (isUpdateForOpen() || handler.isUpdate()){
				
					System.out.println("updating colors");
					//saji.removeOldMarkers((IFile)aac.getRec());
					sajc.setEditorPart(part);
					sajc.setViewer(viewer);
					sajc.setHandler(handler);
					Thread cThread = new Thread(sajc);
					cThread.start();
				
					System.out.println("updating sa icons");
					//sajc.computeColors();//handler, viewer, part);
					saji.setHandler(handler);
					saji.setRec((IFile)aac.getRec());
					Thread iThread = new Thread(saji);
					iThread.start();
					//saji.addSootAttributeMarkers();//handler, (IFile)aac.getRec());
					handler.setUpdate(false);
				}
				
			}
	
			//System.out.println("active Ed: "+part.getTitle());
			handleKeys(handler);
			handleTypes(handler, (IFile)aac.getRec());
		}
		setUpdateForOpen(false);
	}
	
	private void handleTypes(SootAttributesHandler handler, IFile file){
		System.out.println("handler all types: "+handler.isShowAllTypes());
		System.out.println("handler types: "+handler.getTypesToShow());
		IWorkbenchPage page = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ArrayList types = computeTypes(handler);
		if (!types.isEmpty()){
			IViewPart view = page.findView(ISootConstants.ANALYSIS_TYPES_VIEW_ID);
			try {
				if (view == null) {
					//System.out.println("view part was null");
					IWorkbenchPart activePart = page.getActivePart();
					page.showView(ISootConstants.ANALYSIS_TYPES_VIEW_ID);
					//restore focus stolen by the creation of the console
					IViewPart shownPart = page.findView(ISootConstants.ANALYSIS_TYPES_VIEW_ID);
		
					if (shownPart != null){
						((AnalysisTypeView)shownPart).setFile(file);
						((AnalysisTypeView)shownPart).setAllTypesChecked(handler.isShowAllTypes());
						((AnalysisTypeView)shownPart).setTypesChecked(handler.getTypesToShow());
						((AnalysisTypeView)shownPart).setInputTypes(types);
					}		
					page.activate(activePart);
				} 
				else {
					if (view != null){
						//System.out.println("view part was not null");
						((AnalysisTypeView)view).setFile(file);
						((AnalysisTypeView)view).setAllTypesChecked(handler.isShowAllTypes());
						((AnalysisTypeView)view).setTypesChecked(handler.getTypesToShow());
						((AnalysisTypeView)view).setInputTypes(types);
					}
					page.bringToTop(view);
				}
			} 
			catch (PartInitException pie) {
				System.out.println(pie.getMessage());	
			}
			
		}
	}
	
	private ArrayList computeTypes(SootAttributesHandler handler){
		ArrayList types = new ArrayList();
		if ((handler != null) && (handler.getAttrList() != null)){
			Iterator attrsIt = handler.getAttrList().iterator();
			while (attrsIt.hasNext()){
				SootAttribute sa = (SootAttribute)attrsIt.next();
				//System.out.println("sa: "+sa);
				Iterator typesIt = sa.getAnalysisTypes().iterator();
				while (typesIt.hasNext()){
					String val = (String)typesIt.next();
					//System.out.println("will add: "+val+" if not there");
					if (!types.contains(val)){
						types.add(val);
					}
				}
			}
			
		}
		return types;
	}
	
	private void handleKeys(SootAttributesHandler handler){
		// make a new view and put it in properties 
		// area (bring to top if necessary - make list of keys
		IWorkbenchPage page = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart viewPart = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ISootConstants.ANALYSIS_KEY_VIEW_ID);
		/*if ((handler.getKeyList() == null) || handler.getKeyList().isEmpty()){
			page.hideView(viewPart);
		}
		else {*/ 
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
		//}
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
