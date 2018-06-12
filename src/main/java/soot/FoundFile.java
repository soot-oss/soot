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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FoundFile {
  private static final Logger logger = LoggerFactory.getLogger(FoundFile.class);
  protected File file;
  protected String entryName;
  protected ZipFile zipFile;
  protected ZipEntry zipEntry;
  protected List<InputStream> openedInputStreams;

  public FoundFile(ZipFile file, ZipEntry entry) {
    this();
    if (file == null || entry == null) {
      throw new IllegalArgumentException("Error: The archive and entry cannot be null.");
    }
    this.zipFile = file;
    this.zipEntry = entry;
  }

  public FoundFile(String archivePath, String entryName) {
    this();
    if (archivePath == null || entryName == null) {
      throw new IllegalArgumentException("Error: The archive path and entry name cannot be null.");
    }
    this.file = new File(archivePath);
    this.entryName = entryName;
  }

  public FoundFile(File file) {
    this();
    if (file == null) {
      throw new IllegalArgumentException("Error: The file cannot be null.");
    }
    this.file = file;
    this.entryName = null;
  }

  private FoundFile() {
    this.openedInputStreams = new ArrayList<InputStream>();
  }

  public String getFilePath() {
    return file.getPath();
  }

  public boolean isZipFile() {
    return entryName != null;
  }

  public File getFile() {
    return file;
  }

  public InputStream inputStream() {
    InputStream ret = null;
    if (!isZipFile()) {
      try {
        ret = new FileInputStream(file);
      } catch (Exception e) {
        throw new RuntimeException("Error: Failed to open a InputStream for the file at path '" + file.getPath() + "'.", e);
      }
    } else {
      if (zipFile == null) {
        try {
          zipFile = new ZipFile(file);
          zipEntry = zipFile.getEntry(entryName);
          if (zipEntry == null) {
            silentClose();
            throw new RuntimeException(
                "Error: Failed to find entry '" + entryName + "' in the archive file at path '" + file.getPath() + "'.");
          }
        } catch (Exception e) {
          silentClose();
          throw new RuntimeException(
              "Error: Failed to open the archive file at path '" + file.getPath() + "' for entry '" + entryName + "'.", e);
        }
      }

      InputStream stream = null;
      try {
        stream = zipFile.getInputStream(zipEntry);
        ret = doJDKBugWorkaround(stream, zipEntry.getSize());
      } catch (Exception e) {
        throw new RuntimeException("Error: Failed to open a InputStream for the entry '" + zipEntry.getName()
            + "' of the archive at path '" + zipFile.getName() + "'.", e);
      } finally {
        if (stream != null) {
          try {
            stream.close();
          } catch (IOException e) {
            // There's not much we can do here
            logger.debug(e.getMessage(), e);
          }
        }
      }
    }

    openedInputStreams.add(ret);
    return ret;
  }

  public void silentClose() {
    try {
      close();
    } catch (Exception e) {
      logger.debug(e.getMessage(), e);
    }
  }

  public void close() {
    // Try to close all opened input streams
    List<Exception> errs = new ArrayList<Exception>(0);
    for (InputStream is : openedInputStreams) {
      try {
        is.close();
      } catch (Exception e) {
        errs.add(e);// record errors for later
      }
    }
    openedInputStreams.clear();
    closeZipFile(errs);

    // Throw single exception combining all errors
    if (!errs.isEmpty()) {
      String msg = null;
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PrintStream ps = null;
      try {
        ps = new PrintStream(baos, true, "utf-8");
        ps.println("Error: Failed to close all opened resources. The following exceptions were thrown in the process: ");
        int i = 0;
        for (Throwable t : errs) {
          ps.print("Exception ");
          ps.print(i++);
          ps.print(": ");
          logger.error(t.getMessage(), t);
        }
        msg = new String(baos.toByteArray(), StandardCharsets.UTF_8);
      } catch (Exception e) {
        // Do nothing as this will never occur
      } finally {
        ps.close();
      }
      throw new RuntimeException(msg);
    }
  }

  protected void closeZipFile(List<Exception> errs) {
    // Try to close the opened zip file if it exists
    if (zipFile != null) {
      try {
        zipFile.close();
        errs.clear();// Successfully closed the archive so all input
        // streams were closed successfully also
      } catch (Exception e) {
        errs.add(e);
      }
      zipFile = null;// set to null no matter what
      zipEntry = null;// set to null no matter what
    }
  }

  private InputStream doJDKBugWorkaround(InputStream is, long size) throws IOException {
    int sz = (int) size;
    byte[] buf = new byte[sz];
    final int N = 1024;
    int ln = 0;
    int count = 0;
    while (sz > 0 && (ln = is.read(buf, count, Math.min(N, sz))) != -1) {
      count += ln;
      sz -= ln;
    }
    return new ByteArrayInputStream(buf);
  }
}
