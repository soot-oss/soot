package soot.tagkit;

import soot.*;

import java.util.*;
import java.io.*;



/**
 *  This class  must be extended  by Attributes that can 
 *  be emitted in Jasmin. The attributes must format their data
 *  in Base64 and if Unit references they may contain must be emitted as
 *  labels embedded and
 *  escaped in the attribute's Base64 data stream at the location where the value
 *  of their pc is to occur. For example:
<pre> 
    aload_1
    iload_2
    label2:
    iaload
 label3:
    iastore
    iinc 2 1
    label0:
    iload_2
    aload_0
    arraylength
 label4:
   if_icmplt label1
   return
 .code_attribute ArrayCheckAttribute "%label2%Aw==%label3%Ag==%label4%Ag=="

</pre>
 *
 */


public abstract class JasminAttribute implements Attribute
{
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

		pcArray[1] = (byte)(pcvalue&0x0FF);
				
		pcArray[0] = (byte)((pcvalue>>8)&0x0FF);

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

    
    abstract public String getJasminValue(Map instToLabel);
}
