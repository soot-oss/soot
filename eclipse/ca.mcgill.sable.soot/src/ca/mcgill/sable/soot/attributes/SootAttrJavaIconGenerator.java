/*
 * Created on Nov 7, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.attributes;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.texteditor.MarkerUtilities;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SootAttrJavaIconGenerator implements Runnable{

	private IFile rec;
	private SootAttributesHandler handler;

	public void run(){
		removeOldMarkers();
		addSootAttributeMarkers();		
	}
	
	public void addSootAttributeMarkers(){//SootAttributesHandler handler, IFile rec) {
		
		if (getHandler().getAttrList() == null) return;
		Iterator it = getHandler().getAttrList().iterator();
		HashMap markerAttr = new HashMap();
	
		while (it.hasNext()) {
			SootAttribute sa = (SootAttribute)it.next();
			if (((sa.getAllTextAttrs("<br>") == null) || (sa.getAllTextAttrs("<br>").length() == 0)) && 
				((sa.getAllLinkAttrs() == null) || (sa.getAllLinkAttrs().size() ==0))) continue;
			markerAttr.put(IMarker.LINE_NUMBER, new Integer(sa.getJavaStartLn()));
	
			try {
				MarkerUtilities.createMarker(getRec(), markerAttr, "ca.mcgill.sable.soot.sootattributemarker");
			}
			catch(CoreException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	public void removeOldMarkers(){//IFile file){
		try{
			//System.out.println("removing old markers");
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
