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

import org.eclipse.jface.dialogs.IInputValidator;

/**
 * Manages disallowing of duplicate names for saving Soot configurations
 */
public class SootConfigNameInputValidator implements IInputValidator {

	private ArrayList alreadyUsed;
	
	/**
	 * Constructor for SootConfigNameInputValidator.
	 */
	public SootConfigNameInputValidator() {
		super();
	}

	/**
	 * @see org.eclipse.jface.dialogs.IInputValidator#isValid(String)
	 */
	public String isValid(String newText) {
		if (newText.equals("")){
			return "You must enter a name!";
		}
		else if (newText == null) {
			return "Must not be null!";
		}
		else if (getAlreadyUsed().contains(newText)) {
			return "A configuration with that name already exists!";
		}
		return null;
	}

	/**
	 * Returns the alreadyUsed.
	 * @return ArrayList
	 */
	public ArrayList getAlreadyUsed() {
		return alreadyUsed;
	}

	/**
	 * Sets the alreadyUsed.
	 * @param alreadyUsed The alreadyUsed to set
	 */
	public void setAlreadyUsed(ArrayList alreadyUsed) {
		this.alreadyUsed = alreadyUsed;
	}

}
