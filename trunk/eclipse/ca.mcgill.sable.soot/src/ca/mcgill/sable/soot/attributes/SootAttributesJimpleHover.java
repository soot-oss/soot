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


import java.util.HashMap;
import java.util.Iterator;


import org.eclipse.ui.*;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.MarkerUtilities;



import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
//import org.eclipse.jface.text.TextPresentation;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.editors.*;



public class SootAttributesJimpleHover extends AbstractSootAttributesHover {//implements ITextHover {

	
	
	
	public SootAttributesJimpleHover(IEditorPart editor) {
		setEditor(editor);
	}
	
	
	
	protected String getAttributes(AbstractTextEditor editor) {
	
		
		JimpleAttributesComputer jac = new JimpleAttributesComputer();
		SootAttributesHandler handler = jac.getAttributesHandler(editor);
				
		
		
		if (handler != null){			
		
	
			return handler.getJimpleAttributes(
			getLineNum());
			
		}
		else {
			return null;
		}
		
		
	}


}
