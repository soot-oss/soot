package ca.mcgill.sable.soot.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.TextAttribute;
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
	
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
		return new SootAttributesJimpleHover(getEditor());
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
