package ca.mcgill.sable.soot.launching;

import org.eclipse.jface.text.*;
import org.eclipse.ui.*;
import ca.mcgill.sable.soot.*;

/**
 * @author jlhotak
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SootDocument extends Document implements ISootOutputEventListener {

	
	public SootDocument() {
	}
	
	public void startUp() {
		//System.out.println("Soot doc registers as listener");
		SootPlugin.getDefault().addSootOutputEventListener(this);
	}
	
	
	public void handleSootOutputEvent(SootOutputEvent event) {
		System.out.println(event.getEventType());
		switch (event.getEventType()) {
			case ISootOutputEventConstants.SOOT_CLEAR_EVENT: 
				clearText();
				break;
			case ISootOutputEventConstants.SOOT_NEW_TEXT_EVENT:
				appendText(event.getTextToAppend());
				break;
			default:
				break;
		}
		notifySootOutputView();
		showSootOutputView();
	}
	
	private void notifySootOutputView() {
		IWorkbench workbench= PlatformUI.getWorkbench();
		IWorkbenchWindow[] windows= workbench.getWorkbenchWindows();
		for (int i = 0; i < windows.length; i++) {
			IWorkbenchWindow iWorkbenchWindow = windows[i];
			IWorkbenchPage[] pages= iWorkbenchWindow.getPages();
			for (int j = 0; j < pages.length; j++) {
				IWorkbenchPage iWorkbenchPage = pages[j];
				IViewPart part= iWorkbenchPage.findView(ISootConstants.SOOT_OUTPUT_VIEW_ID);
				if (part instanceof SootOutputView) {
					SootOutputView view= (SootOutputView)part;
					view.setSootDocument(this);
				}
			}
		}
	}
	
	private void clearText() {
		System.out.println("clearing text");
		set(new String());
	}
	
	private void appendText(String text) {
		StringBuffer sb = new StringBuffer(get());
		sb.append("\n");
		sb.append(text);
		set(sb.toString());
	}
	
	 
	private void showSootOutputView() {
		IWorkbenchWindow window = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();		
		if (window != null) {
			IWorkbenchPage page= window.getActivePage();
			if (page != null) {
				try {
					IViewPart sootOutputViewer = page.findView("ca.mcgill.sable.soot.launching.sootoutputview.view");
					if(sootOutputViewer == null) {
						IWorkbenchPart activePart= page.getActivePart();
						page.showView("ca.mcgill.sable.soot.launching.sootoutputview.view");
						//restore focus stolen by the creation of the console
						page.activate(activePart);
					} 
					else {
						page.bringToTop(sootOutputViewer);
					}
				} 
				catch (PartInitException pie) {
					System.out.println(pie.getMessage());	
				}
			}
		}
	}
	
}
