package soot.jimple.toolkits.pointer;
import soot.*;
import java.util.*;
import soot.toolkits.graph.*;

/** A body transformer that simply calls the CastCheckEliminator analysis. */
public class CastCheckEliminatorDumper extends BodyTransformer
{ 
    private static CastCheckEliminatorDumper instance = 
	new CastCheckEliminatorDumper();
    private CastCheckEliminatorDumper() {}

    public static CastCheckEliminatorDumper v() { return instance; }

    public String getDefaultOptions() { return ""; }

    protected void internalTransform(Body b, String phaseName, Map options)
    {
	CastCheckEliminator cce = new CastCheckEliminator( 
		new BriefUnitGraph( b ) );
    }
}


