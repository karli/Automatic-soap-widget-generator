<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
  xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
  xmlns:xsd="http://www.w3.org/2001/XMLSchema"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0">

<!-- Technique 1-8 copyright of IBM http://www.ibm.com/developerworks/webservices/library/ws-xsltwsdl/ -->

<!-- Technique 1 -->

<!-- emits the namespace uri associated with the prefix of the
     specified qname based on current()'s namespace nodes -->

<xsl:template name="namespace-uri-of-qname">
  <xsl:param name="qname"/>

  <xsl:if test="contains($qname,':')">
    <xsl:value-of select="namespace::*[name()=substring-before($qname,':')]"/>
  </xsl:if>
</xsl:template>



<!-- Technique 2 -->

<!-- emits a prefix that maps to the specified namespace uri for current() -->

<xsl:template name="prefix-for-namespace">
  <xsl:param name="namespace-uri"/>

  <xsl:if test="$namespace-uri">
    <!-- terminate if current() does not have a prefix for $namespace-uri -->
    <xsl:if test="not(namespace::*[string() = $namespace-uri])">
      <xsl:message terminate="yes">
        <xsl:text>Unable to find namespace prefix for namespace </xsl:text>
        <xsl:value-of select="$namespace-uri"/>
        <xsl:text> while processing </xsl:text>
        <xsl:value-of select="name()"/>
        <xsl:text> element.</xsl:text>
      </xsl:message>
    </xsl:if>

    <xsl:value-of select="name(namespace::*[string() = $namespace-uri])"/>
    <xsl:text>:</xsl:text>
  </xsl:if>
  
</xsl:template>


<!-- emits a prefix that maps to the wsdl:definitions/@targetNamespace -->

<xsl:template name="prefix-for-target-namespace">
  <xsl:if test="/wsdl:definitions/@targetNamespace">
    <xsl:call-template name="prefix-for-namespace">
      <xsl:with-param name="namespace-uri">
        <xsl:value-of select="/wsdl:definitions/@targetNamespace"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:if>
</xsl:template>



<!-- Technique 3 -->

<!-- emits the local name of the specified qname -->

<xsl:template name="local-name-of-qname">
  <xsl:param name="qname"/>
  
  <xsl:choose>
    <xsl:when test="contains($qname,':')">
      <xsl:value-of select="substring-after($qname,':')"/>
    </xsl:when>
    
    <xsl:otherwise>
      <xsl:value-of select="$qname"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>



<!-- Technique 4 -->

<!-- index of wsdl:message by namespace uri, wsdl:message/@name -->

<xsl:key name="message"
         match="wsdl:message"
         use="concat(/wsdl:definitions/@targetNamespace,' ',@name)"/>


<!-- message key value that corresponds to a
     wsdl:portType/wsdl:operation/wsdl:* -->

<xsl:template match="wsdl:portType/wsdl:operation/wsdl:input |
                     wsdl:portType/wsdl:operation/wsdl:output |
                     wsdl:portType/wsdl:operation/wsdl:fault"
              mode="message-key">
  <xsl:call-template name="namespace-uri-of-qname">
    <xsl:with-param name="qname" select="@message"/>
  </xsl:call-template>

  <xsl:text> </xsl:text>

  <xsl:call-template name="local-name-of-qname">
    <xsl:with-param name="qname" select="@message"/>
  </xsl:call-template>
</xsl:template>



<!-- Technique 5 -->


<!-- index of wsdl:portType by namespace uri, wsdl:portType/@name -->

<xsl:key name="porttype"
         match="wsdl:portType"
         use="concat(/wsdl:definitions/@targetNamespace,' ',@name)"/>


<!-- porttype key that corresponds to a wsdl:binding -->

<xsl:template match="wsdl:binding"
              mode="porttype-key">
  <xsl:call-template name="namespace-uri-of-qname">
    <xsl:with-param name="qname" select="@type"/>
  </xsl:call-template>
  
  <xsl:text> </xsl:text>

  <xsl:call-template name="local-name-of-qname">
    <xsl:with-param name="qname" select="@type"/>
  </xsl:call-template>
</xsl:template>



<!-- Technique 6 -->

<!-- index of wsdl:binding by namespace uri and wsdl:binding/@name -->

<xsl:key name="binding" match="wsdl:binding"
         use="concat(/wsdl:definitions/@targetNamespace,' ',@name)"/>

         
<!-- binding key value for wsdl:port -->

<xsl:template match="wsdl:port" mode="binding-key">
  <xsl:call-template name="namespace-uri-of-qname">
    <xsl:with-param name="qname" select="@binding"/>
  </xsl:call-template>
  
  <xsl:text> </xsl:text>

  <xsl:call-template name="local-name-of-qname">
    <xsl:with-param name="qname" select="@binding"/>
  </xsl:call-template>
</xsl:template>

<!-- IBM techniques end -->

		
<xsl:key name="type"
         match="wsdl:types/xsd:schema/*"
         use="concat(//wsdl:types/xsd:schema/@targetNamespace,' ',@name)"/>


<!--  build upon previous techniques. 
	  resolve a qname from "prefix:local-name to "namespace-uri local-name" -->
<xsl:template name="resolve-qname">
	<xsl:param name="qname"/>
	<xsl:call-template name="namespace-uri-of-qname">
		<xsl:with-param name="qname" select="$qname"/>
	</xsl:call-template>
	  
	<xsl:text> </xsl:text>
	
	<xsl:call-template name="local-name-of-qname">
	  <xsl:with-param name="qname" select="$qname"/>
	</xsl:call-template>
</xsl:template>

</xsl:stylesheet>