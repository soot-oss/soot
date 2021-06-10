package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2020 Timothy Hoffman
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
 * @author Timothy Hoffman
 */
public class LocalPackerTestInput {

  public void prefixVariableNames() {
    // NOTE: with Options.v().setPhaseOption("jb", "use-original-names:true"),
    // this method body will contain locals like "a" and "a#1" (i.e. the part
    // before the '#' character matches the exact name of another Local). If
    // the LocalPacker does not add all names (even those without '#') to the
    // "usedLocalNames" set, then the body will contain multiple locals with
    // the same name after running the LocalPacker becuase "a#1" will be
    // renamed to "a" because it did not consider that "a" is already used.
    {
      int a = getInt();
      int b = getInt();
      System.out.println(a + b);
    }

    {
      String a = getString();
      String b = getString();
      System.out.println(a + b);
    }

    {
      double a = getDouble();
      double b = getDouble();
      System.out.println(a + b);
    }

    {
      long a = getLong();
      long b = getLong();
      System.out.println(a + b);
    }
  }

  private static int getInt() {
    return 0;
  }

  private static String getString() {
    return "";
  }

  private static double getDouble() {
    return 0.0d;
  }

  private static long getLong() {
    return 1L;
  }
}
