package soot.jimple.toolkits.pointer;
import soot.*;
import soot.jimple.toolkits.pointer.representations.*;

public class ValNode extends Node implements ReferenceVariable
{
    protected ValNode( Type t ) {
	super( t );
    }
}

