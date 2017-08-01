package com.credithc.spider;

import java.util.Map;

/**
 * Created by dell3010 on 2017/7/26.
 */
public class Function {

    private String functionName;
    private String resultType;
    private String httpMethod;
    private Map<String, String> headers;
    private Map<String, String> params;
    /**
     * example: password : {123456,abcdef}
     */
    private Map<String, String> factors;
    private boolean requestBodyIsJson;
    private String url;

    private String fileNames;

    public boolean isRequestBodyIsJson() {
        return requestBodyIsJson;
    }

    public void setRequestBodyIsJson(boolean requestBodyIsJson) {
        this.requestBodyIsJson = requestBodyIsJson;
    }

    public String getFileNames() {
        return fileNames;
    }

    public void setFileNames(String fileNames) {
        this.fileNames = fileNames;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, String> getFactors() {
        return factors;
    }

    public void setFactors(Map<String, String> factors) {
        this.factors = factors;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
