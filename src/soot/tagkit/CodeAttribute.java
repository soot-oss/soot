package soot.tagkit;

import java.util.*;
import soot.baf.*;
import soot.*;

/** The CodeAttribute holds the PC -> Tag pairs.
 */

public class CodeAttribute extends JasminAttribute
{
    private List mUnits;
    private List mTags;

    private byte[] value;
    
    private String name = "CodeAtribute";

    public CodeAttribute(){}
    
    /* Define the code attribute with name. */
    public CodeAttribute(String name)
    {
    	this.name = name;
    }

    /* Define the code attribute with name and lists of unit-tag pairs. */
    public CodeAttribute(String name, List units, List tags)
    {
    	this.name = name;
	this.mUnits = units;
	this.mTags = tags;
    }

    public String toString()
    {
	return name;
    }

    public String getName()
    {
	return name;
    }

    /* Only used by SOOT to read in an existing attribute without interpret it.*/
    public void setValue(byte[] v)
    {
    	this.value = v;
    }
    
    /* Also only as setValue(). */
    public byte[] getValue() throws AttributeValueException
    {
	if (value == null)
	    throw new AttributeValueException();
	else
	    return value;
    }

    /* Generate Jasmin Value String */
    public String getJasminValue(Map instToLabel)
    {
	StringBuffer buf = new StringBuffer();
	
	if (mTags.size() != mUnits.size())
	    throw new RuntimeException("Sizes must match!");
	
	Iterator tagIt = mTags.iterator();
	Iterator unitIt = mUnits.iterator();

	while (tagIt.hasNext())
	{
	    Object unit = unitIt.next();
	    Object tag = tagIt.next();

	    buf.append("%"+instToLabel.get(unit) + "%"+ 
		       new String(Base64.encode(((Tag)tag).getValue())));
	}
    
	return buf.toString();
    }

    public List getUnitBoxes()
    {
	List unitBoxes = new ArrayList(mUnits.size());
	
	Iterator it = mUnits.iterator();
	
	while(it.hasNext()) {
	    unitBoxes.add(Baf.v().newInstBox((Unit)it.next()));
	}

	return unitBoxes;
    }
}
	  
