package ca.mcgill.sable.soot.editors;

import java.util.*;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.MarkerUtilities;

public class JimpleEditor extends TextEditor {

	private ColorManager colorManager;
	
	/**
	 * Constructor for JimpleEditor.
	 */
	public JimpleEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new JimpleConfiguration(colorManager, this));
		setDocumentProvider(new JimpleDocumentProvider());
		
	}
	
		
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}
	
}
