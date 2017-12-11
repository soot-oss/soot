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

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Java representation of the xml root element. It's a simple holder for plugins.
 * 
 * @author Bernhard J. Berger
 */
@XmlRootElement(namespace="http://github.com/Sable/soot/plugins", name="soot-plugins")
public class Plugins {
	/**
	 * List of all plugin entries.
	 */
	private final List<PluginDescription> pluginDescriptions = new LinkedList<PluginDescription>();
	
	@XmlElementRefs({@XmlElementRef(name="phase-plugin", type=PhasePluginDescription.class)})
    public List<PluginDescription> getPluginDescriptions() {
		return pluginDescriptions;
	}
}
