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
public class DavaDecompileFolderLauncher extends SootFolderLauncher {
	
	/**
	 * @see org.eclipse.ui.IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
	  	super.run(action);
	  	
		//setCmd();
/*		SootSavedConfiguration ssc = new SootSavedConfiguration("Temp", getSootCommandList());
      		ssc.toSaveString();
      		
      		
      		//HashMap temp = dialog.getOkMap();
      		//System.out.println("ok map: "+temp.get("test"));
      		//TestOptionsDialogHandler handler = new TestOptionsDialogHandler();
      		setCmd(ssc.toRunString());*/
		setCmd();
		//getSootCommandList().addDashes();
		runSootDirectly();
		runFinish();
	
	}
	
	/**
	 * Method getCmd.
	 * @return String
	 */
	private void setCmd() {
		ArrayList commands = new ArrayList();
		commands.add("--"+LaunchCommands.SOOT_CLASSPATH);
		commands.add(getSootClasspath().getSootClasspath()+getSootClasspath().getSeparator()+getProcessPath());
		//getSootCommandList().addDoubleOpt("--"+LaunchCommands.SOOT_CLASSPATH, getSootClasspath().getSootClasspath()+getSootClasspath().getSeparator()+getProcessPath());
			
		/*StringBuffer classpath = new StringBuffer(LaunchCommands.SOOT_CLASSPATH);
		classpath.append(getSootClasspath().getSootClasspath());
		classpath.append(getSootClasspath().getSeparator());
		classpath.append(getProcessPath());

		
		String output_path = LaunchCommands.OUTPUT_DIR + getOutputLocation();
				
		StringBuffer cmd = new StringBuffer();
		cmd.append(classpath+" ");
		cmd.append(output_path+" ");*/
		commands.add("--"+LaunchCommands.OUTPUT_DIR);
		commands.add(getOutputLocation());
		//getSootCommandList().addDoubleOpt("--"+LaunchCommands.OUTPUT_DIR, getOutputLocation());
		getSootCommandList().addSingleOpt("--"+LaunchCommands.KEEP_LINE_NUMBER);
		getSootCommandList().addSingleOpt("--"+LaunchCommands.XML_ATTRIBUTES);
		
		commands.add("--"+LaunchCommands.PROCESS_PATH);
		commands.add(getProcessPath());
		//getSootCommandList().addDoubleOpt("--"+LaunchCommands.PROCESS_PATH, getProcessPath());
		getSootCommandList().addSingleOpt("--"+LaunchCommands.DAVA);
		
		getSootCommandList().addSingleOpt(commands);
	  	//return cmd.toString();
	}

}	
