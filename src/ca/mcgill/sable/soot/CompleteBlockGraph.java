package ca.mcgill.sable.soot;

import java.util.*;
import ca.mcgill.sable.soot.*;
import java.io.*;


public class CompleteBlockGraph extends BlockGraph 
{

    public CompleteBlockGraph(Body body)
    {
        super(body, COMPLETE);
    }
}


