/*
 * Created on 20-Mar-2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.attributes;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import ca.mcgill.sable.soot.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
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
					//System.out.println("Contents changed: "+delta.getResource().getFullPath().toOSString());
					if (delta.getResource() instanceof IFile){
						SootPlugin.getDefault().getManager().updateFileChangedFlag((IFile)delta.getResource());
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
				//System.out.println("Resource added event: "+delta.getResource().getFullPath().toOSString());
				SootPlugin.getDefault().getManager().addToLists(delta.getResource());
			}
		}
		return true;
	}

}
