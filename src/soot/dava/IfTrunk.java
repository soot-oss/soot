package soot.dava;

import soot.*;
import java.util.*;

interface IfTrunk extends Trunk
{
    public Trunk getIf(); 
    public void setIf(Trunk t);
    
    public Value getCondition();
    public void setCondition(Value v);
}
