package soot.dava;

import soot.*;
import java.util.*;

public abstract class AbstractTrunk extends AbstractUnit implements Trunk 
{
    static final public List emptyList = Collections.unmodifiableList(new ArrayList());

    public List getChildren() 
    {
        return emptyList;
    }

    public boolean branches()
    {
        return false;
    }

    public boolean fallsThrough()
    {
        return true;
    }
}
