package ca.mcgill.sable.soot.launching;

import org.eclipse.ui.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.action.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;

import soot.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.attributes.SootAttributeFilesReader;
import ca.mcgill.sable.soot.attributes.SootAttributesHandler;
import ca.mcgill.sable.soot.util.*;
import org.eclipse.swt.widgets.*;


import java.util.*;

/**
 * @author jlhotak
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SootLauncher implements IWorkbenchWindowActionDelegate {
	
	private IWorkbenchPart part;
 	protected IWorkbenchWindow window;
	private ISelection selection;
	private IStructuredSelection structured;
	protected String platform_location;
	protected String external_jars_location;
	public SootClasspath sootClasspath = new SootClasspath();
	public SootSelection sootSelection;
	private IFolder sootOutputFolder;
	private SootCommandList sootCommandList;
	private String outputLocation;
	private SootDefaultCommands sdc;

	public void run(IAction action) {

		setSootSelection(new SootSelection(structured));		
 		getSootSelection().initialize(); 		
		
		resetSootOutputFolder();		
		initPaths();
		initCommandList();
		
	}
	
	private void initCommandList() {
		setSootCommandList(new SootCommandList());
		//getSootCommandList().addDoubleOpt(LaunchCommands.SOOT_CLASSPATH, getSootClasspath().getSootClasspath());		
		//getSootCommandList().addDoubleOpt(LaunchCommands.OUTPUT_DIR, getOutputLocation());
		
	}
	
	public void resetSootOutputFolder() {
		try {
			setSootOutputFolder(getSootSelection().getProject().getFolder("sootOutput"));
			if (!getSootOutputFolder().exists()) {
				getSootOutputFolder().create(false, true, null);
			}
		}
		catch (Exception e1) {
			System.out.println(e1.getMessage());
		}	
	}
	
	protected void runSootDirectly() {
		
		int length = getSootCommandList().getList().size();
		//Object [] temp = getSootCommandList().getList().toArray();
		String temp [] = new String [length];
		
		getSootCommandList().getList().toArray(temp);
		
		final String [] cmdAsArray = temp;
		
		for (int i = 0; i < temp.length; i++) {
			System.out.println(temp[i]);
			
		}
		System.out.println("about to make list be array of strings");
		//final String [] cmdAsArray = (String []) temp;
		SootRunner op;    
        try {   
        	newProcessStarting();
            op = new SootRunner(Display.getCurrent(), cmdAsArray);
            if (window == null) {
            	window = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
            }
            new  ProgressMonitorDialog(window.getShell()).run(true, true, op);
 			//ProgressIndicator pIndicator = new ProgressIndicator(pMonitorDialog.getShell());
 			//pIndicator.beginAnimatedTask();
 		} 
 		catch (InvocationTargetException e1) {
    		// handle exception
 		} 
 		catch (InterruptedException e2) {
    		// handle cancelation
    		System.out.println(e2.getMessage());
    		//op.getProc().destroy();
 		}
		/*try {
		final String [] cmdLine = formCmdLine(cmd);
		final PipedInputStream pis = new PipedInputStream();
		//StreamGobbler sootIn = new StreamGobbler(new PipedInputStream(),0);
      	final PipedOutputStream pos = new PipedOutputStream(pis);
      	final PrintStream sootOut = new PrintStream(pos);
      	
      	//StreamGobbler out = new StreamGobbler(pis,0);
      	//out.run();
        	(new Thread() {
            	public void run() {
                	Main.main(cmdLine, sootOut);
                	try {
                	BufferedReader br = new BufferedReader(new InputStreamReader(pis));
                	while (true) {
                	
                	String temp = (String)br.readLine();
                	if (temp == null) break;
                	System.out.println(temp);
                	}
                	}
                	catch(IOException e1) {
                		System.out.println(e1.getMessage());
                	}
        		}
            }).start();
            //StreamGobbler out = new StreamGobbler(pis,0);        	
              
      	}
      	catch (Exception e1) {
      		System.out.println(e1.getMessage());
      	}*/
	}
	
	/*public void printText(String text) {
		SootOutputEvent se = new SootOutputEvent(this, ISootOutputEventConstants.SOOT_NEW_TEXT_EVENT);
       	se.setTextToAppend(text);
       	//SootPlugin.getDefault().fireSootOutputEvent(se);         	
	}
	
	public void handleOutput(Process proc) {
		StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), StreamGobbler.OUTPUT_STREAM_TYPE);
        StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), StreamGobbler.ERROR_STREAM_TYPE);
        Display.getCurrent().asyncExec(
    		outputGobbler
    	);
        Display.getCurrent().asyncExec(
        	errorGobbler
        );
	}*/
	
	protected void runSootAsProcess(String cmd) {
		//try {
                              
        	//String exec1 = "java -cp "+sootClasspath.getSootClasspath()+" soot.Main " + cmd;
            //System.out.println(exec1);
        SootProcessRunner op;    
        try {   
        	newProcessStarting();
            op = new SootProcessRunner(Display.getCurrent(), cmd, sootClasspath);
            //op.run(null);
            //op.setCmd(cmd);
            //op.setSootClasspath(sootClasspath);
            //newProcessStarting();
            if (window == null) {
            	window = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
            }
            new  ProgressMonitorDialog(window.getShell()).run(true, true, op);
 			
 			//ProgressIndicator pIndicator = new ProgressIndicator(pMonitorDialog.getShell());
 			//pIndicator.beginAnimatedTask();
 		} 
 		catch (InvocationTargetException e1) {
    		// handle exception
 		} 
 		catch (InterruptedException e2) {
    		// handle cancelation
    		System.out.println(e2.getMessage());
    		//op.getProc().destroy();
 		}
 
            //Process proc = Runtime.getRuntime().exec(exec1);
            //StreamGobbler outputGobbler = new StreamGobbler(this, proc.getInputStream(), StreamGobbler.OUTPUT_STREAM_TYPE);
            //StreamGobbler errorGobbler = new StreamGobbler(this, proc.getErrorStream(), StreamGobbler.ERROR_STREAM_TYPE);
            //Display.getCurrent().asyncExec(
    		//	outputGobbler
    	    //);
            //Display.getCurrent().asyncExec(
            //	errorGobbler
            //);
            //outputGobbler.start()});
            //errorGobbler.start();
            //proc.waitFor();*/
        	//BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			//BufferedReader br2 = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			//handleSootOutput(br);
			//handleSootOutput(br2);
			//proc.waitFor();
		
		/*}
        catch (Exception e1) {
        	System.out.println(e1.getMessage());
        }*/
        //System.out.println(getSootSelection().getProject().getName());
        //for demo only if (getSootSelection().getProject().getName().equals("test")) { 
        SootAttributesHandler temp = new SootAttributesHandler();
		SootPlugin.getDefault().setSootAttributesHandler(temp); 	
		SootAttributeFilesReader safr = new SootAttributeFilesReader();
		safr.readFiles(getSootSelection().getProject().getName());
        //}
		
        
	}
	private void newProcessStarting() {
		SootOutputEvent clear_event = new SootOutputEvent(this, ISootOutputEventConstants.SOOT_CLEAR_EVENT);
        SootPlugin.getDefault().fireSootOutputEvent(clear_event);    
        SootOutputEvent se = new SootOutputEvent(this, ISootOutputEventConstants.SOOT_NEW_TEXT_EVENT);
        se.setTextToAppend("Starting ...");
        SootPlugin.getDefault().fireSootOutputEvent(se);
	}
	
	/*private void handleSootOutput(BufferedReader br) {
		try {
			//SootOutputEvent clear_event = new SootOutputEvent(this, ISootOutputEventConstants.SOOT_CLEAR_EVENT);
            //SootPlugin.getDefault().fireSootOutputEvent(clear_event);
            while (true) {
               	String temp = (String)br.readLine();
               	if (temp == null) break;
               	SootOutputEvent se = new SootOutputEvent(this, ISootOutputEventConstants.SOOT_NEW_TEXT_EVENT);
               	se.setTextToAppend(temp);
               	SootPlugin.getDefault().fireSootOutputEvent(se);
              	//System.out.println(temp);
            }
		}
		catch(Exception e1) {
			System.out.println(e1.getMessage());
		}
	}*/
	
	private String[] formCmdLine(String cmd) {
	 
	  	
	   	StringTokenizer st = new StringTokenizer(cmd);
	  	int count = st.countTokens();
        String[] cmdLine = new String[count];        
        count = 0;
        
        while(st.hasMoreTokens()) {
            cmdLine[count++] = st.nextToken();
            System.out.println(cmdLine[count-1]); 
        }
        
        return cmdLine; 
	}
	
	private void initPaths() {
		
		sootClasspath.initialize();
		
		// platform location 
		platform_location = Platform.getLocation().toOSString();
		// external jars location - may need to change
		external_jars_location = Platform.getLocation().removeLastSegments(2).toOSString();
		setOutputLocation(platform_location+getSootOutputFolder().getFullPath().toOSString());
		
	}
	
	public void runFinish() {
		try {
			getSootOutputFolder().refreshLocal(IResource.DEPTH_INFINITE,null);
			//SootOutputEvent se = new SootOutputEvent(this, ISootOutputEventConstants.SOOT_NEW_TEXT_EVENT);
			//se.setTextToAppend("Attributes Ready");
			//SootPlugin.getDefault().fireSootOutputEvent(se);
		} 
		catch (CoreException e1) {
			System.out.println(e1.getMessage());
		}
	}
	

	public IStructuredSelection getStructured() {
		return structured;
	}
		
	private void setStructured(IStructuredSelection struct) {
		structured = struct;
	}
	
	public void init(IWorkbenchWindow window) {
 		this.window = window;
 	}
 	
 	public void dispose() {
 	}
 	
 	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;	
		
		if (selection instanceof IStructuredSelection){
			setStructured((IStructuredSelection)selection);
		}
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
	 * Returns the sootSelection.
	 * @return SootSelection
	 */
	public SootSelection getSootSelection() {
		return sootSelection;
	}

	/**
	 * Sets the sootSelection.
	 * @param sootSelection The sootSelection to set
	 */
	public void setSootSelection(SootSelection sootSelection) {
		this.sootSelection = sootSelection;
	}

	/**
	 * Returns the sootOutputFolder.
	 * @return IFolder
	 */
	public IFolder getSootOutputFolder() {
		return sootOutputFolder;
	}

	/**
	 * Sets the sootOutputFolder.
	 * @param sootOutputFolder The sootOutputFolder to set
	 */
	public void setSootOutputFolder(IFolder sootOutputFolder) {
		this.sootOutputFolder = sootOutputFolder;
	}

	/**
	 * Returns the window.
	 * @return IWorkbenchWindow
	 */
	public IWorkbenchWindow getWindow() {
		return window;
	}

	/**
	 * Returns the sootCommandList.
	 * @return SootCommandList
	 */
	public SootCommandList getSootCommandList() {
		return sootCommandList;
	}

	/**
	 * Sets the sootCommandList.
	 * @param sootCommandList The sootCommandList to set
	 */
	public void setSootCommandList(SootCommandList sootCommandList) {
		this.sootCommandList = sootCommandList;
	}

	/**
	 * Returns the output_location.
	 * @return String
	 */
	public String getOutputLocation() {
		return outputLocation;
	}

	/**
	 * Sets the output_location.
	 * @param output_location The output_location to set
	 */
	public void setOutputLocation(String outputLocation) {
		this.outputLocation = outputLocation;
	}

	/**
	 * Returns the sdc.
	 * @return SootDefaultCommands
	 */
	public SootDefaultCommands getSdc() {
		return sdc;
	}

	/**
	 * Sets the sdc.
	 * @param sdc The sdc to set
	 */
	public void setSdc(SootDefaultCommands sdc) {
		this.sdc = sdc;
	}

}
