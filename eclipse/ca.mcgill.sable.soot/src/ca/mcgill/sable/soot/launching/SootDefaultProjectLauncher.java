package ca.mcgill.sable.soot.launching;

import org.eclipse.jface.action.*;

/**
 * @author jlhotak
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SootDefaultProjectLauncher extends SootProjectLauncher {

	
	public void run(IAction action) {
		
		super.run(action);

		String cmd = getCmd();
		runSootAsProcess(cmd);
		runFinish();
	}
	
	private String getCmd() {
		
			
		StringBuffer classpath = new StringBuffer(LaunchCommands.SOOT_CLASSPATH);
		classpath.append(getSootClasspath().getSootClasspath());
		classpath.append(getSootClasspath().getSeparator());
		classpath.append(getProcess_path());
		
		
		String output_path = LaunchCommands.OUTPUT_DIR+getOutputLocation();
				
		StringBuffer cmd = new StringBuffer();
		cmd.append(classpath+" ");
		cmd.append(output_path+" ");
		cmd.append(LaunchCommands.PROCESS_PATH+getProcess_path()+" ");
		cmd.append(LaunchCommands.INTRA_PROC);
		
	  	return cmd.toString();
	}
}
