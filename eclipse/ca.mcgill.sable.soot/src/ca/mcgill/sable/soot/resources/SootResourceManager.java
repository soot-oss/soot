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

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextPresentation;

import ca.mcgill.sable.soot.attributes.SootAttributesHandler;

public class SootResourceManager implements IResourceChangeListener, ITextListener {

	private static final String JAVA_FILE_EXT = Messages.getString("SootResourceManager.java"); //$NON-NLS-1$
	public static final String JIMPLE_FILE_EXT = Messages.getString("SootResourceManager.jimple"); //$NON-NLS-1$
	private static final int SOOT_RAN_BIT = 1;
	private static final int CHANGED_BIT = 0;
	
	
	private HashMap filesWithAttributes;
	private HashMap changedResources;
	private HashMap colorList;
		
	public SootResourceManager() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);

		
	}
	
	public void textChanged(TextEvent e){
	}

	
	public void updateSootRanFlag(){
		if (getChangedResources() == null) return;
			
		Iterator it = getChangedResources().keySet().iterator();
		while (it.hasNext()){
			BitSet bits = (BitSet)getChangedResources().get(it.next());
			bits.set(SOOT_RAN_BIT);
			bits.clear(CHANGED_BIT);
		}
		setColorList(null);
	}
	
	
	
	public void updateFileChangedFlag(IFile file){
        if (file.getFileExtension() == null) return;
		if ((file.getFileExtension().equals(JAVA_FILE_EXT)) ||
			(file.getFileExtension().equals(JIMPLE_FILE_EXT))){
			if (getChangedResources() == null){
				addToLists(file);
			}
			else if (getChangedResources().get(file) == null){
				addToLists(file);
			}
			((BitSet)getChangedResources().get(file)).set(CHANGED_BIT);
			}

		
	}
	
	public void clearColors(){
		// clear colors
		if (getColorList() != null){
			Iterator it = getColorList().keySet().iterator();
			while (it.hasNext()){
				((TextPresentation)getColorList().get(it.next())).clear();
				 
			}
		}
	}
	public boolean isFileMarkersUpdate(IFile file){
		if (getChangedResources() == null) return false;
		if (getChangedResources().get(file) == null) return false;
		return ((BitSet)getChangedResources().get(file)).get(SOOT_RAN_BIT);
	}

	public void setToFalseUpdate(IFile file){
		if (getChangedResources() == null) return;
		if (getChangedResources().get(file) == null) return;
		((BitSet)getChangedResources().get(file)).clear(SOOT_RAN_BIT);
			
	}

	public void setToFalseRemove(IFile file){
		if (getChangedResources() == null) return;
		if (getChangedResources().get(file) == null) return;
		((BitSet)getChangedResources().get(file)).clear(CHANGED_BIT);
			
	}

	public boolean isFileMarkersRemove(IFile file){
		if (getChangedResources() == null) return false;
		if (getChangedResources().get(file) == null) return false;
		return ((BitSet)getChangedResources().get(file)).get(CHANGED_BIT);
	}

	
	public void addToLists(IResource res){
		if (res instanceof IFile){
			IFile file = (IFile)res;
			if (file.getFileExtension() == null) return;
			if ((file.getFileExtension().equals(JAVA_FILE_EXT)) ||
			 	(file.getFileExtension().equals(JIMPLE_FILE_EXT))){
						
				if (getChangedResources() == null){
					setChangedResources(new HashMap());
				}
				getChangedResources().put(file, new BitSet(2));
			 }
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		switch(event.getType()){
			case IResourceChangeEvent.POST_CHANGE:{
				try {
					event.getDelta().accept(new SootDeltaVisitor());
				}
					catch (CoreException e){ 
				}
				break;
			}
		}

	}
	

	public HashMap getChangedResources() {
		return changedResources;
	}

	/**
	 * @param map
	 */
	public void setChangedResources(HashMap map) {
		changedResources = map;
	}
	
	public void addToFileWithAttributes(IFile file, SootAttributesHandler handler){
		if (getFilesWithAttributes() == null){
			setFilesWithAttributes(new HashMap());
		}
		getFilesWithAttributes().put(file, handler);
	}
	
	public SootAttributesHandler getAttributesHandlerForFile(IFile file){
		if (getFilesWithAttributes() == null) {
			return null;
		} 
		else return (SootAttributesHandler)getFilesWithAttributes().get(file);
	}
	
	// colors
	public void addToColorList(IFile file, TextPresentation tp){
		if (getColorList() == null){
			setColorList(new HashMap());
		}
		getColorList().put(file, tp);
	}
	
	public boolean alreadyOnColorList(IFile file){
		if (getColorList() == null) return false;
		else return getColorList().containsKey(file);
	}
	
	/**
	 * @return
	 */
	public HashMap getFilesWithAttributes() {
		return filesWithAttributes;
	}

	/**
	 * @param map
	 */
	public void setFilesWithAttributes(HashMap map) {
		filesWithAttributes = map;
	}

	/**
	 * @return
	 */
	public HashMap getColorList() {
		return colorList;
	}

	/**
	 * @param map
	 */
	public void setColorList(HashMap map) {
		colorList = map;
	}

}
