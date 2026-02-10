package soot.asm;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2012 Andreas Dann
 *
 * (c) 2012 University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 * Alexandre Bartel
 *
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

import javax.annotation.Nullable;

public class ScopeFinderTarget {

  public Object field;

  public static Object static_field;

  static {
    static_field = new Object();
  }

  public ScopeFinderTarget() {
    field = new Object();
  }

  public void method() {
    System.out.println("in method");
  }

  public static class Inner {

    public Object field;

    public static Object static_field;

    static {
      static_field = new Object();
    }

    public Inner() {
      field = new Object();
    }

    public void method() {
      System.out.println("in method");
    }

    public class InnerInner {
      public Object field;

      public InnerInner() {
        field = new Object();
      }

      public void method() {
        System.out.println("in method");
      }
    }
  }

  public ScopeFinderTarget(Object param) {
    field = param;
  }

  public void methodPara(String p1) {
    System.out.println(p1);
  }

  public void methodPara(String p1, String p2) {
    System.out.println(p1);
    System.out.println(p2);
  }
}
