package soot.dava;

import java.util.*;

public class DEmptyTrunk extends AbstractTrunk implements EmptyTrunk
{
  public String toString(boolean isBrief, Map stmtToName, String indentation)
  {
    return indentation + ";";
  }

  public Object clone()
  {
    return Dava.v().newEmptyTrunk();
  }

}

