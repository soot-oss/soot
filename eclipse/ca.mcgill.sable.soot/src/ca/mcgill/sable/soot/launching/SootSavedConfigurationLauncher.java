package ca.mcgill.sable.soot.launching;

import org.eclipse.jface.action.IAction;
import ca.mcgill.sable.soot.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

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
public class SootSavedConfigurationLauncher extends SootLauncher {

	public void run(IAction action) {
		
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		int numConfig = 0;
		try {
			numConfig = settings.getInt("config_count");
		}
		catch(NumberFormatException e) {
		}

		System.out.println("config_count: "+numConfig);
		if (numConfig != 0) {		
			String [] configNames = new String[numConfig];
			
			for (int i = 0; i < numConfig; i++) {
				configNames[i] = settings.get("soot_run_config_"+(i+1));
				//System.out.println("configNames[i]: "+configNames[i]);
			}
			
	
			ElementListSelectionDialog configChooser = new ElementListSelectionDialog(getWindow().getShell(), new LabelProvider());
			//System.out.println("configChooser created");
			configChooser.setElements(configNames);
			//System.out.println("set elements");
			configChooser.setTitle("Soot Configuration Chooser");
			configChooser.setMessage("Select:");
			configChooser.setMultipleSelection(false);
			configChooser.open(); 
			
			if (configChooser.getReturnCode() == Dialog.OK) {
				String choosen = (String)configChooser.getFirstResult();
				System.out.println("choosen: "+choosen);
				//SootSavedConfiguration config = new SootSavedConfiguration(choosen, settings.get(choosen));
				setSootCommandList(new SootCommandList());
				//System.out.println(settings.get(choosen));
				SootSavedConfiguration ssc = new SootSavedConfiguration(choosen, settings.get(choosen));
				getSootCommandList().addSingleOpt(ssc.toRunString());
				//System.out.println("set SootCommandList");
				runSootDirectly();
			}
		}
		else {
			MessageDialog noConfigs = new MessageDialog(getWindow().getShell(), "Soot Configuration Chooser Message", null, "There are no saved configurations to launch!", 0, new String [] {"OK"}, 0);	
			noConfigs.open();
		}
	}
}
