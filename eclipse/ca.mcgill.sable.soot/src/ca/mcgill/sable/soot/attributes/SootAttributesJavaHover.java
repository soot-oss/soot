package ca.mcgill.sable.soot.attributes;


import org.eclipse.jface.text.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.*;
import org.eclipse.ui.texteditor.AbstractTextEditor;



import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.ui.text.java.hover.*;
import org.eclipse.jdt.core.*;
import ca.mcgill.sable.soot.*;

/**
 * @author jlhotak
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SootAttributesJavaHover extends AbstractSootAttributesHover implements IJavaEditorTextHover {

	
	public IJavaElement getJavaElement(AbstractTextEditor textEditor) {
		IEditorInput input= textEditor.getEditorInput();
		return (IJavaElement) ((IAdaptable) input).getAdapter(IJavaElement.class);
	
	}
	
	public void setEditor(IEditorPart ed) {
		
		super.setEditor(ed);
		if (ed instanceof AbstractTextEditor) {
			IJavaElement jElem = getJavaElement((AbstractTextEditor)ed);
			setSelectedProj(jElem.getResource().getProject().getName());
			System.out.println(jElem.getElementName()+" "+jElem.getElementType());
			if (jElem.getElementType() == IJavaElement.COMPILATION_UNIT) {
			
				ICompilationUnit cu = (ICompilationUnit)jElem;//javaProj.findElement(rec.getLocation());
				System.out.println("cu name: "+cu.getElementName());
				try {
					IPackageDeclaration [] pfs = cu.getPackageDeclarations();
					if (pfs.length == 0) {
						System.out.println("no package decls");
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
		}
		
	}
	
	public String fileToNoExt(String filename) {
		return filename.substring(0, filename.lastIndexOf('.'));
	}
	
	protected String getAttributes() {
		if (SootPlugin.getDefault().getSootAttributesHandler() != null) {
		  	return SootPlugin.getDefault().getSootAttributesHandler().getJavaAttribute(getSelectedProj(), getPackFileName(), getLineNum());
		}
		else {
			return null;
		}
	}
	

}
