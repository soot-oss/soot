package soot.asm.backend.targets;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Mustafa Senoglu
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
 * Target class for {@code InterfaceStaticCallTest}: calls an interface's static method and default method, so that
 * Soot's ASM backend must emit {@code InterfaceMethodref} constant pool entries for both.
 *
 * @see <a href="https://github.com/soot-oss/soot/issues/1917">issue #1917</a>
 */
public class InterfaceStaticCall {

  public static int callStatic() {
    return InterfaceWithStaticMethod.answer();
  }

  public int callDefault(InterfaceWithStaticMethod i) {
    return i.twice(21);
  }
}
