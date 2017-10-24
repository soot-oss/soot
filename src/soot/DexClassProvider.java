package soot;
/* Soot - a J*va Optimization Framework
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
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

import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.ClassDef;
import soot.dexpler.DexFileProvider;
import soot.dexpler.Util;
import soot.options.Options;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Looks for a dex file which includes the definition of a class.
 */
public class DexClassProvider implements ClassProvider {

    public static Set<String> classesOfDex(DexBackedDexFile dexFile) {
        Set<String> classes = new HashSet<String>();
        for (ClassDef c : dexFile.getClasses()) {
            String name = Util.dottedClassName(c.getType());
            classes.add(name);
        }
        return classes;
    }

    /**
     * Provides the DexClassSource for the class.
     *
     * @param className class to provide.
     * @return a DexClassSource that defines the className named class.
     */
    public ClassSource find(String className) {
        ensureDexIndex();

        Map<String, File> index = SourceLocator.v().dexClassIndex();
        File file = index.get(className);
        if (file == null)
            return null;

        return new DexClassSource(className, file);
    }

    /**
     * Checks whether the dex class index needs to be (re)built and triggers the
     * build if necessary
     */
    protected void ensureDexIndex() {
        Map<String, File> index = SourceLocator.v().dexClassIndex();
        if (index == null) {
            index = new HashMap<String, File>();
            buildDexIndex(index, SourceLocator.v().classPath());
            SourceLocator.v().setDexClassIndex(index);
        }

        // Process the classpath extensions
        if (SourceLocator.v().getDexClassPathExtensions() != null) {
            buildDexIndex(SourceLocator.v().dexClassIndex(),
                    new ArrayList<>(SourceLocator.v().getDexClassPathExtensions()));
            SourceLocator.v().clearDexClassPathExtensions();
        }
    }

    /**
     * Build index of ClassName-to-File mappings.
     *
     * @param index     map to insert mappings into
     * @param classPath paths to index
     */
    private void buildDexIndex(Map<String, File> index, List<String> classPath) {
        for (String path : classPath) {
            try {
                for (DexFileProvider.DexContainer container : DexFileProvider.v().getDexFromSource(new File(path))) {
                    for (String className : classesOfDex(container.getBase())) {
                        if (!index.containsKey(className))
                            index.put(className, container.getFilePath());
                        else if(Options.v().verbose())
                            G.v().out.println(String.format("Warning: Duplicate of class '%s' found in dex file '%s' from source '%s'. Omitting class.",
                                    className, container.getDexName(), container.getFilePath().getCanonicalPath()));
                    }
                }
            } catch (IOException e) {
                G.v().out.println("Warning: IO error while processing dex file '" + path + "'");
                G.v().out.println("Exception: " + e);
            } catch (Exception e) {
                G.v().out.println("Warning: exception while processing dex file '" + path + "'");
                G.v().out.println("Exception: " + e);
            }
        }

    }
}

