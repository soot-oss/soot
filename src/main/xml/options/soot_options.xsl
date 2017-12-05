<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
<html>
<head>
	<title>Soot Command Line Options</title>
	<META NAME="description" CONTENT="Soot command-line options"/>
	<META NAME="keywords" CONTENT="usage"/>
	<META HTTP-EQUIV="Content-Style-Type" CONTENT="text/css"/>
</head>

<LINK REL="STYLESHEET" HREF="soot_options.css" />

<body>

<H1 ALIGN="CENTER">Soot command-line options</H1>

<H1>CONTENTS</H1>
<ul>
	<li><a href="#synopsis">SYNOPSIS</a></li>
	<li><a href="#description">DESCRIPTION</a></li>
	<li><a href="#options">OPTIONS</a>
		<ul>
			<xsl:for-each select="options/section">
				<xsl:variable name="sectionId" select="position()"/>
				<li><a href="#section_{$sectionId}"><xsl:value-of select="name"/></a></li>
			</xsl:for-each>
		</ul>
	</li>
	<li><a href="#phaseOptions">PHASES AND PHASE OPTIONS</a>
		<ul>
			<xsl:for-each select="options/section/phaseopt/phase|options/section/phaseopt/radio_phase">
				<li>
					<xsl:variable name="phaseId" select="position()"/>
					<a href="#phase_{$phaseId}"><xsl:value-of select="name"/> (<xsl:value-of select="alias|alias"/>)</a>
					<xsl:if test="sub_phase">
						<ul>
							<xsl:for-each select="sub_phase">
								<xsl:variable name="subphaseId" select="position()"/>
								<li><a href="#phase_{$phaseId}_{$subphaseId}"><xsl:value-of select="name"/> (<xsl:value-of select="alias|alias"/>)</a></li>
							</xsl:for-each>
						</ul>
					</xsl:if>
				</li>
			</xsl:for-each>
		</ul>
	</li>
</ul>

<H1><A NAME="synopsis">
SYNOPSIS</A>
</H1>

<P>
Soot is invoked as follows:
<BLOCKQUOTE>
	<TT>java</TT> <I>javaOptions</I> <TT>soot.Main</TT> [ <I>sootOption</I>* ] <I>classname</I>*
</BLOCKQUOTE>
</P>

<H1><A NAME="description">DESCRIPTION</A></H1> 
<p>This manual documents the command line options of the Soot
bytecode compiler/optimizer tool. In essence, it tells you what you can
use to replace the <I>sootOption</I> placeholder which appears in the SYNOPSIS.</p>

<xsl:copy-of select="options/intro" />

<H1><A NAME="options">OPTIONS</A></H1> 


<xsl:for-each select="options/section">
<xsl:variable name="sectionId" select="position()"/>
<H2><a name="section_{$sectionId}"><xsl:value-of select="name"/></a></H2> 
<table border="3">

<xsl:for-each select="boolopt|listopt|multiopt|stropt|intopt|macroopt|phaseopt">

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
</table>
</xsl:for-each>

<h1><a name="phaseOptions">PHASES AND PHASE OPTIONS</a></h1>
<xsl:copy-of select="options/phaseintro" />
<xsl:for-each select="options/section/phaseopt/phase|options/section/phaseopt/radio_phase">
	<xsl:variable name="phaseId" select="position()"/>
	<h2><a name="phase_{$phaseId}"><xsl:value-of select="name"/> (<xsl:value-of select="alias|alias"/>)</a></h2>
	<p><xsl:value-of select="long_desc"/></p>
	<xsl:if test="boolopt|multiopt|intopt|flopt|stropt|section/boolopt|section/multiopt|section/intopt|section/flopt|section/stropt">
		<h4>Accepted phase options:</h4>
		<ul>
			<xsl:apply-templates mode="opt" select="boolopt|multiopt|intopt|flopt|stropt|section/boolopt|section/multiopt|section/intopt|section/flopt|section/stropt"/>
		</ul>
	</xsl:if> 

	<xsl:for-each select="sub_phase">
		<xsl:variable name="subphaseId" select="position()"/>
		<h2><a name="phase_{$phaseId}_{$subphaseId}"><xsl:value-of select="name"/> (<xsl:value-of select="alias|alias"/>)</a></h2>
		<p><xsl:value-of select="long_desc"/></p>
		<xsl:if test="boolopt|multiopt|intopt|flopt|stropt|section/boolopt|section/multiopt|section/intopt|section/flopt|section/stropt">
			<h4>Accepted phase options:</h4>
			<ul>
				<xsl:apply-templates mode="opt" select="boolopt|multiopt|intopt|flopt|stropt|section/boolopt|section/multiopt|section/intopt|section/flopt|section/stropt"/>
			</ul>
		</xsl:if> 
	</xsl:for-each>
</xsl:for-each>

</body>
</html>
</xsl:template>

<xsl:template mode="opt" match="boolopt|multiopt|intopt|flopt|stropt|section/boolopt|section/multiopt|section/intopt|section/flopt|section/stropt">
	<li>
		<b><xsl:value-of select="name"/></b> (<xsl:value-of select="alias"/>)
		<xsl:choose>
		<xsl:when test="default">
			<br/>
			(default value: <span class="value"><xsl:value-of select="default"/></span>)
		</xsl:when>
		<xsl:when test="value/default">
			<br/>
			(default value: <span class="value">
				<xsl:for-each select="value">
					<xsl:if test="default">
						<xsl:value-of select="alias"/>
					</xsl:if>
				</xsl:for-each></span>)
		</xsl:when>
		<xsl:otherwise>
		</xsl:otherwise>
		</xsl:choose>
		<p>
		<xsl:value-of select="long_desc"/>
		</p>
		<xsl:if test="value">
			<table border="0">
				<th colspan="2">
					Possible values:
				</th>
				<xsl:for-each select="value">
					<tr>
						<td class="value">
							<xsl:value-of select="alias"/>
						</td>
						<td>
							<xsl:value-of select="long_desc"/>
						</td>
					</tr>
				</xsl:for-each>			
			</table>
		</xsl:if>
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
    <xsl:when test="parent::listopt | parent::multiopt | parent::stropt | parent::intopt | parent::phaseopt"><var>arg</var></xsl:when>
  </xsl:choose>
</xsl:template>

<xsl:template match="var">
  <var><xsl:apply-templates/></var>
</xsl:template>
</xsl:stylesheet>
