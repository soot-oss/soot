package ca.mcgill.sable.soot.launching;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.*;

//import ca.mcgill.sable.soot.util.*;
//import ca.mcgill.sable.soot.*;

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
public class SootProcessRunner implements IRunnableWithProgress {

	private String cmd;
	private SootClasspath sootClasspath;
	private Display display;
	private Process proc;
	
	public SootProcessRunner(Display display, String cmd, SootClasspath cp) {
		setCmd(cmd);
		setSootClasspath(cp);
		setDisplay(display);
	}
	
	
	/**
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor)
		throws InvocationTargetException, InterruptedException {
		
			//System.out.println("SootProcessRunner - running");
		try {
                              
        	String exec1 = "java -cp \""+getSootClasspath().getSootClasspath()+"\" soot.Main -p jb use-original-names "+ getCmd();
            //System.out.println(exec1);
            //newProcessStarting();
            setProc(Runtime.getRuntime().exec(exec1));
            
            /*try {
        	(new Thread() {
            	public void run() {
              		monitor.isCanceled()
                }
        	    }).start();
      		}
      		catch (Exception e1) {
      			System.out.println(e1.getMessage());
      		}*/
            
            //StreamGobbler outputGobbler = new StreamGobbler(getProc().getInputStream(), StreamGobbler.OUTPUT_STREAM_TYPE);
            //StreamGobbler errorGobbler = new StreamGobbler(getProc().getErrorStream(), StreamGobbler.ERROR_STREAM_TYPE);
                        
            //getDisplay().asyncExec(
    		//	outputGobbler
    	    //);
            //getDisplay().asyncExec(
            //	errorGobbler
            //);
            
            //System.out.println("just before wait for");
			getProc().waitFor();
		}
        catch (Exception e1) {
        	System.out.println(e1.getMessage());
        }
	}

	/**
	 * Returns the cmd.
	 * @return String
	 */
	public String getCmd() {
		return cmd;
	}

	/**
	 * Sets the cmd.
	 * @param cmd The cmd to set
	 */
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	

	/**
	 * Returns the sootClasspath.
	 * @return SootClasspath
	 */
	public SootClasspath getSootClasspath() {
		return sootClasspath;
	}

	/**
	 * Sets the sootClasspath.
	 * @param sootClasspath The sootClasspath to set
	 */
	public void setSootClasspath(SootClasspath sootClasspath) {
		this.sootClasspath = sootClasspath;
	}

	/**
	 * Returns the display.
	 * @return Display
	 */
	public Display getDisplay() {
		return display;
	}

	/**
	 * Sets the display.
	 * @param display The display to set
	 */
	public void setDisplay(Display display) {
		this.display = display;
	}

	/**
	 * Returns the proc.
	 * @return Process
	 */
	public Process getProc() {
		return proc;
	}

	/**
	 * Sets the proc.
	 * @param proc The proc to set
	 */
	public void setProc(Process proc) {
		this.proc = proc;
	}

}
