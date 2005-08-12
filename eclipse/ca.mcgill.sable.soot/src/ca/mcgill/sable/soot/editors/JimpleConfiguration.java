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

package ca.mcgill.sable.soot.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.TextAttribute;

import org.eclipse.jface.text.contentassist.*;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import ca.mcgill.sable.soot.attributes.SootAttributesJimpleHover;



public class JimpleConfiguration extends SourceViewerConfiguration {
	private JimpleDoubleClickStrategy doubleClickStrategy;
	private JimpleScanner scanner;
	private ColorManager colorManager;
	private JimpleEditor editor;

	public JimpleConfiguration(ColorManager colorManager, JimpleEditor editor) {
		this.colorManager = colorManager;
		setEditor(editor);
	}
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE};
	}
	public ITextDoubleClickStrategy getDoubleClickStrategy(
		ISourceViewer sourceViewer,
		String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new JimpleDoubleClickStrategy();
		return doubleClickStrategy;
	}

	protected JimpleScanner getJimpleScanner() {
		if (scanner == null) {
			scanner = new JimpleScanner(colorManager);
			scanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(colorManager.getColor(IJimpleColorConstants.JIMPLE_DEFAULT))));
		}
		return scanner;
	}

	/**
	 * This is what causes Jimple keywords to be highlighted
	 */
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
 
  		PresentationReconciler reconciler= new PresentationReconciler();
  
  		DefaultDamagerRepairer dr= new DefaultDamagerRepairer(getJimpleScanner());
  		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
  		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
 
 		dr= new DefaultDamagerRepairer(getJimpleScanner());
  		reconciler.setDamager(dr, JimplePartitionScanner.JIMPLE_STRING);
  		reconciler.setRepairer(dr, JimplePartitionScanner.JIMPLE_STRING);
		
  		return reconciler;

	}
	/**
	 * This allows text hover on Jimple Editor 
	 */
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
		return new SootAttributesJimpleHover(getEditor());
	}

	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer){
		IContentAssistant ca = new ContentAssistant();
		ca.install(sourceViewer);
		return ca;
	}
	/**
	 * Returns the editor.
	 * @return JimpleEditor
	 */
	public JimpleEditor getEditor() {
		return editor;
	}

	/**
	 * Sets the editor.
	 * @param editor The editor to set
	 */
	public void setEditor(JimpleEditor editor) {
		this.editor = editor;
	}

}
