
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
    public Options(Singletons.Global g) { }
    public static Options v() { return G.v().Options(); }


    public static final int src_prec_c = 1;
    public static final int src_prec_class = 1;
    public static final int src_prec_J = 2;
    public static final int src_prec_jimple = 2;
    public static final int output_format_j = 1;
    public static final int output_format_jimp = 1;
    public static final int output_format_J = 2;
    public static final int output_format_jimple = 2;
    public static final int output_format_shimp = 3;
    public static final int output_format_S = 4;
    public static final int output_format_shimple = 4;
    public static final int output_format_B = 5;
    public static final int output_format_baf = 5;
    public static final int output_format_b = 6;
    public static final int output_format_g = 7;
    public static final int output_format_grimp = 7;
    public static final int output_format_G = 8;
    public static final int output_format_grimple = 8;
    public static final int output_format_X = 9;
    public static final int output_format_xml = 9;
    public static final int output_format_n = 10;
    public static final int output_format_none = 10;
    public static final int output_format_s = 11;
    public static final int output_format_jasmin = 11;
    public static final int output_format_c = 12;
    public static final int output_format_class = 12;
    public static final int output_format_d = 13;
    public static final int output_format_dava = 13;

    public boolean parse( String[] argv ) {
        for( int i = argv.length; i > 0; i-- ) {
            pushOptions( argv[i-1] );
        }
        while( hasMoreOptions() ) {
            String option = nextOption();
            if( option.charAt(0) != '-' ) {
                classes.add( option );
                continue;
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
            || option.equals( "w" )
            || option.equals( "whole-program" )
            )
                whole_program = true;
  
            else if( false 
            || option.equals( "debug" )
            )
                debug = true;
  
            else if( false
            || option.equals( "cp" )
            || option.equals( "soot-class-path" )
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
            || option.equals( "f" )
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
                || value.equals( "shimp" )
                ) {
                    if( output_format != 0
                    && output_format != output_format_shimp ) {
                        G.v().out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    output_format = output_format_shimp;
                }
    
                else if( false
                || value.equals( "S" )
                || value.equals( "shimple" )
                ) {
                    if( output_format != 0
                    && output_format != output_format_shimple ) {
                        G.v().out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    output_format = output_format_shimple;
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
                
                pushOptions( "enabled:true" );
                pushOptions( "sop" );
                pushOptions( "-p" );
                pushOptions( "enabled:true" );
                pushOptions( "jop" );
                pushOptions( "-p" );
                pushOptions( "enabled:true" );
                pushOptions( "gop" );
                pushOptions( "-p" );
                pushOptions( "enabled:true" );
                pushOptions( "bop" );
                pushOptions( "-p" );
                pushOptions( "only-stack-locals:false" );
                pushOptions( "gb.a2" );
                pushOptions( "-p" );
                pushOptions( "only-stack-locals:false" );
                pushOptions( "gb.a1" );
                pushOptions( "-p" );
            }
  
            else if( false
            || option.equals( "W" )
            || option.equals( "whole-optimize" )
            ) {
                
                pushOptions( "-w" );
                pushOptions( "enabled:true" );
                pushOptions( "wjop" );
                pushOptions( "-p" );
            }
  
            else if( false 
            || option.equals( "via-shimple" )
            )
                via_shimple = true;
  
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
                
                pushOptions( "enabled:true" );
                pushOptions( "tag.an" );
                pushOptions( "-p" );
                pushOptions( "enabled:true" );
                pushOptions( "jap.npc" );
                pushOptions( "-p" );
            }
  
            else if( false
            || option.equals( "annot-arraybounds" )
            ) {
                
                pushOptions( "enabled:true" );
                pushOptions( "tag.an" );
                pushOptions( "-p" );
                pushOptions( "enabled:true" );
                pushOptions( "jap.abc" );
                pushOptions( "-p" );
                pushOptions( "enabled:true" );
                pushOptions( "wjap.ra" );
                pushOptions( "-p" );
            }
  
            else if( false
            || option.equals( "annot-side-effect" )
            ) {
                
                pushOptions( "enabled:true" );
                pushOptions( "tag.dep" );
                pushOptions( "-p" );
                pushOptions( "enabled:true" );
                pushOptions( "jap.sea" );
                pushOptions( "-p" );
                pushOptions( "-w" );
            }
  
            else if( false
            || option.equals( "annot-fieldrw" )
            ) {
                
                pushOptions( "enabled:true" );
                pushOptions( "tag.fieldrw" );
                pushOptions( "-p" );
                pushOptions( "enabled:true" );
                pushOptions( "jap.fieldrw" );
                pushOptions( "-p" );
                pushOptions( "-w" );
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
    public void set_help( boolean setting ) { help = setting; }
  
    public boolean version() { return version; }
    private boolean version = false;
    public void set_version( boolean setting ) { version = setting; }
  
    public boolean verbose() { return verbose; }
    private boolean verbose = false;
    public void set_verbose( boolean setting ) { verbose = setting; }
  
    public boolean app() { return app; }
    private boolean app = false;
    public void set_app( boolean setting ) { app = setting; }
  
    public boolean whole_program() { return whole_program; }
    private boolean whole_program = false;
    public void set_whole_program( boolean setting ) { whole_program = setting; }
  
    public boolean debug() { return debug; }
    private boolean debug = false;
    public void set_debug( boolean setting ) { debug = setting; }
  
    public String soot_classpath() { return soot_classpath; }
    public void set_soot_classpath( String setting ) { soot_classpath = setting; }
    private String soot_classpath = "";
    public int src_prec() {
        if( src_prec == 0 ) return src_prec_class;
        return src_prec; 
    }
    public void set_src_prec( int setting ) { src_prec = setting; }
    private int src_prec = 0;
    public boolean allow_phantom_refs() { return allow_phantom_refs; }
    private boolean allow_phantom_refs = false;
    public void set_allow_phantom_refs( boolean setting ) { allow_phantom_refs = setting; }
  
    public String output_dir() { return output_dir; }
    public void set_output_dir( String setting ) { output_dir = setting; }
    private String output_dir = "";
    public int output_format() {
        if( output_format == 0 ) return output_format_class;
        return output_format; 
    }
    public void set_output_format( int setting ) { output_format = setting; }
    private int output_format = 0;
    public boolean via_grimp() { return via_grimp; }
    private boolean via_grimp = false;
    public void set_via_grimp( boolean setting ) { via_grimp = setting; }
  
    public boolean xml_attributes() { return xml_attributes; }
    private boolean xml_attributes = false;
    public void set_xml_attributes( boolean setting ) { xml_attributes = setting; }
  
    public boolean via_shimple() { return via_shimple; }
    private boolean via_shimple = false;
    public void set_via_shimple( boolean setting ) { via_shimple = setting; }
  
    public List process_path() { 
        if( process_path == null )
            return java.util.Collections.EMPTY_LIST;
        else
            return process_path;
    }
    public void set_process_path( List setting ) { process_path = setting; }
    private List process_path = null;
    public List include() { 
        if( include == null )
            return java.util.Collections.EMPTY_LIST;
        else
            return include;
    }
    public void set_include( List setting ) { include = setting; }
    private List include = null;
    public List exclude() { 
        if( exclude == null )
            return java.util.Collections.EMPTY_LIST;
        else
            return exclude;
    }
    public void set_exclude( List setting ) { exclude = setting; }
    private List exclude = null;
    public List dynamic_classes() { 
        if( dynamic_classes == null )
            return java.util.Collections.EMPTY_LIST;
        else
            return dynamic_classes;
    }
    public void set_dynamic_classes( List setting ) { dynamic_classes = setting; }
    private List dynamic_classes = null;
    public List dynamic_path() { 
        if( dynamic_path == null )
            return java.util.Collections.EMPTY_LIST;
        else
            return dynamic_path;
    }
    public void set_dynamic_path( List setting ) { dynamic_path = setting; }
    private List dynamic_path = null;
    public List dynamic_package() { 
        if( dynamic_package == null )
            return java.util.Collections.EMPTY_LIST;
        else
            return dynamic_package;
    }
    public void set_dynamic_package( List setting ) { dynamic_package = setting; }
    private List dynamic_package = null;
    public boolean keep_line_number() { return keep_line_number; }
    private boolean keep_line_number = false;
    public void set_keep_line_number( boolean setting ) { keep_line_number = setting; }
  
    public boolean keep_offset() { return keep_offset; }
    private boolean keep_offset = false;
    public void set_keep_offset( boolean setting ) { keep_offset = setting; }
  
    public boolean time() { return time; }
    private boolean time = false;
    public void set_time( boolean setting ) { time = setting; }
  
    public boolean subtract_gc() { return subtract_gc; }
    private boolean subtract_gc = false;
    public void set_subtract_gc( boolean setting ) { subtract_gc = setting; }
  

    public String getUsage() {
        return ""

+"\nGeneral Options:\n"
      
+padOpt(" -h -help", "Display Help and Exit" )
+padOpt(" -version", "Display Version Information and Exit" )
+padOpt(" -v -verbose", "Verbose Mode" )
+padOpt(" -app", "Run in Application Mode" )
+padOpt(" -w -whole-program", "Run in Whole-program Mode" )
+padOpt(" -debug", "Prints Various Soot Debugging Info" )
+"\nInput Options:\n"
      
+padOpt(" -cp ARG -soot-class-path ARG -soot-classpath ARG", "Uses given PATH as the classpath for finding classes for Soot processing." )
+padOpt(" -src-prec ARG", "Sets Source Precedence for Soot" )
+padVal(" c class", "" )
+padVal(" J jimple", "" )
+padOpt(" -allow-phantom-refs", "Allow unresolved classes: may cause errors" )
+"\nOutput Options:\n"
      
+padOpt(" -d ARG -output-dir ARG", "Store Produced Files in PATH" )
+padOpt(" -f ARG -output-format ARG", "Sets Output Format for Soot" )
+padVal(" j jimp", "" )
+padVal(" J jimple", "" )
+padVal(" shimp", "" )
+padVal(" S shimple", "" )
+padVal(" B baf", "" )
+padVal(" b", "" )
+padVal(" g grimp", "" )
+padVal(" G grimple", "" )
+padVal(" X xml", "" )
+padVal(" n none", "" )
+padVal(" s jasmin", "" )
+padVal(" c class", "" )
+padVal(" d dava", "" )
+padOpt(" -via-grimp", "Convert Jimple to Bytecode via Grimp Instead of via Baf" )
+padOpt(" -xml-attributes", "Save tags to XML attributes for Eclipse" )
+"\nProcessing Options:\n"
      
+padOpt(" -p PHASE-NAME PHASE-OPTIONS -phase-option PHASE-NAME PHASE-OPTIONS", "set run-time option KEY to VALUE for PHASE-NAME" )
+padOpt(" -O -optimize", "perform scalar optimizations on the classfiles" )
+padOpt(" -W -whole-optimize", "perform whole program optimizations on the classfiles" )
+padOpt(" -via-shimple", "enables phases operating on Shimple SSA representation" )
+"\nSingle File Mode Options:\n"
      
+padOpt(" -process-path ARG", "Process all Classes on the PATH" )
+"\nApplication Mode Options:\n"
      
+padOpt(" -i ARG -include ARG", "Marks Classfiles in PACKAGE (e.g. java.util.) as Application Classes" )
+padOpt(" -x ARG -exclude ARG", "Marks Classfiles in PACKAGE (e.g. java.) as Context Classes" )
+padOpt(" -dynamic-classes ARG", "Marks CLASSES (separated by colons) as Potentially Dynamic Classes" )
+padOpt(" -dynamic-path ARG", "Marks all Class Files in PATH as Potentially Dynamic Classes" )
+padOpt(" -dynamic-package ARG", "Marks Class Files in PACKAGES (separated by commas) as Potentially Dynamic Classes" )
+"\nInput Attribute Options:\n"
      
+padOpt(" -keep-line-number", "Keep Line Number Tables" )
+padOpt(" -keep-bytecode-offset -keep-offset", "Attach Bytecode Offset to Jimple Statement" )
+"\nAnnotation Options:\n"
      
+padOpt(" -annot-nullpointer", "Turn on the Annotation for Null Pointer" )
+padOpt(" -annot-arraybounds", "Turn on the Annotation for Array Bounds Check" )
+padOpt(" -annot-side-effect", "Turn on Side-effect Attributes" )
+padOpt(" -annot-fieldrw", "Turn on Field Read/Write Attributes" )
+"\nMiscellaneous Options:\n"
      
+padOpt(" -time", "Print out Time Statistics about Tranformations" )
+padOpt(" -subtract-gc", "Attempt to Subtract the gc from the Time Stats" )
        + getPhaseUsage();
    }

    public static String getDeclaredOptionsForPhase( String phaseName ) {
    
        if( phaseName.equals( "jb" ) )
            return ""
                +"enabled "
                +"use-original-names ";
    
        if( phaseName.equals( "jb.ls" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "jb.a1" ) )
            return ""
                +"enabled "
                +"only-stack-locals ";
    
        if( phaseName.equals( "jb.ule1" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "jb.tr" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "jb.a2" ) )
            return ""
                +"enabled "
                +"only-stack-locals ";
    
        if( phaseName.equals( "jb.ule2" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "jb.ulp" ) )
            return ""
                +"enabled "
                +"unsplit-original-locals ";
    
        if( phaseName.equals( "jb.lns" ) )
            return ""
                +"enabled "
                +"only-stack-locals ";
    
        if( phaseName.equals( "jb.cp" ) )
            return ""
                +"enabled "
                +"only-regular-locals "
                +"only-stack-locals ";
    
        if( phaseName.equals( "jb.dae" ) )
            return ""
                +"enabled "
                +"only-stack-locals ";
    
        if( phaseName.equals( "jb.cp-ule" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "jb.lp" ) )
            return ""
                +"enabled "
                +"unsplit-original-locals ";
    
        if( phaseName.equals( "jb.ne" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "jb.uce" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "cg" ) )
            return ""
                +"enabled "
                +"safe-forname "
                +"verbose ";
    
        if( phaseName.equals( "cg.cha" ) )
            return ""
                +"enabled "
                +"verbose ";
    
        if( phaseName.equals( "cg.spark" ) )
            return ""
                +"enabled "
                +"verbose "
                +"ignore-types "
                +"force-gc "
                +"pre-jimplify "
                +"vta "
                +"rta "
                +"field-based "
                +"types-for-sites "
                +"merge-stringbuffer "
                +"simulate-natives "
                +"simple-edges-bidirectional "
                +"on-fly-cg "
                +"parms-as-fields "
                +"returns-as-fields "
                +"simplify-offline "
                +"simplify-sccs "
                +"ignore-types-for-sccs "
                +"propagator "
                +"set-impl "
                +"double-set-old "
                +"double-set-new "
                +"dump-html "
                +"dump-pag "
                +"dump-solution "
                +"topo-sort "
                +"dump-types "
                +"class-method-var "
                +"dump-answer "
                +"add-tags "
                +"set-mass ";
    
        if( phaseName.equals( "wjtp" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "wjop" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "wjop.smb" ) )
            return ""
                +"enabled "
                +"insert-null-checks "
                +"insert-redundant-casts "
                +"allowed-modifier-changes ";
    
        if( phaseName.equals( "wjop.si" ) )
            return ""
                +"enabled "
                +"insert-null-checks "
                +"insert-redundant-casts "
                +"allowed-modifier-changes "
                +"expansion-factor "
                +"max-container-size "
                +"max-inlinee-size ";
    
        if( phaseName.equals( "wjap" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "wjap.ra" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "sb" ) )
            return ""
                +"enabled "
                +"naive-phi-elimination "
                +"pre-optimize-phi-elimination "
                +"post-optimize-phi-elimination ";
    
        if( phaseName.equals( "stp" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "sop" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "sop.cpf" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "jtp" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "jop" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "jop.cse" ) )
            return ""
                +"enabled "
                +"naive-side-effect ";
    
        if( phaseName.equals( "jop.bcm" ) )
            return ""
                +"enabled "
                +"naive-side-effect ";
    
        if( phaseName.equals( "jop.lcm" ) )
            return ""
                +"enabled "
                +"safe "
                +"unroll "
                +"naive-side-effect ";
    
        if( phaseName.equals( "jop.cp" ) )
            return ""
                +"enabled "
                +"only-regular-locals "
                +"only-stack-locals ";
    
        if( phaseName.equals( "jop.cpf" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "jop.cbf" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "jop.dae" ) )
            return ""
                +"enabled "
                +"only-stack-locals ";
    
        if( phaseName.equals( "jop.uce1" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "jop.uce2" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "jop.ubf1" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "jop.ubf2" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "jop.ule" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "jap" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "jap.npc" ) )
            return ""
                +"enabled "
                +"only-array-ref "
                +"profiling ";
    
        if( phaseName.equals( "jap.abc" ) )
            return ""
                +"enabled "
                +"with-all "
                +"with-fieldref "
                +"with-arrayref "
                +"with-cse "
                +"with-classfield "
                +"with-rectarray "
                +"profiling ";
    
        if( phaseName.equals( "jap.profiling" ) )
            return ""
                +"enabled "
                +"notmainentry ";
    
        if( phaseName.equals( "jap.sea" ) )
            return ""
                +"enabled "
                +"naive ";
    
        if( phaseName.equals( "jap.fieldrw" ) )
            return ""
                +"enabled "
                +"threshold ";
    
        if( phaseName.equals( "jap.cgtagger" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "gb" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "gb.a1" ) )
            return ""
                +"enabled "
                +"only-stack-locals ";
    
        if( phaseName.equals( "gb.cf" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "gb.a2" ) )
            return ""
                +"enabled "
                +"only-stack-locals ";
    
        if( phaseName.equals( "gb.ule" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "gop" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "bb" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "bb.lso" ) )
            return ""
                +"enabled "
                +"debug "
                +"inter "
                +"sl "
                +"sl2 "
                +"sll "
                +"sll2 ";
    
        if( phaseName.equals( "bb.pho" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "bb.ule" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "bb.lp" ) )
            return ""
                +"enabled "
                +"unsplit-original-locals ";
    
        if( phaseName.equals( "bop" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "tag" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "tag.ln" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "tag.an" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "tag.dep" ) )
            return ""
                +"enabled ";
    
        if( phaseName.equals( "tag.fieldrw" ) )
            return ""
                +"enabled ";
    
        // The default set of options is just enabled.
        return "enabled";
    }

    public static String getDefaultOptionsForPhase( String phaseName ) {
    
        if( phaseName.equals( "jb" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "jb.ls" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "jb.a1" ) )
            return ""
              +"enabled:true "
              +"only-stack-locals:true ";
    
        if( phaseName.equals( "jb.ule1" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "jb.tr" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "jb.a2" ) )
            return ""
              +"enabled:true "
              +"only-stack-locals:true ";
    
        if( phaseName.equals( "jb.ule2" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "jb.ulp" ) )
            return ""
              +"enabled:true "
              +"unsplit-original-locals:true ";
    
        if( phaseName.equals( "jb.lns" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "jb.cp" ) )
            return ""
              +"enabled:true "
              +"only-stack-locals:true ";
    
        if( phaseName.equals( "jb.dae" ) )
            return ""
              +"enabled:true "
              +"only-stack-locals:true ";
    
        if( phaseName.equals( "jb.cp-ule" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "jb.lp" ) )
            return ""
              +"enabled:false ";
    
        if( phaseName.equals( "jb.ne" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "jb.uce" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "cg" ) )
            return ""
              +"enabled:true "
              +"safe-forname:false "
              +"verbose:false ";
    
        if( phaseName.equals( "cg.cha" ) )
            return ""
              +"enabled:true "
              +"verbose:false ";
    
        if( phaseName.equals( "cg.spark" ) )
            return ""
              +"enabled:false "
              +"verbose:false "
              +"ignore-types:false "
              +"force-gc:false "
              +"pre-jimplify:false "
              +"vta:false "
              +"rta:false "
              +"field-based:false "
              +"types-for-sites:false "
              +"merge-stringbuffer:true "
              +"simulate-natives:true "
              +"simple-edges-bidirectional:false "
              +"on-fly-cg:true "
              +"parms-as-fields:false "
              +"returns-as-fields:false "
              +"simplify-offline:false "
              +"simplify-sccs:false "
              +"ignore-types-for-sccs:false "
              +"propagator:worklist "
              +"set-impl:double "
              +"double-set-old:hybrid "
              +"double-set-new:hybrid "
              +"dump-html:false "
              +"dump-pag:false "
              +"dump-solution:false "
              +"topo-sort:false "
              +"dump-types:true "
              +"class-method-var:true "
              +"dump-answer:false "
              +"add-tags:false "
              +"set-mass:false ";
    
        if( phaseName.equals( "wjtp" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "wjop" ) )
            return ""
              +"enabled:false ";
    
        if( phaseName.equals( "wjop.smb" ) )
            return ""
              +"enabled:false "
              +"insert-null-checks:true "
              +"insert-redundant-casts:true "
              +"allowed-modifier-changes:unsafe ";
    
        if( phaseName.equals( "wjop.si" ) )
            return ""
              +"enabled:true "
              +"insert-null-checks:true "
              +"insert-redundant-casts:true "
              +"allowed-modifier-changes:unsafe "
              +"expansion-factor:3 "
              +"max-container-size:5000 "
              +"max-inlinee-size:20 ";
    
        if( phaseName.equals( "wjap" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "wjap.ra" ) )
            return ""
              +"enabled:false ";
    
        if( phaseName.equals( "sb" ) )
            return ""
              +"enabled:true "
              +"naive-phi-elimination:false "
              +"pre-optimize-phi-elimination:false "
              +"post-optimize-phi-elimination:true ";
    
        if( phaseName.equals( "stp" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "sop" ) )
            return ""
              +"enabled:false ";
    
        if( phaseName.equals( "sop.cpf" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "jtp" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "jop" ) )
            return ""
              +"enabled:false ";
    
        if( phaseName.equals( "jop.cse" ) )
            return ""
              +"enabled:false ";
    
        if( phaseName.equals( "jop.bcm" ) )
            return ""
              +"enabled:false ";
    
        if( phaseName.equals( "jop.lcm" ) )
            return ""
              +"enabled:false "
              +"safe:safe "
              +"unroll:true ";
    
        if( phaseName.equals( "jop.cp" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "jop.cpf" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "jop.cbf" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "jop.dae" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "jop.uce1" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "jop.uce2" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "jop.ubf1" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "jop.ubf2" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "jop.ule" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "jap" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "jap.npc" ) )
            return ""
              +"enabled:false ";
    
        if( phaseName.equals( "jap.abc" ) )
            return ""
              +"enabled:false ";
    
        if( phaseName.equals( "jap.profiling" ) )
            return ""
              +"enabled:false "
              +"notmainentry:false ";
    
        if( phaseName.equals( "jap.sea" ) )
            return ""
              +"enabled:false "
              +"naive:false ";
    
        if( phaseName.equals( "jap.fieldrw" ) )
            return ""
              +"enabled:false "
              +"threshold:100 ";
    
        if( phaseName.equals( "jap.cgtagger" ) )
            return ""
              +"enabled:false ";
    
        if( phaseName.equals( "gb" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "gb.a1" ) )
            return ""
              +"enabled:true "
              +"only-stack-locals:true ";
    
        if( phaseName.equals( "gb.cf" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "gb.a2" ) )
            return ""
              +"enabled:true "
              +"only-stack-locals:true ";
    
        if( phaseName.equals( "gb.ule" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "gop" ) )
            return ""
              +"enabled:false ";
    
        if( phaseName.equals( "bb" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "bb.lso" ) )
            return ""
              +"enabled:true "
              +"sl:true "
              +"sll:true ";
    
        if( phaseName.equals( "bb.pho" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "bb.ule" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "bb.lp" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "bop" ) )
            return ""
              +"enabled:false ";
    
        if( phaseName.equals( "tag" ) )
            return ""
              +"enabled:true ";
    
        if( phaseName.equals( "tag.ln" ) )
            return ""
              +"enabled:false ";
    
        if( phaseName.equals( "tag.an" ) )
            return ""
              +"enabled:false ";
    
        if( phaseName.equals( "tag.dep" ) )
            return ""
              +"enabled:false ";
    
        if( phaseName.equals( "tag.fieldrw" ) )
            return ""
              +"enabled:false ";
    
        // The default default value is enabled.
        return "enabled";
    }
  
    public void warnForeignPhase( String phaseName ) {
    
        if( phaseName.equals( "jb" ) ) return;
        if( phaseName.equals( "jb.ls" ) ) return;
        if( phaseName.equals( "jb.a1" ) ) return;
        if( phaseName.equals( "jb.ule1" ) ) return;
        if( phaseName.equals( "jb.tr" ) ) return;
        if( phaseName.equals( "jb.a2" ) ) return;
        if( phaseName.equals( "jb.ule2" ) ) return;
        if( phaseName.equals( "jb.ulp" ) ) return;
        if( phaseName.equals( "jb.lns" ) ) return;
        if( phaseName.equals( "jb.cp" ) ) return;
        if( phaseName.equals( "jb.dae" ) ) return;
        if( phaseName.equals( "jb.cp-ule" ) ) return;
        if( phaseName.equals( "jb.lp" ) ) return;
        if( phaseName.equals( "jb.ne" ) ) return;
        if( phaseName.equals( "jb.uce" ) ) return;
        if( phaseName.equals( "cg" ) ) return;
        if( phaseName.equals( "cg.cha" ) ) return;
        if( phaseName.equals( "cg.spark" ) ) return;
        if( phaseName.equals( "wjtp" ) ) return;
        if( phaseName.equals( "wjop" ) ) return;
        if( phaseName.equals( "wjop.smb" ) ) return;
        if( phaseName.equals( "wjop.si" ) ) return;
        if( phaseName.equals( "wjap" ) ) return;
        if( phaseName.equals( "wjap.ra" ) ) return;
        if( phaseName.equals( "sb" ) ) return;
        if( phaseName.equals( "stp" ) ) return;
        if( phaseName.equals( "sop" ) ) return;
        if( phaseName.equals( "sop.cpf" ) ) return;
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
        if( phaseName.equals( "jap.sea" ) ) return;
        if( phaseName.equals( "jap.fieldrw" ) ) return;
        if( phaseName.equals( "jap.cgtagger" ) ) return;
        if( phaseName.equals( "gb" ) ) return;
        if( phaseName.equals( "gb.a1" ) ) return;
        if( phaseName.equals( "gb.cf" ) ) return;
        if( phaseName.equals( "gb.a2" ) ) return;
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
        if( phaseName.equals( "tag.fieldrw" ) ) return;
        G.v().out.println( "Warning: Phase "+phaseName+" is not a standard Soot phase listed in XML files." );
    }

    public void warnNonexistentPhase() {
    
        if( !PackManager.v().hasPhase( "jb" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb" );
        if( !PackManager.v().hasPhase( "jb.ls" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb.ls" );
        if( !PackManager.v().hasPhase( "jb.a1" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb.a1" );
        if( !PackManager.v().hasPhase( "jb.ule1" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb.ule1" );
        if( !PackManager.v().hasPhase( "jb.tr" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb.tr" );
        if( !PackManager.v().hasPhase( "jb.a2" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb.a2" );
        if( !PackManager.v().hasPhase( "jb.ule2" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb.ule2" );
        if( !PackManager.v().hasPhase( "jb.ulp" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb.ulp" );
        if( !PackManager.v().hasPhase( "jb.lns" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb.lns" );
        if( !PackManager.v().hasPhase( "jb.cp" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb.cp" );
        if( !PackManager.v().hasPhase( "jb.dae" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jb.dae" );
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
        if( !PackManager.v().hasPhase( "cg.cha" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase cg.cha" );
        if( !PackManager.v().hasPhase( "cg.spark" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase cg.spark" );
        if( !PackManager.v().hasPhase( "wjtp" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase wjtp" );
        if( !PackManager.v().hasPhase( "wjop" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase wjop" );
        if( !PackManager.v().hasPhase( "wjop.smb" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase wjop.smb" );
        if( !PackManager.v().hasPhase( "wjop.si" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase wjop.si" );
        if( !PackManager.v().hasPhase( "wjap" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase wjap" );
        if( !PackManager.v().hasPhase( "wjap.ra" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase wjap.ra" );
        if( !PackManager.v().hasPhase( "sb" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase sb" );
        if( !PackManager.v().hasPhase( "stp" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase stp" );
        if( !PackManager.v().hasPhase( "sop" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase sop" );
        if( !PackManager.v().hasPhase( "sop.cpf" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase sop.cpf" );
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
        if( !PackManager.v().hasPhase( "jap.sea" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jap.sea" );
        if( !PackManager.v().hasPhase( "jap.fieldrw" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jap.fieldrw" );
        if( !PackManager.v().hasPhase( "jap.cgtagger" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase jap.cgtagger" );
        if( !PackManager.v().hasPhase( "gb" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase gb" );
        if( !PackManager.v().hasPhase( "gb.a1" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase gb.a1" );
        if( !PackManager.v().hasPhase( "gb.cf" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase gb.cf" );
        if( !PackManager.v().hasPhase( "gb.a2" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase gb.a2" );
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
        if( !PackManager.v().hasPhase( "tag.fieldrw" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase tag.fieldrw" );
    }
  
}
