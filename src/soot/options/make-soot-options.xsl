<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:redirect="http://xml.apache.org/xalan/redirect"
    extension-element-prefixes="redirect"
>
  <xsl:output method="text" indent="no"/>
  <xsl:strip-space elements="*"/>

<!--*************************************************************************-->
<!--* ROOT TEMPLATE *********************************************************-->
<!--*************************************************************************-->

  <xsl:template match="/options">
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
    public static Options v() { return G.v().soot_options_Options(); }

<xsl:apply-templates mode="constants" select="/options/section"/>

    public boolean parse( String[] argv ) {
        LinkedList phaseOptions = new LinkedList();

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
<xsl:apply-templates mode="parse" select="/options/section"/>
            else {
                G.v().out.println( "Invalid option -"+option );
                return false;
            }
        }

        Iterator it = phaseOptions.iterator();
        while( it.hasNext() ) {
            String phaseName = (String) it.next();
            String phaseOption = (String) it.next();
            if( !setPhaseOption( phaseName, "enabled:true" ) ) return false;
        }

        it = phaseOptions.iterator();
        while( it.hasNext() ) {
            String phaseName = (String) it.next();
            String phaseOption = (String) it.next();
            if( !setPhaseOption( phaseName, phaseOption ) ) return false;
        }

        return true;
    }

<xsl:apply-templates mode="vars" select="/options/section"/>

    public String getUsage() {
        return ""
<xsl:apply-templates mode="usage" select="/options/section"/>;
    }

<xsl:apply-templates mode="phaselist" select="/options/section/phaseopt"/>
<xsl:apply-templates mode="declphaseopts" select="/options/section/phaseopt"/>
<xsl:apply-templates mode="warnforeign" select="/options/section/phaseopt"/>
}
<xsl:apply-templates mode="phaseopts" select="/options/section/phaseopt"/>
  </xsl:template>

<!--*************************************************************************-->
<!--* PARSE TEMPLATES *******************************************************-->
<!--*************************************************************************-->

  <xsl:template mode="parse" match="section">
      <xsl:apply-templates mode="parse" select="boolopt|multiopt|listopt|phaseopt|stropt|macroopt"/>
  </xsl:template>

<!--* BOOLEAN_OPTION *******************************************************-->
  <xsl:template mode="parse" match="boolopt">
            else if( false <xsl:text/>
    <xsl:for-each select="alias">
            || option.equals( "<xsl:value-of select="."/>" )<xsl:text/>
    </xsl:for-each>
            )
                <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = true;
  </xsl:template>

<!--* MULTI_OPTION *******************************************************-->
  <xsl:template mode="parse" match="multiopt">
            else if( false<xsl:text/>
    <xsl:for-each select="alias">
            || option.equals( "<xsl:value-of select="."/>" )<xsl:text/>
    </xsl:for-each>
            ) {
                if( !hasMoreOptions() ) {
                    G.v().out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    <xsl:variable name="name" select="translate(alias[last()],'-. ','___')"/>
                if( false );
    <xsl:for-each select="value">
                else if( false<xsl:text/>
      <xsl:for-each select="alias">
                || value.equals( "<xsl:value-of select="."/>" )<xsl:text/>
      </xsl:for-each>
                ) {
                    if( <xsl:copy-of select="$name"/> != 0
                    &#38;&#38; <xsl:copy-of select="$name"/> != <xsl:copy-of select="$name"/>_<xsl:value-of select="translate(alias[last()],'-. ','___')"/> ) {
                        G.v().out.println( "Multiple values given for option "+option );
                        return false;
                    }
                    <xsl:copy-of select="$name"/> = <xsl:copy-of select="$name"/>_<xsl:value-of select="translate(alias[last()],'-. ','___')"/>;
                }
    </xsl:for-each>
                else {
                    G.v().out.println( "Invalid value "+value+" given for option -"+option );
                    return false;
                }
           }
  </xsl:template>

<!--* PATH_OPTION *******************************************************-->
  <xsl:template mode="parse" match="listopt">
            else if( false<xsl:text/>
    <xsl:for-each select="alias">
            || option.equals( "<xsl:value-of select="."/>" )<xsl:text/>
    </xsl:for-each>
            ) {
                if( !hasMoreOptions() ) {
                    G.v().out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    <xsl:variable name="name" select="translate(alias[last()],'-. ','___')"/>
                if( <xsl:copy-of select="$name"/> == null )
                    <xsl:copy-of select="$name"/> = new LinkedList();

                <xsl:copy-of select="$name"/>.add( value );
            }
  </xsl:template>

<!--* PHASE_OPTION *******************************************************-->
  <xsl:template mode="parse" match="phaseopt">
            else if( false<xsl:text/>
    <xsl:for-each select="alias">
            || option.equals( "<xsl:value-of select="."/>" )<xsl:text/>
    </xsl:for-each>
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
    <xsl:variable name="name" select="translate(alias[last()],'-. ','___')"/>
                phaseOptions.add( phaseName );
                phaseOptions.add( phaseOption );
            }
  </xsl:template>

<!--* STRING_OPTION *******************************************************-->
  <xsl:template mode="parse" match="stropt">
            else if( false<xsl:text/>
    <xsl:for-each select="alias">
            || option.equals( "<xsl:value-of select="."/>" )<xsl:text/>
    </xsl:for-each>
            ) {
                if( !hasMoreOptions() ) {
                    G.v().out.println( "No value given for option -"+option );
                    return false;
                }
                String value = nextOption();
    <xsl:variable name="name" select="translate(alias[last()],'-. ','___')"/>
                if( <xsl:copy-of select="$name"/>.length() == 0 )
                    <xsl:copy-of select="$name"/> = value;
                else {
                    G.v().out.println( "Duplicate values "+<xsl:copy-of select="$name"/>+" and "+value+" for option -"+option );
                    return false;
                }
            }
  </xsl:template>

<!--* MACRO_OPTION *******************************************************-->
  <xsl:template mode="parse" match="macroopt">
            else if( false<xsl:text/>
    <xsl:for-each select="alias">
            || option.equals( "<xsl:value-of select="."/>" )<xsl:text/>
    </xsl:for-each>
            ) {
                <xsl:for-each select="expansion">
                <xsl:sort select="position()" data-type="number" order="descending"/><xsl:text/>
                pushOptions( "<xsl:value-of select="."/>" );<xsl:text/>
                </xsl:for-each>
            }
  </xsl:template>

<!--*************************************************************************-->
<!--* VARS TEMPLATES ********************************************************-->
<!--*************************************************************************-->

  <xsl:template mode="vars" match="section">
      <xsl:apply-templates mode="vars" select="boolopt|multiopt|listopt|phaseopt|stropt|macroopt"/>
  </xsl:template>

<!--* BOOLEAN_OPTION *******************************************************-->
  <xsl:template mode="vars" match="boolopt">
    public boolean <xsl:value-of select="translate(alias[last()],'-. ','___')"/>() { return <xsl:value-of select="translate(alias[last()],'-. ','___')"/>; }
    private boolean <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = false;<xsl:text/>
    public void set_<xsl:value-of select="translate(alias[last()],'-. ','___')"/>( boolean setting ) { <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = setting; }
  </xsl:template>

<!--* MULTI_OPTION *******************************************************-->
  <xsl:template mode="vars" match="multiopt">
    public int <xsl:value-of select="translate(alias[last()],'-. ','___')"/>() {<xsl:text/>
    <xsl:variable name="name" select="translate(alias[last()],'-. ','___')"/><xsl:text/>
    <xsl:for-each select="value">
      <xsl:if test="default"><xsl:text/>
        if( <xsl:value-of select="$name"/> == 0 ) return <xsl:value-of select="$name"/>_<xsl:value-of select="translate(alias[last()],'-. ','___')"/>;<xsl:text/>
      </xsl:if>
    </xsl:for-each>
        return <xsl:value-of select="translate(alias[last()],'-. ','___')"/>; 
    }
    public void set_<xsl:value-of select="translate(alias[last()],'-. ','___')"/>( int setting ) { <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = setting; }
    private int <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = 0;<xsl:text/>
  </xsl:template>

<!--* PATH_OPTION *******************************************************-->
  <xsl:template mode="vars" match="listopt">
    public List <xsl:value-of select="translate(alias[last()],'-. ','___')"/>() { 
        if( <xsl:value-of select="translate(alias[last()],'-. ','___')"/> == null )
            return java.util.Collections.EMPTY_LIST;
        else
            return <xsl:value-of select="translate(alias[last()],'-. ','___')"/>;
    }
    public void set_<xsl:value-of select="translate(alias[last()],'-. ','___')"/>( List setting ) { <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = setting; }
    private List <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = null;<xsl:text/>
  </xsl:template>

<!--* PHASE_OPTION *******************************************************-->
  <xsl:template mode="vars" match="phaseopt">
  </xsl:template>

<!--* STRING_OPTION *******************************************************-->
  <xsl:template mode="vars" match="stropt">
    public String <xsl:value-of select="translate(alias[last()],'-. ','___')"/>() { return <xsl:value-of select="translate(alias[last()],'-. ','___')"/>; }
    public void set_<xsl:value-of select="translate(alias[last()],'-. ','___')"/>( String setting ) { <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = setting; }
    private String <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = "";<xsl:text/>
  </xsl:template>

<!--* MACRO_OPTION *******************************************************-->
  <xsl:template mode="vars" match="macroopt">
  </xsl:template>

<!--*************************************************************************-->
<!--* CONSTANTS TEMPLATES ***************************************************-->
<!--*************************************************************************-->

  <xsl:template mode="constants" match="section">
      <xsl:apply-templates mode="constants" select="multiopt"/>
  </xsl:template>

<!--* MULTI_OPTION *******************************************************-->
  <xsl:template mode="constants" match="multiopt">
    <xsl:variable name="name" select="translate(alias[last()],'-. ','___')"/>
    <xsl:for-each select="value">
      <xsl:variable name="number"><xsl:number/></xsl:variable>
      <xsl:for-each select="alias">
    public static final int <xsl:copy-of select="$name"/>_<xsl:value-of select="translate(.,'-. ','___')"/> = <xsl:value-of select="$number"/>;<xsl:text/>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:template>

<!--*************************************************************************-->
<!--* USAGE TEMPLATES *******************************************************-->
<!--*************************************************************************-->

  <xsl:template mode="usage" match="section">
+"\n<xsl:value-of select="name"/>:\n"
      <xsl:apply-templates mode="usage" select="boolopt|multiopt|listopt|phaseopt|stropt|macroopt"/>
  </xsl:template>

<!--* BOOLEAN_OPTION *******************************************************-->
  <xsl:template mode="usage" match="boolopt">
+padOpt("<xsl:for-each select="alias"> -<xsl:value-of select="."/></xsl:for-each>", "<xsl:apply-templates select="short_desc"/>" )<xsl:text/>
  </xsl:template>

<!--* MULTI_OPTION *******************************************************-->
  <xsl:template mode="usage" match="multiopt">
+padOpt("<xsl:for-each select="alias"> -<xsl:value-of select="."/><xsl:text> </xsl:text><xsl:call-template name="arg-label"/></xsl:for-each>", "<xsl:apply-templates select="short_desc"/>" )<xsl:text/>
    <xsl:for-each select="value">
+padVal("<xsl:for-each select="alias"><xsl:value-of select="string(' ')"/><xsl:value-of select="."/></xsl:for-each><xsl:if test="default"> (default)</xsl:if>", "<xsl:value-of select="translate(short_desc,'&#10;',' ')"/>" )<xsl:text/>
      </xsl:for-each>
  </xsl:template>

<!--* PATH_OPTION *******************************************************-->
  <xsl:template mode="usage" match="listopt">
+padOpt("<xsl:for-each select="alias"> -<xsl:value-of select="."/><xsl:text> </xsl:text><xsl:call-template name="arg-label"/></xsl:for-each>", "<xsl:apply-templates select="short_desc"/>" )<xsl:text/>
  </xsl:template>

<!--* PHASE_OPTION *******************************************************-->
  <xsl:template mode="usage" match="phaseopt">
+padOpt("<xsl:for-each select="alias"> -<xsl:value-of select="."/><xsl:text> </xsl:text><xsl:call-template name="arg-label"/></xsl:for-each>", "<xsl:apply-templates select="short_desc"/>" )<xsl:text/>
  </xsl:template>

<!--* STRING_OPTION *******************************************************-->
  <xsl:template mode="usage" match="stropt">
+padOpt("<xsl:for-each select="alias"> -<xsl:value-of select="."/><xsl:text> </xsl:text><xsl:call-template name="arg-label"/></xsl:for-each>", "<xsl:apply-templates select="short_desc"/>" )<xsl:text/>
  </xsl:template>

<!--* MACRO_OPTION *******************************************************-->
  <xsl:template mode="usage" match="macroopt">
+padOpt("<xsl:for-each select="alias"> -<xsl:value-of select="."/></xsl:for-each>", "<xsl:apply-templates select="short_desc"/>" )<xsl:text/>
  </xsl:template>

<!--*************************************************************************-->
<!--* PHASE OPTION TEMPLATES ************************************************-->
<!--*************************************************************************-->

  <xsl:template mode="phaseopts" match="phaseopt">
    <xsl:for-each select="phase|radio_phase|phase/sub_phase|radio_phase/sub_phase">
      <xsl:if test="class">
        <xsl:variable name="filename" select="class"/>
        <xsl:variable name="fullname" select="concat($filename,'.java')"/>
        <xsl:variable name="fullpath" select="concat('generated/options/soot/options/',$fullname)"/>
        <redirect:write file="{$fullname}">
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

/** Option parser for <xsl:value-of select="name|name"/>. */
public class <xsl:copy-of select="$filename"/><xsl:if test="extends"> extends <xsl:copy-of select="extends"/></xsl:if>
{
    private Map options;

    public <xsl:copy-of select="$filename"/>( Map options ) {
        this.options = options;
    }
    <xsl:for-each select="boolopt|section/boolopt"><xsl:text/>
    /** <xsl:value-of select="name"/> --
    <xsl:call-template name="wrap-comment"><xsl:with-param name="text" select="short_desc"/></xsl:call-template>.
    <xsl:call-template name="wrap-comment"><xsl:with-param name="text" select="long_desc"/></xsl:call-template>
     */
    public boolean <xsl:value-of select="translate(alias[last()],'-. ','___')"/>() {
        return soot.PhaseOptions.getBoolean( options, "<xsl:value-of select="alias"/>" );
    }
    </xsl:for-each>
    <xsl:for-each select="intopt|section/intopt"><xsl:text/>
    /** <xsl:value-of select="name"/> --
    <xsl:call-template name="wrap-comment"><xsl:with-param name="text" select="short_desc"/></xsl:call-template>.
    <xsl:call-template name="wrap-comment"><xsl:with-param name="text" select="long_desc"/></xsl:call-template>
     */
    public int <xsl:value-of select="translate(alias[last()],'-. ','___')"/>() {
        return soot.PhaseOptions.getInt( options, "<xsl:value-of select="alias"/>" );
    }
    </xsl:for-each>
    <xsl:for-each select="flopt|section/flopt"><xsl:text/>
    /** <xsl:value-of select="name"/> --
    <xsl:call-template name="wrap-comment"><xsl:with-param name="text" select="short_desc"/></xsl:call-template>.
    <xsl:call-template name="wrap-comment"><xsl:with-param name="text" select="long_desc"/></xsl:call-template>
     */
    public float <xsl:value-of select="translate(alias[last()],'-. ','___')"/>() {
        return soot.PhaseOptions.getFloat( options, "<xsl:value-of select="alias"/>" );
    }
    </xsl:for-each>
    <xsl:for-each select="stropt|section/stropt"><xsl:text/>
    /** <xsl:value-of select="name"/> --
    <xsl:call-template name="wrap-comment"><xsl:with-param name="text" select="short_desc"/></xsl:call-template>.
    <xsl:call-template name="wrap-comment"><xsl:with-param name="text" select="long_desc"/></xsl:call-template>
     */
    public String <xsl:value-of select="translate(alias[last()],'-. ','___')"/>() {
        return soot.PhaseOptions.getString( options, "<xsl:value-of select="alias"/>" );
    }
    </xsl:for-each>
    <xsl:for-each select="multiopt|section/multiopt"><xsl:text/>
      <xsl:variable name="name" select="translate(alias[last()],'-. ','___')"/>
        <xsl:for-each select="value"><xsl:text/>
          <xsl:variable name="number"><xsl:number/></xsl:variable>
          <xsl:for-each select="alias">
    public static final int <xsl:value-of select="$name"/>_<xsl:value-of select="translate(.,'-. ','___')"/> = <xsl:value-of select="$number"/>;<xsl:text/>
          </xsl:for-each>
        </xsl:for-each>
    /** <xsl:value-of select="name"/> --
    <xsl:call-template name="wrap-comment"><xsl:with-param name="text" select="short_desc"/></xsl:call-template>.
    <xsl:call-template name="wrap-comment"><xsl:with-param name="text" select="long_desc"/></xsl:call-template>
     */
    public int <xsl:value-of select="translate(alias[last()],'-. ','___')"/>() {
        String s = soot.PhaseOptions.getString( options, "<xsl:value-of select="alias"/>" );
        <xsl:for-each select="value"><xsl:text/>
        if( s.equalsIgnoreCase( "<xsl:value-of select="alias"/>" ) )
            return <xsl:value-of select="$name"/>_<xsl:value-of select="translate(alias[last()],'-. ','___')"/>;
        </xsl:for-each>
        throw new RuntimeException( "Invalid value "+s+" of phase option <xsl:value-of select="alias"/>" );
    }
    </xsl:for-each>
}
        </redirect:write>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

<!--*************************************************************************-->
<!--* PHASE LIST TEMPLATES **************************************************-->
<!--*************************************************************************-->

  <xsl:template mode="phaselist" match="phaseopt">
    public String getPhaseList() {
        return ""
    <xsl:for-each select="phase|radio_phase"><xsl:text/>
        +padOpt("<xsl:value-of select="alias"/>", "<xsl:value-of select="short_desc"/>")<xsl:text/>
      <xsl:for-each select="sub_phase"><xsl:text/>
        +padVal("<xsl:value-of select="alias"/>", "<xsl:value-of select="short_desc"/>")<xsl:text/>
      </xsl:for-each>
    </xsl:for-each>;
    }

    public String getPhaseHelp( String phaseName ) {
    <xsl:for-each select="phase|radio_phase|phase/sub_phase|radio_phase/sub_phase">
        if( phaseName.equals( "<xsl:value-of select="alias|alias"/>" ) )
            return "Phase "+phaseName+":\n"+
                "<xsl:call-template name="wrap-string"><xsl:with-param name="text" select="long_desc"/></xsl:call-template>"<xsl:text/>
                +"\n\nRecognized options (with default values):\n"<xsl:text/>
      <xsl:for-each select="boolopt|multiopt|intopt|flopt|stropt|section/boolopt|section/multiopt|section/intopt|section/flopt|section/stropt"><xsl:text/>
                +padOpt( "<xsl:value-of select="alias"/><xsl:if test="default"> (<xsl:value-of select="default"/>)</xsl:if>", "<xsl:value-of select="translate(short_desc,'&#10;',' ')"/>" )<xsl:text/>
        <xsl:if test="value">
                <xsl:for-each select="value">
                +padVal( "<xsl:value-of select="alias"/><xsl:if test="default"> (default)</xsl:if>", "<xsl:value-of select="translate(short_desc,'&#10;',' ')"/>" )
                </xsl:for-each>
        </xsl:if>
      </xsl:for-each>;
    </xsl:for-each>

        return "Unrecognized phase: "+phaseName;
    }
  </xsl:template>

<!--*************************************************************************-->
<!--* DECLARED PHASE OPTION TEMPLATES ***************************************-->
<!--*************************************************************************-->

  <xsl:template mode="declphaseopts" match="phaseopt">
    public static String getDeclaredOptionsForPhase( String phaseName ) {
    <xsl:for-each select="phase|radio_phase|phase/sub_phase|radio_phase/sub_phase">
        if( phaseName.equals( "<xsl:value-of select="alias|alias"/>" ) )
            return ""<xsl:text/>
      <xsl:for-each select="boolopt|multiopt|intopt|flopt|stropt|section/boolopt|section/multiopt|section/intopt|section/flopt|section/stropt"><xsl:text/>
                +"<xsl:value-of select="alias"/> "<xsl:text/>
      </xsl:for-each>;
    </xsl:for-each>
        // The default set of options is just enabled.
        return "enabled";
    }

    public static String getDefaultOptionsForPhase( String phaseName ) {
    <xsl:for-each select="phase|radio_phase|phase/sub_phase|radio_phase/sub_phase">
        if( phaseName.equals( "<xsl:value-of select="alias|alias"/>" ) )
            return ""<xsl:text/>
      <xsl:for-each select="boolopt|multiopt|intopt|flopt|stropt|section/boolopt|section/multiopt|section/intopt|section/flopt|section/stropt"><xsl:text/>
            <xsl:if test="default">
              +"<xsl:value-of select="alias"/>:<xsl:value-of select="default"/> "<xsl:text/>
            </xsl:if>
            <xsl:variable name="key_alias" select="alias"/>
            <xsl:for-each select="value">
              <xsl:if test="default">
              +"<xsl:value-of select="$key_alias"/>:<xsl:value-of select="alias"/> "<xsl:text/>
              </xsl:if>
            </xsl:for-each>
      </xsl:for-each>;
    </xsl:for-each>
        // The default default value is enabled.
        return "enabled";
    }
  </xsl:template>

<!--*************************************************************************-->
<!--* WARN FOREIGN TEMPLATES ************************************************-->
<!--*************************************************************************-->

  <xsl:template mode="warnforeign" match="phaseopt">
    public void warnForeignPhase( String phaseName ) {
    <xsl:for-each select="phase|radio_phase|phase/sub_phase|radio_phase/sub_phase"><xsl:text/>
        if( phaseName.equals( "<xsl:value-of select="alias|alias"/>" ) ) return;<xsl:text/>
    </xsl:for-each>
        G.v().out.println( "Warning: Phase "+phaseName+" is not a standard Soot phase listed in XML files." );
    }

    public void warnNonexistentPhase() {
    <xsl:for-each select="phase|radio_phase|phase/sub_phase|radio_phase/sub_phase"><xsl:text/>
        if( !PackManager.v().hasPhase( "<xsl:value-of select="alias|alias"/>" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase <xsl:value-of select="alias|alias"/>" );<xsl:text/>
    </xsl:for-each>
    }
  </xsl:template>



<!-- code to justify comments -->
  <xsl:template name="wrap-string">
    <xsl:param name="text"/>
    <xsl:call-template name="wrap">
      <xsl:with-param name="text" select="$text"/>
      <xsl:with-param name="newline"><xsl:text>\n</xsl:text></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="wrap-comment">
    <xsl:param name="text"/>
    <xsl:call-template name="wrap">
      <xsl:with-param name="text" select="$text"/>
      <xsl:with-param name="newline"><xsl:text>
     * </xsl:text></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="wrap">
    <xsl:param name="text"/>
    <xsl:param name="newline"/>
    <xsl:call-template name="wrap-guts">
      <xsl:with-param name="text" select="translate($text,'&#10;',' ')"/>
      <xsl:with-param name="width" select='0'/>
      <xsl:with-param name="newline" select="$newline"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="wrap-guts">
    <xsl:param name="text"/>
    <xsl:param name="width"/>
    <xsl:param name="newline"/>
    <xsl:variable name="print" select="concat(substring-before(concat($text,' '),' '),' ')"/>
    <xsl:choose>
      <xsl:when test="string-length($print) > number($width)">
        <xsl:copy-of select="$newline"/>
        <xsl:call-template name="wrap-guts">
          <xsl:with-param name="text" select="$text"/>
          <xsl:with-param name="width" select='65'/>
          <xsl:with-param name="newline" select="$newline"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="substring($print,1,string-length($print)-1)"/>
        <xsl:if test="contains($text,' ')">
          <xsl:if test="string-length($print) > 1">
            <xsl:text> </xsl:text>
          </xsl:if>
          <xsl:call-template name="wrap-guts">
            <xsl:with-param name="text" select="substring-after($text,' ')"/>
            <xsl:with-param name="width" select="number($width) - string-length($print)"/>
            <xsl:with-param name="newline" select="$newline"/>
          </xsl:call-template>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="use_arg_label">
    <xsl:call-template name="arg-label"/>
  </xsl:template>

  <!-- Factored out so it can be used to print the argument labels in
       the option summary, e.g. "-src-prec format", 
       as well as argument labels in short_desc and long_desc. -->
  <xsl:template name="arg-label">
  <xsl:choose>
    <xsl:when test="ancestor::*/set_arg_label">
      <xsl:value-of select="translate(string(ancestor::*/set_arg_label),
                            'abcdefghijklmnopqrstuvwxyz',
                            'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
    </xsl:when>
    <xsl:otherwise>ARG</xsl:otherwise>
  </xsl:choose>
  </xsl:template>

  <xsl:template match="var">
  <xsl:value-of select="translate(string(),
                            'abcdefghijklmnopqrstuvwxyz',
                            'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
  </xsl:template>
</xsl:stylesheet>
