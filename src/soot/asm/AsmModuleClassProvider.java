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

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;
import soot.ClassProvider;
import soot.ClassSource;
import soot.ModulePathSourceLocator;
import soot.SourceLocator;

import java.io.IOException;
import java.io.InputStream;

/**
 * Objectweb ASM class provider.
 *
 * @author Aaloan Miftah
 */
public class AsmModuleClassProvider implements ClassProvider {

    public ClassSource find(String cls) {
        String clsFileName = cls.substring(cls.lastIndexOf(":") + 1, cls.length()).replace('.', '/') + ".class";
        String modules = cls.substring(0, cls.lastIndexOf(":") + 1);
        String clsFile = modules + clsFileName;
        ModulePathSourceLocator.FoundFile file =
                ModulePathSourceLocator.v().lookUpInModulePath(clsFile);
        return file == null ? null : new AsmClassSource(cls, file);
    }


    public String getModuleName(InputStream data) {
        final String[] moduleName = {null};
        org.objectweb.asm.ClassVisitor visitor = new org.objectweb.asm.ClassVisitor(Opcodes.ASM6) {


            @Override
            public ModuleVisitor visitModule(String name, int access, String version) {
                moduleName[0] = name;
                return null;
            }
        };
        try {
            new ClassReader(data).accept(visitor, ClassReader.SKIP_FRAMES);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return moduleName[0];
    }
}
