package soot;

import java.util.*;
import javax.swing.*;
import java.io.*;


public class StdTagPrinter implements TagPrinter
{

    public String  print(String aClassName, String aFieldOrMtdSignature, Tag aTag)
    {
	String identifier = "<" + aClassName + 
	    (aFieldOrMtdSignature == null ? "": ":" + aFieldOrMtdSignature ) + ">" +
	    (aTag instanceof CodeAttribute ? "+" + ((CodeAttribute)aTag).getPcAsInt(): "") + "/" +
	    aTag.getName() + " ";


	byte[] encoding = 	    aTag.getEncoding();
	char[] value;
	
	if(aTag instanceof CodeAttribute) {
	    byte[] nopc = new byte[encoding.length -2];
	    for(int i = 0; i < nopc.length; i++ )
		nopc[i] = encoding[i + 2];	    
	    value = Base64.encode(nopc);
	} else {
	    value = Base64.encode(encoding);
	}
	return identifier + new String(value);
    }
}



