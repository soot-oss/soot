package ca.mcgill.sable.soot;

import java.util.*;

// implemented by SootClass, SootField, SootMethod, Scene

public interface Host
{
    public List getTags();

    public void destroyTag(String name);

    public Object getTagValue(String name);
    public void setTagValue(String name, Object v); 
    public void incTagValue(String name);
    public void incTagValue(String name, long inc);
    public void incTagValue(String name, double inc);

    
    public Tag newTag(String name, Object v);
    public Tag newTag(String name);

    public Tag getTag(String aName);
    public boolean hasTag(String aName);

}




