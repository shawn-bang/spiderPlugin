package com.credithc.spider;

import java.util.Map;

/**
 * Created by dell3010 on 2017/7/26.
 */
public class Connection {

    /**
     * 文件名
     */
    private String fileName;
    /**
     * 请求url
     */
    private String requestUrl;
    /**
     * 请求url不带参数前缀
     */
    private String requestUrlPrefix;
    /**
     * 请求url参数key-value形式map
     */
    private Map<String, String> urlParamsMap;
    /**
     * 例：username&password
     * 请求url参数name排序拼接字符串
     */
    private String urlParamsName;
    /**
     * 请求方法名
     */
    private String requestMethod;
    /**
     * 请求headers
     */
    private Map<String, String> requestHeaders;
    /**
     * 请求体内容
     */
    private String requestBody;

    /**
     * 请求体是否为json格式
     */
    private boolean requestBodyIsJson;

    /**
     * 请求体参数key-value形式map,参数名统一加入统一后缀(@)
     */
    private Map<String, String> requestBodyMap;
    /**
     * 例：username&password
     * 请求体参数name排序拼接字符串
     */
    private String requestBodyName;
    /**
     * 例：username&password&account@&
     * 请求体参数name排序拼接字符串,其中body体里面的参数名统一加入统一后缀(@)
     */
    private String fullNames;
    /**
     * 响应headers
     */
    private Map<String, String> responseHeaders;
    /**
     * 响应体内容
     */
    private String responseBody;
    /**
     * 响应码
     */
    private String status;

    public boolean isRequestBodyIsJson() {
        return requestBodyIsJson;
    }

    public void setRequestBodyIsJson(boolean requestBodyIsJson) {
        this.requestBodyIsJson = requestBodyIsJson;
    }

    public String getFullNames() {
        return fullNames;
    }

    public void setFullNames(String fullNames) {
        this.fullNames = fullNames;
    }

    public String getRequestUrlPrefix() {
        return requestUrlPrefix;
    }

    public void setRequestUrlPrefix(String requestUrlPrefix) {
        this.requestUrlPrefix = requestUrlPrefix;
    }

    public String getUrlParamsName() {
        return urlParamsName;
    }

    public void setUrlParamsName(String urlParamsName) {
        this.urlParamsName = urlParamsName;
    }

    public String getRequestBodyName() {
        return requestBodyName;
    }

    public void setRequestBodyName(String requestBodyName) {
        this.requestBodyName = requestBodyName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Map<String, String> getUrlParamsMap() {
        return urlParamsMap;
    }

    public void setUrlParamsMap(Map<String, String> urlParamsMap) {
        this.urlParamsMap = urlParamsMap;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public Map<String, String> getRequestBodyMap() {
        return requestBodyMap;
    }

    public void setRequestBodyMap(Map<String, String> requestBodyMap) {
        this.requestBodyMap = requestBodyMap;
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
