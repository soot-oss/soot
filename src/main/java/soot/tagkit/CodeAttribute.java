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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Unit;
import soot.UnitBox;
import soot.baf.Baf;
import soot.options.Options;

/**
 * A CodeAttribute object holds PC -> Tag pairs. It represents abstracted attributes of Code_attribute such as
 * LineNumberTable, ArrayBoundsCheck.
 */
public class CodeAttribute extends JasminAttribute {
  private static final Logger logger = LoggerFactory.getLogger(CodeAttribute.class);

  protected List<Unit> mUnits;
  protected List<Tag> mTags;

  private final String name;
  private byte[] value;

  public CodeAttribute() {
    this("CodeAtribute");
  }

  /** Creates an attribute object with the given name. */
  public CodeAttribute(String name) {
    this.name = name;
  }

  /** Create an attribute object with the name and lists of unit-tag pairs. */
  public CodeAttribute(String name, List<Unit> units, List<Tag> tags) {
    this.name = name;
    this.mUnits = units;
    this.mTags = tags;
  }

  /** Returns the name. */
  @Override
  public String toString() {
    return name;
  }

  /** Returns the attribute name. */
  @Override
  public String getName() {
    return name;
  }

  /** Only used by SOOT to read in an existing attribute without interpret it. */
  @Override
  public void setValue(byte[] v) {
    this.value = v;
  }

  /** Also only used as setValue(). */
  @Override
  public byte[] getValue() throws AttributeValueException {
    if (value == null) {
      throw new AttributeValueException();
    } else {
      return value;
    }
  }

  /** Generates Jasmin Value String */
  @Override
  public String getJasminValue(Map<Unit, String> instToLabel) {
    if (mTags.size() != mUnits.size()) {
      throw new RuntimeException("Sizes must match!");
    }

    // some benchmarks fail because of the returned string larger than the possible buffer size.
    StringBuilder buf = new StringBuilder();
    Iterator<Unit> unitIt = mUnits.iterator();
    for (Tag tag : mTags) {
      Unit unit = unitIt.next();
      buf.append('%').append(instToLabel.get(unit));
      buf.append('%').append(new String(Base64.encode(tag.getValue())));
    }
    return buf.toString();
  }

  /** Returns a list of unit boxes that have tags attached. */
  public List<UnitBox> getUnitBoxes() {
    List<UnitBox> unitBoxes = new ArrayList<UnitBox>(mUnits.size());
    for (Unit next : mUnits) {
      unitBoxes.add(Baf.v().newInstBox(next));
    }
    return unitBoxes;
  }

  @Override
  public byte[] decode(String attr, Hashtable<String, Integer> labelToPc) {
    if (Options.v().verbose()) {
      logger.debug("[] JasminAttribute decode...");
    }

    List<byte[]> attributeHunks = new LinkedList<byte[]>();
    int attributeSize = 0, tablesize = 0;
    boolean isLabel = attr.startsWith("%");
    StringTokenizer st = new StringTokenizer(attr, "%");
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if (isLabel) {
        Integer pc = labelToPc.get(token);
        if (pc == null) {
          throw new RuntimeException("PC is null, the token is " + token);
        }

        int pcvalue = pc;
        if (pcvalue > 65535) {
          throw new RuntimeException("PC great than 65535, the token is " + token + " : " + pcvalue);
        }

        attributeHunks.add(new byte[] { (byte) (pcvalue & 0x0FF), (byte) ((pcvalue >> 8) & 0x0FF) });
        attributeSize += 2;
        tablesize++;
      } else {
        byte[] hunk = Base64.decode(token.toCharArray());
        attributeSize += hunk.length;
        attributeHunks.add(hunk);
      }
      isLabel = !isLabel;
    }

    /* first two bytes indicate the length of attribute table. */
    attributeSize += 2;
    byte[] attributeValue = new byte[attributeSize];
    {
      attributeValue[0] = (byte) ((tablesize >> 8) & 0x0FF);
      attributeValue[1] = (byte) (tablesize & 0x0FF);
    }
    int index = 2;
    for (byte[] hunk : attributeHunks) {
      for (byte element : hunk) {
        attributeValue[index++] = element;
      }
    }

    if (index != attributeSize) {
      throw new RuntimeException("Index does not euqal to attrubute size : " + index + " -- " + attributeSize);
    }

    if (Options.v().verbose()) {
      logger.debug("[] Jasmin.decode finished...");
    }

    return attributeValue;
  }
}
