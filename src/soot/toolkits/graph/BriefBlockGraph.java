package soot.toolkits.graph;

import java.util.*;
import java.io.*;
import soot.*;

public class BriefBlockGraph extends BlockGraph 
{

    public  BriefBlockGraph(Body body)
    {
        super(body, BRIEF);
    }
}


