package soot.tagkit;

import soot.*;

import java.util.*;
import java.io.*;



public class ArrayCheckTag implements Tag
{
    private final static String NAME = "ArrayCheckTag";

    private boolean lowerCheck = true;
    private boolean upperCheck = true;

    public ArrayCheckTag(boolean lower, boolean upper)
    {
	lowerCheck = lower;
	upperCheck = upper;
    }
           
    public boolean isCheckUpper()
    {
	return upperCheck;
    }
    public boolean isCheckLower()
    {
	return lowerCheck;
    }


    public String getName()
    {
	return NAME;
    }

    public String toString()
    {
	return   (lowerCheck ? "": "[safe lower bound]") +" " +  (upperCheck ? "":"[safe upper bound]");
    }
}
