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
public class SootJimpleFileLauncher extends SootFileLauncher {

	public void run(IAction action) {
	super.run(action);

		//String cmd = getCmd();
		setCmd();
		runSootDirectly();
		runFinish();
	
	}
	
	private void setCmd() {
		
			
		//StringBuffer classpath = new StringBuffer(LaunchCommands.SOOT_CLASSPATH);
		//classpath.append(getSootClasspath().getSootClasspath());
		//classpath.append(getSootClasspath().getSeparator());
		getSootCommandList().addDoubleOpt(LaunchCommands.SOOT_CLASSPATH, getSootClasspath().getSootClasspath()+getSootClasspath().getSeparator()+getClasspathAppend());
		//classpath.append(getClasspathAppend());
		
		
		//String output_path = LaunchCommands.OUTPUT_DIR+getOutputLocation();
				
		//StringBuffer cmd = new StringBuffer();
		//cmd.append(classpath+" ");
		//cmd.append(output_path+" ");
		
		if (isExtraCmd()) {
			getSootCommandList().addSingleOpt(getExtraCmd());
		}
		getSootCommandList().addDoubleOpt(LaunchCommands.OUTPUT, LaunchCommands.JIMPLE_OUT);
		getSootCommandList().addSingleOpt(getToProcess());
	  	//return cmd.toString();
	}
}
