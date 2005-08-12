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


package ca.mcgill.sable.soot.attributes;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
// not used
public class FindMethodResolver implements IMarkerResolution {


	private String label;
	private IMarker marker;
		
	public FindMethodResolver(IMarker marker){
		setMarker(marker);
		generateLabel();
	}
	
	private void generateLabel(){
		setLabel("myMarker1");
	}
	
	public void setLabel(String l){
		label = l;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IMarkerResolution#getLabel()
	 */
	public String getLabel() {
		return getLabel();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IMarkerResolution#run(org.eclipse.core.resources.IMarker)
	 */
	public void run(IMarker marker) {
	}

	/**
	 * @return
	 */
	public IMarker getMarker() {
		return marker;
	}

	/**
	 * @param marker
	 */
	public void setMarker(IMarker marker) {
		this.marker = marker;
	}

}
