package ca.mcgill.sable.soot.launching;

import org.eclipse.ui.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.action.*;
//import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
//import org.eclipse.jdt.core.*;
//import java.io.*;
import java.lang.reflect.InvocationTargetException;

//import soot.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.attributes.SootAttributeFilesReader;
import ca.mcgill.sable.soot.attributes.SootAttributesHandler;
//import ca.mcgill.sable.soot.util.*;
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
public class SootLauncher  implements IWorkbenchWindowActionDelegate {
	
	private IWorkbenchPart part;
 	protected IWorkbenchWindow window;
	private ISelection selection;
	private IStructuredSelection structured;
	protected String platform_location;
	protected String external_jars_location;
	public SootClasspath sootClasspath = new SootClasspath();
	public SootSelection sootSelection;
	//private IFolder sootOutputFolder;
	private SootCommandList sootCommandList;
	private String outputLocation;
	private SootDefaultCommands sdc;
	private SootOutputFilesHandler fileHandler;

	public void run(IAction action) {
		
		setSootSelection(new SootSelection(structured));		
 		getSootSelection().initialize(); 		
		setFileHandler(new SootOutputFilesHandler(window));
		getFileHandler().resetSootOutputFolder(getSootSelection().getProject());		
		initPaths();
		initCommandList();

		
	}
	
	
	private void initCommandList() {
		setSootCommandList(new SootCommandList());
	}
	
	/*public void resetSootOutputFolder() {
		try {
			setSootOutputFolder(getSootSelection().getProject().getFolder("sootOutput"));
			if (!getSootOutputFolder().exists()) {
				getSootOutputFolder().create(false, true, null);
			}
		}
		catch (Exception e1) {
			System.out.println(e1.getMessage());
		}	
	}*/
	
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
		IRunnableWithProgress op; 
		try {   
        	newProcessStarting();
            op = new SootRunner(temp, Display.getCurrent());
            ModalContext.run(op, true, new NullProgressMonitor(), Display.getCurrent());
            //if (window == null) {
            //	window = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
            //}
            //new  ProgressMonitorDialog(window.getShell()).run(true, true, op);
 			//ProgressIndicator pIndicator = new ProgressIndicator(pMonitorDialog.getShell());
 			//pIndicator.beginAnimatedTask();
 		} 
 		catch (InvocationTargetException e1) {
    		// handle exception
    		System.out.println("InvocationTargetException: "+e1.getMessage());
 		} 
 		catch (InterruptedException e2) {
    		// handle cancelation
    		System.out.println("InterruptedException: "+e2.getMessage());
    		//op.getProc().destroy();
 		}

	}
	
	protected void runSootAsProcess(String cmd) {
		
        SootProcessRunner op;    
        try {   
        	newProcessStarting();
            op = new SootProcessRunner(Display.getCurrent(), cmd, sootClasspath);

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
 
   
        SootAttributesHandler temp = new SootAttributesHandler();
		SootPlugin.getDefault().setSootAttributesHandler(temp); 	
		SootAttributeFilesReader safr = new SootAttributeFilesReader();
		safr.readFiles(getSootSelection().getProject().getName());
      
		
        
	}
	private void newProcessStarting() {
		SootOutputEvent clear_event = new SootOutputEvent(this, ISootOutputEventConstants.SOOT_CLEAR_EVENT);
        SootPlugin.getDefault().fireSootOutputEvent(clear_event);    
        SootOutputEvent se = new SootOutputEvent(this, ISootOutputEventConstants.SOOT_NEW_TEXT_EVENT);
        se.setTextToAppend("Starting ...");
        SootPlugin.getDefault().fireSootOutputEvent(se);
	}
			
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
		// external jars location - may need to change don't think I use this anymore
		external_jars_location = Platform.getLocation().removeLastSegments(2).toOSString();
		setOutputLocation(platform_location+getFileHandler().getSootOutputFolder().getFullPath().toOSString());
		
	}
	
	public void runFinish() {
		getFileHandler().refreshFolder();
		//getFileHandler().handleFilesChanged();
		/*SootAttributesHandler temp = new SootAttributesHandler();
		SootPlugin.getDefault().setSootAttributesHandler(temp); 	
		SootAttributeFilesReader safr = new SootAttributeFilesReader();
		safr.readFiles(getSootSelection().getProject().getName());
      */
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
	/*public IFolder getSootOutputFolder() {
		return sootOutputFolder;
	}

	/**
	 * Sets the sootOutputFolder.
	 * @param sootOutputFolder The sootOutputFolder to set
	 */
	/*public void setSootOutputFolder(IFolder sootOutputFolder) {
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

	/**
	 * Returns the fileHandler.
	 * @return SootOutputFilesHandler
	 */
	public SootOutputFilesHandler getFileHandler() {
		return fileHandler;
	}

	/**
	 * Sets the fileHandler.
	 * @param fileHandler The fileHandler to set
	 */
	public void setFileHandler(SootOutputFilesHandler fileHandler) {
		this.fileHandler = fileHandler;
	}

}
