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

  /**
   * Get the {@link List} of {@link Tag Tags} on {@code this} {@link Host}. This list should not be modified!
   * 
   * @return
   */
  @Override
  public List<Tag> getTags() {
    return (mTagList == null) ? Collections.<Tag>emptyList() : mTagList;
  }

  /**
   * Remove the {@link Tag} named {@code aName} from {@code this} {@link Host}.
   * 
   * @param aName
   */
  @Override
  public void removeTag(String aName) {
    int tagIndex = searchForTag(aName);
    if (tagIndex != -1) {
      mTagList.remove(tagIndex);
    }
  }

  /**
   * Search for {@link Tag} named {@code aName}.
   */
  private int searchForTag(String aName) {
    if (mTagList != null) {
      int i = 0;
      for (Tag tag : mTagList) {
        if (tag != null && tag.getName().equals(aName)) {
          return i;
        }
        i++;
      }
    }
    return -1;
  }

  /**
   * Return the {@link Tag} named {@code aName} from {@code this} {@link Host} or {@code null} if there is no such
   * {@link Tag}.
   * 
   * @param aName
   * 
   * @return
   */
  @Override
  public Tag getTag(String aName) {
    int tagIndex = searchForTag(aName);
    return (tagIndex == -1) ? null : mTagList.get(tagIndex);
  }

  /**
   * Check if {@code this} {@link Host} has a {@link Tag} named {@code aName}.
   * 
   * @param aName
   * 
   * @return
   */
  @Override
  public boolean hasTag(String aName) {
    return (searchForTag(aName) != -1);
  }

  /**
   * Add the given {@link Tag} to {@code this} {@link Host}.
   * 
   * @param t
   */
  @Override
  public void addTag(Tag t) {
    if (mTagList == null) {
      mTagList = new ArrayList<Tag>(1);
    }
    mTagList.add(t);
  }

  /**
   * Removes all the tags from {@code this} {@link Host}.
   */
  @Override
  public void removeAllTags() {
    mTagList = null;
  }

  /**
   * Adds all the tags from the given {@link Host} to {@code this} {@link Host}.
   * 
   * @param h
   */
  @Override
  public void addAllTagsOf(Host h) {
    List<Tag> tags = h.getTags();
    if (!tags.isEmpty()) {
      if (mTagList == null) {
        mTagList = new ArrayList<Tag>(tags.size());
      }
      mTagList.addAll(tags);
    }
  }

  @Override
  public int getJavaSourceStartLineNumber() {
    if (line <= 0) {
      // get line from source
      SourceLnPosTag tag = (SourceLnPosTag) getTag(SourceLnPosTag.NAME);
      if (tag != null) {
        line = tag.startLn();
      } else {
        // get line from bytecode
        LineNumberTag tag2 = (LineNumberTag) getTag(LineNumberTag.NAME);
        line = (tag2 == null) ? -1 : tag2.getLineNumber();
      }
    }
    return line;
  }

  @Override
  public int getJavaSourceStartColumnNumber() {
    if (col <= 0) {
      // get line from source
      SourceLnPosTag tag = (SourceLnPosTag) getTag(SourceLnPosTag.NAME);
      col = (tag == null) ? -1 : tag.startPos();
    }
    return col;
  }
}
