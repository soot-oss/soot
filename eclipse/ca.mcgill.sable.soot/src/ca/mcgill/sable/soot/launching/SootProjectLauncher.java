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
public class SootProjectLauncher extends SootLauncher {

	//private String output_location;
	private String process_path;
	
	public void run(IAction action) {
		super.run(action);
		//super.resetSootOutputFolder();
		try {
			setProcess_path(platform_location+getSootSelection().getJavaProject().getOutputLocation().toOSString());
		}
		catch(Exception e1) {
			System.out.println(e1.getMessage());
		}
		//setOutput_location(platform_location+getSootOutputFolder().getFullPath().toOSString());
	}

	/**
	 * Returns the output_location.
	 * @return String
	 */
	/*public String getOutput_location() {
		return output_location;
	}

	/**
	 * Returns the process_path.
	 * @return String
	 */
	public String getProcess_path() {
		return process_path;
	}

	/**
	 * Sets the output_location.
	 * @param output_location The output_location to set
	 */
	/*public void setOutput_location(String output_location) {
		this.output_location = output_location;
	}

	/**
	 * Sets the process_path.
	 * @param process_path The process_path to set
	 */
	public void setProcess_path(String process_path) {
		this.process_path = process_path;
	}

}
