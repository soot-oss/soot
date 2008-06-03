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


import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import soot.toolkits.graph.interaction.InteractionHandler;
import ca.mcgill.sable.soot.SootPlugin;
import ca.mcgill.sable.soot.interaction.InteractionController;


public class SootThread extends Thread {

	private Display display;
	private String mainClass;
	private ArrayList cfgList;
	private InteractionController listener;
	private SootRunner parent;
	private Shell activeShell;

	
	/**
	 * Constructor for SootThread.
	 */
	public SootThread(Display display, String mainClass, SootRunner parent) {
		super();
        setDisplay(display);
        
		setMainClass(mainClass);
		
		InteractionController controller = new InteractionController();
       	controller.setDisplay(getDisplay());
        controller.setSootThread(this);
        setListener(controller);
        setParent(parent);
        this.setName("soot thread");
        	
	}

	
	
	private String [] cmd;
	private PrintStream sootOut;

	
	public void run() {
		final String [] cmdFinal = getCmd();
		final PrintStream sootOutFinal = getSootOut();
		try {
			
			soot.G.reset();
			soot.G.v().out = sootOutFinal;
           
			InteractionController listener = getListener();
			InteractionHandler.v().setInteractionListener(listener);
            
            String mainClass = getMainClass();
            String mainProject = null;
            if(mainClass.contains(":")) {
            	String[] split = mainClass.split(":");
				mainProject = split[0];
            	mainClass = split[1];
            }
            Class<?> toRun;
			try {
				ClassLoader loader;
	            if(mainProject!=null) {
		            IProject project = SootPlugin.getWorkspace().getRoot().getProject(mainProject);
		            if(project.exists() && project.isOpen() && project.hasNature("org.eclipse.jdt.core.javanature")) {
		            	IJavaProject javaProject = JavaCore.create(project);
						URL[] urls = SootClasspath.projectClassPath(javaProject);
						loader = new URLClassLoader(urls,SootThread.class.getClassLoader());
		            } else {
		    			final String mc = mainClass;
		    			final Shell defaultShell = getShell();
		    			getDisplay().syncExec(new Runnable() {
		    				public void run() {
				    			MessageDialog.openError(defaultShell, "Unable to find Soot Main Project", "Project "+mc+" does not exist," +
								" is no Java project or is closed. Aborting...");
		    				}
		    			});
		    			SootPlugin.getDefault().getConsole().clearConsole();
		    			return;
		            }
	            } else {
	            	loader = SootThread.class.getClassLoader();
	            }

	            toRun = loader.loadClass(mainClass);
			} catch(final ClassNotFoundException e) {
				final Shell defaultShell = getShell();
    			final String inProject = mainProject!=null ? (" in project "+mainProject):"";
    			getDisplay().syncExec(new Runnable() {
    				public void run() {
    	    			MessageDialog.openError(defaultShell, "Unable to find class", "Cannot find class"+inProject+". Aborting...\n"+e.getLocalizedMessage());
    				}
    			});
    			SootPlugin.getDefault().getConsole().clearConsole();
				return;
			}
			            
			Method [] meths = toRun.getDeclaredMethods();
			Object [] args = new Object [1];
			args[0] = cmdFinal;
			for (int i = 0; i < meths.length; i++){
				if (meths[i].getName().equals("main")){
					Class<?>[] fields = meths[i].getParameterTypes();
					if (fields.length == 1){
					meths[i].invoke(toRun, args);
					}
				}
			}
			setCfgList(soot.Scene.v().getPkgList());
			getParent().setCfgList(getCfgList());
			
		}
		catch (Exception e) {
            System.out.println("Soot exception: "+e);
			e.printStackTrace(sootOutFinal);
			System.out.println(e.getCause());
       	}
	}

	private Shell getShell() {
		getDisplay().syncExec(new Runnable() {

			public void run() {
				activeShell = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
			}
		});		
		return activeShell;
	}

	/**
	 * Returns the cmd.
	 * @return String
	 */
	public String [] getCmd() {
		return cmd;
	}

	/**
	 * Returns the sootOut.
	 * @return PrintStream
	 */
	public PrintStream getSootOut() {
		return sootOut;
	}

	/**
	 * Sets the cmd.
	 * @param cmd The cmd to set
	 */
	public void setCmd(String [] cmd) {
		this.cmd = cmd;
	}

	/**
	 * Sets the sootOut.
	 * @param sootOut The sootOut to set
	 */
	public void setSootOut(PrintStream sootOut) {
		this.sootOut = sootOut;
	}

	/**
	 * Returns the display.
	 * @return Display
	 */
	public Display getDisplay() {
		return display;
	}

	/**
	 * Sets the display.
	 * @param display The display to set
	 */
	public void setDisplay(Display display) {
		this.display = display;
	}

	/**
	 * @return
	 */
	public String getMainClass() {
		return mainClass;
	}

	/**
	 * @param string
	 */
	public void setMainClass(String string) {
		mainClass = string;
	}

	/**
	 * @return
	 */
	public ArrayList getCfgList() {
		return cfgList;
	}

	/**
	 * @param list
	 */
	public void setCfgList(ArrayList list) {
		cfgList = list;
	}

	/**
	 * @return
	 */
	public InteractionController getListener() {
		return listener;
	}

	/**
	 * @param controller
	 */
	public void setListener(InteractionController controller) {
		this.listener = controller;
	}

	/**
	 * @return
	 */
	public SootRunner getParent() {
		return parent;
	}

	/**
	 * @param runner
	 */
	public void setParent(SootRunner runner) {
		parent = runner;
	}

}
