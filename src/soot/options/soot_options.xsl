<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
<html>
<head>
<title>
Soot Command Line Options
</title>
</head>

<body>

<table border="3">

<xsl:for-each select="options/section">
<tr><td colspan="3">
<h2><xsl:value-of select="name"/></h2>
</td></tr>

<xsl:for-each select="boolopt|listopt|multiopt|stropt|macroopt|phaseopt">

<xsl:if test="value">
<tr>
<td>
<xsl:for-each select="alias">
<tt>-<xsl:value-of select="."/></tt><br/>
</xsl:for-each>
</td>
<td>
<xsl:for-each select="value">
<xsl:for-each select="alias">
<tt><xsl:value-of select="."/>&#160;</tt>
</xsl:for-each><br/>
</xsl:for-each>
</td>
<td>
<xsl:value-of select="short_desc"/>
</td></tr>
</xsl:if>

<xsl:if test="not(value)">
<tr>
<td>
<xsl:for-each select="alias">
<tt>-<xsl:value-of select="."/></tt><br/>
</xsl:for-each>
</td>
<td colspan="2">
<xsl:value-of select="short_desc"/>
</td></tr>
</xsl:if>

</xsl:for-each>
</xsl:for-each>

</table>
</body>
</html>

<h1>Phases and phase options</h1>
<ul>
<xsl:for-each select="options/section/phaseopt/phase|options/section/phaseopt/radio_phase">
<li/><xsl:value-of select="alias|alias"/>
<ul>
<ul>
<xsl:apply-templates mode="opt" select="boolopt|multiopt|intopt|flopt|stropt|section/boolopt|section/multiopt|section/intopt|section/flopt|section/stropt"/>
</ul>
</ul>
<ul>
<xsl:for-each select="sub_phase">
<li/><xsl:value-of select="alias|alias"/>
<ul>
<xsl:apply-templates mode="opt" select="boolopt|multiopt|intopt|flopt|stropt|section/boolopt|section/multiopt|section/intopt|section/flopt|section/stropt"/>
</ul>
</xsl:for-each>
</ul>
</xsl:for-each>
</ul>

</xsl:template>

<xsl:template mode="opt" match="boolopt|multiopt|intopt|flopt|stropt|section/boolopt|section/multiopt|section/intopt|section/flopt|section/stropt">
<li/>
<tt><xsl:value-of select="alias"/>
<xsl:choose>
<xsl:when test="default">
: <xsl:value-of select="default"/>
</xsl:when>
<xsl:when test="value/default">
: <xsl:for-each select="value"><xsl:if test="default"><xsl:value-of select="alias"/></xsl:if></xsl:for-each>
</xsl:when>
<xsl:otherwise>
: false
</xsl:otherwise>
</xsl:choose>
</tt>
</xsl:template>
</xsl:stylesheet>
