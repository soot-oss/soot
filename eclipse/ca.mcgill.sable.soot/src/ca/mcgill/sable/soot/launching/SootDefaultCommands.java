package ca.mcgill.sable.soot.launching;

import ca.mcgill.sable.soot.testing.PhaseOptionsDialog;

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
public class SootDefaultCommands {

	private PhaseOptionsDialog dialog;
	
	/**
	 * Constructor for SootDefaultCommands.
	 */
	public SootDefaultCommands(PhaseOptionsDialog dialog) {
		setDialog(dialog);
	}
	
	public void setSootClassPath(String val) {
		getDialog().addToDefList("cp", val);
	}
	
	public void setProcessPath(String val) {
		getDialog().addToDefList("process-path", val);
	}
	
	public void setOutputDir(String val) {
		getDialog().addToDefList("d", val);
	}
	
	public void setKeepLineNum() {
		getDialog().addToDefList("keep-line-number", new Boolean(true));
	}
	
	public void setPrintTags() {
		getDialog().addToDefList("xml-attributes", new Boolean(true));
	}
	
	public void setSrcPrec(String val) {
		
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
