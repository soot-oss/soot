/**
 * @author jlhotak
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

import org.eclipse.jface.text.*;
import org.eclipse.jface.text.source.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.*;


import ca.mcgill.sable.soot.editors.JimpleEditor;
import ca.mcgill.sable.soot.ui.PopupListSelector;
import ca.mcgill.sable.soot.*;

public class SootAttributeSelectAction extends ResourceAction {

	JimpleEditor editor;
	IVerticalRulerInfo rulerInfo;
	AbstractMarkerAnnotationModel model;
	int lineNumber;
	
	/**
	 * @param bundle
	 * @param prefix
	 */
	public SootAttributeSelectAction(ResourceBundle bundle, String prefix, ITextEditor editor, IVerticalRulerInfo rulerInfo) {
		
		super(bundle, prefix);
		// TODO Auto-generated constructor stub
		System.out.println("called SootAttributeSelectAction constr");
		if (editor instanceof JimpleEditor){
			setEditor((JimpleEditor)editor);
		}
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
		System.out.println("Running SootAttributeSelectAction");
		
		// need to get list of texts
		IAnnotationModel model = getEditor().getDocumentProvider().getAnnotationModel(getEditor().getEditorInput());
		if (model instanceof AbstractMarkerAnnotationModel){
			setModel((AbstractMarkerAnnotationModel)model);
		}
		
		int markerLine = getRulerInfo().getLineOfLastMouseButtonActivity();
		System.out.println("markerLine: "+markerLine);
		
		IResource rec = getResource(getEditor());
		try {
			IMarker [] markers = rec.findMarkers("ca.mcgill.sable.soot.sootattributemarker", true, IResource.DEPTH_INFINITE);
			for (int i = 0; i < markers.length; i++){
				setLineNumber(getDocument().getLineOfOffset(getModel().getMarkerPosition(markers[i]).getOffset()));
				if (getLineNumber() == markerLine){
					System.out.println("selected marker at line: "+getLineNumber());
					System.out.println("offset: "+getDocument().getLineOffset(getLineNumber()));
					//getMarkerResolutions(markers[i]);
					
					ArrayList links = getMarkerLinks();
					String [] list = getMarkerLabels(links);
					if ((list == null) || (list.length == 0)) {
						// show no links
					}
					else {
						IWorkbenchWindow window = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getWorkbenchWindow();
						if (window == null ){
							System.out.println("window is null");
						}
						PopupListSelector popup = new PopupListSelector(window.getShell());
						popup.setItems(list);
						
						if (getEditor() instanceof JimpleEditor){
							int startOffset = ((JimpleEditor)getEditor()).getViewer().getTopIndexStartOffset();
							System.out.println("startOffset: "+startOffset);
							int lineOffset = getDocument().getLineOffset(getLineNumber());
							System.out.println("lineOffset: "+lineOffset);
							int topIndex = ((JimpleEditor)getEditor()).getViewer().getTopIndex();
							System.out.println("topIndex: "+topIndex);
							int bottomOffset = ((JimpleEditor)getEditor()).getViewer().getBottomIndexEndOffset();
							System.out.println("bottom offset: "+ bottomOffset);
							int markerOffset = getModel().getMarkerPosition(markers[i]).getOffset();
							System.out.println("marker Offset: "+markerOffset);
							popup.open(new Rectangle(400, (getLineNumber()+1-topIndex), 600, 30 ));
						}	
						System.out.println(popup.getSelected());
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
	
	public void handleSelection(String selected, ArrayList links){
		if (selected == null) return;
		try {
			int toShow = 0;
			String className;
			Iterator it = links.iterator();
			while (it.hasNext()){
				LinkAttribute la = (LinkAttribute)it.next();
				if (la.getLabel().equals(selected)){
					toShow = la.getLink() - 1;
					className = la.getClassName();
					System.out.println("className: "+className);
				}
			}
		
			System.out.println("toLookUp: "+toShow);
			int selOffset = getDocument().getLineOffset(toShow);
			if ((selOffset != -1) && (selOffset != 0)){
				System.out.println("toLookUp offset:"+selOffset);
				if (getEditor() instanceof JimpleEditor){
					//((JimpleEditor)getEditor()).getViewer().setSelectedRange(selOffset+1, toLookUp.length()-1);
					((JimpleEditor)getEditor()).getViewer().setRangeIndication(selOffset, 1, true);
				}
			}
		}
		catch(BadLocationException e){
		}
	}
	
	public ArrayList getMarkerLinks(){
		SootAttributesHandler handler = SootPlugin.getDefault().getManager().getAttributesHandlerForFile((IFile)getResource(getEditor()));
		if (handler == null ) System.out.println("handler is null");
		ArrayList links = handler.getJimpleLinks(getLineNumber()+1);
		return links;
	}
	
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
	public void getMarkerResolutions(IMarker marker){
		
		SootAttributeResolutionGenerator sarg = new SootAttributeResolutionGenerator();
		if (sarg.hasResolutions(marker)){
			IMarkerResolution [] res = sarg.getResolutions(marker);
			for (int i = 0; i < res.length; i++){
				System.out.println("res: "+res[i].getLabel());
			}
		}
	}

	/**
	 * @return
	 */
	public JimpleEditor getEditor() {
		return editor;
	}

	/**
	 * @param editor
	 */
	public void setEditor(JimpleEditor editor) {
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

}
