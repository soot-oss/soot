
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

/* THIS FILE IS AUTO-GENERATED FROM soot_options.xml. DO NOT MODIFY. */

package soot.options;
import java.util.*;

/** Soot command-line options parser.
 * @author Ondrej Lhotak
 */

public class Options extends OptionsBase {
    public Options( String[] argv ) {
        for( int i = argv.length; i > 0; i-- ) {
            pushOptions( argv[i-1] );
        }
    }

    public static final int srcPrec_classFile = 1;
    public static final int srcPrec_jimple = 2;
    public static final int outputFormat_jimp = 1;
    public static final int outputFormat_njimple = 2;
    public static final int outputFormat_jimple = 3;
    public static final int outputFormat_baf = 4;
    public static final int outputFormat_b = 5;
    public static final int outputFormat_grimp = 6;
    public static final int outputFormat_grimple = 7;
    public static final int outputFormat_xml = 8;
    public static final int outputFormat_none = 9;
    public static final int outputFormat_jasmin = 10;
    public static final int outputFormat_classFile = 11;
    public static final int outputFormat_dava = 12;

    public boolean parse() {
        while( hasMoreOptions() ) {
            String option = nextOption();
            if( option.charAt(0) != '-' ) {
                while(true) {
                    classes.add( option );
                    if( !hasMoreOptions() ) break;
                    option = nextOption();
                }
                return true;
            }
            while( option.charAt(0) == '-' ) {
                option = option.substring(1);
            }
            if( false );

            else if( false 
            || option.equals( "h" )
            || option.equals( "help" )
            )
                help = true;
  
            else if( false 
            || option.equals( "version" )
            )
                version = true;
  
            else if( false 
            || option.equals( "v" )
            || option.equals( "verbose" )
            )
                verbose = true;
  
            else if( false 
            || option.equals( "app" )
            )
                appMode = true;
  
            else if( false
            || option.equals( "cp" )
            || option.equals( "soot-classpath" )
            ) {
                if( !hasMoreOptions() ) {
                    System.out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    
                if( classpath == null )
                    classpath = value;
                else {
                    System.out.println( "Duplicate values "+classpath+" and "+value+" for option -"+option );
                    return false;
                }
            }
  
            else if( false
            || option.equals( "src-prec" )
            ) {
                if( !hasMoreOptions() ) {
                    System.out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    
                if( false );
    
                else if( false
                || value.equals( "c" )
                || value.equals( "class" )
                ) {
                    if( srcPrec != 0
                    && srcPrec != srcPrec_classFile ) {
                        System.out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    srcPrec = srcPrec_classFile;
                }
    
                else if( false
                || value.equals( "J" )
                || value.equals( "jimple" )
                ) {
                    if( srcPrec != 0
                    && srcPrec != srcPrec_jimple ) {
                        System.out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    srcPrec = srcPrec_jimple;
                }
    
                else {
                    System.out.println( "Invalid value "+value+" given for option -"+option );
                    return false;
                }
           }
  
            else if( false 
            || option.equals( "allow-phantom-refs" )
            )
                allowPhantoms = true;
  
            else if( false
            || option.equals( "d" )
            || option.equals( "output-dir" )
            ) {
                if( !hasMoreOptions() ) {
                    System.out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    
                if( outputDir == null )
                    outputDir = value;
                else {
                    System.out.println( "Duplicate values "+outputDir+" and "+value+" for option -"+option );
                    return false;
                }
            }
  
            else if( false
            || option.equals( "o" )
            || option.equals( "output-format" )
            ) {
                if( !hasMoreOptions() ) {
                    System.out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    
                if( false );
    
                else if( false
                || value.equals( "j" )
                || value.equals( "jimp" )
                ) {
                    if( outputFormat != 0
                    && outputFormat != outputFormat_jimp ) {
                        System.out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    outputFormat = outputFormat_jimp;
                }
    
                else if( false
                || value.equals( "njimple" )
                ) {
                    if( outputFormat != 0
                    && outputFormat != outputFormat_njimple ) {
                        System.out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    outputFormat = outputFormat_njimple;
                }
    
                else if( false
                || value.equals( "J" )
                || value.equals( "jimple" )
                ) {
                    if( outputFormat != 0
                    && outputFormat != outputFormat_jimple ) {
                        System.out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    outputFormat = outputFormat_jimple;
                }
    
                else if( false
                || value.equals( "B" )
                || value.equals( "baf" )
                ) {
                    if( outputFormat != 0
                    && outputFormat != outputFormat_baf ) {
                        System.out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    outputFormat = outputFormat_baf;
                }
    
                else if( false
                || value.equals( "b" )
                ) {
                    if( outputFormat != 0
                    && outputFormat != outputFormat_b ) {
                        System.out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    outputFormat = outputFormat_b;
                }
    
                else if( false
                || value.equals( "g" )
                || value.equals( "grimp" )
                ) {
                    if( outputFormat != 0
                    && outputFormat != outputFormat_grimp ) {
                        System.out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    outputFormat = outputFormat_grimp;
                }
    
                else if( false
                || value.equals( "G" )
                || value.equals( "grimple" )
                ) {
                    if( outputFormat != 0
                    && outputFormat != outputFormat_grimple ) {
                        System.out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    outputFormat = outputFormat_grimple;
                }
    
                else if( false
                || value.equals( "X" )
                || value.equals( "xml" )
                ) {
                    if( outputFormat != 0
                    && outputFormat != outputFormat_xml ) {
                        System.out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    outputFormat = outputFormat_xml;
                }
    
                else if( false
                || value.equals( "n" )
                || value.equals( "none" )
                ) {
                    if( outputFormat != 0
                    && outputFormat != outputFormat_none ) {
                        System.out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    outputFormat = outputFormat_none;
                }
    
                else if( false
                || value.equals( "s" )
                || value.equals( "jasmin" )
                || value.equals( "jasmin-through-baf" )
                ) {
                    if( outputFormat != 0
                    && outputFormat != outputFormat_jasmin ) {
                        System.out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    outputFormat = outputFormat_jasmin;
                }
    
                else if( false
                || value.equals( "c" )
                || value.equals( "class" )
                || value.equals( "class-through-baf" )
                ) {
                    if( outputFormat != 0
                    && outputFormat != outputFormat_classFile ) {
                        System.out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    outputFormat = outputFormat_classFile;
                }
    
                else if( false
                || value.equals( "d" )
                || value.equals( "dava" )
                ) {
                    if( outputFormat != 0
                    && outputFormat != outputFormat_dava ) {
                        System.out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    outputFormat = outputFormat_dava;
                }
    
                else {
                    System.out.println( "Invalid value "+value+" given for option -"+option );
                    return false;
                }
           }
  
            else if( false 
            || option.equals( "via-grimp" )
            )
                viaGrimp = true;
  
            else if( false
            || option.equals( "p" )
            || option.equals( "phase-option" )
            ) {
                if( !hasMoreOptions() ) {
                    System.out.println( "No phase name given for option -"+option );
                    return false;
                }
                String phaseName = nextOption();
                if( !hasMoreOptions() ) {
                    System.out.println( "No phase option given for option -"+option+" "+phaseName );
                    return false;
                }
                String phaseOption = nextOption();
    
                if( !setPhaseOption( phaseName, phaseOption ) )
                    return false;
            }
  
            else if( false
            || option.equals( "O" )
            || option.equals( "optimize" )
            ) {
                pushOptions( "-p jop enabled:true" );
            }
  
            else if( false
            || option.equals( "W" )
            || option.equals( "whole-optimize" )
            ) {
                pushOptions( "-p wjop enabled:true" );
            }
  
            else if( false
            || option.equals( "process-path" )
            ) {
                if( !hasMoreOptions() ) {
                    System.out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    
                if( processPath == null )
                    processPath = new LinkedList();

                processPath.add( value );
            }
  
            else if( false
            || option.equals( "i" )
            || option.equals( "include" )
            ) {
                if( !hasMoreOptions() ) {
                    System.out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    
                if( incPackage == null )
                    incPackage = new LinkedList();

                incPackage.add( value );
            }
  
            else if( false
            || option.equals( "x" )
            || option.equals( "exclude" )
            ) {
                if( !hasMoreOptions() ) {
                    System.out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    
                if( excPackage == null )
                    excPackage = new LinkedList();

                excPackage.add( value );
            }
  
            else if( false 
            || option.equals( "a" )
            || option.equals( "analyze-context" )
            )
                analyzeContext = true;
  
            else if( false
            || option.equals( "dynamic-classes" )
            ) {
                if( !hasMoreOptions() ) {
                    System.out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    
                if( dynClasses == null )
                    dynClasses = new LinkedList();

                dynClasses.add( value );
            }
  
            else if( false
            || option.equals( "dynamic-path" )
            ) {
                if( !hasMoreOptions() ) {
                    System.out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    
                if( dynPath == null )
                    dynPath = new LinkedList();

                dynPath.add( value );
            }
  
            else if( false
            || option.equals( "dynamic-package" )
            ) {
                if( !hasMoreOptions() ) {
                    System.out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    
                if( dynPackage == null )
                    dynPackage = new LinkedList();

                dynPackage.add( value );
            }
  
            else if( false 
            || option.equals( "keep-line-number" )
            )
                keepLineNum = true;
  
            else if( false 
            || option.equals( "keep-bytecode-offset" )
            || option.equals( "keep-offset" )
            )
                keepByteOffset = true;
  
            else if( false
            || option.equals( "annot-nullpointer" )
            ) {
                pushOptions( "-p jtp.npc enabled:true -p jtp.profiling enabled:true -p agg.an enabled:true" );
            }
  
            else if( false
            || option.equals( "annot-arraybounds" )
            ) {
                pushOptions( "-p wjtp2.ra enabled:true -p jtp.abc enabled:true -p jtp.profiling enabled:true -p agg.an enabled:true" );
            }
  
            else if( false 
            || option.equals( "time" )
            )
                time = true;
  
            else if( false 
            || option.equals( "subtract-gc" )
            )
                subGC = true;
  
            else {
                System.out.println( "Invalid option -"+option );
                return false;
            }
        }
        return true;
    }

    public boolean help() { return help; }
    private boolean help = false;
    public boolean version() { return version; }
    private boolean version = false;
    public boolean verbose() { return verbose; }
    private boolean verbose = false;
    public boolean appMode() { return appMode; }
    private boolean appMode = false;
    public String classpath() { return classpath; }
    private String classpath = null;
    public int srcPrec() {
        if( srcPrec == 0 ) return srcPrec_classFile;
        return srcPrec; 
    }
    private int srcPrec = 0;
    public boolean allowPhantoms() { return allowPhantoms; }
    private boolean allowPhantoms = false;
    public String outputDir() { return outputDir; }
    private String outputDir = null;
    public int outputFormat() {
        if( outputFormat == 0 ) return outputFormat_classFile;
        return outputFormat; 
    }
    private int outputFormat = 0;
    public boolean viaGrimp() { return viaGrimp; }
    private boolean viaGrimp = false;
    public List processPath() { 
        if( processPath == null )
            return java.util.Collections.EMPTY_LIST;
        else
            return processPath;
    }
    private List processPath = null;
    public List incPackage() { 
        if( incPackage == null )
            return java.util.Collections.EMPTY_LIST;
        else
            return incPackage;
    }
    private List incPackage = null;
    public List excPackage() { 
        if( excPackage == null )
            return java.util.Collections.EMPTY_LIST;
        else
            return excPackage;
    }
    private List excPackage = null;
    public boolean analyzeContext() { return analyzeContext; }
    private boolean analyzeContext = false;
    public List dynClasses() { 
        if( dynClasses == null )
            return java.util.Collections.EMPTY_LIST;
        else
            return dynClasses;
    }
    private List dynClasses = null;
    public List dynPath() { 
        if( dynPath == null )
            return java.util.Collections.EMPTY_LIST;
        else
            return dynPath;
    }
    private List dynPath = null;
    public List dynPackage() { 
        if( dynPackage == null )
            return java.util.Collections.EMPTY_LIST;
        else
            return dynPackage;
    }
    private List dynPackage = null;
    public boolean keepLineNum() { return keepLineNum; }
    private boolean keepLineNum = false;
    public boolean keepByteOffset() { return keepByteOffset; }
    private boolean keepByteOffset = false;
    public boolean time() { return time; }
    private boolean time = false;
    public boolean subGC() { return subGC; }
    private boolean subGC = false;

    public String getUsage() {
        return ""

+"\nGeneral Options:\n"
      
+padOpt(" -h -help", "display help and exit" )
+padOpt(" -version", "output version information and exit" )
+padOpt(" -v -verbose", "verbose mode" )
+padOpt(" -app", "runs in application mode" )
+"\nInput Options:\n"
      
+padOpt(" -cp ARG -soot-classpath ARG", "uses given PATH as the classpath for finding classes for Soot processing" )
+padOpt(" -src-prec ARG", "sets the source precedence for Soot" )
+padVal(" c class", "Class File" )
+padVal(" J jimple", "Jimple File" )
+padOpt(" -allow-phantom-refs", "allow unresolved classes; may cause errors" )
+"\nOutput Options:\n"
      
+padOpt(" -d ARG -output-dir ARG", "store produced files in PATH" )
+padOpt(" -o ARG -output-format ARG", "sets the source precedence for Soot" )
+padVal(" j jimp", "Jimp File" )
+padVal(" njimple", "Njimple File" )
+padVal(" J jimple", "Jimple File" )
+padVal(" B baf", "Baf File" )
+padVal(" b", "Aggregated Baf File" )
+padVal(" g grimp", "Grimp File" )
+padVal(" G grimple", "Grimple File" )
+padVal(" X xml", "Xml File" )
+padVal(" n none", "No Output File" )
+padVal(" s jasmin jasmin-through-baf", "Jasmin File" )
+padVal(" c class class-through-baf", "Class File" )
+padVal(" d dava", "Dava Decompiled File" )
+padOpt(" -via-grimp", "convert jimple to bytecode via grimp instead of via baf" )
+"\nProcessing Options:\n"
      
+padOpt(" -p PHASE-NAME PHASE-OPTIONS -phase-option PHASE-NAME PHASE-OPTIONS", "set run-time option KEY to VALUE for PHASE-NAME" )
+padOpt(" -O -optimize", "perform scalar optimizations on the classfiles" )
+padOpt(" -W -whole-optimize", "perform whole program optimizations on the classfiles" )
+"\nSingle File Mode Options:\n"
      
+padOpt(" -process-path ARG", "process all classes on the PATH" )
+"\nApplication Mode Options:\n"
      
+padOpt(" -i ARG -include ARG", "marks classfiles in PACKAGE (e.g. java.util.)as application classes" )
+padOpt(" -x ARG -exclude ARG", "marks classfiles in PACKAGE (e.g. java.) as context classes" )
+padOpt(" -a -analyze-context", "label context classes as library" )
+padOpt(" -dynamic-classes ARG", "marks CLASSES (separated by colons) as potentially dynamic classes" )
+padOpt(" -dynamic-path ARG", "marks all class files in PATH as potentially dynamic classes" )
+padOpt(" -dynamic-package ARG", "marks classfiles in PACKAGES (separated by commas) as potentially dynamic classes" )
+"\nInput Attribute Options:\n"
      
+padOpt(" -keep-line-number", "keep line number tables" )
+padOpt(" -keep-bytecode-offset -keep-offset", "attach bytecode offset to jimple statement" )
+"\nAnnotation Options:\n"
      
+padOpt(" -annot-nullpointer", "turn on the annotation for null pointer" )
+padOpt(" -annot-arraybounds", "turn on the annotation for array bounds check" )
+"\nMiscellaneous Options:\n"
      
+padOpt(" -time", "print out time statistics about tranformations" )
+padOpt(" -subtract-gc", "attempt to subtract the gc from the time stats" )
        ;
    }
}
