package soot.dexpler;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.MultiDexContainer;
import soot.CompilationDeathException;
import soot.G;
import soot.Scene;
import soot.Singletons;
import soot.options.Options;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Class providing dex files from a given source, e.g., jar, apk, dex, etc.
 *
 * @author Manuel Benz
 * created on 16.10.17
 */
public class DexFileProvider {

    /**
     * Mapping of filesystem file (apk, dex, etc.) to mapping of dex name to dex file
     */
    private final Map<String, Map<String, DexBackedDexFile>> dexMap = new HashMap<>();

    public DexFileProvider(Singletons.Global g) {
    }

    public static DexFileProvider v() {
        return G.v().soot_dexpler_DexFileProvider();
    }

    /**
     * @param dexSourceFile Path to a jar, apk, dex, odex, etc. file
     * @return List of dex files derived from source file
     */
    public Map<String, DexBackedDexFile> getDexNameToFileMapping(File dexSourceFile) throws IOException {
        String key = dexSourceFile.getCanonicalPath();

        Map<String, DexBackedDexFile> dexFiles = dexMap.get(key);
        if (dexFiles == null) {
            try {
                dexFiles = init(dexSourceFile);
                dexMap.put(key, dexFiles);
            } catch (IOException e) {
                throw new CompilationDeathException("Error parsing dex source", e);
            }
        }
        return dexFiles;
    }

    /**
     * @param dexSourceFile Path to a jar, apk, dex, odex, etc. file
     * @return List of dex files derived from source file
     */
    public Collection<DexBackedDexFile> getDexFiles(File dexSourceFile) throws IOException {
        return getDexNameToFileMapping(dexSourceFile).values();
    }

    public DexBackedDexFile getDexInFile(File dexSourceFile, String fileName) throws IOException {
        DexBackedDexFile dexFile = getDexNameToFileMapping(dexSourceFile).get(fileName);
        if (dexFile == null)
            throw new CompilationDeathException("Dex file with name '" + fileName + "' not found in " + dexSourceFile);
        return dexFile;
    }

    private Map<String, DexBackedDexFile> init(File dexSourceFile) throws IOException {
        int api = Scene.v().getAndroidAPIVersion();
        boolean multiple_dex = Options.v().process_multiple_dex();

        // load dex files from apk/folder/file
        MultiDexContainer<? extends DexBackedDexFile> dexContainer = DexFileFactory.loadDexContainer(dexSourceFile, Opcodes.forApi(api));

        List<String> dexEntryNameList = dexContainer.getDexEntryNames();
        int dexFileCount = dexEntryNameList.size();

        if (dexFileCount < 1)
            throw new RuntimeException(String.format("No dex file found in '%s'", dexSourceFile));

        Map<String, DexBackedDexFile> dexMap = new HashMap<>(dexFileCount);

        // report found dex files and add to list.
        // We do this in reverse order to make sure that we add the first entry if there is no classes.dex file in single dex mode
        ListIterator<String> entryNameIterator = dexEntryNameList.listIterator(dexFileCount);
        while (entryNameIterator.hasPrevious()) {
            String entryName = entryNameIterator.previous();
            DexBackedDexFile entry = dexContainer.getEntry(entryName);
            G.v().out.println(String.format("Found dex file '%s' with %d classes in '%s'", entryName, entry.getClasses().size(), dexSourceFile.getName()));

            if (multiple_dex)
                dexMap.put(entryName, entry);
            else if (dexMap.isEmpty() && (entryName.equals("classes.dex") || !entryNameIterator.hasPrevious())) {
                // We prefer to have classes.dex in single dex mode.
                // If we haven't found a classes.dex until the last element, take the last!
                dexMap = Collections.singletonMap(entryName, entry);
                G.v().out.println("WARNING: Multiple dex files detected, only processing '" + entryName + "'. Use '-process-multiple-dex' option to process them all.");
            }
        }
        return Collections.unmodifiableMap(dexMap);
    }

}
