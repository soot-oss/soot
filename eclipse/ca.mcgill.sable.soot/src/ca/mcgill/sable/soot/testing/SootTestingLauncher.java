package ca.mcgill.sable.soot.testing;

import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.preference.PreferenceManager;

import ca.mcgill.sable.soot.SootPlugin;
import ca.mcgill.sable.soot.launching.SootLauncher;
/**
 * @author jlhotak
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SootTestingLauncher extends SootLauncher {

	public void run(IAction action) {
		
		super.run(action);
		
		/*TestDialog dialog = new TestDialog(window.getShell(),
         getSootSelection().getProject());
        dialog.open();
        
        IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
        System.out.println(settings.get("jimple_button_select")); 
        System.out.println(settings.get("jimp_button_select"));
        System.out.println(settings.get("grimp_button_select"));
        System.out.println(settings.get("b_button_select"));
        System.out.println(settings.get("baf_button_select"));
        */
        /*PreferenceManager pm = new PreferenceManager();
        
        SootOptionsTreeDialog dialog = new SootOptionsTreeDialog(window.getShell());
        dialog.open();*/
        
        /*TestOutputDialog dialog = new TestOutputDialog(window.getShell(),
        	getSootSelection().getProject());
        dialog.open();*/
      	
		
	}

}
