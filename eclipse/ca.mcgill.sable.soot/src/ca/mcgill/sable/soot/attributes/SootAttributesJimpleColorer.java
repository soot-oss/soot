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
import org.eclipse.ui.*;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import ca.mcgill.sable.soot.editors.*;

public class SootAttributesJimpleColorer implements Runnable{


	private ITextViewer viewer;
	private IEditorPart editorPart;
    private ArrayList textPresList;
    private SootAttributesHandler handler;
	private Display display;
	
	public void run(){
		computeColors();	
	}
	
	public void computeColors(){//SootAttributesHandler handler, ITextViewer viewer, IEditorPart editorPart){
		//setViewer(viewer);
		//setEditorPart(editorPart);
		setDisplay(getEditorPart().getSite().getShell().getDisplay());
		clearPres();
		
		if (getHandler().getAttrList() == null) return;
		Iterator it = getHandler().getAttrList().iterator();
		//TextPresentation tp = new TextPresentation();
		//System.out.println("computing colors");
		while (it.hasNext()) {
			// sets colors for stmts
			SootAttribute sa = (SootAttribute)it.next();
			if (sa.getColor() == null){
			}
			else {
				setAttributeTextColor(sa.getJimpleStartLn(), sa.getJimpleEndLn(), sa.getJimpleStartPos()+1, sa.getJimpleEndPos()+1, sa.getRGBColor());//, tp);
			}
			// sets colors for valueboxes
			/*if (sa.getValueAttrs() != null){
				Iterator valIt = sa.getValueAttrs().iterator();
				while (valIt.hasNext()){
					PosColAttribute vba = (PosColAttribute)valIt.next();
					if ((vba.getRed() == 0) && (vba.getGreen() == 0) && (vba.getBlue() == 0)){
					}
					else {
						setAttributeTextColor(sa.getJimpleStartLn(), sa.getJimpleEndLn(), vba.getStartOffset()+1, vba.getEndOffset()+1, vba.getRGBColor());//, tp);
					}
				}
			}*/
		}
		//return tp;
					
	}
	
	private void setAttributeTextColor(int line, int eline, int start, int end, RGB colorKey) {//, TextPresentation tp){
		Display display = getEditorPart().getSite().getShell().getDisplay();
		TextPresentation tp = new TextPresentation();
        if (getTextPresList() == null) {
            setTextPresList(new ArrayList());
        }
        getTextPresList().add(tp);
		ColorManager colorManager = new ColorManager();
		int sLineOffset = 0;
		int eLineOffset = 0;
		//System.out.println("line: "+line+" eline: "+eline+" spos: "+start+" epos: "+end);
		try {
			sLineOffset = getViewer().getDocument().getLineOffset((line-1));
			eLineOffset = getViewer().getDocument().getLineOffset((eline-1));
		}
		catch(Exception e){	
		}
		//System.out.println("sLineOffset: "+sLineOffset);
		//System.out.println("eLineOffset: "+eLineOffset);
		StyleRange sr = new StyleRange((sLineOffset + start - 1	), ((eLineOffset + end -1) - (sLineOffset + start - 1)), colorManager.getColor(IJimpleColorConstants.JIMPLE_DEFAULT), colorManager.getColor(colorKey));
		tp.addStyleRange(sr);
		Color c = tp.getFirstStyleRange().background;
		
		final TextPresentation newPresentation = tp;
		
		display.asyncExec( new Runnable() {
			public void run() {
				getViewer().changeTextPresentation(newPresentation, true);
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
        
    }
    */
	private void clearPres(){
		if (getEditorPart() == null) return;
		if (getEditorPart().getEditorInput() != null){
    	
			getDisplay().asyncExec(new Runnable(){
    	
				public void run() {
					((AbstractTextEditor)getEditorPart()).setInput(getEditorPart().getEditorInput());
				};
			});
		}
	}

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

	/**
	 * @return
	 */
	public SootAttributesHandler getHandler() {
		return handler;
	}

	/**
	 * @param handler
	 */
	public void setHandler(SootAttributesHandler handler) {
		this.handler = handler;
	}

	/**
	 * @return
	 */
	public Display getDisplay() {
		return display;
	}

	/**
	 * @param display
	 */
	public void setDisplay(Display display) {
		this.display = display;
	}

}
