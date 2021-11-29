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

<tr>
<td>
<xsl:for-each select="alias">
<tt>-<xsl:value-of select="."/><xsl:text> </xsl:text><xsl:call-template name="format_arg"/></tt><br/>
</xsl:for-each>
</td>

<xsl:if test="value">
<td>
<xsl:for-each select="value">
<xsl:for-each select="alias">
<tt><xsl:value-of select="."/>&#160;</tt>
</xsl:for-each><br/>
</xsl:for-each>
</td>
</xsl:if>

<td colspan="{2 - number(count(value)>0)}">
<xsl:apply-templates select="short_desc"/>
</td></tr>

</xsl:for-each>
</xsl:for-each>

</table>
</body>
</html>

<h1>Phases and phase options</h1>
<ul>
<xsl:for-each select="options/section/phaseopt/phase|options/section/phaseopt/radio_phase">
<li><b><xsl:value-of select="alias|alias"/></b>: <xsl:value-of select="long_desc"/></li>
<ul>
<ul>
<xsl:apply-templates mode="opt" select="boolopt|multiopt|intopt|flopt|stropt|section/boolopt|section/multiopt|section/intopt|section/flopt|section/stropt"/>
</ul>
</ul>
<ul>
<xsl:for-each select="sub_phase">
<li><b><xsl:value-of select="alias|alias"/></b>: <xsl:value-of select="long_desc
"/></li>
<ul>
<xsl:apply-templates mode="opt" select="boolopt|multiopt|intopt|flopt|stropt|section/boolopt|section/multiopt|section/intopt|section/flopt|section/stropt"/>
</ul>
</xsl:for-each>
</ul>
</xsl:for-each>
</ul>

</xsl:template>

<xsl:template mode="opt" match="boolopt|multiopt|intopt|flopt|stropt|section/boolopt|section/multiopt|section/intopt|section/flopt|section/stropt">
<li>
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
</li>
</xsl:template>

<xsl:template match="use_arg_label">
  <xsl:choose>
    <xsl:when test="count(ancestor::*/set_arg_label)!=0"><var><xsl:value-of select="ancestor::*/set_arg_label"/></var></xsl:when>
    <xsl:otherwise><var>arg</var></xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="format_arg">
  <xsl:choose>
    <xsl:when test="../set_arg_label"><var><xsl:value-of select="../set_arg_label"/></var></xsl:when>
    <xsl:when test="parent::listopt | parent::multiopt | parent::stropt | parent::phaseopt"><var>arg</var></xsl:when>
  </xsl:choose>
</xsl:template>

<xsl:template match="var">
  <var><xsl:apply-templates/></var>
</xsl:template>
</xsl:stylesheet>
