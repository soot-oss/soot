package ca.mcgill.sable.soot.launching;


import java.io.PrintStream;

import org.eclipse.swt.widgets.Display;

import ca.mcgill.sable.soot.SootPlugin;

import soot.*;
/**
 * @author jlhotak
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
public class SootThread extends Thread {

	private Display display;
	/**
	 * Constructor for SootThread.
	 */
	public SootThread(Display display) {
		super();
		setDisplay(display);
	}

	/**
	 * Constructor for SootThread.
	 * @param target
	 */
	/*public SootThread(Runnable target) {
		super(target);
	}

	/**
	 * Constructor for SootThread.
	 * @param group
	 * @param target
	 */
	/*(public SootThread(ThreadGroup group, Runnable target) {
		super(group, target);
	}

	/**
	 * Constructor for SootThread.
	 * @param name
	 */
	/*public SootThread(String name) {
		super(name);
	}

	/**
	 * Constructor for SootThread.
	 * @param group
	 * @param name
	 */
	/*public SootThread(ThreadGroup group, String name) {
		super(group, name);
	}

	/**
	 * Constructor for SootThread.
	 * @param target
	 * @param name
	 */
	/*public SootThread(Runnable target, String name) {
		super(target, name);
	}

	/**
	 * Constructor for SootThread.
	 * @param group
	 * @param target
	 * @param name
	 */
	/*public SootThread(ThreadGroup group, Runnable target, String name) {
		super(group, target, name);
	}

	/**
	 * Constructor for SootThread.
	 * @param group
	 * @param target
	 * @param name
	 * @param stackSize
	 */
	/*public SootThread(
		ThreadGroup group,
		Runnable target,
		String name,
		long stackSize) {
		super(group, target, name, stackSize);
	}
	*/
	
	private String [] cmd;
	private PrintStream sootOut;

	public void run() {
		
		final String [] cmdFinal = getCmd();
		final PrintStream sootOutFinal = getSootOut();
		try {
			Main.main(cmdFinal, sootOutFinal);
		}
		catch (Exception e) {
			SootOutputEvent se = new SootOutputEvent(this, ISootOutputEventConstants.SOOT_NEW_TEXT_EVENT);
       		se.setTextToAppend(e.getLocalizedMessage());
       		final SootOutputEvent toSend = se;
       		getDisplay().asyncExec(new Runnable(){
       			public void run() {
       				SootPlugin.getDefault().fireSootOutputEvent(toSend);
       				};
       			});
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

}
