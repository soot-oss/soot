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

import java.util.*;


import org.eclipse.core.resources.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import ca.mcgill.sable.soot.SootPlugin;


/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SootAttributeJimpleSelectAction
	extends SootAttributeSelectAction {
		
	public SootAttributeJimpleSelectAction(ResourceBundle bundle, String prefix, ITextEditor editor, IVerticalRulerInfo rulerInfo) {	
		super(bundle, prefix, editor, rulerInfo);
	}
	
	public ArrayList getMarkerLinks(){
		SootAttributesHandler handler = SootPlugin.getDefault().getManager().getAttributesHandlerForFile((IFile)getResource(getEditor()));
		//if (handler == null ) System.out.println("handler is null");
		ArrayList links = handler.getJimpleLinks(getLineNumber()+1);
		return links;
	}
	protected int getLinkLine(LinkAttribute la){
		return la.getJimpleLink();
	}
	
	public void findClass(String className){
		//System.out.println("className: "+className);
		//System.out.println("rec: "+getResource(getEditor()).getName());

		setLinkToEditor(getEditor());		
		String resource = removeExt(getResource(getEditor()).getName());
		//System.out.println("rec: "+resource);
	
		String ext = getResource(getEditor()).getFileExtension();
	
		IProject proj = getResource(getEditor()).getProject();
	
		//System.out.println("proj: "+proj);
	
		String slashedClassName = className.replaceAll("\\.", System.getProperty("file.separator"));
		String classNameToFind = slashedClassName+"."+ext;
	
		//System.out.println("slashedClassName: "+slashedClassName);
		//IResource fileFound = proj.findMember(slashedClassName);
	

	
		//System.out.println("file Found: "+fileToFind);
	
		if (!resource.equals(className)){
			IContainer parent = getResource(getEditor()).getParent();
			//System.out.println("parent: "+parent);
			IResource file = parent.findMember(className+"."+ext);
			//System.out.println("file: "+file);
			try {
				setLinkToEditor((AbstractTextEditor)SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor((IFile)file));
					//System.out.println("after setting link to editor - diff file");
			}
			catch (PartInitException e){
			
			}
			
		}
		
	}
}
