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
<h2><xsl:value-of select="section_name"/></h2>
</td></tr>

<xsl:for-each select="boolean_option|path_option|multi_option|string_option|macro_option|phase_option">

<xsl:if test="value">
<tr>
<td>
<xsl:for-each select="alias_name">
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
<xsl:for-each select="alias_name">
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
<xsl:for-each select="options/section/phase_option/phase">
<li/><xsl:value-of select="phase_alias|sub_phase_alias"/>
<ul>
<ul>
<xsl:apply-templates mode="opt" select="boolean_option|multi_option|int_option|float_option|string_option"/>
</ul>
</ul>
<ul>
<xsl:for-each select="sub_phase">
<li/><xsl:value-of select="phase_alias|sub_phase_alias"/>
<ul>
<xsl:apply-templates mode="opt" select="boolean_option|multi_option|int_option|float_option|string_option"/>
</ul>
</xsl:for-each>
</ul>
</xsl:for-each>
</ul>

</xsl:template>

<xsl:template mode="opt" match="boolean_option|multi_option|int_option|float_option|string_option">
<li/>
<tt><xsl:value-of select="alias_name"/>
<xsl:choose>
<xsl:when test="default_value">
: <xsl:value-of select="default_value"/>
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
