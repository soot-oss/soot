<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
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
    public static Options v() { return G.v().Options(); }

<xsl:apply-templates mode="constants" select="/options/section"/>

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
<xsl:apply-templates mode="parse" select="/options/section"/>
            else {
                G.v().out.println( "Invalid option -"+option );
                return false;
            }
        }
        return true;
    }
<xsl:apply-templates mode="vars" select="/options/section"/>

    public String getUsage() {
        return ""
<xsl:apply-templates mode="usage" select="/options/section"/>
        + getPhaseUsage();
    }
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
            else if( false <!---->
    <xsl:for-each select="alias">
            || option.equals( "<xsl:value-of select="."/>" )<!---->
    </xsl:for-each>
            )
                <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = true;
  </xsl:template>

<!--* MULTI_OPTION *******************************************************-->
  <xsl:template mode="parse" match="multiopt">
            else if( false<!---->
    <xsl:for-each select="alias">
            || option.equals( "<xsl:value-of select="."/>" )<!---->
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
                else if( false<!---->
      <xsl:for-each select="alias">
                || value.equals( "<xsl:value-of select="."/>" )<!---->
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
            else if( false<!---->
    <xsl:for-each select="alias">
            || option.equals( "<xsl:value-of select="."/>" )<!---->
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
            else if( false<!---->
    <xsl:for-each select="alias">
            || option.equals( "<xsl:value-of select="."/>" )<!---->
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
                if( !setPhaseOption( phaseName, phaseOption ) )
                    return false;
            }
  </xsl:template>

<!--* STRING_OPTION *******************************************************-->
  <xsl:template mode="parse" match="stropt">
            else if( false<!---->
    <xsl:for-each select="alias">
            || option.equals( "<xsl:value-of select="."/>" )<!---->
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
            else if( false<!---->
    <xsl:for-each select="alias">
            || option.equals( "<xsl:value-of select="."/>" )<!---->
    </xsl:for-each>
            ) {
                <xsl:for-each select="expansion">
                <xsl:sort select="position()" data-type="number" order="descending"/><!---->
                pushOptions( "<xsl:value-of select="."/>" );<!---->
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
    private boolean <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = false;<!---->
    public void set_<xsl:value-of select="translate(alias[last()],'-. ','___')"/>( boolean setting ) { <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = setting; }
  </xsl:template>

<!--* MULTI_OPTION *******************************************************-->
  <xsl:template mode="vars" match="multiopt">
    public int <xsl:value-of select="translate(alias[last()],'-. ','___')"/>() {<!---->
    <xsl:variable name="name" select="translate(alias[last()],'-. ','___')"/><!---->
    <xsl:for-each select="value">
      <xsl:if test="default"><!---->
        if( <xsl:value-of select="$name"/> == 0 ) return <xsl:value-of select="$name"/>_<xsl:value-of select="translate(alias[last()],'-. ','___')"/>;<!---->
      </xsl:if>
    </xsl:for-each>
        return <xsl:value-of select="translate(alias[last()],'-. ','___')"/>; 
    }
    public void set_<xsl:value-of select="translate(alias[last()],'-. ','___')"/>( int setting ) { <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = setting; }
    private int <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = 0;<!---->
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
    private List <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = null;<!---->
  </xsl:template>

<!--* PHASE_OPTION *******************************************************-->
  <xsl:template mode="vars" match="phaseopt">
  </xsl:template>

<!--* STRING_OPTION *******************************************************-->
  <xsl:template mode="vars" match="stropt">
    public String <xsl:value-of select="translate(alias[last()],'-. ','___')"/>() { return <xsl:value-of select="translate(alias[last()],'-. ','___')"/>; }
    public void set_<xsl:value-of select="translate(alias[last()],'-. ','___')"/>( String setting ) { <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = setting; }
    private String <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = "";<!---->
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
    public static final int <xsl:copy-of select="$name"/>_<xsl:value-of select="translate(alias[last()],'-. ','___')"/> = <xsl:number/>;<!---->
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
+padOpt("<xsl:for-each select="alias"> -<xsl:value-of select="."/></xsl:for-each>", "<xsl:value-of select="short_desc"/>" )<!---->
  </xsl:template>

<!--* MULTI_OPTION *******************************************************-->
  <xsl:template mode="usage" match="multiopt">
+padOpt("<xsl:for-each select="alias"> -<xsl:value-of select="."/> ARG</xsl:for-each>", "<xsl:value-of select="short_desc"/>" )<!---->
    <xsl:for-each select="value">
+padVal("<xsl:for-each select="alias"><xsl:value-of select="string(' ')"/><xsl:value-of select="."/></xsl:for-each>", "<xsl:value-of select="value"/>" )<!---->
      </xsl:for-each>
  </xsl:template>

<!--* PATH_OPTION *******************************************************-->
  <xsl:template mode="usage" match="listopt">
+padOpt("<xsl:for-each select="alias"> -<xsl:value-of select="."/> ARG</xsl:for-each>", "<xsl:value-of select="short_desc"/>" )<!---->
  </xsl:template>

<!--* PHASE_OPTION *******************************************************-->
  <xsl:template mode="usage" match="phaseopt">
+padOpt("<xsl:for-each select="alias"> -<xsl:value-of select="."/> PHASE-NAME PHASE-OPTIONS</xsl:for-each>", "<xsl:value-of select="short_desc"/>" )<!---->
  </xsl:template>

<!--* STRING_OPTION *******************************************************-->
  <xsl:template mode="usage" match="stropt">
+padOpt("<xsl:for-each select="alias"> -<xsl:value-of select="."/> ARG</xsl:for-each>", "<xsl:value-of select="short_desc"/>" )<!---->
  </xsl:template>

<!--* MACRO_OPTION *******************************************************-->
  <xsl:template mode="usage" match="macroopt">
+padOpt("<xsl:for-each select="alias"> -<xsl:value-of select="."/></xsl:for-each>", "<xsl:value-of select="short_desc"/>" )<!---->
  </xsl:template>

<!--*************************************************************************-->
<!--* PHASE OPTION TEMPLATES ************************************************-->
<!--*************************************************************************-->

  <xsl:template mode="phaseopts" match="phaseopt">
    <xsl:for-each select="phase|radio_phase|phase/sub_phase|radio_phase/sub_phase">
      <xsl:if test="class">
        <xsl:variable name="filename" select="class"/>
        <xsl:document href="src/soot/options/{$filename}.java" method="text" indent="no">
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
public class <xsl:copy-of select="$filename"/>
{
    private Map options;

    public <xsl:copy-of select="$filename"/>( Map options ) {
        this.options = options;
    }
    <xsl:for-each select="boolopt|section/boolopt"><!---->
    /** <xsl:value-of select="name"/> --
    <xsl:call-template name="wrap-comment"><xsl:with-param name="text" select="short_desc"/></xsl:call-template>.
    <xsl:call-template name="wrap-comment"><xsl:with-param name="text" select="long_desc"/></xsl:call-template>
     */
    public boolean <xsl:value-of select="translate(alias[last()],'-. ','___')"/>() {
        return soot.PhaseOptions.getBoolean( options, "<xsl:value-of select="alias"/>" );
    }
    </xsl:for-each>
    <xsl:for-each select="intopt|section/intopt"><!---->
    /** <xsl:value-of select="name"/> --
    <xsl:call-template name="wrap-comment"><xsl:with-param name="text" select="short_desc"/></xsl:call-template>.
    <xsl:call-template name="wrap-comment"><xsl:with-param name="text" select="long_desc"/></xsl:call-template>
     */
    public int <xsl:value-of select="translate(alias[last()],'-. ','___')"/>() {
        return soot.PhaseOptions.getInt( options, "<xsl:value-of select="alias"/>" );
    }
    </xsl:for-each>
    <xsl:for-each select="flopt|section/flopt"><!---->
    /** <xsl:value-of select="name"/> --
    <xsl:call-template name="wrap-comment"><xsl:with-param name="text" select="short_desc"/></xsl:call-template>.
    <xsl:call-template name="wrap-comment"><xsl:with-param name="text" select="long_desc"/></xsl:call-template>
     */
    public float <xsl:value-of select="translate(alias[last()],'-. ','___')"/>() {
        return soot.PhaseOptions.getFloat( options, "<xsl:value-of select="alias"/>" );
    }
    </xsl:for-each>
    <xsl:for-each select="stropt|section/stropt"><!---->
    /** <xsl:value-of select="name"/> --
    <xsl:call-template name="wrap-comment"><xsl:with-param name="text" select="short_desc"/></xsl:call-template>.
    <xsl:call-template name="wrap-comment"><xsl:with-param name="text" select="long_desc"/></xsl:call-template>
     */
    public String <xsl:value-of select="translate(alias[last()],'-. ','___')"/>() {
        return soot.PhaseOptions.getString( options, "<xsl:value-of select="alias"/>" );
    }
    </xsl:for-each>
    <xsl:for-each select="multiopt|section/multiopt"><!---->
      <xsl:variable name="name" select="translate(alias[last()],'-. ','___')"/>
        <xsl:for-each select="value"><!---->
    public static final int <xsl:value-of select="$name"/>_<xsl:value-of select="translate(alias[last()],'-. ','___')"/> = <xsl:number/>;<!---->
        </xsl:for-each>
    /** <xsl:value-of select="name"/> --
    <xsl:call-template name="wrap-comment"><xsl:with-param name="text" select="short_desc"/></xsl:call-template>.
    <xsl:call-template name="wrap-comment"><xsl:with-param name="text" select="long_desc"/></xsl:call-template>
     */
    public int <xsl:value-of select="translate(alias[last()],'-. ','___')"/>() {
        String s = soot.PhaseOptions.getString( options, "<xsl:value-of select="alias"/>" );
        <xsl:for-each select="value"><!---->
        if( s.equalsIgnoreCase( "<xsl:value-of select="alias"/>" ) )
            return <xsl:value-of select="$name"/>_<xsl:value-of select="translate(alias[last()],'-. ','___')"/>;
        </xsl:for-each>
        throw new RuntimeException( "Invalid value "+s+" of phase option <xsl:value-of select="alias"/>" );
    }
    </xsl:for-each>
}
        </xsl:document>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

<!--*************************************************************************-->
<!--* DECLARED PHASE OPTION TEMPLATES ***************************************-->
<!--*************************************************************************-->

  <xsl:template mode="declphaseopts" match="phaseopt">
    public static String getDeclaredOptionsForPhase( String phaseName ) {
    <xsl:for-each select="phase|radio_phase|phase/sub_phase|radio_phase/sub_phase">
        if( phaseName.equals( "<xsl:value-of select="alias|alias"/>" ) )
            return ""<!---->
      <xsl:for-each select="boolopt|multiopt|intopt|flopt|stropt|section/boolopt|section/multiopt|section/intopt|section/flopt|section/stropt"><!---->
                +"<xsl:value-of select="alias"/> "<!---->
      </xsl:for-each>;
    </xsl:for-each>
        // The default set of options is just enabled.
        return "enabled";
    }

    public static String getDefaultOptionsForPhase( String phaseName ) {
    <xsl:for-each select="phase|radio_phase|phase/sub_phase|radio_phase/sub_phase">
        if( phaseName.equals( "<xsl:value-of select="alias|alias"/>" ) )
            return ""<!---->
      <xsl:for-each select="boolopt|multiopt|intopt|flopt|stropt|section/boolopt|section/multiopt|section/intopt|section/flopt|section/stropt"><!---->
            <xsl:if test="default">
              +"<xsl:value-of select="alias"/>:<xsl:value-of select="default"/> "<!---->
            </xsl:if>
            <xsl:variable name="key_alias" select="alias"/>
            <xsl:for-each select="value">
              <xsl:if test="default">
              +"<xsl:value-of select="$key_alias"/>:<xsl:value-of select="alias"/> "<!---->
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
    <xsl:for-each select="phase|radio_phase|phase/sub_phase|radio_phase/sub_phase"><!---->
        if( phaseName.equals( "<xsl:value-of select="alias|alias"/>" ) ) return;<!---->
    </xsl:for-each>
        G.v().out.println( "Warning: Phase "+phaseName+" is not a standard Soot phase listed in XML files." );
    }

    public void warnNonexistentPhase() {
    <xsl:for-each select="phase|radio_phase|phase/sub_phase|radio_phase/sub_phase"><!---->
        if( !PackManager.v().hasPhase( "<xsl:value-of select="alias|alias"/>" ) )
            G.v().out.println( "Warning: Options exist for non-existent phase <xsl:value-of select="alias|alias"/>" );<!---->
    </xsl:for-each>
    }
  </xsl:template>



<!-- code to justify comments -->
  <xsl:template name="wrap-comment">
    <xsl:param name="text"/>
    <xsl:call-template name="wrap-comment-guts">
      <xsl:with-param name="text" select="translate($text,'&#10;',' ')"/>
      <xsl:with-param name="width" select='0'/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="wrap-comment-guts">
    <xsl:param name="text"/>
    <xsl:param name="width"/>
    <xsl:variable name="print" select="concat(substring-before(concat($text,' '),' '),' ')"/>
    <xsl:choose>
      <xsl:when test="string-length($print) > number($width)">
      <xsl:text>
     * </xsl:text>
          <xsl:call-template name="wrap-comment-guts">
            <xsl:with-param name="text" select="$text"/>
            <xsl:with-param name="width" select='65'/>
          </xsl:call-template>
       </xsl:when>
       <xsl:otherwise>
        <xsl:copy-of select="substring($print,1,string-length($print)-1)"/>
        <xsl:if test="contains($text,' ')">
          <xsl:if test="string-length($print) > 1">
            <xsl:text> </xsl:text>
          </xsl:if>
          <xsl:call-template name="wrap-comment-guts">
            <xsl:with-param name="text" select="substring-after($text,' ')"/>
            <xsl:with-param name="width" select="number($width) - string-length($print)"/>
          </xsl:call-template>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
