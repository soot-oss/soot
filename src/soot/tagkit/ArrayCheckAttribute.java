package soot.tagkit;


import java.util.*;
import soot.baf.*;
import soot.*;

public class ArrayCheckAttribute
    implements Attribute, JasminAttribute
{    
    private List mTags;
    private List mUnits;

    private byte[] value;


    private final String name = "ArrayCheckAttribute";
    

    public String toString()
    {

	return name;
    }


    public String getName() { return name;}

    // needed to instanciate the class by it's name (ie see TagManager)
    public ArrayCheckAttribute(){}

    public ArrayCheckAttribute(List tags, List units)
    {
	mTags = tags;
	mUnits = units;
    }

    public byte[] getValue() throws AttributeValueException
    {	
	if(value == null)
	    throw new AttributeValueException();//PC have not yet been resolved");
	else
	    return value;
    }

    public void setValue(byte[] v)
    {
	value = v;       	    
    }
    
    private static byte[] getValue(ArrayCheckTag t)
    {
	byte[]  value = new byte[1];
	value[0] = 0;
	if(t.isCheckUpper()) 
	    value[0] |= 0X01;
	if(t.isCheckLower())
	   value[0] |= 0x02;	   
	return value;
    }

    public String getJasminValue(Map instToLabel)
    {       
	StringBuffer buf = new StringBuffer();
		
	if(mTags.size() != mUnits.size()) 
	    throw new RuntimeException("Sizes must match!");
	
	Iterator tagIt = mTags.iterator();
	Iterator unitIt = mUnits.iterator();
	while(tagIt.hasNext()) {
	    buf.append( "%" + instToLabel.get(unitIt.next()) + "%" + 
			new String(Base64.encode(ArrayCheckAttribute.getValue( (ArrayCheckTag) tagIt.next()))));
	}
	   	
	return "\"" + buf.toString() + "\"";
    }

    public List getUnitBoxes()
    {
	List unitBoxes = new ArrayList(mUnits.size());
	
	Iterator it = mUnits.iterator();
	while(it.hasNext()) {
	    unitBoxes.add(Baf.v().newInstBox((Unit) it.next()));
	}
	return unitBoxes;
    }


    public static byte[] decode(String attr, Hashtable labelToPc)
    {
	List attributeHunks = new LinkedList();
	int attributeSize = 0;

	StringTokenizer st = new StringTokenizer(attr, "%");
	boolean isLabel = false;
	if(attr.startsWith("%"))
	    isLabel = true;

	byte[] pcArray;
	while(st.hasMoreTokens()) {	    
	    String token = st.nextToken();
	    if(isLabel) {		
		Integer pc = (Integer) labelToPc.get(token);
		if(pc == null)
		    throw new RuntimeException();
		int pcvalue = pc.intValue();
		if(pcvalue > 65535) 
		    throw new RuntimeException();

		pcArray = new byte[2];
		pcArray[0] = 0;
		pcArray[1] = 1;
		attributeHunks.add(pcArray);
		attributeSize += 2;
	    } else {
		byte[] hunk = Base64.decode(token.toCharArray());		
		attributeSize += hunk.length;
		attributeHunks.add(hunk);
	    }
	    isLabel = !isLabel;	  
	}
	
	int index = 0;
	byte[] attributeValue = new byte[attributeSize];


	Iterator it = attributeHunks.iterator();
	while(it.hasNext()) {
	    byte[] hunk = (byte[]) it.next();
	    for(int i = 0; i < hunk.length; i++) {
		attributeValue[index++] = hunk[i];
	    }
	}

	if(index != (attributeSize))
	    throw new RuntimeException();

	return attributeValue;
    }

}
