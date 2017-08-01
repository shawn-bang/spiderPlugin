package ${ package };

import org.springframework.web.client.RestTemplate;
import com.credithc.bdp.service.spider.util.CookieStoreRestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpMethod;


@Component
public class ${ className } {

<#list functions as function>
    <#if function.factors??>
    /**
     * <#list function.factors?keys as key><#assign fieldName=key><#assign realFieldName=fieldName?replace("@", "")>(${ realFieldName } : ${ function.factors[key] })</#list>
     * ${ function.fileNames }
     */
    </#if>
    public ResponseEntity<${ function.resultType }> ${ function.functionName }(
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
        ResponseEntity<${ function.resultType }> response = restTemplate.exchange("${ function.url }", ${ function.httpMethod }, new HttpEntity<>(map, headers), ${ function.resultType }.class);

        return response;
    }

</#list>

}