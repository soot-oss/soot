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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import soot.util.ConcurrentList;

// extended by SootClass, SootField, SootMethod, Scene

/**
 * This class is the reference implementation for the Host interface, which allows arbitrary taggable data to be stored with
 * Soot objects.
 */
public class AbstractHost implements Host {

  private final Object lock = new Object();

  protected int line;

  // avoid creating an empty list for each element, when it is not used
  // use lazy instantiation (in addTag) instead
  protected volatile ConcurrentList<Tag> mTagList = null;

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
    Iterator<Tag> it = mTagList.iterator();
    while (it.hasNext()) {
      Tag tag = it.next();
      if (tag != null && tag.getName().equals(aName)) {
        it.remove();
        break;
      }
    }
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
    if (mTagList != null) {
      for (Tag tag : mTagList) {
        if (tag != null && tag.getName().equals(aName)) {
          return tag;
        }
      }
    }
    return null;
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
    if (mTagList != null) {
      for (Tag tag : mTagList) {
        if (tag != null && tag.getName().equals(aName)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Add the given {@link Tag} to {@code this} {@link Host}.
   * 
   * @param t
   */
  @Override
  public void addTag(Tag t) {
    List<Tag> l = getOrCreateTagList();
    l.add(t);
  }

  private List<Tag> getOrCreateTagList() {
    ConcurrentList<Tag> l = mTagList;
    if (l == null) {
      synchronized (lock) {
        l = mTagList;
        if (l == null) {
          l = new ConcurrentList<Tag>();
          mTagList = l;
        }
      }
    }
    return l;
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
      getOrCreateTagList().addAll(tags);
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
    SourceLnPosTag tag = (SourceLnPosTag) getTag(SourceLnPosTag.NAME);
    return (tag == null) ? -1 : tag.startPos();
  }

  @Override
  public Tag getOrComputeTag(String aName, Supplier<Tag> supplier) {
    List<Tag> l = getOrCreateTagList();
    
    for (Tag p : l) {
      if (p != null && p.getName().equals(aName)) {
        return p;
      }
    }
    Tag newTag = supplier.get();
    mTagList.add(newTag);
    return newTag;
  }
}
