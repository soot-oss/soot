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


import java.util.ArrayList;

import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.*;
import ca.mcgill.sable.soot.SootPlugin;
import ca.mcgill.sable.soot.ui.*;
/**
 * Displays the Soot Options dialog and launches Soot with
 * selected options on all class files in output dir of 
 * selected project.
 */
public class SootOptionsFolderLauncher extends SootFolderLauncher {

	public void run(IAction action) {
		
		super.run(action);
		
		window = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		
      	PhaseOptionsDialog dialog = new PhaseOptionsDialog(window.getShell());
        setSdc(new SootDefaultCommands(dialog));
        presetDialog();
        dialog.open();
        if (dialog.getReturnCode() == Dialog.CANCEL) {	
        	SavedConfigManager scm = new SavedConfigManager();
			scm.setEditMap(dialog.getEditMap());
			scm.handleEdits();
      	}
      	else {
      		SootSavedConfiguration ssc = new SootSavedConfiguration("Temp", dialog.getConfig());
      		ssc.toSaveArray();
      		
      		
      		setCmd(ssc.toRunArray());
      		String mainClass = dialog.getSootMainClass();
      		if ((mainClass == null) || (mainClass.length() == 0)){
      			runSootDirectly();
      		}
      		else {
      			runSootDirectly(mainClass);
      		}
			runFinish();
			
			// save config if nessesary
			SavedConfigManager scm = new SavedConfigManager();
			scm.setEditMap(dialog.getEditMap());
			scm.handleEdits();
      	}
	}
	
	private void presetDialog() {
		getSdc().setOutputDir(getOutputLocation());
		getSdc().setSootClassPath(getProcessPath()+getSootClasspath().getSeparator()+getClasspathAppend());
		getSdc().setProcessPath(getProcessPath());
		getSdc().setKeepLineNum();
		getSdc().setPrintTags();	
		getSdc().setSootMainClass();
	}
	
	// TODO use this method instaed of one with String
	private void setCmd(ArrayList user_cmd){
		getSootCommandList().addSingleOpt(user_cmd);
	}
	
	private void setCmd(String user_cmd) {
		
		getSootCommandList().addSingleOpt(user_cmd);
	
	}
}
