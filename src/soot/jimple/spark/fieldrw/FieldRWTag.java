package soot.jimple.spark.fieldrw;
import soot.tagkit.*;
import java.util.*;
import soot.*;

/** Implements a tag that holds a list of fields read or written by a call. */
public abstract class FieldRWTag implements Tag {
    String fieldNames = new String();
    FieldRWTag( Set fields ) {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for( Iterator fieldIt = fields.iterator(); fieldIt.hasNext(); ) {
            final SootField field = (SootField) fieldIt.next();
            if( !first ) sb.append( "%" );
            first = false;
            sb.append( field.getDeclaringClass().getName() );
            sb.append( ":" );
            sb.append( field.getName() );
        }
        fieldNames = sb.toString();
    }
    public abstract String getName();
    public byte[] getValue() {
        byte[] bytes = fieldNames.getBytes();
        byte[] ret = new byte[bytes.length+2];
        ret[0] = (byte)( bytes.length/256 );
        ret[1] = (byte)( bytes.length%256 );
        System.arraycopy( bytes, 0, ret, 2, bytes.length );
        return ret;
    }
    public String toString() {
        return getName()+fieldNames;
    }
}

