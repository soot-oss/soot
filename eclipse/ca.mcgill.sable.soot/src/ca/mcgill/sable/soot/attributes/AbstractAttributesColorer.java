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

public abstract class AbstractAttributesColorer {

    private ITextViewer viewer;
    private IEditorPart editorPart;
    private Color bgColor;
    private Display display;
    private ArrayList styleList;
    private SootAttributesHandler handler;
    private ColorManager colorManager;

    protected void init(){
        setColorManager(SootPlugin.getDefault().getColorManager());
        setDisplay(getEditorPart().getSite().getShell().getDisplay());
        clearPres();
    }

    protected abstract void computeColors();
    
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
    
    protected ArrayList sortAttrsByLength(ArrayList attrs){
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
  
            setLength(sa, length);
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
    
    // set java or jimple length;
    protected abstract void setLength(SootAttribute sa, int length);
    
    protected void changeStyles(){
        final StyleRange [] srs = new StyleRange[styleList.size()];
        styleList.toArray(srs);

        getDisplay().asyncExec(new Runnable(){
            public void run(){
                for (int i = 0; i < srs.length; i++){
                    try{
                        System.out.println("Style Range: "+srs[i]);
                        getViewer().getTextWidget().setStyleRange(srs[i]);
                    }
                    catch(Exception e){
                        System.out.println("Seting Style Range Ex: "+e.getMessage());
                    }
                }
            };
        });        
    }

    protected void setAttributeTextColor(int sline, int eline, int spos, int epos, RGB colorKey, boolean fg){
        int sLineOffset = 0;
        int eLineOffset = 0;
        int [] offsets = new int[eline-sline+1];
        int [] starts = new int[eline-sline+1];
        int [] lengths = new int[eline-sline+1];
        int unColLen = spos < epos ? spos-1: epos-1;

        try {
            int j = 0;
            for (int i = sline; i <= eline; i++){
                offsets[j] = getViewer().getDocument().getLineOffset((i-1));
                starts[j] = offsets[j] + unColLen;
                lengths[j] = getViewer().getDocument().getLineOffset(i) - 1 - starts[j];
                j++;
            }
            sLineOffset = getViewer().getDocument().getLineOffset((sline-1));
            eLineOffset = getViewer().getDocument().getLineOffset((eline-1));
        }
        catch(Exception e){
            return;
        }

        int sOffset = sLineOffset + spos - 1;
        int eOffset = eLineOffset + epos - 1;
        int len = eOffset - sOffset;

        Color color = getColorManager().getColor(colorKey);
        Color oldBgColor = getColorManager().getColor(IJimpleColorConstants.JIMPLE_DEFAULT);

        StyleRange sr;
        if (len > 0){
            if (sline == eline){
                if (fg){
                    sr = new StyleRange(sOffset-1, len+1, color, getBgColor());
                }
                else {
                    sr = new StyleRange(sOffset, len, oldBgColor, color);
                }
                //System.out.println("adding sr: "+sr);
                getStyleList().add(sr);
            }
            else {
                for (int i = 0; i < starts.length; i++){
                    if (fg){
                        sr = new StyleRange(starts[i]-1, lengths[i]+1, color, getBgColor());
                    }
                    else {
                        sr = new StyleRange(starts[i], lengths[i], oldBgColor, color);
                    }
                    //System.out.println("adding sr: "+sr);
                    getStyleList().add(sr);
                }
            }
        }
    }
	/**
	 * @return
	 */
	public Color getBgColor() {
		return bgColor;
	}

	/**
	 * @return
	 */
	public ColorManager getColorManager() {
		return colorManager;
	}

	/**
	 * @return
	 */
	public Display getDisplay() {
		return display;
	}

	/**
	 * @return
	 */
	public IEditorPart getEditorPart() {
		return editorPart;
	}

	/**
	 * @return
	 */
	public SootAttributesHandler getHandler() {
		return handler;
	}

	/**
	 * @return
	 */
	public ArrayList getStyleList() {
		return styleList;
	}

	/**
	 * @return
	 */
	public ITextViewer getViewer() {
		return viewer;
	}

	/**
	 * @param color
	 */
	public void setBgColor(Color color) {
		bgColor = color;
	}

	/**
	 * @param manager
	 */
	public void setColorManager(ColorManager manager) {
		colorManager = manager;
	}

	/**
	 * @param display
	 */
	public void setDisplay(Display display) {
		this.display = display;
	}

	/**
	 * @param part
	 */
	public void setEditorPart(IEditorPart part) {
		editorPart = part;
	}

	/**
	 * @param handler
	 */
	public void setHandler(SootAttributesHandler handler) {
		this.handler = handler;
	}

	/**
	 * @param list
	 */
	public void setStyleList(ArrayList list) {
		styleList = list;
	}

	/**
	 * @param viewer
	 */
	public void setViewer(ITextViewer viewer) {
		this.viewer = viewer;
	}

}
