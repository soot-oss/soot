/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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

package soot;
import java.util.*;
import java.io.*;
import soot.util.*;
import soot.util.queue.*;
import soot.jimple.*;
import soot.shimple.*;
import soot.grimp.*;
import soot.baf.*;
import soot.jimple.toolkits.invoke.*;
import soot.jimple.toolkits.base.*;
import soot.shimple.toolkits.scalar.*;
import soot.grimp.toolkits.base.*;
import soot.baf.toolkits.base.*;
import soot.jimple.toolkits.typing.*;
import soot.jimple.toolkits.scalar.*;
import soot.jimple.toolkits.scalar.pre.*;
import soot.jimple.toolkits.annotation.arraycheck.*;
import soot.jimple.toolkits.annotation.profiling.*;
import soot.jimple.toolkits.annotation.callgraph.*;
import soot.jimple.toolkits.annotation.parity.*;
import soot.jimple.toolkits.annotation.methods.*;
import soot.jimple.toolkits.annotation.fields.*;
import soot.jimple.toolkits.annotation.qualifiers.*;
import soot.jimple.toolkits.annotation.nullcheck.*;
import soot.jimple.toolkits.annotation.tags.*;
import soot.jimple.toolkits.annotation.defs.*;
import soot.jimple.toolkits.pointer.*;
import soot.jimple.toolkits.callgraph.*;
import soot.tagkit.*;
import soot.options.Options;
import soot.toolkits.scalar.*;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.spark.BDDSparkTransformer;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.spark.fieldrw.*;
import soot.dava.*;
import soot.dava.toolkits.base.misc.*;
import soot.xml.*;
import soot.toolkits.graph.*;

/** Manages the Packs containing the various phases and their options. */
public class PackManager {
    public PackManager( Singletons.Global g ) { G.v().PhaseOptions().setPackManager(this); init(); }

    public boolean onlyStandardPacks() { return onlyStandardPacks; }
    private boolean onlyStandardPacks = false;
    void notifyAddPack() {
        onlyStandardPacks = false;
    }

    private void init()
    {
        Pack p;

        // Jimple body creation
        addPack(p = new JimpleBodyPack());
        {
            p.add(new Transform("jb.ls", LocalSplitter.v()));
            p.add(new Transform("jb.a", Aggregator.v()));
            p.add(new Transform("jb.ule", UnusedLocalEliminator.v()));
            p.add(new Transform("jb.tr", TypeAssigner.v()));
            p.add(new Transform("jb.ulp", LocalPacker.v()));
            p.add(new Transform("jb.lns", LocalNameStandardizer.v()));
            p.add(new Transform("jb.cp", CopyPropagator.v()));
            p.add(new Transform("jb.dae", DeadAssignmentEliminator.v()));
            p.add(new Transform("jb.cp-ule", UnusedLocalEliminator.v()));
            p.add(new Transform("jb.lp", LocalPacker.v()));
            p.add(new Transform("jb.ne", NopEliminator.v()));
            p.add(new Transform("jb.uce", UnreachableCodeEliminator.v()));
        }

        // Java to Jimple - Jimple body creation
        addPack(p = new JavaToJimpleBodyPack());
        {
            p.add(new Transform("jj.ls", LocalSplitter.v()));
            p.add(new Transform("jj.a", Aggregator.v()));
            p.add(new Transform("jj.ule", UnusedLocalEliminator.v()));
            p.add(new Transform("jj.ne", NopEliminator.v()));
            p.add(new Transform("jj.tr", TypeAssigner.v()));
            p.add(new Transform("jj.ulp", LocalPacker.v()));
            p.add(new Transform("jj.lns", LocalNameStandardizer.v()));
            p.add(new Transform("jj.cp", CopyPropagator.v()));
            p.add(new Transform("jj.dae", DeadAssignmentEliminator.v()));
            p.add(new Transform("jj.cp-ule", UnusedLocalEliminator.v()));
            p.add(new Transform("jj.lp", LocalPacker.v()));
            p.add(new Transform("jj.uce", UnreachableCodeEliminator.v()));
        
        }
        
        // Call graph pack
        addPack(p = new CallGraphPack("cg"));
        {
            p.add(new Transform("cg.cha", CHATransformer.v()));
            p.add(new Transform("cg.spark", SparkTransformer.v()));
            p.add(new Transform("cg.bdd", BDDSparkTransformer.v()));
        }

        // Whole-Shimple transformation pack
        addPack(p = new ScenePack("wstp"));

        // Whole-Shimple Optimization pack
        addPack(p = new ScenePack("wsop"));

        // Whole-Jimple transformation pack 
        addPack(p = new ScenePack("wjtp"));
        {
        }

        // Whole-Jimple Optimization pack
        addPack(p = new ScenePack("wjop"));
        {
            p.add(new Transform("wjop.smb", StaticMethodBinder.v()));
            p.add(new Transform("wjop.si", StaticInliner.v()));
        }

        // Give another chance to do Whole-Jimple transformation
        // The RectangularArrayFinder will be put into this package.
        addPack(p = new ScenePack("wjap"));
        {
            p.add(new Transform("wjap.ra", RectangularArrayFinder.v()));
            p.add(new Transform("wjap.umt", UnreachableMethodsTagger.v()));
            p.add(new Transform("wjap.uft", UnreachableFieldsTagger.v()));
            p.add(new Transform("wjap.tqt", TightestQualifiersTagger.v()));
        }

        // Shimple pack
        addPack(p = new BodyPack(Shimple.PHASE));
        
        // Shimple transformation pack
        addPack(p = new BodyPack("stp"));
            
        // Shimple optimization pack
        addPack(p = new BodyPack("sop"));
        {
            p.add(new Transform("sop.cpf", SConstantPropagatorAndFolder.v()));
        }

        // Jimple transformation pack
        addPack(p = new BodyPack("jtp"));
        
        // Jimple optimization pack
        addPack(p = new BodyPack("jop"));
        {
            p.add(new Transform("jop.cse", CommonSubexpressionEliminator.v()));
            p.add(new Transform("jop.bcm", BusyCodeMotion.v()));
            p.add(new Transform("jop.lcm", LazyCodeMotion.v()));
            p.add(new Transform("jop.cp", CopyPropagator.v()));
            p.add(new Transform("jop.cpf", ConstantPropagatorAndFolder.v()));
            p.add(new Transform("jop.cbf", ConditionalBranchFolder.v()));
            p.add(new Transform("jop.dae", DeadAssignmentEliminator.v()));
            p.add(new Transform("jop.uce1", UnreachableCodeEliminator.v()));
            p.add(new Transform("jop.ubf1", UnconditionalBranchFolder.v()));
            p.add(new Transform("jop.uce2", UnreachableCodeEliminator.v()));
            p.add(new Transform("jop.ubf2", UnconditionalBranchFolder.v()));
            p.add(new Transform("jop.ule", UnusedLocalEliminator.v()));
        }

        // Jimple annotation pack
        addPack(p = new BodyPack("jap"));
        {
            p.add(new Transform("jap.npc", NullPointerChecker.v()));
            p.add(new Transform("jap.npcolorer", NullPointerColorer.v()));
            p.add(new Transform("jap.abc", ArrayBoundsChecker.v()));
            p.add(new Transform("jap.profiling", ProfilingGenerator.v()));
            p.add(new Transform("jap.sea", SideEffectTagger.v()));
            p.add(new Transform("jap.fieldrw", FieldTagger.v()));
            p.add(new Transform("jap.cgtagger", CallGraphTagger.v()));
            p.add(new Transform("jap.parity", ParityTagger.v()));
            p.add(new Transform("jap.pat", ParameterAliasTagger.v()));
            p.add(new Transform("jap.rdtagger", ReachingDefsTagger.v()));
            p.add(new Transform("jap.che", CastCheckEliminatorDumper.v()));
	    
        }

        // CFG Viewer 
        addPack(p = new BodyPack("cfg"));
        {
            p.add(new Transform("cfg.output", CFGPrinter.v()));
        }
        
        // Grimp body creation
        addPack(p = new BodyPack("gb"));
        {
            p.add(new Transform("gb.a1", Aggregator.v()));
            p.add(new Transform("gb.cf", ConstructorFolder.v()));
            p.add(new Transform("gb.a2", Aggregator.v()));
            p.add(new Transform("gb.ule", UnusedLocalEliminator.v()));
        }

        // Grimp optimization pack
        addPack(p = new BodyPack("gop"));

        // Baf body creation
        addPack(p = new BodyPack("bb"));
        {
            p.add(new Transform("bb.lso", LoadStoreOptimizer.v()));
            p.add(new Transform("bb.pho", PeepholeOptimizer.v()));
            p.add(new Transform("bb.ule", UnusedLocalEliminator.v()));
            p.add(new Transform("bb.lp", LocalPacker.v()));
        }

        // Baf optimization pack
        addPack(p = new BodyPack("bop"));

        // Code attribute tag aggregation pack
        addPack(p = new BodyPack("tag"));
        {
            p.add(new Transform("tag.ln", LineNumberTagAggregator.v()));
            p.add(new Transform("tag.an", ArrayNullTagAggregator.v()));
            p.add(new Transform("tag.dep", DependenceTagAggregator.v()));
            p.add(new Transform("tag.fieldrw", FieldTagAggregator.v()));
        }

        onlyStandardPacks = true;
    }

    public static PackManager v() { 
        return G.v().PackManager();
    }

    private Map packNameToPack = new HashMap();
    private List packList = new LinkedList();

    private void addPack( Pack p ) {
        if( packNameToPack.containsKey( p.getPhaseName() ) )
            throw new RuntimeException( "Duplicate pack "+p.getPhaseName() );
        packNameToPack.put( p.getPhaseName(), p );
        packList.add( p );
    }

    public boolean hasPack(String phaseName) {
        return getPhase( phaseName ) != null;
    }

    public Pack getPack(String phaseName) {
        Pack p = (Pack) packNameToPack.get(phaseName);
        return p;
    }

    public boolean hasPhase(String phaseName) {
        return getPhase(phaseName) != null;
    }

    public HasPhaseOptions getPhase(String phaseName) {
        int index = phaseName.indexOf( "." );
        if( index < 0 ) return getPack( phaseName );
        String packName = phaseName.substring(0,index);
        if( !hasPack( packName ) ) return null;
        return getPack( packName ).get( phaseName );
    }

    public Transform getTransform(String phaseName) {
        return (Transform) getPhase( phaseName );
    }


    public Collection allPacks() {
        return Collections.unmodifiableList( packList );
    }

    public void runPacks() {
        if (Options.v().whole_program() || Options.v().whole_shimple()) {
            runWholeProgramPacks();
        }
        preProcessDAVA();
        runBodyPacks( reachableClasses() );
    }

    public void writeOutput() {
        if( Options.v().output_format() == Options.output_format_dava ) {
            postProcessDAVA();
        } else {
            writeOutput( reachableClasses() );
        }
        postProcessXML( reachableClasses() );
        releaseBodies( reachableClasses() );
    }

    private void runWholeProgramPacks() {
        getPack("cg").apply();

        if (Options.v().whole_shimple()) {
            ShimpleTransformer.v().transform();
            getPack("wstp").apply();
            getPack("wsop").apply();
        } else {
            getPack("wjtp").apply();
            getPack("wjop").apply();
            getPack("wjap").apply();
        }
    }

    /* preprocess classes for DAVA */
    private void preProcessDAVA() {
        if (Options.v().output_format() == Options.output_format_dava) {
            ThrowFinder.v().find();
            PackageNamer.v().fixNames();

            G.v().out.println();
        }
    }

    private void runBodyPacks( Iterator classes ) {
        while( classes.hasNext() ) {
            SootClass cl = (SootClass) classes.next();
            runBodyPacks( cl );
        }
    }

    private void writeOutput( Iterator classes ) {
        while( classes.hasNext() ) {
            SootClass cl = (SootClass) classes.next();
            writeClass( cl );
        }
    }

    private void releaseBodies( Iterator classes ) {
        while( classes.hasNext() ) {
            SootClass cl = (SootClass) classes.next();
            releaseBodies( cl );
        }
    }

    private Iterator reachableClasses() {
        if( false && (Options.v().whole_program() ||
                      Options.v().whole_shimple())) {
            QueueReader methods = Scene.v().getReachableMethods().listener();
            HashSet reachableClasses = new HashSet();
            
            while(true) {
                    SootMethod m = (SootMethod) methods.next();
                    if(m == null) break;
                    SootClass c = m.getDeclaringClass();
                    if( !c.isApplicationClass() ) continue;
                    reachableClasses.add( c );
            }
            return reachableClasses.iterator();
        } else {
            return Scene.v().getApplicationClasses().iterator();
        }
    }

    /* post process for DAVA */
    private void postProcessDAVA() {
        G.v().out.println();

        Iterator classIt = Scene.v().getApplicationClasses().iterator();
        while (classIt.hasNext()) {
            SootClass s = (SootClass) classIt.next();

            FileOutputStream streamOut = null;
            PrintWriter writerOut = null;
            String fileName = SourceLocator.v().getFileNameFor(s, Options.v().output_format());

            try {
                streamOut = new FileOutputStream(fileName);
                writerOut =
                    new PrintWriter(new OutputStreamWriter(streamOut));
            } catch (IOException e) {
                G.v().out.println("Cannot output file " + fileName);
            }

            G.v().out.print("Generating " + fileName + "... ");
            G.v().out.flush();

            DavaPrinter.v().printTo(s, writerOut);

            G.v().out.println();
            G.v().out.flush();

            {
                try {
                    writerOut.flush();
                    streamOut.close();
                } catch (IOException e) {
                    G.v().out.println(
                        "Cannot close output file " + fileName);
                }
            }
        }
        G.v().out.println();
    }

    private void runBodyPacks(SootClass c) {
        final int format = Options.v().output_format();
        if (format == Options.output_format_dava) {
            G.v().out.print("Decompiling ");
        } else {
            G.v().out.print("Transforming ");
        }
        G.v().out.println(c.getName() + "... ");

        boolean produceBaf = false, produceGrimp = false, produceDava = false,
            produceJimple = true, produceShimple = false;

        switch (format) {
            case Options.output_format_none :
            case Options.output_format_xml :
            case Options.output_format_jimple :
            case Options.output_format_jimp :
                break;
            case Options.output_format_shimp:
            case Options.output_format_shimple:
                produceShimple = true;
                // FLIP produceJimple
                produceJimple = false;
                break;
            case Options.output_format_dava :
                produceDava = true;
                // FALL THROUGH
            case Options.output_format_grimp :
            case Options.output_format_grimple :
                produceGrimp = true;
                break;
            case Options.output_format_baf :
            case Options.output_format_b :
                produceBaf = true;
                break;
            case Options.output_format_jasmin :
            case Options.output_format_class :
                produceGrimp = Options.v().via_grimp();
                produceBaf = !produceGrimp;
                break;
            default :
                throw new RuntimeException();
        }

        soot.xml.TagCollector tc = new soot.xml.TagCollector();
        
        boolean wholeShimple = Options.v().whole_shimple();
        if( Options.v().via_shimple() ) produceShimple = true;

        Iterator methodIt = c.methodIterator();
        while (methodIt.hasNext()) {
            SootMethod m = (SootMethod) methodIt.next();

            if (!m.isConcrete()) continue;

            if (produceShimple || wholeShimple) {
                ShimpleBody sBody = null;

                // whole shimple or not?
                {
                    Body body = m.retrieveActiveBody();

                    if(body instanceof ShimpleBody){
                        sBody = (ShimpleBody) body;
                        if(!sBody.isSSA())
                            sBody.rebuild();
                    }
                    else{
                        sBody = Shimple.v().newBody(body);
                    }
                }
                
                m.setActiveBody(sBody);
                PackManager.v().getPack("stp").apply(sBody);
                PackManager.v().getPack("sop").apply(sBody);

                if( produceJimple || (wholeShimple && !produceShimple) )
                    m.setActiveBody(sBody.toJimpleBody());
            }

            if (produceJimple) {
                JimpleBody body =(JimpleBody) m.retrieveActiveBody();
                PackManager.v().getPack("jtp").apply(body);
                PackManager.v().getPack("jop").apply(body);
                PackManager.v().getPack("jap").apply(body);
                if (Options.v().xml_attributes() && Options.v().output_format() != Options.output_format_jimple) {
                    //System.out.println("collecting body tags");
                    tc.collectBodyTags(body);
                }
            }
            
            PackManager.v().getPack("cfg").apply(m.retrieveActiveBody());

            if (produceGrimp) {
                m.setActiveBody(Grimp.v().newBody(m.getActiveBody(), "gb"));
                PackManager.v().getPack("gop").apply(m.getActiveBody());
            } else if (produceBaf) {
                m.setActiveBody(Baf.v().newBody
                                ((JimpleBody) m.getActiveBody()));
                PackManager.v().getPack("bop").apply(m.getActiveBody());
                PackManager.v().getPack("tag").apply(m.getActiveBody());
            }
        }
            
        if (Options.v().xml_attributes() && Options.v().output_format() != Options.output_format_jimple) {
            processXMLForClass(c, tc);
            //System.out.println("processed xml for class");
        }

        if (produceDava) {
            methodIt = c.methodIterator();
            while (methodIt.hasNext()) {
                SootMethod m = (SootMethod) methodIt.next();

                if (!m.isConcrete()) continue;

                m.setActiveBody(Dava.v().newBody(m.getActiveBody()));
            }
        }
    }

    private void writeClass(SootClass c) {
        final int format = Options.v().output_format();
        if( format == Options.output_format_none ) return;
        if( format == Options.output_format_dava ) return;

        FileOutputStream streamOut = null;
        PrintWriter writerOut = null;
        boolean noOutputFile = false;

        String fileName = SourceLocator.v().getFileNameFor(c, format);

        if (format != Options.output_format_class) {
            try {
                streamOut = new FileOutputStream(fileName);
                writerOut = new PrintWriter(new OutputStreamWriter(streamOut));
                G.v().out.println( "Writing to "+fileName );
            } catch (IOException e) {
                G.v().out.println("Cannot output file " + fileName);
            }
        }

        if (Options.v().xml_attributes()) {
            Printer.v().setOption(Printer.ADD_JIMPLE_LN);
        }
        switch (format) {
            case Options.output_format_jasmin :
                if (c.containsBafBody())
                    new soot.baf.JasminClass(c).print(writerOut);
                else
                    new soot.jimple.JasminClass(c).print(writerOut);
                break;
            case Options.output_format_jimp :
            case Options.output_format_shimp :
            case Options.output_format_b :
            case Options.output_format_grimp :
                Printer.v().setOption(Printer.USE_ABBREVIATIONS);
                Printer.v().printTo(c, writerOut);
                break;
            case Options.output_format_baf :
            case Options.output_format_jimple :
            case Options.output_format_shimple :
            case Options.output_format_grimple :
                writerOut =
                    new PrintWriter(
                        new EscapedWriter(new OutputStreamWriter(streamOut)));
                Printer.v().printTo(c, writerOut);
                break;
            case Options.output_format_class :
                Printer.v().write(c, SourceLocator.v().getOutputDir());
                break;
            case Options.output_format_xml :
                writerOut =
                    new PrintWriter(
                        new EscapedWriter(new OutputStreamWriter(streamOut)));
                XMLPrinter.v().printJimpleStyleTo(c, writerOut);
                break;
            default :
                throw new RuntimeException();
        }

        if (format != Options.output_format_class) {
            try {
                writerOut.flush();
                streamOut.close();
            } catch (IOException e) {
                G.v().out.println("Cannot close output file " + fileName);
            }
        }
    }

    private void postProcessXML( Iterator classes ) {
        if (!Options.v().xml_attributes()) return;
        if (Options.v().output_format() != Options.output_format_jimple) return;
        while( classes.hasNext() ) {
            SootClass c = (SootClass) classes.next();
            processXMLForClass(c);
        }
    }

    private void processXMLForClass(SootClass c, TagCollector tc){
        final int format = Options.v().output_format();
        String fileName = SourceLocator.v().getFileNameFor(c, format);
        XMLAttributesPrinter xap = new XMLAttributesPrinter(fileName,
               SourceLocator.v().getOutputDir());
        xap.printAttrs(c, tc);
    }
    
    private void processXMLForClass(SootClass c){
        final int format = Options.v().output_format();
        String fileName = SourceLocator.v().getFileNameFor(c, format);
        XMLAttributesPrinter xap = new XMLAttributesPrinter(fileName,
               SourceLocator.v().getOutputDir());
        xap.printAttrs(c);
    }

    private void releaseBodies( SootClass cl ) {
        Iterator methodIt = cl.methodIterator();
        while (methodIt.hasNext()) {
            SootMethod m = (SootMethod) methodIt.next();

            if (m.hasActiveBody())
                m.releaseActiveBody();
        }
    }
}
