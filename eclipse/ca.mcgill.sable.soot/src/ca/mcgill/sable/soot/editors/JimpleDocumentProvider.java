package ca.mcgill.sable.soot.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.DefaultPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

public class JimpleDocumentProvider extends FileDocumentProvider {


	public JimpleDocumentProvider() {
		super();
	}

	/* (non-Javadoc)
	 * Method declared on AbstractDocumentProvider
	 */
	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner =
				new DefaultPartitioner(
					new JimplePartitionScanner(),
					new String[] { JimplePartitionScanner.JIMPLE_STRING});
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}
}
