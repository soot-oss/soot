package ca.mcgill.sable.soot.launching;

import java.util.ArrayList;
//import java.util.HashMap;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.LabelProvider;

import ca.mcgill.sable.soot.SootPlugin;
import ca.mcgill.sable.soot.ui.*;
import org.eclipse.jface.dialogs.*;
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

// classs not used
public class SootEditConfigProjectLauncher extends SootProjectLauncher {
	
	public void run(IAction action) {
		super.run(action);
		// show dialog with list of configs and
		// ok and cancel buttons at bottom and
		// edit and remove buttons at side
		
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		int numConfig = 0;
		try {
			numConfig = settings.getInt("config_count");
		}
		catch(NumberFormatException e) {
		}

		//System.out.println("config_count: "+numConfig);
		if (numConfig != 0) {		
			//String [] configNames = new String[numConfig];
			ArrayList configNames = new ArrayList();
			for (int i = 0; i < numConfig; i++) {
				configNames.add(settings.get("soot_run_config_"+(i+1)));
				//System.out.println("configNames[i]: "+configNames[i]);
			}
			
			EditSavedConfigDialog ecd = new EditSavedConfigDialog(getWindow().getShell(), new LabelProvider(), configNames);
			ecd.setElements(configNames.toArray());
			ecd.setTitle("Soot Configuration Editor");
			ecd.setMessage("Select:");
			ecd.setMultipleSelection(false);
			addEclipseDefsToDialog(ecd);
			ecd.open();
			
			if (ecd.getReturnCode() == Dialog.OK) {
				SavedConfigManager scm = new SavedConfigManager();
				scm.setEditMap(ecd.getEditMap());
				scm.handleEdits();
				scm.setDeleteList(ecd.getDeleteList());
				scm.handleDeletes();
		
			}
			else {
			}
			
			
		}
		else {
			MessageDialog noConfigs = new MessageDialog(getWindow().getShell(), "Soot Configuration Chooser Message", null, "There are no saved configurations to edit!", 0, new String [] {"OK"}, 0);	
			noConfigs.open();
		}
		
	}
	
	private void addEclipseDefsToDialog(EditSavedConfigDialog dialog) {
		//HashMap defs = new HashMap();
		dialog.addToEclipseDefList(LaunchCommands.OUTPUT_DIR, getOutputLocation());
		System.out.println("setting eclipse defs");
		System.out.println(getOutputLocation());
		//System.out.println("presetting output dir");
		dialog.addToEclipseDefList(LaunchCommands.SOOT_CLASSPATH, getSootClasspath().getSootClasspath()+getSootClasspath().getSeparator()+getProcess_path());
		System.out.println(getSootClasspath().getSootClasspath()+getSootClasspath().getSeparator()+getProcess_path());
		//System.out.println("presetting cp");
		dialog.addToEclipseDefList(LaunchCommands.PROCESS_PATH, getProcess_path());
		System.out.println(getProcess_path());
		//System.out.println("presetting process-path"+getProcess_path());
		dialog.addToEclipseDefList(LaunchCommands.KEEP_LINE_NUMBER, new Boolean(true));
		//getSdc().setKeepLineNum();
		//System.out.println("presetting keep line num");
		dialog.addToEclipseDefList(LaunchCommands.XML_ATTRIBUTES, new Boolean(true));
		//getSdc().setPrintTags();	
		//System.out.println("presetting print tags");
	
	}
}
