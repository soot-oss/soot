<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:include href="../to_latex.xsl"/>
<xsl:output method="text" indent="no"/>

<xsl:template match="/">
\documentclass{article}
\usepackage{fullpage}
\usepackage{html}
\usepackage{longtable}

\title{Soot phase options}
\author{Patrick Lam (\htmladdnormallink{plam@sable.mcgill.ca}{mailto:plam@sable.mcgill.ca})\\
Feng Qian (\htmladdnormallink{fqian@sable.mcgill.ca}{mailto:fqian@sable.mcgill.ca})\\
Ond\v{r}ej Lhot\'ak (\htmladdnormallink{olhotak@sable.mcgill.ca}{mailto:olhotak@sable.mcgill.ca})\\
John Jorgensen\\ 
}

\begin{document}

\maketitle

Soot supports the powerful---but initially confusing---notion of
``phase options''.  This document aims to clear up the confusion so
you can exploit the power of phase options.

Soot's execution is divided into a number of phases.  For example,
{\tt JimpleBody}s are built by a phase called {\tt jb}, which is
itself comprised of subphases, such as the aggregation of local
variables ({\tt jb.a}).

Phase options provide a way for you to
change the behaviour of a phase from the Soot command-line.  They take
the form {\tt -p }{\em phase}.{\em name} 
{\em option}:{\em value}.  For instance,
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

There are five types of phase options:
\begin{enumerate}
\item Boolean options take the values
``true'' and ``false''; if you specify the name of a boolean option without adding a value for it, ``true'' is assumed.
\item
Multi-valued options take a value from a set of allowed values
specific to that option.
\item Integer options
take an integer value. 
\item Floating point options take a 
floating point number as their value. 
\item String options take an arbitrary
string as their value.
\end{enumerate}

Each option has a default value which is used if the option is not
specified on the command line.

All phases and subphases accept the option ``{\tt enabled}'', which
must be ``{\tt true}'' for the phase or subphase to execute. To save
you some typing, the pseudo-options ``{\tt on}'' and ``{\tt off}''
are equivalent to ``{\tt enabled:true}'' and ``{\tt enabled:false}'',
respectively. In addition, specifying any options for a phase
automatically enables that phase.

\paragraph{Adding your own subphases}

\noindent
\par
Within Soot, each phase is implemented by a {\tt Pack}. The {\tt Pack}
is a collection of transformers, each corresponding to a subphase of
the phase implemented by the {\tt Pack}. When the {\tt Pack} is
called, it executes each of its transformers in order.

Soot transformers are usually instances of classes that extend 
{\tt BodyTransformer} or {\tt SceneTransformer}.  In either case, the
transformer class must override the {\tt internalTransform} method,
providing an implementation which carries out some transformation on
the code being analyzed.

To add a transformer to some {\tt Pack} without modifying Soot itself,
create your own class which changes the contents of the {\tt Pack}s to
meet your requirements and then calls {\tt soot.Main}.

\vspace{3ex}

The remainder of this document describes the transformations belonging
to Soot's various {\tt Pack}s and their corresponding phase
options.

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

<xsl:apply-templates mode="to_latex" select="long_desc"/>

\paragraph{Accepted phase options:} 

\begin{description}
<xsl:apply-templates mode="opt" select="boolopt|multiopt|intopt|flopt|stropt"/>
\end{description}
</xsl:template>

<xsl:template mode="opt" match="boolopt|multiopt|intopt|flopt|stropt">
\item[<xsl:value-of select="name"/> ({\tt <xsl:value-of select="alias"/>})]
(default value: {\tt <xsl:choose>
<xsl:when test="default"><xsl:value-of select="default"/></xsl:when>
<xsl:when test="value/default"><xsl:value-of select="value/default/../alias"/></xsl:when>
<xsl:otherwise>false</xsl:otherwise>
</xsl:choose>})

<xsl:text>

</xsl:text>
<xsl:apply-templates mode="to_latex" select="long_desc"/>
<xsl:text>

</xsl:text>
<xsl:if test="value">

Possible values:\\
\begin{longtable}{p{1in}p{4in}}
<xsl:for-each select="value">
{\tt <xsl:for-each select="alias"><xsl:value-of select="."/><xsl:if test="count(./following-sibling::alias) > 0">,</xsl:if><xsl:text> </xsl:text></xsl:for-each>}
&amp;
<xsl:apply-templates mode="to_latex" select="long_desc"/>\\
</xsl:for-each>
\end{longtable}

</xsl:if>
</xsl:template>
</xsl:stylesheet>

