package ca.mcgill.sable.soot.launching;

import java.util.ArrayList;

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
public class SootGrimpFileLauncher extends SootFileLauncher {
	
	public void run(IAction action) {
	super.run(action);

		setCmd();
		runSootDirectly();
		runFinish();
	
	}
	
	private void setCmd() {
		
			
		/*StringBuffer classpath = new StringBuffer(LaunchCommands.SOOT_CLASSPATH);
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
		cmd.append(LaunchCommands.GRIMP_OUT);
		*/
	  	//return cmd.toString();
	  	
	  	ArrayList commands = new ArrayList();
	  	commands.add("--"+LaunchCommands.SOOT_CLASSPATH);
	  	commands.add(getSootClasspath().getSootClasspath()+getSootClasspath().getSeparator()+getClasspathAppend());
	  	
		//getSootCommandList().addDoubleOpt("--"+LaunchCommands.SOOT_CLASSPATH, getSootClasspath().getSootClasspath()+getSootClasspath().getSeparator()+getClasspathAppend());
		commands.add("--"+LaunchCommands.OUTPUT_DIR);
		commands.add(getOutputLocation());
		//getSootCommandList().addDoubleOpt("--"+LaunchCommands.OUTPUT_DIR, getOutputLocation());
		getSootCommandList().addSingleOpt("--"+LaunchCommands.KEEP_LINE_NUMBER);
		getSootCommandList().addSingleOpt("--"+LaunchCommands.XML_ATTRIBUTES);
		getSootCommandList().addDoubleOpt("--"+LaunchCommands.OUTPUT, LaunchCommands.GRIMP_OUT);
	

		if (isExtraCmd()) {
			getSootCommandList().addSingleOpt("--"+getExtraCmd());
		}
		//getSootCommandList().addDoubleOpt("--"+LaunchCommands.OUTPUT, LaunchCommands.JIMPLE_OUT);
		commands.add(getToProcess());
		//getSootCommandList().addSingleOpt(getToProcess());
		getSootCommandList().addSingleOpt(commands);
		//return cmd.toString();
	}
}
