package soot.dotnet;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.ClassProvider;
import soot.ClassSource;
import soot.SourceLocator;
import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.dotnet.types.DotnetBasicTypes;
import soot.options.Options;

import java.io.File;
import java.util.*;

/**
 * This ClassProvider provides the assembly files with their types as ClassSource
 * SourceLocator -> ClassProvider -> ClassSource -> MethodSource
 */
public class DotnetClassProvider implements ClassProvider {
    private static final Logger logger = LoggerFactory.getLogger(DotnetClassProvider.class);

    /**
     * Return the ClassSource of requested class
     * @param className requested class
     * @return class source of the class
     */
    @Override
    public ClassSource find(String className) {
        ensureAssemblyIndex();

        // if fake LdFtn instruction
        if (className.equals(DotnetBasicTypes.FAKE_LDFTN))
            return new DotnetClassSource(className, null);

        File assemblyFile = SourceLocator.v().classContainerFileClassIndex().get(className);
        return assemblyFile == null ? null : new DotnetClassSource(className, assemblyFile);
    }

    /**
     * Generate index of all assembly files with their types. An assembly file contains several types in one file
     */
    private void ensureAssemblyIndex() {
        Map<String, File> index = SourceLocator.v().classContainerFileClassIndex();
        if (index == null) {
            if (Options.v().verbose())
                logger.info("Creating assembly index");
            index = new HashMap<>();
            buildAssemblyIndex(index, SourceLocator.v().classPath());
            SourceLocator.v().setClassContainerFileClassIndex(index);
            if (Options.v().verbose())
                logger.info("Created assembly index");
        }

        // Process the classpath extensions
        if (SourceLocator.v().getClassContainerFileClassPathExtensions() != null) {
            if (Options.v().verbose())
                logger.info("Process classpath extensions");
            buildAssemblyIndex(index, new ArrayList<>(SourceLocator.v().getClassContainerFileClassPathExtensions()));
            SourceLocator.v().clearClassContainerFileClassPathExtensions();
        }
    }

    /**
     * Build index of ClassName-to-File mappings.
     *
     * @param index
     *          map to insert mappings into
     * @param classPath
     *          paths to index
     */
    private void buildAssemblyIndex(Map<String, File> index, List<String> classPath) {
        if (Strings.isNullOrEmpty(Options.v().dotnet_nativehost_path()))
            throw new RuntimeException("Dotnet NativeHost Path is not set! Use -dotnet-nativehost-path Soot parameter!");

        for (String path : classPath) {
            try {
                File file = new File(path);
                if (file.exists()) {
                    // if classpath is only directory, look for dll/exe inside dir to add to index - only one hierarchical step
                    File[] listFiles = file.isDirectory() ? file.listFiles(File::isFile) : new File[]{file};
                    for (File f : Objects.requireNonNull(listFiles)) {
                        if (Options.v().verbose())
                            logger.info("Process " + f.getCanonicalPath() + " file");
                        // Check if given assembly is dll or exe and is assembly
                        if (!f.getCanonicalPath().endsWith(".exe") && !f.getCanonicalPath().endsWith(".dll"))
                            continue;
                        AssemblyFile assemblyFile = new AssemblyFile(f.getCanonicalPath());
                        if (!assemblyFile.isAssembly())
                            continue;

                        // Get all classes of given assembly
                        ProtoAssemblyAllTypes.AssemblyAllTypes assemblyDefinition = assemblyFile.getAllTypes();
                        if (assemblyDefinition == null)
                            continue;
                        // save later computation and calls of nativehost
                        if (!index.containsKey(f.getCanonicalPath()))
                            index.put(f.getCanonicalPath(), assemblyFile);
                        List<ProtoAssemblyAllTypes.TypeDefinition> allTypesOfMainModule = assemblyDefinition.getListOfTypesList();
                        for (ProtoAssemblyAllTypes.TypeDefinition type : allTypesOfMainModule) {
                            String typeName = type.getFullname();
                            if (Options.v().verbose())
                                logger.info("Add class " + typeName + " to index");

                            if (!index.containsKey(typeName)) {
                                index.put(typeName, assemblyFile);
                            } else if (Options.v().verbose()) {
                                logger.debug("" + String.format(
                                        "Warning: Duplicate of class '%s' found in assembly file '%s' from source '%s'. Omitting class.", type,
                                        assemblyFile.getAssemblyFileName(), assemblyFile.getFullPath()));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.warn("exception while processing assembly file '" + path + "'");
                logger.warn("Exception: " + e);
            }
        }

    }
}
