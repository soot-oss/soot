package soot;

import java.util.*;
import javax.swing.*;
import java.io.*;

/** Represents a tag; these get attached to implementations of Host.
 */
public class ArrayCheckTag implements Tag 
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
    
    
    public String getName()
    {
	return NAME;
    }

    public byte[] getEncoding()
    {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	try {
	    ObjectOutputStream oos = new ObjectOutputStream(baos);
	    oos.write(value);
	    return baos.toByteArray();	    
	} catch (IOException e) {throw new RuntimeException("");}	
    }

    public String toString()
    {
	boolean doLower = false, doUpper = false;
	
	if((value & DO_LOWER) != 0)
	    doLower = true;
	if((value & DO_UPPER) != 0)
	    doUpper = true;

	return "// " + NAME + " "  + (doLower ? "[check lower bound]":"") + (doUpper ? "[check upper bound]":"");
    }
}
