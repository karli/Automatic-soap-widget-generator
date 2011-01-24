<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
	xmlns:xsd="http://www.w3.org/2001/XMLSchema"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	
	<xsl:import href="wsdl-util.xsl" />

	<xsl:variable name="xsd-uri" select="'http://www.w3.org/2001/XMLSchema'"/>
	
	<xsl:output method="text" />
	
	<!-- string type -->
	<xsl:template name="base-type-2-json">
		<xsl:param name="type"/>
		
		<xsl:variable name="resolved-qname">
			<xsl:call-template name="resolve-qname">
				<xsl:with-param name="qname" select="$type"/>
			</xsl:call-template>
		</xsl:variable>
		
		<xsl:choose>
			<xsl:when test="$resolved-qname = concat($xsd-uri,' ', 'string')">
				<xsl:text>"type":"string"</xsl:text>
			</xsl:when>
			<xsl:when test="$resolved-qname = concat($xsd-uri,' ', 'boolean')">
				<xsl:text>"type":"boolean"</xsl:text>
			</xsl:when>
			<xsl:when test="$resolved-qname = concat($xsd-uri,' ', 'decimal')">
				<xsl:text>"type":"number"</xsl:text>
			</xsl:when>	
			<xsl:when test="$resolved-qname = concat($xsd-uri,' ', 'float')">
				<xsl:text>"type":"number"</xsl:text>
			</xsl:when>
			<xsl:when test="$resolved-qname = concat($xsd-uri,' ', 'double')">
				<xsl:text>"type":"number"</xsl:text>
			</xsl:when>
			<xsl:when test="$resolved-qname = concat($xsd-uri,' ', 'integer')">
				<xsl:text>"type":"integer"</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>"type":"any"</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- check if the type of an element is of base xml schema type -->
	<xsl:template match="*[@type]" mode="is-base-type">
		<xsl:variable name="namespace-uri">        	
        	<xsl:call-template name="namespace-uri-of-qname">
		    	<xsl:with-param name="qname" select="@type"/>
		  	</xsl:call-template>
        </xsl:variable>
        
        <xsl:value-of select="$namespace-uri = 'http://www.w3.org/2001/XMLSchema'"/>
	</xsl:template>

</xsl:stylesheet>
