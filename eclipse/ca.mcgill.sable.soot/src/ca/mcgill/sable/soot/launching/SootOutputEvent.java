package ca.mcgill.sable.soot.launching;

import java.util.EventObject;

/**
 * @author jlhotak
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SootOutputEvent extends EventObject {

	private int event_type;
	private String textToAppend;
	
	public void setTextToAppend(String text) {
		textToAppend = text;
	}
	
	public String getTextToAppend() {
		return textToAppend;
	}
	
	public SootOutputEvent(Object eventSource, int type) {
		super(eventSource);
		setEventType(type);
	}
	
	public void setEventType(int type) {
		event_type = type;
	}
	
	public int getEventType() {
		return event_type;
	}
}
