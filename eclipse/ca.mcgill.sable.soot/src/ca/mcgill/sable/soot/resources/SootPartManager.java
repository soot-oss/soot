/*
 * Created on Nov 6, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.resources;

import org.eclipse.ui.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.*;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ui.*;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import ca.mcgill.sable.soot.attributes.*;
import ca.mcgill.sable.soot.editors.JimpleEditor;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SootPartManager {
	
	public void updatePart(IEditorPart part){
		
		System.out.println("part in update: "+part);
		if (part == null) return;
		
		if (part instanceof JimpleEditor){
			AbstractAttributesComputer aac = new JimpleAttributesComputer();
			SootAttributesJimpleColorer sajc = new SootAttributesJimpleColorer();
			SootAttrJimpleIconGenerator saji = new SootAttrJimpleIconGenerator();
			
			SourceViewer viewer = (SourceViewer)((AbstractTextEditor)part).getAdapter(ITextOperationTarget.class);
			SootAttributesHandler handler = aac.getAttributesHandler((AbstractTextEditor)part);
			
			if (handler != null){
				saji.removeOldMarkers((IFile)aac.getRec());
				sajc.computeColors(handler, viewer, part);
				saji.addSootAttributeMarkers(handler, (IFile)aac.getRec());
			}
		}
		else {
			
			IEditorInput input= ((AbstractTextEditor)part).getEditorInput();
			IJavaElement jElem = (IJavaElement) ((IAdaptable) input).getAdapter(IJavaElement.class);
			if (!(jElem instanceof ICompilationUnit)) return;
			AbstractAttributesComputer aac = new JavaAttributesComputer();
			
			SootAttributesJavaColorer sajc = new SootAttributesJavaColorer();
			SootAttrJavaIconGenerator saji = new SootAttrJavaIconGenerator();
			
			SourceViewer viewer = (SourceViewer)((AbstractTextEditor)part).getAdapter(ITextOperationTarget.class);
			SootAttributesHandler handler = aac.getAttributesHandler((AbstractTextEditor)part);
			if (handler != null){
				saji.removeOldMarkers((IFile)aac.getRec());
				sajc.computeColors(handler, viewer, part);
				saji.addSootAttributeMarkers(handler, (IFile)aac.getRec());
			
				
			}
	
			System.out.println("active Ed: "+part.getTitle());
		}
	}
}
