<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" indent="no"/>

<xsl:template mode="to_latex" match="tt">{\tt <xsl:value-of select="."/>}</xsl:template>

<xsl:template mode="to_latex" match="b">{\bf <xsl:value-of select="."/>}</xsl:template>

<xsl:template mode="to_latex" match="cite">\cite{<xsl:value-of select="."/>}</xsl:template>

<xsl:template mode="to_latex" match="uscore">\_</xsl:template>

<xsl:template mode="to_latex" match="dollar">\$</xsl:template>

<!-- HTML links like <a href="http://foo">bar</a> -->
<xsl:template mode="to_latex" match="a">\htmladdnormallink{<xsl:value-of select="@href"/>}{<xsl:value-of select="."/>}</xsl:template>

</xsl:stylesheet>

