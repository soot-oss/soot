package ca.mcgill.sable.soot.launching;

import java.util.HashMap;

import org.eclipse.jface.action.IAction;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.ui.SootConfigManagerDialog;

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
public class SootConfigProjectLauncher extends SootProjectLauncher {

	public void run(IAction action) {
		
		super.run(action);
		
		SootConfigManagerDialog manager = new SootConfigManagerDialog(getWindow().getShell());
		manager.setEclipseDefList(setEclipseDefs());
		manager.setLauncher(this);
		manager.open();
		
		
	}
	
	public void launch(String name) {
		
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		
		setSootCommandList(new SootCommandList());
		//System.out.println(settings.get(choosen));
		SootSavedConfiguration ssc = new SootSavedConfiguration(name, settings.get(name));
		ssc.setEclipseDefs(setEclipseDefs());
		getSootCommandList().addSingleOpt(ssc.toRunString());
		//System.out.println("set SootCommandList");
		runSootDirectly();
		runFinish();
	}
	
	private HashMap setEclipseDefs() {
		
		HashMap defs = new HashMap();
		defs.put(LaunchCommands.OUTPUT_DIR, getOutputLocation());
		//System.out.println("setting eclipse defs");
		//System.out.println(getOutputLocation());
		
		defs.put(LaunchCommands.SOOT_CLASSPATH, getSootClasspath().getSootClasspath()+getSootClasspath().getSeparator()+getProcess_path());
		//System.out.println(getSootClasspath().getSootClasspath()+getSootClasspath().getSeparator()+getProcess_path());
		
		defs.put(LaunchCommands.PROCESS_PATH, getProcess_path());
		//System.out.println(getProcess_path());
		//System.out.println("presetting process-path"+getProcess_path());
		defs.put(LaunchCommands.KEEP_LINE_NUMBER, new Boolean(true));
		//getSdc().setKeepLineNum();
		//System.out.println("presetting keep line num");
		defs.put(LaunchCommands.XML_ATTRIBUTES, new Boolean(true));
		//getSdc().setPrintTags();	
		//System.out.println("presetting print tags");
		
		//ssc.setEclipseDefs(defs);
		return defs;
	}
	
	/*private void setEclipseDefs(SootSavedConfiguration ssc) {
		
		HashMap defs = new HashMap();
		defs.put(LaunchCommands.OUTPUT_DIR, getOutputLocation());
		//System.out.println("setting eclipse defs");
		//System.out.println(getOutputLocation());
		
		defs.put(LaunchCommands.SOOT_CLASSPATH, getSootClasspath().getSootClasspath()+getSootClasspath().getSeparator()+getProcess_path());
		//System.out.println(getSootClasspath().getSootClasspath()+getSootClasspath().getSeparator()+getProcess_path());
		
		defs.put(LaunchCommands.PROCESS_PATH, getProcess_path());
		//System.out.println(getProcess_path());
		//System.out.println("presetting process-path"+getProcess_path());
		defs.put(LaunchCommands.KEEP_LINE_NUMBER, new Boolean(true));
		//getSdc().setKeepLineNum();
		//System.out.println("presetting keep line num");
		defs.put(LaunchCommands.XML_ATTRIBUTES, new Boolean(true));
		//getSdc().setPrintTags();	
		//System.out.println("presetting print tags");
		
		ssc.setEclipseDefs(defs);
		//return defs;
	}*/
	
}
