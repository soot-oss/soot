/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
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
    // avoid creating an empty list for each element, when it is not used
    // use lazy instantation (in addTag) instead
    private final static List emptyList = Collections.EMPTY_LIST;
    private List mTagList = emptyList;
    
    /** get the list of tags. This list should not be modified! */
    public List getTags()
    {
        return mTagList;
    }

    /** remove the tag named <code>aName</code> */
    public void removeTag(String aName)
    {
        int tagIndex;
        if((tagIndex = searchForTag(aName)) != -1) {
            mTagList.remove(tagIndex);
        }
    }

    /** search for tag named <code>aName</code> */
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

    /** get the Tag object named <code>aName</code> */
   public Tag getTag(String aName)
    {      
        int tagIndex;
        if((tagIndex = searchForTag(aName)) != -1) {
            return (Tag) mTagList.get(tagIndex);
        }
        
				return null;
    }

    /** look if this host has a tag named <code>aName</code> */ 
    public boolean hasTag(String aName)
    {
        return (searchForTag(aName) != -1);
    }
    
    /** add tag <code>t</code> to this host */
    public void addTag(Tag t)
    {
        if (mTagList == emptyList) 
            mTagList = new ArrayList(1);
        mTagList.add(t);
    }

    /** Removes all the tags from this host. */
    public void removeAllTags() {
        mTagList = emptyList;
    }

    /** Adds all the tags from h to this host. */
    public void addAllTagsOf( Host h ) {
        for( Iterator tIt = h.getTags().iterator(); tIt.hasNext(); ) {
            final Tag t = (Tag) tIt.next();
            addTag( t );
        }
    }
}




