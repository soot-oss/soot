package soot.asm;

import com.google.common.base.Optional;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;
import soot.RefType;
import soot.SootClass;
import soot.SootModuleInfo;
import soot.SootModuleResolver;

/**
 * Created by ralle on 14.08.16.
 */
public class SootModuleInfoBuilder extends ModuleVisitor {
  private final SootClassBuilder scb;
  private SootModuleInfo klass;

  private String name;

  public SootModuleInfoBuilder(String name, SootModuleInfo klass, SootClassBuilder scb) {
    super(Opcodes.ASM6);
    this.klass = klass;
    this.name = name;
    this.scb = scb;
  }

  @Override
  public void visitRequire(String module, int access, String version) {
    // SootClass moduleInfo = SootResolver.v().resolveClass("module-info", SootClass.SIGNATURES, Optional.of(module));
    SootClass moduleInfo = SootModuleResolver.v().makeClassRef(SootModuleInfo.MODULE_INFO, Optional.of(module));
    klass.getRequiredModules().put((SootModuleInfo) moduleInfo, access);
    scb.addDep(RefType.v(moduleInfo));
  }

  @Override
  public void visitExport(String packaze, int access, String... modules) {
    if (packaze != null) {
      klass.addExportedPackage(packaze, modules);
    }
  }

  @Override
  public void visitOpen(String packaze, int access, String... modules) {
    if (packaze != null) {
      klass.addOpenedPackage(packaze, modules);
    }

  }
}
