/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
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

public class SootPartManager {
	
	private boolean updateForOpen;
	
	public void updatePart(IEditorPart part){
		
		if (part == null) return;
		
		if (part instanceof JimpleEditor){
			AbstractAttributesComputer aac = new JimpleAttributesComputer();
			SootAttributesJimpleColorer sajc = new SootAttributesJimpleColorer();
			SootAttrJimpleIconGenerator saji = new SootAttrJimpleIconGenerator();
			
			SourceViewer viewer = (SourceViewer)((AbstractTextEditor)part).getAdapter(ITextOperationTarget.class);
			SootAttributesHandler handler = aac.getAttributesHandler((AbstractTextEditor)part);
			if (handler != null){
				
				if (isUpdateForOpen() || handler.isUpdate()){
				
					sajc.setEditorPart(part);
					sajc.setViewer(viewer);
					sajc.setHandler(handler);
					Thread cThread = new Thread(sajc);
					cThread.start();
				
					
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
			if (handler != null){
				if (isUpdateForOpen() || handler.isUpdate()){
					sajc.setEditorPart(part);
					sajc.setViewer(viewer);
					sajc.setHandler(handler);
					Thread cThread = new Thread(sajc);
					cThread.start();
				
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
		setUpdateForOpen(false);
	}
	
	private void handleTypes(SootAttributesHandler handler, IFile file){
		IWorkbenchPage page = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ArrayList types = computeTypes(handler);
		if (!types.isEmpty()){
			IViewPart view = page.findView(ISootConstants.ANALYSIS_TYPES_VIEW_ID);
			try {
				if (view == null) {
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
				Iterator typesIt = sa.getAnalysisTypes().iterator();
				while (typesIt.hasNext()){
					String val = (String)typesIt.next();
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
			try {
				if (viewPart == null) {
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
