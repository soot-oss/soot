package ca.mcgill.sable.soot.editors;

import java.util.*;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
//import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import ca.mcgill.sable.soot.SootPlugin;

//import ca.mcgill.sable.soot.launching.SootConfiguration;

/**
 * @author jlhotak
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
		//System.out.println("getting image");
		ImageDescriptor descriptor = null;
		if (element instanceof JimpleOutlineObject){
			//System.out.println("is JimpleOutlineObject");
			//System.out.println(((JimpleOutlineObject)element).getType());
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
					//System.out.println("is method type");
					descriptor = SootPlugin.getImageDescriptor("public_co.gif");
					//System.out.println("found descriptor for method image");
					break;
				}
				case JimpleOutlineObject.PROTECTED_METHOD:{
					//System.out.println("is method type");
					descriptor = SootPlugin.getImageDescriptor("protected_co.gif");
					//System.out.println("found descriptor for method image");
					break;
				}
				case JimpleOutlineObject.PRIVATE_METHOD:{
					//System.out.println("is method type");
					descriptor = SootPlugin.getImageDescriptor("private_co.gif");
					//System.out.println("found descriptor for method image");
					break;
				}
				case JimpleOutlineObject.NONE_METHOD: {
					descriptor = SootPlugin.getImageDescriptor("default_co.gif");
					//System.out.println("found descriptor for method image");
					break;
				}
				case JimpleOutlineObject.PUBLIC_FIELD: {
					//System.out.println("is field type");
					descriptor = SootPlugin.getImageDescriptor("field_public_obj.gif");
					break;
				}
				case JimpleOutlineObject.PROTECTED_FIELD: {
					//System.out.println("is field type");
					descriptor = SootPlugin.getImageDescriptor("field_protected_obj.gif");
					break;
				}
				case JimpleOutlineObject.PRIVATE_FIELD: {
					//System.out.println("is field type");
					descriptor = SootPlugin.getImageDescriptor("field_private_obj.gif");
					break;
				}	
				case JimpleOutlineObject.NONE_FIELD: {
					//System.out.println("is field type");
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
		 	//System.out.println("image was null");
		  	image = descriptor.createImage();
		  	//System.out.println("created image");
		   	getImageCache().put(descriptor, image);
		}
		
		/*if (element instanceof JimpleOutlineObject){
			BitSet bits = ((JimpleOutlineObject)element).getDecorators();
			if (bits != null){
				if (bits.get(JimpleOutlineObject.FINAL_DEC)){
					descriptor = SootPlugin.getImageDescriptor("final_co.gif");
					// add this descriptor image to image as decoration ??
					image = descriptor.createImage();
				}
			}
		}*/
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
