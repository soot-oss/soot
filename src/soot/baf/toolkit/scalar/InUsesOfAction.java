package soot.baf.toolkit.scalar;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.*;
import soot.toolkit.scalar.*;


public class InUsesOfAction implements SimpleAction
{
    Unit mX, mY;
    Local mLocal;
    LocalDefs mDefs;
    
    public InUsesOfAction(Unit aX, Unit aY, Local aLocal, LocalDefs aDefs)
    {        
        LocalDefs mDefs = aDefs;
        Unit mX = aX;
        Unit mY = aY;
        Local mLocal = aLocal;        
    }

    
    
    public boolean eval()
    {
        
        List list = mDefs.getDefsOfAt(mLocal, mY);
        if(list != null) {
            Iterator it = list.iterator();
        
            while(it.hasNext()) {
                if(it.next() == mX) 
                    return true;
            }
        }
        return false;
    }    
}
