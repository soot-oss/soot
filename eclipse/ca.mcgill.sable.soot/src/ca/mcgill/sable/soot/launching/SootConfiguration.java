package ca.mcgill.sable.soot.launching;

import java.util.HashMap;

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
public class SootConfiguration {

	/**
	 * Constructor for SootConfiguration.
	 */
	public SootConfiguration(HashMap aliasValPairs, String name) {
		super();
		setAliasValPairs(aliasValPairs);
		setName(name);
	}
	
	private HashMap aliasValPairs;
	private String name;

	/**
	 * Returns the aliasValPairs.
	 * @return HashMap
	 */
	public HashMap getAliasValPairs() {
		return aliasValPairs;
	}

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the aliasValPairs.
	 * @param aliasValPairs The aliasValPairs to set
	 */
	public void setAliasValPairs(HashMap aliasValPairs) {
		this.aliasValPairs = aliasValPairs;
	}

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
