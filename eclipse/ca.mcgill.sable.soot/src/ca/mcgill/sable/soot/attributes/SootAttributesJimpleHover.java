package ca.mcgill.sable.soot.attributes;


import java.util.HashMap;
import java.util.Iterator;

//import org.eclipse.jface.text.*;
//import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.*;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.MarkerUtilities;



import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
//import org.eclipse.jdt.ui.text.java.hover.*;
//import org.eclipse.jdt.core.*;
import ca.mcgill.sable.soot.*;

/**
 * @author jlhotak
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
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
		if (ed instanceof AbstractTextEditor) {
			IResource rec = getResource((AbstractTextEditor)ed);
			setRec(rec);
			setSelectedProj(rec.getProject().getName());
			//System.out.println(rec.getProject().getFullPath().toOSString());
			setFileName(rec.getFullPath().toOSString());
			setPackFileName(fileToNoExt(rec.getName()));
			//System.out.println(getPackFileName());
			//System.out.println(getLineNum());
			
			//createAttrFileName();
			/*SootAttributeFilesReader safr = new SootAttributeFilesReader();
			AttributeDomProcessor adp = safr.readFile(createAttrFileName());
			if (adp != null) {
				//System.out.println(adp.getAttributes().size());
				setAttrsHandler(new SootAttributesHandler());
				getAttrsHandler().setAttrList(adp.getAttributes());
				//System.out.println(adp.getAttributes().size());
				//getAttrsHandler().printAttrs();
				//addSootAttributeMarkers();
			}*/
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
			markerAttr.put(IMarker.LINE_NUMBER, new Integer(sa.getJimple_ln()));
			try {
				MarkerUtilities.createMarker(getRec(), markerAttr, "ca.mcgill.sable.soot.sootattributemarker");
				//
				//System.out.println("made attributes marker");
				//MarkerUtilities.createMarker(getRec(), markerAttr, "org.eclipse.core.resources.bookmark");		
			}
			catch(CoreException e) {
				System.out.println(e.getMessage());
			}
		}
		/*try {
			IMarker [] markers = getRec().findMarkers("ca.mcgill.sable.soot.sootattributemarker", false, IResource.DEPTH_INFINITE);
			//System.out.println(markers.length+" sootattributemarkers were created");
			//markers = getRec().findMarkers("org.eclipse.core.resources.marker", true, IResource.DEPTH_INFINITE);
			//System.out.println(markers.length+" markers were created");
			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		

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
			
			return getAttrsHandler().getJimpleAttributes(
			getLineNum());
		}
		else {
			return null;
		}
	}

	
}
