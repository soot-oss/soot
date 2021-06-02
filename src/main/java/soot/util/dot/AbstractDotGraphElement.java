package soot.util.dot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Sable Research Group
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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * @author Timothy Hoffman
 */
public abstract class AbstractDotGraphElement {

  private LinkedHashMap<String, DotGraphAttribute> attributes;

  /**
   * Sets an attribute for this dot element.
   * 
   * @param id
   *          the attribute id to be set
   * @param value
   *          the attribute value
   */
  public void setAttribute(String id, String value) {
    this.setAttribute(new DotGraphAttribute(id, value));
  }

  /**
   * Sets an attribute for this dot element.
   * 
   * @param attr
   *          {@link DotGraphAttribute} specifying the attribute name and value
   */
  public void setAttribute(DotGraphAttribute attr) {
    LinkedHashMap<String, DotGraphAttribute> attrs = this.attributes;
    if (attrs == null) {
      this.attributes = attrs = new LinkedHashMap<String, DotGraphAttribute>();
    }
    attrs.put(attr.getID(), attr);
  }

  /**
   * @return unmodifiable {@link Collection} of {@link DotGraphAttribute} for this {@link AbstractDotGraphElement}
   */
  public Collection<DotGraphAttribute> getAttributes() {
    LinkedHashMap<String, DotGraphAttribute> attrs = this.attributes;
    return attrs == null ? Collections.emptyList() : Collections.unmodifiableCollection(attrs.values());
  }

  /**
   * @param id
   * @return the {@link DotGraphAttribute} with the given {@code id}
   */
  public DotGraphAttribute getAttribute(String id) {
    LinkedHashMap<String, DotGraphAttribute> attrs = this.attributes;
    return attrs == null ? null : attrs.get(id);
  }

  /**
   * @param id
   * @return the value for the {@link DotGraphAttribute} with the given {@code id}
   */
  public String getAttributeValue(String id) {
    DotGraphAttribute attr = getAttribute(id);
    return attr == null ? null : attr.getValue();
  }

  /**
   * Removes the attribute with the given {@link id} from this dot element.
   * 
   * @param id
   */
  public void removeAttribute(String id) {
    LinkedHashMap<String, DotGraphAttribute> attrs = this.attributes;
    if (attrs != null) {
      attrs.remove(id);
    }
  }

  /**
   * Removes the given attribute from this dot element.
   * 
   * @param attr
   *          {@link DotGraphAttribute} specifying the attribute to remove
   */
  public void removeAttribute(DotGraphAttribute attr) {
    LinkedHashMap<String, DotGraphAttribute> attrs = this.attributes;
    if (attrs != null) {
      attrs.remove(attr.getID(), attr);
    }
  }

  /**
   * Removes all attributes from this dot element.
   */
  public void removeAllAttributes() {
    this.attributes = null;
  }

  /**
   * Sets the label for this dot element.
   * 
   * @param label
   *          a label string
   */
  public void setLabel(String label) {
    label = DotGraphUtility.replaceQuotes(label);
    label = DotGraphUtility.replaceReturns(label);
    this.setAttribute("label", "\"" + label + "\"");
  }

  /**
   * @return the label for this dot element
   */
  public String getLabel() {
    return this.getAttributeValue("label");
  }
}
