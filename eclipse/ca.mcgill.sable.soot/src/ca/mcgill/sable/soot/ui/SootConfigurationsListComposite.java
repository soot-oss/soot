package ca.mcgill.sable.soot.ui;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;
import java.util.Vector;

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
public class SootConfigurationsListComposite extends Composite {
	
	/**
	 * Constructor for SootConfigurationsListComposite.
	 * @param parent
	 * @param style
	 */
	public SootConfigurationsListComposite(Composite parent, int style) {
		super(parent, style);
	}
		
	/**
	 * Constructor for SootConfigurationsListComposite.
	 * @param parent
	 * @param style
	 */
	public SootConfigurationsListComposite(Composite parent, int style, Vector ListData) {
		super(parent, style);
	
		Text selectionField = new Text(parent, SWT.NONE);
		List configs = new List(parent, SWT.SINGLE);
		
	
	}
	
	
}
