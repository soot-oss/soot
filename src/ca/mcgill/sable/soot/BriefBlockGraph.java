package ca.mcgill.sable.soot;

import java.util.*;
import java.io.*;


public class BriefBlockGraph extends BlockGraph 
{

    public  BriefBlockGraph(UnitBody body)
    {
	super(body, BRIEF);
    }
}


