/**
    This example is (C) Copyright 1999 Raja Vallee-Rai
    ...and is under the GNU LGPL.
 */

package ca.mcgill.sable.soot.tools.counter;

 
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.soot.grimp.*;
import ca.mcgill.sable.util.*;
import java.io.*;
import java.util.*;

public class Main
{
    static String outputDir = "";
    
    public static void main(String[] args) 
    {
        int firstNonOption = 0;
        boolean isRecursing = false;
        List excludingPackages = new ArrayList();
        List classesToProcess;
        SootClass entryClass = null;
        
        if(args.length == 0)
        {
            System.out.println("Syntax: java InsertCounters [option]* entryclass otherclass ...  ");
            System.out.println("");
            System.out.println("  -d PATH                    store produced files in PATH");
            System.out.println("  -r, --recurse              process dependent classfiles as well");
            System.out.println("  -x, --exclude PACKAGE      exclude classfiles in PACKAGE (e.g. java)"); 
            System.out.println("                             from transformation");
            System.out.println("");
            System.out.println("Misc. options:");
            System.out.println("  --soot-class-path PATH     uses PATH as the classpath for finding classes");
            System.out.println("  -v, --verbose              verbose mode");
            System.out.println("");
            System.out.println("Examples:");
            System.out.println("");
            System.out.println("  java InsertCounters -x java -x sun -r -d counterClasses Simulator");
            System.out.println("         Transforms all classes starting with Simulator, excluding ");
            System.out.println("         those in java.*, sun.*, and stores them in counterClasses. ");
               
            System.exit(0);
        }

        // Handle all the options
            for(int i = 0; i < args.length; i++)
            {
                String arg = args[i];
                
                if(arg.equals("-v") || arg.equals("--verbose"))
                    ca.mcgill.sable.soot.Main.isVerbose = true;
                else if(arg.equals("--soot-class-path"))
                {   
                    if(++i < args.length)
                        ca.mcgill.sable.soot.Main.sootClassPath = args[i];
                }
                else if(arg.equals("-r") || arg.equals("--recurse"))
                    isRecursing=true;
                else if(arg.equals("-d"))
                {
                    if(++i < args.length)
                        outputDir = args[i];
                }
                else if(arg.equals("-x") || arg.equals("--exclude"))
                {
                    if(++i < args.length)
                        excludingPackages.add(args[i]);
                }
                else if(arg.startsWith("-"))
                {
                    System.out.println("Unrecognized option: " + arg);
                    System.exit(0);
                }
                else
                    break;

                firstNonOption = i + 1;
            }

        // Generate classes to process
        {
            classesToProcess = new LinkedList();
            
            for(int i = firstNonOption; i < args.length; i++)
            {
                SootClass c = Scene.v().loadClassAndSupport(args[i]);
                
                if(entryClass == null)  
                    entryClass = c;
                    
                classesToProcess.add(c);
            }
            
            if(isRecursing)
            {
                classesToProcess = new LinkedList();
                classesToProcess.addAll(Scene.v().getClasses());
             }   
                         
            // Remove all classes from excludingPackages
            {
                Iterator classIt = classesToProcess.iterator();
                
                while(classIt.hasNext())
                {
                    SootClass s = (SootClass) classIt.next();
                    
                    Iterator packageIt = excludingPackages.iterator();
                    
                    while(packageIt.hasNext())
                    {
                        String pkg = (String) packageIt.next();
                        
                        if(s.getPackageName().startsWith(pkg))
                            classIt.remove();
                    }
                }
            }
        }
    
        // Generate counter class
        {
            System.out.println("Creating CounterClass...");
            
            try 
            {
                InputStream in = ClassLocator.getInputStreamOf("CounterClass");
            
                byte[] bytes = new byte[20000];
                
                String fileName;
                char fileSeparator = System.getProperty("file.separator").charAt(0);

                if(!outputDir.equals(""))
                    fileName = outputDir + fileSeparator + "CounterClass.class";
                else
                    fileName = "CounterClass.class";
                
                try
                {    
                    FileOutputStream out = new FileOutputStream(fileName);
                    
                    for(;;)
                    {
                        int available = in.available();
                        
                        if(available == 0)
                            break;
                            
                        in.read(bytes, 0, available);
                        out.write(bytes, 0, available);
                    }
                    
                    out.close();
                } catch(IOException e)
                {
                    System.out.println("Unable to write file " + fileName);
                    System.exit(1);
                }                
            } catch(ClassNotFoundException e)
            {
                System.out.println("Unable to find CounterClass to copy!");
                System.exit(1);
            }
        }
        // Initialize some fields
            counterClass = Scene.v().loadClassAndSupport("CounterClass");
            
            interfaceInvokeCount = counterClass.getField("long interfaceInvokeCount");
            virtualInvokeCount = counterClass.getField("long virtualInvokeCount");
            specialInvokeCount = counterClass.getField("long specialInvokeCount");
            staticInvokeCount = counterClass.getField("long staticInvokeCount");

            systemClass = Scene.v().loadClassAndSupport("java.lang.System");
            systemExitMethod = systemClass.getMethod("void exit(int)");            
            
        // Handle each class
        {
            Iterator classIt = classesToProcess.iterator();
            
            while(classIt.hasNext())
            {
                SootClass sClass = (SootClass) classIt.next();
                
                System.out.print("Inserting counters for " + sClass.getName() + "... " );
                System.out.flush();
                       
                processClass(sClass, entryClass == sClass);
                System.out.println();
            }
        }
    }

    static SootClass counterClass, systemClass;
    static SootField interfaceInvokeCount,
        virtualInvokeCount,
        specialInvokeCount,
        staticInvokeCount;
    
    static SootMethod systemExitMethod;
    
    static void processClass(SootClass sClass, boolean isEntryClass)
    {

        // Add code to increase goto counter each time an invokevirtual/interface is encountered
        {
            Iterator methodIt = sClass.getMethods().iterator();
            
            while(methodIt.hasNext())
            {
                SootMethod m = (SootMethod) methodIt.next();
                
                JimpleBody body = new JimpleBody(new ClassFileBody(m));
                
                m.setActiveBody(body);
                
                Local tmpLocal = Jimple.v().newLocal("tmp", LongType.v());
                body.addLocal(tmpLocal);
                
                StmtList stmtList = body.getStmtList();
                ListIterator stmtIt = stmtList.listIterator();
                
                while(stmtIt.hasNext())
                {
                    Stmt s = (Stmt) stmtIt.next();
                                   
                    // Add an increment counter before an invoke expression
                    {
                        InvokeExpr invokeExpr = null;
                        
                        if(s instanceof InvokeStmt)
                            invokeExpr = (InvokeExpr) ((InvokeStmt) s).getInvokeExpr();
                        else if(s instanceof AssignStmt)
                        {
                            AssignStmt as = (AssignStmt) s;
                            
                            if(as.getRightOp() instanceof InvokeExpr)
                                invokeExpr = (InvokeExpr) as.getRightOp();
                        }
                        
                        if(invokeExpr != null)
                        {
                            SootField counter = null;
                            
                            if(invokeExpr instanceof StaticInvokeExpr)
                                counter = staticInvokeCount;
                            else if(invokeExpr instanceof SpecialInvokeExpr)
                                counter = specialInvokeCount;
                            else if(invokeExpr instanceof VirtualInvokeExpr)
                                counter = virtualInvokeCount;
                            else if(invokeExpr instanceof InterfaceInvokeExpr)
                                counter = interfaceInvokeCount;    
                                                        
                            // insert "tmpLocal = counter;"
                                stmtIt.add(Jimple.v().newAssignStmt(tmpLocal, 
                                    Jimple.v().newStaticFieldRef(counter)));
                                                        
                            // insert "counter = tmpLocal + 1L;" 
                                stmtIt.add(Jimple.v().newAssignStmt(
                                    Jimple.v().newStaticFieldRef(counter), 
                                    Jimple.v().newAddExpr(tmpLocal, LongConstant.v(1L))));
                    
                            // Need to do this hideously contorted operation to make sure these statements
                            // were added to the basic block
                                stmtIt.previous();
                                stmtIt.previous();
                                stmtIt.previous();
                                stmtIt.remove();
                                stmtIt.next();
                                stmtIt.next();
                                stmtIt.add(s);
                                
                            if(invokeExpr.getMethod() == systemExitMethod)
                            {   
                                // Add a call to CounterClass before the System.exit(x) call
                                    stmtIt.add(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(
                                        counterClass.getMethod("void stopProfiling()"))));
                                                                        
                                // Contortions to make sure element is inserted in correct basic block.  hideous!
                                    stmtIt.previous();
                                    stmtIt.previous();
                                    stmtIt.remove();
                                    stmtIt.next();
                                    stmtIt.add(s);
                            }   
                        }
                    }   
                }
            }
        }
        
        // Add a call to CounterClass.stopProfiling() before each return in the main method.
        {
            if(isEntryClass)
            {
                SootMethod m = sClass.getMethod("void main(java.lang.String[])");
                    
                JimpleBody body = (JimpleBody) m.getActiveBody();
                
                StmtList stmtList = body.getStmtList();
                
                ListIterator stmtIt = stmtList.listIterator();
                
                while(stmtIt.hasNext())
                {
                    Stmt s = (Stmt) stmtIt.next();
                    
                    if(s instanceof ReturnVoidStmt)
                    {
                        stmtIt.add(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(
                            counterClass.getMethod("void stopProfiling()"))));       
                     
                        // More contortions
                            stmtIt.previous();
                            stmtIt.previous();
                            stmtIt.remove();
                            stmtIt.next();
                            stmtIt.add(s);
                    } 
                }
            }
        }
        
        //sClass.printTo(new PrintWriter(System.out, true), PrintJimpleBodyOption.USE_ABBREVIATIONS);
        sClass.write(outputDir);
        
        // Release the bodies for this method
        {
            Iterator methodIt = sClass.getMethods().iterator();
                
            while(methodIt.hasNext())
            {
                SootMethod m = (SootMethod) methodIt.next();
                m.releaseActiveBody();
            }
        }
    }   
}

