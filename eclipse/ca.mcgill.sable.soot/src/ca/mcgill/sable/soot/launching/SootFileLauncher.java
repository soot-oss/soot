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

import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.*;

import soot.coffi.*;


import java.io.*;

import ca.mcgill.sable.soot.SootPlugin;

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
	private boolean doNotContinue = false;
	
	public void run(IAction action){
		super.run(action);
		
		//super.resetSootOutputFolder();
		//setOutputLocation(platform_location+getSootOutputFolder().getFullPath().toOSString());
		setDoNotContinue(false);
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
					handleClassFile(file);
					
				}
				catch(Exception e){
					System.out.println("not a class file");
				}
			}
						
		}
		else if (getSootSelection().getType() == SootSelection.CU_SELECTED_TYPE) {
			ICompilationUnit cu = getSootSelection().getJavaFile();
			IPackageFragmentRoot pfr = (IPackageFragmentRoot) cu.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
			IPackageFragment pf = (IPackageFragment) cu.getAncestor(IJavaElement.PACKAGE_FRAGMENT);
			try {
				IProject proj = cu.getJavaProject().getProject();
				
				IFolder output = proj.getFolder(cu.getJavaProject().getOutputLocation().lastSegment());
				//System.out.println("Project Output Folder Location: "+output.getLocation().toOSString());
				IPackageFragment pkf = (IPackageFragment)cu.getAncestor(IJavaElement.PACKAGE_FRAGMENT);
				IFile exists = null;
				if (pkf.isDefaultPackage()) {
					//if (cu.getPackageDeclarations())
					exists = output.getFile(removeFileExt(cu.getElementName())+".class");
					//System.out.println("output: "+output);
					//System.out.println(removeFileExt(cu.getElementName())+".class");
					//IFile exists = output.getFile(output.getLocation().toOSString()+removeFileExt(cu.getElementName())+".class");
					System.out.println("No Pck: "+exists.getLocation().toOSString());
				}
				else {
					IFolder pkg = output.getFolder(dotsToSlashes(pf.getElementName()));
					System.out.println("pkg folder: "+pkg.getLocation().toOSString());
                    System.out.println("pf path: "+pf.getPath().toOSString());
                    if (pkg.exists()){
                        System.out.println("pkg exists");
                    }
                    exists = pkg.getFile(removeFileExt(cu.getElementName())+".class");
					System.out.println("Pck: "+exists.getLocation().toOSString());
				}
				if (!exists.exists()){
					//System.out.println("underlying class file cannot be found.");
					window = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
					MessageDialog noClassFound = new MessageDialog(window.getShell(), "Soot Information", null, "No underlying class file was found, maybe build project.", 0, new String [] {"OK"}, 0);
					noClassFound.open();
					setDoNotContinue(true);	
				}
			setClasspathAppend(platform_location+cu.getJavaProject().getOutputLocation().toOSString());
			}
			catch (CoreException e){
			}
			if (pf.isDefaultPackage()) {
				setToProcess(removeFileExt(cu.getElementName()));
			}
			else {
				setToProcess(pf.getElementName()+"."+removeFileExt(cu.getElementName()));
			}
		}
	}

    private String dotsToSlashes(String name) {
        name = name.replaceAll("\\.", System.getProperty("file.separator"));
        return name;
    }
    
	private void handleClassFile(IFile file) {
		ClassFile cf = new ClassFile( file.getLocation().toOSString());
		FileInputStream fis = null;
		try {
			fis = new FileInputStream( file.getLocation().toOSString() );
		} 
		catch( FileNotFoundException e ) {
		//throw new RuntimeException( "couldn't find file "+file.getLocation().toOSString() );
		
		}
		if (!cf.loadClassFile( fis )){
		
			MessageDialog noClassFound = new MessageDialog(window.getShell(), "Soot Information", null, "Could not determine package for class file will not continue.", 0, new String [] {"OK"}, 0);
			noClassFound.open();
			setDoNotContinue(true);	
		}
		
		//System.out.println( cf.getClass().toString()+" "+cf.toString() );
		
		setToProcess(replaceWithDot(cf.toString()));
		
		//System.out.println("to process: "+getToProcess());
		//		System.out.println("classpath append: "+getClasspathAppend());
		//setToProcess(cf.toString().substring(cf.toString().lastIndexOf(".")));
		setClasspathAppend(file.getLocation().toOSString().substring(0, file.getLocation().toOSString().indexOf(cf.toString())));
		//System.out.println("to process: "+getToProcess());
		//System.out.println("classpath append: "+getClasspathAppend());
	}
	
	private String replaceWithDot(String in){
		String separator = System.getProperty("file.separator");
		//System.out.println(separator);
		in = in.replaceAll(separator, ".");
		//System.out.println(in);
		return in;
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

	/**
	 * @return
	 */
	public boolean isDoNotContinue() {
		return doNotContinue;
	}

	/**
	 * @param b
	 */
	public void setDoNotContinue(boolean b) {
		doNotContinue = b;
	}

}
