/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
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

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;

import ca.mcgill.sable.soot.SootPlugin;

public class SootWorkbenchListener implements IWindowListener {

    private boolean initialized = false;
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWindowListener#windowActivated(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void windowActivated(IWorkbenchWindow window) {
        if (!initialized){
		    window.getActivePage().addPartListener(new EditorActivationListener());
		    IEditorPart activeEdPart = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		    SootPlugin.getDefault().getPartManager().updatePart(activeEdPart);
            initialized = true;
        }

        
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWindowListener#windowDeactivated(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void windowDeactivated(IWorkbenchWindow window) {
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWindowListener#windowClosed(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void windowClosed(IWorkbenchWindow window) {
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWindowListener#windowOpened(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void windowOpened(IWorkbenchWindow window) {
		window.getActivePage().addPartListener(new EditorActivationListener());
		IEditorPart activeEdPart = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		SootPlugin.getDefault().getPartManager().updatePart(activeEdPart);
	}

}
