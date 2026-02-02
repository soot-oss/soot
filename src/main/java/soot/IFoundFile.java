package soot;

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

import java.io.File;
import java.io.InputStream;
import java.util.zip.ZipFile;

import org.slf4j.LoggerFactory;

/**
 * This abstraction allows to use different a implementation of file access. It contains essentially the same methods as
 * {@link FoundFile}.
 * 
 * @author Marc Miltenberger
 */
public interface IFoundFile {
  @Deprecated
  /**
   * Returns the concrete file on disk if it is not within a zip file. Otherwise, the returned file is the zip file itself.
   * Note that it may return null in some circumstances.
   * 
   * @return the file on disk
   */
  public String getFilePath();

  /**
   * Returns true if and only if this file is contained within a zip file.
   */
  public boolean isZipFile();

  /**
   * Returns the zip file or null.
   * 
   * @return the zip file
   */
  public ZipFile getZipFile();

  /**
   * Returns the concrete file on disk if it is not within a zip file. Otherwise, the returned file is the zip file itself.
   * Note that it may return null in some circumstances.
   * 
   * @return the file on disk
   */
  public File getFile();

  /**
   * Returns the absolute path to the file or the zip file.
   * 
   * @return the absolute path to the file or the zip file.
   */
  public String getAbsolutePath();

  /**
   * Opens the file and returns a new input stream containing the file contents.
   * 
   * @return a fresh stream
   */
  public InputStream inputStream();

  /**
   * Closes all opened input streams.
   */
  public void close();

  /**
   * Closes the instance without throwing an exception.
   */
  public default void silentClose() {
    try {
      close();
    } catch (Exception e) {
      LoggerFactory.getLogger(getClass()).debug(e.getMessage(), e);
    }
  }

}
