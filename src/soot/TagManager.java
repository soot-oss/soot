package soot;

import java.util.*;
import javax.swing.*;
import java.io.*;


public class TagManager
{
    private static TagPrinter tagPrinter = new StdTagPrinter();

    public static void setTagPrinter(TagPrinter p) 
    {
	tagPrinter = p;
    }

    public static String print(String aClassName, String aFieldOrMtdSignature, Tag aTag)
    {
	return tagPrinter.print(aClassName, aFieldOrMtdSignature,  aTag);
    }
}



