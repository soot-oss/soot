<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:include href="to_latex.xsl"/>
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

Soot can be invoked in the following way:

\begin{verbatim}
soot [option]* [classname]+
\end{verbatim}


\section{DESCRIPTION}
This manual page documents the command line options of the {\tt soot}
bytecode compiler/optimizer tool.

\section{OPTIONS}

<xsl:for-each select="options/section">
\subsection{<xsl:value-of select="name"/>}
<xsl:apply-templates mode="to_latex" select="long_desc|short_desc"/>

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

<xsl:apply-templates mode="to_latex" select="short_desc"/>

<xsl:text>

</xsl:text>
<xsl:apply-templates mode="to_latex" select="long_desc"/>\\
<xsl:text>

</xsl:text>

<xsl:if test="value">

Allowed values:\\
\begin{tabular}{p{1in}p{1.5in}p{3in}}
<xsl:for-each select="value">
{\tt <xsl:for-each select="alias"><xsl:value-of select="."/><xsl:text> </xsl:text></xsl:for-each>}
&amp;
<xsl:value-of select="name"/>
&amp;
<xsl:apply-templates mode="to_latex" select="long_desc"/>\\
</xsl:for-each>
\end{tabular}

</xsl:if>

</xsl:template>

</xsl:stylesheet>

