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

public class SootAttributesJavaHover extends AbstractSootAttributesHover implements IJavaEditorTextHover {

	private ArrayList fileNames;
	
	public IJavaElement getJavaElement(AbstractTextEditor textEditor) {
		IEditorInput input= textEditor.getEditorInput();
		return (IJavaElement) ((IAdaptable) input).getAdapter(IJavaElement.class);
	
	}
	
	public void setEditor(IEditorPart ed) {
		super.setEditor(ed);
	
		//System.out.println(ed.getClass().toString());
		if (ed instanceof AbstractTextEditor) {
			IJavaElement jElem = getJavaElement((AbstractTextEditor)ed);
            if (jElem == null){
                System.out.println("jElem null");
                if (jElem.getResource() == null){
                    System.out.println("jElem.getResource() null");
                    if (jElem.getResource().getProject() == null){
                        System.out.println("jElem.getResource().getProject() null");
                    }
                }
            }
            if (jElem != null){
                System.out.println("jElem not null");
                if (jElem.getResource() != null){
                    System.out.println("jElem.getResource() not null");
                    if (jElem.getResource().getProject() != null){
                        System.out.println("jElem.getResource().getProject() not null");
                    }
                }
            }
            
            if (jElem.getElementType() != IJavaElement.COMPILATION_UNIT) return;
			setSelectedProj(jElem.getResource().getProject().getName());
			setRec(jElem.getResource());
			
			if (jElem.getElementType() == IJavaElement.COMPILATION_UNIT) {
			
				ICompilationUnit cu = (ICompilationUnit)jElem;
				setPackFileNames(new ArrayList());
				try {
					IPackageDeclaration [] pfs = cu.getPackageDeclarations();
					//if (pfs.length == 0) {
						
					//	getPackFileNames().add(fileToNoExt(cu.getElementName()));
					//}
					//else {
					//	for (int i = 0; i < pfs.length; i++) {
							//System.out.println(pfs[i].getElementName());
					//	}
					
					//    getPackFileNames().add(fileToNoExt(pfs[0].getElementName()+"."+cu.getElementName()));
					//}
					
					IType [] topLevelDecls = cu.getTypes();
					for (int i = 0; i < topLevelDecls.length; i++){
						/*System.out.println("top level decl: "+topLevelDecls[i]);
						System.out.println("top level decl element name: "+topLevelDecls[i].getElementName());
						System.out.println("top level decl type qualified name: "+topLevelDecls[i].getTypeQualifiedName());
						String name = topLevelDecls[i].getFullyQualifiedName();
						name = name.replaceAll("\\.", System.getProperty("file.separator"));
						System.out.println("top level decl name: "+name);
						*/
						getPackFileNames().add(topLevelDecls[i].getFullyQualifiedName());
					}
				}
				catch (Exception e1) {
					System.out.println(e1.getMessage());
				}
				
				
			}
		
		
			//computeAttributes();
			//addSootAttributeMarkers();
			
			//addAction();
		}
		
	}
	
	/*private void addAction(){
		SootAttributeRulerActionDelegate actionDel = new SootAttributeRulerActionDelegate();
		((AbstractTextEditor)getEditor()).setAction("sootattributeAction", actionDel.createAction(getEditor(), ((AbstractTextEditor)getEditor()).get.getVerticalRuler()));
	}
	}*/
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
		sb.append("/sootOutput/attributes/");
		System.out.println(sb.toString());
		String dir = sb.toString();
		IContainer c = (IContainer)SootPlugin.getWorkspace().getRoot().getProject(getSelectedProj()).getFolder("sootOutput/attributes/");
		try {
		
			IResource [] files = c.members();
			for (int i = 0; i < files.length; i++){
				Iterator it = getPackFileNames().iterator();
				while (it.hasNext()){
					
					String fileNameToMatch = (String)it.next();
					System.out.println("file to match: "+fileNameToMatch);
					System.out.println(files[i].getName());
					//System.out.println(getPackFileName());
					if (files[i].getName().matches(fileNameToMatch+"[$].*") || files[i].getName().matches(fileNameToMatch+"\\."+"xml")){
						System.out.println(files[i]);
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
		
		//removeOldMarkers();
		
		if (getAttrsHandler() == null)return;
		
		Iterator it = getAttrsHandler().getAttrList().iterator();
		HashMap markerAttr = new HashMap();
		
		while (it.hasNext()) {
			SootAttribute sa = (SootAttribute)it.next();
			if (((sa.getAllTextAttrs("<br>") == null) || (sa.getAllTextAttrs("<br>").length() == 0)) && 
				((sa.getAllLinkAttrs() == null) || (sa.getAllLinkAttrs().size() ==0))) continue;
			
			//markerAttr.put(IMarker.MESSAGE, "Soot Attribute");
			markerAttr.put(IMarker.LINE_NUMBER, new Integer(sa.getJavaStartLn()));
		
			try {
			//if (sa.getTextList() != null){
				MarkerUtilities.createMarker(getRec(), markerAttr, "ca.mcgill.sable.soot.sootattributemarker");
			//	}
				//MarkerUtilities.createMarker(getRec(), markerAttr, "org.eclipse.core.resources.bookmark");		
			}
			catch(CoreException e) {
				System.out.println(e.getMessage());
			}
		
		}

	}
	
	public String fileToNoExt(String filename) {
		return filename.substring(0, filename.lastIndexOf('.'));
	}
	
	protected String getAttributes() {
		
		/*if (SootPlugin.getDefault().getManager().isFileMarkersUpdate((IFile)getRec())){
			SootPlugin.getDefault().getManager().setToFalseUpdate((IFile)getRec());
			try {
				System.out.println("need to remove markers from: "+getRec().getFullPath().toOSString());
				getRec().deleteMarkers("ca.mcgill.sable.soot.sootattributemarker", true, IResource.DEPTH_ONE);
				SootPlugin.getDefault().getManager().setToFalseRemove((IFile)getRec());
			} 
			catch (CoreException e) {
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
			}
			catch(CoreException e){
			}
			return null;
		}*/
		
		if (getAttrsHandler() != null) {
            
            //System.out.println("about to make java colorer");
            ////setSajc(new SootAttributesJavaColorer());
            
            //sajc.computeColors(getAttrsHandler(), getViewer(), getEditor());
                          
			//System.out.println("getting attribute for java ln: "+getLineNum());
		  	return getAttrsHandler().getJavaAttribute(getLineNum());
		}
		else {
			return null;
		}
	}
    
    protected void addColorTags(){
    	setSajc(new SootAttributesJavaColorer());
    	getSajc().computeColors(getAttrsHandler(), getViewer(), getEditor());	
    }
    
    private SootAttributesJavaColorer sajc;   

    /**
     * @return
     */
    public SootAttributesJavaColorer getSajc() {
        return sajc;
    }

    /**
     * @param colorer
     */
    public void setSajc(SootAttributesJavaColorer colorer) {
        sajc = colorer;
    }

}
