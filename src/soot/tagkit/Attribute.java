package soot.tagkit;

import soot.*;
import java.util.*;

/** 
 *  Tags that are attached to the class file, field, method, or method body
 *  should implement this interface.
 */

public interface  Attribute extends Tag
{
    /** Sets the value of the attribute from a byte[]. */
    public void setValue(byte[] v);    
}
