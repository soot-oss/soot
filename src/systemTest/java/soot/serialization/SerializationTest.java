package soot.serialization;

import com.esotericsoftware.minlog.Log;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import soot.PackManager;
import soot.SootMethod;
import soot.options.Options;
import soot.testing.framework.AbstractTestingFramework;

/**
 * @author Manuel Benz at 2019-08-26
 */
public class SerializationTest extends AbstractTestingFramework {

  private static final String TEST_METHOD_NAME = "main";
  private static final String TEST_METHOD_RET = "void";
  public static final Path SERIALIZATION_OUT = Paths.get("./sootOutput");

  @Before
  public void setUp() throws Exception {
    Options.v().set_output_dir(SERIALIZATION_OUT.toString());
    FileUtils.deleteDirectory(new File(Options.v().output_dir()));
    Files.createDirectory(SERIALIZATION_OUT);
    Log.TRACE();
  }

  @Override
  protected void setupSoot() {
    super.setupSoot();
    Options.v().set_output_format(Options.output_format_binary);
    Options.v().set_src_prec(Options.src_prec_binary);
    prependToProcessDir(SERIALIZATION_OUT.toString());
  }

  @Override
  protected void runSoot() {
    super.runSoot();
    PackManager.v().writeOutput();
  }

  @Test
  public void WriteOut() {
    String testClass = "soot.lambdaMetaFactory.LambdaNoCaptures";

    final SootMethod target = prepareTarget(methodSigFromComponents(testClass, TEST_METHOD_RET, TEST_METHOD_NAME), testClass,
        "java.util.function.Function");
  }

  @Test
  public void writeAndRead() {
    String testClass = "soot.lambdaMetaFactory.LambdaNoCaptures";

    SootMethod target = prepareTarget(methodSigFromComponents(testClass, TEST_METHOD_RET, TEST_METHOD_NAME), testClass,
        "java.util.function.Function");

    System.out.println();

    target = prepareTarget(methodSigFromComponents(testClass, TEST_METHOD_RET, TEST_METHOD_NAME), testClass,
        "java.util.function.Function");
  }
}
