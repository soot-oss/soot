package soot.toDex;

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
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.writer.io.FileDataStore;
import org.jf.dexlib2.writer.pool.DexPool;

/**
 * @author Manuel Benz created on 26.09.17
 */
public class MultiDexBuilder {

  protected final Opcodes opcodes;
  protected final List<DexPool> dexPools = new LinkedList<>();
  protected DexPool curPool;

  public MultiDexBuilder(Opcodes opcodes) {
    this.opcodes = opcodes;
    newDexPool();
  }

  protected void newDexPool() {
    curPool = new DexPool(opcodes);
    dexPools.add(curPool);
  }

  public void internClass(final ClassDef clz) {
    curPool.mark();
    curPool.internClass(clz);
    if (hasOverflowed()) {
      // reset to state before overflow occurred
      curPool.reset();

      // we need a new dexpool
      newDexPool();

      // re-execute on new pool since the last execution was dropped
      // NOTE: We do not want to call internClass recursively here, this
      // might end in an endless loop
      // if the class is to large for a single dex file!
      curPool.internClass(clz);

      // If a single class causes an overflow, we're really out of luck
      if (curPool.hasOverflowed()) {
        throw new RuntimeException("Class is bigger than a single dex file can be");
      }
    }
  }

  protected boolean hasOverflowed() {
    if (!curPool.hasOverflowed()) {
      return false;
    }
    // We only support splitting for api versions since Lollipop (22).
    // Since Api 22, Art runtime is used which needs to extract all dex
    // files anyway. Thus,
    // we can pack classes arbitrarily and do not need to care about which
    // classes need to go together in
    // the same dex file.
    // For Dalvik (pre 22), it is important that at least the main dex file
    // (classes.dex) contains all needed
    // dependencies of the Main activity, which means that one would have to
    // determine necessary dependencies and
    // pack those explicitly in the first dex file.
    // (https://developer.android.com/studio/build/multidex.html,
    // http://www.fasteque.com/deep-dive-into-android-multidex/)
    if (!opcodes.isArt()) {
      throw new RuntimeException("Dex file overflow. Splitting not support for pre Lollipop Android (Api 22).");
    }

    return true;
  }

  /**
   * Writes all built dex files to the given folder.
   *
   * @param folder
   *          the output folder
   * @return File handles to all written dex files
   * @throws IOException
   *           when failed to create {@link FileDataStore}
   */
  public List<File> writeTo(String folder) throws IOException {
    final List<File> result = new ArrayList<>(dexPools.size());
    for (DexPool dexPool : dexPools) {
      int count = result.size();
      // name dex files: classes.dex, classes2.dex, classes3.dex, etc.
      File file = new File(folder, "classes" + (count == 0 ? "" : count + 1) + ".dex");
      result.add(file);
      FileDataStore fds = new FileDataStore(file);
      dexPool.writeTo(fds);
      fds.close();
    }
    return result;
  }
}
