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

package ca.mcgill.sable.soot.launching;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.ui.SootConfigManagerDialog;

import org.eclipse.jface.dialogs.*;


/**
 * Launches a saved Soot configuration on the selected file
 */
public class SootConfigFileLauncher extends SootFileLauncher {

	public void run(IAction action) {
		
		super.run(action);
        super.handleMultipleFiles();
        
		if (isDoNotContinue()) return;
		window = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		
		SootConfigManagerDialog manager = new SootConfigManagerDialog(window.getShell());
		manager.setEclipseDefList(setEclipseDefs());
		manager.setLauncher(this);
		manager.open();
		
		
	}
	
	public void launch(String name, String mainClass) {
				
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		
		setSootCommandList(new SootCommandList());
		
		
		SootSavedConfiguration ssc = new SootSavedConfiguration(name, settings.getArray(name));
		ssc.setEclipseDefs(setEclipseDefs());
		
		getSootCommandList().addSingleOpt(ssc.toRunArray());
		
		Iterator it = getToProcessList().iterator();
		while (it.hasNext()){
			getSootCommandList().addSingleOpt((String)it.next());
		}
		
		getSootCommandList().printList();
		
		if ((mainClass == null) || (mainClass.length() == 0)){
			runSootDirectly();
		}
		else {
			runSootDirectly(mainClass);
		}
		runFinish();
	}
	
	private HashMap setEclipseDefs() {
		
		HashMap defs = new HashMap();
		defs.put(LaunchCommands.OUTPUT_DIR, getOutputLocation());
		
		defs.put(LaunchCommands.SOOT_CLASSPATH, getClasspathAppend());
		
		if (isSrcPrec()) {
			defs.put(LaunchCommands.SRC_PREC, getSrcPrec());
		}
		defs.put(LaunchCommands.KEEP_LINE_NUMBER, new Boolean(true));

		defs.put(LaunchCommands.XML_ATTRIBUTES, new Boolean(true));
	
		return defs;
	}

	
}
