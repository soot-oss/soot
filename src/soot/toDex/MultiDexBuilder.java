package soot.toDex;

import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.reference.FieldReference;
import org.jf.dexlib2.iface.reference.MethodReference;
import org.jf.dexlib2.iface.reference.StringReference;
import org.jf.dexlib2.iface.reference.TypeReference;
import org.jf.dexlib2.writer.io.FileDataStore;
import org.jf.dexlib2.writer.pool.DexPool;
import soot.Scene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Manuel Benz
 * created on 26.09.17
 */
public class MultiDexBuilder {

    private final Opcodes opcodes;
    private DexPool curPool;
    private List<DexPool> dexPools = new LinkedList<>();

    public MultiDexBuilder(Opcodes opcodes) {
        this.opcodes = opcodes;
        curPool = new DexPool(opcodes);
        dexPools.add(curPool);
    }

    public void internClass(final ClassDef clz) {
        safeIntern(new Executor() {
            @Override
            public void execute(DexPool pool) {
                pool.internClass(clz);
            }
        });
    }

    public void internMethod(final MethodReference mRef) {
        safeIntern(new Executor() {
            @Override
            public void execute(DexPool pool) {
                pool.methodSection.intern(mRef);
            }
        });
    }

    public void internType(final TypeReference tRef) {
        safeIntern(new Executor() {
            @Override
            public void execute(DexPool pool) {
                pool.typeSection.intern(tRef);
            }
        });
    }

    public void internString(final StringReference ref) {
        safeIntern(new Executor() {
            @Override
            public void execute(DexPool pool) {
                pool.stringSection.intern(ref);
            }
        });
    }


    public void internField(final FieldReference fieldRef) {
        safeIntern(new Executor() {
            @Override
            public void execute(DexPool pool) {
                pool.fieldSection.intern(fieldRef);
            }
        });
    }

    private void safeIntern(Executor e) {
        curPool.mark();
        e.execute(curPool);
        if (hasOverflowed())
            e.execute(curPool); // execute on new pool since the last execution was dropped
    }

    private boolean hasOverflowed() {
        if (!curPool.hasOverflowed())
            return false;
        // We only support splitting for api versions since Lollipop (22).
        // Since Api 22, Art runtime is used which needs to extract all dex files anyway. Thus,
        // we can pack classes arbitrarily and do not need to care about which classes need to go together in
        // the same dex file.
        // For Dalvik (pre 22), it is important that at least the main dex file (classes.dex) contains all needed
        // dependencies of the Main activity, which means that one would have to determine necessary dependencies and
        // pack those explicitly in the first dex file. (https://developer.android.com/studio/build/multidex.html, http://www.fasteque.com/deep-dive-into-android-multidex/)
        if (!opcodes.isArt())
            throw new RuntimeException("Dex file overflow. Splitting not support for pre Lollipop Android (Api 22).");

        // reset to state before overflow occurred
        curPool.reset();

        // we need a new dexpool
        curPool = new DexPool(opcodes);
        dexPools.add(curPool);
        return true;
    }

    /**
     * Writes all built dex files to the given folder.
     *
     * @param folder
     * @return File handles to all written dex files
     * @throws IOException
     */
    public List<File> writeTo(String folder) throws IOException {
        List<File> res = new ArrayList<>(dexPools.size());
        for (DexPool dexPool : dexPools) {
            int count = res.size();
            // name dex files: classes.dex, classes2.dex, classes3.dex, etc.
            File file = new File(folder, "classes" + (count == 0 ? "" : count + 1) + ".dex");
            res.add(file);
            FileDataStore fds = new FileDataStore(file);
            dexPool.writeTo(fds);
            fds.close();
        }
        return res;
    }

    private interface Executor {
        void execute(DexPool pool);
    }
}
