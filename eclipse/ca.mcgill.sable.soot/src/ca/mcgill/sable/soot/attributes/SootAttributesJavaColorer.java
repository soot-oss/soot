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

import ca.mcgill.sable.soot.editors.*;

public class SootAttributesJavaColorer implements Runnable{


	private ITextViewer viewer;
	private IEditorPart editorPart;
    //private ArrayList textPresList;
    private Color bgColor;
    int count = 0;
    private Display display;
    private ArrayList styleList;
    private SootAttributesHandler handler;
	
		
	public void run(){
		computeColors();
	}
	
	public void computeColors(){//SootAttributesHandler handler, ITextViewer viewer, IEditorPart editorPart){
		
		//System.out.println("Start Coloring: "+System.currentTimeMillis());
		
		//setViewer(viewer);
		//setEditorPart(editorPart);
		setDisplay(getEditorPart().getSite().getShell().getDisplay());
		clearPres();
		if ((getHandler() == null) || (getHandler().getAttrList() == null)) return;
		ArrayList sortedAttrs = sortAttrsByLength(getHandler().getAttrList());
		Iterator it = sortedAttrs.iterator();
		TextPresentation tp = new TextPresentation();
		
		styleList = new ArrayList();
		//final ArrayList temp = new ArrayList();
		//final TextPresentation tempTp = new TextPresentation();
		/*getDisplay().asyncExec(new Runnable() {
			public void run(){
		StyleRange [] sr = getViewer().getTextWidget().getStyleRanges();
		for (int i = 0; i < sr.length; i++){
			System.out.println("sr fg color: "+sr[i].foreground+" bg color: "+sr[i].background+" font: "+sr[i].fontStyle+" start: "+sr[i].start+" length: "+sr[i].length);
			styleList.add(sr[i]);
		}
			};
		})*/
		
		//tp = tempTp;
		//if (tp.isEmpty()){
		//	tp.addStyleRange(tp.getDefaultStyleRange());
		//	System.out.println("tp has no default");
		//}
		//Color bgColor
		//Display display = getEditorPart().getSite().getShell().getDisplay();
		
		getDisplay().asyncExec( new Runnable() {
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
			if ((sa.getJavaStartLn() == 0 && sa.getJavaEndLn() == 0)){
			}
			else {
				if (sa.getJavaStartPos() == 0 && sa.getJavaEndPos() == 0){
				}
				else {			
					if (sa.getColor() == null){
					}
					else {
						//System.out.println("java line: "+sa.getJava_ln()+" start: "+sa.getJavaOffsetStart()+1+" end: "+ sa.getJavaOffsetEnd()+1);
							
                		boolean fg = false;
                		if (sa.getColor().fg() == 1){
                    		fg = true;
                		}
                
						setAttributeTextColor(tp, sa.getJavaStartLn(), sa.getJavaEndLn(), sa.getJavaStartPos()+1, sa.getJavaEndPos()+1, sa.getRGBColor(), fg);//, tp);
					}
				}
			}
			// sets colors for valueboxes
			/*if (sa.getValueAttrs() != null){
				Iterator valIt = sa.getValueAttrs().iterator();
				while (valIt.hasNext()){
					PosColAttribute vba = (PosColAttribute)valIt.next();
					
					if (vba.getSourceStartOffset() == 0 && vba.getSourceEndOffset() == 0){
						
					}
					else {
					
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
			}*/
		}
		/*Iterator tempIt = temp.iterator();
				while (tempIt.hasNext()){
					tp.addStyleRange((StyleRange)tempIt.next());
				}
		changeTextPres(tp);*/
		final StyleRange [] srs = new StyleRange[styleList.size()];
		styleList.toArray(srs);
		
		getDisplay().asyncExec(new Runnable(){
			public void run(){
				for (int i = 0; i < srs.length; i++){
				
					try{
					//System.out.println("Style Range: "+srs[i]);
					getViewer().getTextWidget().setStyleRange(srs[i]);
					}
					catch(Exception e){
						System.out.println(e.getMessage());
					}
				}
			};
		});	
		//return tp;
		//System.out.println("Stop Coloring: "+System.currentTimeMillis());
		
					
	}
	
	private void setAttributeTextColor(TextPresentation tp, int sline, int eline, int start, int end, RGB colorKey, boolean fg) {//, TextPresentation tp){
		//System.out.println("startline: "+sline+" soffset: "+start+" endoffset: "+end);
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
		int [] offsets = new int[eline-sline+1];
		int [] starts = new int[eline-sline+1];
		int [] lengths = new int[eline-sline+1];
		int unColLen = start < end ? start-1 : end-2;
                	
		try {
			int j = 0;
			for (int i = sline; i <= eline; i++){
				offsets[j] = getViewer().getDocument().getLineOffset((i-1));
				//System.out.println("offset at "+j+" is: "+offsets[j]);
				starts[j] = offsets[j] + unColLen;
				lengths[j] = getViewer().getDocument().getLineOffset((i)) - 1 - starts[j];
				j++;
			}
			sLineOffset = getViewer().getDocument().getLineOffset((sline-1));
			eLineOffset = getViewer().getDocument().getLineOffset((eline-1));
			//System.out.println("slineOffset: "+sLineOffset);
		}
		catch(Exception e){	
			return;
		}
		
		int longest = 0;
		for (int i = 0; i < lengths.length; i++){
			if (lengths[i] >longest){
				longest = lengths[i];
			}
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
      
		//display.asyncExec( new Runnable() {
		//	public void run() {
		//		TextPresentation tp = new TextPresentation();
		//System.out.println("about to create style range");
                StyleRange sr;
                //System.out.println("line: "+sline+" start: "+s+" length: "+l);
                if (l > 0){
                if (sline == eline){
                	//System.out.println("start: "+start+" end: "+end);
                	if (foreground){
				    	sr = new StyleRange(s, l, ck, getBgColor());
                	}
                	else {
                    	sr = new StyleRange(s, l, oldBgC, ck);
                	}
				
					styleList.add(sr);
                }
                else {
                	for (int i = 0; i < starts.length; i++){
                		
                		if (foreground){
							sr = new StyleRange(starts[i], lengths[i], ck, getBgColor());
                	
                		}
                		else {
							sr = new StyleRange(starts[i], lengths[i], oldBgC, ck);
                	
                		}
						styleList.add(sr);	
                	}
					
                }
                
                /*for (int j = 1; j < offsets.length; j++){
                	
                	StyleRange lineBegin = new StyleRange(offsets[j], unColLen, oldBgC, getBgColor());
                	styleList.add(lineBegin);
                }*/
                //if (count == 0 | count == 1){
        		//getViewer().getTextWidget().replaceStyleRanges(s, l, sr);        
				//	tp.addStyleRange(sr);
					//tp.addStyleRange(tp.getDefaultStyleRange());			
                //styleList.add(sr);
                }
            //}
				//count++;
				//try {
				
		//getViewer().changeTextPresentation(tp, true);
				//}
				//catch (Exception e){
				/*	System.out.println(e.getMessage());
						//getViewer().setTextColor(ck, s, l, false);
				}
			};
		});*/
		
		//tp.clear();
			
		
	}
	private ArrayList sortAttrsByLength(ArrayList attrs){
		
		Iterator it = attrs.iterator();
		while(it.hasNext()){
			SootAttribute sa = (SootAttribute)it.next();
			int sLineOffset = 0;
			int eLineOffset = 0;
			try {
				sLineOffset = getViewer().getDocument().getLineOffset((sa.getJavaStartLn()-1));
				eLineOffset = getViewer().getDocument().getLineOffset((sa.getJavaEndLn()-1));
			}
			catch(Exception e){	
			
			}
			
			int sOffset = sLineOffset + sa.getJavaStartPos() - 1;
			int eOffset = eLineOffset + sa.getJavaEndPos() - 1;
			int length = sOffset - eOffset;
			sa.setJavaLength(length);
			
		}
		
		SootAttribute [] sorted = new SootAttribute[attrs.size()];
		attrs.toArray(sorted);
		for (int i = 0; i< sorted.length; i++){
			for (int j = i; j < sorted.length; j++){
				if (sorted[j].getJavaLength() > sorted[i].getJavaLength()){
					SootAttribute temp = sorted[i];
					sorted[i] = sorted[j];
					sorted[j] = temp;
				}
			}
		}
		
		ArrayList sortedArray = new ArrayList();
		for (int i = sorted.length - 1; i >= 0; i--){
			sortedArray.add(sorted[i]);
		}
		
		return sortedArray;
	}
    
    private void changeTextPres(TextPresentation tp) {
		//System.out.println("changing text pres");
		Display display = getEditorPart().getSite().getShell().getDisplay();
		final TextPresentation pres = tp;
		display.asyncExec(new Runnable() {
			public void run(){
				getViewer().changeTextPresentation(pres, true);
			};
		});
    }
    
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

}
