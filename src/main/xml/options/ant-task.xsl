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
 * Copyright (C) 2004 Ondrej Lhotak
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
 *
 */

/* THIS FILE IS AUTO-GENERATED FROM soot_options.xml. DO NOT MODIFY. */

package soot;
import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.*;
import org.apache.tools.ant.types.*;
import soot.*;
import java.util.*;
import java.io.*;

/** Soot ant task.
 * @author Ondrej Lhotak
 */

public class AntTask extends MatchingTask {
    public static final boolean DEBUG = true;
    private void debug(String s) {
        if(DEBUG) System.err.println(s);
    }
    private ArrayList args = new ArrayList();
    public List args() { return args; }
    private void addArg( String s ) { args.add(s); }
    private void addArg( String s, String s2 ) { args.add(s); args.add(s2); }
    private Path appendToPath( Path old, Path newPath ) {
        if( old == null ) return newPath;
        old.append(newPath);
        return old;
    }
    private void addPath(String option, Path path) {
        if( path.size() == 0 ) return;
        addArg(option);
        addArg(path.toString());
    }
    private List phaseopts = new ArrayList(); 
<xsl:apply-templates mode="pathsvars" select="/options/section/listopt"/>
    public void execute() throws BuildException {
<xsl:apply-templates mode="paths" select="/options/section/listopt"/>
        if(DEBUG) System.out.println(args);
        try {
            soot.Main.main((String[]) args.toArray(new String[0]));
            soot.G.v().reset();
        } catch( Exception e ) {
            e.printStackTrace();
            throw new BuildException(e);
        }
    }


<xsl:apply-templates mode="parse" select="/options/section"/>
<xsl:apply-templates mode="phaselist" select="/options/section/phaseopt"/>
}
  </xsl:template>

<!--*************************************************************************-->
<!--* PATHSVARS TEMPLATES ***************************************************-->
<!--*************************************************************************-->

  <xsl:template mode="pathsvars" match="listopt">
        private Path <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = null;
  </xsl:template>

<!--*************************************************************************-->
<!--* PATHS TEMPLATES *******************************************************-->
<!--*************************************************************************-->

  <xsl:template mode="paths" match="listopt">
        if( <xsl:value-of select="translate(alias[last()],'-. ','___')"/> != null ) addPath("-<xsl:value-of select="alias[last()]"/>", <xsl:value-of select="translate(alias[last()],'-. ','___')"/>);
  </xsl:template>

<!--*************************************************************************-->
<!--* PARSE TEMPLATES *******************************************************-->
<!--*************************************************************************-->

  <xsl:template mode="parse" match="section">
      <xsl:apply-templates mode="parse" select="boolopt|multiopt|listopt|phaseopt|stropt|macroopt"/>
  </xsl:template>

<!--* BOOLEAN_OPTION *******************************************************-->
  <xsl:template mode="parse" match="boolopt|macroopt">
        public void set<xsl:value-of select="translate(alias[last()],'-. ','___')"/>(boolean arg) {
            if(arg) addArg("-<xsl:value-of select="alias[last()]"/>");
        }
  </xsl:template>

<!--* MULTI_OPTION *******************************************************-->
  <xsl:template mode="parse" match="multiopt">
        public void set<xsl:value-of select="translate(alias[last()],'-. ','___')"/>(String arg) {
            if(false
    <xsl:for-each select="value">
      <xsl:for-each select="alias">
                || arg.equals( "<xsl:value-of select="."/>" )<xsl:text/>
      </xsl:for-each>
    </xsl:for-each>
                ) {
                addArg("-<xsl:value-of select="alias[last()]"/>");
                addArg(arg);
            } else {
                throw new BuildException("Bad value "+arg+" for option <xsl:value-of select="translate(alias[last()],'-. ','___')"/>");
            }
        }
  </xsl:template>

<!--* PATH_OPTION *******************************************************-->
  <xsl:template mode="parse" match="listopt">
        public void set<xsl:value-of select="translate(alias[last()],'-. ','___')"/>(Path arg) {
            if(<xsl:value-of select="translate(alias[last()],'-. ','___')"/> == null )
                <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = new Path(getProject());
            <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = appendToPath(<xsl:value-of select="translate(alias[last()],'-. ','___')"/>, arg);
        }

        public Path create<xsl:value-of select="translate(alias[last()],'-. ','___')"/>() {
            if(<xsl:value-of select="translate(alias[last()],'-. ','___')"/> == null )
                <xsl:value-of select="translate(alias[last()],'-. ','___')"/> = new Path(getProject());
            return <xsl:value-of select="translate(alias[last()],'-. ','___')"/>.createPath();
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
        public void set<xsl:value-of select="translate(alias[last()],'-. ','___')"/>(String arg) {
            addArg("-<xsl:value-of select="alias[last()]"/>");
            addArg(arg);
        }
  </xsl:template>

<!--* PHASE_OPTION *******************************************************-->
  <xsl:template mode="parse" match="phaseopt">
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


<!--*************************************************************************-->
<!--* PHASE LIST TEMPLATES **************************************************-->
<!--*************************************************************************-->

  <xsl:template mode="phaselist" match="phaseopt">
    <xsl:for-each select="phase|radio_phase|phase/sub_phase|radio_phase/sub_phase">
        <xsl:variable name="phasename" select="alias[last()]"/>
        public Object createp_<xsl:value-of select="translate(alias[last()],'-. ','___')"/>() {
            Object ret = new PhaseOpt<xsl:value-of select="translate(alias[last()],'-. ','___')"/>();
            phaseopts.add(ret);
            return ret;
        }
        public class PhaseOpt<xsl:value-of select="translate(alias[last()],'-. ','___')"/> {
      <xsl:for-each select="boolopt|section/boolopt"><xsl:text/>
          public void set<xsl:value-of select="translate(alias[last()],'-. ','___')"/>(boolean arg) {
            addArg("-p");
            addArg("<xsl:value-of select="$phasename"/>");
            addArg("<xsl:value-of select="alias[last()]"/>:"+(arg?"true":"false"));
          }
      </xsl:for-each>
      <xsl:for-each select="multiopt|intopt|flopt|stropt|section/multiopt|section/intopt|section/flopt|section/stropt"><xsl:text/>
          public void set<xsl:value-of select="translate(alias[last()],'-. ','___')"/>(String arg) {
            addArg("-p");
            addArg("<xsl:value-of select="$phasename"/>");
            addArg("<xsl:value-of select="alias[last()]"/>:"+arg);
          }
      </xsl:for-each>
        }
    </xsl:for-each>
  </xsl:template>

<!--*************************************************************************-->
<!--* DECLARED PHASE OPTION TEMPLATES ***************************************-->
<!--*************************************************************************-->

<!--*************************************************************************-->
<!--* WARN FOREIGN TEMPLATES ************************************************-->
<!--*************************************************************************-->

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
