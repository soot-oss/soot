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


package ca.mcgill.sable.soot.resources;



import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchWindow;


import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.editors.*;



public class SootDeltaVisitor implements IResourceDeltaVisitor {

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
	 */
	public boolean visit(IResourceDelta delta) throws CoreException {
		switch (delta.getKind()) {
			case IResourceDelta.CHANGED: {
			
				int flags = delta.getFlags();
				if ((flags & IResourceDelta.CONTENT) != 0) {
					if (delta.getResource() instanceof IFile){
						SootPlugin.getDefault().getManager().updateFileChangedFlag((IFile)delta.getResource());
						String fileExtension = delta.getResource().getFullPath().getFileExtension();
						if (fileExtension != null && fileExtension.equals(SootResourceManager.JIMPLE_FILE_EXT)){
							updateJimpleOutline((IFile)delta.getResource());
						}
					}
					
				}
				
				break;
			}
			case IResourceDelta.ADDED: {
				SootPlugin.getDefault().getManager().addToLists(delta.getResource());
				if (delta.getResource() instanceof IFile){
					String fileExtension = delta.getResource().getFullPath().getFileExtension();
					if (fileExtension != null && fileExtension.equals(SootResourceManager.JIMPLE_FILE_EXT)){
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
		
		
		IWorkbenchWindow activeWorkbenchWindow = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		if(activeWorkbenchWindow==null) return;
		
		IEditorReference [] refs = activeWorkbenchWindow.getActivePage().getEditorReferences();
		for (int i = 0; i < refs.length; i++){
			if (refs[i] == null) continue;
			if (refs[i].getName() == null) continue;
			if (refs[i].getName().equals(file.getName())){
				JimpleEditor ed = (JimpleEditor) refs[i].getEditor(true).getAdapter(JimpleEditor.class);
				if (ed != null){
					if (ed.getPage() != null){
						ed.getPage().getContentOutline();
						ed.getPage().getViewer().setInput(ed.getPage().getContentOutline());
						ed.getPage().getViewer().refresh();
						ed.getPage().getViewer().expandAll();
					}
				}
			}
		}

		
	}
	
}
