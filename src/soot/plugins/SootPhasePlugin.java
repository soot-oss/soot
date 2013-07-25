package soot.plugins;

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
	 * Default option for enabling a plugin.
	 */
	public final String ENABLED_BY_DEFAULT = "enabled:true";
	
	/**
	 * @return a list of phase options.
	 */
	public abstract String [] getDeclaredOptions();

	/**
	 * Returns a list of default values for initializing the parameters. Each entry in
	 * the list is of kind "<parameter-name>:<default-value>". Please note, that you
	 * have to add the {@code ENABLED_BY_DEFAULT} option if you want the plugin to be
	 * enabled.
	 * 
	 * @return a list of default values.
	 */
	public abstract String [] getDefaultOptions();

	/**
	 * Creates a new transformer instance (either SceneTransformer or BodyTransformer). The
	 * method will be called just once.
	 * 
	 * @return a new transformer instance.
	 */
	public abstract Transformer getTransformer();
	
	/**‚s
	 * 
	 * @param pluginDescription
	 */
	public abstract void setDescription(final PhasePluginDescription pluginDescription);
}
