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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import soot.jimple.toolkits.annotation.callgraph.CallData;
import ca.mcgill.sable.graph.GraphPlugin;
import ca.mcgill.sable.graph.testing.GraphGenerator;
import ca.mcgill.sable.graph.testing.TestNode;
import ca.mcgill.sable.soot.SootPlugin;
			
/**
 * Main Soot Launcher. Handles running Soot directly (or as a 
 * process) 
 */
public abstract class SootLauncher  implements IWorkbenchWindowActionDelegate {
	
	private IWorkbenchPart part;
 	protected IWorkbenchWindow window;
	private ISelection selection;
	private IStructuredSelection structured;
	protected String platform_location;
	protected String external_jars_location;
	public SootClasspath sootClasspath = new SootClasspath();
	public SootSelection sootSelection;
	private SootCommandList sootCommandList;
	private String outputLocation;
	private SootDefaultCommands sdc;
	private SootOutputFilesHandler fileHandler;
	private DavaHandler davaHandler;
	private ArrayList cfgList;
	
	public void run(IAction action) {
		
		setSootSelection(new SootSelection(structured));		
 		getSootSelection().initialize(); 		
		setFileHandler(new SootOutputFilesHandler(window));
		getFileHandler().resetSootOutputFolder(getSootSelection().getProject());		
		setDavaHandler(new DavaHandler());
		getDavaHandler().setSootOutputFolder(getFileHandler().getSootOutputFolder());
		getDavaHandler().handleBefore();
		initPaths();
		initCommandList();

		
	}
	
	
	private void initCommandList() {
		setSootCommandList(new SootCommandList());
	}
	
	protected void runSootDirectly(){
		runSootDirectly("soot.Main");
	}
	
	private void sendSootClearEvent(){
		Display.getCurrent().syncExec(new Runnable(){
			public void run() {
				SootPlugin.getDefault().fireSootOutputEvent(new SootOutputEvent(SootLauncher.this, ISootOutputEventConstants.SOOT_CLEAR_EVENT));
			};
		});
	}

	private void sendSootOutputEvent(String toSend){
		SootOutputEvent send = new SootOutputEvent(this, ISootOutputEventConstants.SOOT_NEW_TEXT_EVENT);
		send.setTextToAppend(toSend);
		final SootOutputEvent sendFinal = send;
		
		Display.getCurrent().asyncExec(new Runnable(){
			public void run() {
				SootPlugin.getDefault().fireSootOutputEvent(sendFinal);
			};
		});
	}
	
	protected void runSootDirectly(String mainClass) {
		
		int length = getSootCommandList().getList().size();
		String temp [] = new String [length];
		
		getSootCommandList().getList().toArray(temp);
		
        String mainProject;
        String realMainClass;
        if(mainClass.contains(":")) {
        	String[] split = mainClass.split(":");
			mainProject = split[0];
        	realMainClass = split[1];
        } else {
        	mainProject = null;
        	realMainClass = mainClass;
        }
		
    	newProcessStarting(realMainClass,mainProject);
        
		sendSootOutputEvent(realMainClass);
		sendSootOutputEvent(" ");
		
		for (int i = 0; i < temp.length; i++) {
			
			sendSootOutputEvent(temp[i]);
			sendSootOutputEvent(" ");
		}
		sendSootOutputEvent("\n");
		
		
		IRunnableWithProgress op; 
		try {   
            op = new SootRunner(temp, Display.getCurrent(), mainClass);
           	((SootRunner)op).setParent(this);
            ModalContext.run(op, true, new NullProgressMonitor(), Display.getCurrent());
 		} 
 		catch (InvocationTargetException e1) {
    		// handle exception
    		System.out.println("InvocationTargetException: "+e1.getMessage());
    		System.out.println("InvocationTargetException: "+e1.getTargetException());
            System.out.println(e1.getStackTrace());
 		} 
 		catch (InterruptedException e2) {
    		// handle cancelation
    		System.out.println("InterruptedException: "+e2.getMessage());
 		}

	}
	
	private void newProcessStarting(String mainClass, String mainProject) {
        if(mainProject == null) {
        	mainProject = " from Soot's class library";
        } else {
			mainProject = " from project "+mainProject;
        }
        sendSootClearEvent();
        sendSootOutputEvent("Starting"+mainProject+":\n");
	}
			
	private void initPaths() {
		
		sootClasspath.initialize(getSootSelection().getJavaProject());
		
		// platform location 
		platform_location = getSootSelection().getJavaProject().getProject().getLocation().toOSString();
		platform_location = platform_location.substring(0, platform_location.lastIndexOf(System.getProperty("file.separator")));
		// external jars location - may need to change don't think I use this anymore
		setOutputLocation(platform_location+getFileHandler().getSootOutputFolder().getFullPath().toOSString());
		
	}
	
	protected void addJars(){
		try {
	
			IPackageFragmentRoot [] roots = getSootSelection().getJavaProject().getAllPackageFragmentRoots();
			
			for (int i = 0; i < roots.length; i++){
				if (roots[i].isArchive()){
					if (roots[i].getResource() != null){
					
						setClasspathAppend(platform_location+roots[i].getPath().toOSString());
					}
					else {
						setClasspathAppend(roots[i].getPath().toOSString());

					}
				}
			}
		}
		catch (JavaModelException e){
		}
	}
	
	public abstract void setClasspathAppend(String ca);
	
	public void runFinish() {
		getFileHandler().refreshFolder();
		getFileHandler().refreshAll(getSootSelection().getProject());
		//for updating markers
		SootPlugin.getDefault().getManager().updateSootRanFlag();
		final IEditorPart activeEdPart = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		SootPlugin.getDefault().getPartManager().updatePart(activeEdPart);
		// run cfgviewer
		if (getCfgList() != null){
			// currently this is the call graph list of pkgs
			GraphGenerator generator = new GraphGenerator();
			generator.setChildren(convertPkgList(getCfgList()));
			GraphPlugin.getDefault().setGenerator(generator);
		
			generator.run(null);
		}
	}
	
	HashMap alreadyDone = new HashMap();
	
	private ArrayList convertPkgList(ArrayList pkgList){
		ArrayList conList = new ArrayList();
		Iterator it = pkgList.iterator();
		while(it.hasNext()){
			CallData cd = (CallData)it.next();
			TestNode tn = null;
			if (alreadyDone.containsKey(cd)){
				tn = (TestNode)alreadyDone.get(cd);
			}
			else {
				tn = new TestNode();
				tn.setData(cd.getData());
				alreadyDone.put(cd, tn);
				if (cd.getChildren().size() != 0){
					tn.setChildren(convertPkgList(cd.getChildren()));
				}
				if (cd.getOutputs().size() != 0){
					tn.setOutputs(convertPkgList(cd.getOutputs()));
				}
			}
			conList.add(tn);
			
		}
		
		return conList;
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

	/**
	 * @return
	 */
	public DavaHandler getDavaHandler() {
		return davaHandler;
	}

	/**
	 * @param handler
	 */
	public void setDavaHandler(DavaHandler handler) {
		davaHandler = handler;
	}

	/**
	 * @return
	 */
	public ArrayList getCfgList() {
		return cfgList;
	}

	/**
	 * @param list
	 */
	public void setCfgList(ArrayList list) {
		cfgList = list;
	}

}
