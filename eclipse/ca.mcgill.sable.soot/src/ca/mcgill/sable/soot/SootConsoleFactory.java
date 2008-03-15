package ca.mcgill.sable.soot;

import org.eclipse.ui.console.IConsoleFactory;

/**
 * This implements the extension point org.eclipse.ui.console.consoleFactories,
 * showing a widget to open the Soot console in the console view.
 * @author Eric Bodden
 */
public class SootConsoleFactory implements IConsoleFactory {

	public void openConsole() {
		SootPlugin.getDefault().showConsole();
	}

}
