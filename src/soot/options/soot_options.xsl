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
<xsl:for-each select="options/section">
<hr />
<h2><xsl:value-of select="section_name"/></h2>
<xsl:for-each select="boolean_option">
<table border="1"><tr><td>
<xsl:value-of select="name"/>
</td><td>
<xsl:for-each select="alias_name">
<xsl:value-of select="."/>&#160;
</xsl:for-each>
</td><td>
<xsl:value-of select="short_desc"/>
</td><td>
<xsl:value-of select="long_desc"/>
</td></tr></table>
</xsl:for-each>
<xsl:for-each select="path_option">
<table border="1"><tr><td>
<xsl:value-of select="name"/>
</td><td>
<xsl:for-each select="alias_name">
<xsl:value-of select="."/>&#160;
</xsl:for-each>
</td><td>
<xsl:value-of select="short_desc"/>
</td><td>
<xsl:value-of select="long_desc"/>
</td></tr></table>
</xsl:for-each>
<xsl:for-each select="string_option">
<table border="1"><tr><td>
<xsl:value-of select="name"/>
</td><td>
<xsl:for-each select="alias_name">
<xsl:value-of select="."/>&#160;
</xsl:for-each>
</td><td>
<xsl:value-of select="short_desc"/>
</td><td>
<xsl:value-of select="long_desc"/>
</td></tr></table>
</xsl:for-each>
<xsl:for-each select="macro_option">
<table border="1"><tr><td>
<xsl:value-of select="name"/>
</td><td>
<xsl:for-each select="alias_name">
<xsl:value-of select="."/>&#160;
</xsl:for-each>
</td><td>
<xsl:value-of select="expansion"/>
</td><td>
<xsl:value-of select="short_desc"/>
</td><td>
<xsl:value-of select="long_desc"/>
</td></tr></table>
</xsl:for-each>
<xsl:for-each select="multi_option">
<table border="1"><tr><td>
<xsl:value-of select="name"/>
</td><td>
<xsl:for-each select="alias_name">
<xsl:value-of select="."/>&#160;
</xsl:for-each>
</td><td>
<xsl:for-each select="values/value">
<xsl:value-of select="value_name"/><br />
<xsl:for-each select="alias">
<xsl:value-of select="."/><br />
</xsl:for-each>
<br />
</xsl:for-each>
</td><td>
<xsl:value-of select="short_desc"/>
</td><td>
<xsl:value-of select="long_desc"/>
</td></tr></table>
</xsl:for-each>
</xsl:for-each>

<!--<table>
<xsl:for-each select="options/section">
<tr><td>
<xsl:for-each select="ids">
<b><xsl:value-of select="id"/>&#160;</b>
</xsl:for-each>
<xsl:value-of select="arg"/>&#160;
<xsl:for-each select="args">
<xsl:value-of select="arg"/>&#160;
</xsl:for-each>
<xsl:for-each select="arg_vals">[
<xsl:value-of select="arg_val"/>
]</xsl:for-each>&#160;
</td><td>
<xsl:value-of select="desc"/><br />
</td></tr>
</xsl:for-each>
</table>-->
</body>
</html>
</xsl:template>

</xsl:stylesheet>
