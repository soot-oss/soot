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
import java.util.*;
import java.lang.reflect.*;

import org.eclipse.swt.widgets.Display;
import soot.*;
import soot.toolkits.graph.interaction.*;
import ca.mcgill.sable.soot.interaction.*;


public class SootThread extends Thread {

	private Display display;
	private String mainClass;
	private ArrayList cfgList;
	private IInteractionListener listener;
	private SootRunner parent;
	
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
			
			soot.G.v().reset();
			soot.G.v().out = sootOutFinal;
           
            InteractionHandler.v().setInteractionListener(getListener());
            
			Class toRun = Class.forName(getMainClass());
			Method [] meths = toRun.getDeclaredMethods();
			Object [] args = new Object [1];
			args[0] = cmdFinal;
			for (int i = 0; i < meths.length; i++){
				if (meths[i].getName().equals("main")){
					Class [] fields = meths[i].getParameterTypes();
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
	public IInteractionListener getListener() {
		return listener;
	}

	/**
	 * @param listener
	 */
	public void setListener(IInteractionListener listener) {
		this.listener = listener;
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
