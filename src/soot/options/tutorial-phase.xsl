<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
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

Soot's execution is divided into a number of phases.  Building the
JimpleBody is a phase (called {\tt jb}), and it has a number of
subphases, like aggregation of stack variables ({\tt jb.asv}).

Soot allows the user to specify options for each phase; these options
will change the behaviour of the phase.  This is specified by giving Soot
the command-line option {\tt -p phase.name option:value}.  For instance,
to instruct Soot to use original names in Jimple, we would invoke Soot
like this:
\begin{verbatim}
[plam@cannanore test] java soot.Main foo -p jb use-original-names
\end{verbatim}

Unless specified otherwise, all options are boolean; allowed
values are ``true'' or ``false''.  When an option is omitted, the
default value is ``false''; specifying an option without a value
is the same as saying ``true''.

All transformers accept the option ``{\tt disabled}'', which,
when set to {\tt true}, causes the given transformer to not execute.

Soot transformers are expected to be classes extending either {\tt
BodyTransformer} or {\tt SceneTransformer}.  In either case, an {\tt
internalTransform} method on the transformer must be overridden to
provide an implementation which carries out some transformation.

These transformers belong to a {\tt Pack}.  The {\tt Pack} keeps a 
collection of transformers, and can execute them, in order,
when called.  To add a transformer to some {\tt Pack} without
modifying Soot itself, create your own class, which modifies the
{\tt Packs} as needed and then calls {\tt soot.Main}.

The remainder of this document describes the various transformations
belonging to the various Packs of Soot.

\tableofcontents

<xsl:for-each select="options/section/phaseopt/phase">
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
<xsl:value-of select="long_desc|short_desc"/>

\paragraph{Recognized options} 

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

