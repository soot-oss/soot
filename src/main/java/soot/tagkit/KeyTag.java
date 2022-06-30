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

public class KeyTag implements Tag {

  public static final String NAME = "KeyTag";

  private final int red;
  private final int green;
  private final int blue;
  private final String key;
  private final String analysisType;

  public KeyTag(int r, int g, int b, String k, String type) {
    this.red = r;
    this.green = g;
    this.blue = b;
    this.key = k;
    this.analysisType = type;
  }

  public KeyTag(int color, String k, String type) {
    switch (color) {
      case ColorTag.RED: {
        this.red = 255;
        this.green = 0;
        this.blue = 0;
        break;
      }
      case ColorTag.GREEN: {
        this.red = 45;
        this.green = 255;
        this.blue = 84;
        break;
      }
      case ColorTag.YELLOW: {
        this.red = 255;
        this.green = 248;
        this.blue = 35;
        break;
      }
      case ColorTag.BLUE: {
        this.red = 174;
        this.green = 210;
        this.blue = 255;
        break;
      }
      case ColorTag.ORANGE: {
        this.red = 255;
        this.green = 163;
        this.blue = 0;
        break;
      }
      case ColorTag.PURPLE: {
        this.red = 159;
        this.green = 34;
        this.blue = 193;
        break;
      }
      default: {
        this.red = 220;
        this.green = 220;
        this.blue = 220;
        break;
      }
    }
    this.key = k;
    this.analysisType = type;
  }

  public KeyTag(int color, String k) {
    this(color, k, null);
  }

  public int red() {
    return red;
  }

  public int green() {
    return green;
  }

  public int blue() {
    return blue;
  }

  public String key() {
    return key;
  }

  public String analysisType() {
    return analysisType;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public byte[] getValue() {
    return new byte[4];
  }
}
