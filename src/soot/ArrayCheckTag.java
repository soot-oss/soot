package soot;

import java.util.*;
import javax.swing.*;
import java.io.*;

/** Represents a tag; these get attached to implementations of Host.
 */
public class ArrayCheckTag extends CodeAttribute
{

    private final static String NAME = "ArrayCheckTag";
    private final static byte DO_LOWER = 1;
    private final static byte DO_UPPER = 2;
    
    private byte value;
    

    public ArrayCheckTag(boolean lowerCheck, boolean upperCheck)
    {
	if(lowerCheck) {
	    value |= DO_LOWER;
	} 
	if(upperCheck) {
	    value |= DO_UPPER;
	}
    }
    
    public ArrayCheckTag()
    {}

    
    public String getName()
    {
	return NAME;
    }

    public byte[] getEncoding()    
    {
	byte[] array = new byte[3];
	byte[] pc = getPc();
	array[0] = pc[0];
	array[1] = pc[1];
	array[2] = value;
	return array; 
    }

    public void setValue(byte[] value)
    {
	if(value.length !=  3) {
	    throw new RuntimeException();	    
	}
	setPc(value[0], value[1]);
	this.value = value[2];
    }

    public String toString()
    {
	boolean doLower = false, doUpper = false;
	
	if((value & DO_LOWER) != 0)
	    doLower = true;
	if((value & DO_UPPER) != 0)
	    doUpper = true;

	return   (doLower ? "": "[safe lower bound]") +" " +  (doUpper ? "":"[safe upper bound]");
    }
}
