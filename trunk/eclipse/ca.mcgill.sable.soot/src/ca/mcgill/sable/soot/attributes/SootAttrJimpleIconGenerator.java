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

/*
 * Created on Nov 7, 2003
 */
package ca.mcgill.sable.soot.attributes;
import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.texteditor.MarkerUtilities;

/**
 * @author jlhotak
 */
public class SootAttrJimpleIconGenerator implements Runnable {
	private IFile rec;
	private SootAttributesHandler handler;
	
	public void run(){
		removeOldMarkers();
		addSootAttributeMarkers();	
	}
	
	private boolean typesContainsOneOf(ArrayList list){
		boolean result = false;
		Iterator it = list.iterator();
		while (it.hasNext()){
			if (getHandler().getTypesToShow().contains(it.next())) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	public void addSootAttributeMarkers(){//SootAttributesHandler handler, IFile rec) {
		
		if (getHandler().getAttrList() == null) return;
		Iterator it = getHandler().getAttrList().iterator();
		HashMap markerAttr = new HashMap();

		while (it.hasNext()) {
			SootAttribute sa = (SootAttribute)it.next();
			if (getHandler().isShowAllTypes() || typesContainsOneOf(sa.getAnalysisTypes())){
				if (((sa.getAllTextAttrs("") == null) || (sa.getAllTextAttrs("").length() == 0)) && 
					((sa.getAllLinkAttrs() == null) || (sa.getAllLinkAttrs().size() ==0))) continue;
				markerAttr.put(IMarker.LINE_NUMBER, new Integer(sa.getJimpleStartLn()));

				try {
					MarkerUtilities.createMarker(getRec(), markerAttr, "ca.mcgill.sable.soot.sootattributemarker");
				}
				catch(CoreException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}
	
	public void removeOldMarkers(){//IFile file){
		try{
			getRec().deleteMarkers("ca.mcgill.sable.soot.sootattributemarker", true, IResource.DEPTH_INFINITE);
		}
		catch(CoreException e){
		}
	}
	/**
	 * @return
	 */
	public SootAttributesHandler getHandler() {
		return handler;
	}

	/**
	 * @return
	 */
	public IFile getRec() {
		return rec;
	}

	/**
	 * @param handler
	 */
	public void setHandler(SootAttributesHandler handler) {
		this.handler = handler;
	}

	/**
	 * @param file
	 */
	public void setRec(IFile file) {
		rec = file;
	}

}
