
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;

public class Frontend extends java.lang.Object {
    // Declared in FrontendMain.jrag at line 12

    protected Program program;

    // Declared in FrontendMain.jrag at line 14


    protected Frontend() {
      program = new Program();
      program.state().reset();
    }

    // Declared in FrontendMain.jrag at line 19


    public boolean process(String[] args, BytecodeReader reader, JavaParser parser) {
      program.initBytecodeReader(reader);
      program.initJavaParser(parser);

      initOptions();
      processArgs(args);

      Collection files = program.options().files();

      if(program.options().hasOption("-version")) {
        printVersion();
        return false;
      }
      if(program.options().hasOption("-help") || files.isEmpty()) {
        printUsage();
        return false;
      }

      try {
        for(Iterator iter = files.iterator(); iter.hasNext(); ) {
          String name = (String)iter.next();
          if(!new File(name).exists())
            System.out.println("WARNING: file \"" + name + "\" does not exist");
          program.addSourceFile(name);
        }

        for(Iterator iter = program.compilationUnitIterator(); iter.hasNext(); ) {
          CompilationUnit unit = (CompilationUnit)iter.next();
          if(unit.fromSource()) {
            Collection errors = unit.parseErrors();
            Collection warnings = new LinkedList();
            // compute static semantic errors when there are no parse errors or 
            // the recover from parse errors option is specified
            if(errors.isEmpty() || program.options().hasOption("-recover"))
              unit.errorCheck(errors, warnings);
            if(!errors.isEmpty()) {
              processErrors(errors, unit);
              return false;
            }
            else {
              processWarnings(warnings, unit);
              processNoErrors(unit);
            }
          }
        }
      } catch (Exception e) {
        System.err.println(e.getMessage());
        e.printStackTrace();
      }
      return true;
    }

    // Declared in FrontendMain.jrag at line 71


    protected void initOptions() {
      Options options = program.options();
      options.initOptions();
      options.addKeyOption("-version");
      options.addKeyOption("-print");
      options.addKeyOption("-g");
      options.addKeyOption("-g:none");
      options.addKeyOption("-g:lines,vars,source");
      options.addKeyOption("-nowarn");
      options.addKeyOption("-verbose");
      options.addKeyOption("-deprecation");
      options.addKeyValueOption("-classpath");
      options.addKeyValueOption("-sourcepath");
      options.addKeyValueOption("-bootclasspath");
      options.addKeyValueOption("-extdirs");
      options.addKeyValueOption("-d");
      options.addKeyValueOption("-encoding");
      options.addKeyValueOption("-source");
      options.addKeyValueOption("-target");
      options.addKeyOption("-help");
      options.addKeyOption("-O");
      options.addKeyOption("-J-Xmx128M");
      options.addKeyOption("-recover");
    }

    // Declared in FrontendMain.jrag at line 95

    protected void processArgs(String[] args) {
      program.options().addOptions(args);
    }

    // Declared in FrontendMain.jrag at line 99


    protected void processErrors(Collection errors, CompilationUnit unit) {
      System.out.println("Errors:");
      for(Iterator iter2 = errors.iterator(); iter2.hasNext(); ) {
        System.out.println(iter2.next());
      }
    }

    // Declared in FrontendMain.jrag at line 105

    protected void processWarnings(Collection warnings, CompilationUnit unit) {
      for(Iterator iter2 = warnings.iterator(); iter2.hasNext(); ) {
        System.out.println(iter2.next());
      }
    }

    // Declared in FrontendMain.jrag at line 110

    protected void processNoErrors(CompilationUnit unit) {
    }

    // Declared in FrontendMain.jrag at line 113


    protected void printUsage() {
      printVersion();
      System.out.println(
          "\n" + name() + "\n\n" +
          "Usage: java " + name() + " <options> <source files>\n" +
          "  -verbose                  Output messages about what the compiler is doing\n" +
          "  -classpath <path>         Specify where to find user class files\n" +
          "  -sourcepath <path>        Specify where to find input source files\n" + 
          "  -bootclasspath <path>     Override location of bootstrap class files\n" + 
          "  -extdirs <dirs>           Override location of installed extensions\n" +
          "  -d <directory>            Specify where to place generated class files\n" +
          "  -help                     Print a synopsis of standard options\n" +
          "  -version                  Print version information\n"
          );
    }

    // Declared in FrontendMain.jrag at line 129


    protected void printVersion() {
      System.out.println(name() + " " + url() + " Version " + version());
    }

    // Declared in FrontendMain.jrag at line 133


    protected String name() {
      return "Java1.4Frontend";
    }

    // Declared in FrontendMain.jrag at line 136

    protected String url() {
      return "(http://jastadd.cs.lth.se)";
    }

    // Declared in FrontendMain.jrag at line 140


    protected String version() {
      return "R20070504";
    }


}
