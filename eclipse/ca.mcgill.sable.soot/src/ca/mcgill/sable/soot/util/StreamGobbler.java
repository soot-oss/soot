package ca.mcgill.sable.soot.util;

import java.io.*;
//import java.util.*;

import org.eclipse.swt.widgets.Display;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.launching.*;

/**
 * @author jlhotak
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class StreamGobbler extends Thread {

	public static final int OUTPUT_STREAM_TYPE = 0;
	public static final int ERROR_STREAM_TYPE = 1;
	
	private InputStream is;
	private int type;
	private Display display;
	
	
	public StreamGobbler(Display display, InputStream is, int type) {
		setIs(is);
		setType(type);
		setDisplay(display);
		
	}
	
	public void run() {
		System.out.println("Gobbler running");
		try {
			InputStreamReader isr = new InputStreamReader(getIs());
			BufferedReader br = new BufferedReader(isr);
			
			while (true) {
				String temp = br.readLine();
				//int temp = isr.read();
				//String temp = (new Character((char)isr.read())).toString();
				if (temp == null) break;
				//if (temp == -1) break;
				//System.out.print((char)temp);
				SootOutputEvent se = new SootOutputEvent(this, ISootOutputEventConstants.SOOT_NEW_TEXT_EVENT);
       			//se.setTextToAppend(Character.toString((char)temp));
       			se.setTextToAppend(temp);
       			final SootOutputEvent toSend = se;
       			//System.out.println("wants to fire sootevent: "+toSend);
       			getDisplay().asyncExec(new Runnable(){
       				public void run() {
       					SootPlugin.getDefault().fireSootOutputEvent(toSend);
       					//System.out.println("fired sootevent: "+toSend);
       				};
       			});
       			se = new SootOutputEvent(this, ISootOutputEventConstants.SOOT_NEW_TEXT_EVENT);
       			//se.setTextToAppend(Character.toString((char)temp));
       			se.setTextToAppend("\n");
       			final SootOutputEvent newline = se;
       			getDisplay().asyncExec(new Runnable(){
       				public void run() {
       					SootPlugin.getDefault().fireSootOutputEvent(newline);
       				};
       			});
       			//SootOutputEventThread eventThread = new SootOutputEventThread(se);
       			
       			//getDisplay().asyncExec(eventThread);
       			//eventThread.start();
       			//se = new SootOutputEvent(this, ISootOutputEventConstants.SOOT_NEW_TEXT_EVENT);
       			//se.setTextToAppend("\n");
       			//SootPlugin.getDefault().fireSootOutputEvent(se);         	
				//System.out.println(temp);
			}
			//System.out.println("exited while loop from gobbler");
		}
		catch(IOException e1) {
			System.out.println(e1.getMessage());
		}
	}
	
	/**
	 * Returns the is.
	 * @return InputStream
	 */
	public InputStream getIs() {
		return is;
	}

	/**
	 * Returns the type.
	 * @return int
	 */
	public int getType() {
		return type;
	}

	/**
	 * Sets the is.
	 * @param is The is to set
	 */
	public void setIs(InputStream is) {
		this.is = is;
	}

	/**
	 * Sets the type.
	 * @param type The type to set
	 */
	public void setType(int type) {
		this.type = type;
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

}
