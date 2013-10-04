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
