package soot.jimple.toolkits.pointer;
import soot.*;
import java.util.*;
import soot.tagkit.*;

public class DependenceTag implements Tag
{
    private final static String NAME = "DependenceTag";
    protected short read = -1;
    protected short write = -1;
    protected boolean callsNative = false;
    
    public boolean setCallsNative() {
	boolean ret = !callsNative;
	callsNative = true;
	return ret;
    }

    protected void setRead( short s ) {
	read = s;
    }

    protected void setWrite( short s ) {
	write = s;
    }

    public String getName()
    {
	return NAME;
    }

    public byte[] getValue() {
	byte[] ret = new byte[5];
	ret[0] = (byte) ( (read >> 8) & 0xff );
	ret[1] = (byte) ( read  & 0xff );
	ret[2] = (byte) ( (write >> 8) & 0xff );
	ret[3] = (byte) ( write  & 0xff );
	ret[4] = (byte) ( callsNative ? 1 : 0 );
	return ret;
    }

    public String toString()
    {
	StringBuffer buf = new StringBuffer();
	if( callsNative ) buf.append( "SECallsNative\n" );
	if( read >= 0 ) buf.append( "SEReads : "+read+"\n" );
	if( write >= 0 ) buf.append( "SEWrites: "+write+"\n" );
	return buf.toString();
    }
}
