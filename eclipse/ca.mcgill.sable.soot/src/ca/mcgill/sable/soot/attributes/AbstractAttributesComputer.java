/*
 * Created on Nov 6, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.attributes;

import java.io.File;
import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ui.texteditor.*;

import ca.mcgill.sable.soot.SootPlugin;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class AbstractAttributesComputer {

	private IResource rec;
	private IProject proj;
	
	/**
	 * compute list of xml filenames
	 */
	protected ArrayList computeFiles(ArrayList names){
		ArrayList fileList = new ArrayList();
		String sep = System.getProperty("file.separator");
		IContainer con = (IContainer)getProj().getFolder("sootOutput"+sep+"attributes"+sep);
		try {
			IResource [] files = con.members();
			for (int i = 0; i < files.length; i++){
				Iterator it = names.iterator();
				while (it.hasNext()){
					String fileNameToMatch = (String)it.next();
					if (files[i].getName().matches(fileNameToMatch+"[$].*") || files[i].getName().matches(fileNameToMatch+"\\."+"xml")){
						fileList.add(files[i]);
					}
				}
			}
		}
		catch(CoreException e){
		}
		return fileList;
	}
	/**
	 * compute top-level names
	 */
	protected abstract ArrayList computeNames(AbstractTextEditor editor);
	
	protected abstract ArrayList computeNames(IFile file);
	
	/**
	 * initialize rec and proj
	 */
	protected abstract void init(AbstractTextEditor editor);
	
	/**
	 * compute attributes
	 */
	protected SootAttributesHandler computeAttributes(ArrayList files, SootAttributesHandler sah) {
		SootAttributeFilesReader safr = new SootAttributeFilesReader();
		Iterator it = files.iterator();
		while (it.hasNext()){
			String fileName = ((IPath)((IFile)it.next()).getLocation()).toOSString();
			AttributeDomProcessor adp = safr.readFile(fileName);
			if (adp != null) {
				System.out.println("updating attrs in sah");	
				sah.setAttrList(adp.getAttributes());
				sah.setKeyList(adp.getKeys());
			}
		}
		sah.setValuesSetTime(System.currentTimeMillis());
		SootPlugin.getDefault().getManager().addToFileWithAttributes((IFile)getRec(), sah);
		return sah;
	}
	
	public SootAttributesHandler getAttributesHandler(IFile file){
		ArrayList files = computeFiles(computeNames(file));
		return getHandler(files);
	}
	
	public SootAttributesHandler getAttributesHandler(AbstractTextEditor editor){
		// init
		init(editor);
		// computeFileNames
		ArrayList files = computeFiles(computeNames(editor));
		return getHandler(files);
	}
	
	private SootAttributesHandler getHandler(ArrayList files){
		// check if any have changed since creation of attributes in handler
		if (!(getRec() instanceof IFile)) return null;
		
		SootAttributesHandler handler = SootPlugin.getDefault().getManager().getAttributesHandlerForFile((IFile)getRec());
		if (handler != null){
		
			long valuesSetTime = handler.getValuesSetTime();
			//System.out.println("value set time: "+valuesSetTime);
			boolean update = handler.isUpdate();
		
			Iterator it = files.iterator();
			while (it.hasNext()){
				IFile next = (IFile)it.next();
				//System.out.println(next.getModificationStamp());
				File realFile = new File(next.getLocation().toOSString());
				System.out.println("val set mod time: "+valuesSetTime);
				System.out.println("real set time: "+realFile.lastModified());
				
				if (realFile.lastModified() > valuesSetTime){
					update = true;
				}
			}
			handler.setUpdate(update);
			// if no return handler
			if (!update){
				//handler.setUpdate(false);
				System.out.println("attr already set");
				return handler;
				//return computeAttributes(files, handler);
			}
			else {
				System.out.println("compute attr");
				return computeAttributes(files, handler);
			}
		}
		else {
			return computeAttributes(files, new SootAttributesHandler());
		}
	}
	/**
	 * @return
	 */
	public IProject getProj() {
		return proj;
	}

	/**
	 * @return
	 */
	public IResource getRec() {
		return rec;
	}

	/**
	 * @param project
	 */
	public void setProj(IProject project) {
		proj = project;
	}

	/**
	 * @param resource
	 */
	public void setRec(IResource resource) {
		rec = resource;
	}

}
