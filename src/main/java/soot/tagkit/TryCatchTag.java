package soot.tagkit;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2012 Eric Bodden
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

import java.util.HashMap;
import java.util.Map;

import soot.Unit;

public class TryCatchTag implements Tag {

  public static final String NAME = "TryCatchTag";

  protected Map<Unit, Unit> handlerUnitToFallThroughUnit = new HashMap<Unit, Unit>();

  public void register(Unit handler, Unit fallThrough) {
    handlerUnitToFallThroughUnit.put(handler, fallThrough);
  }

  public Unit getFallThroughUnitOf(Unit handlerUnit) {
    return handlerUnitToFallThroughUnit.get(handlerUnit);
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public byte[] getValue() throws AttributeValueException {
    throw new UnsupportedOperationException();
  }
}