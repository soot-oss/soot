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

package soot.tagkit;

import soot.*;

import java.util.*;

// extended by SootClass, SootField, SootMethod, Scene

/** 
 * This class is the reference implementation for
 * the Host interface, which allows arbitrary taggable
 * data to be stored with Soot objects. 
 */
public  class AbstractHost implements Host 
{
    List mTagList = new ArrayList(1);
    
    public List getTags()
    {
        return mTagList;
    }

    public void removeTag(String aName)
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
        
	return null;
    }

    public boolean hasTag(String aName)
    {
        return (searchForTag(aName) != -1);
    }
    
    public void addTag(Tag t)
    {
	mTagList.add(t);
    }
}




