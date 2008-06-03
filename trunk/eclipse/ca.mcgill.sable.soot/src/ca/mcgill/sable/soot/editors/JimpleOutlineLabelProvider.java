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


package ca.mcgill.sable.soot.editors;

import java.util.*;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import ca.mcgill.sable.soot.SootPlugin;



public class JimpleOutlineLabelProvider implements ILabelProvider {

	private HashMap imageCache;
	
	/**
	 * Constructor for OptionsTreeLabelProvider.
	 */
	public JimpleOutlineLabelProvider() {
		super();
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(Object)
	 */
	public Image getImage(Object element) {
		
		ImageDescriptor descriptor = null;
		if (element instanceof JimpleOutlineObject){
			
			switch (((JimpleOutlineObject)element).getType()){
				case JimpleOutlineObject.CLASS: {
					descriptor = SootPlugin.getImageDescriptor("class_obj.gif");
					break;
				}
				case JimpleOutlineObject.INTERFACE: {
					descriptor = SootPlugin.getImageDescriptor("int_obj.gif");
					break;
				}
				case JimpleOutlineObject.PUBLIC_METHOD:{
					descriptor = SootPlugin.getImageDescriptor("public_co.gif");
					break;
				}
				case JimpleOutlineObject.PROTECTED_METHOD:{
					descriptor = SootPlugin.getImageDescriptor("protected_co.gif");
					break;
				}
				case JimpleOutlineObject.PRIVATE_METHOD:{
					descriptor = SootPlugin.getImageDescriptor("private_co.gif");
					break;
				}
				case JimpleOutlineObject.NONE_METHOD: {
					descriptor = SootPlugin.getImageDescriptor("default_co.gif");
					break;
				}
				case JimpleOutlineObject.PUBLIC_FIELD: {
					descriptor = SootPlugin.getImageDescriptor("field_public_obj.gif");
					break;
				}
				case JimpleOutlineObject.PROTECTED_FIELD: {
					descriptor = SootPlugin.getImageDescriptor("field_protected_obj.gif");
					break;
				}
				case JimpleOutlineObject.PRIVATE_FIELD: {
					descriptor = SootPlugin.getImageDescriptor("field_private_obj.gif");
					break;
				}	
				case JimpleOutlineObject.NONE_FIELD: {
					descriptor = SootPlugin.getImageDescriptor("field_default_obj.gif");
					break;
				}				
				default:{
					return null;
					
				}
			}
		}
		if (getImageCache() == null){
			setImageCache(new HashMap());
			
		}
		Image image = (Image)getImageCache().get(descriptor);
		if (image == null) {
		  	image = descriptor.createImage();
		   	getImageCache().put(descriptor, image);
            
		}
		
	
		return image;
		
		
		
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(Object)
	 */
	public String getText(Object element) {
		return ((JimpleOutlineObject)element).getLabel();
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		if (getImageCache() != null){
			Iterator it = getImageCache().values().iterator();
			while (it.hasNext()){
				((Image)it.next()).dispose();	
			}
			getImageCache().clear();
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(Object, String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
	}

	/**
	 * @return
	 */
	public HashMap getImageCache() {
		return imageCache;
	}

	/**
	 * @param map
	 */
	public void setImageCache(HashMap map) {
		imageCache = map;
	}

}
