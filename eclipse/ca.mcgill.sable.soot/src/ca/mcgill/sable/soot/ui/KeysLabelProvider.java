/*
 * Created on Nov 19, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.ui;

import java.util.*;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

import ca.mcgill.sable.soot.SootPlugin;
import ca.mcgill.sable.soot.attributes.AnalysisKey;
import ca.mcgill.sable.soot.editors.ColorManager;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class KeysLabelProvider implements ITableLabelProvider {

	private HashMap images;
	private ArrayList imageList;
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		AnalysisKey key = (AnalysisKey)element;
		ColorManager colorManager = SootPlugin.getDefault().getColorManager();
		RGB rgb = new RGB(key.getRed(), key.getGreen(), key.getBlue());
		Color c = colorManager.getColor(rgb);
		if (imageList == null){
			imageList = new ArrayList();
		}
		if (images == null){
			images = new HashMap();
		}
		Image image = null;
		ImageDescriptor id = SootPlugin.getImageDescriptor("key.png");
		if (!images.isEmpty()){
			Iterator it = images.keySet().iterator();
			while (it.hasNext()){
				RGB next = (RGB)it.next();
				if (next.red == key.getRed() && next.green == key.getGreen() && next.blue == key.getBlue()){
					image = (Image)images.get(next);
					//System.out.println("reusing key image");
				}
			}
		}
		//Image image = (Image)images.get(rgb);
		//Image image;// = (Image)images.get(id);
		if (image == null) {
			image = id.createImage();
			//System.out.println("creating new key image");
			images.put(rgb, image);
		}
		//images.put(id, image);
		//imageList.add(image);
		GC gc = new GC(image);
		gc.setBackground(c);
		gc.fillRectangle(3,3,9,9);
		/*if (gcs == null){
			gcs = new ArrayList();
		}
		gcs.add(gc);*/
		//image.setBackground(c);
		gc.dispose();
		//colorManager.dispose();
		
		
		return image;
		//return null;
	}

	public void dispose(){
		if (images == null) return;
		/*Iterator it = imageList.iterator();
		while (it.hasNext()){
			Object next = it.next();
			System.out.println("next: "+next.getClass());
			((Image)next).dispose();
		}*/
		Iterator it2 = images.keySet().iterator();
		while (it2.hasNext()){
			((Image)images.get(it2.next())).dispose();
			System.out.println("disposing key image");
		}
		images = null;
		//System.out.println("disposed key images");
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return ((AnalysisKey)element).getKey();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

}
