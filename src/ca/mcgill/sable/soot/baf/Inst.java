package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;

public interface Inst extends Unit
{    
    public List getUseBoxes();
    public List getDefBoxes();
    public List getUnitBoxes();
    public List getBoxesPointingToThis();
    public List getUseAndDefBoxes();
    public void apply(Switch sw);
    public String toBriefString();
    public String toBriefString(Map stmtToName);
    public String toBriefString(String indentation);
    public String toBriefString(Map stmtToName, String indentation);
    public String toString();
    public String toString(Map stmtToName);
    public String toString(String indentation);
    public String toString(Map stmtToName, String indentation);
}

