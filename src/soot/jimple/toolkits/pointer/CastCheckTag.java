package soot.jimple.toolkits.pointer;
import soot.tagkit.*;

/** Implements a tag that can be used to tell a VM whether a cast check can
 * be eliminated or not. */
public class CastCheckTag implements Tag {
    boolean eliminateCheck;
    CastCheckTag( boolean eliminateCheck ) {
	this.eliminateCheck = eliminateCheck;
    }
    public String getName() {
	return "CastCheckTag";
    }
    public byte[] getValue() {
	byte[] ret = new byte[1];
	ret[0] = (byte) ( eliminateCheck ? 1 : 0 );
	return ret;
    }
    public String toString() {
	if( eliminateCheck ) {
	    return "This cast check can be eliminated.";
	} else {
	    return "This cast check should NOT be eliminated.";
	}
    }
    public boolean canEliminateCheck() {
	return eliminateCheck;
    }
}

