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
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.testing.PhaseOptionsDialog;

/**
 * @author jlhotak
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SootOptionsFileLauncher extends SootFileLauncher {

	public void run(IAction action) {
		
		super.run(action);
		
		if (isDoNotContinue()) return;
		// sometimes window needs to be reset (not sure why)
		window = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			//System.out.println("window is null");
		}
		else {
			//System.out.println("window not null");
		}
		
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
      		
      		
      		//HashMap temp = dialog.getOkMap();
      		//System.out.println("ok map: "+temp.get("test"));
      		//TestOptionsDialogHandler handler = new TestOptionsDialogHandler();
      		
      		// TODO switch these 2 lines
      		setCmd(ssc.toRunArray());
      		//setCmd(ssc.toRunString());
      		//System.out.println("to run String: "+ssc.toRunString());
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
		/*window = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			System.out.println("window is null");
		}
		else {
			System.out.println("window not null");
		}*/
			
		/*SootOptionsLauncherDialog dialog = new SootOptionsLauncherDialog(window.getShell(),
         getSootSelection().getProject());
      	dialog.open();*/
      	
      	/*if (dialog.getReturnCode() == Dialog.CANCEL) {	
      	}
      	else {
			IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
			String cmd = getCmd(settings.get("text_input"));
			runSootAsProcess(cmd);
			runFinish();
      	}*/
		
		
	}
	
	private void presetDialog() {
		getSdc().setOutputDir(getOutputLocation());
		getSdc().setSootClassPath(getSootClasspath().getSootClasspath()+getSootClasspath().getSeparator()+getClasspathAppend());
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
		getSootCommandList().addSingleOpt(getToProcess());
	}
	private void setCmd(String user_cmd) {
		
		/*StringBuffer classpath = new StringBuffer(LaunchCommands.SOOT_CLASSPATH);
		classpath.append(getSootClasspath().getSootClasspath());
		classpath.append(getSootClasspath().getSeparator());
		classpath.append(getClasspathAppend());
		
		
		String output_path = LaunchCommands.OUTPUT_DIR+getOutputLocation();
				
		*/
		//StringBuffer cmd = new StringBuffer();
		/*cmd.append(classpath+" ");
		cmd.append(output_path+" ");
		cmd.append(getToProcess()+" ");
		if (isExtraCmd()) {
			cmd.append(getExtraCmd()+" ");
		}*/
		getSootCommandList().addSingleOpt(user_cmd);
		ArrayList commands = new ArrayList();
		commands.add(getToProcess());
		//getSootCommandList().addSingleOpt(getToProcess());
		getSootCommandList().addSingleOpt(commands);
	  	//return cmd.toString();
	  	
		
	}

}
