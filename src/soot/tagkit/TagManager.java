package soot.tagkit;
import soot.*;


import java.util.*;
import javax.swing.*;
import java.io.*;


public class TagManager
{
    private static TagPrinter tagPrinter = new StdTagPrinter();



    public static Tag getTagFor(String tagName) {
	try {
	    Class cc = Class.forName("soot.tagkit." + tagName);
	    return (Tag)  cc.newInstance();
	} 
	catch (ClassNotFoundException e) {
	    return null;
	} catch(IllegalAccessException e) {
	    throw new RuntimeException();
	} catch (InstantiationException e) {
	    throw new RuntimeException(e.toString());
	}	
    }


    public static void setTagPrinter(TagPrinter p) 
    {
	tagPrinter = p;
    }

    public static String print(String aClassName, String aFieldOrMtdSignature, Tag aTag)
    {
	return tagPrinter.print(aClassName, aFieldOrMtdSignature,  aTag);
    }
}



