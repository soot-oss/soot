package ca.mcgill.sable.soot.launching;

import java.util.ArrayList;

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

		
		setCmd();
		runSootDirectly();
		runFinish();
	
	}
	
	private void setCmd() {
		
		ArrayList commands = new ArrayList();
		commands.add("--"+LaunchCommands.SOOT_CLASSPATH);
		commands.add(getSootClasspath().getSootClasspath()+getSootClasspath().getSeparator()+getClasspathAppend());
	  	
		//getSootCommandList().addDoubleOpt("--"+LaunchCommands.SOOT_CLASSPATH, getSootClasspath().getSootClasspath()+getSootClasspath().getSeparator()+getClasspathAppend());
		commands.add("--"+LaunchCommands.OUTPUT_DIR);
		commands.add(getOutputLocation());
		//getSootCommandList().addDoubleOpt("--"+LaunchCommands.OUTPUT_DIR, getOutputLocation());
		getSootCommandList().addSingleOpt("--"+LaunchCommands.KEEP_LINE_NUMBER);
		getSootCommandList().addSingleOpt("--"+LaunchCommands.XML_ATTRIBUTES);
		getSootCommandList().addDoubleOpt("--"+LaunchCommands.OUTPUT, LaunchCommands.JIMPLE_OUT);
	

		if (isExtraCmd()) {
			getSootCommandList().addSingleOpt("--"+getExtraCmd());
		}
		//getSootCommandList().addDoubleOpt("--"+LaunchCommands.OUTPUT, LaunchCommands.JIMPLE_OUT);
		commands.add(getToProcess());
		//getSootCommandList().addSingleOpt(getToProcess());
		getSootCommandList().addSingleOpt(commands);
	  	//return cmd.toString();
	}
}
