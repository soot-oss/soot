package soot.asm;

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

import java.io.PrintStream;

/**
 * @author Timothy Hoffman
 */
public class LocalNaming {

  public void localNaming(String alpha, Integer beta, byte[] gamma, StringBuilder delta) {
    byte epsilon = 23;
    gamma[0] = epsilon;

    delta.append(alpha);
    delta.append(beta);

    long zeta = (long) 'Z';
    long iota = zeta * 2L;
    Long eta = Long.valueOf(iota);
    long theta = eta;

    delta.append(zeta);
    delta.append(theta);

    PrintStream omega = System.out;
    omega.println(delta);
  }

  static class Config {
    static int getD() {
      return 0;
    }

    static int getF() {
      return 1;
    }
  }

  public void test() {
    int d = Config.getD();
    int f = Config.getF();
    int[] arr = new int[2];
    arr[0] = d;
    arr[1] = f;
    System.out.println(arr);
  }
}
