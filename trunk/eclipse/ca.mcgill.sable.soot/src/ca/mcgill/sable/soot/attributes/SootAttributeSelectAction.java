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

package ca.mcgill.sable.soot.attributes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;

import org.eclipse.jdt.core.*;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.source.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.*;


import ca.mcgill.sable.soot.editors.JimpleEditor;
import ca.mcgill.sable.soot.ui.PopupListSelector;
import ca.mcgill.sable.soot.*;

public abstract class SootAttributeSelectAction extends ResourceAction {

	AbstractTextEditor editor;
	AbstractTextEditor linkToEditor;
	IVerticalRulerInfo rulerInfo;
	AbstractMarkerAnnotationModel model;
	int lineNumber;
	
	/**
	 * @param bundle
	 * @param prefix
	 */
	public SootAttributeSelectAction(ResourceBundle bundle, String prefix, ITextEditor editor, IVerticalRulerInfo rulerInfo) {
		
		super(bundle, prefix);
		setEditor((AbstractTextEditor)editor);
		
		setRulerInfo(rulerInfo);
	}
	
	
	public IResource getResource(AbstractTextEditor textEditor) {
		IEditorInput input= textEditor.getEditorInput();
		return (IResource) ((IAdaptable) input).getAdapter(IResource.class);
	}
	
	protected IDocument getDocument() {
		IDocumentProvider provider= getEditor().getDocumentProvider();
		return provider.getDocument(getEditor().getEditorInput());
	}
	
	public void run() {
	
		// need to get list of texts
		IAnnotationModel model = getEditor().getDocumentProvider().getAnnotationModel(getEditor().getEditorInput());
		if (model instanceof AbstractMarkerAnnotationModel){
			setModel((AbstractMarkerAnnotationModel)model);
		}
		
		int markerLine = getRulerInfo().getLineOfLastMouseButtonActivity();
		IResource rec = getResource(getEditor());
		try {
			IMarker [] markers = rec.findMarkers("ca.mcgill.sable.soot.sootattributemarker", true, IResource.DEPTH_INFINITE);
			for (int i = 0; i < markers.length; i++){
				if (getModel().getMarkerPosition(markers[i]) == null) continue;
				setLineNumber(getDocument().getLineOfOffset(getModel().getMarkerPosition(markers[i]).getOffset()));
  
                
				if (getLineNumber() == markerLine){
					
					ArrayList links = getMarkerLinks();
					Iterator lit = links.iterator();
					String [] list = getMarkerLabels(links);
					if ((list == null) || (list.length == 0)) {
						// show no links
					}
					else {
						IWorkbenchWindow window = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getWorkbenchWindow();
						
						PopupListSelector popup = new PopupListSelector(window.getShell());
						popup.setItems(list);
						
						int listWidth = getListWidth(list);
						
						if (getEditor() instanceof JimpleEditor){
							int topIndex = ((JimpleEditor)getEditor()).getViewer().getTopIndex();
                            Rectangle rect = new Rectangle(320, (getLineNumber()+1-topIndex), listWidth, 45 );
							
							popup.open(rect);
                        }	
						else {
							int topIndex = ((ITextViewer)((AbstractTextEditor)getEditor()).getAdapter(ITextOperationTarget.class)).getTopIndex();
							int pos = getModel().getMarkerPosition(markers[i]).getOffset();
							pos = pos / getLineNumber();
							Rectangle rect = new Rectangle(320, getLineNumber()+1-topIndex, listWidth, 45 );
							popup.open(rect);

						}
						
						handleSelection(popup.getSelected(), links);
					}
				}			
			}
		}
		catch(CoreException e){
			
		}
		catch (BadLocationException e1){
		}
		
	}
	
	public int getListWidth(String[] list){
		int width = 0;
		for (int i = 0; i < list.length; i++){
		
			String next = list[i];
			width = next.length() > width ? next.length() : width;
		}
		
		return width * 6;
	}
	
	public void handleSelection(String selected, ArrayList links){
		if (selected == null) return;
		try {
			int toShow = 0;
			String className;
			Iterator it = links.iterator();
			while (it.hasNext()){
				LinkAttribute la = (LinkAttribute)it.next();
				if (la.getLabel().equals(selected)){
					toShow = getLinkLine(la) - 1;//.getJimpleLink() - 1;
					
					className = la.getClassName();
					findClass(className);
				}
			}
			int selOffset = getLinkToEditor().getDocumentProvider().getDocument(getLinkToEditor().getEditorInput()).getLineOffset(toShow);
			if ((selOffset != -1) && (selOffset != 0)){
				
				if (getLinkToEditor() instanceof JimpleEditor){
					
					((JimpleEditor)getLinkToEditor()).getViewer().setRangeIndication(selOffset, 1, true);
					SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(getLinkToEditor());
					
				}
				else {
					SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(getLinkToEditor());
					((AbstractTextEditor)SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()).selectAndReveal(selOffset, 0);
					((AbstractTextEditor)SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()).setHighlightRange(selOffset, 1, true);
					
				}
			}
		}
		catch(BadLocationException e){
			System.out.println(e.getMessage());		
		}
	}
	
	protected abstract int getLinkLine(LinkAttribute la);
	
	public abstract void findClass(String className);

	
	public String removeExt(String fileName){
		return fileName.substring(0, fileName.lastIndexOf("."));
	}
	
	public abstract ArrayList getMarkerLinks();
	
	public String [] getMarkerLabels(ArrayList links){
		
		if ((links == null) || (links.size() == 0)) return null;
		ArrayList list = new ArrayList();
		String [] attributeTexts = new String[links.size()];
		Iterator it = links.iterator();
		while (it.hasNext()){
			list.add(((LinkAttribute)it.next()).getLabel());
		}
		list.toArray(attributeTexts);
		return attributeTexts;
	
	}
	
	public String [] fixLabels(String [] at){
		for (int i = 0; i < at.length; i++){
			String temp = at[i];
			temp.replaceAll("&lt;", "<");
			temp.replaceAll("&gt;", ">");
			at[i] = temp;
		}
		return at;
	}
	
	public void getMarkerResolutions(IMarker marker){
		
		SootAttributeResolutionGenerator sarg = new SootAttributeResolutionGenerator();
		if (sarg.hasResolutions(marker)){
			IMarkerResolution [] res = sarg.getResolutions(marker);
			for (int i = 0; i < res.length; i++){
				//System.out.println("res: "+res[i].getLabel());
			}
		}
	}

	/**
	 * @return
	 */
	public AbstractTextEditor getEditor() {
		return editor;
	}

	/**
	 * @param editor
	 */
	public void setEditor(AbstractTextEditor editor) {
		this.editor = editor;
	}

	/**
	 * @return
	 */
	public IVerticalRulerInfo getRulerInfo() {
		return rulerInfo;
	}

	/**
	 * @param info
	 */
	public void setRulerInfo(IVerticalRulerInfo info) {
		rulerInfo = info;
	}

	/**
	 * @return
	 */
	public AbstractMarkerAnnotationModel getModel() {
		return model;
	}

	/**
	 * @param model
	 */
	public void setModel(AbstractMarkerAnnotationModel model) {
		this.model = model;
	}

	/**
	 * @return
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * @param i
	 */
	public void setLineNumber(int i) {
		lineNumber = i;
	}

	/**
	 * @return
	 */
	public AbstractTextEditor getLinkToEditor() {
		return linkToEditor;
	}

	/**
	 * @param editor
	 */
	public void setLinkToEditor(AbstractTextEditor editor) {
		linkToEditor = editor;
	}

}
