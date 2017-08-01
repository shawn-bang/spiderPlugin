# spiderPlugin

## 目标  
    为解决爬取过程中通过Spring RestTemplate重复性发送http/https请求的工作，使得该过程可以通过fiddler代理爬取(通过二次开发c#API)到的请求格式化文件自动生成相关的发送请求代码(基础的爬取API)，并分析出请求之间的相关关系找到可变参数体现在生成代码上;  

## 技术相关  
该项目使用的主要技术是freemarker，通过freemarker模板生成可用的java代码;
所有相关配置都在config.properties里面

## 功能实现情况  
分析可变参数主要是针对请求相同的url时，url后挂着的key-value参数和requestBody里面key-value参数的可变情况；  
很多特殊的情况目前没有覆盖，目前覆盖的情况大体如下：  
* 两套数据上对应的两个请求没有任何变化，会直接生成一个请求函数，所以涉及到的参数都是硬编码在代码里面；  
* url后面挂着的key-value参数在多个对应的请求中有变化，requestBody中的key-value参数没有变化；  
* url后面挂着的key-value参数在多个对应的请求中没变化，requestBody中的key-value参数有变化；  
* 情况2和情况3任意变化的组合方式都是支持的；  
* 如果reuqestBody中是json格式，目前不支持深度分析json内容中具体字段的变化，仅支持简单的整个json内容比对；  

## 注意  
* 内容中所有的url是指整个url的一部分(full url:http://www.xxx.com/a/b/c?n=123&y=456 那么url是指http://www.xxx.com/a/b/c);  
* 如果url相同的请求有很多个，工具会根据url后挂着的key-value参数名+requestBody中参数名分组，生成多个函数，来支持参数组合变化的情况， 
  目前不支持多个相同的url采用了不同的请求方式(如：同时采用了GET And POST);

##样例整理
