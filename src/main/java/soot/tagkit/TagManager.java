package soot.tagkit;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Patrice Pominville and Feng Qian
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

import soot.G;
import soot.Singletons;

/** Utility functions for tags. */
public class TagManager {
  public TagManager(Singletons.Global g) {
  }

  public static TagManager v() {
    return G.v().soot_tagkit_TagManager();
  }

  private TagPrinter tagPrinter = new StdTagPrinter();

  /**
   * Returns the Tag class with the given name.
   *
   * (This does not seem to be necessary.)
   */
  public Tag getTagFor(String tagName) {
    try {
      Class<?> cc = Class.forName("soot.tagkit." + tagName);
      return (Tag) cc.newInstance();
    } catch (ClassNotFoundException e) {
      return null;
    } catch (IllegalAccessException e) {
      throw new RuntimeException();
    } catch (InstantiationException e) {
      throw new RuntimeException(e.toString());
    }
  }

  /** Sets the default tag printer. */
  public void setTagPrinter(TagPrinter p) {
    tagPrinter = p;
  }

  /** Prints the given Tag, assuming that it belongs to the given class and field or method. */
  public String print(String aClassName, String aFieldOrMtdSignature, Tag aTag) {
    return tagPrinter.print(aClassName, aFieldOrMtdSignature, aTag);
  }
}
