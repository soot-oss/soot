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

import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.texteditor.*;

import ca.mcgill.sable.soot.SootPlugin;
import ca.mcgill.sable.soot.editors.JimpleEditor;


public class SootAttributeRulerActionDelegate extends AbstractRulerActionDelegate {

	/**
	 * @param bundle
	 * @param prefix
	 * @param editor
	 * @param ruler
	 */
	public SootAttributeRulerActionDelegate(){
	}
	
	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractRulerActionDelegate#createAction(org.eclipse.ui.texteditor.ITextEditor, org.eclipse.jface.text.source.IVerticalRulerInfo)
	 */
	protected IAction createAction(ITextEditor editor, IVerticalRulerInfo rulerInfo) {
		
		try {
			ResourceBundle rb = SootPlugin.getDefault().getResourceBundle();
			if (editor instanceof JimpleEditor){
				return new SootAttributeJimpleSelectAction(rb, null, editor, rulerInfo);
			}
			else {
				return new SootAttributeJavaSelectAction(rb, null, editor, rulerInfo);
			}
		}
		catch (Exception e){
			System.out.println("exception: "+e.getMessage());
		}
		return null;
	}

	

}
