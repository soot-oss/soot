/*
 * Created on Nov 6, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.attributes;

import java.util.ArrayList;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JimpleAttributesComputer extends AbstractAttributesComputer {

	protected ArrayList computeNames(IFile file){
		return getNames();
	}
	
	/* (non-Javadoc)
	 * @see ca.mcgill.sable.soot.attributes.AbstractAttributesComputer#computeNames(org.eclipse.ui.texteditor.AbstractTextEditor)
	 */
	protected ArrayList computeNames(AbstractTextEditor editor) {
		return getNames();
	}
	
	private ArrayList getNames(){
		ArrayList names = new ArrayList();
		names.add(fileToNoExt(getRec().getName()));
		return names;
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.sable.soot.attributes.AbstractAttributesComputer#init(org.eclipse.ui.texteditor.AbstractTextEditor)
	 */
	protected void init(AbstractTextEditor editor) {
		setRec(getResource(editor));
		setProj(getRec().getProject());

	}
	
	public IResource getResource(AbstractTextEditor textEditor) {
		IEditorInput input= textEditor.getEditorInput();
		return (IResource) ((IAdaptable) input).getAdapter(IResource.class);
	}
	
	public String fileToNoExt(String filename) {
		return filename.substring(0, filename.lastIndexOf('.'));
	}


	

}
