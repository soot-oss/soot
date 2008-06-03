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

package ca.mcgill.sable.soot.testing;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel;

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
public class SootMarkerAnnotationModel extends AbstractMarkerAnnotationModel {

	public SootMarkerAnnotationModel() {
		
	}
	
	
	/**
	 * @see org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel#deleteMarkers(IMarker[])
	 */
	protected void deleteMarkers(IMarker[] markers) throws CoreException {
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel#isAcceptable(IMarker)
	 */
	protected boolean isAcceptable(IMarker marker) {
		return false;
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel#listenToMarkerChanges(boolean)
	 */
	protected void listenToMarkerChanges(boolean listen) {
	}

	/**
	 * @see org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel#retrieveMarkers()
	 */
	protected IMarker[] retrieveMarkers() throws CoreException {
		return null;
	}

}
