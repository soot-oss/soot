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
