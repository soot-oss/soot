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

/**
 * A thread for launching Soot a J*va application from within
 * Eclispe another J*va application.
 */
public class SootLauncherThread extends Thread {

	/**
	 * Constructor for SootLauncherThread.
	 */
	public SootLauncherThread() {
		super();
	}

	/**
	 * Constructor for SootLauncherThread.
	 * @param target
	 */
	public SootLauncherThread(Runnable target) {
		super(target);
	}

	/**
	 * Constructor for SootLauncherThread.
	 * @param group
	 * @param target
	 */
	public SootLauncherThread(ThreadGroup group, Runnable target) {
		super(group, target);
	}

	/**
	 * Constructor for SootLauncherThread.
	 * @param name
	 */
	public SootLauncherThread(String name) {
		super(name);
	}

	/**
	 * Constructor for SootLauncherThread.
	 * @param group
	 * @param name
	 */
	public SootLauncherThread(ThreadGroup group, String name) {
		super(group, name);
	}

	/**
	 * Constructor for SootLauncherThread.
	 * @param target
	 * @param name
	 */
	public SootLauncherThread(Runnable target, String name) {
		super(target, name);
	}

	/**
	 * Constructor for SootLauncherThread.
	 * @param group
	 * @param target
	 * @param name
	 */
	public SootLauncherThread(
		ThreadGroup group,
		Runnable target,
		String name) {
		super(group, target, name);
	}

	/**
	 * Constructor for SootLauncherThread.
	 * @param group
	 * @param target
	 * @param name
	 * @param stackSize
	 */
	public SootLauncherThread(
		ThreadGroup group,
		Runnable target,
		String name,
		long stackSize) {
		super(group, target, name, stackSize);
	}

}
