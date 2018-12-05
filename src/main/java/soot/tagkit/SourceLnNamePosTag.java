package soot.tagkit;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Eric Bodden
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
 * @author Eric Bodden
 */
public class SourceLnNamePosTag extends SourceLnPosTag {

  protected final String fileName;

  public SourceLnNamePosTag(String fileName, int sline, int eline, int spos, int epos) {
    super(sline, eline, spos, epos);
    this.fileName = fileName;
  }

  /**
   * @return the fileName
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * {@inheritDoc}
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(super.toString());
    sb.append(" file: ");
    sb.append(fileName);
    return sb.toString();
  }

}
