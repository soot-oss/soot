package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;
import java.util.*;

public interface Inst extends Unit
{    
    public int getInCount();
    public int getOutCount();
    public int getNetCount();
    
    public int getInMachineCount();
    public int getOutMachineCount();
    public int getNetMachineCount();
    

    public String toBriefString();
    public String toBriefString(Map stmtToName);
    public String toBriefString(String indentation);
    public String toBriefString(Map stmtToName, String indentation);
    public String toString();
    public String toString(Map stmtToName);
    public String toString(String indentation);
    public String toString(Map stmtToName, String indentation);
}

