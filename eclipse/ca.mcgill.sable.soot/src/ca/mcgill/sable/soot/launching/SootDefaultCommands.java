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

import java.util.ArrayList;

import ca.mcgill.sable.soot.ui.PhaseOptionsDialog;

/**
 * Sets Soot commands needed by Eclipse in Options Dialog
 * (ex output directory)
 */
public class SootDefaultCommands {

	private PhaseOptionsDialog dialog;
	
	/**
	 * Constructor for SootDefaultCommands.
	 */
	public SootDefaultCommands(PhaseOptionsDialog dialog) {
		setDialog(dialog);
	}
	
	public void setSootClassPath(String val) {
		getDialog().addToEclipseDefList(LaunchCommands.SOOT_CLASSPATH, val);
	}
	
	public void setProcessPath(String val) {
		ArrayList list = new ArrayList();
		list.add(val);
		getDialog().addToEclipseDefList(LaunchCommands.PROCESS_PATH, list);
	}
	public void setProcessPath(ArrayList list){
		getDialog().addToEclipseDefList(LaunchCommands.PROCESS_PATH, list);
	}
	public void setOutputDir(String val) {
		getDialog().addToEclipseDefList(LaunchCommands.OUTPUT_DIR, val);
	}
	
	public void setKeepLineNum() {
		getDialog().addToEclipseDefList(LaunchCommands.KEEP_LINE_NUMBER, new Boolean(true));
	}
	
	public void setPrintTags() {
		getDialog().addToEclipseDefList(LaunchCommands.XML_ATTRIBUTES, new Boolean(true));
	}
	
	public void setSrcPrec(String val) {
		getDialog().addToEclipseDefList(LaunchCommands.SRC_PREC, val);
	}
	
	public void setSootMainClass(){
		getDialog().addToEclipseDefList("sootMainClass", "soot.Main");
	}
	
	

	

	/**
	 * Returns the dialog.
	 * @return PhaseOptionsDialog
	 */
	public PhaseOptionsDialog getDialog() {
		return dialog;
	}

	/**
	 * Sets the dialog.
	 * @param dialog The dialog to set
	 */
	public void setDialog(PhaseOptionsDialog dialog) {
		this.dialog = dialog;
	}

}
