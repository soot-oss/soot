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
			System.out.println(rec.getProject().getFullPath().toOSString());
			setFileName(rec.getFullPath().toOSString());
			setPackFileName(fileToNoExt(rec.getName()));
			System.out.println(getPackFileName());
			System.out.println(getLineNum());
			
			//createAttrFileName();
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
			addSootAttributeMarkers();
		}
		
	}
	
	private String createAttrFileName() {
		StringBuffer sb = new StringBuffer();
		sb.append(SootPlugin.getWorkspace().getRoot().getProject(getSelectedProj()).getLocation().toOSString());
		sb.append("/sootOutput/attributes/");
		sb.append(getPackFileName());
		sb.append(".xml");
		System.out.println(sb.toString());
		return sb.toString();
	}
	
	private void addSootAttributeMarkers() {
		
		removeOldMarkers();
		
		if (getAttrsHandler() == null)return;
		
		Iterator it = getAttrsHandler().getAttrList().iterator();
		while (it.hasNext()) {
			SootAttribute sa = (SootAttribute)it.next();
			HashMap markerAttr = new HashMap();
			markerAttr.put(IMarker.MESSAGE, "Soot Attribute: "+sa.getText());
			markerAttr.put(IMarker.LINE_NUMBER, new Integer(sa.getJimple_ln()));
			try {
				//MarkerUtilities.createMarker(getRec(), markerAttr, "ca.mgill.sable.soot.sootattributemarker");
				System.out.println("made attributes marker");
				MarkerUtilities.createMarker(getRec(), markerAttr, "org.eclipse.core.resources.bookmark");		
			}
			catch(CoreException e) {
				System.out.println(e.getMessage());
			}
		}
		

	}
	
	protected String getAttributes() {
		if (getAttrsHandler() != null) {
			return getAttrsHandler().getJimpleAttributes(
			getLineNum());
		}
		else {
			return null;
		}
	}

	
}
