/*
 * Created on 20-Mar-2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.resources;

import java.io.*;
import java.util.ArrayList;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.views.contentoutline.*;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.editors.*;

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

public class SootDeltaVisitor implements IResourceDeltaVisitor {

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
	 */
	public boolean visit(IResourceDelta delta) throws CoreException {
		// TODO Auto-generated method stub
		switch (delta.getKind()) {
			case IResourceDelta.CHANGED: {
			
				int flags = delta.getFlags();
				if ((flags & IResourceDelta.CONTENT) != 0) {
					System.out.println("Contents changed: "+delta.getResource().getFullPath().toOSString());
					if (delta.getResource() instanceof IFile){
						SootPlugin.getDefault().getManager().updateFileChangedFlag((IFile)delta.getResource());
						if (delta.getResource().getFullPath().getFileExtension().equals(SootResourceManager.JIMPLE_FILE_EXT)){
							updateJimpleOutline((IFile)delta.getResource());
						}
					}
					
				}
				
				break;
			}
			case IResourceDelta.ADDED: {
				System.out.println("Resource added event: "+delta.getResource().getFullPath().toOSString());
				SootPlugin.getDefault().getManager().addToLists(delta.getResource());
				if (delta.getResource() instanceof IFile){
					if (delta.getResource().getFullPath().getFileExtension().equals(SootResourceManager.JIMPLE_FILE_EXT)){
						updateJimpleOutline((IFile)delta.getResource());
					}
				}
			}
		}
		return true;
	}

	// only updates after a save or Soot run 
	// (if editor is currently "dirty" the outline will be potenially incorrect 
	private void updateJimpleOutline(IFile file) {
		
		
		IEditorReference [] refs = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
		for (int i = 0; i < refs.length; i++){
			System.out.println(refs[i].getName());
			if (refs[i].getName().equals(file.getName())){
				JimpleEditor ed = (JimpleEditor) refs[i].getEditor(true).getAdapter(JimpleEditor.class);
				if (ed != null){
					ed.getPage().getContentOutline();
					ed.getPage().getViewer().setInput(ed.getPage().getContentOutline());
					ed.getPage().getViewer().refresh();
					ed.getPage().getViewer().expandAll();
				}
			}
		}

		
	}
}
