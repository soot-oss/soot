package ca.mcgill.sable.soot.attributes;


import org.eclipse.jface.text.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.*;
import org.eclipse.ui.texteditor.AbstractTextEditor;



import org.eclipse.core.resources.*;
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
			setSelectedProj(rec.getProject().getName());
			System.out.println(rec.getProject().getFullPath().toOSString());
			setFileName(rec.getFullPath().toOSString());
			setPackFileName(fileToNoExt(rec.getName()));
			System.out.println(getPackFileName());
			System.out.println(getLineNum());
		}
		
	}
	
	
	protected String getAttributes() {
		if (SootPlugin.getDefault().getSootAttributesHandler() != null) {
			return SootPlugin.getDefault().getSootAttributesHandler().getJimpleAttributes(getSelectedProj(), getPackFileName(), getLineNum());
		}
		else {
			return null;
		}
	}

}
