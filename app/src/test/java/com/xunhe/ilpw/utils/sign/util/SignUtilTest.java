package com.xunhe.ilpw.utils.sign.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by wangliang on 2016/8/24.
 */
public class SignUtilTest {



    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testSign() throws Exception {
        String s = SignUtil.sign("POSTapplication/jsonapplication/json;charset=utf-8Wed, 24 August 2016 07:00:02 GMT/rest/160601/ocr/ocr_driver_license.json"
        , "f1f9700c95786d8653ca551a9c51e5fb");
        assertEquals(s, "test");
    }
}