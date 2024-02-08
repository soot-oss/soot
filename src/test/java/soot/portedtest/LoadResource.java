package soot.portedtest;

import soot.G;
import soot.Scene;
import soot.options.Options;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class LoadResource {
    public static void loadClasses(String first, String... more) {
        Path cp = Paths.get(first, more);

        G.reset();
        Options.v().set_process_dir(Collections.singletonList(cp.toFile().getAbsolutePath()));
        Options.v().set_src_prec(Options.src_prec_class); // Set source precision to class files
        Options.v().set_output_format(Options.output_format_jimple);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_ignore_resolving_levels(true);
        Scene.v().loadNecessaryClasses();
    }
}
