package soot.tagkit;


import soot.*;
import java.util.*;
import javax.swing.*;
import java.io.*;


public class StdTagPrinter implements TagPrinter
{
    public String  print(String aClassName, String aFieldOrMtdSignature, Tag aTag)
    {
	return aTag.toString();	
    }
}



