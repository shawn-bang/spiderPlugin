package com.credithc.spider;

import org.junit.Test;

/**
 * Created by dell3010 on 2017/7/31.
 */
public class TemplateMainTest {

    /**
     * 统一测试入口
     * @throws Exception
     */
    @Test
    public void testGenerateAPI() throws Exception {
        TemplateMain.main(null);
    }

    /**
     * 无参数变化
     * @throws Exception
     */
    @Test
    public void testGenerateAPI001() throws Exception {
        TemplateMain.main(new String[]{"D:/GenerateAPITest/nothingChange"});
    }

    /**
     * url参数变化
     * @throws Exception
     */
    @Test
    public void testGenerateAPI002() throws Exception {
        TemplateMain.main(new String[]{"D:/GenerateAPITest/urlParamsChange"});
    }

    /**
     * body参数变化
     * @throws Exception
     */
    @Test
    public void testGenerateAPI003() throws Exception {
        TemplateMain.main(new String[]{"D:/GenerateAPITest/bodyChange"});
    }

    /**
     * urlAndBody参数变化
     * @throws Exception
     */
    @Test
    public void testGenerateAPI004() throws Exception {
        TemplateMain.main(new String[]{"D:/GenerateAPITest/urlAndBodyParamsChange"});
    }

    /**
     * url参数变化,requestBody为json格式为不可变参数
     * @throws Exception
     */
    @Test
    public void testGenerateAPI005() throws Exception {
        TemplateMain.main(new String[]{"D:/GenerateAPITest/urlParamsChangeAndJsonBody"});
    }

    /**
     * url参数变化,requestBody为json格式为可变参数
     * @throws Exception
     */
    @Test
    public void testGenerateAPI006() throws Exception {
        TemplateMain.main(new String[]{"D:/GenerateAPITest/urlAndJsonBodyChange"});
    }

}
