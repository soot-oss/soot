package soot.jbco.name;

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

/**
 * Generates names that are compatible with Java identifiers.
 *
 * @author p.nesterovich
 * @since 21.03.18
 */
public interface NameGenerator {

  /**
   * According to JVM specification, the name is limited to 65535 characters by the 16-bit unsigned length item of the
   * CONSTANT_Utf8_info structure. As the limit is on the number of bytes and UTF-8 encodes some characters using two or
   * three bytes, we assume that in worst case number or characters is 65535 / 3
   */
  int NAME_MAX_LENGTH = 65_535 / 3;

  /**
   * Generates random name of required length that can be used as Java identifier.
   *
   * @param size
   *          the expected size
   * @return the name of expected length
   * @throws IllegalArgumentException
   *           when passed size is more than {@link NameGenerator#NAME_MAX_LENGTH}
   */
  String generateName(int size);

}
