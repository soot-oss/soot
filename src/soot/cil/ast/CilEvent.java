package soot.cil.ast;

/**
 * An event definition in CIL
 * 
 * @author Steven Arzt
 *
 */
public class CilEvent {
	
	private final String eventName;
	
	public CilEvent(String eventName) {
		this.eventName = eventName;
	}
	
	public String getEventName() {
		return this.eventName;
	}

}
