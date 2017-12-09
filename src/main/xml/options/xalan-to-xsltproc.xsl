<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:redirect="http://xml.apache.org/xalan/redirect"
    extension-element-prefixes="redirect"
>
  <xsl:output method="xml"/>

  <xsl:template match="redirect:write">
    <xsl:element name="xsl:document">
      <xsl:attribute name="href">{$fullpath}</xsl:attribute>
      <xsl:attribute name="method">text</xsl:attribute>
      <xsl:attribute name="indent">no</xsl:attribute>
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>

  <!-- identity template -->
 <xsl:template match="@*|node()">
  <xsl:copy>
   <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
 </xsl:template>

</xsl:stylesheet>
