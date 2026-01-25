/* Soot - a J*va Optimization Framework
 * Copyright (C) 2008 Eric Bodden
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
package ca.mcgill.sable.soot.examples;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.wizards.JavaProjectWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import ca.mcgill.sable.soot.SootClasspathVariableInitializer;
import ca.mcgill.sable.soot.SootPlugin;

public abstract class NewSootExampleWizard extends JavaProjectWizard {

	protected final String fromFile;
	protected final String toFile;

	protected NewSootExampleWizard(String fromFile, String toFile) {
		this.fromFile = fromFile;
		this.toFile = toFile;
	}
	
	@Override
	public boolean performFinish() {
		boolean performFinish = super.performFinish();
		
		if(performFinish) {
			IJavaProject newProject = (IJavaProject) getCreatedElement();
			try {
				IClasspathEntry[] originalCP = newProject.getRawClasspath();
				IClasspathEntry ajrtLIB = JavaCore.newVariableEntry(
						new Path(SootClasspathVariableInitializer.VARIABLE_NAME_CLASSES),
						new Path(SootClasspathVariableInitializer.VARIABLE_NAME_SOURCE),
						null);
				// Update the raw classpath with the new entry
				int originalCPLength = originalCP.length;
				IClasspathEntry[] newCP = new IClasspathEntry[originalCPLength + 1];
				System.arraycopy(originalCP, 0, newCP, 0, originalCPLength);
				newCP[originalCPLength] = ajrtLIB;
				newProject.setRawClasspath(newCP, new NullProgressMonitor());
			} catch (JavaModelException e) {
			}
			
			String templateFilePath = fromFile;
			InputStream is = null;
			FileOutputStream fos = null;
			try {
				is = SootPlugin.getDefault().getBundle().getResource(templateFilePath).openStream();
				if(is==null) {
					new RuntimeException("Resource "+templateFilePath+" not found!").printStackTrace();
				} else {
				
					IClasspathEntry[] resolvedClasspath = newProject.getResolvedClasspath(true);
					IClasspathEntry firstSourceEntry = null;
					for (IClasspathEntry classpathEntry : resolvedClasspath) {
						if(classpathEntry.getEntryKind()==IClasspathEntry.CPE_SOURCE) {
							firstSourceEntry = classpathEntry;
							break;
						}
					}
					if(firstSourceEntry!=null) {
						IPath path = SootPlugin.getWorkspace().getRoot().getFile(firstSourceEntry.getPath()).getLocation();
						String srcPath = path.toString(); 
						String newfileName = toFile;
						final IPath newFilePath = firstSourceEntry.getPath().append(newfileName);
						fos = new FileOutputStream(srcPath + File.separator + newfileName);
						int temp = is.read();
						while(temp>-1) {
							fos.write(temp);
							temp = is.read();
						}
						fos.close();
						//refresh project
						newProject.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
					
						final IWorkbenchPage activePage= JavaPlugin.getActivePage();
						if (activePage != null) {
							final Display display= getShell().getDisplay();
							if (display != null) {
								display.asyncExec(new Runnable() {
									public void run() {
										try {
											IResource newResource = SootPlugin.getWorkspace().getRoot().findMember(newFilePath);
											IDE.openEditor(activePage, (IFile) newResource, true);
										} catch (PartInitException e) {
											JavaPlugin.log(e);
										}
									}
								});
							}
						}
	
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(is!=null) is.close();
					if(fos!=null) fos.close();
				} catch (IOException e) {
				}
			}
		}
		
		return performFinish;
	}
	
	@Override
	public void addPages() {
		addPage(new FirstPage());
		super.addPages();
	}	

	protected static class FirstPage extends WizardPage {

		private FirstPage() {
			super("ca.mcgill.sable.soot.examples.NewExamplePage");
		}
		
		public void createControl(Composite parent) {
			final Composite composite= new Composite(parent, SWT.NULL);
			composite.setFont(parent.getFont());
			composite.setLayout(new FillLayout());
			setControl(composite);
			
			Label label = new Label(composite, SWT.WRAP);
			setControl(composite);
			label.setText("Please create a new Java project using the following Wizard " +
					"pages. Soot will then create the example source files in the project's source folder.");
		
			setTitle("Create example Soot extension");
			setMessage("This wizard will help you create a new example Soot extension.");
		}
		
	}
}
