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
				/*else if ((flags & IResourceDelta.REPLACED) != 0) {
					System.out.println("Resource replaced: "+delta.getResource().getFullPath().toOSString());
					
				}
				
				else {
					System.out.println("Resource other event: "+delta.getResource().getFullPath().toOSString());
					
				}*/
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

	private void updateJimpleOutline(IFile file) {
		
		//SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getAdapter(IContentOutlinePage.class);
		
		//JimpleEditor ed = (JimpleEditor) file.getAdapter(JimpleEditor.class);
	
		//System.out.println("updating outliner");
		//ed.getAdapter(IContentOutlinePage.class);//.getPage().setInput(file);
		/*try {
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getContents()));
			ArrayList text = new ArrayList();
			//StringBuffer text = new StringBuffer();
			while (true) {
				String nextLine = br.readLine();
				if (nextLine == null) break;// || (nextLine.length() == 0)) break;
				//text.append(nextLine);
				text.add(nextLine);
				System.out.println(nextLine);
				//System.out.println(nextLine.trim().length());
			}
			
		}
		catch (IOException e) {
			System.out.println("io exception");
		}
		catch (CoreException e1) {
			System.out.println("core exception");
		}
		ed.getPage().getContentOutline();
		ed.getPage().getViewer().setInput(ed.getPage().getContentOutline());
		ed.getPage().getViewer().refresh();*/
	}
}
