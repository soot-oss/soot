package ca.mcgill.sable.soot.launching;

import org.eclipse.jface.action.IAction;

/**
 * @author jlhotak
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
// not used
public class SootStaticExampleLauncher extends SootFileLauncher {
	public void run(IAction action) {
	super.run(action);

		String cmd = getCmd();
		runSootAsProcess(cmd);
		runFinish();
	
	}
	
	private String getCmd() {
		
			
		StringBuffer classpath = new StringBuffer(LaunchCommands.SOOT_CLASSPATH);
		classpath.append(getSootClasspath().getSootClasspath());
		classpath.append(getSootClasspath().getSeparator());
		classpath.append(getClasspathAppend());
		
		
		String output_path = LaunchCommands.OUTPUT_DIR+getOutputLocation();
				
		StringBuffer cmd = new StringBuffer();
		cmd.append(classpath+" ");
		cmd.append(output_path+" ");
		cmd.append(getToProcess()+" ");
		if (isExtraCmd()) {
			cmd.append(getExtraCmd()+" ");
		}
		cmd.append(LaunchCommands.STATIC);
		
	  	return cmd.toString();
	}
}
