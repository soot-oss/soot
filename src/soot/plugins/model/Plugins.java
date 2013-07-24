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
