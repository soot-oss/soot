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
import java.util.*;

import ca.mcgill.sable.soot.SootPlugin;

/**
 * Soot Launcher for files. Determines file type, project
 * classpath, src-precedence
 */
public class SootFileLauncher extends SootLauncher {

	private String classpathAppend;
	//private String toProcess;
	private ArrayList toProcessList;
	//private String OutputLocation;
	private String extraCmd;
	private boolean isExtraCmd;
	private String srcPrec;
	private boolean isSrcPrec;
	private boolean doNotContinue = false;
	
    public void handleMultipleFiles() {
        //super.run(action);
        //setDoNotContinue(false);
        setToProcessList(new ArrayList());
        Iterator it = getSootSelection().getFileList().iterator();
        while (it.hasNext()){
        	handleFiles(it.next());
            // must set multiple toProcess but also classpaths and 
            // ouptut directories
        }
    }
    
	public void run(IAction action){
		super.run(action);
		classpathAppend = null;
		//handleFiles();
    }
    
    public void handleFiles(Object toProcess){
        
		setDoNotContinue(false);
		if (toProcess instanceof IClassFile){
		//if (getSootSelection().getType() == SootSelection.CLASSFILE_SELECTED_TYPE) {
			IClassFile cf = (IClassFile)toProcess;
			//IClassFile cf = getSootSelection().getClassFile();
			//handleClassFile(cf);
			IPackageFragmentRoot pfr = (IPackageFragmentRoot) cf.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
			IPackageFragment pf = (IPackageFragment) cf.getAncestor(IJavaElement.PACKAGE_FRAGMENT);
			if (pfr.getResource() != null){
				setClasspathAppend(platform_location+pfr.getPath().toOSString());
			}
			else {
				setClasspathAppend(pfr.getPath().toOSString());
			}
			addJars();
			if (pf.isDefaultPackage()) {
				//setToProcess(removeFileExt(cf.getElementName()));
				getToProcessList().add(removeFileExt(cf.getElementName()));
			}
			else {
				//setToProcess(pf.getElementName()+"."+removeFileExt(cf.getElementName()));
				getToProcessList().add(pf.getElementName()+"."+removeFileExt(cf.getElementName()));
			}
		}
		else if (toProcess instanceof IFile){
		//else if (getSootSelection().getType() == SootSelection.FILE_SELECTED_TYPE) {
			IFile file = (IFile)toProcess;
			//IFile file = getSootSelection().getFile();
			if (file.getFileExtension().compareTo("jimple") == 0) {
				setClasspathAppend(platform_location+file.getParent().getFullPath().toOSString());	
				addJars();
				setIsSrcPrec(true);
				setSrcPrec(LaunchCommands.JIMPLE_IN);
				//setToProcess(removeFileExt(file.getName()));
				getToProcessList().add(removeFileExt(file.getName()));
			}
            else if (file.getFileExtension().equals("java")){
                try {
                
                	handleSourceFile(JavaCore.createCompilationUnitFrom(file));
                }
                catch(Exception e){
                	System.out.println("problem creating CompilationUnit");
                }
                
            }
            
			else if (file.getFileExtension().equals("class")) {
				try {
					handleClassFile(file);
					
				}
				catch(Exception e){
					System.out.println("not a class file");
				}
			}
						
		}
		else if (toProcess instanceof ICompilationUnit){
		//else if (getSootSelection().getType() == SootSelection.CU_SELECTED_TYPE) {
			ICompilationUnit cu = (ICompilationUnit)toProcess;
			//ICompilationUnit cu = getSootSelection().getJavaFile();
            handleSourceFile(cu);
			
		}
	}

    private String dotsToSlashes(String name) {
        name = name.replaceAll("\\.", System.getProperty("file.separator"));
        return name;
    }
   
    private void handleSourceFile(ICompilationUnit cu){
		IPackageFragmentRoot pfr = (IPackageFragmentRoot) cu.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
		IPackageFragment pf = (IPackageFragment) cu.getAncestor(IJavaElement.PACKAGE_FRAGMENT);
        addJars();
        if (isSrcPrec() && getSrcPrec().equals("java")){
            setClasspathAppend(platform_location+pfr.getPath().toOSString());    
        }
        else{
        
			try {
				IProject proj = cu.getJavaProject().getProject();
				
				IFolder output = proj.getFolder(cu.getJavaProject().getOutputLocation().lastSegment());
				IPackageFragment pkf = (IPackageFragment)cu.getAncestor(IJavaElement.PACKAGE_FRAGMENT);
				IFile exists = null;
				if (pkf.isDefaultPackage()) {
					exists = output.getFile(removeFileExt(cu.getElementName())+".class");
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
        }
		if (pf.isDefaultPackage()) {
			//setToProcess(removeFileExt(cu.getElementName()));
			getToProcessList().add(removeFileExt(cu.getElementName()));
		}
		else {
			//setToProcess(pf.getElementName()+"."+removeFileExt(cu.getElementName()));
			getToProcessList().add(pf.getElementName()+"."+removeFileExt(cu.getElementName()));
		}
    
    }
    
	private void handleClassFile(IFile file) {
		ClassFile cf = new ClassFile( file.getLocation().toOSString());
		System.out.println("file: "+file.getLocation().toOSString());
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
		
		
		//setToProcess(replaceWithDot(cf.toString()));
		getToProcessList().add(replaceWithDot(cf.toString()));
		addJars();
		System.out.println("cf: "+cf.toString());
		setClasspathAppend(file.getLocation().toOSString().substring(0, file.getLocation().toOSString().indexOf(cf.toString())));
		
	}
	
	private String replaceWithDot(String in){
		String separator = System.getProperty("file.separator");
		in = in.replaceAll(separator, ".");
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
	/*public String getToProcess() {
		return toProcess;
	}*/

	/**
	 * Sets the classpathAppend.
	 * @param classpathAppend The classpathAppend to set
	 */
	public void setClasspathAppend(String ca) {
		System.out.println("Before adding claspath is: "+classpathAppend);
		System.out.println("ca is: "+ca);
		
		if ((this.classpathAppend == null) || (this.classpathAppend.equals(""))){
			System.out.println("classpathAppend found to be null");
			this.classpathAppend = ca;
		}
		else {
			if (this.classpathAppend.indexOf(ca) == -1){
				this.classpathAppend = this.classpathAppend+getSootClasspath().getSeparator()+ca;
			}
		}
		System.out.println("classpathAppend: "+this.classpathAppend);
	}

	/**
	 * Sets the toProcess.
	 * @param toProcess The toProcess to set
	 */
	/*public void setToProcess(String toProcess) {
		this.toProcess = toProcess;
	}*/

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

	/**
	 * @return
	 */
	public ArrayList getToProcessList() {
		return toProcessList;
	}

	/**
	 * @param list
	 */
	public void setToProcessList(ArrayList list) {
		toProcessList = list;
	}

}
