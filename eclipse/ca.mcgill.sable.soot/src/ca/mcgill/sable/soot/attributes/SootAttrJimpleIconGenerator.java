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
public class SootAttrJimpleIconGenerator {

	public void addSootAttributeMarkers(SootAttributesHandler handler, IFile rec) {
		
		if (handler.getAttrList() == null) return;
		Iterator it = handler.getAttrList().iterator();
		HashMap markerAttr = new HashMap();

		while (it.hasNext()) {
			SootAttribute sa = (SootAttribute)it.next();
			if (((sa.getAllTextAttrs("") == null) || (sa.getAllTextAttrs("").length() == 0)) && 
				((sa.getAllLinkAttrs() == null) || (sa.getAllLinkAttrs().size() ==0))) continue;
			markerAttr.put(IMarker.LINE_NUMBER, new Integer(sa.getJimpleStartLn()));

			try {
				MarkerUtilities.createMarker(rec, markerAttr, "ca.mcgill.sable.soot.sootattributemarker");
			}
			catch(CoreException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void removeOldMarkers(IFile file){
		try{
			System.out.println("removing old markers");
			file.deleteMarkers("ca.mcgill.sable.soot.sootattributemarker", true, IResource.DEPTH_INFINITE);
		}
		catch(CoreException e){
		}
	}
}
