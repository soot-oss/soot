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
import java.util.HashMap;
import java.util.Iterator;
import org.eclipse.ui.*;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.MarkerUtilities;



import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.ui.text.java.hover.*;
import org.eclipse.jdt.core.*;
import ca.mcgill.sable.soot.*;

public class SootAttributesJavaHover extends AbstractSootAttributesHover implements IJavaEditorTextHover {

	private ArrayList fileNames;
	
	public IJavaElement getJavaElement(AbstractTextEditor textEditor) {
		IEditorInput input= textEditor.getEditorInput();
		return (IJavaElement) ((IAdaptable) input).getAdapter(IJavaElement.class);
	
	}
	

	protected void computeAttributes() {
		setAttrsHandler(new SootAttributesHandler());
		createAttrFileNames();
		SootAttributeFilesReader safr = new SootAttributeFilesReader();
		Iterator it = fileNames.iterator();
		while (it.hasNext()){
			String fileName = ((IPath)it.next()).toOSString();
			AttributeDomProcessor adp = safr.readFile(fileName);
			if (adp != null) {
				
				getAttrsHandler().setAttrList(adp.getAttributes());
			}
		}
		
		SootPlugin.getDefault().getManager().addToFileWithAttributes((IFile)getRec(), getAttrsHandler());
			
		
	}
	private String createAttrFileNames() {
		fileNames = new ArrayList();
		StringBuffer sb = new StringBuffer();
		sb.append(SootPlugin.getWorkspace().getRoot().getProject(getSelectedProj()).getLocation().toOSString());
		sb.append(sep);
		sb.append("sootOutput");
		sb.append(sep);
		sb.append("attributes");
		sb.append(sep);
		String dir = sb.toString();
		IContainer c = (IContainer)SootPlugin.getWorkspace().getRoot().getProject(getSelectedProj()).getFolder("sootOutput"+sep+"attributes"+sep);
		try {
		
			IResource [] files = c.members();
			for (int i = 0; i < files.length; i++){
				Iterator it = getPackFileNames().iterator();
				while (it.hasNext()){
					
					String fileNameToMatch = (String)it.next();
					if (files[i].getName().matches(fileNameToMatch+"[$].*") || files[i].getName().matches(fileNameToMatch+"\\."+"xml")){
						fileNames.add(files[i].getLocation());
					}
				}
			}
		}
		catch(CoreException e){
		}
		sb.append(getPackFileName());
		sb.append(".xml");
	
		return sb.toString();
	}
	
	protected void addSootAttributeMarkers() {
		
		if (getAttrsHandler() == null)return;
		if (getAttrsHandler().getAttrList() == null) return;
		Iterator it = getAttrsHandler().getAttrList().iterator();
		HashMap markerAttr = new HashMap();
		
		while (it.hasNext()) {
			SootAttribute sa = (SootAttribute)it.next();
			if (((sa.getAllTextAttrs("<br>") == null) || (sa.getAllTextAttrs("<br>").length() == 0)) && 
				((sa.getAllLinkAttrs() == null) || (sa.getAllLinkAttrs().size() ==0))) continue;
			
			markerAttr.put(IMarker.LINE_NUMBER, new Integer(sa.getJavaStartLn()));
		
			try {
				MarkerUtilities.createMarker(getRec(), markerAttr, "ca.mcgill.sable.soot.sootattributemarker");
			}
			catch(CoreException e) {
				System.out.println(e.getMessage());
			}
		
		}

	}
	
	
	protected String getAttributes(AbstractTextEditor editor) {
		JavaAttributesComputer jac = new JavaAttributesComputer();
		SootAttributesHandler handler = jac.getAttributesHandler(editor);

        if (handler != null){    
        
        	return handler.getJavaAttribute(getLineNum());
		}
		else {
			return null;
		}
	}
    

}
