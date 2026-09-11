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

package soot.toDex;

import com.android.tools.smali.dexlib2.Opcode;
import com.android.tools.smali.dexlib2.Opcodes;
import com.android.tools.smali.dexlib2.builder.BuilderInstruction;
import com.android.tools.smali.dexlib2.builder.MutableMethodImplementation;
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction31c;
import com.android.tools.smali.dexlib2.iface.ClassDef;
import com.android.tools.smali.dexlib2.iface.Method;
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction;
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction;
import com.android.tools.smali.dexlib2.iface.reference.StringReference;
import com.android.tools.smali.dexlib2.writer.io.DeferredOutputStream;
import com.android.tools.smali.dexlib2.writer.io.DeferredOutputStreamFactory;
import com.android.tools.smali.dexlib2.writer.io.FileDataStore;
import com.android.tools.smali.dexlib2.writer.io.MemoryDeferredOutputStream;
import com.android.tools.smali.dexlib2.writer.pool.DexPool;
import com.android.tools.smali.dexlib2.writer.pool.StringPool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Manuel Benz created on 26.09.17
 */
public class MultiDexBuilder {
  private static final Logger logger = LoggerFactory.getLogger(MultiDexBuilder.class);

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
      logger.warn("Dex file overflow. Splitting is not supported for any version earlier than"
          + "Lollipop Android (API 22). The application will not run on older devices.");
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
      dexPool.writeTo(fds, new DeferredOutputStreamFactory() {
        boolean first = true;

        @Override
        public DeferredOutputStream makeDeferredOutputStream() {
          if (first) {
            fixJumboStringInstructions();
            first = false;
          }
          return new MemoryDeferredOutputStream(16 * 1024);
        }
      });
      fds.close();
    }
    return result;
  }

  /**
   * Fixes Jumbo String instructions. While dexlib can also fix them, they encounter a problem due to the immutability. Thus,
   * we fix it here.
   */
  private void fixJumboStringInstructions() {
    StringPool stringSection = curPool.stringSection;
    for (ClassDef clz : curPool.classSection.getSortedClasses()) {
      for (Method m : clz.getMethods()) {

        MutableMethodImplementation t = (MutableMethodImplementation) m.getImplementation();
        if (t == null) {
          continue;
        }
        List<BuilderInstruction> insns = t.getInstructions();
        for (int i = 0; i < insns.size(); i++) {
          BuilderInstruction insn = insns.get(i);
          if (insn.getOpcode() == Opcode.CONST_STRING) {
            if (stringSection.getItemIndex((StringReference) ((ReferenceInstruction) insn).getReference()) >= 65536) {
              // Fix String jumbo instructions.
              t.replaceInstruction(i, new BuilderInstruction31c(Opcode.CONST_STRING_JUMBO,
                  ((OneRegisterInstruction) insn).getRegisterA(), ((ReferenceInstruction) insn).getReference()));
            }
          }
        }
      }
    }
  }
}
