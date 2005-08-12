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
import java.util.Iterator;

import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.ui.PhaseOptionsDialog;

/**
 * Displays the Soot Options dialog and launches Soot with
 * selected options on selected file.
 */
public class SootOptionsFromJavaFileLauncher extends SootFileLauncher {

	public void run(IAction action) {
		
		super.run(action);
        super.setIsSrcPrec(true);
        super.setSrcPrec(LaunchCommands.JAVA_IN);
        super.handleMultipleFiles();
		
		if (isDoNotContinue()) return;
		// sometimes window needs to be reset (not sure why)
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
		getSdc().setSootClassPath(getClasspathAppend());
		if (isSrcPrec()) {
			getSdc().setSrcPrec(getSrcPrec());
		}
		getSdc().setKeepLineNum();
		getSdc().setPrintTags();	
		getSdc().setSootMainClass();
	}
	
	// TODO use this instead of String one
	private void setCmd(ArrayList user_cmd){
		getSootCommandList().addSingleOpt(user_cmd);
		Iterator it = getToProcessList().iterator();
		while (it.hasNext()){
			getSootCommandList().addSingleOpt((String)it.next());
		}
	}
	private void setCmd(String user_cmd) {
		
		
		getSootCommandList().addSingleOpt(user_cmd);
		ArrayList commands = new ArrayList();
		Iterator it = getToProcessList().iterator();
		while (it.hasNext()){
			commands.add((String)it.next());
		}
		getSootCommandList().addSingleOpt(commands);
		
	}

}
