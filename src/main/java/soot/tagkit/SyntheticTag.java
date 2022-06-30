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

/**
 * Represents the synthetic attribute.
 */
public class SyntheticTag implements Tag {

  public static final String NAME = "SyntheticTag";

  public SyntheticTag() {
  }

  @Override
  public String toString() {
    return "Synthetic";
  }

  @Override
  public String getName() {
    return NAME;
  }

  public String getInfo() {
    return "Synthetic";
  }

  @Override
  public byte[] getValue() {
    throw new RuntimeException("SyntheticTag has no value for bytecode");
  }
}
