/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot;

import java.util.*;

// extended by SootClass, SootField, SootMethod, Scene

/** This class is the reference implementation for
 * the Host interface, which allows arbitrary taggable
 * data to be stored with Soot objects. */
public abstract class AbstractHost implements Host 
{
    List mTagList = new ArrayList(1);
    
    public List getTags()
    {
        return mTagList;
    }

    public void destroyTag(String aName)
    {
        int tagIndex;
        if((tagIndex = searchForTag(aName)) != -1) {
            mTagList.remove(tagIndex);
        }
    }

    private int searchForTag(String aName) 
    {
        int i = 0;
        Iterator it = mTagList.iterator();
        while(it.hasNext()) {
            Tag tag = (Tag) it.next();
            if(tag.getName().equals(aName))
                return i;
            i++;
        }
        return -1;
    }

    public Tag getTag(String aName)
    {      
        int tagIndex;
        if((tagIndex = searchForTag(aName)) != -1) {
            return (Tag) mTagList.get(tagIndex);
        }
        
        throw new RuntimeException("Host doesn't have tag named:" + aName);
    }

    public boolean hasTag(String aName)
    {
        return (searchForTag(aName) != -1);
    }

    public Object getTagValue(String aName)
    {
        return getTag(aName).getValue();       
    }

    public void setTagValue(String aName, Object v)
    {
        getTag(aName).setValue(v);
    }

    public void incTagValue(String aName)
    {
        Tag tag = getTag(aName);
        Object value = tag.getValue();
        if(value instanceof Long) 
            tag.setValue(new Long( ((Long)value).longValue() +1));
        else if (value instanceof Double)
            tag.setValue(new Double( ((Double)value).doubleValue() +1));
        else
            throw new RuntimeException("Operation not supported on given tag type");        
    }

    public void incTagValue(String aName, long inc)
    {
        Tag tag = getTag(aName);
        Object value = tag.getValue();
        if(value instanceof Long) 
            tag.setValue(new Long( ((Long)value).longValue() + inc));
        else
            throw new RuntimeException("Tag must be long.");        
    }

    public void incTagValue(String aName, double inc)
    {
        Tag tag = getTag(aName);
        Object value = tag.getValue();
        if (value instanceof Double)
            tag.setValue(new Double( ((Double)value).doubleValue() +  inc));
        else
            throw new RuntimeException("Tag must be double.");        
    }
    
    
    public Tag newTag(String aName, Object v)
    {
        Tag tag = new  Tag(aName, v);
        if(hasTag(aName)) 
            throw new RuntimeException("tag having the same name already present: "+ aName);
            
        mTagList.add(tag);
        return tag;
    }


    public Tag newTag(String aName)
    {
        Tag tag = null;
        if(aName.endsWith(".l")) 
            tag = newTag(aName, new Long(0L));
        else if (aName.endsWith(".d")) 
            tag = newTag(aName, new Double(0.0));
        else if(aName.endsWith(".s")) 
            tag =newTag(aName, new String(""));
        else 
            throw new RuntimeException("Cannot create tag: name extension is invalid:" + aName );        

        return tag;
    }
    
}




