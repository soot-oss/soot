package ca.mcgill.sable.soot.launching;

import org.eclipse.jface.action.*;
import org.eclipse.core.resources.*;
import org.eclipse.jdt.core.*;

/**
 * @author jlhotak
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SootFileLauncher extends SootLauncher {

	private String classpathAppend;
	private String toProcess;
	//private String OutputLocation;
	private String extraCmd;
	private boolean isExtraCmd;
	private String srcPrec;
	private boolean isSrcPrec;
	
	public void run(IAction action){
		super.run(action);
		
		//super.resetSootOutputFolder();
		//setOutputLocation(platform_location+getSootOutputFolder().getFullPath().toOSString());
		
		if (getSootSelection().getType() == SootSelection.CLASSFILE_SELECTED_TYPE) {
			IClassFile cf = getSootSelection().getClassFile();
			//handleClassFile(cf);
			IPackageFragmentRoot pfr = (IPackageFragmentRoot) cf.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
			IPackageFragment pf = (IPackageFragment) cf.getAncestor(IJavaElement.PACKAGE_FRAGMENT);
			setClasspathAppend(platform_location+pfr.getPath().toOSString());
			if (pf.isDefaultPackage()) {
				setToProcess(removeFileExt(cf.getElementName()));
			}
			else {
				setToProcess(pf.getElementName()+"."+removeFileExt(cf.getElementName()));
			}
		}
		else if (getSootSelection().getType() == SootSelection.FILE_SELECTED_TYPE) {
			IFile file = getSootSelection().getFile();
			if (file.getFileExtension().compareTo("jimple") == 0) {
				setClasspathAppend(platform_location+file.getParent().getFullPath().toOSString());	
				setIsSrcPrec(true);
				setSrcPrec(LaunchCommands.JIMPLE_IN);
				setToProcess(removeFileExt(file.getName()));
			}
			else if (file.getFileExtension().compareTo("class") == 0) {
				try {
					// broken
					IJavaElement elem = JavaCore.create(file);
					if (elem instanceof IClassFile) {
						handleClassFile((IClassFile)elem);
					}
					
				}
				catch(Exception e){
					System.out.println("not a class file");
				}
			}
			
		}
	}
	
	private void handleClassFile(IClassFile cf) {
		IPackageFragmentRoot pfr = (IPackageFragmentRoot) cf.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
		System.out.println("pfr path: "+pfr.getPath().toOSString());
		IPackageFragment pf = (IPackageFragment) cf.getAncestor(IJavaElement.PACKAGE_FRAGMENT);
		System.out.println("pf path: "+pf.getPath().toOSString());
		// incorrect pfr - pf workaround - temporary
		if (pfr.getPath().toOSString().equals(pf.getPath().toOSString())) {
			setClasspathAppend(platform_location+pfr.getPath().removeLastSegments(1).toOSString());
		}
		else {
			//System.out.println("pfr path: "+pfr.getPath().toOSString());
		
			setClasspathAppend(platform_location+pfr.getPath().toOSString());
		}
		
		// first condition incorrect pfr - pf workaround - temporary
		/*if (pfr.getPath().toOSString().equals(pf.getPath().toOSString())) {
			setToProcess(pf.getElementName()+"."+removeFileExt(cf.getElementName()));
		}*/
		if (pf.isDefaultPackage()) {
			setToProcess(removeFileExt(cf.getElementName()));
		}
		else {
			setToProcess(pf.getElementName()+"."+removeFileExt(cf.getElementName()));
		}
	}
	
	private String removeFileExt(String filename) {
		int dot = filename.lastIndexOf('.');
		filename = filename.substring(0, dot);
		return filename;
	}
	
	/**
	 * Returns the classpathAppend.
	 * @return String
	 */
	public String getClasspathAppend() {
		return classpathAppend;
	}

	/**
	 * Returns the toProcess.
	 * @return String
	 */
	public String getToProcess() {
		return toProcess;
	}

	/**
	 * Sets the classpathAppend.
	 * @param classpathAppend The classpathAppend to set
	 */
	public void setClasspathAppend(String classpathAppend) {
		this.classpathAppend = classpathAppend;
	}

	/**
	 * Sets the toProcess.
	 * @param toProcess The toProcess to set
	 */
	public void setToProcess(String toProcess) {
		this.toProcess = toProcess;
	}

	/**
	 * Returns the outputLocation.
	 * @return String
	 */
	/*public String getOutputLocation() {
		return OutputLocation;
	}

	/**
	 * Sets the outputLocation.
	 * @param outputLocation The outputLocation to set
	 */
	/*public void setOutputLocation(String outputLocation) {
		OutputLocation = outputLocation;
	}

	/**
	 * Returns the extraCmd.
	 * @return String
	 */
	public String getExtraCmd() {
		return extraCmd;
	}

	/**
	 * Sets the extraCmd.
	 * @param extraCmd The extraCmd to set
	 */
	public void setExtraCmd(String extraCmd) {
		this.extraCmd = extraCmd;
	}

	/**
	 * Returns the isExtraCmd.
	 * @return boolean
	 */
	public boolean isExtraCmd() {
		return isExtraCmd;
	}

	/**
	 * Sets the isExtraCmd.
	 * @param isExtraCmd The isExtraCmd to set
	 */
	public void setIsExtraCmd(boolean isExtraCmd) {
		this.isExtraCmd = isExtraCmd;
	}

	/**
	 * Returns the isSrcPrec.
	 * @return boolean
	 */
	public boolean isSrcPrec() {
		return isSrcPrec;
	}

	/**
	 * Returns the srcPrec.
	 * @return String
	 */
	public String getSrcPrec() {
		return srcPrec;
	}

	/**
	 * Sets the isSrcPrec.
	 * @param isSrcPrec The isSrcPrec to set
	 */
	public void setIsSrcPrec(boolean isSrcPrec) {
		this.isSrcPrec = isSrcPrec;
	}

	/**
	 * Sets the srcPrec.
	 * @param srcPrec The srcPrec to set
	 */
	public void setSrcPrec(String srcPrec) {
		this.srcPrec = srcPrec;
	}

}
