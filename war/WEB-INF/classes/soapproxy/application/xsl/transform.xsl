<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.1"
	xmlns:ws="http://schemas.xmlsoap.org/wsdl/"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema"
	xmlns:sawsdl="http://www.w3.org/ns/sawsdl"
  xmlns:xalan="http://xml.apache.org/xalan">


	
	<!-- import ibm xsl utils for wsdl -->
	<xsl:import href="utils/wsdl-util.xsl"/>
	
	<xsl:output method="xml"/>
	
	<xsl:param name="operationName">sayHello</xsl:param>

	<!-- get part/@type as namespace-uri + ' ' + local-name -->
	<xsl:template match="ws:part"  mode="type-key">
	  <xsl:call-template name="resolve-qname">
	  	<xsl:with-param name="qname" select="@type"/>
	  </xsl:call-template>
	</xsl:template>
	
	<xsl:template match="/">
		<xsl:apply-templates select="//ws:portType/ws:operation[@name = $operationName]"/>
	</xsl:template>
	
	
	<!-- Rendering type information -->
		
	<!-- Avoid generating unneeded output for not relevant elements -->
	<xsl:template match="*" mode="render-type"/>
	
	<!-- detect arrays in WSDL type definitions, add special output to these cases -->
	<xsl:template match="xsd:restriction" mode="render-type">
		<xsl:param name="path-so-far"/>
	
		<xsl:variable name="local-name">
			<xsl:call-template name="local-name-of-qname">
				<xsl:with-param name="qname" select="@base"/>
			</xsl:call-template>
		</xsl:variable>
		
		<xsl:variable name="namespace-uri">
			<xsl:call-template name="namespace-uri-of-qname">
		    	<xsl:with-param name="qname" select="@base"/>
		  	</xsl:call-template>
		</xsl:variable>
		
		<xsl:if test="$local-name = 'Array' and $namespace-uri = 'http://schemas.xmlsoap.org/soap/encoding/'">
		
<xsl:text>
   </xsl:text><repeating_element_group path="{$path-so-far}">
<xsl:apply-templates select="*" mode="render-type">
	<xsl:with-param name="path-so-far" select="$path-so-far"/>
</xsl:apply-templates><xsl:text>
   </xsl:text></repeating_element_group>

		</xsl:if>
		
	</xsl:template>
	
	<xsl:template match="xsd:all|xsd:any|xsd:choice|xsd:sequence|xsd:complexContent" mode="render-type">
        <xsl:param name="path-so-far"/>
		<xsl:apply-templates select="*" mode="render-type">
			<xsl:with-param name="path-so-far" select="$path-so-far" />
		</xsl:apply-templates> 
	</xsl:template>
	
	<!-- concatenate element name if it exists -->
	<xsl:template match="*" mode="concat-name">
		<xsl:param name="path-so-far"/>
		<xsl:choose>
			<xsl:when test="string-length(@name) = 0">
				<xsl:value-of select="$path-so-far"></xsl:value-of>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="concat($path-so-far,'/',@name)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
    <xsl:template match="xsd:complexType" mode="render-type">
        <xsl:param name="path-so-far"/>
    	<xsl:apply-templates select="*" mode="render-type">
    		<xsl:with-param name="path-so-far">
    			<xsl:apply-templates select="." mode="concat-name">
    				<xsl:with-param name="path-so-far" select="$path-so-far"/>
    			</xsl:apply-templates>
    		</xsl:with-param>
    	</xsl:apply-templates> 
    </xsl:template> 
    
    <xsl:template match="xsd:element" mode="render-type">
        <xsl:param name="path-so-far"/>
        
        <!--  if type is xsd base type -->
        <xsl:variable name="namespace-uri">        	
        	<xsl:call-template name="namespace-uri-of-qname">
		    	<xsl:with-param name="qname" select="@type"/>
		  	</xsl:call-template>
        </xsl:variable>
        
        
        <xsl:choose>
        	<xsl:when test="$namespace-uri = 'http://www.w3.org/2001/XMLSchema'">
<xsl:text>
   </xsl:text><mapping><xsl:text>
      </xsl:text><global_ref><xsl:value-of select="@sawsdl:modelReference"/></global_ref><xsl:text>
      </xsl:text><path><xsl:value-of select="concat($path-so-far,'/', @name)"/></path><xsl:text> 
   </xsl:text></mapping>       	
        	</xsl:when>
        	<xsl:otherwise>
        		<xsl:apply-templates select="*" mode="render-type">
					<xsl:with-param name="path-so-far" select="concat($path-so-far,'/',@name)" />
				</xsl:apply-templates> 
        	</xsl:otherwise>
        </xsl:choose>

    </xsl:template> 
    
    
	
	<xsl:template match="ws:operation">
		<!-- get the correct value for the message name key -->
	    <xsl:variable name="input-message-key">
	      <xsl:apply-templates select="ws:input" mode="message-key"/>
	    </xsl:variable>
	    
	    <xsl:variable name="output-message-key">
	      <xsl:apply-templates select="ws:output" mode="message-key"/>
	    </xsl:variable>
	
		<xsl:variable name="input-part-type-key">
	      <xsl:apply-templates select="key('message',$input-message-key)/ws:part" mode="type-key"/>
	    </xsl:variable>
	    
	    <xsl:variable name="input-type" select="key('type',$input-part-type-key)"/>
	    
	    <xsl:variable name="output-part-type-key">
	      <xsl:apply-templates select="key('message',$output-message-key)/ws:part[@name='keha']" mode="type-key"/>
	    </xsl:variable>
	    
	    <xsl:variable name="output-type" select="key('type',$output-part-type-key)"/>
<xsl:text>
</xsl:text><mappings><xsl:apply-templates select="$input-type" mode="render-type"/><xsl:text>
</xsl:text></mappings>   
<xsl:text>
</xsl:text><mappings><xsl:apply-templates select="$output-type" mode="render-type"/><xsl:text>
</xsl:text></mappings>
	
	</xsl:template>	
	
	<xsl:template name="definitions">
		<xsl:for-each select="namespace::*">
			<xsl:value-of select="string()"/>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="service">
		<xsl:value-of select="namespace-uri(ws:port)"/>
	</xsl:template>

</xsl:stylesheet>