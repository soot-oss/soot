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
     *   Returns the attribute's value as a raw byte[].
     *   If this cannot be accomplished (ie because the attribute has Unit
     *   references that have not been mapped to a PC value), a AttributeValueException 
     *   is thrown.
     */
    public byte[] getValue() throws AttributeValueException;

    /**
     *  Sets the value of the attribute from a byte[]. Currently this is usualy done
     *  when reading in a classfile with Soot attributes in it.
     */
    public void setValue(byte[] v);
    
    /**
     *  For attributes that hold references to Units, these must be returned wrapped
     *  in UnitBoxes before returning to an agent that queries for the references in a Body.
     *  (ie to generate the appropriete labels the JasminCode.
     *  @return A list of UnitBoxes containing the Units referenced by this attribute.
     */
    public List getUnitBoxes();
}
