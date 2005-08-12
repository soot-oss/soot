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
        super.handleMultipleFiles();

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
		commands.add(getClasspathAppend());
		commands.add("--"+LaunchCommands.OUTPUT_DIR);
		commands.add(getOutputLocation());
		
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
		
		Iterator it = getToProcessList().iterator();
		while (it.hasNext()){
			commands.add((String)it.next());
		}
		getSootCommandList().addSingleOpt(commands);
	}
}
