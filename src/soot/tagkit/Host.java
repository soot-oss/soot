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

// implemented by SootClass, SootField, SootMethod, Scene

/** A "taggable" object.
 * Implementing classes can have arbitrary labelled data attached to them.
 * 
 * Currently, only classes, fields, methods and the Scene are Hosts.
 *
 * One example of a tag would be to store Boolean values, associated with
 * array accesses, indicating whether bounds checks can be omitted.
 *
 * @see Tag
 */
public interface Host
{
    /** Gets a list of tags associated with the current object. */
    public List getTags();
    
    /** Returns the tag with the given name. */
    public Tag getTag(String aName);

  /** Adds a tag. */
    public void addTag(Tag t);

    /** Removes the first tag with the given name. */
    public void removeTag(String name);
   
    /** Returns true if this host has a tag with the given name. */
    public boolean hasTag(String aName);

    /** Removes all the tags from this host. */
    public void removeAllTags();

    /** Adds all the tags from h to this host. */
    public void addAllTagsOf( Host h );
}




