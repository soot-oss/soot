/*
 * Created on Nov 6, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.attributes;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.ui.*;
import org.eclipse.ui.texteditor.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JavaAttributesComputer extends AbstractAttributesComputer {

	
	/**
	 * compute top-level names
	 */
	protected ArrayList computeNames(AbstractTextEditor editor){
		IJavaElement jElem = getJavaElement(editor);
		ArrayList names = new ArrayList();
		ICompilationUnit cu = (ICompilationUnit)jElem;
						
		try {
			IType [] topLevelDecls = cu.getTypes();
			for (int i = 0; i < topLevelDecls.length; i++){
				names.add(topLevelDecls[i].getFullyQualifiedName());
			}
		}
		catch(JavaModelException e){
		}
		return names;
	}
	
	/**
	 * initialize rec and proj
	 */
	protected void init(AbstractTextEditor editor){
		IJavaElement jElem = getJavaElement(editor);
		setProj(jElem.getResource().getProject());
		setRec(jElem.getResource());
	}
	
	public IJavaElement getJavaElement(AbstractTextEditor textEditor) {
		IEditorInput input= textEditor.getEditorInput();
		return (IJavaElement) ((IAdaptable) input).getAdapter(IJavaElement.class);
	}
	
	
}
