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

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.action.*;

/**
 * Launches Soot with -f J for selected files.
 */
public class SootJimpleFromJavaFileLauncher extends SootFileLauncher {

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
	
	private void setCmd() {
		
		ArrayList commands = new ArrayList();
		commands.add("--"+LaunchCommands.SOOT_CLASSPATH);
		commands.add(getClasspathAppend());
	  	
		commands.add("--"+LaunchCommands.OUTPUT_DIR);
		commands.add(getOutputLocation());
		getSootCommandList().addSingleOpt("--"+LaunchCommands.KEEP_LINE_NUMBER);
		getSootCommandList().addSingleOpt("--"+LaunchCommands.XML_ATTRIBUTES);
		getSootCommandList().addDoubleOpt("--"+LaunchCommands.OUTPUT, LaunchCommands.JIMPLE_OUT);
	

		if (isExtraCmd()) {
			getSootCommandList().addSingleOpt("--"+getExtraCmd());
		}
        if (isSrcPrec()) {
            getSootCommandList().addDoubleOpt("--"+LaunchCommands.SRC_PREC, getSrcPrec());
        }
		Iterator it = getToProcessList().iterator();
		while (it.hasNext()){
			commands.add((String)it.next());
		}
		getSootCommandList().addSingleOpt(commands);
	}
}
