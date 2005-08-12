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


package ca.mcgill.sable.soot.util;

import java.io.*;


import org.eclipse.swt.widgets.Display;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.launching.*;


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
		try {
			InputStreamReader isr = new InputStreamReader(getIs());
			BufferedReader br = new BufferedReader(isr);
			
			while (true) {
				String temp = br.readLine();
				
				if (temp == null) break;
		
				SootOutputEvent se = new SootOutputEvent(this, ISootOutputEventConstants.SOOT_NEW_TEXT_EVENT);
       			se.setTextToAppend(temp);
       			final SootOutputEvent toSend = se;

       			getDisplay().asyncExec(new Runnable(){
       				public void run() {
       					SootPlugin.getDefault().fireSootOutputEvent(toSend);
       					
       				};
       			});
       			se = new SootOutputEvent(this, ISootOutputEventConstants.SOOT_NEW_TEXT_EVENT);
       			se.setTextToAppend("\n");
       			final SootOutputEvent newline = se;
       			getDisplay().asyncExec(new Runnable(){
       				public void run() {
       					SootPlugin.getDefault().fireSootOutputEvent(newline);
       				};
       			});
 
			}
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
