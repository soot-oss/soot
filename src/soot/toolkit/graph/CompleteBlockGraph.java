package soot.toolkit.graph;

import java.util.*;
import soot.*;
import java.io.*;


public class CompleteBlockGraph extends BlockGraph 
{

    public CompleteBlockGraph(Body body)
    {
        super(body, COMPLETE);
    }
}


