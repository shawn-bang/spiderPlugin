package ${ package };

import org.springframework.web.client.RestTemplate;
import com.credithc.bdp.service.spider.util.CookieStoreRestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpMethod;


@Component
public class ${ className } {

<#list functions as function>
    @MethodInfo(fileName=${ function.fileNames }<#if (function.factors?? && function.factors?size > 0)>,params={<#list function.factors?keys as key><#assign fieldName=key><#assign realFieldName=fieldName?replace("@", "")>"${ realFieldName }=${ function.factors[key] }"<#if key_has_next >,</#if></#list>}</#if>)
    public ResponseEntity<${ function.resultType }> ${ function.functionName }(
        CookieStoreRestTemplate restTemplate<#if (function.factors?? && function.factors?size > 0)>,</#if>
<#if function.factors??>
    <#list function.factors?keys as key>
        <#assign fieldName=key>
        <#assign realFieldName=fieldName?replace("@", "")>
        String ${ realFieldName }<#if key_has_next >,</#if>
    </#list>
</#if>
    ) {
<#if function.headers??>
    <#list function.headers?keys as key>
        <#if key_index == 0>
        HttpHeaders headers = new HttpHeaders();
        </#if>
        headers.set("${ key }", "${ function.headers[key] }");
    </#list>
</#if>
<#if function.httpMethod != "HTTP.GET">
<#if function.requestBodyIsJson>
<#if function.params??>
    <#if function.params["requestBodyJson@"]??>
        String map = "${ function.params["requestBodyJson@"] }";
    </#if>
</#if>
<#if function.factors??>
    <#if function.factors["requestBodyJson@"]??>
        String map = requestBodyJson;
    </#if>
</#if>
</#if>
<#if !function.requestBodyIsJson>
<#if function.params?? || function.factors??>
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
</#if>
<#if function.params??>
    <#list function.params?keys as key>
        <#assign fieldName=key>
        <#assign realFieldName=fieldName?replace("@", "")>
        <#if fieldName?ends_with("@")>
        map.add("${ realFieldName }", "${ function.params[key] }");
        </#if>
    </#list>
</#if>
<#if function.factors??>
    <#list function.factors?keys as key>
        <#assign fieldName=key>
        <#assign realFieldName=fieldName?replace("@", "")>
        <#if fieldName?ends_with("@")>
        map.add("${ realFieldName }", ${ realFieldName });
        </#if>
    </#list>
</#if>
</#if>
</#if>
        ResponseEntity<${ function.resultType }> response = restTemplate.exchange("${ function.url }", ${ function.httpMethod }, new HttpEntity<>(<#if function.params?? || function.factors??>map<#else>null</#if>, headers), ${ function.resultType }.class);

        return response;
    }

</#list>

}