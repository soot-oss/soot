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


package ca.mcgill.sable.soot.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;

import ca.mcgill.sable.soot.SootPlugin;


public class PopupListSelector {
	private Shell shell;
	private List list;
	private String selected; 
	private int minimumWidth;
	

	public PopupListSelector(Shell parent){
	
		shell = new Shell(parent, 0);
	
		list = new List(shell, SWT.SINGLE | SWT.V_SCROLL);	
		list.setBackground(SootPlugin.getDefault().getColorManager().getColor(new RGB(255, 255, 255)));
		list.setFont(SootPlugin.getDefault().getSootFont());
		
		// close dialog if user selects outside of the shell
		shell.addListener(SWT.Deactivate, new Listener() {
			public void handleEvent(Event e){	
				shell.setVisible (false);
			};
		});
		
		// resize shell when list resizes
		shell.addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent e){}
			public void controlResized(ControlEvent e){
				Rectangle shellSize = shell.getClientArea();
				list.setSize(shellSize.width, shellSize.height);
			}
		});
		
		// return list selection on Mouse Up or Carriage Return
		list.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e){};
			public void mouseDown(MouseEvent e){};
			public void mouseUp(MouseEvent e){
				setSelected(list.getSelection()[0]);
				shell.setVisible (false);
				};
			});
			
		list.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e){};
			public void keyPressed(KeyEvent e){
				if (e.character == '\r'){
					shell.setVisible (false);
				}
			};
		});
		
		
	}
	
	public String open (Rectangle rect) {

		Point listSize = getList().computeSize (rect.width, SWT.DEFAULT);
		Rectangle screenSize = getShell().getDisplay().getBounds();

		// Position the dialog so that it does not run off the screen and the largest number of items are visible
		int spaceBelow = screenSize.height - (rect.y + rect.height) - 30;
		int spaceAbove = rect.y - 30;

		int y = 0;
		if (spaceAbove > spaceBelow && listSize.y > spaceBelow) {
			// place popup list above table cell
			if (listSize.y > spaceAbove){
				listSize.y = spaceAbove;
			} else {
				listSize.y += 2;
			}
			y = rect.y - listSize.y;
		
		} else {
			// place popup list below table cell
			if (listSize.y > spaceBelow){
				listSize.y = spaceBelow;
			} else {
				listSize.y += 2;
			}
			y = rect.y + rect.height;
		}
	
		// Make dialog as wide as the cell
		listSize.x = rect.width;
		// dialog width should not be les than minimumwidth
		if (listSize.x < getMinimumWidth())
			listSize.x = getMinimumWidth();
	
		// Align right side of dialog with right side of cell
		int x = rect.x + rect.width - listSize.x;
		y = 0;
		if (spaceAbove <= spaceBelow){
			y = spaceAbove + rect.y;
		}
		else {
			y = rect.y - spaceBelow;
		}
		
		y = (rect.y * 16) + 85;
	
	
		getShell().setBounds(rect.x, y, listSize.x, listSize.y);
	
		shell.open();
		list.setFocus();
		

		Display display = shell.getDisplay();
		while (!shell.isDisposed () && shell.isVisible ()) {
			if (!display.readAndDispatch()) display.sleep();
		}
	
		String result = null;
		if (!shell.isDisposed ()) {
			String [] strings = list.getSelection ();
			shell.dispose();
			if (strings.length != 0) result = strings [0];
		}
		return result;
	}
	
	public void setItems (String[] strings) {
		list.setItems(strings);
	}
	/**
	* Sets the minimum width of the list.
	*
	* @param width the minimum width of the list
	*/
	public void setMinimumWidth (int width) {
		minimumWidth = width;
	}
	
	/**
	 * @return
	 */
	public List getList() {
		return list;
	}

	/**
	 * @return
	 */
	public Shell getShell() {
		return shell;
	}

	/**
	 * @param list
	 */
	public void setList(List list) {
		this.list = list;
	}

	/**
	 * @param shell
	 */
	public void setShell(Shell shell) {
		this.shell = shell;
	}

	/**
	 * @return
	 */
	public String getSelected() {
		return selected;
	}

	/**
	 * @param string
	 */
	public void setSelected(String string) {
		selected = string;
	}

	/**
	 * @return
	 */
	public int getMinimumWidth() {
		return minimumWidth;
	}

}
