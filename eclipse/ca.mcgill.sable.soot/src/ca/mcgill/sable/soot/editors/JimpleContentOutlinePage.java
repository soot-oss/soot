/*
 * Created on 19-Mar-2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.editors;

import java.io.*;
import java.util.ArrayList;
//import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
//import org.eclipse.ui.model.WorkbenchContentProvider;
//import org.eclipse.ui.model.WorkbenchLabelProvider;
//import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.*;

import ca.mcgill.sable.soot.editors.parser.JimpleFile;

/**
 * @author jlhotak
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JimpleContentOutlinePage extends ContentOutlinePage implements ISelectionChangedListener  {

	private IFile input;
	private JimpleEditor ed;
	private JimpleFile jimpleFileParser;
	
	public JimpleContentOutlinePage(IFile file, JimpleEditor ed) {
		super();
		setInput(file);
		setEd(ed);
		
	}
	
	public void createControl(Composite parent) {
		super.createControl(parent);
		
		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(new JimpleOutlineContentProvider());
		viewer.setLabelProvider(new JimpleOutlineLabelProvider());
		viewer.setInput(getContentOutline());
		viewer.expandAll();
		
		viewer.addSelectionChangedListener(this);
	}
	
	private JimpleOutlineObject getContentOutline(){
	
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(getInput().getContents()));
			ArrayList text = new ArrayList();
			//StringBuffer text = new StringBuffer();
			while (true) {
				String nextLine = br.readLine();
				if (nextLine == null) break;// || (nextLine.length() == 0)) break;
				//text.append(nextLine);
				text.add(nextLine);
				//System.out.println(nextLine);
				//System.out.println(nextLine.trim().length());
			}
			setJimpleFileParser(new JimpleFile(text));
			//System.out.println(getJimpleFileParser().getFile());
			return getJimpleFileParser().getOutline();
		}
		catch (IOException e) {
			return null;
		}
		catch (CoreException e) {
			return null;
		}
		
		//return null;
	}
	
	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection selection = (IStructuredSelection)event.getSelection();
		if (selection.isEmpty()) {
			System.out.println("selection empty");	
		}
		else {
			Object elem = selection.getFirstElement();
			if (elem instanceof JimpleOutlineObject) {
				String toHighlight = ((JimpleOutlineObject)elem).getLabel();
				//System.out.println(toHighlight);
				int start = getJimpleFileParser().getStartOfSelected(toHighlight);
				int length = getJimpleFileParser().getLength(toHighlight);
				//System.out.println("start: "+start+" length: "+length);
				getEd().selectAndReveal(start, length);
			}
		}
	}
	
	/**
	 * @return IFile
	 */
	public IFile getInput() {
		return input;
	}

	/**
	 * Sets the input.
	 * @param input The input to set
	 */
	public void setInput(IFile input) {
		this.input = input;
	}

	/**
	 * @return
	 */
	public JimpleEditor getEd() {
		return ed;
	}

	/**
	 * @param editor
	 */
	public void setEd(JimpleEditor editor) {
		ed = editor;
	}

	/**
	 * @return
	 */
	public JimpleFile getJimpleFileParser() {
		return jimpleFileParser;
	}

	/**
	 * @param file
	 */
	public void setJimpleFileParser(JimpleFile file) {
		jimpleFileParser = file;
	}

}
