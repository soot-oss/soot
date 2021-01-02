package soot.tagkit;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Jennifer Lhotak
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

/**
 * Represents an inner class attribute which can be attached to implementations of Host. It can be directly used to add
 * attributes to class files.
 */
public class InnerClassAttribute implements Tag {

  public static final String NAME = "InnerClassAttribute";

  private ArrayList<InnerClassTag> list;

  public InnerClassAttribute() {
    this.list = null;
  }

  public InnerClassAttribute(ArrayList<InnerClassTag> list) {
    this.list = list;
  }

  public String getClassSpecs() {
    if (list == null) {
      return "";
    } else {
      StringBuilder sb = new StringBuilder();
      for (InnerClassTag ict : list) {
        sb.append(".inner_class_spec_attr ");
        sb.append(ict.getInnerClass());
        sb.append(' ');
        sb.append(ict.getOuterClass());
        sb.append(' ');
        sb.append(ict.getShortName());
        sb.append(' ');
        sb.append(ict.getAccessFlags());
        sb.append(' ');
        sb.append(".end .inner_class_spec_attr ");
      }
      return sb.toString();
    }
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public byte[] getValue() throws AttributeValueException {
    return new byte[1];
  }

  public List<InnerClassTag> getSpecs() {
    return list == null ? Collections.<InnerClassTag>emptyList() : list;
  }

  public void add(InnerClassTag newt) {
    ArrayList<InnerClassTag> this_list = this.list;
    if (this_list == null) {
      this.list = this_list = new ArrayList<InnerClassTag>();
    } else {
      String newt_inner = newt.getInnerClass();
      int newt_accessFlags = newt.getAccessFlags();
      for (InnerClassTag ict : this_list) {
        if (newt_inner.equals(ict.getInnerClass())) {
          int ict_accessFlags = ict.getAccessFlags();
          if (ict_accessFlags != 0 && newt_accessFlags > 0 && ict_accessFlags != newt_accessFlags) {
            throw new RuntimeException("Error: trying to add an InnerClassTag twice with different access flags! ("
                + ict_accessFlags + " and " + newt_accessFlags + ")");
          }
          if (ict_accessFlags == 0 && newt_accessFlags != 0) {
            // The Dalvik parser may find an InnerClass annotation without accessFlags in the outer class
            // and then an annotation with the accessFlags in the inner class.
            // When we have more information about the accessFlags we update the InnerClassTag.
            this_list.remove(ict);
            this_list.add(newt);
          }
          return;
        }
      }
    }
    this_list.add(newt);
  }
}
