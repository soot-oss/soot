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
public class DavaDecompileProjectLauncher extends SootProjectLauncher {

	
	public void run(IAction action) {
		
		super.run(action);

		setCmd();
		runSootDirectly();
		runFinish();
	}
	
	private void setCmd() {
		
		getSootCommandList().addDoubleOpt("--"+LaunchCommands.SOOT_CLASSPATH, getSootClasspath().getSootClasspath()+getSootClasspath().getSeparator()+getProcess_path());
			
				/*StringBuffer classpath = new StringBuffer(LaunchCommands.SOOT_CLASSPATH);
				classpath.append(getSootClasspath().getSootClasspath());
				classpath.append(getSootClasspath().getSeparator());
				classpath.append(getProcessPath());

		
				String output_path = LaunchCommands.OUTPUT_DIR + getOutputLocation();
				
				StringBuffer cmd = new StringBuffer();
				cmd.append(classpath+" ");
				cmd.append(output_path+" ");*/
				getSootCommandList().addDoubleOpt("--"+LaunchCommands.OUTPUT_DIR, getOutputLocation());
				getSootCommandList().addSingleOpt("--"+LaunchCommands.KEEP_LINE_NUMBER);
				getSootCommandList().addSingleOpt("--"+LaunchCommands.XML_ATTRIBUTES);
		
				getSootCommandList().addDoubleOpt("--"+LaunchCommands.PROCESS_PATH, getProcess_path());
				getSootCommandList().addSingleOpt("--"+LaunchCommands.DAVA);
			
		/*StringBuffer classpath = new StringBuffer(LaunchCommands.SOOT_CLASSPATH);
		classpath.append(getSootClasspath().getSootClasspath());
		classpath.append(getSootClasspath().getSeparator());
		classpath.append(getProcess_path());
	
		
		String output_path = LaunchCommands.OUTPUT_DIR+getOutputLocation();
				
		StringBuffer cmd = new StringBuffer();
		cmd.append(classpath+" ");
		cmd.append(output_path+" ");
		cmd.append(LaunchCommands.PROCESS_PATH+getProcess_path()+" ");
		cmd.append(LaunchCommands.DAVA);
		
	  	return cmd.toString();*/
	}

}
