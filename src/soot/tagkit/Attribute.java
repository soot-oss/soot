package soot.tagkit;

import soot.*;
import java.util.*;




/** 
 *  Tags that are meant to be translated into bytecode attributes should implement this 
 *  interface.
 */

public interface  Attribute extends Tag
{
    /**
     *  Sets the value of the attribute from a byte[]. Currently this is usualy done
     *  when reading in a classfile with Soot attributes in it.
     */
    public void setValue(byte[] v);    
}
