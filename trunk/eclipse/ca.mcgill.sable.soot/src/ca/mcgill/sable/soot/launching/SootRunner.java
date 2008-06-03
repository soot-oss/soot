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

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import ca.mcgill.sable.soot.interaction.*;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import ca.mcgill.sable.soot.util.*;
import java.util.*;
import org.eclipse.jface.dialogs.*;

/**
 * Runs Soot and creates Handler for Soot output.
 */
public class SootRunner implements IRunnableWithProgress {

	Display display;
	String [] cmd;
	String mainClass;
	ArrayList cfgList;
	private SootLauncher parent;
	
	/**
	 * Constructor for SootRunner.
	 */
	public SootRunner(String [] cmd, Display display , String mainClass) {
		setDisplay(display);
		setCmd(cmd);
		setMainClass(mainClass);
		
	}

	/**
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor)
		throws InvocationTargetException, InterruptedException {
		try {
		
			final PipedInputStream pis = new PipedInputStream();
			
      		final PipedOutputStream pos = new PipedOutputStream(pis);
      		final PrintStream sootOut = new PrintStream(pos);
      		
            final String [] cmdFinal = getCmd();
            
            SootThread sootThread = new SootThread(getDisplay(), getMainClass(), this);
            
            sootThread.setCmd(cmdFinal);
            sootThread.setSootOut(sootOut);
            sootThread.start();
             
        	StreamGobbler out = new StreamGobbler(getDisplay(), pis, StreamGobbler.OUTPUT_STREAM_TYPE);
        	out.start();
      	
        	sootThread.join();
        	getParent().setCfgList(getCfgList());
      	}
      	catch (Exception e) {
      		System.out.println(e.getStackTrace());
      	}
	}
	

	/**
	 * Returns the cmd.
	 * @return String[]
	 */
	public String[] getCmd() {
		return cmd;
	}

	/**
	 * Returns the display.
	 * @return Display
	 */
	public Display getDisplay() {
		return display;
	}

	/**
	 * Sets the cmd.
	 * @param cmd The cmd to set
	 */
	public void setCmd(String[] cmd) {
		this.cmd = cmd;
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
	public SootLauncher getParent() {
		return parent;
	}

	/**
	 * @param launcher
	 */
	public void setParent(SootLauncher launcher) {
		parent = launcher;
	}

}
