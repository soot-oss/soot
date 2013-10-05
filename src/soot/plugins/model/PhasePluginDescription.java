/* Soot - a J*va Optimization Framework
 * 
 * Copyright (C) 2013 Bernhard J. Berger
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

package soot.plugins.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A phase plugin adds a new analysis to a pack. Therefore, it needs a phase
 * name and a plugin class name.
 * 
 * @author Bernhard J. Berger
 */
@XmlRootElement(namespace="http://github.com/Sable/soot/plugins", name="phase-plugin")
public class PhasePluginDescription extends PluginDescription {
	/**
	 * Name of phase. Make sure it consists of '&lt;pack&gt;.&lt;phase&gt;'.
	 */
	private String phaseName;
	
	/**
	 * Name of the plugin class that has to implement {@link SootPhasePlugin}.
	 */
	private String className;
	
	@XmlAttribute(name = "class", required = true)
	public String getClassName() {
		return className;
	}

	public void setClassName(final String name) {
		this.className = name;
	}

	@XmlAttribute(name = "phase", required = true)
	public String getPhaseName() {
		return phaseName;
	}
	
	public void setPhaseName(final String name) {
		phaseName = name;
	}
	
	@Override
	public String toString() {
		return "<PhasePluginDescription name=" + getPhaseName() + " class= " + getClassName() + ">";
	}
}
