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

import java.util.List;

// implemented by SootClass, SootField, SootMethod, Scene

/**
 * A "taggable" object. Implementing classes can have arbitrary labelled data attached to them.
 *
 * Currently, only classes, fields, methods and the Scene are Hosts.
 *
 * One example of a tag would be to store Boolean values, associated with array accesses, indicating whether bounds checks
 * can be omitted.
 *
 * @see Tag
 */
public interface Host {
  /** Gets a list of tags associated with the current object. */
  public List<Tag> getTags();

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
  public void addAllTagsOf(Host h);

  /**
   * Returns the Java source line number if available. Returns -1 if not.
   */
  public int getJavaSourceStartLineNumber();

  /**
   * Returns the Java source line column if available. Returns -1 if not.
   */
  public int getJavaSourceStartColumnNumber();
}
