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


import java.util.HashMap;
import java.util.Iterator;


import org.eclipse.ui.*;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.MarkerUtilities;



import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
//import org.eclipse.jface.text.TextPresentation;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.editors.*;

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

public class SootAttributesJimpleHover extends AbstractSootAttributesHover {//implements ITextHover {

	
	
	
	public SootAttributesJimpleHover(IEditorPart editor) {
		setEditor(editor);
	}
	
	public IResource getResource(AbstractTextEditor textEditor) {
		IEditorInput input= textEditor.getEditorInput();
		return (IResource) ((IAdaptable) input).getAdapter(IResource.class);
	
	}
	
	public String fileToNoExt(String filename) {
		return filename.substring(0, filename.lastIndexOf('.'));
	}

	public void setEditor(IEditorPart ed) {
		
		super.setEditor(ed);
		if (ed instanceof JimpleEditor){
			//System.out.println("editor is a jimple editor");
		}
		if (ed instanceof AbstractTextEditor) {
			IResource rec = getResource((AbstractTextEditor)ed);
			setRec(rec);
			setSelectedProj(rec.getProject().getName());
			//System.out.println(rec.getProject().getFullPath().toOSString());
			setFileName(rec.getFullPath().toOSString());
			setPackFileName(fileToNoExt(rec.getName()));
			
			computeAttributes();
			addSootAttributeMarkers();
		}
		
	}
	
	
	private void computeAttributes() {
		//System.out.println("computing attributes");
		SootAttributeFilesReader safr = new SootAttributeFilesReader();
		AttributeDomProcessor adp = safr.readFile(createAttrFileName());
		if (adp != null) {
			//System.out.println(adp.getAttributes().size());
			setAttrsHandler(new SootAttributesHandler());
			getAttrsHandler().setAttrList(adp.getAttributes());
			SootPlugin.getDefault().getManager().addToFileWithAttributes((IFile)getRec(), getAttrsHandler());
		}
	}
	
	private String createAttrFileName() {
		StringBuffer sb = new StringBuffer();
		sb.append(getRec().getLocation().removeLastSegments(1).toOSString());
		sb.append("/attributes/");
		sb.append(getPackFileName());
		sb.append(".xml");
		//System.out.println("Created attribute file name: "+sb.toString());
		return sb.toString();
	}
	
	private void addSootAttributeMarkers() {
		
		//removeOldMarkers();
		
		if (getAttrsHandler() == null)return;
		
		Iterator it = getAttrsHandler().getAttrList().iterator();
		while (it.hasNext()) {
			SootAttribute sa = (SootAttribute)it.next();
			HashMap markerAttr = new HashMap();
			markerAttr.put(IMarker.MESSAGE, "Soot Attribute: "+sa.getText());
			markerAttr.put(IMarker.LINE_NUMBER, new Integer(sa.getJimple_ln()));
			try {
				if (sa.getTextList() != null){
					MarkerUtilities.createMarker(getRec(), markerAttr, "ca.mcgill.sable.soot.sootattributemarker");
				}
				//
				//System.out.println("made attributes marker");
				//MarkerUtilities.createMarker(getRec(), markerAttr, "org.eclipse.core.resources.bookmark");		
			}
			catch(CoreException e) {
				System.out.println(e.getMessage());
			}
		}
		
		

	}
	
	protected String getAttributes() {
	
		
				
		
		if (SootPlugin.getDefault().getManager().isFileMarkersUpdate((IFile)getRec())){
			SootPlugin.getDefault().getManager().setToFalseUpdate((IFile)getRec());
			try {
				//System.out.println("need to remove markers from: "+getRec().getFullPath().toOSString());
				getRec().deleteMarkers("ca.mcgill.sable.soot.sootattributemarker", true, IResource.DEPTH_ONE);
				SootPlugin.getDefault().getManager().setToFalseRemove((IFile)getRec());
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println("need to update markers from: "+getRec().getFullPath().toOSString());
			computeAttributes();
			
			addSootAttributeMarkers();
			
		}
		else if (SootPlugin.getDefault().getManager().isFileMarkersRemove((IFile)getRec())) {
			//SootPlugin.getDefault().getManager().setToFalseRemove((IFile)getRec());
			try {
				System.out.println("need to remove markers from: "+getRec().getFullPath().toOSString());
				getRec().deleteMarkers("ca.mcgill.sable.soot.sootattributemarker", true, IResource.DEPTH_ONE);
				//SootPlugin.getDefault().getManager().clearColors();
                if (getSajc() != null){
                    getSajc().clearTextPresentations();
                }
			}
			catch(CoreException e){
			}
			return null;
		}
		if (getAttrsHandler() != null) {
					
			//if (!SootPlugin.getDefault().getManager().alreadyOnColorList((IFile)getRec())){
				//  getSajc().clearTextPresentations();
                setSajc(new SootAttributesJimpleColorer());
				//TextPresentation tp = 
				sajc.computeColors(getAttrsHandler(), getViewer(), getEditor());
				//SootPlugin.getDefault().getManager().addToColorList((IFile)getRec(), tp);
			//}
			
			return getAttrsHandler().getJimpleAttributes(
			getLineNum());
			
		}
		else {
			return null;
		}
		
		
	}
    
    private SootAttributesJimpleColorer sajc;
    

	
    /**
     * @return
     */
    public SootAttributesJimpleColorer getSajc() {
        return sajc;
    }

    /**
     * @param colorer
     */
    public void setSajc(SootAttributesJimpleColorer colorer) {
        sajc = colorer;
    }

}
