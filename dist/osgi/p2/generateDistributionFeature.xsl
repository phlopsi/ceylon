<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:param name="featureId"/>
	<xsl:param name="featureVersion"/>
    <xsl:param name="includedBundles"/>
    <xsl:param name="ceylonVersion"/>
	<xsl:template match="/">
<feature
      id="{$featureId}"
      label="Ceylon Full Distribution Feature"
      version="{$featureVersion}"
      provider-name="Red Hat">
   <description>
      The Ceylon Full Distribution Feature provides the full Ceylon distribution as OSGI bundles to be used inside Eclipse,
      as well as an embedded Ceylon repository of the full Ceylon distribution.
      
      In order to simply run some Ceylon code inside Eclipse, the Ceylon Runtime feature is sufficient.
   </description>

   <copyright>
JBoss, Home of Professional Open Source
Copyright JBoss by Red Hat, and individual contributors as indicated 
by the @authors tag, 2011.
   </copyright>

   <license  url="">
      Red Hat, Inc. licenses these features and plugins to you under certain open source licenses (or aggregations of such licenses), which in a particular case may include the Eclipse Public License, the GNU Lesser General Public License, and/or certain other open source licenses. For precise licensing details, consult the corresponding source code, or contact Red Hat Legal Affairs, 1801 Varsity Drive, Raleigh NC 27606 USA.
   </license>
      
      
      <xsl:for-each select="/*[local-name()='repository']/*[local-name()='resource']">
         <xsl:variable name="bundleName" select="./*[(local-name()='capability') and (@namespace='osgi.identity')]/*[(local-name()='attribute') and (@name='osgi.identity')]/@value"/>      
   <plugin
         id="{$bundleName}"
         download-size="{./*[(local-name()='capability') and (@namespace='osgi.content')]/*[(local-name()='attribute') and (@name='size')]/@value}"
         install-size="{./*[(local-name()='capability') and (@namespace='osgi.content')]/*[(local-name()='attribute') and (@name='size')]/@value}"
         version="{./*[(local-name()='capability') and (@namespace='osgi.identity')]/*[(local-name()='attribute') and (@name='version')]/@value}"/>
      </xsl:for-each>
</feature>
	</xsl:template>
</xsl:stylesheet>