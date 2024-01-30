package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2014 Raja Vallee-Rai and others
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

import java.io.File;
import java.io.InputStream;
import java.util.zip.ZipFile;

public class ClassLoaderFoundFile implements IFoundFile {

  private String fileName;
  private ClassLoader classLoader;

  public ClassLoaderFoundFile(ClassLoader cl, String fileName) {
    this.fileName = fileName;
    this.classLoader = cl;
    
  }

  @Override
  public String getFilePath() {
    return fileName;
  }

  @Override
  public boolean isZipFile() {
    return false;
  }

  @Override
  public ZipFile getZipFile() {
    return null;
  }

  @Override
  public File getFile() {
    return null;
  }

  @Override
  public String getAbsolutePath() {
    throw new RuntimeException("Not supported");
  }

  @Override
  public InputStream inputStream() {
    return classLoader.getResourceAsStream(fileName);
  }

  @Override
  public void close() {

  }

}
