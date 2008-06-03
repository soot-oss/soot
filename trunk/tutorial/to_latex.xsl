<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" indent="no"/>

<xsl:template mode="to_latex" match="tt">{\tt <xsl:apply-templates mode="to_latex"/>}</xsl:template>

<xsl:template mode="to_latex" match="b">{\bf <xsl:apply-templates mode="to_latex"/>}</xsl:template>

<xsl:template mode="to_latex" match="i">{\it <xsl:apply-templates mode="to_latex"/>}</xsl:template>

<xsl:template mode="to_latex" match="var">{\it <xsl:apply-templates mode="to_latex"/>}</xsl:template>

<xsl:template mode="to_latex" match="cite">\cite{<xsl:apply-templates mode="to_latex"/>}</xsl:template>

<xsl:template mode="to_latex" match="uscore">\_</xsl:template>

<xsl:template mode="to_latex" match="dollar">\$</xsl:template>

<xsl:template mode="to_latex" match="lt"><xsl:choose><xsl:when test="parent::pre">&lt;</xsl:when><xsl:otherwise>\ensuremath{&lt;}</xsl:otherwise></xsl:choose></xsl:template>

<xsl:template mode="to_latex" match="gt"><xsl:choose><xsl:when test="parent::pre">&gt;</xsl:when><xsl:otherwise>\ensuremath{&gt;}</xsl:otherwise></xsl:choose></xsl:template>

<xsl:template mode="to_latex" match="pre">
\begin{quote}\begin{verbatim}
<xsl:apply-templates mode="to_latex"/>
\end{verbatim}\end{quote}
</xsl:template>

<xsl:template mode="to_latex" match="p">
\par
<xsl:apply-templates mode="to_latex"/>
</xsl:template>

<!-- HTML links like <a href="http://foo">bar</a> -->
<xsl:template mode="to_latex" match="a">\htmladdnormallink{<xsl:value-of select="."/>}{<xsl:call-template name="string-replace"><xsl:with-param name="text" select="@href"/><xsl:with-param name="from" select="'#'"/><xsl:with-param name="to" select="'\#'"/></xsl:call-template>}</xsl:template>

 <!-- reusable replace-string function -->
 <xsl:template name="string-replace">
    <xsl:param name="text"/>
    <xsl:param name="from"/>
    <xsl:param name="to"/>

    <xsl:choose>
      <xsl:when test="contains($text, $from)">

	<xsl:variable name="before" select="substring-before($text, $from)"/>
	<xsl:variable name="after" select="substring-after($text, $from)"/>
	<xsl:variable name="prefix" select="concat($before, $to)"/>

	<xsl:value-of select="$before"/>
	<xsl:value-of select="$to"/>
        <xsl:call-template name="string-replace">
	  <xsl:with-param name="text" select="$after"/>
	  <xsl:with-param name="from" select="$from"/>
	  <xsl:with-param name="to" select="$to"/>
	</xsl:call-template>
      </xsl:when> 
      <xsl:otherwise>
        <xsl:value-of select="$text"/>  
      </xsl:otherwise>
    </xsl:choose>            
 </xsl:template>

</xsl:stylesheet>

