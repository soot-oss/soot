package soot.javaToJimple;
import polyglot.main.*;
import polyglot.frontend.*;
import polyglot.util.*;
import polyglot.visit.*;
import polyglot.ast.*;

import java.util.*;
import java.io.*;

public class JavaToJimple {
	
    public static final polyglot.frontend.Pass.ID CAST_INSERTION = new polyglot.frontend.Pass.ID("cast-insertion");
    
	public polyglot.frontend.ExtensionInfo initExtInfo(String fileName, List sourceLocations){
		
        Set source = new HashSet();
        ExtensionInfo extInfo = new polyglot.ext.jl.ExtensionInfo() {
            public List passes(Job job) {
                List passes = super.passes(job);
                beforePass(passes, Pass.TYPE_CHECK, new VisitorPass(polyglot.frontend.Pass.FOLD, job, new polyglot.visit.ConstantFolder(ts, nf)));
                beforePass(passes, Pass.EXIT_CHECK, new VisitorPass(CAST_INSERTION, job, new CastInsertionVisitor(job, ts, nf)));
                removePass(passes, Pass.OUTPUT);
                return passes;
            }
            
        };
        polyglot.main.Options options = extInfo.getOptions();

        options.source_path = new LinkedList();
        Iterator it = sourceLocations.iterator();
        while (it.hasNext()){
            Object next = it.next();
            ////System.out.println("Location for polyglot source: "+next);
            options.source_path.add(new File(next.toString()));
        }

        //options.report.put("types", new Integer(3));
        options.source_ext = "java";
		options.serialize_type_info = false;
		
		source.add(fileName);
		
		options.source_path.add(new File(fileName).getParentFile());
		
        polyglot.main.Options.global = options;
		//polyglot.frontend.Compiler compiler = new polyglot.frontend.Compiler(extInfo);
		////System.out.println("About to Compile");

        return extInfo;
    }
    /*public polyglot.ast.Node createAst(String fileName, List sourceLocations) {
		
		Set source = new HashSet();
		/*polyglot.main.Options options = polyglot.main.Options.global;

        options.source_path = new LinkedList();
        Iterator it = sourceLocations.iterator();
        while (it.hasNext()){
            Object next = it.next();
            //System.out.println("Location for polyglot source: "+next);
            options.source_path.add(new File(next.toString()));
        }

        //options.report.put("types", new Integer(5));
        options.source_ext = "java";
		options.serialize_type_info = false;
*/
        /*ExtensionInfo extInfo = new polyglot.ext.jl.ExtensionInfo() {
            public List passes(Job job) {
                List passes = super.passes(job);
                beforePass(passes, Pass.TYPE_CHECK, new VisitorPass(polyglot.frontend.Pass.FOLD, job, new polyglot.visit.ConstantFolder(ts, nf)));
                beforePass(passes, Pass.EXIT_CHECK, new VisitorPass(CAST_INSERTION, job, new CastInsertionVisitor(job, ts, nf)));
                return passes;
            }
            
        };
		/*try {
			Class extClass = Class.forName("polyglot.ext.jl.ExtensionInfo");
		
			extInfo = (polyglot.frontend.ExtensionInfo) extClass.newInstance();
		}
		catch (ClassNotFoundException e) {
			//System.out.println(e.getMessage());
		}
		catch (Exception e2 ) {
			//System.out.println(e2.getMessage());
		}*/
		
       /* polyglot.main.Options options = extInfo.getOptions();

        options.source_path = new LinkedList();
        Iterator it = sourceLocations.iterator();
        while (it.hasNext()){
            Object next = it.next();
            ////System.out.println("Location for polyglot source: "+next);
            options.source_path.add(new File(next.toString()));
        }

        //options.report.put("visit", new Integer(10));
        options.source_ext = "java";
		options.serialize_type_info = false;
		
		source.add(fileName);
		
		options.source_path.add(new File(fileName).getParentFile());
		
		/*try {
			options.extension.setOptions(options);
		}
		catch(Exception e) {
			//System.out.println(e.getMessage());
		}*/
	
        //polyglot.main.Options.global = options;
		//polyglot.frontend.Compiler compiler = new polyglot.frontend.Compiler(extInfo);
		////System.out.println("About to Compile");
		
		//return compile(compiler, fileName, extInfo);
		
	//}

	//private polyglot.ast.Node compile(polyglot.frontend.Compiler compiler, String fileName, polyglot.frontend.ExtensionInfo extInfo){
	public polyglot.ast.Node compile(polyglot.frontend.Compiler compiler, String fileName, polyglot.frontend.ExtensionInfo extInfo){
		SourceLoader source_loader = compiler.sourceExtension().sourceLoader();

        ////System.out.println("source_loader set");
		try {
			FileSource source = new FileSource(fileName);

            ////System.out.println("source created");
			SourceJob job = compiler.sourceExtension().addJob(source);
            ////System.out.println("job added");
           
            //polyglot.frontend.Pass.ID CAST_INSERTION = new polyglot.frontend.Pass.ID("cast-insertion");
   
            //extInfo.beforePass(extInfo.passes(job), polyglot.frontend.Pass.EXC_CHECK, new VisitorPass(CAST_INSERTION, job, new CastInsertionVisitor(job, extInfo.typeSystem(), extInfo.nodeFactory())));
            
            //Iterator it = extInfo.passes(job).iterator();
            //while (it.hasNext()) {
            //    //System.out.println(it.next());
            //}
			//compiler.sourceExtension().runToPass(job, polyglot.frontend.Pass.EXIT_CHECK);
            boolean result = false;
		    result = compiler.sourceExtension().runToCompletion();
		
            if (!result) {
            
                throw new soot.CompilationDeathException(0, "Could not compile");
            }

            ////System.out.println("ast built");
        
            
            polyglot.ast.Node node = job.ast();

            //CastInsertionVisitor castInsertionVisitor = new CastInsertionVisitor(job, extInfo.typeSystem(), extInfo.nodeFactory());

            //node = node.visit(castInsertionVisitor);
			return node;

			//handleAst(node, sc);
		}
		catch (IOException e){
			//System.out.println(e.getMessage());
            return null;
		}

	}

}
