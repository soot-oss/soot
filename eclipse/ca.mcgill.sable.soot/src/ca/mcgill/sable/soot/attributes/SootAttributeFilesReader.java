package ca.mcgill.sable.soot.attributes;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.util.*;
import org.eclipse.core.resources.*;
import org.w3c.dom.Document;

/**
 * @author jlhotak
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

public class SootAttributeFilesReader {

	/**
	 * @see java.lang.Object#Object()
	 */
	public SootAttributeFilesReader() {
	}

	/**
	 * Method readFiles.
	 * @param selectedProj
	 */
	public void readFiles(String selectedProj) {

		
		// get all projects but only read files in proj 
		// where something just changed
		IProject [] projects = SootPlugin.getDefault().getWorkspace().getRoot().getProjects();
		for (int j = 0; j < projects.length; j++ ) {
			
			if (projects[j].getName().equals(selectedProj)) {
			System.out.println("will read attributes for: "+projects[j].getName());
		// for now read files in test project
		//IProject project =
		//	SootPlugin.getDefault().getWorkspace().getRoot().getProject("test");
		IFolder folder = projects[j].getFolder("attributes");
		try {
			IResource[] attr_files = folder.members();
			for (int i = 0; i < attr_files.length; i++) {
				if (attr_files[i] instanceof IFile) {
					System.out.println("attribute file names: "+attr_files[i].getName());
					AttributeFileReader afr = new AttributeFileReader(attr_files[i].getLocation().toOSString());
					String file =
						afr.readFile();
					//System.out.println();
					//System.out.println(file);
					file = file.replaceAll("\"", "\\\"");
					//System.out.println(file);
					StringToDom domMaker = new StringToDom();
					domMaker.getDocFromString(file);
					Document domDoc = domMaker.getDomDoc();
					//System.out.println(domDoc.getNodeType());
					//System.out.println(domDoc.getNodeName());
					AttributeDomProcessor adp = new AttributeDomProcessor(domDoc);
					adp.processAttributesDom();
					System.out.println("attribute file names after reading: "+attr_files[i].getName());
					//System.out.println(adp.getAttributes().toString());
					if (SootPlugin.getDefault().getSootAttributesHandler() == null) {
						System.out.println("attribute handler is null");
					}
					if (adp.getAttributes() == null) {
						System.out.println("adp attributes is null");
					}
					System.out.println("in attrList : "+adp.getAttributes().capacity());
					SootPlugin.getDefault().getSootAttributesHandler().setAttrListForFilename(adp.getAttributes(), fileToNoExt(attr_files[i].getName()), selectedProj);
					
					SootPlugin.getDefault().getSootAttributesHandler().printAttributes();
					}
			}
		} catch (Exception e1) {
			System.out.println(e1.getMessage());
		}
			}
		}
	}
	
	public String fileToNoExt(String filename) {
		//System.out.println(filename.substring(0, filename.lastIndexOf('.')));
		return filename.substring(0, filename.lastIndexOf('.'));
	}
}
