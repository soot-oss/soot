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

import org.eclipse.jface.action.IAction;
//import ca.mcgill.sable.soot.util.*;
import java.util.*;
/**
 * Launches Soot with --app --f dava on selected file
 */
public class DavaDecompileAppFromJavaFileLauncher extends SootFileLauncher {

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		super.run(action);
        super.setIsSrcPrec(true);
        super.setSrcPrec(LaunchCommands.JAVA_IN);
        super.handleFiles();

		if (isDoNotContinue()) return;
		setCmd();
		runSootDirectly();
		runFinish();
	
	}
	
	/**
	 * Method getCmd.
	 * @return String
	 */
	private void setCmd() {
		
		ArrayList commands = new ArrayList();
		commands.add("--"+LaunchCommands.SOOT_CLASSPATH);
		commands.add(getSootClasspath().getSootClasspath()+getSootClasspath().getSeparator()+getClasspathAppend());
		//getSootCommandList().addDoubleOpt("--"+LaunchCommands.SOOT_CLASSPATH, getSootClasspath().getSootClasspath()+getSootClasspath().getSeparator()+getClasspathAppend());
			
		/*StringBuffer classpath = new StringBuffer(LaunchCommands.SOOT_CLASSPATH);
		classpath.append(getSootClasspath().getSootClasspath());
		classpath.append(getSootClasspath().getSeparator());
		classpath.append(getClasspathAppend());
		*/
		commands.add("--"+LaunchCommands.OUTPUT_DIR);
		commands.add(getOutputLocation());
		//getSootCommandList().addDoubleOpt("--"+LaunchCommands.OUTPUT_DIR, getOutputLocation());
		/*String output_path = LaunchCommands.OUTPUT_DIR+getOutputLocation();
				
		StringBuffer cmd = new StringBuffer();
		cmd.append(classpath+" ");
		cmd.append(output_path+" ");
		cmd.append(getToProcess()+" ");*/
		
		// I think we need these two options here for consistency
		getSootCommandList().addSingleOpt("--"+LaunchCommands.KEEP_LINE_NUMBER);
		getSootCommandList().addSingleOpt("--"+LaunchCommands.XML_ATTRIBUTES);
		if (isExtraCmd()) {
			getSootCommandList().addSingleOpt("--"+getExtraCmd());
		}
        
        if (isSrcPrec()) {
            getSootCommandList().addDoubleOpt("--"+LaunchCommands.SRC_PREC, getSrcPrec());
        }
        
		getSootCommandList().addSingleOpt("--"+LaunchCommands.APP);
		getSootCommandList().addSingleOpt("--"+LaunchCommands.DAVA);
		
		commands.add(getToProcess());
		//getSootCommandList().addSingleOpt(getToProcess());
		getSootCommandList().addSingleOpt(commands);
	  	//return cmd.toString();*/
	}
}
