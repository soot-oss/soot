package ca.mcgill.sable.soot;

import java.util.*;
import ca.mcgill.sable.soot.*;
import java.io.*;


public class BriefBlockGraph extends BlockGraph 
{

    BriefBlockGraph(UnitBody body)
    {
	super(body, BRIEF);
    }
}


