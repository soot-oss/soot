/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-2014 Raja Vallee-Rai and others
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package soot.asm;

import soot.*;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;

/**
 * Objectweb ASM class provider.
 *
 * @author Aaloan Miftah
 */
public class AsmJava9ClassProvider implements ClassProvider {


    public ClassSource find(String cls) {
        String clsFile = cls.replace('.', '/') + ".class";
        FoundFile file = null;
        //here we go through all modules, since we are in classpath mode

        Path p = Paths.get(URI.create("jrt:/modules"));
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(p)) {
            for (Path entry : stream) {
                //check each module folder for the class
                file = ModulePathSourceLocator.v().lookUpInVirtualFileSystem(entry.toUri().toString(), clsFile);
                if (file != null)
                    break;
            }
        } catch (FileSystemNotFoundException ex) {
            System.out.println("Could not read my modules (perhaps not Java 9?).");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file == null ? null : new AsmClassSource(cls, file);

    }
}
