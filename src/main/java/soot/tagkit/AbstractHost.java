package soot.tagkit;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

// extended by SootClass, SootField, SootMethod, Scene

/**
 * This class is the reference implementation for the Host interface, which allows arbitrary taggable data to be stored with
 * Soot objects.
 */
public class AbstractHost implements Host {

  protected int line, col;

  // avoid creating an empty list for each element, when it is not used
  // use lazy instantiation (in addTag) instead
  protected List<Tag> mTagList = null;

  /** get the list of tags. This list should not be modified! */
  @Override
  public List<Tag> getTags() {
    return (mTagList == null) ? Collections.<Tag>emptyList() : mTagList;
  }

  /** remove the tag named <code>aName</code> */
  @Override
  public void removeTag(String aName) {
    int tagIndex;
    if ((tagIndex = searchForTag(aName)) != -1) {
      mTagList.remove(tagIndex);
    }
  }

  /** search for tag named <code>aName</code> */
  private int searchForTag(String aName) {
    if (mTagList == null) {
      return -1;
    }

    int i = 0;
    Iterator<Tag> it = mTagList.iterator();
    while (it.hasNext()) {
      Tag tag = it.next();
      if (tag != null && tag.getName().equals(aName)) {
        return i;
      }
      i++;
    }
    return -1;
  }

  /** get the Tag object named <code>aName</code> */
  @Override
  public Tag getTag(String aName) {
    int tagIndex;
    if ((tagIndex = searchForTag(aName)) != -1) {
      return mTagList.get(tagIndex);
    }

    return null;
  }

  /** look if this host has a tag named <code>aName</code> */
  @Override
  public boolean hasTag(String aName) {
    return (searchForTag(aName) != -1);
  }

  /** add tag <code>t</code> to this host */
  @Override
  public void addTag(Tag t) {
    if (mTagList == null) {
      mTagList = new ArrayList<Tag>(1);
    }
    mTagList.add(t);
  }

  /** Removes all the tags from this host. */
  @Override
  public void removeAllTags() {
    mTagList = null;
  }

  /** Adds all the tags from h to this host. */
  @Override
  public void addAllTagsOf(Host h) {
    List<Tag> tags = h.getTags();
    if (tags.isEmpty()) {
      return;
    }

    if (mTagList == null) {
      mTagList = new ArrayList<Tag>(tags.size());
    }

    mTagList.addAll(tags);
  }

  @Override
  public int getJavaSourceStartLineNumber() {
    if (line <= 0) {
      // get line from source
      SourceLnPosTag tag = (SourceLnPosTag) getTag("SourceLnPosTag");
      if (tag != null) {
        line = tag.startLn();
      } else {
        // get line from bytecode
        LineNumberTag tag2 = (LineNumberTag) getTag("LineNumberTag");
        if (tag2 != null) {
          line = tag2.getLineNumber();
        } else {
          line = -1;
        }
      }
    }
    return line;
  }

  @Override
  public int getJavaSourceStartColumnNumber() {
    if (col <= 0) {
      // get line from source
      SourceLnPosTag tag = (SourceLnPosTag) getTag("SourceLnPosTag");
      if (tag != null) {
        col = tag.startPos();
      } else {
        col = -1;
      }
    }
    return col;
  }
}
