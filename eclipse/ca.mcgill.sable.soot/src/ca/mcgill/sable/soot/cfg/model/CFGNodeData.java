/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
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

/*
 * Created on Feb 26, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.cfg.model;
import java.util.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CFGNodeData extends CFGElement {

	private ArrayList text;
	private boolean head;
	private boolean tail;
	
	/**
	 * 
	 */
	public CFGNodeData() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return
	 */
	public ArrayList getText() {
		return text;
	}

	/**
	 * @param list
	 */
	public void setText(ArrayList list) {
		text = list;
		//System.out.println("about to send TEXT event");
		firePropertyChange(TEXT, text);
	}

	/**
	 * @return
	 */
	public boolean isHead() {
		return head;
	}

	/**
	 * @return
	 */
	public boolean isTail() {
		return tail;
	}

	/**
	 * @param b
	 */
	public void setHead(boolean b) {
		head = b;
		firePropertyChange(HEAD, new Boolean(head));
	}

	/**
	 * @param b
	 */
	public void setTail(boolean b) {
		tail = b;
		firePropertyChange(TAIL, new Boolean(tail));
	}

}
