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

import ca.mcgill.sable.soot.editors.*;

public class SootAttributesJavaColorer {


	private ITextViewer viewer;
	private IEditorPart editorPart;
    private ArrayList textPresList;
	
	public void computeColors(SootAttributesHandler handler, ITextViewer viewer, IEditorPart editorPart){
		setViewer(viewer);
		setEditorPart(editorPart);
		Iterator it = handler.getAttrList().iterator();
		//TextPresentation tp = new TextPresentation();
		//System.out.println("computing colors");
		while (it.hasNext()) {
			// sets colors for stmts
			SootAttribute sa = (SootAttribute)it.next();
			if ((sa.getRed() == 0) && (sa.getGreen() == 0) && (sa.getBlue() == 0)){
			}
			else {
				System.out.println("java line: "+sa.getJava_ln()+" start: "+sa.getJavaOffsetStart()+1+" end: "+ sa.getJavaOffsetEnd()+1);
						
				setAttributeTextColor(sa.getJava_ln(), sa.getJavaOffsetStart()+1, sa.getJavaOffsetEnd()+1, sa.getRGBColor());//, tp);
			}
			// sets colors for valueboxes
			if (sa.getValueAttrs() != null){
				Iterator valIt = sa.getValueAttrs().iterator();
				while (valIt.hasNext()){
					PosColAttribute vba = (PosColAttribute)valIt.next();
					if ((vba.getRed() == 0) && (vba.getGreen() == 0) && (vba.getBlue() == 0)){
					}
					else {
						System.out.println("java line: "+sa.getJava_ln()+" start: "+vba.getSourceStartOffset()+1+" end: "+ vba.getSourceEndOffset()+1);
											
                        setAttributeTextColor(sa.getJava_ln(), vba.getSourceStartOffset()+1, vba.getSourceEndOffset()+1, vba.getRGBColor());//, tp);
					}
				}
			}
		}
		//return tp;
					
	}
	
	private void setAttributeTextColor(int line, int start, int end, RGB colorKey) {//, TextPresentation tp){
		System.out.println("setting text color");
        Display display = getEditorPart().getSite().getShell().getDisplay();
		//Color backgroundColor = getEditorPart().getSite().getShell().getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND);
		TextPresentation tp = new TextPresentation();
        if (getTextPresList() == null) {
            setTextPresList(new ArrayList());
        }
        getTextPresList().add(tp);
		ColorManager colorManager = new ColorManager();
		int lineOffset = 0;
		try {
			lineOffset = getViewer().getDocument().getLineOffset((line-1));
            System.out.println("lineOffset: "+lineOffset);
		}
		catch(Exception e){	
		}
		System.out.println("style range: ");
        System.out.println("start: "+start);
        System.out.println("end: "+end);
        
        //StyleRange sr = new StyleRange((lineOffset + start - 1	), (end - start), colorManager.getColor(colorKey), getViewer().get.getTextWidget().getBackground());
		//tp.addStyleRange(sr);
		//Color c = tp.getFirstStyleRange().background;
		
		//final TextPresentation newPresentation = tp;
		final int s = lineOffset + start - 1;
		final int l = end - start;
		final Color ck = colorManager.getColor(colorKey);
		display.asyncExec( new Runnable() {
			public void run() {
				TextPresentation tp = new TextPresentation();
				StyleRange sr = new StyleRange(s, l, ck, getViewer().getTextWidget().getBackground());
				tp.addStyleRange(sr);			
				getViewer().changeTextPresentation(tp, true);
				//getViewer().setTextColor(ck, s, l, false);
			
			};
		});
		
		//tp.clear();
			
		
	}
    
    /*public void clearTextPresentations(){
        if (getTextPresList() == null) return;
        
        Iterator it = getTextPresList().iterator();
        while (it.hasNext()){
            TextPresentation tp = (TextPresentation)it.next();
            tp.clear();
            //System.out.println("cleared TextPresentation");
        }
        
    }*/
	

	/**
	 * @return
	 */
	public ITextViewer getViewer() {
		return viewer;
	}

	/**
	 * @param viewer
	 */
	public void setViewer(ITextViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * @return
	 */
	public IEditorPart getEditorPart() {
		return editorPart;
	}

	/**
	 * @param part
	 */
	public void setEditorPart(IEditorPart part) {
		editorPart = part;
	}

    /**
     * @return
     */
    public ArrayList getTextPresList() {
        return textPresList;
    }

    /**
     * @param list
     */
    public void setTextPresList(ArrayList list) {
        textPresList = list;
    }

}
