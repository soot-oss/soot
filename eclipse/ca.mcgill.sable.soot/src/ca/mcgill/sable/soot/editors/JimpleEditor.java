package ca.mcgill.sable.soot.editors;

//import java.util.*;

//import org.eclipse.core.resources.IResource;
//import org.eclipse.core.runtime.CoreException;
//import org.eclipse.core.runtime.IAdaptable;
//import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
//import org.eclipse.ui.texteditor.MarkerUtilities;

public class JimpleEditor extends TextEditor {

	private ColorManager colorManager;
	protected JimpleContentOutlinePage page;
	
	/**
	 * Constructor for JimpleEditor.
	 */
	public JimpleEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new JimpleConfiguration(colorManager, this));
		setDocumentProvider(new JimpleDocumentProvider());
		
	}
	
	public Object getAdapter(Class key) {
		if (key.equals(IContentOutlinePage.class)) {
			IEditorInput input = getEditorInput();
			if (input instanceof IFileEditorInput) {
				page = new JimpleContentOutlinePage(((IFileEditorInput)input).getFile());
				return page;
			}
		}
		return super.getAdapter(key);
	}
		
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}
	
}
