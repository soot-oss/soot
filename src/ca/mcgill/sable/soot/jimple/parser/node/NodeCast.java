package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;

public class NodeCast implements Cast
{
    public final static NodeCast instance = new NodeCast();

    private NodeCast()
    {
    }

    public Object cast(Object o)
    {
        return (Node) o;
    }
}
