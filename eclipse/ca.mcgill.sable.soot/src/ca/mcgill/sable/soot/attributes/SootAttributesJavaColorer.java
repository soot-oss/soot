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

import org.eclipse.jface.text.*;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.*;
import org.eclipse.ui.*;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import ca.mcgill.sable.soot.SootPlugin;
import ca.mcgill.sable.soot.editors.*;

public class SootAttributesJavaColorer extends AbstractAttributesColorer implements Runnable{

	public void run(){
		init();
        computeColors();
	}
	
	public void computeColors(){//SootAttributesHandler handler, ITextViewer viewer, IEditorPart editorPart){
	
		if ((getHandler() == null) || (getHandler().getAttrList() == null)) return;
		ArrayList sortedAttrs = sortAttrsByLength(getHandler().getAttrList());
		Iterator it = sortedAttrs.iterator();

		setStyleList(new ArrayList());
		
		getDisplay().asyncExec( new Runnable() {
			public void run() {
				if ((getViewer() != null) && (getViewer().getTextWidget() != null)){
                	setBgColor(getViewer().getTextWidget().getBackground());
				}
			};
		});
		
		while (it.hasNext()) {
			// sets colors for stmts
			SootAttribute sa = (SootAttribute)it.next();
			if ((sa.getJavaStartLn() != 0) && (sa.getJavaEndLn() != 0)){
				if (sa.getJavaStartPos() != 0 && sa.getJavaEndPos() != 0){
					if (sa.getColorList() != null){
						Iterator cit = sa.getColorList().iterator();
						while (cit.hasNext()){
							ColorAttribute ca = (ColorAttribute)cit.next();
							if (getHandler().isShowAllTypes()){
								boolean fg = ca.fg() == 1 ? true: false;
								setAttributeTextColor(sa.getJavaStartLn(), sa.getJavaEndLn(), sa.getJavaStartPos()+1, sa.getJavaEndPos()+1, ca.getRGBColor(), fg);//, tp);
							}
							else { 
								if (getHandler().getTypesToShow().contains(ca.type())){
									boolean fg = ca.fg() == 1 ? true: false;
									setAttributeTextColor(sa.getJavaStartLn(), sa.getJavaEndLn(), sa.getJavaStartPos()+1, sa.getJavaEndPos()+1, ca.getRGBColor(), fg);//, tp);
								}
							}
						}
					}	
					
				}
			}
	
		}
        changeStyles();			
	}
	
	
	protected void setLength(SootAttribute sa, int len){
		sa.setJavaLength(len);
	}
	
}
