package soot.dexpler.instructions;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import org.jf.dexlib2.analysis.ClassPath;
import org.jf.dexlib2.iface.DexFile;
import org.jf.dexlib2.iface.Method;

/**
 * Interface for instructions that are only valid in optimized dex files (ODEX). These instructions require special handling
 * for de-odexing.
 *
 * @author Steven Arzt
 */
public interface OdexInstruction {

  /**
   * De-odexes the current instruction.
   *
   * @param parentFile
   *          The parent file to which the current ODEX instruction belongs
   */
  public void deOdex(DexFile parentFile, Method m, ClassPath cp);

}
