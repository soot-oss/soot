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
package soot.asm;

import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import soot.ClassProvider;
import soot.ClassSource;
import soot.FoundFile;
import soot.ModulePathSourceLocator;

/**
 * Objectweb ASM class provider.
 *
 * @author Andreas Dann
 */
public class AsmJava9ClassProvider implements ClassProvider {

  public ClassSource find(String cls) {
    String clsFile = cls.replace('.', '/') + ".class";
    FoundFile file = null;
    // here we go through all modules, since we are in classpath mode

    Path p = ModulePathSourceLocator.getRootModulesPathOfJDK();
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(p)) {
      for (Path entry : stream) {
        // check each module folder for the class
        file = ModulePathSourceLocator.v().lookUpInVirtualFileSystem(entry.toUri().toString(), clsFile);
        if (file != null) {
          break;
        }
      }
    } catch (FileSystemNotFoundException ex) {
      System.out.println("Could not read my modules (perhaps not Java 9?).");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return file == null ? null : new AsmClassSource(cls, file);

  }
}
