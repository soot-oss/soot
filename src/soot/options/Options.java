
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
import soot.*;
import java.util.*;
import soot.PackManager;

/** Soot command-line options parser.
 * @author Ondrej Lhotak
 */

public class Options extends OptionsBase {
    public Options( String[] argv ) {
        for( int i = argv.length; i > 0; i-- ) {
            pushOptions( argv[i-1] );
        }
    }

    public static final int src_prec_class = 1;
    public static final int src_prec_jimple = 2;
    public static final int output_format_jimp = 1;
    public static final int output_format_njimple = 2;
    public static final int output_format_jimple = 3;
    public static final int output_format_baf = 4;
    public static final int output_format_b = 5;
    public static final int output_format_grimp = 6;
    public static final int output_format_grimple = 7;
    public static final int output_format_xml = 8;
    public static final int output_format_none = 9;
    public static final int output_format_jasmin = 10;
    public static final int output_format_class = 11;
    public static final int output_format_dava = 12;

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
                app = true;
  
            else if( false
            || option.equals( "cp" )
            || option.equals( "soot-classpath" )
            ) {
                if( !hasMoreOptions() ) {
                    G.v().out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    
                if( soot_classpath.length() == 0 )
                    soot_classpath = value;
                else {
                    G.v().out.println( "Duplicate values "+soot_classpath+" and "+value+" for option -"+option );
                    return false;
                }
            }
  
            else if( false
            || option.equals( "src-prec" )
            ) {
                if( !hasMoreOptions() ) {
                    G.v().out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    
                if( false );
    
                else if( false
                || value.equals( "c" )
                || value.equals( "class" )
                ) {
                    if( src_prec != 0
                    && src_prec != src_prec_class ) {
                        G.v().out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    src_prec = src_prec_class;
                }
    
                else if( false
                || value.equals( "J" )
                || value.equals( "jimple" )
                ) {
                    if( src_prec != 0
                    && src_prec != src_prec_jimple ) {
                        G.v().out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    src_prec = src_prec_jimple;
                }
    
                else {
                    G.v().out.println( "Invalid value "+value+" given for option -"+option );
                    return false;
                }
           }
  
            else if( false 
            || option.equals( "allow-phantom-refs" )
            )
                allow_phantom_refs = true;
  
            else if( false
            || option.equals( "d" )
            || option.equals( "output-dir" )
            ) {
                if( !hasMoreOptions() ) {
                    G.v().out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    
                if( output_dir.length() == 0 )
                    output_dir = value;
                else {
                    G.v().out.println( "Duplicate values "+output_dir+" and "+value+" for option -"+option );
                    return false;
                }
            }
  
            else if( false
            || option.equals( "o" )
            || option.equals( "output-format" )
            ) {
                if( !hasMoreOptions() ) {
                    G.v().out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    
                if( false );
    
                else if( false
                || value.equals( "j" )
                || value.equals( "jimp" )
                ) {
                    if( output_format != 0
                    && output_format != output_format_jimp ) {
                        G.v().out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    output_format = output_format_jimp;
                }
    
                else if( false
                || value.equals( "njimple" )
                ) {
                    if( output_format != 0
                    && output_format != output_format_njimple ) {
                        G.v().out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    output_format = output_format_njimple;
                }
    
                else if( false
                || value.equals( "J" )
                || value.equals( "jimple" )
                ) {
                    if( output_format != 0
                    && output_format != output_format_jimple ) {
                        G.v().out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    output_format = output_format_jimple;
                }
    
                else if( false
                || value.equals( "B" )
                || value.equals( "baf" )
                ) {
                    if( output_format != 0
                    && output_format != output_format_baf ) {
                        G.v().out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    output_format = output_format_baf;
                }
    
                else if( false
                || value.equals( "b" )
                ) {
                    if( output_format != 0
                    && output_format != output_format_b ) {
                        G.v().out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    output_format = output_format_b;
                }
    
                else if( false
                || value.equals( "g" )
                || value.equals( "grimp" )
                ) {
                    if( output_format != 0
                    && output_format != output_format_grimp ) {
                        G.v().out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    output_format = output_format_grimp;
                }
    
                else if( false
                || value.equals( "G" )
                || value.equals( "grimple" )
                ) {
                    if( output_format != 0
                    && output_format != output_format_grimple ) {
                        G.v().out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    output_format = output_format_grimple;
                }
    
                else if( false
                || value.equals( "X" )
                || value.equals( "xml" )
                ) {
                    if( output_format != 0
                    && output_format != output_format_xml ) {
                        G.v().out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    output_format = output_format_xml;
                }
    
                else if( false
                || value.equals( "n" )
                || value.equals( "none" )
                ) {
                    if( output_format != 0
                    && output_format != output_format_none ) {
                        G.v().out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    output_format = output_format_none;
                }
    
                else if( false
                || value.equals( "s" )
                || value.equals( "jasmin" )
                ) {
                    if( output_format != 0
                    && output_format != output_format_jasmin ) {
                        G.v().out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    output_format = output_format_jasmin;
                }
    
                else if( false
                || value.equals( "c" )
                || value.equals( "class" )
                ) {
                    if( output_format != 0
                    && output_format != output_format_class ) {
                        G.v().out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    output_format = output_format_class;
                }
    
                else if( false
                || value.equals( "d" )
                || value.equals( "dava" )
                ) {
                    if( output_format != 0
                    && output_format != output_format_dava ) {
                        G.v().out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    output_format = output_format_dava;
                }
    
                else {
                    G.v().out.println( "Invalid value "+value+" given for option -"+option );
                    return false;
                }
           }
  
            else if( false 
            || option.equals( "via-grimp" )
            )
                via_grimp = true;
  
            else if( false 
            || option.equals( "xml-attributes" )
            )
                xml_attributes = true;
  
            else if( false
            || option.equals( "p" )
            || option.equals( "phase-option" )
            ) {
                if( !hasMoreOptions() ) {
                    G.v().out.println( "No phase name given for option -"+option );
                    return false;
                }
                String phaseName = nextOption();
                if( !hasMoreOptions() ) {
                    G.v().out.println( "No phase option given for option -"+option+" "+phaseName );
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
                    G.v().out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    
                if( process_path == null )
                    process_path = new LinkedList();

                process_path.add( value );
            }
  
            else if( false
            || option.equals( "i" )
            || option.equals( "include" )
            ) {
                if( !hasMoreOptions() ) {
                    G.v().out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    
                if( include == null )
                    include = new LinkedList();

                include.add( value );
            }
  
            else if( false
            || option.equals( "x" )
            || option.equals( "exclude" )
            ) {
                if( !hasMoreOptions() ) {
                    G.v().out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    
                if( exclude == null )
                    exclude = new LinkedList();

                exclude.add( value );
            }
  
            else if( false 
            || option.equals( "a" )
            || option.equals( "analyze-context" )
            )
                analyze_context = true;
  
            else if( false
            || option.equals( "dynamic-classes" )
            ) {
                if( !hasMoreOptions() ) {
                    G.v().out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    
                if( dynamic_classes == null )
                    dynamic_classes = new LinkedList();

                dynamic_classes.add( value );
            }
  
            else if( false
            || option.equals( "dynamic-path" )
            ) {
                if( !hasMoreOptions() ) {
                    G.v().out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    
                if( dynamic_path == null )
                    dynamic_path = new LinkedList();

                dynamic_path.add( value );
            }
  
            else if( false
            || option.equals( "dynamic-package" )
            ) {
                if( !hasMoreOptions() ) {
                    G.v().out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    
                if( dynamic_package == null )
                    dynamic_package = new LinkedList();

                dynamic_package.add( value );
            }
  
            else if( false 
            || option.equals( "keep-line-number" )
            )
                keep_line_number = true;
  
            else if( false 
            || option.equals( "keep-bytecode-offset" )
            || option.equals( "keep-offset" )
            )
                keep_offset = true;
  
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
                subtract_gc = true;
  
            else {
                G.v().out.println( "Invalid option -"+option );
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
    public boolean app() { return app; }
    private boolean app = false;
    public String soot_classpath() { return soot_classpath; }
    private String soot_classpath = "";
    public int src_prec() {
        if( src_prec == 0 ) return src_prec_class;
        return src_prec; 
    }
    private int src_prec = 0;
    public boolean allow_phantom_refs() { return allow_phantom_refs; }
    private boolean allow_phantom_refs = false;
    public String output_dir() { return output_dir; }
    private String output_dir = "";
    public int output_format() {
        if( output_format == 0 ) return output_format_class;
        return output_format; 
    }
    private int output_format = 0;
    public boolean via_grimp() { return via_grimp; }
    private boolean via_grimp = false;
    public boolean xml_attributes() { return xml_attributes; }
    private boolean xml_attributes = false;
    public List process_path() { 
        if( process_path == null )
            return java.util.Collections.EMPTY_LIST;
        else
            return process_path;
    }
    private List process_path = null;
    public List include() { 
        if( include == null )
            return java.util.Collections.EMPTY_LIST;
        else
            return include;
    }
    private List include = null;
    public List exclude() { 
        if( exclude == null )
            return java.util.Collections.EMPTY_LIST;
        else
            return exclude;
    }
    private List exclude = null;
    public boolean analyze_context() { return analyze_context; }
    private boolean analyze_context = false;
    public List dynamic_classes() { 
        if( dynamic_classes == null )
            return java.util.Collections.EMPTY_LIST;
        else
            return dynamic_classes;
    }
    private List dynamic_classes = null;
    public List dynamic_path() { 
        if( dynamic_path == null )
            return java.util.Collections.EMPTY_LIST;
        else
            return dynamic_path;
    }
    private List dynamic_path = null;
    public List dynamic_package() { 
        if( dynamic_package == null )
            return java.util.Collections.EMPTY_LIST;
        else
            return dynamic_package;
    }
    private List dynamic_package = null;
    public boolean keep_line_number() { return keep_line_number; }
    private boolean keep_line_number = false;
    public boolean keep_offset() { return keep_offset; }
    private boolean keep_offset = false;
    public boolean time() { return time; }
    private boolean time = false;
    public boolean subtract_gc() { return subtract_gc; }
    private boolean subtract_gc = false;

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
+padVal(" s jasmin", "Jasmin File" )
+padVal(" c class", "Class File" )
+padVal(" d dava", "Dava Decompiled File" )
+padOpt(" -via-grimp", "convert jimple to bytecode via grimp instead of via baf" )
+padOpt(" -xml-attributes", "Save tags to XML attributes for Eclipse" )
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

    public static String getDeclaredOptionsForPhase( String phaseName ) {
    
        if( phaseName.equals( "jb" ) )
            return ""
                +"disabled "
                +"no-splitting "
                +"no-typing "
                +"aggregate-all-locals "
                +"no-aggregating "
                +"use-original-names "
                +"pack-locals "
                +"no-cp "
                +"no-nop-elimination "
                +"no-unreachable-code-elimination "
                +"verbatim ";
    
        if( phaseName.equals( "jb.asv" ) )
            return ""
                +"disabled "
                +"only-stack-locals ";
    
        if( phaseName.equals( "jb.ulp" ) )
            return ""
                +"disabled "
                +"unsplit-original-locals ";
    
        if( phaseName.equals( "jb.lns" ) )
            return ""
                +"disabled "
                +"only-stack-locals ";
    
        if( phaseName.equals( "jb.cp" ) )
            return ""
                +"disabled "
                +"only-regular-locals "
                +"only-stack-locals ";
    
        if( phaseName.equals( "jb.dae" ) )
            return ""
                +"disabled "
                +"only-stack-locals ";
    
        if( phaseName.equals( "jb.ls" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "jb.a" ) )
            return ""
                +"disabled "
                +"only-stack-locals ";
    
        if( phaseName.equals( "jb.ule" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "jb.tr" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "jb.cp-ule" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "jb.lp" ) )
            return ""
                +"disabled "
                +"unsplit-original-locals ";
    
        if( phaseName.equals( "jb.ne" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "jb.uce" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "cg" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "wstp" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "wsop" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "wjtp" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "wjtp.Spark" ) )
            return ""
                +"disabled "
                +"verbose "
                +"ignoreTypesEntirely "
                +"forceGCs "
                +"preJimplify "
                +"VTA "
                +"RTA "
                +"ignoreBaseObjects "
                +"typesForSites "
                +"mergeStringBuffer "
                +"simulateNatives "
                +"simpleEdgesBidirectional "
                +"onFlyCallGraph "
                +"parmsAsFields "
                +"returnsAsFields "
                +"simplifyOffline "
                +"simplifySCCs "
                +"ignoreTypesForSCCs "
                +"propagator "
                +"setImpl "
                +"doubleSetOld "
                +"doubleSetNew "
                +"dumpHTML "
                +"dumpPAG "
                +"dumpSolution "
                +"topoSort "
                +"dumpTypes "
                +"classMethodVar "
                +"dumpAnswer "
                +"trimInvokeGraph "
                +"addTags ";
    
        if( phaseName.equals( "wjop" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "wjop.smb" ) )
            return ""
                +"disabled "
                +"insert-null-checks "
                +"insert-redundant-casts "
                +"allowed-modifier-changes "
                +"VTA-passes ";
    
        if( phaseName.equals( "wjop.si" ) )
            return ""
                +"disabled "
                +"insert-null-checks "
                +"insert-redundant-casts "
                +"allowed-modifier-changes "
                +"expansion-factor "
                +"max-container-size "
                +"max-inlinee-size "
                +"VTA-passes ";
    
        if( phaseName.equals( "wjtp2" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "wjtp2.ra" ) )
            return ""
                +"disabled "
                +"with-wholeapp ";
    
        if( phaseName.equals( "stp" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "sop" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "jtp" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "jop" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "jop.cse" ) )
            return ""
                +"disabled "
                +"naive-side-effect ";
    
        if( phaseName.equals( "jop.bcm" ) )
            return ""
                +"disabled "
                +"naive-side-effect ";
    
        if( phaseName.equals( "jop.lcm" ) )
            return ""
                +"disabled "
                +"safe "
                +"unroll "
                +"naive-side-effect ";
    
        if( phaseName.equals( "jop.cp" ) )
            return ""
                +"disabled "
                +"only-regular-locals "
                +"only-stack-locals ";
    
        if( phaseName.equals( "jop.cpf" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "jop.cbf" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "jop.dae" ) )
            return ""
                +"disabled "
                +"only-stack-locals ";
    
        if( phaseName.equals( "jop.uce1" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "jop.uce2" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "jop.ubf1" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "jop.ubf2" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "jop.ule" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "jap" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "jap.npc" ) )
            return ""
                +"disabled "
                +"only-array-ref "
                +"profiling ";
    
        if( phaseName.equals( "jap.abc" ) )
            return ""
                +"disabled "
                +"with-all "
                +"with-fieldref "
                +"with-arrayref "
                +"with-cse "
                +"with-classfield "
                +"with-rectarray "
                +"profiling ";
    
        if( phaseName.equals( "jap.profiling" ) )
            return ""
                +"disabled "
                +"enable "
                +"notmainentry ";
    
        if( phaseName.equals( "gb" ) )
            return ""
                +"disabled "
                +"no-aggregating "
                +"aggregate-all-locals ";
    
        if( phaseName.equals( "gb.a" ) )
            return ""
                +"disabled "
                +"only-stack-locals ";
    
        if( phaseName.equals( "gb.asv1" ) )
            return ""
                +"disabled "
                +"only-stack-locals ";
    
        if( phaseName.equals( "gb.asv2" ) )
            return ""
                +"disabled "
                +"only-stack-locals ";
    
        if( phaseName.equals( "gb.cf" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "gb.ule" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "gop" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "bb" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "bb.lso" ) )
            return ""
                +"disabled "
                +"debug "
                +"inter "
                +"sl "
                +"sl2 "
                +"sll "
                +"sll2 ";
    
        if( phaseName.equals( "bb.pho" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "bb.ule" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "bb.lp" ) )
            return ""
                +"disabled "
                +"unsplit-original-locals ";
    
        if( phaseName.equals( "bop" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "tag" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "tag.ln" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "tag.an" ) )
            return ""
                +"disabled ";
    
        if( phaseName.equals( "tag.dep" ) )
            return ""
                +"disabled ";
    
        // The default set of options is just disabled.
        return "disabled";
    }

    public static String getDefaultOptionsForPhase( String phaseName ) {
    
        if( phaseName.equals( "jb" ) )
            return "";
    
        if( phaseName.equals( "jb.asv" ) )
            return ""
              +"only-stack-locals:true ";
    
        if( phaseName.equals( "jb.ulp" ) )
            return ""
              +"unsplit-original-locals:true ";
    
        if( phaseName.equals( "jb.lns" ) )
            return "";
    
        if( phaseName.equals( "jb.cp" ) )
            return ""
              +"only-stack-locals:true ";
    
        if( phaseName.equals( "jb.dae" ) )
            return ""
              +"only-stack-locals:true ";
    
        if( phaseName.equals( "jb.ls" ) )
            return "";
    
        if( phaseName.equals( "jb.a" ) )
            return "";
    
        if( phaseName.equals( "jb.ule" ) )
            return "";
    
        if( phaseName.equals( "jb.tr" ) )
            return "";
    
        if( phaseName.equals( "jb.cp-ule" ) )
            return "";
    
        if( phaseName.equals( "jb.lp" ) )
            return "";
    
        if( phaseName.equals( "jb.ne" ) )
            return "";
    
        if( phaseName.equals( "jb.uce" ) )
            return "";
    
        if( phaseName.equals( "cg" ) )
            return "";
    
        if( phaseName.equals( "wstp" ) )
            return "";
    
        if( phaseName.equals( "wsop" ) )
            return "";
    
        if( phaseName.equals( "wjtp" ) )
            return ""
              +"disabled:true ";
    
        if( phaseName.equals( "wjtp.Spark" ) )
            return ""
              +"disabled:true "
              +"verbose:false "
              +"ignoreTypesEntirely:false "
              +"forceGCs:false "
              +"preJimplify:false "
              +"VTA:false "
              +"RTA:false "
              +"ignoreBaseObjects:false "
              +"typesForSites:false "
              +"mergeStringBuffer:true "
              +"simulateNatives:true "
              +"simpleEdgesBidirectional:false "
              +"onFlyCallGraph:false "
              +"parmsAsFields:false "
              +"returnsAsFields:false "
              +"simplifyOffline:false "
              +"simplifySCCs:false "
              +"ignoreTypesForSCCs:false "
              +"propagator:worklist "
              +"setImpl:double "
              +"doubleSetOld:hybrid "
              +"doubleSetNew:hybrid "
              +"dumpHTML:false "
              +"dumpPAG:false "
              +"dumpSolution:false "
              +"topoSort:false "
              +"dumpTypes:true "
              +"classMethodVar:true "
              +"dumpAnswer:false "
              +"trimInvokeGraph:false "
              +"addTags:false ";
    
        if( phaseName.equals( "wjop" ) )
            return ""
              +"disabled:true ";
    
        if( phaseName.equals( "wjop.smb" ) )
            return ""
              +"disabled:true "
              +"insert-null-checks:true "
              +"insert-redundant-casts:true "
              +"allowed-modifier-changes:unsafe "
              +"VTA-passes:0 ";
    
        if( phaseName.equals( "wjop.si" ) )
            return ""
              +"insert-null-checks:true "
              +"insert-redundant-casts:true "
              +"allowed-modifier-changes:unsafe "
              +"expansion-factor:3 "
              +"max-container-size:5000 "
              +"max-inlinee-size:20 "
              +"VTA-passes:0 ";
    
        if( phaseName.equals( "wjtp2" ) )
            return "";
    
        if( phaseName.equals( "wjtp2.ra" ) )
            return ""
              +"disabled:true ";
    
        if( phaseName.equals( "stp" ) )
            return "";
    
        if( phaseName.equals( "sop" ) )
            return "";
    
        if( phaseName.equals( "jtp" ) )
            return "";
    
        if( phaseName.equals( "jop" ) )
            return "";
    
        if( phaseName.equals( "jop.cse" ) )
            return ""
              +"disabled:true ";
    
        if( phaseName.equals( "jop.bcm" ) )
            return ""
              +"disabled:true ";
    
        if( phaseName.equals( "jop.lcm" ) )
            return ""
              +"disabled:true "
              +"safe:safe "
              +"unroll:true ";
    
        if( phaseName.equals( "jop.cp" ) )
            return "";
    
        if( phaseName.equals( "jop.cpf" ) )
            return "";
    
        if( phaseName.equals( "jop.cbf" ) )
            return "";
    
        if( phaseName.equals( "jop.dae" ) )
            return "";
    
        if( phaseName.equals( "jop.uce1" ) )
            return "";
    
        if( phaseName.equals( "jop.uce2" ) )
            return "";
    
        if( phaseName.equals( "jop.ubf1" ) )
            return "";
    
        if( phaseName.equals( "jop.ubf2" ) )
            return "";
    
        if( phaseName.equals( "jop.ule" ) )
            return "";
    
        if( phaseName.equals( "jap" ) )
            return "";
    
        if( phaseName.equals( "jap.npc" ) )
            return ""
              +"disabled:true ";
    
        if( phaseName.equals( "jap.abc" ) )
            return ""
              +"disabled:true ";
    
        if( phaseName.equals( "jap.profiling" ) )
            return ""
              +"disabled:true "
              +"enable:false "
              +"notmainentry:false ";
    
        if( phaseName.equals( "gb" ) )
            return "";
    
        if( phaseName.equals( "gb.a" ) )
            return "";
    
        if( phaseName.equals( "gb.asv1" ) )
            return ""
              +"only-stack-locals:true ";
    
        if( phaseName.equals( "gb.asv2" ) )
            return ""
              +"only-stack-locals:true ";
    
        if( phaseName.equals( "gb.cf" ) )
            return "";
    
        if( phaseName.equals( "gb.ule" ) )
            return "";
    
        if( phaseName.equals( "gop" ) )
            return "";
    
        if( phaseName.equals( "bb" ) )
            return "";
    
        if( phaseName.equals( "bb.lso" ) )
            return ""
              +"sl:true "
              +"sll:true ";
    
        if( phaseName.equals( "bb.pho" ) )
            return "";
    
        if( phaseName.equals( "bb.ule" ) )
            return "";
    
        if( phaseName.equals( "bb.lp" ) )
            return "";
    
        if( phaseName.equals( "bop" ) )
            return "";
    
        if( phaseName.equals( "tag" ) )
            return "";
    
        if( phaseName.equals( "tag.ln" ) )
            return ""
              +"disabled:true ";
    
        if( phaseName.equals( "tag.an" ) )
            return ""
              +"disabled:true ";
    
        if( phaseName.equals( "tag.dep" ) )
            return ""
              +"disabled:true ";
    
        // The default default value is nothing.
        return "";
    }
  
    public void warnForeignPhase( String phaseName ) {
    
        if( phaseName.equals( "jb" ) ) return;
        if( phaseName.equals( "jb.asv" ) ) return;
        if( phaseName.equals( "jb.ulp" ) ) return;
        if( phaseName.equals( "jb.lns" ) ) return;
        if( phaseName.equals( "jb.cp" ) ) return;
        if( phaseName.equals( "jb.dae" ) ) return;
        if( phaseName.equals( "jb.ls" ) ) return;
        if( phaseName.equals( "jb.a" ) ) return;
        if( phaseName.equals( "jb.ule" ) ) return;
        if( phaseName.equals( "jb.tr" ) ) return;
        if( phaseName.equals( "jb.cp-ule" ) ) return;
        if( phaseName.equals( "jb.lp" ) ) return;
        if( phaseName.equals( "jb.ne" ) ) return;
        if( phaseName.equals( "jb.uce" ) ) return;
        if( phaseName.equals( "cg" ) ) return;
        if( phaseName.equals( "wstp" ) ) return;
        if( phaseName.equals( "wsop" ) ) return;
        if( phaseName.equals( "wjtp" ) ) return;
        if( phaseName.equals( "wjtp.Spark" ) ) return;
        if( phaseName.equals( "wjop" ) ) return;
        if( phaseName.equals( "wjop.smb" ) ) return;
        if( phaseName.equals( "wjop.si" ) ) return;
        if( phaseName.equals( "wjtp2" ) ) return;
        if( phaseName.equals( "wjtp2.ra" ) ) return;
        if( phaseName.equals( "stp" ) ) return;
        if( phaseName.equals( "sop" ) ) return;
        if( phaseName.equals( "jtp" ) ) return;
        if( phaseName.equals( "jop" ) ) return;
        if( phaseName.equals( "jop.cse" ) ) return;
        if( phaseName.equals( "jop.bcm" ) ) return;
        if( phaseName.equals( "jop.lcm" ) ) return;
        if( phaseName.equals( "jop.cp" ) ) return;
        if( phaseName.equals( "jop.cpf" ) ) return;
        if( phaseName.equals( "jop.cbf" ) ) return;
        if( phaseName.equals( "jop.dae" ) ) return;
        if( phaseName.equals( "jop.uce1" ) ) return;
        if( phaseName.equals( "jop.uce2" ) ) return;
        if( phaseName.equals( "jop.ubf1" ) ) return;
        if( phaseName.equals( "jop.ubf2" ) ) return;
        if( phaseName.equals( "jop.ule" ) ) return;
        if( phaseName.equals( "jap" ) ) return;
        if( phaseName.equals( "jap.npc" ) ) return;
        if( phaseName.equals( "jap.abc" ) ) return;
        if( phaseName.equals( "jap.profiling" ) ) return;
        if( phaseName.equals( "gb" ) ) return;
        if( phaseName.equals( "gb.a" ) ) return;
        if( phaseName.equals( "gb.asv1" ) ) return;
        if( phaseName.equals( "gb.asv2" ) ) return;
        if( phaseName.equals( "gb.cf" ) ) return;
        if( phaseName.equals( "gb.ule" ) ) return;
        if( phaseName.equals( "gop" ) ) return;
        if( phaseName.equals( "bb" ) ) return;
        if( phaseName.equals( "bb.lso" ) ) return;
        if( phaseName.equals( "bb.pho" ) ) return;
        if( phaseName.equals( "bb.ule" ) ) return;
        if( phaseName.equals( "bb.lp" ) ) return;
        if( phaseName.equals( "bop" ) ) return;
        if( phaseName.equals( "tag" ) ) return;
        if( phaseName.equals( "tag.ln" ) ) return;
        if( phaseName.equals( "tag.an" ) ) return;
        if( phaseName.equals( "tag.dep" ) ) return;
        G.v().out.println( "Warning: Phase "+phaseName+" is not a standard Soot phase listed in XML files." );
    }

    public void warnNonexistentPhase() {
    
        if( !PackManager.v().hasPhase( "jb" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb" );
        if( !PackManager.v().hasPhase( "jb.asv" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb.asv" );
        if( !PackManager.v().hasPhase( "jb.ulp" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb.ulp" );
        if( !PackManager.v().hasPhase( "jb.lns" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb.lns" );
        if( !PackManager.v().hasPhase( "jb.cp" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb.cp" );
        if( !PackManager.v().hasPhase( "jb.dae" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb.dae" );
        if( !PackManager.v().hasPhase( "jb.ls" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb.ls" );
        if( !PackManager.v().hasPhase( "jb.a" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb.a" );
        if( !PackManager.v().hasPhase( "jb.ule" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb.ule" );
        if( !PackManager.v().hasPhase( "jb.tr" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb.tr" );
        if( !PackManager.v().hasPhase( "jb.cp-ule" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb.cp-ule" );
        if( !PackManager.v().hasPhase( "jb.lp" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb.lp" );
        if( !PackManager.v().hasPhase( "jb.ne" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb.ne" );
        if( !PackManager.v().hasPhase( "jb.uce" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb.uce" );
        if( !PackManager.v().hasPhase( "cg" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase cg" );
        if( !PackManager.v().hasPhase( "wstp" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase wstp" );
        if( !PackManager.v().hasPhase( "wsop" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase wsop" );
        if( !PackManager.v().hasPhase( "wjtp" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase wjtp" );
        if( !PackManager.v().hasPhase( "wjtp.Spark" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase wjtp.Spark" );
        if( !PackManager.v().hasPhase( "wjop" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase wjop" );
        if( !PackManager.v().hasPhase( "wjop.smb" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase wjop.smb" );
        if( !PackManager.v().hasPhase( "wjop.si" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase wjop.si" );
        if( !PackManager.v().hasPhase( "wjtp2" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase wjtp2" );
        if( !PackManager.v().hasPhase( "wjtp2.ra" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase wjtp2.ra" );
        if( !PackManager.v().hasPhase( "stp" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase stp" );
        if( !PackManager.v().hasPhase( "sop" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase sop" );
        if( !PackManager.v().hasPhase( "jtp" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jtp" );
        if( !PackManager.v().hasPhase( "jop" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jop" );
        if( !PackManager.v().hasPhase( "jop.cse" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jop.cse" );
        if( !PackManager.v().hasPhase( "jop.bcm" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jop.bcm" );
        if( !PackManager.v().hasPhase( "jop.lcm" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jop.lcm" );
        if( !PackManager.v().hasPhase( "jop.cp" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jop.cp" );
        if( !PackManager.v().hasPhase( "jop.cpf" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jop.cpf" );
        if( !PackManager.v().hasPhase( "jop.cbf" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jop.cbf" );
        if( !PackManager.v().hasPhase( "jop.dae" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jop.dae" );
        if( !PackManager.v().hasPhase( "jop.uce1" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jop.uce1" );
        if( !PackManager.v().hasPhase( "jop.uce2" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jop.uce2" );
        if( !PackManager.v().hasPhase( "jop.ubf1" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jop.ubf1" );
        if( !PackManager.v().hasPhase( "jop.ubf2" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jop.ubf2" );
        if( !PackManager.v().hasPhase( "jop.ule" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jop.ule" );
        if( !PackManager.v().hasPhase( "jap" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jap" );
        if( !PackManager.v().hasPhase( "jap.npc" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jap.npc" );
        if( !PackManager.v().hasPhase( "jap.abc" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jap.abc" );
        if( !PackManager.v().hasPhase( "jap.profiling" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jap.profiling" );
        if( !PackManager.v().hasPhase( "gb" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase gb" );
        if( !PackManager.v().hasPhase( "gb.a" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase gb.a" );
        if( !PackManager.v().hasPhase( "gb.asv1" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase gb.asv1" );
        if( !PackManager.v().hasPhase( "gb.asv2" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase gb.asv2" );
        if( !PackManager.v().hasPhase( "gb.cf" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase gb.cf" );
        if( !PackManager.v().hasPhase( "gb.ule" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase gb.ule" );
        if( !PackManager.v().hasPhase( "gop" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase gop" );
        if( !PackManager.v().hasPhase( "bb" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase bb" );
        if( !PackManager.v().hasPhase( "bb.lso" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase bb.lso" );
        if( !PackManager.v().hasPhase( "bb.pho" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase bb.pho" );
        if( !PackManager.v().hasPhase( "bb.ule" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase bb.ule" );
        if( !PackManager.v().hasPhase( "bb.lp" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase bb.lp" );
        if( !PackManager.v().hasPhase( "bop" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase bop" );
        if( !PackManager.v().hasPhase( "tag" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase tag" );
        if( !PackManager.v().hasPhase( "tag.ln" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase tag.ln" );
        if( !PackManager.v().hasPhase( "tag.an" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase tag.an" );
        if( !PackManager.v().hasPhase( "tag.dep" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase tag.dep" );
    }
  
}
