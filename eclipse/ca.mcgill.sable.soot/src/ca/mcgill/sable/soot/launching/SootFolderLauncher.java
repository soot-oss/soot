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
public class SootFolderLauncher extends SootLauncher {

	private String processPath;
	//private String outputLocation;

	public void run(IAction action) {
		super.run(action);
		
		//super.resetSootOutputFolder();
		//setOutputLocation(platform_location+getSootOutputFolder().getFullPath().toOSString());
		
		if (getSootSelection().getType() == SootSelection.PACKAGEROOT_SELECTED_TYPE){
			setProcessPath(platform_location+getSootSelection().getPackageFragmentRoot().getPath().toOSString());
		}
	}

	/**
	 * Returns the outputLocation.
	 * @return String
	 */
	/*public String getOutputLocation() {
		return outputLocation;
	}

	/**
	 * Returns the processPath.
	 * @return String
	 */
	public String getProcessPath() {
		return processPath;
	}

	/**
	 * Sets the outputLocation.
	 * @param outputLocation The outputLocation to set
	 */
	/*public void setOutputLocation(String outputLocation) {
		this.outputLocation = outputLocation;
	}

	/**
	 * Sets the processPath.
	 * @param processPath The processPath to set
	 */
	public void setProcessPath(String processPath) {
		this.processPath = processPath;
	}

}
