<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" indent="no"/>

<xsl:template match="/">
\documentclass{article}
\usepackage{fullpage}
\usepackage{html}

\title{Soot command-line options}
\author{Patrick Lam (\htmladdnormallink{plam@sable.mcgill.ca}{mailto:plam@sable.mcgill.ca})\\
Feng Qian (\htmladdnormallink{fqian@sable.mcgill.ca}{mailto:fqian@sable.mcgill.ca})\\
Ond\v{r}ej Lhot\'ak (\htmladdnormallink{olhotak@sable.mcgill.ca}{mailto:olhotak@sable.mcgill.ca})\\
}

\begin{document}

\maketitle

\tableofcontents

\section{SYNOPSIS}

Soot can be invoked in the following ways:

\begin{verbatim}
soot [option]* [classname]+
soot --app [option]* classname
\end{verbatim}


\section{DESCRIPTION}
This manual page documents the command line version of the {\tt soot}
bytecode compiler/optimizer tool.

When given one or several {\em classname}s that refers to a Java type,
and no {\tt -}{\tt -app} option, {\tt soot} will attempt to resolve it by
finding a file containing the given type. Once {\tt soot} has located
such a file, it will read in its contents, perform transformations on
its bytecode and output the type in a specified output format. This
mode of operation is referred to as running {\tt soot} in {\em
single-file mode}. All types specified on the commandline are resolved
and processed.  In this mode, the last file specified on the command-line
serves as the main class, when such a notion is needed.

The {\tt -}{\tt -app} argument can be used to activate {\tt soot}'s {\em
application mode}. In {\em application mode} {\tt soot} will perform a
transitive closure on the types listed in the constant pool of the
type provided on the command line. {\tt soot} will then proceed to
transform the types in this closure. The closure will contain Java
library types, as well as types particular to the application. By
default, only the application-specific types will be processed by {\tt
soot}. This can be overridden by command line options.  Clearly,
in this case, the file specified on the command-line is the main class.

To resolve a type, {\tt soot} uses the same semantics as the {\tt java}
command; {\tt soot} looks for files containing
types in the directories specified by the {\tt soot.class.path}
system property. This property serves the same purpose as {\tt
java}'s {\tt CLASSPATH} environment variable. There is also a command
line option to override the {\tt soot.class.path} property.  If there
is no Soot classpath, then the external Java {\tt CLASSPATH} is used.
(There is a note for Windows users in the section describing the
soot-class-path entry).

Once a type has been resolved and read into {\tt soot}, various
transformations can be applied to its code.  These are described in
the optimization section of the options below.

The Soot framework has 3 different internal representations: {\em
Baf}, {\em Jimple} and {\em Grimp}. {\tt soot} allows one to output a
processed class either as a standard classfile or in the textual format
corresponding to one of the above internal representations. Thus a
class can be outputted as a {\tt .baf} file, a {\tt.jimple} file or a
{\tt .grimple} file that will contain textual representations for the
{\em Baf }, {\em Jimple} and {\em Grimp} internal representations
respectively. Additionally a processed type can be outputed in the
{\em Jasmin} assembler format, as a {\tt .jasmin} file.

\section{OPTIONS}

<xsl:for-each select="options/section">
\subsection{<xsl:value-of select="name"/>}
<xsl:value-of select="long_desc|short_desc"/>

\begin{description}
<xsl:for-each select="boolopt|multiopt|listopt|phaseopt|stropt|macroopt">
  <xsl:call-template name="opt"/>
</xsl:for-each>
\end{description}

</xsl:for-each>

\end{document}

</xsl:template>

<xsl:template name="opt">
\item[{\tt <xsl:for-each select="alias">-<xsl:value-of select="."/><xsl:text> </xsl:text></xsl:for-each>}]
<xsl:if test="value/default">
(default value: {\tt 
<xsl:for-each select="value"><xsl:if test="default"><xsl:value-of select="alias"/></xsl:if></xsl:for-each>})
</xsl:if>

<xsl:value-of select="short_desc"/>

<xsl:text>

</xsl:text>
<xsl:if test="value">

Allowed values:\\
\begin{tabular}{lll}
<xsl:for-each select="value">
{\tt <xsl:for-each select="alias"><xsl:value-of select="."/><xsl:text> </xsl:text></xsl:for-each>}
&amp;
<xsl:value-of select="name"/>
&amp;
<xsl:value-of select="long_desc|short_desc"/>\\
</xsl:for-each>
\end{tabular}

</xsl:if>
<xsl:text>

</xsl:text>

<xsl:value-of select="long_desc"/>

</xsl:template>

</xsl:stylesheet>

