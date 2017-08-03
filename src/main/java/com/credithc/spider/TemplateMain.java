package com.credithc.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dell3010 on 2017/7/25.
 */
public class TemplateMain {

    /**
     * 读取文件数据
     * @param path
     * @return
     */
    public static Map<String, String> getConnectionInfo(String path){
        File directory = new File(path);
        File[] files = directory.listFiles();
        System.out.println("该目录下对象个数："+files.length);

        if (files.length == 0) return null;

        Map<String, String> connections = new HashMap<String, String>();
        for (int i = 0; i < files.length; i++) {
            try {
                if (files[i].isDirectory()) continue;
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(files[i])));
                StringBuffer content = new StringBuffer("");
                while (reader.ready()) {
                    content.append(reader.readLine() + "\n");
                }

                connections.put(files[i].getName(), content.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return connections;
    }

    /**
     * 解析文本数据生成connection对象
     * @param infos
     * @return
     */
    public static Map<String, Object> getConnections(Map<String, String> infos) throws Exception {
        if (infos == null || infos.size() == 0) return null;
        Map<String, Object> connections = new HashMap<String, Object>();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String fileName = entry.getKey();
            String content = entry.getValue();
            JSONObject object = JSON.parseObject(content);

            Connection connection = new Connection();
            connection.setFileName(fileName);
            connection.setRequestUrl(URLDecoder.decode(object.getString("request_url"), "UTF-8"));
            connection.setRequestMethod(object.getString("request_method"));
            connection.setStatus(object.getString("response_status"));
            connection.setRequestHeaders(getHeaders(object.getString("request_headers")));
            // 统一base64解密一次
            if (StringUtils.isNotBlank(object.getString("request_body"))){
                String requestBody = new String(org.apache.commons.codec.binary.Base64.decodeBase64(object.getString("request_body")));
                connection.setRequestBody(URLDecoder.decode(requestBody, "UTF-8"));
            }else {
                connection.setRequestBody("");
            }
            connection.setResponseHeaders(getHeaders(object.getString("response_headers")));
            String contentType = connection.getResponseHeaders().get("Content-Type");
            /**
             * 根据规则过滤请求,目前css文件请求不在分析范围,CONNECT方法请求不在分析范围
             */
            if (filterConnectionByContentType(contentType) || filterConnectionByRequestMethod(connection.getRequestMethod())){
                continue;
            }

            connection.setResponseBody(object.getString("response_body"));

            String url = connection.getRequestUrl();
            String[] urlParts = url.split("\\?");
            connection.setRequestUrlPrefix(urlParts[0]);

            if (urlParts.length > 1){
                Map<String, String> paramsMap = getParamsMap(urlParts[1], "");
                connection.setUrlParamsMap(paramsMap);
                Set<String> paramsNameSet = paramsMap.keySet();
                List<String> paramsNameList = new ArrayList<String>(paramsNameSet);
                Collections.sort(paramsNameList);
                connection.setUrlParamsName(spliceCollectionBySeparator(paramsNameList, "&"));
            }
            if (StringUtils.isNotBlank(connection.getRequestBody())){
                if (isJson(connection.getRequestBody())){
                    connection.setRequestBodyIsJson(true);
                    Map<String, String> paramsMap = new HashMap<String, String>();
                    paramsMap.put("requestBodyJson@", connection.getRequestBody());
                    connection.setRequestBodyMap(paramsMap);
                    connection.setRequestBodyName("requestBodyJson@");
                    String urlParamsName = (connection.getUrlParamsName() == null)?"":connection.getUrlParamsName();
                    connection.setFullNames(StringUtils.strip(urlParamsName + "&" + connection.getRequestBodyName(), "&"));
                }else {
                    connection.setRequestBodyIsJson(false);
                    Map<String, String> paramsMap = getParamsMap(connection.getRequestBody(), "@");
                    connection.setRequestBodyMap(paramsMap);
                    Set<String> paramsNameSet = paramsMap.keySet();
                    List<String> paramsNameList = new ArrayList<String>(paramsNameSet);
                    Collections.sort(paramsNameList);
                    connection.setRequestBodyName(spliceCollectionBySeparator(paramsNameList, "&"));
                    String urlParamsName = (connection.getUrlParamsName() == null)?"":connection.getUrlParamsName();
                    connection.setFullNames(StringUtils.strip(urlParamsName + "&" + connection.getRequestBodyName(), "&"));
                }
            }

            Object something = connections.get(urlParts[0]);
            if (something == null) {
                connections.put(urlParts[0], connection);
            }else {
                if (something instanceof Connection) {
                    List<Connection> conns = new ArrayList<Connection>();
                    conns.add((Connection) something);
                    conns.add(connection);
                    connections.put(urlParts[0], conns);
                }else {
                    ((List<Connection>) something).add(connection);
                }
            }
        }
        return connections;
    }

    /**
     * 解析header数据
     * @param content
     * @return
     */
    public static Map<String, String> getHeaders(String content){
        if (StringUtils.isBlank(content)) return null;
        Map<String, String> map = new HashMap<String, String>();
        BufferedReader reader = new BufferedReader(new StringReader(content));
        try {
            int count = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                count++;
                if (count == 1) continue;
                String[] parts = line.split(":");
                Pattern pattern= Pattern.compile("Cookie|Connection|Content-Length|jpg|png|bmp");
                Matcher matcher = pattern.matcher(parts[0]);
                if (matcher.find()){
                    continue;
                }
                map.put(parts[0], parts[1].trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 处理connection信息, 生成functions
     * @param connectionMap
     * @return
     */
    public static List<Function> getFunctions(Map<String, Object> connectionMap) throws Exception {
        if (connectionMap == null || connectionMap.size() == 0) return null;

        List<Function> functions = new ArrayList<Function>();
        for (Map.Entry<String, Object> entry : connectionMap.entrySet()) {
            String url = entry.getKey();
            Object value = entry.getValue();

            // 目前function还不支持requestBody为json的数据请求形式
            if (value instanceof Connection){
                // 直接转换成function
                Connection connection = (Connection) value;
                Function function = new Function();
                function.setFileNames("{" + "\"" + connection.getFileName() + "\"" + "}");
                function.setFunctionName("do" + connection.getRequestMethod() + getFunctionName(connection.getFileName()));
                function.setHeaders(connection.getRequestHeaders());
                function.setHttpMethod("HttpMethod" + "." + connection.getRequestMethod());
                function.setUrl(connection.getRequestUrl());
                function.setResultType(getReturnType(connection.getResponseHeaders().get("Content-Type")));
                if (connection.isRequestBodyIsJson()){
                    function.setRequestBodyIsJson(true);
                }else {
                    function.setRequestBodyIsJson(false);
                    function.setParams(getParamsMap(connection.getRequestBody(), "@"));
                }

                functions.add(function);
            }else {
                // 需要找到变量参数, 然后转换成function
                List<Connection> connections = (List<Connection>) value;
                // 判断所以connection方法是否统一
                if (!judgeAllMethodIsOne(connections)){
                    throw new Exception("目前暂不支持同一个url请求方式不同的数据");
                }
                // get只需要识别URL后可变参数, post则还需要识别body内可变参数
                Connection connection = connections.get(0);
                if (connection.getRequestMethod().equals("GET")){
                    Map<String, List<Connection>> urlGroupMap = groupConnectionByNameString(connections, "url");
                    // 识别可变参数
                    produceFunction(functions, urlGroupMap, connections, connection, "url");
                }else {
                    Map<String, List<Connection>> groupMap = groupConnectionByNameString(connections, "body");
                    /**
                     * 识别可变参数,暂时不处理url后参数和body参数有重名的情况
                      */
                    produceFunction(functions, groupMap, connections, connection, "body");
                }
            }
        }
        return functions;
    }

    /**
     * 比较connections的不同,得到不可变参数params和可变参数factor信息
     * @param connections
     * @return
     */
    public static Map<String, Map<String, String>> getParamsAndFactors(List<Connection> connections, String target) throws Exception {
        // 如果只有一个connection, 则生成单独一个方法, 没办法识别可变参数
        if (connections == null || connections.size() < 2) return null;
        Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
        Map<String, String> same = new HashMap<String, String>();
        Map<String, String> diff = new HashMap<String, String>();

        Connection conn = connections.get(0);
        Map<String, String> targetMap = conn.getUrlParamsMap();
        if (target.equals("body")){
            Map<String, String> temp = conn.getRequestBodyMap();
            if (conn.getUrlParamsMap() != null)
            {
                temp.putAll(conn.getUrlParamsMap());
            }
            targetMap = temp;
        }
        if (targetMap == null) return null;
        for (Map.Entry<String, String> entry : targetMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Set<String> pot = new HashSet<String>();
            pot.add(value);
            for (int i = 1; i < connections.size(); i++) {
                Connection obj = connections.get(i);
                Map<String, String> map = obj.getUrlParamsMap();
                if (target.equals("body")){
                    Map<String, String> temp = obj.getRequestBodyMap();
                    if (obj.getUrlParamsMap() != null)
                    {
                        temp.putAll(obj.getUrlParamsMap());
                    }
                    map = temp;
                }
                pot.add(map.get(key));
            }

            if (pot.size() == 1){
                same.put(key, value);
            }else {
                diff.put(key, spliceCollectionBySeparator(pot, "&"));
            }
        }

        result.put("params", same);
        result.put("factors", diff);
        return result;
    }

    public static void main(String args[]) throws Exception {
        // 读取配置文件
        Properties properties = new Properties();
        properties.load(TemplateMain.class.getClassLoader().getResourceAsStream("config.properties"));
        String connectionsFilesPath = (String) properties.get("connectionsFilesPath");
        String templateFileName = (String) properties.get("templateFileName");
        String generateCodeTargetPath = (String) properties.get("generateCodeTargetPath");
        String importPackage = (String) properties.get("importPackage");
        String className = (String) properties.get("className");

        // 读入文件信息，生成connection对象
        Map<String, String> infos = getConnectionInfo(connectionsFilesPath);
        Map<String, Object> connections = getConnections(infos);
        // 根据既定规则分析connection数据, 生成function
        List<Function> functions = getFunctions(connections);


        // 根据处理得到的function信息和模板生成代码
        Configuration configuration = new Configuration();
        configuration.setClassForTemplateLoading(TemplateMain.class, "/template");
        Template template = configuration.getTemplate(templateFileName);
        File targetFile = new File(generateCodeTargetPath);
        targetFile.deleteOnExit();
        targetFile.mkdirs();
        FileOutputStream outputStream = new FileOutputStream(new File(targetFile.getAbsolutePath() + "/" + className + ".java"));

        Map<String, Object> data = new HashMap();
        data.put("functions", functions);
        data.put("package", importPackage);
        data.put("className", className);

        template.process(data, new OutputStreamWriter(outputStream, "utf-8"));
        outputStream.flush();
        outputStream.close();

    }

    /**
     * 由于数据本身可靠性原因, 只需简单判断格式便可得知结果
     * @param content
     * @return
     */
    public static boolean isJson(String content) {
        return content.matches("\\{[\\s\\S]*\\}");
    }

    /**
     * 把拼接形式的参数字符串拆分为map形式
     * @param content
     * @return
     */
    public static Map<String, String> getParamsMap(String content, String suffix){
        if (StringUtils.isBlank(content)) return null;
        Map<String, String> paramsMap = new HashMap<String, String>();
        String[] paramsInfo = content.split("&");
        for (String paramInfo: paramsInfo) {
            String[] parts = paramInfo.split("=");
            if (parts.length == 1){
                paramsMap.put(parts[0] + suffix, "");
            }else {
                paramsMap.put(parts[0] + suffix, parts[1]);
            }
        }
        return paramsMap;
    }

    /**
     * 取出两个map相同的数据and不同数据
     * @param target1
     * @param target2
     * @return
     * @throws Exception
     */
    public static Map<String, Map<String, String>> compareMap(Map<String, String> target1, Map<String, String> target2) throws Exception {
        Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
        Map<String, String> same = new HashMap<String, String>();
        Map<String, String> diff = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : target1.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            String clue = target2.get(key);
            if (clue == null){
                throw new Exception("目前暂不支持参数不一一对应的数据");
            }
            if (value.equals(clue))
            {
                same.put(key, value);
            }else {
                diff.put(key, value + "&" + clue);
            }
        }

        result.put("params", same);
        result.put("factors", diff);
        return result;
    }

    /**
     * 识别返回类型
     * @param contentType
     * @return
     */
    public static String getReturnType(String contentType){
        if (StringUtils.isBlank(contentType)) return "String";
        Pattern pattern = Pattern.compile("gif|icon|jpeg|jpg|png|bmp");
        Matcher matcher = pattern.matcher(contentType);
        if (matcher.find()){
            return "byte[]";
        }else {
            return "String";
        }
    }

    /**
     * 根据Content-Type过滤请求
     * @param contentType
     * @return
     */
    public static boolean filterConnectionByContentType(String contentType){
        if (StringUtils.isBlank(contentType)) return false;
        Pattern pattern = Pattern.compile("text/css");
        Matcher matcher = pattern.matcher(contentType);
        if (matcher.find()){
            return true;
        }
        return false;
    }

    /**
     * 根据请求方法过滤请求
     * @param requestMethod
     * @return
     */
    public static boolean filterConnectionByRequestMethod(String requestMethod){
        if (StringUtils.isBlank(requestMethod)) return true;
        Pattern pattern = Pattern.compile("CONNECT");
        Matcher matcher = pattern.matcher(requestMethod);
        if (matcher.find()){
            return true;
        }
        return false;
    }

    /**
     * 根据fileName生成functionName
     * @param fileName
     * @return
     * @throws Exception
     */
    public static String getFunctionName(String fileName) throws Exception {
        Pattern pattern= Pattern.compile("[\\d]+_([\\s\\S]+)\\.json");
        Matcher matcher = pattern.matcher(fileName);
        String key = "";
        while (matcher.find()){
            key = matcher.group(1);
        }
        if (StringUtils.isBlank(key)){
            System.out.println(fileName);
            throw new Exception("文件名格式异常");
        }
        return key.replaceAll("[^0-9a-zA-Z]", "");
    }

    /**
     * 反转义字符串
     * @param src
     * @return
     */
    public static String unescape(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    /**
     * 根据参数map拼接出完整URL
     * @param url
     * @param compareRes
     * @return
     */
    public static String getSyntaxUrl(String url, Map<String, Map<String, String>> compareRes){
        if (compareRes == null) return url;
        Map<String, String> params = compareRes.get("params");
        Map<String, String> factors = compareRes.get("factors");
        if (params.size() == 0 && factors.size() == 0) return url;
        url = url + "?";
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            if (!StringUtils.endsWith(name, "@")){
                url = url + name + "=" + value + "&";
            }
        }
        for (Map.Entry<String, String> entry : factors.entrySet()) {
            String name = entry.getKey();
            if (!StringUtils.endsWith(name, "@")){
                url = url + name + "=" + "\" + " + "URLEncoder.encode(" + name + ",\"UTF-8\")" + " + \"" + "&";
            }
        }
        url = StringUtils.strip(url, "?");
        return StringUtils.strip(url, "&");
    }

    /**
     * List<String>根据分隔符拼接成String
     * @param c
     * @return
     */
    public static String spliceCollectionBySeparator(Collection<String> c, String separator){
        if (c == null || c.size() == 0) return "";
        StringBuilder result = new StringBuilder("");
        for (String str: c) {
            result.append(str + separator);
        }
        return StringUtils.strip(result.toString(), separator);
    }

    /**
     * 拼接connection文件名
     * @param l
     * @return
     */
    public static String spliceFileNameBySeparator(List<Connection> l, String separator){
        if (l == null || l.size() == 0) return "";
        StringBuilder result = new StringBuilder("{");
        for (Connection c: l) {
            result.append("\"" + c.getFileName() + "\"" + separator);
        }
        String finalResult = StringUtils.strip(result.toString(), separator);
        return finalResult + "}";
    }

    /**
     * 根据参数名拼接字符串分组connections
     * @param conns
     * @param target
     * @return
     */
    public static Map<String, List<Connection>> groupConnectionByNameString(List<Connection> conns, String target){
        Map<String, List<Connection>> groupMap = new HashMap<String, List<Connection>>();
        for (Connection conn : conns) {
            String key = (target.equals("body"))?conn.getFullNames():conn.getUrlParamsName();
            List<Connection> value = groupMap.get(key);
            if (value == null){
                value = new ArrayList<Connection>();
                value.add(conn);
                groupMap.put(key, value);
            }else {
                value.add(conn);
            }
        }

        return groupMap;
    }

    /**
     * 判断所以connection方法是否统一
     * @param conns
     * @return
     */
    public static boolean judgeAllMethodIsOne(List<Connection> conns){
        boolean status = true;
        String init = conns.get(0).getRequestMethod();
        for (Connection conn: conns) {
            if (!conn.getRequestMethod().equals(init)){
                status = false;
            }
        }
        return status;
    }

    /**
     * 生成functions
     * @param functions
     * @param groupMap
     * @param connections
     * @param connection
     * @throws Exception
     */
    public static void produceFunction(List<Function> functions, Map<String, List<Connection>> groupMap, List<Connection> connections, Connection connection, String target) throws Exception {
        for (Map.Entry<String, List<Connection>> urlGroupMapEntry : groupMap.entrySet()) {
            List<Connection> conns = urlGroupMapEntry.getValue();
            Map<String, Map<String, String>> analysisMap = getParamsAndFactors(conns, target);

            Function function = new Function();
            function.setFileNames(spliceFileNameBySeparator(conns, ","));
            function.setFunctionName("do" + connection.getRequestMethod() + getFunctionName(connection.getFileName()));
            function.setHeaders(connection.getRequestHeaders());
            function.setHttpMethod("HttpMethod" + "." + connection.getRequestMethod());
            function.setUrl(connection.getRequestUrl());
            function.setResultType(getReturnType(connection.getResponseHeaders().get("Content-Type")));
            if (analysisMap != null)
            {
                function.setParams(analysisMap.get("params"));
                function.setFactors(analysisMap.get("factors"));
            }

            if (connection.isRequestBodyIsJson()){
                function.setRequestBodyIsJson(true);
            }else {
                function.setRequestBodyIsJson(false);
            }
            function.setUrl(getSyntaxUrl(connection.getRequestUrlPrefix(), analysisMap));
            functions.add(function);
        }
    }

}
