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

package ca.mcgill.sable.soot.testing;

import org.eclipse.jface.action.*;
//import org.eclipse.jface.dialogs.*;
//import org.eclipse.jface.preference.PreferenceManager;

//import ca.mcgill.sable.soot.SootPlugin;
import ca.mcgill.sable.soot.launching.SootLauncher;
/**
 * @author jlhotak
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SootTestingLauncher extends SootLauncher {

	public void run(IAction action) {
		
		super.run(action);
		
		/*TestDialog dialog = new TestDialog(window.getShell(),
         getSootSelection().getProject());
        dialog.open();
        
        IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
        System.out.println(settings.get("jimple_button_select")); 
        System.out.println(settings.get("jimp_button_select"));
        System.out.println(settings.get("grimp_button_select"));
        System.out.println(settings.get("b_button_select"));
        System.out.println(settings.get("baf_button_select"));
        */
        /*PreferenceManager pm = new PreferenceManager();
        
        SootOptionsTreeDialog dialog = new SootOptionsTreeDialog(window.getShell());
        dialog.open();*/
        
        /*TestOutputDialog dialog = new TestOutputDialog(window.getShell(),
        	getSootSelection().getProject());
        dialog.open();*/
      	
		
	}

	public void setClasspathAppend(String ca){}
}
