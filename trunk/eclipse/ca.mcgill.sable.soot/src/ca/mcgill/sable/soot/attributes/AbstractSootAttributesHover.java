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


import java.util.ArrayList;

import org.eclipse.jface.text.*;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ui.*;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import ca.mcgill.sable.soot.SootPlugin;

public abstract class AbstractSootAttributesHover implements ITextHover {

	private IEditorPart editor;
	private int lineNum;
	private String fileName;
	private String packFileName;
	private ArrayList packFileNames;
	private boolean editorHasChanged;
	private String selectedProj;
	private SootAttributesHandler attrsHandler;
	private IResource rec;
	private ITextViewer viewer;
	private IDocument document;
	public static final String sep = System.getProperty("file.separator");
	
	/**
	 * Method setEditor.
	 * @param ed
	 */
	public void setEditor(IEditorPart ed) {
		editor = ed;
	}
	

	
	/**
	 * Method getAttributes.
	 * @return String
	 * sub classes must implement this method
	 * if more then one attribute return 
	 * each attribute separated by newlines
	 */
	protected abstract String getAttributes(AbstractTextEditor editor);
	
	/**
	 * @see org.eclipse.jface.text.ITextHover#getHoverInfo(ITextViewer, IRegion)
	 */
	public String getHoverInfo(ITextViewer textViewer, org.eclipse.jface.text.IRegion hoverRegion) {
		
		// this prevents showing incorrect tags - at least temporaily
		// and hopefully if the editor has ever changed
	
					
		getHoverRegion(textViewer, hoverRegion.getOffset());
		String attr = null;
		attr = getAttributes((AbstractTextEditor)getEditor());
	
		return attr;
		
	}

	/**
	 * @see org.eclipse.jface.text.ITextHover#getHoverRegion(ITextViewer, int)
	 */
	public org.eclipse.jface.text.IRegion getHoverRegion(ITextViewer textViewer, int offset) {
	    try {
			setLineNum(textViewer.getDocument().getLineOfOffset(offset)+1);
			setDocument(textViewer.getDocument());
			return textViewer.getDocument().getLineInformationOfOffset(offset);
		} catch (BadLocationException e) {
			return null;
		}

	}
	
	
	/**
	 * Returns the lineNum.
	 * @return int
	 */
	public int getLineNum() {
		return lineNum;
	}

	/**
	 * Sets the lineNum.
	 * @param lineNum The lineNum to set
	 */
	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}

	/**
	 * Returns the fileName.
	 * @return String
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the fileName.
	 * @param fileName The fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Returns the packFileName.
	 * @return String
	 */
	public String getPackFileName() {
		return packFileName;
	}

	/**
	 * Sets the packFileName.
	 * @param packFileName The packFileName to set
	 */
	public void setPackFileName(String packFileName) {
		this.packFileName = packFileName;
	}

	/**
	 * Returns the editorHasChanged.
	 * @return boolean
	 */
	public boolean isEditorHasChanged() {
		return editorHasChanged;
	}

	/**
	 * Sets the editorHasChanged.
	 * @param editorHasChanged The editorHasChanged to set
	 */
	public void setEditorHasChanged(boolean editorHasChanged) {
		this.editorHasChanged = editorHasChanged;
	}

	/**
	 * Returns the selectedProj.
	 * @return String
	 */
	public String getSelectedProj() {
		return selectedProj;
	}

	/**
	 * Sets the selectedProj.
	 * @param selectedProj The selectedProj to set
	 */
	public void setSelectedProj(String selectedProj) {
		this.selectedProj = selectedProj;
	}

	/**
	 * Returns the attrsHandler.
	 * @return SootAttributesHandler
	 */
	public SootAttributesHandler getAttrsHandler() {
		return attrsHandler;
	}

	/**
	 * Sets the attrsHandler.
	 * @param attrsHandler The attrsHandler to set
	 */
	public void setAttrsHandler(SootAttributesHandler attrsHandler) {
		this.attrsHandler = attrsHandler;
	}

	/**
	 * Returns the editor.
	 * @return IEditorPart
	 */
	public IEditorPart getEditor() {
		return editor;
	}

	/**
	 * Returns the rec.
	 * @return IResource
	 */
	public IResource getRec() {
		return rec;
	}

	/**
	 * Sets the rec.
	 * @param rec The rec to set
	 */
	public void setRec(IResource rec) {
		this.rec = rec;
	}

	/**
	 * @return
	 */
	public IDocument getDocument() {
		return document;
	}

	/**
	 * @return
	 */
	public ITextViewer getViewer() {
		return viewer;
	}

	/**
	 * @param document
	 */
	public void setDocument(IDocument document) {
		this.document = document;
	}

	/**
	 * @param viewer
	 */
	public void setViewer(ITextViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * @return
	 */
	public ArrayList getPackFileNames() {
		return packFileNames;
	}

	/**
	 * @param list
	 */
	public void setPackFileNames(ArrayList list) {
		packFileNames = list;
	}

}
