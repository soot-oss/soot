package ca.mcgill.sable.soot.attributes;


import java.util.HashMap;
import java.util.Iterator;
import org.eclipse.ui.*;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.MarkerUtilities;



import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
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

	
	public IJavaElement getJavaElement(AbstractTextEditor textEditor) {
		IEditorInput input= textEditor.getEditorInput();
		return (IJavaElement) ((IAdaptable) input).getAdapter(IJavaElement.class);
	
	}
	
	public void setEditor(IEditorPart ed) {
		//System.out.println(ed.getClass().toString());
		super.setEditor(ed);
		if (ed instanceof AbstractTextEditor) {
			IJavaElement jElem = getJavaElement((AbstractTextEditor)ed);
			setSelectedProj(jElem.getResource().getProject().getName());
			setRec(jElem.getResource());
			//System.out.println(jElem.getElementName()+" "+jElem.getElementType());
			if (jElem.getElementType() == IJavaElement.COMPILATION_UNIT) {
			
				ICompilationUnit cu = (ICompilationUnit)jElem;//javaProj.findElement(rec.getLocation());
				//System.out.println("cu name: "+cu.getElementName());
				try {
					IPackageDeclaration [] pfs = cu.getPackageDeclarations();
					if (pfs.length == 0) {
						//System.out.println("no package decls");
						setPackFileName(fileToNoExt(cu.getElementName()));
					}
					else {
						for (int i = 0; i < pfs.length; i++) {
							System.out.println(pfs[i].getElementName());
						}
					
					setPackFileName(fileToNoExt(pfs[0].getElementName()+"."+cu.getElementName()));
					}
				}
				catch (Exception e1) {
					System.out.println(e1.getMessage());
				}
				
			}
		
		
			computeAttributes();
			addSootAttributeMarkers();
		}
		
	}
	
	private void computeAttributes() {
		SootAttributeFilesReader safr = new SootAttributeFilesReader();
		AttributeDomProcessor adp = safr.readFile(createAttrFileName());
		if (adp != null) {
			//System.out.println(adp.getAttributes().size());
			setAttrsHandler(new SootAttributesHandler());
			getAttrsHandler().setAttrList(adp.getAttributes());
			//System.out.println(adp.getAttributes().size());
			//getAttrsHandler().printAttrs();
			//addSootAttributeMarkers();
			SootPlugin.getDefault().getManager().addToFileWithAttributes((IFile)getRec(), getAttrsHandler());
		}
	}
	private String createAttrFileName() {
		StringBuffer sb = new StringBuffer();
		sb.append(SootPlugin.getWorkspace().getRoot().getProject(getSelectedProj()).getLocation().toOSString());
		sb.append("/sootOutput/attributes/");
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
			markerAttr.put(IMarker.LINE_NUMBER, new Integer(sa.getJava_ln()));
			try {
				if (sa.getTextList() != null){
					MarkerUtilities.createMarker(getRec(), markerAttr, "ca.mcgill.sable.soot.sootattributemarker");
				}
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
		
		if (SootPlugin.getDefault().getManager().isFileMarkersUpdate((IFile)getRec())){
			SootPlugin.getDefault().getManager().setToFalseUpdate((IFile)getRec());
			try {
				//System.out.println("need to remove markers from: "+getRec().getFullPath().toOSString());
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
			SootPlugin.getDefault().getManager().setToFalseRemove((IFile)getRec());
			try {
				//System.out.println("need to remove markers from: "+getRec().getFullPath().toOSString());
				getRec().deleteMarkers("ca.mcgill.sable.soot.sootattributemarker", true, IResource.DEPTH_ONE);
			}
			catch(CoreException e){
			}
			return null;
		}
		
		if (getAttrsHandler() != null) {
		  	return getAttrsHandler().getJavaAttribute(getLineNum());
		}
		else {
			return null;
		}
	}
	

}
