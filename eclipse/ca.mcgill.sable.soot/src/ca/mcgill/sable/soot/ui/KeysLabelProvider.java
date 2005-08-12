/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
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

public class KeysLabelProvider implements ITableLabelProvider {

	private HashMap images;
	private ArrayList imageList;
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
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
				}
			}
		}
		if (image == null) {
			image = id.createImage();
			images.put(rgb, image);
		}
		GC gc = new GC(image);
		gc.setBackground(c);
		gc.fillRectangle(3,3,9,9);
		gc.dispose();
		
		
		return image;
	}

	public void dispose(){
		if (images == null) return;
		Iterator it2 = images.keySet().iterator();
		while (it2.hasNext()){
			((Image)images.get(it2.next())).dispose();
		}
		images = null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		return ((AnalysisKey)element).getKey();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {

	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {

	}

}
