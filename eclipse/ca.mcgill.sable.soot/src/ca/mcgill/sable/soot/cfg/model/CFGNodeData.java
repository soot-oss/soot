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
