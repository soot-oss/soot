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
    //private ArrayList textPresList;
    private Color bgColor;
    int count = 0;
	
	public void computeColors(SootAttributesHandler handler, ITextViewer viewer, IEditorPart editorPart){
		setViewer(viewer);
		setEditorPart(editorPart);
		if ((handler == null) || (handler.getAttrList() == null)) return;
		Iterator it = handler.getAttrList().iterator();
		TextPresentation tp = new TextPresentation();
		//if (tp.isEmpty()){
		//	tp.addStyleRange(tp.getDefaultStyleRange());
		//	System.out.println("tp has no default");
		//}
		//Color bgColor
		Display display = getEditorPart().getSite().getShell().getDisplay();
		
		display.asyncExec( new Runnable() {
			public void run() {
			Color bgColor = getViewer().getTextWidget().getBackground();
			setBgColor(bgColor);
			};
		});
		//setBgColor(bgColor);
        //textPresList = newArrayList();
		//System.out.println("computing colors");
		while (it.hasNext()) {
			// sets colors for stmts
			SootAttribute sa = (SootAttribute)it.next();
			if ((sa.getRed() == 0) && (sa.getGreen() == 0) && (sa.getBlue() == 0)){
			}
			else {
				//System.out.println("java line: "+sa.getJava_ln()+" start: "+sa.getJavaOffsetStart()+1+" end: "+ sa.getJavaOffsetEnd()+1);
						
                boolean fg = false;
                if (sa.getFg() == 1){
                    fg = true;
                }
                
				setAttributeTextColor(tp, sa.getJavaStartLn(), sa.getJavaEndLn(), sa.getJavaOffsetStart()+1, sa.getJavaOffsetEnd()+1, sa.getRGBColor(), fg);//, tp);
			}
			// sets colors for valueboxes
			if (sa.getValueAttrs() != null){
				Iterator valIt = sa.getValueAttrs().iterator();
				while (valIt.hasNext()){
					PosColAttribute vba = (PosColAttribute)valIt.next();
					if ((vba.getRed() == 0) && (vba.getGreen() == 0) && (vba.getBlue() == 0)){
					}
					else {
						//System.out.println("java line: "+sa.getJava_ln()+" start: "+vba.getSourceStartOffset()+1+" end: "+ vba.getSourceEndOffset()+1);
						
                        boolean fg = false;
                        if (vba.getFg() == 1) {
                            fg = true;
                        }
                        setAttributeTextColor(tp, sa.getJavaStartLn(), sa.getJavaEndLn(), vba.getSourceStartOffset()+1, vba.getSourceEndOffset()+1, vba.getRGBColor(), fg);//, tp);
					}
				}
			}
		}
		
		//changeTextPres(tp);
		//return tp;
					
	}
	
	private void setAttributeTextColor(TextPresentation tp, int sline, int eline, int start, int end, RGB colorKey, boolean fg) {//, TextPresentation tp){
		System.out.println("startline: "+sline+" soffset: "+start+" endoffset: "+end);
		//System.out.println("setting text color");
        Display display = getEditorPart().getSite().getShell().getDisplay();
		//Color backgroundColor = getEditorPart().getSite().getShell().getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND);
		//TextPresentation tp = new TextPresentation();
        //if (getTextPresList() == null) {
            //setTextPresList(new ArrayList());
        //}
        //getTextPresList().add(tp);
		ColorManager colorManager = new ColorManager();
		int sLineOffset = 0;
		int eLineOffset = 0;
		try {
			sLineOffset = getViewer().getDocument().getLineOffset((sline-1));
			eLineOffset = getViewer().getDocument().getLineOffset((eline-1));
			//System.out.println("slineOffset: "+sLineOffset);
		}
		catch(Exception e){	
		}
		//System.out.println("style range: ");
        //System.out.println("start: "+start);
        //System.out.println("end: "+end);
        
        //StyleRange sr = new StyleRange((lineOffset + start - 1	), (end - start), colorManager.getColor(colorKey), getViewer().get.getTextWidget().getBackground());
		//tp.addStyleRange(sr);
		//Color c = tp.getFirstStyleRange().background;
		
		//final TextPresentation newPresentation = tp;
        final boolean foreground = fg;
		final int s = sLineOffset + start - 1;
		//System.out.println("start offset: "+s);
		int e = eLineOffset + end - 1;
		//System.out.println("end offset: "+e);
		final int l = e - s;
		//System.out.println("length: "+l);
		final Color ck = colorManager.getColor(colorKey);
        final Color oldBgC = colorManager.getColor(IJimpleColorConstants.JIMPLE_DEFAULT);
      
		display.asyncExec( new Runnable() {
			public void run() {
				TextPresentation tp = new TextPresentation();
		//System.out.println("about to create style range");
                StyleRange sr;
                //System.out.println("line: "+sline+" start: "+s+" length: "+l);
                if (l != 0){
                
                if (foreground){
				    sr = new StyleRange(s, l, ck, getBgColor());
                }
                else {
                    sr = new StyleRange(s, l, oldBgC, ck);
                }
                //if (count == 0 | count == 1){
                
					tp.addStyleRange(sr);			
                }
            //}
				//count++;
		getViewer().changeTextPresentation(tp, true);
				//getViewer().setTextColor(ck, s, l, false);
			
			};
		});
		
		//tp.clear();
			
		
	}
    
    private void changeTextPres(TextPresentation tp) {
		System.out.println("changing text pres");
		Display display = getEditorPart().getSite().getShell().getDisplay();
		final TextPresentation pres = tp;
		display.asyncExec(new Runnable() {
			public void run(){
				getViewer().changeTextPresentation(pres, true);
			};
		});
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
    /*public ArrayList getTextPresList() {
        return textPresList;
    }*/

    /**
     * @param list
     */
    /* void setTextPresList(ArrayList list) {
        textPresList = list;
    }*/

	/**
	 * @return
	 */
	public Color getBgColor() {
		return bgColor;
	}

	/**
	 * @param color
	 */
	public void setBgColor(Color color) {
		bgColor = color;
	}

}
