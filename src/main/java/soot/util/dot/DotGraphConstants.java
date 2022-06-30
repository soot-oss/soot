package soot.util.dot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Sable Research Group
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
 * Defines several constants used to generate a Dot graph.
 *
 * @author Feng Qian
 */

public interface DotGraphConstants {
  public static final String NODE_SHAPE_BOX = "box";
  public static final String NODE_SHAPE_ELLIPSE = "ellipse";
  public static final String NODE_SHAPE_CIRCLE = "circle";
  public static final String NODE_SHAPE_DIAMOND = "diamond";
  public static final String NODE_SHAPE_PLAINTEXT = "plaintext";

  public static final String NODE_STYLE_SOLID = "solid";
  public static final String NODE_STYLE_DASHED = "dashed";
  public static final String NODE_STYLE_DOTTED = "dotted";
  public static final String NODE_STYLE_BOLD = "bold";
  public static final String NODE_STYLE_INVISIBLE = "invis";
  public static final String NODE_STYLE_FILLED = "filled";
  public static final String NODE_STYLE_DIAGONALS = "diagonals";
  public static final String NODE_STYLE_ROUNDED = "rounded";

  public static final String EDGE_STYLE_DOTTED = "dotted";
  public static final String EDGE_STYLE_SOLID = "solid";

  public static final String GRAPH_ORIENT_PORTRAIT = "portrait";
  public static final String GRAPH_ORIENT_LANDSCAPE = "landscape";

}
