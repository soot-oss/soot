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
import soot.Value;
import soot.ValueBox;
import soot.jimple.DefinitionStmt;
import soot.jimple.internal.JimpleLocal;

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

  /**
   * Copies the {@link SourceLnPosTag}, {@link LineNumberTag} and {@link BytecodeOffsetTag}s from the given host to the given
   * ValueBox
   *
   * @param target
   *          The box to which the position tags should be copied
   * @param from
   *          The host from which the position tags should be copied
   * @return True if a copy was conducted, false otherwise
   */
  public boolean copyLineTags(Host target, Host from) {
    boolean res = false;

    Tag tag = from.getTag(SourceLnPosTag.NAME);
    if (tag != null) {
      target.addTag(tag);
      res = true;
    }

    tag = from.getTag(LineNumberTag.NAME);
    if (tag != null) {
      target.addTag(tag);
      res = true;
    }

    tag = from.getTag(BytecodeOffsetTag.NAME);
    if (tag != null) {
      target.addTag(tag);
      res = true;
    }

    return res;
  }

  /**
   * Copies the {@link SourceLnPosTag}, {@link LineNumberTag} and {@link BytecodeOffsetTag}s from the given definition
   * statement to the given ValueBox. Takes care to leave names of user defined local variables intact.
   *
   * @param target
   *          The box to which the position tags should be copied
   * @param from
   *          The host from which the position tags should be copied
   * @return True if a copy was conducted, false otherwise
   */
  public void copyLineTags(ValueBox usetarget, DefinitionStmt from) {
    // make sure to also retain user variables
    Value v = usetarget.getValue();
    if (v instanceof JimpleLocal) {
      JimpleLocal dest = (JimpleLocal) v;
      Value srcV = from.getLeftOp();
      if (srcV instanceof JimpleLocal) {
        JimpleLocal src = (JimpleLocal) srcV;
        if (src.isUserDefinedLocal() && !dest.isUserDefinedLocal()) {
          // Resolving duplicates is done later (AsmMethodSource.ensureUniqueNames)
          dest.setName(src.getName());
          dest.setUserDefinedLocal();
        }
      }
    }
    // we might have a def statement which contains a propagated constant itself as right-op. we
    // want to propagate the tags of this constant and not the def statement itself in this case.
    if (!copyLineTags(usetarget, from.getRightOpBox())) {
      copyLineTags(usetarget, (Host) from);
    }
  }
}
