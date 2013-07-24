package soot.plugins;

import java.util.List;

import soot.Transformer;
import soot.plugins.model.PhasePluginDescription;

/**
 * An interface every phase plugin has to implement. The plugin has to support basic
 * functionality, such as creating the transformer and support a list of possible options.
 * The first method that is called after object creation is {@code setDescription} then
 * the list of possible parameters is called and the last step is the call to {@code getTransformer}.
 * 
 * @author Bernhard J. Berger
 */
public interface SootPhasePlugin {
	
	/**
	 * @return a list of phase options similar to the specification {@code soot_options.xml}.
	 */
	//public abstract List<E> getOptions();

	/**
	 * Creates a new transformer instance (either SceneTransformer or BodyTransformer). The
	 * method will be called just once.
	 * 
	 * @return a new transformer instance.
	 */
	public abstract Transformer getTransformer();
	
	/**
	 * 
	 * @param pluginDescription
	 */
	public abstract void setDescription(final PhasePluginDescription pluginDescription);
}
