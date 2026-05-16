package com.ixuea.courses.mymusic.util;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class Base64UtilTest {
    private static final String DATA = "ixueaedu";

    @Test
    public void testEncodeString2String() {
        String r = Base64Util.encodeString2String(DATA);

        Assert.assertEquals("aXh1ZWFlZHU=", r);

        //解码
        Assert.assertEquals(DATA, Base64Util.decodeString2String(r));
    }
}
