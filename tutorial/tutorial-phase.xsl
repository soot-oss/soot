<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:include href="to_latex.xsl"/>
<xsl:output method="text" indent="no"/>

<xsl:template match="/">
\documentclass{article}
\usepackage{fullpage}
\usepackage{html}

\title{Soot phase options}
\author{Patrick Lam (\htmladdnormallink{plam@sable.mcgill.ca}{mailto:plam@sable.mcgill.ca})\\
Feng Qian (\htmladdnormallink{fqian@sable.mcgill.ca}{mailto:fqian@sable.mcgill.ca})\\
Ond\v{r}ej Lhot\'ak (\htmladdnormallink{olhotak@sable.mcgill.ca}{mailto:olhotak@sable.mcgill.ca})\\
}

\begin{document}

\maketitle

Soot supports the powerful, but initially confusing, notion of ``phase
options''.  This document will permit the reader to
successfully use the Soot phase options.

Soot's execution is divided into a number of phases.  For example, building the
JimpleBody is a phase (called {\tt jb}), and it has a number of
subphases, like aggregation of stack variables ({\tt jb.a1}).

Soot allows the user to specify options for each phase; these options
will change the behaviour of the phase.  This is specified by giving Soot
the command-line option {\tt -p phase.name option:value}.  For instance,
to instruct Soot to use original names in Jimple, we would invoke Soot
like this:
\begin{verbatim}
java soot.Main foo -p jb use-original-names:true
\end{verbatim}
Multiple option-value pairs may be specified in a single {\tt -p} option
separated by commas. For example,
\begin{verbatim}
java soot.Main foo -p cg.spark verbose:true,on-fly-cg:true
\end{verbatim}

There are five types of phase options. Boolean options take the values
``true'' and ``false''; if no value is specified, ``true'' is assumed.
Multi-valued options have a set of valid values. Integer options
take a value that is an integer. Floating point options take a 
floating point number as their value. String options take an arbitrary
string as their value.

Each option has a default value which is used if the option is not
specified on the command line.

All phases and subphases accept the option ``{\tt enabled}'', which
must be ``{\tt true}'' for the phase/subphase to execute. To save
you some typing, the pseudo-options ``{\tt on}'' and ``{\tt off}''
are equivalent to ``{\tt enabled:true}'' and ``{\tt enabled:false}'',
respectively. In addition, specifying any options for a phase
automatically enables that phase.

Soot transfomers are expected to be classes extending either {\tt
BodyTransformer} or {\tt SceneTransformer}.  In either case, an {\tt
internalTransform} method on the transformer must be overridden to
provide an implementation which carries out some transformation.

These transformers become the subphases of a phase, which is represented
by a {\tt Pack}.  The {\tt Pack} keeps a 
collection of transformers, and can execute them, in order,
when called.  To add a transformer to some {\tt Pack} without
modifying Soot itself, create your own class, which modifies the
{\tt Pack}s as needed and then calls {\tt soot.Main}.

The remainder of this document describes the various transformations
belonging to the various Packs of Soot, and their corresponding phase options.

\tableofcontents

<xsl:for-each select="options/section/phaseopt/phase|options/section/phaseopt/radio_phase">
\section{<xsl:value-of select="name"/> ({\tt <xsl:value-of select="alias"/>})}
<xsl:call-template name="phase_section"/>

<xsl:for-each select="sub_phase">
\subsection{<xsl:value-of select="name"/> ({\tt <xsl:value-of select="alias"/>})}
<xsl:call-template name="phase_section"/>

<xsl:for-each select="section">
\subsubsection{<xsl:value-of select="name"/>}
<xsl:call-template name="phase_section"/>

</xsl:for-each>
</xsl:for-each>
</xsl:for-each>

\end{document}

</xsl:template>

<xsl:template name="phase_section">
<xsl:apply-templates mode="to_latex" select="long_desc|short_desc"/>

\paragraph{Options} 

\begin{description}
<xsl:apply-templates mode="opt" select="boolopt|multiopt|intopt|flopt|stropt"/>
\end{description}
</xsl:template>

<xsl:template mode="opt" match="boolopt|multiopt|intopt|flopt|stropt">
\item[<xsl:value-of select="name"/> ({\tt <xsl:value-of select="alias"/>})]
(default value: {\tt <xsl:choose>
<xsl:when test="default"><xsl:value-of select="default"/></xsl:when>
<xsl:when test="value/default"><xsl:for-each select="value"><xsl:if test="default"><xsl:value-of select="alias"/></xsl:if></xsl:for-each></xsl:when>
<xsl:otherwise>false</xsl:otherwise>
</xsl:choose>})
<xsl:apply-templates mode="to_latex" select="short_desc"/>
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
<xsl:apply-templates mode="to_latex" select="long_desc|short_desc"/>\\
</xsl:for-each>
\end{tabular}

</xsl:if>
<xsl:text>

</xsl:text>
<xsl:apply-templates mode="to_latex" select="long_desc"/>
</xsl:template>
</xsl:stylesheet>

