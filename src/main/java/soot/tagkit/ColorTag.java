package soot.tagkit;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jennifer Lhotak
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

import java.awt.Color;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColorTag implements Tag {
  private static final Logger logger = LoggerFactory.getLogger(ColorTag.class);

  public static final String NAME = "ColorTag";

  public static final int RED = 0;
  public static final int GREEN = 1;
  public static final int YELLOW = 2;
  public static final int BLUE = 3;
  public static final int ORANGE = 4;
  public static final int PURPLE = 5;

  private static final boolean DEFAULT_FOREGROUND = false;
  private static final String DEFAULT_ANALYSIS_TYPE = "Unknown";

  /* it is a value representing red. */
  private final int red;
  /* it is a value representing green. */
  private final int green;
  /* it is a value representing blue. */
  private final int blue;
  /* for highlighting foreground of text default is to higlight background */
  private final boolean foreground;
  private final String analysisType;

  public ColorTag(Color c) {
    this(c.getRed(), c.getGreen(), c.getBlue(), DEFAULT_FOREGROUND, DEFAULT_ANALYSIS_TYPE);
  }

  public ColorTag(int r, int g, int b) {
    this(r, g, b, DEFAULT_FOREGROUND, DEFAULT_ANALYSIS_TYPE);
  }

  public ColorTag(int r, int g, int b, boolean fg) {
    this(r, g, b, fg, DEFAULT_ANALYSIS_TYPE);
  }

  public ColorTag(int r, int g, int b, String type) {
    this(r, g, b, DEFAULT_FOREGROUND, type);
  }

  public ColorTag(int r, int g, int b, boolean fg, String type) {
    this.red = r;
    this.green = g;
    this.blue = b;
    this.foreground = fg;
    this.analysisType = type;
  }

  public ColorTag(int color) {
    this(color, DEFAULT_FOREGROUND, DEFAULT_ANALYSIS_TYPE);
  }

  public ColorTag(int color, String type) {
    this(color, DEFAULT_FOREGROUND, type);
  }

  public ColorTag(int color, boolean fg) {
    this(color, fg, DEFAULT_ANALYSIS_TYPE);
  }

  public ColorTag(int color, boolean fg, String type) {
    switch (color) {
      case RED: {
        this.red = 255;
        this.green = 0;
        this.blue = 0;
        break;
      }
      case GREEN: {
        this.red = 45;
        this.green = 255;
        this.blue = 84;
        break;
      }
      case YELLOW: {
        this.red = 255;
        this.green = 248;
        this.blue = 35;
        break;
      }
      case BLUE: {
        this.red = 174;
        this.green = 210;
        this.blue = 255;
        break;
      }
      case ORANGE: {
        this.red = 255;
        this.green = 163;
        this.blue = 0;
        break;
      }
      case PURPLE: {
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
    this.foreground = fg;
    this.analysisType = type;
  }

  public String getAnalysisType() {
    return analysisType;
  }

  public int getRed() {
    return red;
  }

  public int getGreen() {
    return green;
  }

  public int getBlue() {
    return blue;
  }

  public boolean isForeground() {
    return foreground;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public byte[] getValue() {
    return new byte[2];
  }

  @Override
  public String toString() {
    return "" + red + " " + green + " " + blue;
  }
}
