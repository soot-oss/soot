/*
 * Created on 19-Mar-2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.editors;

import java.io.*;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.contentoutline.*;

import ca.mcgill.sable.soot.editors.parser.JimpleFile;

/**
 * @author jlhotak
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JimpleContentOutlinePage extends ContentOutlinePage {

	private IFile input;
	
	public JimpleContentOutlinePage(IFile file) {
		super();
		setInput(file);
	}
	
	public void createControl(Composite parent) {
		super.createControl(parent);
		
		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(new WorkbenchContentProvider());
		viewer.setLabelProvider(new WorkbenchLabelProvider());
		viewer.setInput(getContentOutline());
	}
	
	private HashMap getContentOutline(){
	
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(getInput().getContents()));
			StringBuffer text = new StringBuffer();
			while (true) {
				String nextLine = br.readLine();
				if (nextLine == null) break;// || (nextLine.length() == 0)) break;
				text.append(nextLine);
			}
			JimpleFile jFile = new JimpleFile(text.toString());
			//System.out.println(jFile.getFile());
			return jFile.getOutline();
		}
		catch (IOException e) {
			return null;
		}
		catch (CoreException e) {
			return null;
		}
		
		//return null;
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

}
