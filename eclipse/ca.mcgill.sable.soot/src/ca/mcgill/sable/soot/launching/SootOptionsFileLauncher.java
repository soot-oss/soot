package ca.mcgill.sable.soot.launching;

import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.*;
import ca.mcgill.sable.soot.*;

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
		
		window = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			System.out.println("window is null");
		}
		else {
			System.out.println("window not null");
		}
			
		SootOptionsLauncherDialog dialog = new SootOptionsLauncherDialog(window.getShell(),
         getSootSelection().getProject());
      	dialog.open();
      	
      	if (dialog.getReturnCode() == Dialog.CANCEL) {	
      	}
      	else {
			IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
			String cmd = getCmd(settings.get("text_input"));
			runSootAsProcess(cmd);
			runFinish();
      	}
		
		
	}
	
	private void presetDialog() {
		getSdc().setOutputDir(getOutputLocation());
		getSdc().setSootClassPath(getSootClasspath().getSootClasspath()+getSootClasspath().getSeparator()+getClasspathAppend());
		getSdc().setKeepLineNum();
		getSdc().setPrintTags();	
	}
	
	private String getCmd(String user_cmd) {
		
		StringBuffer classpath = new StringBuffer(LaunchCommands.SOOT_CLASSPATH);
		classpath.append(getSootClasspath().getSootClasspath());
		classpath.append(getSootClasspath().getSeparator());
		classpath.append(getClasspathAppend());
		
		
		String output_path = LaunchCommands.OUTPUT_DIR+getOutputLocation();
				
		StringBuffer cmd = new StringBuffer();
		cmd.append(classpath+" ");
		cmd.append(output_path+" ");
		cmd.append(getToProcess()+" ");
		if (isExtraCmd()) {
			cmd.append(getExtraCmd()+" ");
		}
		cmd.append(user_cmd);
		
	  	return cmd.toString();
	  	
		
	}

}
