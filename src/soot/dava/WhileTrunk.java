package soot.dava;

import soot.*;
import java.util.*;

interface WhileTrunk extends Trunk
{
    public Trunk getLoop(); 
    public void setLoop(Trunk t);
    
    public Value getCondition();
    public void setCondition(Value v);
}
